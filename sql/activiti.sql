-- ----------------------------
-- activiti初始化配置表
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_property`;
CREATE TABLE `act_ge_property`  (
  `NAME_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `VALUE_` varchar(300) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `REV_` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`NAME_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- activiti初始化配置
-- ----------------------------
INSERT INTO `act_ge_property` VALUES ('cfg.execution-related-entities-count', 'false', 1);
INSERT INTO `act_ge_property` VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` VALUES ('schema.history', 'create(7.0.0.0)', 1);
INSERT INTO `act_ge_property` VALUES ('schema.version', '7.0.0.0', 1);

-- ----------------------------
-- 修复activiti7的M4版本缺失字段Bug (在上面的脚本执行后，并且项目启动成功生成表结构后，再执行此脚本，因为此脚本是在生成的表中增加字段)
-- ----------------------------
alter table ACT_RE_DEPLOYMENT add column PROJECT_RELEASE_VERSION_ varchar(255) DEFAULT NULL;
alter table ACT_RE_DEPLOYMENT add column VERSION_ varchar(255) DEFAULT NULL;

-- ----------------------------
-- 流程业务表 (区别于act_相关的表，这个表是业务层的抽象，用于记录业务相关的数据，也是在activiti初始化后执行)
-- ----------------------------
DROP TABLE IF EXISTS `biz_workflow`;
CREATE TABLE `biz_workflow`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `proc_inst_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '流程实例ID',
  `biz_id` bigint(20) NOT NULL COMMENT '业务ID关联业务主键',
  `biz_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务模块跟实体表名一样',
  `current_node_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前节点名称',
  `current_node_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前节点Key',
  `applicant` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人账号',
  `applicant_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人姓名',
  `applicant_id` bigint(20) NULL DEFAULT NULL COMMENT '申请人ID',
  `apply_dept` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请部门',
  `apply_time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `approver_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '待审批人账号',
  `approver_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '待审批人姓名',
  `approver_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '待审批人ID',
  `approved_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '已审批人账号',
  `approved_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '已审批人姓名',
  `approved_id` bigint(20) NULL DEFAULT NULL COMMENT '已审批人ID',
  `approved_time` datetime NULL DEFAULT NULL COMMENT '已审批时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `is_del` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_id` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  `update_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人姓名',
  `workflow_status` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '字典workflow_status',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_proc_inst_id`(`proc_inst_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '业务流程关联表' ROW_FORMAT = Dynamic;