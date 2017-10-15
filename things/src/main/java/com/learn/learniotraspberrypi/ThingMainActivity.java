package com.learn.learniotraspberrypi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

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

public class ThingMainActivity extends Activity implements MqttCallback {


    public static final String LED_PIN = "BCM4";
    private Gpio ledPin;
    MqttClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AppLogger.e("onCreate..... MQTT LED .. AnDROID Thing");


        connetToMosquittoServer();

        initiallyLedOff();





    }

    private void initiallyLedOff() {

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            // Create GPIO connection for LED.
            ledPin = service.openGpio(LED_PIN);
            // Configure as an output.
            ledPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        } catch (IOException e) {
            AppLogger.e("Error on PeripheralIO API : "+e);

        }
    }

    private void connetToMosquittoServer() {


        try {
            client = new MqttClient("tcp://m10.cloudmqtt.com:17409", MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("hqoqklmz");
            options.setPassword("3YFYnRNNOCTM".toCharArray());
            client.setCallback(this);
            client.connect(options);


            String topic = "topic/led";
            client.subscribe(topic);

            AppLogger.e("Connected Successfully");

            //client.publish();

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppLogger.e("onDestroy ");

        if (ledPin != null) {
            try {
                ledPin.close();
            } catch (IOException e) {
                AppLogger.e("Error on PeripheralIO API "+e);

            }
        }
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
        switch (payload) {
            case "ON":
                AppLogger.e("LED Status.... "+"LED ON");
                ledPin.setValue(true);

                break;
            case "OFF":
                AppLogger.e("LED Status.... "+"LED OFF");
                ledPin.setValue(false);
                break;
            default:
                AppLogger.e("Message not supported!");
                break;
        }

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
