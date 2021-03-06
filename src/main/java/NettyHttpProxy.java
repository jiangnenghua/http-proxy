import com.fibbery.bean.ServerConfig;
import com.fibbery.handler.ServerChannelInitializer;
import com.fibbery.utils.CertUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.KeyPair;

/**
 * http代理
 *
 * @author fibbery
 * @date 18/1/17
 */
@Slf4j
public class NettyHttpProxy {

    public void start(int port)throws Exception{
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioServerSocketChannel.class);
        //传入服务器配置
        ServerConfig config = new ServerConfig();
        InputStream certIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("ca.crt");
        InputStream privateKeyIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("ca_private_pkcs8.pem");
        //证书配置
        config.setCertPrivateKey(CertUtils.loadPrivateKey(privateKeyIn));
        config.setClientCert(CertUtils.loadCert(certIn));
        //生成公私钥
        KeyPair pair = CertUtils.genRsaKeyPair();
        config.setServerPrivateKey(pair.getPrivate());
        config.setServerPublicKey(pair.getPublic());

        bootstrap.childHandler(new ServerChannelInitializer(config));
        ChannelFuture future = bootstrap.bind(port).sync();
        log.info("server start at port {}", port);
        future.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws Exception {
        new NettyHttpProxy().start(4444);
    }
}
