package com.example.accounts.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.accounts.utils.PasswordUtils;

/*
 * 负责管理数据库的类（核心）
 *   主要对于表当中的内容进行操作，增删改查，搜索
 * */
public class DBManager {

    private static SQLiteDatabase db;
    private static DBOpenHelper dbHelper;

    /** 初始化数据库（Application 中调用一次即可） */
    public static synchronized void initDB(Context context) {
        if (db != null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        dbHelper = new DBOpenHelper(appContext);
        db = dbHelper.getWritableDatabase();
    }

    private static SQLiteDatabase getDb() {
        if (db == null) {
            throw new IllegalStateException("DBManager.initDB() must be called before database access");
        }
        return db;
    }

    /**
     * 读取数据库当中的数据，写入内存集合里
     *   kind :表示收入或者支出
     * */
    @SuppressLint("Range")
    public static List<TypeBean>getTypeList(int kind){
        List<TypeBean>list = new ArrayList<>();
        Cursor cursor = null;
        try {
            //读取typetb表当中的数据
            String sql = "select * from typetb where kind = ?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(kind)});
//        循环读取游标内容，存储到对象当中
            while (cursor.moveToNext()) {
                String typename = cursor.getString(cursor.getColumnIndex("typename"));
                int imageId = cursor.getInt(cursor.getColumnIndex("imageId"));
                int sImageId = cursor.getInt(cursor.getColumnIndex("sImageId"));
                int kind1 = cursor.getInt(cursor.getColumnIndex("kind"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                TypeBean typeBean = new TypeBean(id, typename, imageId, sImageId, kind);
                list.add(typeBean);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /*
    * 向记账表当中插入一条元素
    * */
    public static void insertItemToAccounttb(AccountBean bean){
        ContentValues values = new ContentValues();
        values.put("typename",bean.getTypename());
        values.put("sImageId",bean.getsImageId());
        values.put("beizhu",bean.getBeizhu());
        values.put("money",bean.getMoney());
        values.put("time",bean.getTime());
        values.put("year",bean.getYear());
        values.put("month",bean.getMonth());
        values.put("day",bean.getDay());
        values.put("kind",bean.getKind());
        getDb().insert("accounttb", null, values);
    }
    /*
    * 获取记账表当中某一天的所有支出或者收入情况
    * */
    @SuppressLint("Range")
    public static List<AccountBean>getAccountListOneDayFromAccounttb(int year,int month,int day){
        List<AccountBean>list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from accounttb where year=? and month=? and day=? order by id desc";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)});
            while (cursor.moveToNext()) {
                list.add(readAccountBean(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /*
     * 获取记账表当中某一月的所有支出或者收入情况
     * */
    @SuppressLint("Range")
    public static List<AccountBean>getAccountListOneMonthFromAccounttb(int year,int month){
        List<AccountBean>list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from accounttb where year=? and month=? order by id desc";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month)});
            //遍历符合要求的每一行数据
            while (cursor.moveToNext()) {
                list.add(readAccountBean(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    /**
     * 获取某一天的支出或者收入的总金额   kind：支出==0    收入===1
     * */
    @SuppressLint("Range")
    public static float getSumMoneyOneDay(int year,int month,int day,int kind){
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select sum(money) from accounttb where year=? and month=? and day=? and kind=?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(kind)});
            // 遍历
            total = readSumMoney(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }
    /**
     * 获取某一月的支出或者收入的总金额   kind：支出==0    收入===1
     * */
    @SuppressLint("Range")
    public static float getSumMoneyOneMonth(int year,int month,int kind){
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select sum(money) from accounttb where year=? and month=? and kind=?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(kind)});
            total = readSumMoney(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }
    /** 统计某月份支出或者收入情况有多少条  收入-1   支出-0*/
    @SuppressLint("Range")
    public static int getCountItemOneMonth(int year,int month,int kind){
        int total = 0;
        Cursor cursor = null;
        try {
            String sql = "select count(money) from accounttb where year=? and month=? and kind=?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(kind)});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("count(money)"));
                total = count;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }
    /**
     * 获取某一年的支出或者收入的总金额   kind：支出==0    收入===1
     * */
    @SuppressLint("Range")
    public static float getSumMoneyOneYear(int year,int kind){
        float total = 0.0f;
        Cursor cursor = null;
        try {
            String sql = "select sum(money) from accounttb where year=? and kind=?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(kind)});
            total = readSumMoney(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /*
    * 根据传入的id，删除accounttb表当中的一条数据
    * */
    public static int deleteItemFromAccounttbById(int id){
        int i = getDb().delete("accounttb", "id=?", new String[]{String.valueOf(id)});
        return i;
    }

    /*
    * 根据传入的AccountBean对象，更新accounttb表当中的一条数据
    * */
    public static int updateItemInAccounttb(AccountBean bean){
        ContentValues values = new ContentValues();
        values.put("typename",bean.getTypename());
        values.put("sImageId",bean.getsImageId());
        values.put("beizhu",bean.getBeizhu());
        values.put("money",bean.getMoney());
        values.put("time",bean.getTime());
        values.put("year",bean.getYear());
        values.put("month",bean.getMonth());
        values.put("day",bean.getDay());
        values.put("kind",bean.getKind());
        int i = getDb().update("accounttb", values, "id=?", new String[]{String.valueOf(bean.getId())});
        return i;
    }
    
    /*
    * 根据id查询accounttb表当中的一条数据
    * */
    @SuppressLint("Range")
    public static AccountBean getAccountItemById(int id){
        AccountBean bean = null;
        Cursor cursor = null;
        try {
            String sql = "select * from accounttb where id=?";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                bean = readAccountBean(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bean;
    }
    /**
     * 根据备注搜索收入或者支出的情况列表（优化版本）
     * 支持多字段搜索、分页和排序
     * @param keyword 搜索关键词
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @param sortBy 排序方式：0-按时间倒序，1-按金额倒序，2-按相关度
     * @return 搜索结果列表
     */
    @SuppressLint("Range")
    public static List<AccountBean> searchAccountList(String keyword, int offset, int limit, int sortBy) {
        List<AccountBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String orderBy = "order by id desc";
            if (sortBy == 1) {
                orderBy = "order by money desc";
            } else if (sortBy == 2) {
                // 按相关度排序：精确匹配优先
                orderBy = "order by case when beizhu = ? then 0 " +
                        "when beizhu like ? then 1 " +
                        "else 2 end, id desc";
            }
            
            // 使用参数化查询，更安全且支持索引
            String sql = "select * from accounttb where beizhu like ? " + 
                    "or typename like ? " +
                    orderBy;
            
            if (limit > 0) {
                sql += " limit ? offset ?";
            }
            
            String searchPattern = "%" + keyword + "%";
            String[] args;
            if (sortBy == 2) {
                if (limit > 0) {
                    args = new String[]{keyword, searchPattern, searchPattern, searchPattern, 
                            String.valueOf(limit), String.valueOf(offset)};
                } else {
                    args = new String[]{keyword, searchPattern, searchPattern, searchPattern};
                }
            } else {
                if (limit > 0) {
                    args = new String[]{searchPattern, searchPattern, 
                            String.valueOf(limit), String.valueOf(offset)};
                } else {
                    args = new String[]{searchPattern, searchPattern};
                }
            }
            
            cursor = getDb().rawQuery(sql, args);
            while (cursor.moveToNext()) {
                list.add(readAccountBean(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    
    /**
     * 旧的搜索方法，保留兼容性
     */
    @SuppressLint("Range")
    public static List<AccountBean> getAccountListByRemarkFromAccounttb(String beizhu) {
        return searchAccountList(beizhu, 0, 0, 0);
    }
    
    /**
     * 获取搜索结果总数
     */
    @SuppressLint("Range")
    public static int getSearchResultCount(String keyword) {
        int count = 0;
        Cursor cursor = null;
        try {
            String sql = "select count(*) from accounttb where beizhu like ? or typename like ?";
            String searchPattern = "%" + keyword + "%";
            cursor = getDb().rawQuery(sql, new String[]{searchPattern, searchPattern});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    
    /**
     * 添加搜索历史记录
     */
    public static void addSearchHistory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        keyword = keyword.trim();
        
        Cursor cursor = null;
        try {
            // 先检查是否已存在该关键词
            String checkSql = "select id, searchcount from searchhistorytb where keyword = ?";
            cursor = getDb().rawQuery(checkSql, new String[]{keyword});
            
            if (cursor.moveToFirst()) {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                int count = cursor.getInt(cursor.getColumnIndex("searchcount"));
                // 更新搜索次数和时间
                ContentValues values = new ContentValues();
                values.put("searchcount", count + 1);
                values.put("searchtime", System.currentTimeMillis());
                getDb().update("searchhistorytb", values, "id=?", new String[]{String.valueOf(id)});
            } else {
                // 插入新记录
                ContentValues values = new ContentValues();
                values.put("keyword", keyword);
                values.put("searchtime", System.currentTimeMillis());
                values.put("searchcount", 1);
                getDb().insert("searchhistorytb", null, values);
            }
            
            // 只保留最近100条记录
            String cleanSql = "delete from searchhistorytb where id not in " +
                    "(select id from searchhistorytb order by searchtime desc limit 100)";
            getDb().execSQL(cleanSql);
            
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    /**
     * 获取搜索历史记录
     * @param limit 返回数量限制
     */
    @SuppressLint("Range")
    public static List<SearchHistoryBean> getSearchHistory(int limit) {
        List<SearchHistoryBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select * from searchhistorytb order by searchtime desc";
            if (limit > 0) {
                sql += " limit " + limit;
            }
            cursor = getDb().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String keyword = cursor.getString(cursor.getColumnIndex("keyword"));
                long searchTime = cursor.getLong(cursor.getColumnIndex("searchtime"));
                int searchCount = cursor.getInt(cursor.getColumnIndex("searchcount"));
                SearchHistoryBean bean = new SearchHistoryBean(id, keyword, searchTime, searchCount);
                list.add(bean);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    
    /**
     * 删除单条搜索历史
     */
    public static void deleteSearchHistory(int id) {
        getDb().delete("searchhistorytb", "id=?", new String[]{String.valueOf(id)});
    }
    
    /**
     * 清空所有搜索历史
     */
    public static void clearSearchHistory() {
        getDb().delete("searchhistorytb", null, null);
    }
    
    /**
     * 获取智能搜索建议
     * @param input 用户输入的前缀
     */
    @SuppressLint("Range")
    public static List<String> getSearchSuggestions(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String prefix = input.trim();
        List<String> suggestions = new ArrayList<>();
        Cursor historyCursor = null;
        Cursor remarkCursor = null;
        try {
            String sql = "select distinct keyword from searchhistorytb " +
                    "where keyword like ? order by searchcount desc, searchtime desc limit 10";
            historyCursor = getDb().rawQuery(sql, new String[]{prefix + "%"});
            while (historyCursor.moveToNext()) {
                suggestions.add(historyCursor.getString(0));
            }

            if (suggestions.size() < 5) {
                int remain = 10 - suggestions.size();
                sql = "select distinct beizhu from accounttb where beizhu like ? and beizhu != '' limit ?";
                remarkCursor = getDb().rawQuery(sql, new String[]{prefix + "%", String.valueOf(remain)});
                while (remarkCursor.moveToNext()) {
                    String keyword = remarkCursor.getString(0);
                    if (keyword != null && !keyword.isEmpty() && !suggestions.contains(keyword)) {
                        suggestions.add(keyword);
                    }
                }
            }
        } finally {
            if (historyCursor != null) {
                historyCursor.close();
            }
            if (remarkCursor != null) {
                remarkCursor.close();
            }
        }
        return suggestions;
    }

    /**
     * 查询记账的表当中有几个年份信息
     * */@SuppressLint("Range")
    public static List<Integer>getYearListFromAccounttb(){
        List<Integer>list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "select distinct(year) from accounttb order by year asc";
            cursor = getDb().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                list.add(year);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /*
    * 删除accounttb表格当中的所有数据
    * */
    public static void deleteAllAccount(){
        String sql = "delete from accounttb";
        getDb().execSQL(sql);
    }

    /**
    * 获取这个月当中某一天收入支出最大的金额，金额是多少
     * */
    @SuppressLint("Range")
    public static float getMaxMoneyOneDayInMonth(int year,int month,int kind){
        Cursor cursor = null;
        try {
            String sql = "select sum(money) from accounttb where year=? and month=? and kind=? group by day order by sum(money) desc";
            cursor = getDb().rawQuery(sql, new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(kind)});
            return readSumMoney(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @SuppressLint("Range")
    private static AccountBean readAccountBean(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String typename = cursor.getString(cursor.getColumnIndex("typename"));
        String beizhu = cursor.getString(cursor.getColumnIndex("beizhu"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        int sImageId = cursor.getInt(cursor.getColumnIndex("sImageId"));
        int kind = cursor.getInt(cursor.getColumnIndex("kind"));
        float money = cursor.getFloat(cursor.getColumnIndex("money"));
        int year = cursor.getInt(cursor.getColumnIndex("year"));
        int month = cursor.getInt(cursor.getColumnIndex("month"));
        int day = cursor.getInt(cursor.getColumnIndex("day"));
        return new AccountBean(id, typename, sImageId, beizhu, money, time, year, month, day, kind);
    }

    private static float readSumMoney(Cursor cursor) {
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            return cursor.getFloat(0);
        }
        return 0f;
    }

    // ==================== 用户管理方法 ====================

    /**
     * 用户注册：向usertb表插入新用户数据
     * 调用方需在调用前完成前端校验（用户名长度、密码强度、两次密码一致）
     * 和查重（isUsernameExists），本方法只负责写入数据库
     *
     * @param username 用户名（已校验非空且长度合法）
     * @param password 明文密码（已校验强度）
     * @return 注册成功返回true；主键冲突或SQL异常返回false
     */
    public static boolean registerUser(String username, String password) {
        try {
            // 生成随机盐值 + 计算SHA-256哈希
            String salt = PasswordUtils.generateSalt();
            String hashPassword = PasswordUtils.hashPassword(password, salt);
            if (hashPassword == null) {
                android.util.Log.e("DBManager", "registerUser: 密码哈希计算失败");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.USER_USERNAME, username);
            values.put(DBOpenHelper.USER_PASSWORD_HASH, hashPassword);
            values.put(DBOpenHelper.USER_SALT, salt);
            values.put(DBOpenHelper.USER_STATUS, 0); // 正常状态
            values.put(DBOpenHelper.USER_CREATED_AT, System.currentTimeMillis());

            long result = getDb().insert(DBOpenHelper.TABLE_USER, null, values);
            return result != -1;
        } catch (Exception e) {
            android.util.Log.e("DBManager", "registerUser: 注册失败", e);
            return false;
        }
    }

    /**
     * 根据用户名查询用户完整信息
     * 用于登录时获取用户的哈希值和盐值进行密码比对
     *
     * @param username 用户名
     * @return 查询到返回UserBean对象；未找到或异常时返回null
     */
    @SuppressLint("Range")
    public static UserBean queryUserByUsername(String username) {
        UserBean bean = null;
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM " + DBOpenHelper.TABLE_USER +
                    " WHERE " + DBOpenHelper.USER_USERNAME + " = ? LIMIT 1";
            cursor = getDb().rawQuery(sql, new String[]{username});
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_USERNAME));
                String hash = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_PASSWORD_HASH));
                String salt = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_SALT));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_STATUS));
                long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_CREATED_AT));
                bean = new UserBean(id, name, hash, salt, status, createdAt);
            }
        } catch (Exception e) {
            android.util.Log.e("DBManager", "queryUserByUsername: 查询失败", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bean;
    }

    /**
     * 检查用户名是否已存在
     * 用于注册前的重复性校验
     *
     * @param username 待检查的用户名
     * @return 已存在返回true，不存在或异常返回false
     */
    public static boolean isUsernameExists(String username) {
        Cursor cursor = null;
        try {
            String sql = "SELECT COUNT(*) FROM " + DBOpenHelper.TABLE_USER +
                    " WHERE " + DBOpenHelper.USER_USERNAME + " = ?";
            cursor = getDb().rawQuery(sql, new String[]{username});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            android.util.Log.e("DBManager", "isUsernameExists: 查询失败", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 根据用户ID更新密码（修改密码功能）
     * 强制重新生成盐值，不复用旧盐值
     *
     * @param userId      用户ID
     * @param newPassword 新的明文密码（已校验强度）
     * @return 更新成功返回true；用户不存在或SQL异常返回false
     */
    public static boolean updatePassword(int userId, String newPassword) {
        try {
            // 生成全新随机盐值 + 计算新哈希
            String newSalt = PasswordUtils.generateSalt();
            String newHashPassword = PasswordUtils.hashPassword(newPassword, newSalt);
            if (newHashPassword == null) {
                android.util.Log.e("DBManager", "updatePassword: 密码哈希计算失败");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(DBOpenHelper.USER_PASSWORD_HASH, newHashPassword);
            values.put(DBOpenHelper.USER_SALT, newSalt);

            int affectedRows = getDb().update(
                    DBOpenHelper.TABLE_USER,
                    values,
                    DBOpenHelper.USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
            return affectedRows > 0;
        } catch (Exception e) {
            android.util.Log.e("DBManager", "updatePassword: 更新失败", e);
            return false;
        }
    }
}
