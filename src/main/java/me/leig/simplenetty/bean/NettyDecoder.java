package me.leig.simplenetty.bean;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.leig.simplenetty.comm.NettyUtil;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger log = Logger.getLogger(NettyDecoder.class);
	
    private static final int FRAME_MAX_LENGTH = 16777216;

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 0);
    }
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    	ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();
            byte[] outData = new byte[byteBuffer.remaining()];
            byteBuffer.get(outData, 0, outData.length);

            NettyMessage nettyMessage = new NettyMessage();
            nettyMessage.decodeMsg(outData);
            
            return nettyMessage;
        } catch (Exception e) {
        	e.printStackTrace();
            log.error("decode exception, " + NettyUtil
                    .parseChannelRemoteAddr
                    (ctx.channel()), e);
            NettyUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }

}
