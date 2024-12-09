package BackAnt.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Log4j2
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 브로커 구성
        config.enableSimpleBroker("/topic", "/queue"); // 클라이언트가 구독하는 엔드포인트
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 때 사용
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("도달??");
        registry.addEndpoint("/ws") // WebSocket 연결 엔드포인트
                .setAllowedOrigins("*"); // 허용할 클라이언트 도메인 지정
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024)
                .setSendBufferSizeLimit(512 * 1024)
                .setSendTimeLimit(20000);
    }
}