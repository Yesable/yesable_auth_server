package yesable.auth.module.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yesable.auth.module.client.AuthGrpcClient;
import yesable.auth.module.model.request.CreateAuthRequest;
import yesable.auth.module.model.request.VerifyAuthRequest;
import yesable.auth.service.service.*;

/**
 * deprecated
 */
@RestController
@RequestMapping("/auth-service/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth 관련 컨트롤러 (기업/사용자 userType 으로 구분)")
public class AuthController {

    private final AuthGrpcClient authGrpcClient;

    @Operation(summary = "로그인", description = """
            아이디, 비밀번호 기반으로 paseto 토큰 발급
            """)
    @PostMapping("/create")
    public ResponseEntity<String> createAuth(@RequestBody CreateAuthRequest request) throws InvalidProtocolBufferException {
        CreateTokenRequest tokenRequest = CreateTokenRequest.newBuilder()
                .setAuth(AuthData.newBuilder()
                        .setId(request.getId())
                        .setPw(request.getPw())
                        .setUserType(UserType.valueOf(request.getUserType().getName()))
                        .build())
                .build();

        CreateTokenResponse response = authGrpcClient.createAuth(tokenRequest);

        String jsonResponse = JsonFormat.printer().print(response);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(jsonResponse, headers, HttpStatus.OK);
    }

    @Operation(summary = "토큰 유효 검사", description = """
           발급 받은 토큰의 유효성 검사
           Status - SUCCESS, FAILED, EXPIRED_TIME 으로 구분
            """)
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAuth(@RequestBody VerifyAuthRequest request) throws InvalidProtocolBufferException {
        VerifyTokenRequest verifyRequest = VerifyTokenRequest.newBuilder()
                .setToken(request.getToken())
                .build();

        VerifyTokenResponse response = authGrpcClient.verifyAuth(verifyRequest);

        String jsonResponse = JsonFormat.printer().print(response);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(jsonResponse, headers, HttpStatus.OK);
    }
}
