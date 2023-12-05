#include <wiringPi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "MQTTClient.h"
#include <softTone.h>
#define ADDRESS     "113.198.86.190:1883"
#define CLIENTID    "extinguish the fire"
#define TOPIC       "SmartHome/miraeb1/ctrflame"
#define PAYLOAD     "Hello World!"
#define QOS         0
#define TIMEOUT     10000L
#define PIEZO 25

int i;
volatile MQTTClient_deliveryToken deliveredtoken;

void delivered(void *context, MQTTClient_deliveryToken dt) {
    printf("Message with token value %d delivery confirmed\n", dt);
    deliveredtoken = dt;
}

int msgarrvd(void *context, char *topicName, int topicLen, MQTTClient_message *message) {
         printf("message arrived!\n");
         //peozo buzzer turn on and than off
                for(i=0;i<1;i++){
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3135);
delay(200);
softToneStop(PIEZO);
//delay(10);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3520);
delay(200);
softToneStop(PIEZO);
delay(200);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 4186);
delay(200);
softToneStop(PIEZO);
delay(200);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 3951);
delay(200);
softToneStop(PIEZO);
delay(200);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3520);
delay(200);
softToneStop(PIEZO);
//delay(10);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3135);
delay(200);
softToneStop(PIEZO);
//delay(10);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3520);
delay(200);
softToneStop(PIEZO);
delay(200);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 2637);
delay(600);
softToneStop(PIEZO);
delay(400);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 3135);
delay(200);
softToneStop(PIEZO);
//delay(10);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3520);
delay(200);
softToneStop(PIEZO);
delay(200);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 4186);
delay(200);
softToneStop(PIEZO);
delay(200);

softToneCreate(PIEZO);
softToneWrite(PIEZO, 3951);
delay(200);
softToneStop(PIEZO);
delay(200);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3520);
delay(200);
softToneStop(PIEZO);
//delay(10);
softToneCreate(PIEZO);
softToneWrite(PIEZO, 3729);
delay(800);
softToneStop(PIEZO);
delay(10);

softToneCreate(PIEZO);
softToneWrite(PIEZO,3520);
delay(200);
softToneStop(PIEZO);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4186);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneCreate(PIEZO);
softToneWrite(PIEZO,5274);
delay(400);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,6271);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,5587);
delay(200);
softToneStop(PIEZO);

softToneCreate(PIEZO);
softToneWrite(PIEZO,3135);
delay(200);
softToneStop(PIEZO);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4186);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,5274);
delay(400);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,5587);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,5274);
delay(200);
softToneStop(PIEZO);
delay(20);


softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(400);
softToneStop(PIEZO);

softToneCreate(PIEZO);
softToneWrite(PIEZO,5274);
delay(200);
softToneStop(PIEZO);

softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneStop(PIEZO);
softToneCreate(PIEZO);
softToneWrite(PIEZO,3729);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4698);
delay(200);
softToneStop(PIEZO);
delay(20);
softToneCreate(PIEZO);
softToneWrite(PIEZO,4186);
delay(800);
softToneStop(PIEZO);
delay(400);

        }
        MQTTClient_freeMessage(&message);
        MQTTClient_free(topicName);

          return 1;
}

 void connlost(void *context, char *cause) {
            printf("\nConnection lost\n");
            printf("cause: %s\n", cause);
}

//volatitle… ~ connlost…까지 비동기 조작을 위해 클라이언트가 사용할 세 가지 콜백 메소드 delivered, $
int main(int argc, char* argv[]) {
    MQTTClient client; // 프로그램에 사용되는 로컬변수
    MQTTClient_connectOptions conn_opts = MQTTClient_connectOptions_initializer; // 프로그램에 사용$
    int rc;
    int ch;
   wiringPiSetup();
 MQTTClient_create(&client, ADDRESS, CLIENTID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
    //&client : 새로 작성한 클라이언트의 핸들을 가리키는 포인터,프로덕션 코드에서 올바르게 완료되었$
    //ADDRESS : 클라이언트 연결 요청을 모니터하는 MQTT 포트의 URI
  //&client : 새로 작성한 클라이언트의 핸들을 가리키는 포인터,프로덕션 코드에서 올바르게 완료되었$
    //ADDRESS : 클라이언트 연결 요청을 모니터하는 MQTT 포트의 URI
    //CLIENTID : 클라이언트를 식별하는데 사용되는 이름
    //MQTTCLIENT_PERSISTENCE_NONE : 클라이언트 상태가 메모리에 있으며 시스템 장애가 발생하는 경우  $
    //MQTTClient_connect 함수를 호출할 때까지 아무런 메시지도 처리되지 않음
    conn_opts.keepAliveInterval = 20;
    conn_opts.cleansession = 1;
    conn_opts.keepAliveInterval = 20;
    conn_opts.cleansession = 1;
    MQTTClient_setCallbacks(client, NULL, connlost, msgarrvd, delivered); //서버에서 클라이언트의  $
    //클라이언트 연결하는 부분
    if ((rc = MQTTClient_connect(client, &conn_opts)) != MQTTCLIENT_SUCCESS) {
        printf("Failed to connect, return code %d\n", rc);
        exit(EXIT_FAILURE);
    }
    printf("Subscribing to topic %s\nfor client %s using QoS%d\n\n"
           "Press Q<Enter> to quit\n\n", TOPIC, CLIENTID, QOS);
    MQTTClient_subscribe(client, TOPIC, QOS);
        printf("waiting for topic....\n");
    do {
        ch = getchar();
        } while(ch!='Q' && ch != 'q');
    MQTTClient_disconnect(client, 10000);
    MQTTClient_destroy(&client);
    return rc;

}


