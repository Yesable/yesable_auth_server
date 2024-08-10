package yesable.auth.module.model.request;

import lombok.Data;
import yesable.auth.module.enums.UserType;

@Data
public class CreateAuthRequest {
    private String id;
    private String pw;
    private UserType userType;
}
