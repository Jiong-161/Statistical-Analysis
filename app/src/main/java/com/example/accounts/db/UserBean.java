package com.example.accounts.db;

/**
 * 用户信息实体类
 * <p>
 * 对应数据库 usertb 表，存储用户注册与登录相关的核心字段。
 * 密码以SHA-256加盐哈希形式存储，不保存明文。
 * </p>
 */
public class UserBean {
    /**
     * 用户ID（自增主键）
     */
    private int id;
    /**
     * 用户名（唯一）
     */
    private String username;
    /**
     * 密码SHA-256哈希值（64位十六进制）
     */
    private String passwordHash;
    /**
     * 随机盐值（32位十六进制）
     */
    private String salt;
    /**
     * 账号状态：0-正常，1-禁用
     */
    private int status;
    /**
     * 注册时间戳（毫秒）
     */
    private long createdAt;

    public UserBean() {
    }

    /**
     * 全参构造方法
     *
     * @param id           用户ID
     * @param username     用户名
     * @param passwordHash 密码哈希值
     * @param salt         盐值
     * @param status       账号状态
     * @param createdAt    注册时间戳
     */
    public UserBean(int id, String username, String passwordHash, String salt,
                    int status, long createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
