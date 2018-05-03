package me.leig.simplenetty.tool;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 枚举法单例模式
 *
 * @author leig
 *
 */

public enum ConfigParser implements ConfigParserIF {

    INSTANCE;

    ConfigParser() {}

    private final static Logger log = Logger.getLogger(ConfigParser.class);

    @Override
    public Config parser() {
        // 批量导入Json文件地址
        String jsonPath = "config.json";

        FileReader fr = null;

        try {

            fr = new FileReader(new File(jsonPath));

            StringBuilder sb = new StringBuilder();

            //读写数据
            int ch;

            while(-1 != (ch=fr.read())){
                sb.append((char)ch);
            }

            Gson gson = new Gson();

            Config config = gson.fromJson(sb.toString(), Config.class);

            log.info("parse() success: " + config.version);

            return config;

        } catch (IOException eio) {
            eio.printStackTrace();
            log.error("parse() failed: " + eio.getMessage());
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("parse() failed: " + e.getMessage());
            }
        }
        return null;
    }
}
