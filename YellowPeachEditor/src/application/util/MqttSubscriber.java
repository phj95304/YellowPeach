package application.util;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import application.view.DatabaseOptionDialogController;


public class MqttSubscriber {
	private String host = "tcp://113.198.85.4:1883";
	private String clientId = "DataSaver";
	private String payloadContent;
	private String time;
	private String subIoTSystemTopic;

	private DatabaseOptionDialogController dodc;
	private MqttClient mqttClient;
	public MqttSubscriber(DatabaseOptionDialogController dodc) {
		this.dodc = dodc; 
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("\nReceived a Message!" + "\n\tTime:    " + time + "\n\tTopic:   " + topic);
		
		// paylaod에 저장된 값을 string 변수에 저장한다.
		payloadContent = new String(message.getPayload());
		System.out.println("\n\tMessage: " + payloadContent + "\n\tQoS:     " + message.getQos() + "\n");
		// DatabaseOptionController의 TopicSet에 payload로 넘어온 토픽들을 담는다.
		dodc.setTopicSet(payloadContent);
		
	}
	
	public void run() {
		System.out.println("TopicSubscriber initializing...");

		// sub할 토픽
		String subTopic = "discovery/"+ subIoTSystemTopic; 

		try {
			// Mqtt클라이언트 생성
			mqttClient = new MqttClient(host, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			// 브로커와 클라이언트 연결
			mqttClient.connect(connOpts);
			System.out.println("Connected");

			// Latch used for synchronizing b/w threads
			//final CountDownLatch latch = new CountDownLatch(1);

			mqttClient.subscribe(subTopic, 0);

			// Callback - Anonymous inner-class for receiving messages
			mqttClient.setCallback(new MqttCallback() {
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					System.out.println("\nReceived a Message!" + "\n\tTime:    " + time + "\n\tTopic:   " + topic);
					// paylaod에 저장된 값을 string 변수에 저장한다.
					payloadContent = new String(message.getPayload());
					System.out.println("\n\tMessage: " + payloadContent + "\n\tQoS:     " + message.getQos() + "\n");
					dodc.setTopicSet(payloadContent);
					dodc.setFlag(false);
					//mqttClient.disconnect();
					//System.out.println("disconnect\n");
				}

				public void connectionLost(Throwable cause) {
					System.out.println("Connection to Solace messaging lost!" + cause.getMessage());
					dodc.setFlag(true);
				}

				public void deliveryComplete(IMqttDeliveryToken token) {
					dodc.setFlag(true);
					try {
						mqttClient.disconnect();
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			dodc.setFlag(true);
			me.printStackTrace();
		}
		dodc.setFlag(true);
	}

	public String getTopicSet() {
		return payloadContent;
	}
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSubIoTSystemTopic() {
		return subIoTSystemTopic;
	}

	public void setSubIoTSystemTopic(String subIoTSystemTopic) {
		this.subIoTSystemTopic = subIoTSystemTopic;
	}

	public MqttClient getMqttClient() {
		return mqttClient;
	}

	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	
}
