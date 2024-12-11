package com.ruoyi.activiti.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 发起人状态枚举
 *
 * @author dingsheng
 * @date 2024/6/27
 */
@Getter
public enum ProcessStatus
{

    DRAFT(0, "草稿箱"),
    TO_BE_REITERATED(1, "待重申"),
    PENDING_APPROVAL(2, "待审批"),
    IN_APPROVAL(3, "审批中"),
    APPROVED(4, "审批完成");

    private final int code;
    private final String info;

    ProcessStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    // 通过code 获取info
    public static String getInfoByCode(int code) {
        for (ProcessStatus bizApplyStatus : ProcessStatus.values()) {
            if (bizApplyStatus.getCode() == code) {
                return bizApplyStatus.getInfo();
            }
        }
        return null;
    }

    /**
     * 获取可以审批的节点
     *
     * @return nodeList
     */
    public static List<Integer> getApprovalStatus() {
        return Arrays.asList(ProcessStatus.PENDING_APPROVAL.code, ProcessStatus.IN_APPROVAL.code);
    }

    /**
     * 获取可以修改的节点
     *
     * @return nodeList
     */
    public static List<Integer> getUpdateStatus() {
        return Arrays.asList(ProcessStatus.TO_BE_REITERATED.code, ProcessStatus.DRAFT.code);
    }

}
