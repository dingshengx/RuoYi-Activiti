package com.ruoyi.common.core.domain;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作消息提醒
 *
 * @author ruoyi
 */
@ApiModel
@EqualsAndHashCode(callSuper = false)
@Data
public class AjaxResult<T> extends HashMap<String, Object> {
  private static final long serialVersionUID = 1L;

  /** 状态码 */
  public static final String CODE_TAG = "code";

  /** 返回内容 */
  public static final String MSG_TAG = "msg";

  /** 数据对象 */
  public static final String DATA_TAG = "data";

  /** 状态码 */
  @ApiModelProperty(value = "状态码")
  private int code;

  /** 返回内容 */
  @ApiModelProperty(value = "返回内容")
  private String msg;

  /** 数据对象 */
  @ApiModelProperty(value = "数据对象")
  private T data;

  /** 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。 */
  public AjaxResult() {}

  /**
   * 初始化一个新创建的 AjaxResult 对象
   *
   * @param code 状态码
   * @param msg 返回内容
   */
  public AjaxResult(int code, String msg) {
    super.put(CODE_TAG, code);
    super.put(MSG_TAG, msg);
  }

  /**
   * 初始化一个新创建的 AjaxResult 对象
   *
   * @param code 状态码
   * @param msg 返回内容
   * @param data 数据对象
   */
  public AjaxResult(int code, String msg, T data) {
    super.put(CODE_TAG, code);
    super.put(MSG_TAG, msg);
    if (StringUtils.isNotNull(data)) {
      super.put(DATA_TAG, data);
    }
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  /**
   * 返回成功消息
   *
   * @return 成功消息
   */
  public static <T> AjaxResult<T> success() {
    return AjaxResult.success("操作成功");
  }

  /**
   * 返回成功数据
   *
   * @return 成功消息
   */
  public static <T> AjaxResult<T> success(T data) {
    return AjaxResult.success("操作成功", data);
  }

  /**
   * 返回成功消息
   *
   * @param msg 返回内容
   * @return 成功消息
   */
  public static <T> AjaxResult<T> success(String msg) {
    return AjaxResult.success(msg, null);
  }

  /**
   * 返回成功消息
   *
   * @param msg 返回内容
   * @param data 数据对象
   * @return 成功消息
   */
  public static <T> AjaxResult<T> success(String msg, T data) {
    return new AjaxResult<>(HttpStatus.SUCCESS, msg, data);
  }

  /**
   * 返回警告消息
   *
   * @param msg 返回内容
   * @return 警告消息
   */
  public static <T> AjaxResult<T> warn(String msg) {
    return AjaxResult.warn(msg, null);
  }

  /**
   * 返回警告消息
   *
   * @param msg 返回内容
   * @param data 数据对象
   * @return 警告消息
   */
  public static <T> AjaxResult<T> warn(String msg, T data) {
    return new AjaxResult<>(HttpStatus.WARN, msg, data);
  }

  /**
   * 返回错误消息
   *
   * @return 错误消息
   */
  public static <T> AjaxResult<T> error() {
    return AjaxResult.error("操作失败");
  }

  /**
   * 返回错误消息
   *
   * @param msg 返回内容
   * @return 错误消息
   */
  public static <T> AjaxResult<T> error(String msg) {
    return AjaxResult.error(msg, null);
  }

  /**
   * 返回错误消息
   *
   * @param msg 返回内容
   * @param data 数据对象
   * @return 错误消息
   */
  public static <T> AjaxResult<T> error(String msg, T data) {
    return new AjaxResult<T>(HttpStatus.ERROR, msg, data);
  }

  /**
   * 返回错误消息
   *
   * @param code 状态码
   * @param msg 返回内容
   * @return 错误消息
   */
  public static <T> AjaxResult<T> error(int code, String msg) {
    return new AjaxResult<T>(code, msg, null);
  }

  /**
   * 是否为成功消息
   *
   * @return 结果
   */
  public boolean isSuccess() {
    return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG));
  }

  /**
   * 是否为警告消息
   *
   * @return 结果
   */
  public boolean isWarn() {
    return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG));
  }

  /**
   * 是否为错误消息
   *
   * @return 结果
   */
  public boolean isError() {
    return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG));
  }

  /**
   * 方便链式调用
   *
   * @param key 键
   * @param value 值
   * @return 数据对象
   */
  @Override
  public AjaxResult<T> put(String key, Object value) {
    super.put(key, value);
    return this;
  }
}
