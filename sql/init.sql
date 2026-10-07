-- ============================================================
-- 企业管理后台系统 · 建表脚本 + 种子数据
-- 数据库: enterprise_admin
-- 默认账号: admin / 123456
-- ============================================================

CREATE DATABASE IF NOT EXISTS enterprise_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE enterprise_admin;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、部门表 sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id`    BIGINT       NOT NULL DEFAULT 0     COMMENT '父部门ID(0=顶级)',
  `dept_name`    VARCHAR(50)  NOT NULL               COMMENT '部门名称',
  `order_num`   INT          NOT NULL DEFAULT 0     COMMENT '显示顺序',
  `status`      CHAR(1)      NOT NULL DEFAULT '0'   COMMENT '状态(0=正常 1=停用)',
  `create_by`   VARCHAR(50)           DEFAULT ''     COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   VARCHAR(50)           DEFAULT ''     COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL   COMMENT '更新时间',
  `deleted`     TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '逻辑删除(0=存在 1=删除)',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 2、用户表 sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL               COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL               COMMENT '密码(BCrypt)',
  `nickname`    VARCHAR(50)           DEFAULT ''     COMMENT '昵称',
  `avatar`      VARCHAR(200)          DEFAULT ''     COMMENT '头像地址',
  `phone`       VARCHAR(20)           DEFAULT ''     COMMENT '手机号',
  `status`      CHAR(1)      NOT NULL DEFAULT '0'   COMMENT '状态(0=正常 1=停用)',
  `create_by`   VARCHAR(50)           DEFAULT ''     COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   VARCHAR(50)           DEFAULT ''     COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL   COMMENT '更新时间',
  `deleted`     TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '逻辑删除(0=存在 1=删除)',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`, `deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='后台用户表';

-- ----------------------------
-- 3、角色表 sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`   VARCHAR(50)  NOT NULL               COMMENT '角色名称',
  `role_key`    VARCHAR(100) NOT NULL               COMMENT '角色权限标识',
  `status`      CHAR(1)      NOT NULL DEFAULT '0'   COMMENT '状态(0=正常 1=停用)',
  `remark`      VARCHAR(200)          DEFAULT ''     COMMENT '备注',
  `create_by`   VARCHAR(50)           DEFAULT ''     COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   VARCHAR(50)           DEFAULT ''     COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL   COMMENT '更新时间',
  `deleted`     TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '逻辑删除(0=存在 1=删除)',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 4、菜单表 sys_menu（树形，含目录/菜单/按钮）
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id`   BIGINT       NOT NULL DEFAULT 0     COMMENT '父菜单ID(0=顶级)',
  `menu_name`   VARCHAR(50)  NOT NULL               COMMENT '菜单名称',
  `path`        VARCHAR(200)          DEFAULT ''     COMMENT '路由地址',
  `component`   VARCHAR(200)          DEFAULT ''     COMMENT '前端组件路径',
  `perms`       VARCHAR(100)          DEFAULT ''     COMMENT '权限标识(如 system:user:list)',
  `menu_type`   CHAR(1)      NOT NULL DEFAULT 'C'   COMMENT '菜单类型(M=目录 C=菜单 F=按钮)',
  `icon`        VARCHAR(100)          DEFAULT ''     COMMENT '图标',
  `order_num`   INT          NOT NULL DEFAULT 0     COMMENT '显示顺序',
  `visible`     CHAR(1)      NOT NULL DEFAULT '0'   COMMENT '是否显示(0=显示 1=隐藏)',
  `create_by`   VARCHAR(50)           DEFAULT ''     COMMENT '创建者',
  `create_time` DATETIME              DEFAULT NULL   COMMENT '创建时间',
  `update_by`   VARCHAR(50)           DEFAULT ''     COMMENT '更新者',
  `update_time` DATETIME              DEFAULT NULL   COMMENT '更新时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='菜单与权限表';

-- ----------------------------
-- 5、用户-角色关联表 sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 6、角色-菜单关联表 sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 7、员工表 employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `emp_id`     BIGINT         NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name`        VARCHAR(50)    NOT NULL               COMMENT '姓名',
  `gender`      CHAR(1)        NOT NULL DEFAULT '0'   COMMENT '性别(0=男 1=女)',
  `phone`       VARCHAR(20)             DEFAULT ''    COMMENT '手机号',
  `email`       VARCHAR(50)             DEFAULT ''    COMMENT '邮箱',
  `dept_id`     BIGINT         NOT NULL               COMMENT '部门ID',
  `position`    VARCHAR(50)             DEFAULT ''    COMMENT '职位',
  `salary`      DECIMAL(10,2)           DEFAULT 0     COMMENT '薪资',
  `hire_date`   DATE                    DEFAULT NULL  COMMENT '入职日期',
  `status`      CHAR(1)        NOT NULL DEFAULT '0'   COMMENT '状态(0=在职 1=离职)',
  `create_by`   VARCHAR(50)             DEFAULT ''    COMMENT '创建者',
  `create_time` DATETIME                DEFAULT NULL  COMMENT '创建时间',
  `update_by`   VARCHAR(50)             DEFAULT ''    COMMENT '更新者',
  `update_time` DATETIME                DEFAULT NULL  COMMENT '更新时间',
  `deleted`     TINYINT(1)     NOT NULL DEFAULT 0     COMMENT '逻辑删除(0=存在 1=删除)',
  PRIMARY KEY (`emp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 种子数据
-- ============================================================

-- ----------------------------
-- 部门数据
-- ----------------------------
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `dept_name`, `order_num`, `status`, `create_by`, `create_time`) VALUES
(1, 0, '总公司',     0, '0', 'admin', '2025-01-01 09:00:00'),
(2, 1, '研发部',     1, '0', 'admin', '2025-01-01 09:00:00'),
(3, 1, '市场部',     2, '0', 'admin', '2025-01-01 09:00:00'),
(4, 1, '财务部',     3, '0', 'admin', '2025-01-01 09:00:00'),
(5, 1, '人力资源部', 4, '0', 'admin', '2025-01-01 09:00:00');

-- ----------------------------
-- 角色数据
-- ----------------------------
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `status`, `remark`, `create_by`, `create_time`) VALUES
(1, '管理员', 'admin',  '0', '系统管理员，拥有所有权限', 'admin', '2025-01-01 09:00:00'),
(2, '普通员工','common', '0', '只能查看，不能操作',     'admin', '2025-01-01 09:00:00');

-- ----------------------------
-- 用户数据（admin / 123456，密码为 BCrypt 加密）
-- ----------------------------
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `avatar`, `phone`, `status`, `create_by`, `create_time`) VALUES
(1, 'admin', '$2a$10$f8yN41QONguDtvRMS8DKhOehtIJ8gn4gL9P3uX81aA.UXpN6qObx6', '系统管理员', '', '13800138000', '0', 'admin', '2025-01-01 09:00:00');

-- ----------------------------
-- 菜单数据（树形：系统管理 / 业务管理 / 数据可视化）
-- menu_type: M=目录 C=菜单 F=按钮
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `parent_id`, `menu_name`, `path`, `component`, `perms`, `menu_type`, `icon`, `order_num`, `visible`, `create_by`, `create_time`) VALUES
-- 一级目录
(1, 0, '系统管理',   'system',     '', '', 'M', 'Setting',   1, '0', 'admin', '2025-01-01 09:00:00'),
(2, 0, '业务管理',   'business',   '', '', 'M', 'Briefcase', 2, '0', 'admin', '2025-01-01 09:00:00'),
(3, 0, '数据可视化', 'dashboard',  '', '', 'M', 'DataLine',  3, '0', 'admin', '2025-01-01 09:00:00'),

-- 系统管理 > 用户管理
(100, 1, '用户管理', 'user',  'system/user/index',  '',                       'C', 'User',      1, '0', 'admin', '2025-01-01 09:00:00'),
(101, 100, '用户查询', '', '', 'system:user:list',    'F', '', 1, '0', 'admin', '2025-01-01 09:00:00'),
(102, 100, '用户新增', '', '', 'system:user:add',     'F', '', 2, '0', 'admin', '2025-01-01 09:00:00'),
(103, 100, '用户修改', '', '', 'system:user:edit',    'F', '', 3, '0', 'admin', '2025-01-01 09:00:00'),
(104, 100, '用户删除', '', '', 'system:user:remove',  'F', '', 4, '0', 'admin', '2025-01-01 09:00:00'),

-- 系统管理 > 角色管理
(110, 1, '角色管理', 'role', 'system/role/index',  '',                       'C', 'UserFilled',2, '0', 'admin', '2025-01-01 09:00:00'),
(111, 110, '角色查询', '', '', 'system:role:list',    'F', '', 1, '0', 'admin', '2025-01-01 09:00:00'),
(112, 110, '角色新增', '', '', 'system:role:add',     'F', '', 2, '0', 'admin', '2025-01-01 09:00:00'),
(113, 110, '角色修改', '', '', 'system:role:edit',    'F', '', 3, '0', 'admin', '2025-01-01 09:00:00'),
(114, 110, '角色删除', '', '', 'system:role:remove',  'F', '', 4, '0', 'admin', '2025-01-01 09:00:00'),

-- 系统管理 > 菜单管理
(120, 1, '菜单管理', 'menu', 'system/menu/index',  '',                        'C', 'Menu',      3, '0', 'admin', '2025-01-01 09:00:00'),
(121, 120, '菜单查询', '', '', 'system:menu:list',    'F', '', 1, '0', 'admin', '2025-01-01 09:00:00'),
(122, 120, '菜单新增', '', '', 'system:menu:add',     'F', '', 2, '0', 'admin', '2025-01-01 09:00:00'),
(123, 120, '菜单修改', '', '', 'system:menu:edit',    'F', '', 3, '0', 'admin', '2025-01-01 09:00:00'),
(124, 120, '菜单删除', '', '', 'system:menu:remove',  'F', '', 4, '0', 'admin', '2025-01-01 09:00:00'),

-- 系统管理 > 部门管理
(130, 1, '部门管理', 'dept', 'system/dept/index',  '',                         'C', 'OfficeBuilding', 4, '0', 'admin', '2025-01-01 09:00:00'),
(131, 130, '部门查询', '', '', 'system:dept:list',    'F', '', 1, '0', 'admin', '2025-01-01 09:00:00'),
(132, 130, '部门新增', '', '', 'system:dept:add',     'F', '', 2, '0', 'admin', '2025-01-01 09:00:00'),
(133, 130, '部门修改', '', '', 'system:dept:edit',    'F', '', 3, '0', 'admin', '2025-01-01 09:00:00'),
(134, 130, '部门删除', '', '', 'system:dept:remove',  'F', '', 4, '0', 'admin', '2025-01-01 09:00:00'),

-- 业务管理 > 员工管理
(200, 2, '员工管理', 'employee', 'employee/index', '',                         'C', 'Avatar',    1, '0', 'admin', '2025-01-01 09:00:00'),
(201, 200, '员工查询', '', '', 'employee:list',       'F', '', 1, '0', 'admin', '2025-01-01 09:00:00'),
(202, 200, '员工新增', '', '', 'employee:add',        'F', '', 2, '0', 'admin', '2025-01-01 09:00:00'),
(203, 200, '员工修改', '', '', 'employee:edit',       'F', '', 3, '0', 'admin', '2025-01-01 09:00:00'),
(204, 200, '员工删除', '', '', 'employee:remove',     'F', '', 4, '0', 'admin', '2025-01-01 09:00:00'),
(205, 200, '员工导出', '', '', 'employee:export',    'F', '', 5, '0', 'admin', '2025-01-01 09:00:00'),

-- 数据可视化 > 数据大屏
(300, 3, '数据大屏', 'screen', 'dashboard/screen', '',                         'C', 'DataAnalysis', 1, '0', 'admin', '2025-01-01 09:00:00');

-- ----------------------------
-- 用户-角色关联（admin 用户 = 管理员角色）
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- ----------------------------
-- 角色-菜单关联（管理员 = 所有菜单）
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3),
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104),
(1, 110), (1, 111), (1, 112), (1, 113), (1, 114),
(1, 120), (1, 121), (1, 122), (1, 123), (1, 124),
(1, 130), (1, 131), (1, 132), (1, 133), (1, 134),
(1, 200), (1, 201), (1, 202), (1, 203), (1, 204), (1, 205),
(1, 300);

-- ----------------------------
-- 员工示例数据（15 条，跨部门、跨时间，用于大屏图表展示）
-- ----------------------------
INSERT INTO `employee` (`name`, `gender`, `phone`, `email`, `dept_id`, `position`, `salary`, `hire_date`, `status`, `create_by`, `create_time`) VALUES
('张伟',   '0', '13912345001', 'zhangwei@company.com',   2, '后端工程师',  15000.00, '2024-01-15', '0', 'admin', '2024-01-15 09:00:00'),
('王芳',   '1', '13912345002', 'wangfang@company.com',   2, '前端工程师',  14000.00, '2024-02-20', '0', 'admin', '2024-02-20 09:00:00'),
('李娜',   '1', '13912345003', 'lina@company.com',       2, '测试工程师',  11000.00, '2024-03-10', '0', 'admin', '2024-03-10 09:00:00'),
('刘强',   '0', '13912345004', 'liuqiang@company.com',   2, '架构师',      28000.00, '2023-06-01', '0', 'admin', '2023-06-01 09:00:00'),
('陈杰',   '0', '13912345005', 'chenjie@company.com',    3, '市场专员',     9000.00, '2024-04-05', '0', 'admin', '2024-04-05 09:00:00'),
('杨洋',   '1', '13912345006', 'yangyang@company.com',   3, '市场经理',    18000.00, '2023-08-15', '0', 'admin', '2023-08-15 09:00:00'),
('赵敏',   '1', '13912345007', 'zhaomin@company.com',    4, '会计',        12000.00, '2023-09-01', '0', 'admin', '2023-09-01 09:00:00'),
('黄磊',   '0', '13912345008', 'huanglei@company.com',   4, '财务经理',    20000.00, '2023-03-12', '0', 'admin', '2023-03-12 09:00:00'),
('周婷',   '1', '13912345009', 'zhouting@company.com',   5, 'HR专员',      10000.00, '2024-05-18', '0', 'admin', '2024-05-18 09:00:00'),
('吴昊',   '0', '13912345010', 'wuhao@company.com',      5, 'HR经理',      17000.00, '2023-07-22', '0', 'admin', '2023-07-22 09:00:00'),
('郑爽',   '1', '13912345011', 'zhengshuang@company.com', 2, '产品经理',    16000.00, '2024-06-03', '0', 'admin', '2024-06-03 09:00:00'),
('孙超',   '0', '13912345012', 'sunchao@company.com',    3, '销售代表',     8500.00, '2025-01-10', '0', 'admin', '2025-01-10 09:00:00'),
('马丽',   '1', '13912345013', 'mali@company.com',       4, '出纳',         9500.00, '2025-02-14', '0', 'admin', '2025-02-14 09:00:00'),
('朱军',   '0', '13912345014', 'zhujun@company.com',      2, '运维工程师',  13000.00, '2025-03-20', '0', 'admin', '2025-03-20 09:00:00'),
('胡静',   '1', '13912345015', 'hujing@company.com',      5, '招聘主管',    14500.00, '2024-07-08', '0', 'admin', '2024-07-08 09:00:00');

-- ============================================================
-- 完成。登录账号: admin / 123456
-- ============================================================
