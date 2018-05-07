package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

/**
 * 服务器消息处理类
 *
 * Created by i on 2017/10/12.
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private final static Logger log = Logger.getLogger(NettyServerHandler.class);

    private ServerListener mServerListener;

    /**
     * 构建服务器对象
     *
     * @param serverListener
     */
    public NettyServerHandler(ServerListener serverListener) {
        this.mServerListener = serverListener;
    }

    /**
     * 初始化通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (null != mServerListener.getConnectListener()) {
            mServerListener.getConnectListener().connectSuccess();
        }
        log.info("与客户端建立通道");
    }

    /**
     * 断开通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        mServerListener.disconnect(ctx);
        log.info("客户端离线了...");
    }

    /**
     * 处理消息
     *
     * @param channelHandlerContext
     * @param nettyMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage) {
        try {
            if (null != nettyMessage) {
                switch (nettyMessage.getMsgType()) {
                    case Constant.MSG_TYPE_FIRST:
                        CtxData ctxData = new CtxData();
                        ctxData.setUserId(nettyMessage.getSenderId());
                        String data = new String(nettyMessage.getData());
                        String[] msgs = data.split(Constant.SEG);
                        ctxData.setUserName(msgs[0]);
                        ctxData.setLocalIP(msgs[1]);
                        ctxData.setTime(msgs[2]);
                        ctxData.setRemark(msgs[3]);
                        ctxData.setCtx(channelHandlerContext);
                        mServerListener.saveCtxData(ctxData);
                        break;
                    case Constant.MSG_TYPE_HEARTBEAT:
                        NettyMessage heartbeatMessage = new NettyMessage();
                        heartbeatMessage.setMsgType(Constant.MSG_TYPE_HEARTBEAT);
                        heartbeatMessage.setData("@@".getBytes());
                        channelHandlerContext.channel().writeAndFlush(heartbeatMessage);
                        break;
                    case Constant.MSG_TYPE_USERLIST:
                        mServerListener.getUserList(Integer.parseInt(nettyMessage.getSenderId()));
                        break;
                    case Constant.MSG_TYPE_TEXT:
                        String receiverId = nettyMessage.getReceiverId();
                        if (null == receiverId || "".equals(receiverId)) {
                            int userId = Integer.parseInt(nettyMessage.getSenderId());
                            mServerListener.receiveMessage(userId, new String(nettyMessage.getData()));
                        } else {
                            int senderId = Integer.parseInt(nettyMessage.getSenderId());
                            int rId = Integer.parseInt(receiverId);
                            mServerListener.clientToClient(senderId, rId, new String(nettyMessage.getData()));
                        }
                        break;
                    default:
                        log.info("channelRead0() type is default [" + nettyMessage.getSenderId() + "说了: " + new String(nettyMessage.getData()) + "]");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("channelRead0() failed: " + e.getMessage());
        }
    }

    /**
     * 捕获异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (null != mServerListener.getConnectListener()) {
            mServerListener.getConnectListener().connectFailure();
        }
        log.error(cause.getMessage());
    }
}
