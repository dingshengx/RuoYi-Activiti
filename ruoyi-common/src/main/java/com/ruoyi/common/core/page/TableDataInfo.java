package com.ruoyi.common.core.page;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 表格分页数据对象
 *
 * @author ruoyi
 */
@Setter
@Getter
public class TableDataInfo<T> implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 总记录数 */
  private long total;

  /** 列表数据 */
  private T rows;

  /** 消息状态码 */
  private int code;

  /** 消息内容 */
  private String msg;

  /** 表格数据对象 */
  public TableDataInfo() {}

  /**
   * 分页
   *
   * @param list 列表数据
   * @param total 总记录数
   */
  public TableDataInfo(T list, int total) {
    this.rows = list;
    this.total = total;
  }
}
