package com.example.accounts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.utils.CalendarDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 图表统计界面 - 全面优化版
 */
public class ChartActivity extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    private ImageView backIv, calendarIv;
    private TextView dateTv, outTv, inTv, outcomeTab, incomeTab;
    private TextView dayTab, monthTab, yearTab;
    private PieChart pieChart;
    private BarChart barChart;

    private int year, month;
    private int selectKind = 0; // 0-支出 1-收入
    private int timeDimension = 0; // 0-日 1-月 2-年
    
    // 缓存数据，避免重复计算
    private Map<String, Float> cachedTypeMoneyMap;
    private Map<Integer, Float> cachedDayMoneyMap;
    private List<AccountBean> cachedAccountList;

    // 专业的图表颜色数组 - 支出和收入分开配色
    private int[] outcomeColors = {
            Color.parseColor("#FF6B6B"), Color.parseColor("#FF8E53"),
            Color.parseColor("#FFBE0B"), Color.parseColor("#FFD93D"),
            Color.parseColor("#4ECDC4"), Color.parseColor("#45B7D1"),
            Color.parseColor("#96CEB4"), Color.parseColor("#98D8C8"),
            Color.parseColor("#DDA0DD"), Color.parseColor("#BB8FCE")
    };
    
    private int[] incomeColors = {
            Color.parseColor("#4ECDC4"), Color.parseColor("#45B7D1"),
            Color.parseColor("#5DADE2"), Color.parseColor("#5499C7"),
            Color.parseColor("#48C9B0"), Color.parseColor("#1ABC9C"),
            Color.parseColor("#2ECC71"), Color.parseColor("#58D68D"),
            Color.parseColor("#82E0AA"), Color.parseColor("#A9DFBF")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initTime();
        initView();
        initChart();
        loadData();
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
    }

    private void initView() {
        backIv = findViewById(R.id.chart_iv_back);
        calendarIv = findViewById(R.id.chart_iv_calendar);
        dateTv = findViewById(R.id.chart_tv_date);
        outTv = findViewById(R.id.chart_tv_out);
        inTv = findViewById(R.id.chart_tv_in);
        outcomeTab = findViewById(R.id.chart_tv_outcome);
        incomeTab = findViewById(R.id.chart_tv_income);
        dayTab = findViewById(R.id.chart_tv_day);
        monthTab = findViewById(R.id.chart_tv_month);
        yearTab = findViewById(R.id.chart_tv_year);
        pieChart = findViewById(R.id.chart_pie);
        barChart = findViewById(R.id.chart_bar);

        backIv.setOnClickListener(this);
        calendarIv.setOnClickListener(this);
        dateTv.setOnClickListener(this);
        outcomeTab.setOnClickListener(this);
        incomeTab.setOnClickListener(this);
        dayTab.setOnClickListener(this);
        monthTab.setOnClickListener(this);
        yearTab.setOnClickListener(this);

        updateDateDisplay();
    }

    private void updateDateDisplay() {
        if (timeDimension == 0) {
            dateTv.setText(year + "年" + (month < 10 ? "0" + month : month) + "月");
        } else if (timeDimension == 1) {
            dateTv.setText(year + "年");
        } else {
            dateTv.setText("近年统计");
        }
    }

    private void initChart() {
        initPieChart();
        initBarChart();
    }
    
    private void initPieChart() {
        // 饼图全面优化
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(10, 10, 10, 10);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText("收支分布");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.parseColor("#333333"));
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(100);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.setNoDataText("暂无数据");
        pieChart.setNoDataTextColor(Color.parseColor("#999999"));

        Legend pieLegend = pieChart.getLegend();
        pieLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        pieLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieLegend.setDrawInside(false);
        pieLegend.setXEntrySpace(10f);
        pieLegend.setYEntrySpace(5f);
        pieLegend.setYOffset(10f);
        pieLegend.setTextSize(12f);
        pieLegend.setTextColor(Color.parseColor("#666666"));
        pieLegend.setFormSize(10f);
        pieLegend.setWordWrapEnabled(true);
        pieLegend.setMaxSizePercent(0.9f);
    }
    
    private void initBarChart() {
        // 柱状图全面优化
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.setNoDataText("暂无数据");
        barChart.setNoDataTextColor(Color.parseColor("#999999"));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.parseColor("#E0E0E0"));
        xAxis.setAxisLineWidth(1f);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setTextSize(11f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12, false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#F0F0F0"));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineColor(Color.parseColor("#E0E0E0"));
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setTextSize(11f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        
        // 添加空数据提示
        barChart.setTouchEnabled(true);
    }

    private void loadData() {
        // 清空缓存
        clearCache();
        
        // 加载本月收支总额
        float income = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcome = DBManager.getSumMoneyOneMonth(year, month, 0);
        inTv.setText("￥" + String.format(Locale.getDefault(), "%.2f", income));
        outTv.setText("￥" + String.format(Locale.getDefault(), "%.2f", outcome));

        // 加载饼图数据
        loadPieChartData();
        // 加载柱状图数据
        loadBarChartData();
    }
    
    private void clearCache() {
        cachedTypeMoneyMap = null;
        cachedDayMoneyMap = null;
        cachedAccountList = null;
    }
    
    private void loadPieChartData() {
        List<AccountBean> list = getAccountList();
        Map<String, Float> typeMoneyMap = getTypeMoneyMap(list);

        ArrayList<PieEntry> entries = new ArrayList<>();
        float total = 0f;
        
        for (Map.Entry<String, Float> entry : typeMoneyMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            total += entry.getValue();
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "暂无数据"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);
        dataSet.setColors(selectKind == 0 ? outcomeColors : incomeColors);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.parseColor("#666666"));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new CustomPieValueFormatter(total));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.parseColor("#333333"));

        pieChart.setData(data);
        pieChart.highlightValues(null);
        // 动画效果
        pieChart.animateY(800);
        pieChart.invalidate();
    }
    
    private List<AccountBean> getAccountList() {
        if (cachedAccountList == null) {
            cachedAccountList = DBManager.getAccountListOneMonthFromAccounttb(year, month);
        }
        return cachedAccountList;
    }
    
    private Map<String, Float> getTypeMoneyMap(List<AccountBean> list) {
        if (cachedTypeMoneyMap != null) {
            return cachedTypeMoneyMap;
        }
        
        Map<String, Float> typeMoneyMap = new HashMap<>();
        for (AccountBean bean : list) {
            if (bean.getKind() == selectKind) {
                String typeName = bean.getTypename();
                float money = typeMoneyMap.getOrDefault(typeName, 0f);
                typeMoneyMap.put(typeName, money + bean.getMoney());
            }
        }
        
        cachedTypeMoneyMap = typeMoneyMap;
        return typeMoneyMap;
    }
    
    private Map<Integer, Float> getDayMoneyMap(List<AccountBean> list) {
        if (cachedDayMoneyMap != null) {
            return cachedDayMoneyMap;
        }
        
        Map<Integer, Float> dayMoneyMap = new HashMap<>();
        for (AccountBean bean : list) {
            if (bean.getKind() == selectKind) {
                int day = bean.getDay();
                float money = dayMoneyMap.getOrDefault(day, 0f);
                dayMoneyMap.put(day, money + bean.getMoney());
            }
        }
        
        cachedDayMoneyMap = dayMoneyMap;
        return dayMoneyMap;
    }

    private void loadBarChartData() {
        List<AccountBean> list = getAccountList();
        Map<Integer, Float> dayMoneyMap = getDayMoneyMap(list);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int maxDays = 31;
        if (timeDimension == 0) {
            // 日维度
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1);
            maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            for (int i = 1; i <= maxDays; i++) {
                float money = dayMoneyMap.getOrDefault(i, 0f);
                entries.add(new BarEntry(i - 1, money));
                if (maxDays <= 10 || i % 3 == 0 || i == 1 || i == maxDays) {
                    labels.add(String.valueOf(i));
                } else {
                    labels.add("");
                }
            }
        } else if (timeDimension == 1) {
            // 月维度 - 显示当年各月数据
            for (int i = 1; i <= 12; i++) {
                float money = DBManager.getSumMoneyOneMonth(year, i, selectKind);
                entries.add(new BarEntry(i - 1, money));
                labels.add(i + "月");
            }
        } else {
            // 年维度 - 显示最近5年数据
            List<Integer> years = DBManager.getYearListFromAccounttb();
            int startYear = year - 4;
            if (!years.isEmpty()) {
                startYear = Math.min(startYear, years.get(0));
            }
            
            for (int i = 0; i < 5; i++) {
                int currentYear = startYear + i;
                float money = DBManager.getSumMoneyOneYear(currentYear, selectKind);
                entries.add(new BarEntry(i, money));
                labels.add(String.valueOf(currentYear));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        int color = selectKind == 0 ? Color.parseColor("#FF6B6B") : Color.parseColor("#4ECDC4");
        dataSet.setColor(color);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#666666"));
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value == 0) return "";
                if (value >= 10000) {
                    return String.format(Locale.getDefault(), "%.1fw", value / 10000);
                } else if (value >= 1000) {
                    return String.format(Locale.getDefault(), "%.0f", value);
                } else if (value >= 100) {
                    return String.format(Locale.getDefault(), "%.0f", value);
                }
                return String.format(Locale.getDefault(), "%.0f", value);
            }
        });
        dataSet.setHighLightColor(Color.parseColor("#FFD700"));
        dataSet.setHighLightAlpha(100);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.7f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        // 动画效果
        barChart.animateY(600);
        barChart.invalidate();
    }

    private void switchTab(int kind) {
        selectKind = kind;
        clearCache();
        
        if (kind == 0) {
            outcomeTab.setBackgroundColor(Color.parseColor("#f3f3f3"));
            outcomeTab.setTextColor(Color.BLACK);
            incomeTab.setBackgroundColor(Color.WHITE);
            incomeTab.setTextColor(Color.GRAY);
        } else {
            incomeTab.setBackgroundColor(Color.parseColor("#f3f3f3"));
            incomeTab.setTextColor(Color.BLACK);
            outcomeTab.setBackgroundColor(Color.WHITE);
            outcomeTab.setTextColor(Color.GRAY);
        }
        
        loadPieChartData();
        loadBarChartData();
    }
    
    private void switchTimeDimension(int dimension) {
        timeDimension = dimension;
        clearCache();
        
        // 重置所有tab背景
        dayTab.setBackgroundColor(Color.WHITE);
        dayTab.setTextColor(Color.GRAY);
        monthTab.setBackgroundColor(Color.WHITE);
        monthTab.setTextColor(Color.GRAY);
        yearTab.setBackgroundColor(Color.WHITE);
        yearTab.setTextColor(Color.GRAY);
        
        // 设置选中tab
        if (dimension == 0) {
            dayTab.setBackgroundColor(Color.parseColor("#f3f3f3"));
            dayTab.setTextColor(Color.BLACK);
        } else if (dimension == 1) {
            monthTab.setBackgroundColor(Color.parseColor("#f3f3f3"));
            monthTab.setTextColor(Color.BLACK);
        } else {
            yearTab.setBackgroundColor(Color.parseColor("#f3f3f3"));
            yearTab.setTextColor(Color.BLACK);
        }
        
        updateDateDisplay();
        loadBarChartData();
    }

    private void showCalendarDialog() {
        CalendarDialog dialog = new CalendarDialog(this, -1, month);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnDateSelectListener(new CalendarDialog.OnDateSelectListener() {
            @Override
            public void onDateSelected(int selYear, int selMonth) {
                year = selYear;
                month = selMonth;
                updateDateDisplay();
                loadData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.chart_iv_back) {
            finish();
        } else if (id == R.id.chart_iv_calendar || id == R.id.chart_tv_date) {
            showCalendarDialog();
        } else if (id == R.id.chart_tv_outcome) {
            switchTab(0);
        } else if (id == R.id.chart_tv_income) {
            switchTab(1);
        } else if (id == R.id.chart_tv_day) {
            switchTimeDimension(0);
        } else if (id == R.id.chart_tv_month) {
            switchTimeDimension(1);
        } else if (id == R.id.chart_tv_year) {
            switchTimeDimension(2);
        }
    }
    
    // OnChartValueSelectedListener 实现
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // 选中时的交互效果
        if (e instanceof PieEntry) {
            PieEntry entry = (PieEntry) e;
            pieChart.setCenterText(entry.getLabel() + "\n" + String.format(Locale.getDefault(), "￥%.2f", entry.getValue()));
        }
    }
    
    @Override
    public void onNothingSelected() {
        // 取消选中时恢复
        pieChart.setCenterText("收支分布");
    }
    
    /**
     * 自定义饼图数值格式化器
     */
    private static class CustomPieValueFormatter implements IValueFormatter {
        private final float total;
        
        public CustomPieValueFormatter(float total) {
            this.total = total;
        }
        
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (total == 0) return "";
            float percent = value / total * 100;
            if (percent < 3) return ""; // 小于3%不显示标签
            return String.format(Locale.getDefault(), "%.1f%%", percent);
        }
    }
}
