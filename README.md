# 课堂派项目

基于 Spring Boot + Vue + Element Plus + MySQL 的课堂管理系统

## 技术栈

### 后端技术
- Spring Boot 2.7.14
- Spring MVC
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- Druid 数据源
- JWT
- Lombok

### 前端技术
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 项目结构

```
ClassPi/
├── classpi-backend/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/classpi/
│   │   │   │   ├── controller/      # 控制器层
│   │   │   │   ├── service/         # 服务层
│   │   │   │   ├── mapper/          # 数据访问层
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── dto/             # 数据传输对象
│   │   │   │   ├── vo/              # 视图对象
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── util/            # 工具类
│   │   │   │   ├── common/          # 公共类
│   │   │   │   └── exception/       # 异常类
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   │   └── test/
│   └── pom.xml
├── classpi-frontend/         # 前端项目
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── assets/           # 静态资源
│   │   ├── components/       # 组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── utils/            # 工具函数
│   │   ├── views/            # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── database/                 # 数据库脚本
│   └── init.sql
└── README.md
```

## 快速开始

### 环境要求
- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 数据库配置
1. 创建数据库：
```bash
mysql -u root -p < database/init.sql
```

2. 修改数据库连接配置：
编辑 `classpi-backend/src/main/resources/application.yml`，修改数据库用户名和密码

### 后端启动
```bash
cd classpi-backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动
```bash
cd classpi-frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 功能模块

- 用户管理（登录、注册）
- 课程管理
- 学生管理
- 作业管理
- 成绩管理

## 默认账号

- 管理员：admin / admin123
- 教师：teacher1 / teacher123
- 学生：student1 / student123

## 开发说明

### 后端开发
- 遵循 RESTful API 设计规范
- 使用 MyBatis Plus 简化数据库操作
- 统一返回格式使用 Result 类

### 前端开发
- 使用 Vue 3 Composition API
- 使用 Element Plus 组件库
- 使用 Pinia 进行状态管理
- 使用 Axios 进行 HTTP 请求

## 许可证

MIT License
