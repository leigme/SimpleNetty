package me.leig.simplenetty.bean;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.leig.simplenetty.comm.NettyUtil;
import org.apache.log4j.Logger;

public class NettyEncoder extends MessageToByteEncoder<NettyMessage> {

	private final static Logger log = Logger.getLogger(NettyEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out) throws Exception {
			
		try {
			
			byte[] outData = msg.encodeCmd();

			out.writeBytes(outData);

        } catch (Exception e) {
        	e.printStackTrace();
			log.error("encode exception, " + NettyUtil
					.parseChannelRemoteAddr
					(ctx.channel()), e);
            if (null != msg) {
				log.error(msg.toString());
            }
            NettyUtil.closeChannel(ctx.channel());
        }
	}
}
