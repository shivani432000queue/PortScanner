package portScanner;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScanner {

    public static void main(String[] args) {

        List openPorts = portScan("192.168.56.1");
//        openPorts.forEach(port -> System.out.println("Port " + port + " is open!"));

    }

    public static List portScan(String ip) {

        ConcurrentLinkedQueue openPorts = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        AtomicInteger port = new AtomicInteger(0);

        while (port.get() < 65535) {

            final int CurrentPort = port.getAndIncrement();
            executorService.submit(() -> {

                        try {

                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(ip, CurrentPort), 200);
                            socket.close();
                            openPorts.add(CurrentPort);
                            System.out.println(ip + " port " + CurrentPort + " is open!");


                        } catch (IOException e) {
                        }
                    }
            );
        }
            try {

                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            List openPortList = new ArrayList<>();
            System.out.println("Open Ports Queue: " + openPorts.size());
            while (!openPorts.isEmpty()) {
                openPortList.add(openPorts.poll());
            }

            return openPortList;
        }


}
