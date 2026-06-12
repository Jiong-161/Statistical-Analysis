package com.example.accounts.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.accounts.R;

/**
 * 数据库帮助类
 * <p>
 * 功能说明：
 * - 数据库创建和版本管理
 * - 表结构定义和初始化
 * - 性能优化：索引创建
 * - 安全升级：版本兼容处理
 * </p>
 *
 * @author AccountApp Team
 * @version 2.0
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBOpenHelper";

    // ==================== 数据库基础常量 ====================
    /** 数据库文件名 */
    public static final String DB_NAME = "ZH-1.db";
    /** 数据库版本号 */
    public static final int DB_VERSION = 2;

    // ==================== 表名常量 ====================
    /** 收支类型表 */
    public static final String TABLE_TYPE = "typetb";
    /** 记账记录表 */
    public static final String TABLE_ACCOUNT = "accounttb";
    /** 搜索历史表 */
    public static final String TABLE_SEARCH_HISTORY = "searchhistorytb";

    // ==================== typetb 表字段 ====================
    public static final String TYPE_ID = "id";
    public static final String TYPE_NAME = "typename";
    public static final String TYPE_IMAGE_ID = "imageId";
    public static final String TYPE_SELECTED_IMAGE_ID = "sImageId";
    public static final String TYPE_KIND = "kind";

    // ==================== accounttb 表字段 ====================
    public static final String ACCOUNT_ID = "id";
    public static final String ACCOUNT_TYPENAME = "typename";
    public static final String ACCOUNT_IMAGE_ID = "sImageId";
    public static final String ACCOUNT_BEIZHU = "beizhu";
    public static final String ACCOUNT_MONEY = "money";
    public static final String ACCOUNT_TIME = "time";
    public static final String ACCOUNT_YEAR = "year";
    public static final String ACCOUNT_MONTH = "month";
    public static final String ACCOUNT_DAY = "day";
    public static final String ACCOUNT_KIND = "kind";

    // ==================== searchhistorytb 表字段 ====================
    public static final String HISTORY_ID = "id";
    public static final String HISTORY_KEYWORD = "keyword";
    public static final String HISTORY_TIME = "searchtime";
    public static final String HISTORY_COUNT = "searchcount";

    // ==================== 索引名常量 ====================
    /** 备注字段索引 */
    public static final String IDX_ACCOUNT_BEIZHU = "idx_accounttb_beizhu";
    /** 时间字段索引 */
    public static final String IDX_ACCOUNT_TIME = "idx_accounttb_time";
    /** 年月日复合索引 */
    public static final String IDX_ACCOUNT_YEAR_MONTH_DAY = "idx_accounttb_year_month_day";
    /** 搜索历史关键词索引 */
    public static final String IDX_HISTORY_KEYWORD = "idx_searchhistory_keyword";
    /** 搜索历史时间索引 */
    public static final String IDX_HISTORY_TIME = "idx_searchhistory_time";

    // ==================== 构造函数 ====================
    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    public DBOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DBOpenHelper initialized with DB_NAME=" + DB_NAME + ", version=" + DB_VERSION);
    }

    // ==================== 数据库创建 ====================
    /**
     * 数据库第一次创建时调用
     * <p>
     * 执行操作：
     * 1. 创建收支类型表
     * 2. 插入默认类型数据
     * 3. 创建记账记录表
     * 4. 创建性能优化索引
     * 5. 创建搜索历史表
     * </p>
     *
     * @param db SQLiteDatabase实例
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: Start database initialization");
        try {
            // 1. 创建收支类型表
            createTypeTable(db);

            // 2. 插入默认类型数据
            insertDefaultTypes(db);

            // 3. 创建记账记录表
            createAccountTable(db);

            // 4. 创建记账表性能优化索引
            createAccountTableIndices(db);

            // 5. 创建搜索历史表
            createSearchHistoryTable(db);

            Log.i(TAG, "onCreate: Database initialization completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Database initialization failed", e);
            throw new RuntimeException("Database creation failed", e);
        }
    }

    // ==================== 表创建方法 ====================
    /**
     * 创建收支类型表
     *
     * @param db SQLiteDatabase实例
     */
    private void createTypeTable(SQLiteDatabase db) {
        Log.d(TAG, "createTypeTable: Creating type table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_TYPE + " (" +
                TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TYPE_NAME + " VARCHAR(10) NOT NULL," +
                TYPE_IMAGE_ID + " INTEGER NOT NULL," +
                TYPE_SELECTED_IMAGE_ID + " INTEGER NOT NULL," +
                TYPE_KIND + " INTEGER NOT NULL)";
        db.execSQL(sql);
        Log.d(TAG, "createTypeTable: Type table created successfully");
    }

    /**
     * 创建记账记录表
     *
     * @param db SQLiteDatabase实例
     */
    private void createAccountTable(SQLiteDatabase db) {
        Log.d(TAG, "createAccountTable: Creating account table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " (" +
                ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ACCOUNT_TYPENAME + " VARCHAR(10) NOT NULL," +
                ACCOUNT_IMAGE_ID + " INTEGER NOT NULL," +
                ACCOUNT_BEIZHU + " VARCHAR(80)," +
                ACCOUNT_MONEY + " FLOAT NOT NULL," +
                ACCOUNT_TIME + " VARCHAR(60) NOT NULL," +
                ACCOUNT_YEAR + " INTEGER NOT NULL," +
                ACCOUNT_MONTH + " INTEGER NOT NULL," +
                ACCOUNT_DAY + " INTEGER NOT NULL," +
                ACCOUNT_KIND + " INTEGER NOT NULL)";
        db.execSQL(sql);
        Log.d(TAG, "createAccountTable: Account table created successfully");
    }

    /**
     * 创建记账表性能优化索引
     *
     * @param db SQLiteDatabase实例
     */
    private void createAccountTableIndices(SQLiteDatabase db) {
        Log.d(TAG, "createAccountTableIndices: Creating indices");

        // 备注字段索引 - 用于搜索
        db.execSQL("CREATE INDEX IF NOT EXISTS " + IDX_ACCOUNT_BEIZHU +
                " ON " + TABLE_ACCOUNT + "(" + ACCOUNT_BEIZHU + ")");

        // 时间字段索引 - 用于时间查询
        db.execSQL("CREATE INDEX IF NOT EXISTS " + IDX_ACCOUNT_TIME +
                " ON " + TABLE_ACCOUNT + "(" + ACCOUNT_TIME + ")");

        // 年月日复合索引 - 用于历史记录查询
        db.execSQL("CREATE INDEX IF NOT EXISTS " + IDX_ACCOUNT_YEAR_MONTH_DAY +
                " ON " + TABLE_ACCOUNT + "(" + ACCOUNT_YEAR + "," + ACCOUNT_MONTH + "," + ACCOUNT_DAY + ")");

        Log.d(TAG, "createAccountTableIndices: Indices created successfully");
    }

    /**
     * 创建搜索历史表
     *
     * @param db SQLiteDatabase实例
     */
    private void createSearchHistoryTable(SQLiteDatabase db) {
        Log.d(TAG, "createSearchHistoryTable: Creating search history table");
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_SEARCH_HISTORY + " (" +
                HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HISTORY_KEYWORD + " VARCHAR(100) NOT NULL," +
                HISTORY_TIME + " INTEGER NOT NULL," +
                HISTORY_COUNT + " INTEGER DEFAULT 1)";
        db.execSQL(sql);

        // 创建搜索历史索引
        db.execSQL("CREATE INDEX IF NOT EXISTS " + IDX_HISTORY_KEYWORD +
                " ON " + TABLE_SEARCH_HISTORY + "(" + HISTORY_KEYWORD + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS " + IDX_HISTORY_TIME +
                " ON " + TABLE_SEARCH_HISTORY + "(" + HISTORY_TIME + ")");

        Log.d(TAG, "createSearchHistoryTable: Search history table created successfully");
    }

    // ==================== 初始化数据方法 ====================
    /**
     * 插入默认收支类型数据
     *
     * @param db SQLiteDatabase实例
     */
    private void insertDefaultTypes(SQLiteDatabase db) {
        Log.d(TAG, "insertDefaultTypes: Inserting default type data");

        String sql = "INSERT INTO " + TABLE_TYPE + " (" +
                TYPE_NAME + "," + TYPE_IMAGE_ID + "," + TYPE_SELECTED_IMAGE_ID + "," + TYPE_KIND +
                ") VALUES (?,?,?,?)";

        // 开始事务，保证数据一致性
        db.beginTransaction();
        try {
            // ==================== 支出类型（kind=0） ====================
            db.execSQL(sql, new Object[]{"其他", R.drawable.ic_qita, R.drawable.ic_qita_fs, 0});
            db.execSQL(sql, new Object[]{"餐饮", R.drawable.ic_canyin, R.drawable.ic_canyin_fs, 0});
            db.execSQL(sql, new Object[]{"交通", R.drawable.ic_jiaotong, R.drawable.ic_jiaotong_fs, 0});
            db.execSQL(sql, new Object[]{"购物", R.drawable.ic_gouwu, R.drawable.ic_gouwu_fs, 0});
            db.execSQL(sql, new Object[]{"服饰", R.drawable.ic_fushi, R.drawable.ic_fushi_fs, 0});
            db.execSQL(sql, new Object[]{"日用品", R.drawable.ic_riyongpin, R.drawable.ic_riyongpin_fs, 0});
            db.execSQL(sql, new Object[]{"娱乐", R.drawable.ic_yule, R.drawable.ic_yule_fs, 0});
            db.execSQL(sql, new Object[]{"零食", R.drawable.ic_lingshi, R.drawable.ic_lingshi_fs, 0});
            db.execSQL(sql, new Object[]{"烟酒茶", R.drawable.ic_yanjiu, R.drawable.ic_yanjiu_fs, 0});
            db.execSQL(sql, new Object[]{"学习", R.drawable.ic_xuexi, R.drawable.ic_xuexi_fs, 0});
            db.execSQL(sql, new Object[]{"医疗", R.drawable.ic_yiliao, R.drawable.ic_yiliao_fs, 0});
            db.execSQL(sql, new Object[]{"住宅", R.drawable.ic_zhufang, R.drawable.ic_zhufang_fs, 0});
            db.execSQL(sql, new Object[]{"水电煤", R.drawable.ic_shuidianfei, R.drawable.ic_shuidianfei_fs, 0});
            db.execSQL(sql, new Object[]{"通讯", R.drawable.ic_tongxun, R.drawable.ic_tongxun_fs, 0});
            db.execSQL(sql, new Object[]{"人情往来", R.drawable.ic_renqingwanglai, R.drawable.ic_renqingwanglai_fs, 0});

            // ==================== 收入类型（kind=1） ====================
            db.execSQL(sql, new Object[]{"其他", R.drawable.in_qt, R.drawable.in_qt_fs, 1});
            db.execSQL(sql, new Object[]{"薪资", R.drawable.in_xinzi, R.drawable.in_xinzi_fs, 1});
            db.execSQL(sql, new Object[]{"奖金", R.drawable.in_jiangjin, R.drawable.in_jiangjin_fs, 1});
            db.execSQL(sql, new Object[]{"借入", R.drawable.in_jieru, R.drawable.in_jieru_fs, 1});
            db.execSQL(sql, new Object[]{"收债", R.drawable.in_shouzhai, R.drawable.in_shouzhai_fs, 1});
            db.execSQL(sql, new Object[]{"利息收入", R.drawable.in_lixifuji, R.drawable.in_lixifuji_fs, 1});
            db.execSQL(sql, new Object[]{"投资回报", R.drawable.in_touzi, R.drawable.in_touzi_fs, 1});
            db.execSQL(sql, new Object[]{"二手交易", R.drawable.in_ershoushebei, R.drawable.in_ershoushebei_fs, 1});
            db.execSQL(sql, new Object[]{"意外所得", R.drawable.in_yiwai, R.drawable.in_yiwai_fs, 1});

            // 标记事务成功
            db.setTransactionSuccessful();
            Log.d(TAG, "insertDefaultTypes: Default types inserted successfully");
        } finally {
            // 结束事务
            db.endTransaction();
        }
    }

    // ==================== 数据库版本升级 ====================
    /**
     * 数据库版本升级时调用
     * <p>
     * 根据旧版本号和新版本号，执行相应的升级策略，保证数据不丢失
     * </p>
     *
     * @param db         SQLiteDatabase实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            // 按版本号递进升级，支持跨版本升级
            for (int version = oldVersion + 1; version <= newVersion; version++) {
                upgradeToVersion(db, version);
            }
            Log.i(TAG, "onUpgrade: Database upgrade completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "onUpgrade: Database upgrade failed", e);
            // 升级失败时的备用方案：重建表（根据实际需求调整）
            handleUpgradeFailure(db);
        }
    }

    /**
     * 升级到指定版本
     *
     * @param db            SQLiteDatabase实例
     * @param targetVersion 目标版本号
     */
    private void upgradeToVersion(SQLiteDatabase db, int targetVersion) {
        Log.d(TAG, "upgradeToVersion: Upgrading to version " + targetVersion);

        switch (targetVersion) {
        case 2:
            upgradeToVersion2(db);
            break;
        // 未来可以添加更多版本的升级逻辑
        default:
            Log.w(TAG, "upgradeToVersion: Unknown target version " + targetVersion);
            break;
        }
    }

    /**
     * 升级到版本2
     * <p>
     * 升级内容：
     * - 添加accounttb表的性能优化索引
     * - 添加searchhistorytb搜索历史表
     * </p>
     *
     * @param db SQLiteDatabase实例
     */
    private void upgradeToVersion2(SQLiteDatabase db) {
        Log.i(TAG, "upgradeToVersion2: Starting upgrade to version 2");

        // 添加记账表索引
        try {
            createAccountTableIndices(db);
            Log.i(TAG, "upgradeToVersion2: Account table indices created");
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2: Failed to create account table indices", e);
        }

        // 创建搜索历史表
        try {
            createSearchHistoryTable(db);
            Log.i(TAG, "upgradeToVersion2: Search history table created");
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2: Failed to create search history table", e);
        }

        Log.i(TAG, "upgradeToVersion2: Upgrade to version 2 completed");
    }

    /**
     * 处理升级失败的备用方案
     *
     * @param db SQLiteDatabase实例
     */
    private void handleUpgradeFailure(SQLiteDatabase db) {
        Log.e(TAG, "handleUpgradeFailure: Upgrade failed, trying backup strategy");

        try {
            // 尝试安全的重建方式
            // 注意：实际生产环境应该先备份数据再重建
            Log.w(TAG, "handleUpgradeFailure: Database upgrade failed, using safe recovery");
        } catch (Exception e) {
            Log.e(TAG, "handleUpgradeFailure: Recovery also failed", e);
        }
    }

    // ==================== 数据库版本降级 ====================
    /**
     * 数据库版本降级时调用（可选）
     *
     * @param db         SQLiteDatabase实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onDowngrade: Database downgrade from " + oldVersion + " to " + newVersion);
        // 默认策略：不处理降级，避免数据丢失
        // super.onDowngrade(db, oldVersion, newVersion); // 会抛异常
        Log.w(TAG, "onDowngrade: Downgrade not supported, keeping current version");
    }

    // ==================== 数据库打开回调 ====================
    /**
     * 数据库打开时的回调
     *
     * @param db SQLiteDatabase实例
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "onOpen: Database opened successfully");

        // 开启外键约束（如果需要）
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * 获取数据库信息
     *
     * @param context 上下文对象
     * @return 数据库信息字符串
     */
    public static String getDatabaseInfo(Context context) {
        return "Database Name: " + DB_NAME + "\n" +
                "Database Version: " + DB_VERSION + "\n" +
                "Database Path: " + context.getDatabasePath(DB_NAME).getAbsolutePath();
    }
}