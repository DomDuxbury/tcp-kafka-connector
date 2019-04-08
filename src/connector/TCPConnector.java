package connector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TCPConnector {

    public static void main(String[] args) {
        String topic = "test";
        ServerSocket tcpSocket = startTCPClient(6789);
        Producer producer = createProducer();
        while (true) {
            eventLoop(producer, tcpSocket, topic);
        }
    }

    static private void eventLoop(Producer producer, ServerSocket socket, String topic) {
        try {
            String xml = readLine(socket);
            producer.send(new ProducerRecord<String, String>(topic, xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private ServerSocket startTCPClient(int port) {
        try {
            return new ServerSocket(port);
        } catch (Exception e){
            System.out.println("Could not open server on port: " + port);
            return null;
        }
    }

    static private String readLine(ServerSocket socket) throws Exception {
        Socket connectionSocket = socket.accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        return inFromClient.readLine();
    }

    public static Producer<String, String> createProducer() {
        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "localhost:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<String, String>(props);
    }
}
