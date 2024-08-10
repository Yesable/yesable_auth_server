package yesable.auth.module.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yesable.auth.module.client.AuthGrpcClient;
import yesable.auth.module.model.request.CreateAuthRequest;
import yesable.auth.module.model.request.VerifyAuthRequest;
import yesable.auth.service.service.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthGrpcClient authGrpcClient;

    @PostMapping("/create")
    public ResponseEntity<CreateTokenResponse> createAuth(@RequestBody CreateAuthRequest request) {
        CreateTokenRequest tokenRequest = CreateTokenRequest.newBuilder()
                .setAuth(AuthData.newBuilder()
                        .setId(request.getId())
                        .setPw(request.getPw())
                        .setUserType(UserType.valueOf(request.getUserType().getName()))
                        .build())
                .build();

        CreateTokenResponse response = authGrpcClient.createAuth(tokenRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyTokenResponse> verifyAuth(@RequestBody VerifyAuthRequest request) {
        VerifyTokenRequest verifyRequest = VerifyTokenRequest.newBuilder()
                .setToken(request.getToken())
                .build();

        VerifyTokenResponse response = authGrpcClient.verifyAuth(verifyRequest);
        return ResponseEntity.ok(response);
    }
}
