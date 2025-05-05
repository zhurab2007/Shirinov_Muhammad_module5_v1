package uz.pdp.Entity;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Book {
    private String name;
    private int price;
    private int quantity;
    private String imageFileId;
    private String documentFileId;
}

