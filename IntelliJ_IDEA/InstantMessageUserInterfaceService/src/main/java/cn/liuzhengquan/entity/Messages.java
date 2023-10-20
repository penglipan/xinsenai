package cn.liuzhengquan.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Messages
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
public class Messages {
    /**
     * 消息类型
     */
    private String type;
    /**
     * 发送好友昵称
     */
    private String sendNickname;
    /**
     * 接收好友昵称
     */
    private String receiveNickname;
    /**
     * 消息内容
     */
    private Object messages;

/*    public static void main(String[] args) {
        Messages messages1 = new Messages();
        messages1.setType("1");
        messages1.setSendNickname("1");
        messages1.setReceiveNickname("1");
        messages1.setMessages("1");
        System.out.println(JSON.toJSONString(messages1));
    }*/
}
