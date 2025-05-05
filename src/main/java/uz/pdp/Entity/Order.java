package uz.pdp.Entity;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Order {
    private UUID id;
    private Long userChatId;
    private UUID bookId;
    private String bookName;
    private Integer price;
    private Integer bookQuantity;
    private String bookPhotoFieldID;
    private String bookDocumentField;
    private Boolean isSold;

}
