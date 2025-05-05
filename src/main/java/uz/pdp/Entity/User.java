package uz.pdp.Entity;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long chatId;
    private String username;
    private List<Order> orders;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long chatId;
        private String username;
        private List<Order> orders;

        public UserBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder orders(List<Order> orders) {
            this.orders = orders;
            return this;
        }

        public User build() {
            return new User(chatId, username, orders);
        }
    }
}
