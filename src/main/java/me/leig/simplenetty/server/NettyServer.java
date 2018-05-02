package me.leig.simplenetty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.leig.simplenetty.bean.NettyDecoder;
import me.leig.simplenetty.bean.NettyEncoder;
import me.leig.simplenetty.handler.NettyServerHandler;
import me.leig.simplenetty.handler.ServerListener;
import org.apache.log4j.Logger;

/**
 * Created by i on 2017/10/12.
 */

public class NettyServer {

    private final static Logger log = Logger.getLogger(NettyServer.class);

    private int mPort;
    private ServerListener mServerListener;

    public NettyServer(ServerListener serverListener) {
        this.mPort = serverListener.getCtxData().getPort();
        this.mServerListener = serverListener;
    }

    public void startUp() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(
                            new NettyEncoder(),
                            new NettyDecoder(),
                            new NettyServerHandler(mServerListener));
                }
            }).option(ChannelOption.SO_BACKLOG, 1024)
                    // 地址是否可复用（UDP socket address绑定时用到）
                    // 作用是重用处于TIME_WAIT但是未完全关闭的socket地址
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 设置封包 使用一次大数据的写操作，而不是多次小数据的写操作
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture cf = sb.bind(mPort).sync();

            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("=========>" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
