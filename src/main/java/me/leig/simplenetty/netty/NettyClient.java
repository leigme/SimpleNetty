package me.leig.simplenetty.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import me.leig.simplenetty.bean.NettyDecoder;
import me.leig.simplenetty.bean.NettyEncoder;
import me.leig.simplenetty.handler.ClientListener;
import me.leig.simplenetty.handler.ConnectListener;
import me.leig.simplenetty.handler.NettyClientHandler;
import org.apache.log4j.Logger;

/**
 * Created by i on 2017/10/12.
 */

public class NettyClient {

    private final static Logger log = Logger.getLogger(NettyClient.class);

    private String mHost;
    private int mPort;
    public ClientListener mClientListener;

    public NettyClient(String remoteIP, int port, ClientListener clientListener) {
        this.mHost = remoteIP;
        this.mPort = port;
        this.mClientListener = clientListener;
    }

    public void startConnect() throws Exception {

        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(
                            new IdleStateHandler(0, 0, 5),
                            new NettyEncoder(),
                            new NettyDecoder(),
                            new NettyClientHandler(mClientListener));
                }
            });
            ChannelFuture cf = bootstrap.connect(mHost, mPort).sync();

            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("=========>" + e.getMessage());
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    public void setConnectListener(ConnectListener connectListener) {
        mClientListener.setConnectListener(connectListener);
    }
}
