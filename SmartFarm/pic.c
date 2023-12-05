#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "MQTTClient.h"
#define ADDRESS     "113.198.86.190:1883"
#define CLIENTID    "TakingPicture"
#define TOPIC       "SmartFarm/miraeb1/IMG"
#define PAYLOAD     "Hello World!"
#define QOS         0
#define TIMEOUT     10000L
 
volatile MQTTClient_deliveryToken deliveredtoken;
 
void delivered(void *context, MQTTClient_deliveryToken dt) {
    printf("Message with token value %d delivery confirmed\n", dt);
    deliveredtoken = dt;
}
 
int msgarrvd(void *context, char *topicName, int topicLen, MQTTClient_message *message) {
    MQTTClient_freeMessage(&message);
    MQTTClient_free(topicName);

printf("message arrived!");
    
    //메시지가 도착하였을 때 사진을 찍은 후 지정한 폴더에 저장한다.
//    pid_t fk = fork();
 //   if(!fk) {

   //     execl("/home/pi/Pictures/raspistill", "raspistill", "-o" ,"image.jpg");
   // }

    system("raspistill --nopreview -w 390 -h 170 -o image.jpg");
    printf("taking picture\n");
    return 1;
              
}
              
void connlost(void *context, char *cause) {
            printf("\nConnection lost\n");
            printf("cause: %s\n", cause);
}
//volatitle… ~ connlost…까지 비동기 조작을 위해 클라이언트가 사용할 세 가지 콜백 메소드 delivered, msgarrvd 및 connlost를 구현한 부분.
 
int main(int argc, char* argv[]) {
    MQTTClient client; // 프로그램에 사용되는 로컬변수
    MQTTClient_connectOptions conn_opts = MQTTClient_connectOptions_initializer; // 프로그램에 사용되는 로컬변수
    int rc;
    int ch;
    MQTTClient_create(&client, ADDRESS, CLIENTID, MQTTCLIENT_PERSISTENCE_NONE, NULL);
    //&client : 새로 작성한 클라이언트의 핸들을 가리키는 포인터,프로덕션 코드에서 올바르게 완료되었는지 테스트할 수 있는 오류 코드
    //ADDRESS : 클라이언트 연결 요청을 모니터하는 MQTT 포트의 URI
    //CLIENTID : 클라이언트를 식별하는데 사용되는 이름
    //MQTTCLIENT_PERSISTENCE_NONE : 클라이언트 상태가 메모리에 있으며 시스템 장애가 발생하는 경우 손실됨을 지정
    //MQTTClient_connect 함수를 호출할 때까지 아무런 메시지도 처리되지 않음
            
    conn_opts.keepAliveInterval = 20;
    conn_opts.cleansession = 1;
    conn_opts.keepAliveInterval = 20;
    conn_opts.cleansession = 1;
            
    MQTTClient_setCallbacks(client, NULL, connlost, msgarrvd, delivered); //서버에서 클라이언트의 연결이 끊어진 동안에 호출
            
    //클라이언트 연결하는 부분
    if ((rc = MQTTClient_connect(client, &conn_opts)) != MQTTCLIENT_SUCCESS) {
        printf("Failed to connect, return code %d\n", rc);
        exit(EXIT_FAILURE);
    }
    printf("Subscribing to topic %s\nfor client %s using QoS%d\n\n"
           "Press Q<Enter> to quit\n\n", TOPIC, CLIENTID, QOS);
            
    MQTTClient_subscribe(client, TOPIC, QOS);

	printf("waiting for topic....");
    do {
        ch = getchar();
        } while(ch!='Q' && ch != 'q');
    MQTTClient_disconnect(client, 10000);
    MQTTClient_destroy(&client);
    return rc;
}

