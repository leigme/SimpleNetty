package me.leig.simplenetty;

import static org.junit.Assert.assertTrue;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.handler.ClientListener;
import me.leig.simplenetty.handler.ConnectListener;
import me.leig.simplenetty.netty.NettyClient;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception {
        assertTrue( true );

        ClientListener clientListener = ClientListener.INSTANCE;

        CtxData ctxData = new CtxData();
        ctxData.setUserId("1729186");
        ctxData.setUserName("王磊家长");
        ctxData.setLocalIP("192.168.0.137");
        ctxData.setRemark("备注哦");
        clientListener.setCtxData(ctxData);

        clientListener.setConnectListener(new ConnectListener() {
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
                for (CtxData cd: ctxDataList) {
                    System.out.print(cd.getUserName());
                }
            }

            @Override
            public void addUserInfo(CtxData ctxData) {
                System.out.print(ctxData.getUserName());
            }

            @Override
            public void removeUserInfo(CtxData ctxData) {
                System.out.print(ctxData.getUserName());
            }
        });

        NettyClient nettyClient = new NettyClient("192.168.0.155", 8099, clientListener);

        nettyClient.startConnect();

    }
}
