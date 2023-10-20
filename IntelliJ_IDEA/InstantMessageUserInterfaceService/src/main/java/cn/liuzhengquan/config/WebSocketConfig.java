package cn.liuzhengquan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @ClassName: WebSocketConfig
 * @Author: 刘政权
 * @AuthorEmail: liuzhengquanmail@163.com
 * @AuthorWebsite: liuzhengquan.cn
 * @Date: 2023-09-12 22:04
 * @Description TODO
 * @SourceCodeCopyrightReserved(c)：刘政权
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
