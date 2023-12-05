#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>//라이브러리 함수 실행 중 에러가 발생하면 어떠한 에러가 발생했는지 체크하고자 확인하는 용도로 사용. 0:정상 이외:비정상
#include <wiringPi.h>//GPIO사용을 위한 c언어 라이브러리
#include <wiringPiSPI.h>//spi통신을 위한 라이브러리, 
                         //A/D Converters 인 MCP3008의 SPI 기능을 이용하기 위함.
#include "MQTTClient.h"
 
#define CS_MCP3008 10 //BCM_GPIO 8
#define SPI_CHANNEL 0
#define SPI_SPEED 1000000 // 1MHz
#define ADDRESS "113.198.86.190:1883"
#define CLIENTID "GREEN"
#define PAYLOAD "Hello World"
#define TOPIC "GREEN"
 
#define TOPIC1 "SmartFarm/miraeb1/temp"
#define TOPIC2 "SmartFarm/miraeb1/soilhumid"
#define TOPIC3 "SmartFarm/miraeb1/cds"
 
#define QOS 1
#define TIMEOUT 1000L
 
//bcm확인 틀리면 안됨 ㅠㅁㅠ
#define LED1 0 //BCM 17
#define LED2 2 //BCM 27
#define LED3 3 //BCM 22
#define LED4 21 //BCM 5
#define LED5 22 //BCM 6
#define LED6 23 //BCM 13
#define LED7 24 //BCM 19
#define LED8 25 //BCM 26
 
#define LED9 1 //BCM 18
#define LED10 4 //BCM 23
#define LED11 5 //BCM 24
#define LED12 6 //BCM 25
#define LED13 26//BCM 12
#define LED14 27//BCM 16
#define LED15 7 //BCM 4
 
//pump
#define IN1 28//BCM 20
#define IN2 29 //BCM 21
 
float tempo = 0;
float humi = 0;
float light = 0;
 
void sendMsg(MQTTClient *, char *, char *);
 
int read_mcp3008_adc(unsigned char adcChannel)
{
        unsigned char buff[3];
        int adcValue = 0;
        buff[0] = 0x06 | ((adcChannel & 0x07) >> 2);
        buff[1] = ((adcChannel & 0x07) << 6);
        buff[2] = 0x00;
        digitalWrite(CS_MCP3008, 0); // Low : CS Active
        wiringPiSPIDataRW(SPI_CHANNEL, buff, 3);
        buff[1] = 0x0F & buff[1];
        adcValue = (buff[1] << 8) | buff[2];
        digitalWrite(CS_MCP3008, 1); // High : CS Inactive
        return adcValue;
}
 
 
void mainLoop(MQTTClient *pClient) {
        char buf[10];
 
        int adcChannel = 0;
        int adcValue = 0;
 
        float tempoVolt = 0;
        float humiVolt = 0;
        float lightVolt = 0;
 
 
 
        while (1) {
                 adcChannel = 2;//0번:수위,1번:습도, 2번:온도,3번:조도
                 adcValue = read_mcp3008_adc(adcChannel);
                 printf("MCP3008 IC Ch1 Value = %u\n", adcValue);
                 tempoVolt = adcValue;
                 tempo = (tempoVolt / 4078) + 23;//0v ~ 3.3v 의 센서 출력 값을 0 ~ 4095 (12bit) 의 디지털 값으로 변환해 주는 것이므로 적절하게 변환한다.
                 printf("TempVolt : %f", tempoVolt);
                 printf("Temperature : %f\n\n ", tempo);
                 printf("\n\n------------------------\n\n");
 
                 sprintf(buf, "%.2f", tempo);
                 sendMsg(pClient, TOPIC1, buf);
 
                 delay(1000);
 
                 if ((tempo>19) || (humi > -15)) {
                         digitalWrite(IN1, 1);
                         digitalWrite(IN2, 0);
                         delay(100);
                 }
                 else if (tempo <= 19 && humi <= 78) {
                         digitalWrite(IN1, 0);
                         digitalWrite(IN2, 0);
                         delay(100);
                 }
                 delay(1000);
 
                 adcChannel = 1;//0번:수위,1번:습도, 2번:온도,3번:조도
                 adcValue = read_mcp3008_adc(adcChannel);
 
                 humiVolt = (3.25 / 4095)*adcValue;
                 humi = (humiVolt / (3.25 / 100)) - 15;
                 printf("\nMCP3008 IC Ch2 Value = %d\n", adcValue);
                 printf("HumiVolt : %f", humiVolt);
                 printf("Humidity : %f \n", humi);
                 printf("\n\n------------------------\n\n");
 
                 sprintf(buf, "%.2f", humi);
                 sendMsg(pClient, TOPIC2, buf);
 
                 delay(1000);
 
 
                 adcChannel = 3;//0번:수위,1번:습도, 2번:온도,3번:조도
                 adcValue = read_mcp3008_adc(adcChannel);
 
                 lightVolt = (3.25 / 4095)*adcValue;
                 light = (lightVolt / (3.25 / 100))+90;
                 printf("\nMCP3008 IC Ch3 Value = %d\n", adcValue);
                 printf("LuxVolt : %f", lightVolt);
                 printf("Lux   :%f ", light);
                 printf("\n\n------------------------\n\n");
 
                 sprintf(buf, "%.2f", light);
                 sendMsg(pClient, TOPIC3, buf);
 
                 if (light>5) {//
                         printf("LED ON");
                         digitalWrite(LED1, 1);
                         digitalWrite(LED2, 1);
                         digitalWrite(LED3, 1);
                         digitalWrite(LED4, 1);
                         digitalWrite(LED5, 1);
                         digitalWrite(LED6, 1);
                         digitalWrite(LED7, 1);
                         digitalWrite(LED8, 1);
                         digitalWrite(LED9, 1);
                         digitalWrite(LED10, 1);
                         digitalWrite(LED11, 1);
                         digitalWrite(LED12, 1);
                         digitalWrite(LED13, 1);
                         digitalWrite(LED14, 1);
                         digitalWrite(LED15, 1);
                 }
                 else if (light <= 5) {
                         printf("LED OFF");
                         digitalWrite(LED1, 0);
                         digitalWrite(LED2, 0);
                         digitalWrite(LED3, 0);
                         digitalWrite(LED4, 0);
                         digitalWrite(LED5, 0);
                         digitalWrite(LED6, 0);
                         digitalWrite(LED7, 0);
                         digitalWrite(LED8, 0);
                         digitalWrite(LED9, 0);
                         digitalWrite(LED10, 0);
                         digitalWrite(LED11, 0);
                         digitalWrite(LED12, 0);
                         digitalWrite(LED13, 0);
                         digitalWrite(LED14, 0);
                         digitalWrite(LED15, 0);
                 }
                 delay(1000);
        }
}
 
void sendMsg(MQTTClient *pClient, char *pTopic, char* msg) {//publish
        MQTTClient_message pubmsg = MQTTClient_message_initializer;
        MQTTClient_deliveryToken token;// 클라이언트가 생성 될 때 생기는 것으로 전송이 잘되었는지 확인, 전송이 완료 되기 전까지 클라이언트를 차단한다.
        pubmsg.payload = msg;//buf안의 내용
        pubmsg.payloadlen = strlen(msg);//buff길이
        pubmsg.qos = QOS;//메시지 보증. 전송되는 메시지는 QOS(Quality of Service)레벨에 따라 결정 된다.
                                          //QOS = 0 메시지가 서버로 전송 되거나 안될 수 있음(응답 확인 불가)
                                          //QOS = 1 서버에 메시지가 도착하였는지 확인가능(pushbach message),
//만약 응답을 받지 못하면 클라이언트가 다시 메시지를 보내어 확인(DUP 헤더)
//서버가 중복된 메시지를 받을 경우 서버가 subscriber에 한번더 메시지를 pub하고 다른 pushback메시지를 클라이언트한테 전송
//QOS = 2  가장 높은 단계의 메시지 보증. 문제가 발생한 경우 가장 마지막에 공인되지 않은 메시지 프로토콜부터 재실행,
                                                  //메시지는 전송 및 수신은 1번만 전송됨(중복 되지 않음)
       
        pubmsg.retained = 0;
        MQTTClient_publishMessage(*pClient, pTopic, &pubmsg, &token);
}
 
 
int initSpi() {
        if (wiringPiSetup() == -1)
        {
                 fprintf(stdout, "Unable to start wiringPi: %s\n", strerror(errno));
                 return 1;
        }
        if (wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1)
        {
                 fprintf(stdout, "wiringPiSPISetup Failed: %s\n", strerror(errno));
                 return 1;
        }
        pinMode(CS_MCP3008, OUTPUT);
}
 
int main(int argc, char * argv[])
{
        if (wiringPiSetup() == -1) return 1;//오류 체크
 
        //pump output setting
        pinMode(IN1, OUTPUT);
        pinMode(IN2, OUTPUT);
 
        pinMode(IN1, OUTPUT);
        pinMode(IN2, OUTPUT);
 
        //led output setting
        pinMode(LED1, OUTPUT);
        pinMode(LED2, OUTPUT);
        pinMode(LED3, OUTPUT);
        pinMode(LED4, OUTPUT);
        pinMode(LED5, OUTPUT);
        pinMode(LED6, OUTPUT);
        pinMode(LED7, OUTPUT);
        pinMode(LED8, OUTPUT);
        pinMode(LED9, OUTPUT);
        pinMode(LED10, OUTPUT);
        pinMode(LED11, OUTPUT);
        pinMode(LED12, OUTPUT);
        pinMode(LED13, OUTPUT);
        pinMode(LED14, OUTPUT);
        pinMode(LED15, OUTPUT);
 
        MQTTClient client;
        MQTTClient_connectOptions conn_opts = MQTTClient_connectOptions_initializer;
        int rc;
        char *serverAddress = NULL;
 
        if (argc != 2) {
                 serverAddress = ADDRESS;//입력된 값 2개 아닐때는 113.198.84.26:1883
        }
        else {
                 serverAddress = argv[1];//입력된 값 2개일 때 2번째 argv[1] = 주소
        }
 
        MQTTClient_create(&client, serverAddress, CLIENTID,//ClientID => GREEN
                 MQTTCLIENT_PERSISTENCE_NONE, NULL);//mqtt클라이언트 생성
        conn_opts.keepAliveInterval = 20;
        conn_opts.cleansession = 1;
 
        if ((rc = MQTTClient_connect(client, &conn_opts)) != MQTTCLIENT_SUCCESS)
        {
                 printf("Failed to connect, return code %d\n", rc);
                 exit(EXIT_FAILURE);
        }
 
        initSpi();
        mainLoop(&client);
 
        MQTTClient_disconnect(client, 10000);
        MQTTClient_destroy(&client);//종료
        return 0;
} 

