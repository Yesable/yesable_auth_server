package yesable.auth.module.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import yesable.auth.module.enums.ValidateType;
import yesable.auth.global.utils.security.PasetoTokenProvider;
import yesable.auth.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 범용적 auth 사용자 인증 처리
 */
@GrpcService
@RequiredArgsConstructor
public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {

    private final PasetoTokenProvider pasetoTokenProvider;

    /**
     * 일반 로그인
     */
    @Override
    public void createAuth(CreateTokenRequest request, StreamObserver<CreateTokenResponse> responseObserver) {
        // 사용자 검증
        String userId = "1010";

        // 토큰 생성
        String token = pasetoTokenProvider.generateToken(userId);

        Instant now = Instant.now();
        CreateTokenResponse response = CreateTokenResponse.newBuilder().setAuth(
                            AuthData.newBuilder()
                                .setUserId(userId)
                                .setCreateTime(now.toEpochMilli())
                                .setExpireTime(now.plus(2, ChronoUnit.HOURS).toEpochMilli())
                                .setToken(token))
        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    /**
     * oAuth 기반 로그인
     */

    /**
     * 토큰 검증
     */
    @Override
    public void verifyAuth(VerifyTokenRequest request, StreamObserver<VerifyTokenResponse> responseObserver) {
        ValidateType v = pasetoTokenProvider.validateToken(request.getToken());

        Verify.Builder verifyBuilder = Verify.newBuilder();
        pasetoTokenProvider.parseToken(request.getToken());

        int statusValue = switch (v.getValue()) {
            case 0 -> ResponseType.SUCCESS_VALUE;
            case 1 -> ResponseType.EXPIRED_TIME_VALUE;
            default -> ResponseType.FAILED_VALUE;
        };

       VerifyTokenResponse response = VerifyTokenResponse.newBuilder()
                .setV(verifyBuilder
                        .setStatusValue(statusValue)
                        .setAuth(AuthData.newBuilder()
                                .setUserId(pasetoTokenProvider.extractId(request.getToken()))
                                .setToken(request.getToken()))
                        .build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

}
