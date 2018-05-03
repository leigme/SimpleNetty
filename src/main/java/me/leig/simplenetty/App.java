package me.leig.simplenetty;

import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.handler.ContentHandler;
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

            ctxData.setPort(config.port);

            NettyServer server = new NettyServer(new Server(ctxData, new ServerHandler()));

            server.startUp();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("config failed: " + e.getMessage());
        }
    }

    static class Server extends ServerListener {

        public Server(CtxData ctxData, ContentHandler contentHandler) {
            super(ctxData, contentHandler);
        }
    }

    static class ServerHandler implements ContentHandler {

        @Override
        public void obtainUsers(List<String> userIds) {

        }

        @Override
        public void receiveContent(String content) {

        }

        @Override
        public void endConnect(String msg) {

        }

        @Override
        public void addUser(String userId) {

        }

        @Override
        public void replay(String userId) {

        }
    }
}
