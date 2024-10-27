package yesable.auth.global.client;

import com.example.grpc.GetPrivateUserIdRequest;
import com.example.grpc.GetPrivateUserIdResponse;
import com.example.grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import yesable.auth.module.service.AccountData;

import javax.annotation.PreDestroy;
@Component
public class PrivateUserClient {

    private final UserServiceGrpc.UserServiceBlockingStub privateUserServiceStub;
    private final ManagedChannel channel;

    public PrivateUserClient() {

        // ManagedChannel을 생성하고 싱글톤으로 관리
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9999)
                .usePlaintext()
                .build();

        // UserServiceGrpc Stub 생성
        this.privateUserServiceStub = UserServiceGrpc.newBlockingStub(channel);
    }

    public GetPrivateUserIdResponse getPrivateUserId(AccountData accountData) {
        GetPrivateUserIdRequest request = GetPrivateUserIdRequest.newBuilder()
                .setEncuserId(accountData.getId())
                .setEncuserpassword(accountData.getPw())
                .build();
        return privateUserServiceStub.getPrivateUserId(request);
    }

    @PreDestroy
    public void shutdownChannel() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}

