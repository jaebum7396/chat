package chat.model;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChannelResponse {
    int statusCode;
    HttpStatus status;
    String message;
    Map<String,Object> result;
}
