# CardCase 项目运行指南

## 项目概述
这是一个名片管理应用，包含Android客户端和Spring Boot服务器。

## 环境要求
- MySQL 数据库
- Java JDK 17
- Android Studio (用于运行客户端)
- Maven (用于构建服务器)

## 安装步骤

### 1. 安装 Java JDK 17

由于网络问题，推荐手动下载安装：

```bash
# macOS 下载地址
https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_macos-x64_bin.tar.gz

# 解压并安装
tar -xzf openjdk-17.0.2_macos-x64_bin.tar.gz
sudo mv jdk-17.0.2.jdk /Library/Java/JavaVirtualMachines/

# 设置环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. 配置 MySQL 数据库

```bash
# 确保MySQL服务已启动
brew services start mysql

# 创建数据库
mysql -u root -p
CREATE DATABASE IF NOT EXISTS cardcase;
EXIT;
```

### 3. 配置服务器

检查配置文件：`服务器/src/main/resources/application.properties`

确保以下配置正确：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cardcase
spring.datasource.username=root
spring.datasource.password=你的MySQL密码
spring.mail.username=你的QQ邮箱
spring.mail.password=你的QQ邮箱授权码
```

### 4. 启动 Spring Boot 服务器

```bash
cd 服务器
./mvnw spring-boot:run
```

服务器将在 `http://localhost:8080` 启动

### 5. 运行 Android 客户端

1. 打开 Android Studio
2. 导入项目：打开 `客户端` 目录
3. 等待 Gradle 同步完成
4. 连接 Android 设备或启动模拟器
5. 点击 Run 按钮

## 项目重构完成内容

### 已完成的功能

1. **注册验证码发送** ✅
   - 服务器端：完整实现邮件发送功能
   - 使用 QQ SMTP 服务
   - 验证码图片生成

2. **订单管理** ✅
   - 服务器 API：
     - `POST /api/orders` - 创建订单
     - `GET /api/orders/user/{userId}` - 获取用户订单列表
     - `GET /api/orders/{orderId}` - 获取订单详情
     - `PUT /api/orders/{orderId}/cancel` - 取消订单
   - 客户端 UI：PayFragment 和 Wrench1Fragment

3. **分组管理** ✅
   - 服务器 API：
     - `POST /api/groups` - 创建分组
     - `GET /api/groups/user/{userId}` - 获取用户分组列表
     - `POST /api/groups/cards` - 添加名片到分组
     - `DELETE /api/groups/cards` - 从分组移除名片
   - 客户端：GroupFragment 已有完整实现

4. **二维码扫描** ✅
   - 支持相机直接扫描
   - 支持从相册选择图片扫描
   - 使用 ZXing 库实现

5. **Settings 页面** ✅
   - Wrench1Fragment：订单列表页面
   - Wrench2Fragment：用户资料编辑页面

### 文件结构

```
CardCase/
├── 客户端/                  # Android 客户端
│   ├── app/src/main/java/
│   │   ├── foreground/       # UI Fragment
│   │   │   ├── Wrench1Fragment.kt    # 订单列表
│   │   │   ├── Wrench2Fragment.kt    # 用户资料
│   │   │   └── ScanFragment.kt       # 二维码扫描
│   │   └── entity/
│   │       └── OrderResponse.kt      # 订单数据类
│   └── ...
└── 服务器/                  # Spring Boot 服务器
    ├── src/main/java/
    │   ├── controller/
    │   │   ├── OrderController.java  # 订单API
    │   │   └── GroupController.java  # 分组API
    │   ├── service/
    │   │   ├── OrderService.java
    │   │   ├── OrderServiceImpl.java
    │   │   ├── GroupService.java
    │   │   └── GroupServiceImpl.java
    │   └── repository/
    │       ├── OrderRepo.java
    │       ├── CardGroup.java        # 分组实体
    │       └── GroupRepo.java
    └── ...
```

## API 端点列表

### 用户相关
- `POST /api/register` - 用户注册
- `POST /api/verify-email` - 邮箱验证
- `POST /api/login` - 用户登录
- `POST /api/forgot-password` - 忘记密码
- `POST /api/reset-password` - 重置密码

### 名片相关
- `POST /api/create-card` - 创建名片
- `GET /api/cards/{cardId}` - 获取名片详情
- `GET /api/cards/user/{userId}` - 获取用户名片列表
- `PUT /api/cards/{cardId}` - 更新名片
- `DELETE /api/cards/{cardId}` - 删除名片

### 订单相关
- `POST /api/orders` - 创建订单
- `GET /api/orders/user/{userId}` - 获取用户订单
- `GET /api/orders/{orderId}` - 获取订单详情
- `PUT /api/orders/{orderId}/cancel` - 取消订单

### 分组相关
- `POST /api/groups` - 创建分组
- `GET /api/groups/user/{userId}` - 获取分组列表
- `POST /api/groups/cards` - 添加名片到分组
- `DELETE /api/groups/cards` - 从分组移除名片

## 开发注意事项

1. 虚拟设备访问主机服务使用 `10.0.2.2:8080`
2. 邮箱验证需要配置 QQ 邮箱授权码
3. 所有 API 请求需要 JWT token 认证
4. 分组数据暂时存储在客户端 SQLite 中，需要迁移到服务器

## 故障排除

### 服务器无法启动
- 检查 Java 版本：`java -version`
- 检查 MySQL 连接
- 查看 application.properties 配置

### 客户端无法连接服务器
- 确保服务器正在运行
- 检查网络权限配置
- 虚拟设备使用 `10.0.2.2:8080`，真机使用局域网 IP

### 邮件发送失败
- 检查 QQ 邮箱授权码是否正确
- 确保 SMTP 配置正确

## 联系方式
如有问题请联系项目成员。
