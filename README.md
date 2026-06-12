# 统计分析记账 · Statistical Analysis

[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-orange)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

一款简洁实用的 Android 个人记账应用，支持收支记录、图表统计、预算管理和数据备份。

An Android personal finance tracker with expense/income recording, chart analytics, budget management, and data backup.

---

## ✨ 功能 · Features

- **记账管理** — 快速记录收支，支持分类、备注、时间，可编辑或删除记录
- **图表统计** — 饼图展示消费/收入分布，柱状图按日/月/年维度对比
- **预算控制** — 设置月度预算，实时显示剩余金额
- **搜索记录** — 关键词搜索历史账单，搜索历史自动保存
- **隐私保护** — 一键切换金额明文/密文显示
- **数据备份** — 数据库导出到本地存储，防止数据丢失

## 📸 截图 · Screenshots

> *点击下方标签页切换功能*

| 主页 · Main | 图表 · Chart | 记账 · Record |
|:---:|:---:|:---:|
| 今日收支概览 | 饼图 + 柱状图分析 | 分类选择 & 金额输入 |

## 🛠 技术栈 · Tech Stack

| 类别 | 技术 |
|:---|:---|
| 语言 | Java 8 |
| 最低 SDK | Android 8.0 (API 28) |
| 目标 SDK | Android 14 (API 34) |
| UI | ConstraintLayout + Material Design |
| 图表 | MPAndroidChart v3.0.3 |
| 数据 | SQLite (`typetb`, `accounttb`, `searchhistorytb`) |
| 架构 | Activity + Fragment + Adapter 模式 |

## 📁 项目结构 · Structure

```
├── app/src/main/java/com/example/accounts/
│   ├── MainActivity.java         # 主页 - 今日收支列表
│   ├── RecordActivity.java       # 记账 - 新增/编辑记录
│   ├── ChartActivity.java        # 图表 - 饼图 & 柱状图统计
│   ├── HistoryActivity.java      # 历史账单记录
│   ├── SearchActivity.java       # 搜索记录
│   ├── SettingsActivity.java     # 设置
│   ├── AboutActivity.java        # 关于
│   ├── userActivity.java         # 启动页 / 登录
│   ├── db/                       # 数据库层
│   │   ├── DBOpenHelper.java     # 建库建表 & 版本升级
│   │   ├── DBManager.java        # CRUD 操作
│   │   └── AccountBean.java      # 数据实体
│   ├── adapter/                  # ListView / ViewPager 适配器
│   ├── frag_record/              # 收支分类 Fragment
│   └── utils/                    # 工具类 (备份/日历/金额格式化)
└── app/src/main/res/             # 布局 & 资源文件
```

## 🚀 快速开始 · Quick Start

```bash
# 克隆项目
git clone https://github.com/Jiong-161/Statistical-Analysis.git

# 用 Android Studio 打开，Sync Gradle，Run ▶
```

要求 Android Studio Hedgehog (2023.1) 或更新版本。

## 📄 License

MIT © [Jiong-161](https://github.com/Jiong-161)
