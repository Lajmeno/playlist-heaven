package de.neuefische.app.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@AllArgsConstructor
public class UserDocument {

    private String email;
    private String spotifyId;
    private String name;
    private String password;

    @Id
    private String id;
}
