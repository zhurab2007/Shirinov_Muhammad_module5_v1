package uz.pdp.Entity;
import com.pengrad.telegrambot.model.ChatShared;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private UUID id;
    private String login;
    private String password;

    public static ChatShared builder() {
        return null;
    }
}
