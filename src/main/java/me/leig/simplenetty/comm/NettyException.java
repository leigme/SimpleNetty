package me.leig.simplenetty.comm;

import org.apache.log4j.Logger;

public class NettyException extends Exception {

    public NettyException(Class t, String error) {
        Logger.getLogger(t).error(error);
    }
}
