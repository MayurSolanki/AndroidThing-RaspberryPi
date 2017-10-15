package com.learn.learniotraspberrypi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    EditText etMessage;
    Button btPublishMessage;
    MqttClient client;
    TextView tvMessageReceived;
    Switch switchOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessage =(EditText)findViewById(R.id.et_publish_message);
        btPublishMessage =(Button) findViewById(R.id.bt_publish_message);
        tvMessageReceived =(TextView)findViewById(R.id.tv_message_received);
        switchOnOff=(Switch) findViewById(R.id.switch_on_off);





        connetToMosquittoServer();

        subscribeToMosquittoBrocker();


        btPublishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(client.isConnected()){
                    publishMessage(etMessage.getText().toString().trim());
                }else {
                    connetToMosquittoServer();
                    publishMessage(etMessage.getText().toString().trim());
                    subscribeToMosquittoBrocker();
                }



            }
        });


        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    publishMessage("ON");
                }else {
                    publishMessage("OFF");
                }
            }
        });


    }

    private void subscribeToMosquittoBrocker() {
        String topic = "topic/led";
        try {
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    private void publishMessage(String messgaeInput) {

        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(messgaeInput.getBytes());
            client.publish("topic/led",mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private void connetToMosquittoServer() {

        AppLogger.e("Connecting to Mosquitto");





//        tcp://localhost:1883
//        tcp://192.168.1.102:1883
//        tcp://broker.mqttdashboard.com:1883
// tcp://broker.hivemq.com:1883
//         tcp://0.0.0.0:1883

        try {
            client = new MqttClient("tcp://m10.cloudmqtt.com:17409", MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("hqoqklmz");
            options.setPassword("3YFYnRNNOCTM".toCharArray());
            client.setCallback(this);
            client.connect(options);


            AppLogger.e("Connected Successfully");

        } catch (MqttException e) {
            e.printStackTrace();
            AppLogger.e("MqttException connection getMessage: "+e.getMessage());
            AppLogger.e("MqttException connection getReasonCode: "+e.getReasonCode());
            AppLogger.e("MqttException connection getReasonCode: "+e.getCause());
            AppLogger.e("MqttException connection getStackTrace: "+e.getStackTrace());
        }

        if(client.isConnected()){
            AppLogger.e("Connected Successfully");
        }else {
            AppLogger.e("Connection failed");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLogger.e("onDestroy");



    }

    /**
     * This method is called when the connection to the server is lost.
     *
     * @param cause the reason behind the loss of connection.
     */
    @Override
    public void connectionLost(Throwable cause) {
        AppLogger.e("connectionLost....");

    }

    /**
     * This method is called when a message arrives from the server.
     * <p>
     * <p>
     * This method is invoked synchronously by the MQTT client. An
     * acknowledgment is not sent back to the server until this
     * method returns cleanly.</p>
     * <p>
     * If an implementation of this method throws an <code>Exception</code>, then the
     * client will be shut down.  When the client is next re-connected, any QoS
     * 1 or 2 messages will be redelivered by the server.</p>
     * <p>
     * Any additional messages which arrive while an
     * implementation of this method is running, will build up in memory, and
     * will then back up on the network.</p>
     * <p>
     * If an application needs to persist data, then it
     * should ensure the data is persisted prior to returning from this method, as
     * after returning from this method, the message is considered to have been
     * delivered, and will not be reproducible.</p>
     * <p>
     * It is possible to send a new message within an implementation of this callback
     * (for example, a response to this message), but the implementation must not
     * disconnect the client, as it will be impossible to send an acknowledgment for
     * the message being processed, and a deadlock will occur.</p>
     *
     * @param topic   name of the topic on the message was published to
     * @param message the actual message.
     * @throws Exception if a terminal error has occurred, and the client should be
     *                   shut down.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        final String payload = new String(message.getPayload());

        AppLogger.e("messageArrived..." + payload);

        runOnUiThread(new Runnable(){
            public void run() {
                tvMessageReceived.append(payload+"\n");
            }
        });




    }



    /**
     * Called when delivery for a message has been completed, and all
     * acknowledgments have been received. For QoS 0 messages it is
     * called once the message has been handed to the network for
     * delivery. For QoS 1 it is called when PUBACK is received and
     * for QoS 2 when PUBCOMP is received. The token will be the same
     * token as that returned when the message was published.
     *
     * @param token the delivery token associated with the message.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        AppLogger.e("deliveryComplete..." + token);


    }
}