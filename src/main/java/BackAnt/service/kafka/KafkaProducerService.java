package BackAnt.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // 비동기 처리로 성능 향상
    public void sendMessage(String topic, String message) {
        // send() 메서드 자체가 CompletableFuture를 반환
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.thenAccept(result -> {
            System.out.println("Message sent successfully: " + message +
                    " to topic: " + topic +
                    " at offset: " + result.getRecordMetadata().offset());
        }).exceptionally(ex -> {
            System.err.println("Message failed to send: " + message +
                    " due to: " + ex.getMessage());
            return null; // 예외 처리 후 null 반환
        });
    }
}
