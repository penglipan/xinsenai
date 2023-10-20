package cn.liuzhengquan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Results
 * @Author: 刘政权
 * @AuthorEmail: liuzhengquanmail@163.com
 * @AuthorWebsite: liuzhengquan.cn
 * @Date: 2023-09-04 9:10
 * @Description TODO 视图 Vo 结果集 基础类（Results）
 * @SourceCodeCopyrightReserved(c)：刘政权
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    /**
     * 状态 （status）
     */
    private int status;
    /**
     * 返回信息（message）
     */
    private String message;
    /**
     * 返回数据（data）
     */
    private Object data;

}
