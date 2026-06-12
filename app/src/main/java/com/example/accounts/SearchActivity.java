package com.example.accounts;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accounts.adapter.AccountAdapter;
import com.example.accounts.adapter.SearchHistoryAdapter;
import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.db.SearchHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索界面（优化版本）
 * 功能：
 * - 搜索历史记录管理
 * - 智能搜索建议
 * - 多字段搜索（备注、类型）
 * - 搜索结果排序（时间、金额、相关度）
 * - 分页加载
 * - 友好的错误处理和空状态提示
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    // UI控件
    private ListView searchLv;
    private EditText searchEt;
    private TextView emptyTv;
    private ListView historyLv;
    private View historyLayout;
    private View sortLayout;
    private TextView sortTimeTv, sortAmountTv, sortRelevanceTv;
    private TextView resultCountTv;
    private TextView clearHistoryTv;

    // 数据相关
    private List<AccountBean> mDatas;
    private AccountAdapter adapter;
    private List<SearchHistoryBean> historyList;
    private SearchHistoryAdapter historyAdapter;

    // 搜索配置
    private String currentKeyword = "";
    private int sortBy = 0; // 0-时间，1-金额，2-相关度
    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private int totalCount = 0;

    // Handler用于搜索防抖
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
        loadSearchHistory();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        searchEt = findViewById(R.id.search_et);
        searchLv = findViewById(R.id.search_lv);
        emptyTv = findViewById(R.id.search_tv_empty);
        historyLv = findViewById(R.id.lv_history);
        historyLayout = findViewById(R.id.layout_history);
        sortLayout = findViewById(R.id.layout_sort);
        sortTimeTv = findViewById(R.id.tv_sort_time);
        sortAmountTv = findViewById(R.id.tv_sort_amount);
        sortRelevanceTv = findViewById(R.id.tv_sort_relevance);
        resultCountTv = findViewById(R.id.tv_result_count);
        clearHistoryTv = findViewById(R.id.tv_clear_history);

        sortLayout.setClickable(false);
        sortLayout.setFocusable(false);
        sortTimeTv.setOnClickListener(v -> onSortSelected(0));
        sortAmountTv.setOnClickListener(v -> onSortSelected(1));
        sortRelevanceTv.setOnClickListener(v -> onSortSelected(2));
        clearHistoryTv.setOnClickListener(this);
        findViewById(R.id.search_iv_back).setOnClickListener(this);
        findViewById(R.id.search_iv_sh).setOnClickListener(this);

        // 初始状态显示历史记录
        showHistoryLayout(true);
        showSearchResults(false);

        // 设置搜索输入监听
        setupSearchInputListener();

        // 设置滚动监听（分页加载）
        setupScrollListener();

        // 设置默认排序状态
        updateSortButtons(0);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mDatas = new ArrayList<>();
        adapter = new AccountAdapter(this, mDatas);
        searchLv.setAdapter(adapter);
        searchLv.setEmptyView(emptyTv);

        historyList = new ArrayList<>();
        historyAdapter = new SearchHistoryAdapter(this, historyList);
        historyLv.setAdapter(historyAdapter);

        // 设置历史记录点击事件
        historyAdapter.setOnHistoryItemClickListener(new SearchHistoryAdapter.OnHistoryItemClickListener() {
            @Override
            public void onItemClick(String keyword) {
                searchEt.setText(keyword);
                searchEt.setSelection(keyword.length());
                performSearch(keyword);
            }

            @Override
            public void onDeleteClick(int id) {
                DBManager.deleteSearchHistory(id);
                loadSearchHistory();
            }
        });
    }

    /**
     * 设置搜索输入监听
     */
    private void setupSearchInputListener() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (TextUtils.isEmpty(text)) {
                    showHistoryLayout(true);
                    showSearchResults(false);
                    currentKeyword = "";
                } else {
                    // 防抖搜索：延迟300ms执行
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    searchRunnable = () -> performSearch(text);
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            }
        });

        // 输入法搜索按钮
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String text = searchEt.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    performSearch(text);
                }
                return true;
            }
            return false;
        });
    }

    /**
     * 设置滚动监听（实现分页加载）
     */
    private void setupScrollListener() {
        searchLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 滚动到底部时加载更多
                if (!isLoading && hasMoreData && 
                    firstVisibleItem + visibleItemCount >= totalItemCount - 3) {
                    loadMoreData();
                }
            }
        });
    }

    /**
     * 执行搜索
     */
    private void performSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }

        currentKeyword = keyword;
        currentPage = 0;
        mDatas.clear();
        adapter.notifyDataSetChanged();

        // 保存搜索历史
        DBManager.addSearchHistory(keyword);

        showHistoryLayout(false);
        showSearchResults(true);
        loadSearchData(true);
    }

    /**
     * 加载搜索数据
     */
    private void loadSearchData(boolean isFirstPage) {
        isLoading = true;
        
        new Thread(() -> {
            try {
                // 获取总数
                totalCount = DBManager.getSearchResultCount(currentKeyword);
                
                // 搜索数据
                List<AccountBean> results = DBManager.searchAccountList(
                    currentKeyword,
                    currentPage * PAGE_SIZE,
                    PAGE_SIZE,
                    sortBy
                );

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isFirstPage) {
                        mDatas.clear();
                    }
                    mDatas.addAll(results);
                    adapter.notifyDataSetChanged();
                    
                    // 更新状态
                    hasMoreData = (currentPage + 1) * PAGE_SIZE < totalCount;
                    updateResultCount();
                    
                    // 显示空状态
                    if (totalCount == 0) {
                        emptyTv.setVisibility(View.VISIBLE);
                        emptyTv.setText("未找到包含\"" + currentKeyword + "\"的记录");
                    } else {
                        emptyTv.setVisibility(View.GONE);
                    }
                    
                    isLoading = false;
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(SearchActivity.this, "搜索失败，请重试", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        }).start();
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (isLoading || !hasMoreData) return;
        currentPage++;
        loadSearchData(false);
    }

    /**
     * 加载搜索历史
     */
    private void loadSearchHistory() {
        new Thread(() -> {
            List<SearchHistoryBean> histories = DBManager.getSearchHistory(20);
            new Handler(Looper.getMainLooper()).post(() -> {
                historyList.clear();
                historyList.addAll(histories);
                historyAdapter.notifyDataSetChanged();
                
                if (histories.isEmpty()) {
                    historyLayout.setVisibility(View.GONE);
                } else {
                    historyLayout.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }

    /**
     * 更新排序按钮状态
     */
    private void updateSortButtons(int selected) {
        sortBy = selected;
        sortTimeTv.setSelected(selected == 0);
        sortAmountTv.setSelected(selected == 1);
        sortRelevanceTv.setSelected(selected == 2);
        sortTimeTv.refreshDrawableState();
        sortAmountTv.refreshDrawableState();
        sortRelevanceTv.refreshDrawableState();
    }

    private void onSortSelected(int sortType) {
        if (sortBy == sortType) {
            return;
        }
        updateSortButtons(sortType);
        if (!TextUtils.isEmpty(currentKeyword)) {
            currentPage = 0;
            loadSearchData(true);
        }
    }

    /**
     * 更新结果计数显示
     */
    private void updateResultCount() {
        if (totalCount > 0) {
            resultCountTv.setVisibility(View.VISIBLE);
            String text = "共找到 " + totalCount + " 条结果";
            if (mDatas.size() < totalCount) {
                text += "（已显示 " + mDatas.size() + " 条）";
            }
            resultCountTv.setText(text);
        } else {
            resultCountTv.setVisibility(View.GONE);
        }
    }

    /**
     * 显示/隐藏历史记录布局
     */
    private void showHistoryLayout(boolean show) {
        historyLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 显示/隐藏搜索结果布局
     */
    private void showSearchResults(boolean show) {
        sortLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        searchLv.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            sortLayout.bringToFront();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_iv_back) {
            finish();
        } else if (id == R.id.search_iv_sh) {
            String msg = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                performSearch(msg);
            }
        } else if (id == R.id.tv_clear_history) {
            showClearHistoryDialog();
        }
    }

    /**
     * 显示清空历史确认对话框
     */
    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this)
            .setTitle("确认清空")
            .setMessage("确定要清空所有搜索历史吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                DBManager.clearSearchHistory();
                loadSearchHistory();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理Handler
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
