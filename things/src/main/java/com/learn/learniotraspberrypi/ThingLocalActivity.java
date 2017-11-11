package com.learn.learniotraspberrypi;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class ThingLocalActivity extends Activity implements MqttCallback {


    MqttClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AppLogger.e("onCreate..... MQTT LED  Android Thing");


        connetToMosquittoServer();

    }



    private void connetToMosquittoServer() {

        try {
            //        tcp://localhost:1883
//        tcp://192.168.1.102:1883
//        tcp://broker.mqttdashboard.com:1883
// tcp://broker.hivemq.com:1883
//         tcp://0.0.0.0:1883
            //http://192.168.1.102:1883
            //tcp://localhost:1883


            client = new MqttClient("tcp://192.168.1.104:1883", MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("steve");
            options.setPassword("00123456789abcdef".toCharArray());
            client.setCallback(this);
            client.connect(options);

            String topic = "topic/led";
            client.subscribe(topic);


            AppLogger.e("Connected Successfully");

            //client.publish();

        } catch (MqttException e) {
            e.printStackTrace();

            AppLogger.e("Connection Error : "+e.getMessage());
            AppLogger.e("Connection Error : "+e.getReasonCode());
            AppLogger.e("Connection Error : "+e.getStackTrace());
            AppLogger.e("Connection Error : "+e.getLocalizedMessage());

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppLogger.e("onDestroy ");


    }

    /**
     * This method is called when the connection to the server is lost.
     *
     * @param cause the reason behind the loss of connection.
     */
    @Override
    public void connectionLost(Throwable cause) {
        AppLogger.e("connectionLost.... ");

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
        String payload = new String(message.getPayload());
        AppLogger.e("payload.... "+payload);


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
        AppLogger.e("deliveryComplete....");
    }
}
