package cn.liuzhengquan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Friends
 * @Author: 刘政权
 * @AuthorEmail: liuzhengquanmail@163.com
 * @AuthorWebsite: liuzhengquan.cn
 * @Date: 2023-09-12 22:11
 * @Description TODO
 * @SourceCodeCopyrightReserved(c)：刘政权
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    /**
     * 好友昵称
     */
    private String nickname;
}
