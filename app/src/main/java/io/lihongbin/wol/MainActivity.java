package io.lihongbin.wol;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private final Logger logger = Logger.getLogger(MainActivity.class.getName());

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // ipconfig /all
    private final static String ip = "192.168.3.105";
    private final static String mac = "3C-7C-3F-0F-54-1D".replace("-", "");
    private final static byte[] sendPacket = new byte[6 + mac.length() * 16];
    static {
        int i;
        for (i = 0; i < 6; i++) {
            sendPacket[i] = -1;
        }
        for (int j = 0; j < 16; j++) {
            for (int k = 0; k < mac.length(); k+=2, i++) {
                sendPacket[i] = Byte.valueOf(mac.substring(k, k + 2), 16);
            }
        }
    }

    public void startUp(View v) {
        logger.info("start up");
        executor.execute(() -> {
            try {
                logger.info("start send");
                DatagramSocket datagramSocket = new DatagramSocket();
                DatagramPacket datagramPacket = new DatagramPacket(sendPacket, 0, sendPacket.length, InetAddress.getByName(ip), 9);
                datagramSocket.send(datagramPacket);
                logger.info("start send success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}