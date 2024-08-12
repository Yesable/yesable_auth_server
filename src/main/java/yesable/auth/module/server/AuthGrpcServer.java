package yesable.auth.module.server;

import io.grpc.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yesable.auth.module.service.AuthService;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;

import java.io.IOException;

@Component
public class AuthGrpcServer {

    @Value("${grpc.server.port}")
    private int grpcPort;

    private final AuthService authService;

    @Autowired
    public AuthGrpcServer(AuthService authService) {
        this.authService = authService;
    }

    public void start() throws IOException, InterruptedException {
        // Configure the server with SO_REUSEADDR option enabled
        Server server = NettyServerBuilder.forPort(grpcPort)
                .withChildOption(ChannelOption.SO_REUSEADDR, true)
                .addService(authService)
                .build()
                .start();

        System.out.println("Server started, listening on " + server.getPort());

        // Add shutdown hook to stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server since JVM is shutting down");
            this.stop(server);
            System.out.println("Server shut down");
        }));

        // Keep the server running until it is terminated
        blockUntilShutdown(server);
    }

    public void stop(Server server) {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown(Server server) throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
