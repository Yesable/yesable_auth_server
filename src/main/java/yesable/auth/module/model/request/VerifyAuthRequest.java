package yesable.auth.module.model.request;

import lombok.Data;

@Data
public class VerifyAuthRequest {
    private String token;
}
