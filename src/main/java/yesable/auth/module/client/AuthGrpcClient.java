package yesable.auth.module.client;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;
import yesable.auth.module.service.*;

@Component
public class AuthGrpcClient {

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    public AuthGrpcClient(ManagedChannel channel) {
        this.authServiceBlockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    public CreateTokenResponse createAuth(CreateTokenRequest request) {
        return authServiceBlockingStub.createAuth(request);
    }

    public VerifyTokenResponse verifyAuth(VerifyTokenRequest request) {
        return authServiceBlockingStub.verifyAuth(request);
    }
}
