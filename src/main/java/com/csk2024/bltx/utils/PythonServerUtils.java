package com.csk2024.bltx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PythonServerUtils {
    public static String predict(String url){
        try {
            //python服务所在主机的ip
            String ip = "127.0.0.1";
            //python服务所在主机监听端口
            int port = 50007;
            Socket client = new Socket(ip, port);
            OutputStreamWriter os = new OutputStreamWriter(client.getOutputStream());
            os.write(url);
            os.flush();

            InputStream is = client.getInputStream();
            byte[] buf = new byte[1024 * 8];
            StringBuilder msg = new StringBuilder();
            for (int len = is.read(buf); len > 0; len = is.read(buf)) {
                msg.append(new String(buf, 0, len));
            }
            client.shutdownInput();
            System.out.println("正在接收回复信息...");
            System.out.println("服务器返回的信息: " + msg);
            System.out.println("接收回复信息完成");
            return msg.toString();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
