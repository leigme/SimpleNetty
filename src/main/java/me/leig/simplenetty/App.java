package me.leig.simplenetty;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.handler.ConnectListener;
import me.leig.simplenetty.handler.ServerListener;
import me.leig.simplenetty.netty.NettyServer;
import me.leig.simplenetty.tool.Config;
import me.leig.simplenetty.tool.ConfigParser;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    private final static Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {

        log.info("start app...");

        try {

            ConfigParser configParser = ConfigParser.INSTANCE;

            Config config = configParser.parser();

            if (null == config) {
                log.error("config is null");
                return;
            }

            log.info("config parse() success: " + config.version);

            CtxData ctxData = new CtxData();

            ctxData.setUserId("");
            ctxData.setUserName("");
            ctxData.setLocalIP("192.168.0.155");
            ctxData.setRemark("服务器端");

            ServerListener serverListener = ServerListener.INSTANCE;

            serverListener.setCtxData(ctxData);

            serverListener.setConnectListener(new ConnectListener() {
                @Override
                public void connectSuccess() {

                }

                @Override
                public void connectFailure() {

                }

                @Override
                public void disconnect(ChannelHandlerContext ctx) {

                }

                @Override
                public void getUserList(List<CtxData> ctxDataList) {

                }

                @Override
                public void addUserInfo(CtxData ctxData) {

                }

                @Override
                public void removeUserInfo(CtxData ctxData) {

                }
            });

            NettyServer server = new NettyServer(serverListener);

            server.startUp();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("config failed: " + e.getMessage());
        }
    }
}
