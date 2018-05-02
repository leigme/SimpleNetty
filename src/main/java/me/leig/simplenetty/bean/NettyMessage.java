package me.leig.simplenetty.bean;

import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NettyMessage {

	private final static Logger log = Logger.getLogger(NettyMessage.class);

	// 消息版本版本
	private int version;
	// 消息类型类型
	private int msgType;
	// 消息发送者Id
	private String senderId = "";
	// 消息接收者Id
	private String receiverId = "";
	// 消息执行结果
	private String resStatus = Constant.RES_OK;
	// 消息执行结果 消息 如果没有错误，就是正常的空字符串 如果有错误就错误信息
	private String resMsg = "";
	// 消息数据
	private byte[] data = new byte[0];

	public NettyMessage() {
		super();
	}

	public NettyMessage(String senderId, int msgType, byte[] data) {
		this.senderId = senderId;
		this.setMsgType(msgType);
		if (null == data) {
			this.data = new byte[0];
		} else {
			this.data = data;
		}
	}

	public NettyMessage(String senderId, String receiverId, int msgType, byte[]
			data) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msgType = msgType;
		if (null == data) {
			this.data = new byte[0];
		} else {
			this.data = data;
		}
	}

	/**
	 * 编码协议
	 *
	 * @return
	 */
	public byte[] encodeCmd() {

		try{
			// 命令数据长度
			int dataLength = 0;

			// 获得命令参数长度
			if (null != data && data.length > 0) {
				dataLength = data.length;
			}

			// 整个命令的长度
			int totleCmdLength =
					Constant.INT_SIZE+
					Constant.INT_SIZE+
					Constant.INT_SIZE+
					this.senderId.getBytes().length+
					Constant.INT_SIZE+
					this.receiverId.getBytes().length+
					Constant.INT_SIZE+
					this.resStatus.getBytes().length+
					Constant.INT_SIZE+
					this.resMsg.getBytes().length+
					Constant.INT_SIZE+
					dataLength;

			ByteBuffer msgBuffer = ByteBuffer.allocateDirect(Constant.INT_SIZE+totleCmdLength);

			 // 整个命令长度 4
			msgBuffer.putInt(totleCmdLength);
			 // 版本 4
			msgBuffer.putInt(this.version);
			 // 命令类型 4
			msgBuffer.putInt(this.msgType);

			 // 命令Id
			msgBuffer.putInt(this.senderId.getBytes().length);
			msgBuffer.put(this.senderId.getBytes());

			 // 命令编码  C0101001 8
			 // CT00001001 10
			 msgBuffer.putInt(this.receiverId.getBytes().length);
			 msgBuffer.put(this.receiverId.getBytes());

			 // 命令执行结果
			 msgBuffer.putInt(this.resStatus.getBytes().length);
			 msgBuffer.put(this.resStatus.getBytes());

			 // 命令执行结果消息
			 msgBuffer.putInt(this.resMsg.getBytes().length);
			 msgBuffer.put(this.resMsg.getBytes());

			 // 命令数据长度 4
			 msgBuffer.putInt(dataLength);

			 if (null != data && data.length > 0) {
				 // 命令数据
				 msgBuffer.put(data,0,data.length);
			 }
			 msgBuffer.flip();

			 byte[] outData = new byte[msgBuffer.remaining()];
			 msgBuffer.get(outData, 0, outData.length);

			 return outData;
		} catch (Exception ex) {
			log.error("encodeCmd 错误: " + this.senderId);
			log.error("encodeCmd 错误: " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	 }



	/**
	 * 解码协议
	 *
	 * @return
	 */
	public NettyMessage decodeMsg(byte[] datas) {

		 ByteBuffer msgBuffer = ByteBuffer.wrap(datas);

		 // 整个命令长度 4
		 msgBuffer.getInt();

		 // 版本
		 int _ver = msgBuffer.getInt();
		 this.version = _ver;

		 // 命令类型 4
		 int _msgType = msgBuffer.getInt();
		 this.msgType = _msgType;

		 // 命令Id
		 int senderIdLength = msgBuffer.getInt();
		 byte[] _bCmdId = new byte[senderIdLength];
		 for (int index = 0; index < senderIdLength; index++) {
			 _bCmdId[index] = msgBuffer.get();
		 }
		 String _senderId = new String(_bCmdId);
		 this.senderId = _senderId;

		// 消息接收人Id
		int receiverIdLength = msgBuffer.getInt();
		byte[] _breceiverId = new byte[receiverIdLength];
		for (int index = 0; index < receiverIdLength; index++) {
			_breceiverId[index] = msgBuffer.get();
		}
		String _receiverId = new String(_breceiverId);
		this.receiverId = _receiverId;

		 // 命令执行结果
		 int cmdResLength = msgBuffer.getInt();
		 byte[] _bCmdRes = new byte[cmdResLength];
		 for (int index = 0; index < cmdResLength; index++) {
			 _bCmdRes[index] = msgBuffer.get();
		 }
		 String _cmdRes = new String(_bCmdRes);
		 this.resStatus = _cmdRes;

		 // 命令执行结果消息
		 int cmdResMsgLength = msgBuffer.getInt();
		 byte[] _bCmdResMsg = new byte[cmdResMsgLength];
		 for (int index = 0; index < cmdResMsgLength; index++) {
			 _bCmdResMsg[index] = msgBuffer.get();
		 }
		 String _cmdResMsg = new String(_bCmdResMsg);
		 this.resMsg = _cmdResMsg;

		 //  命令数据长度
		 int _dataLength = msgBuffer.getInt();

		 if (_dataLength > 0) {
			this.data = new byte[_dataLength];
			for (int index = 0; index < _dataLength; index++) {
				this.data[index] = msgBuffer.get();
			}
		 }

		 return this;
	 }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getreceiverId() {
		return receiverId;
	}

	public void setreceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getResStatus() {
		return resStatus;
	}

	public void setResStatus(String resStatus) {
		this.resStatus = resStatus;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}



	@Override
	public String toString() {
		return "NettyMessage [version=" + version + ", senderId=" + senderId +
				", msgType=" + msgType + ", receiverId=" + receiverId + ", resStatus=" + resStatus
				+ ", resMsg=" + resMsg + ", data=" + Arrays.toString(data) + "]";
	}

}
