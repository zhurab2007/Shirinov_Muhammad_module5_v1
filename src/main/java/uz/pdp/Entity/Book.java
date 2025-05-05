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
    private UUID id;
    private String name;
    private Integer price;
    private Integer quantity;
    private String bookPhoto;
    private String bookDocument;
}
