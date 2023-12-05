#include<wiringPi.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<stdint.h>
#include"MQTTClient.h"
#include<softTone.h>

#define MAXTIMINGS 85
#define DHTPIN 0//17
#define LEDPOWER 7 //4
#define FLAMEPOWER 2 //27
#define PIEZO 25//26

#define CS_MCP3008 10 //BCM_GPIO 8
#define SPI_CHANNEL 0
#define SPI_SPEED 1000000 // 1MHz
#define ADDRESS "113.198.86.190:1883"
#define CLIENTID "rasp_6(mirae)"
#define PAYLOAD "Hello World"

#define TOPIC1 "SmartHome/miraeb1/temp"
#define TOPIC2 "SmartHome/miraeb1/humid"
#define TOPIC3 "SmartHome/miraeb1/cds"
#define TOPIC4 "SmartHome/miraeb1/flame"
#define TOPIC5 "SmartHome/miraeb1/dust"

#define QOS 1
#define TIMEOUT 1000L


int dht11_dat[5]={0,0,0,0,0};//temp&humi
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;

float temp = 0;
float humi = 0;
float light = 0;//2
float flame = 0;//3
float dust = 0;//4

void sendMsg(MQTTClient*, char*, char*);

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
void runSpi(MQTTClient *pClient){
 char buf[10];

        int adcChannel = 0;
        int adcValue = 0;
        float lightVolt = 0;
        float flameVolt = 0;
        float dustVolt = 0;
  while (1) {

                char buf[10];
                uint8_t laststate = HIGH;
                uint8_t counter  =0;
                uint8_t j = 0, i;
                float f;
                dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] =0;
                pinMode( DHTPIN, OUTPUT );
                digitalWrite( DHTPIN, LOW );
                delay( 18 );
                digitalWrite( DHTPIN, HIGH );
                delayMicroseconds( 40 );
                pinMode( DHTPIN, INPUT );
                for ( i =0; i < MAXTIMINGS; i++ ){
                        counter =0;
                        while ( digitalRead( DHTPIN ) == laststate ){
                                counter++;
                                delayMicroseconds( 1 );
                                if ( counter ==255 ){
                                        break;
                                }
                        }
 laststate = digitalRead( DHTPIN );
                        if ( counter ==255 )break;
                        if ( (i >=4) && (i % 2==0) ){
                                dht11_dat[j /8] <<=1;
                                if ( counter >16 ) dht11_dat[j /8] |=1;
                         j++;
                        }
                }//end od for

              if ( (j >=30)|| (dht11_dat[4] == ( (dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3]) &0xFF) ) ){
                        f = dht11_dat[2] *9./5.+32;
                        printf( "Humidity = %d.%d %% Temperature = %d.%d C (%.1f F)\n",dht11_dat[0],dht11_dat[1], dht11_dat[2], dht11_dat[3], f );
                        printf("\n\n------------------------\n\n");
 char a[1] = ".";
                        humi  = dht11_dat[0]+dht11_dat[1]*0.01;
                        sprintf(buf, "%.2f", humi);
                        temp = dht11_dat[2]+dht11_dat[3]*0.01;
                        sendMsg(pClient, TOPIC2, buf);
                        sprintf(buf, "%.2f", temp);
                        sendMsg(pClient, TOPIC1, buf);

                }

                else  {

                        printf( "Data not good, skip j=%u \n",j );
                        printf("\n\n------------------------\n\n");

                 }
                delay(1000);

//light
               adcChannel = 2;//light dark-> low
               adcValue = read_mcp3008_adc(adcChannel);
               printf("MCP3008 IC Ch2 Value = %u\n", adcValue);
               lightVolt = 3.3 - adcValue * (3.3/1024.0);
               light = lightVolt ;
               printf("lightVolt : %f", lightVolt);
                light = light+109;

               printf("light : %f\n\n ", light);
               printf("\n\n------------------------\n\n");

               sprintf(buf, "%.2f", light);
               sendMsg(pClient, TOPIC3, buf);

                delay(1000);


//flame
 adcChannel = 3;
               adcValue = read_mcp3008_adc(adcChannel);
               printf("MCP3008 IC Ch3 Value = %u\n", adcValue);
               flameVolt = adcValue;
               flame = flameVolt ;
               printf("flameVolt : %f", flameVolt);
               printf("flame : %f\n\n ", flame);
               printf("\n\n------------------------\n\n");


                if (flame<1000){
                        for(i=0;i<4;i++){
                                softToneCreate(PIEZO);
                                softToneWrite(PIEZO,2637);
                                delay(500);
                                softToneStop(PIEZO);
                                delay(50);
                                softToneCreate(PIEZO);
                                softToneWrite(PIEZO,1975);
                                delay(500);
                                softToneStop(PIEZO);
                                delay(50);
                        }
                }else softToneStop(PIEZO);


                sprintf(buf, "%.2f", flame);
               sendMsg(pClient, TOPIC4, buf);

                delay(1000);
//dust
                pinMode(LEDPOWER,0);
                delayMicroseconds(samplingTime);
                adcChannel = 4;
                adcValue = read_mcp3008_adc(adcChannel);
                printf("MCP3008 IC Ch4 adcValue = %u\n", adcValue);
  delayMicroseconds(deltaTime);
                pinMode(LEDPOWER, 1);
                dustVolt = adcValue * (3.3/1024.0);

                dust = 0.17*dustVolt +10;

                printf("dustVolt : %f", dustVolt);
                printf("Dust : %f\n\n ", dust);
                printf("\n\n------------------------\n\n");

                sprintf(buf, "%.2f", dust);
                sendMsg(pClient, TOPIC5, buf);
                delay(1000);

        }

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

void sendMsg(MQTTClient *pClient, char *pTopic, char* msg) {//publish
        MQTTClient_message pubmsg = MQTTClient_message_initializer;
        MQTTClient_deliveryToken token;// 클라이언트가 생성 될 때 생기는 것으로 전송이 잘되었는지 확인, 전송이 완료 되기 전까지 클라이언트를 차단한다.
        pubmsg.payload = msg;//buf안의 내용
        pubmsg.payloadlen = strlen(msg);//buff길이
                                                       pubmsg.payload = msg;//buf안의 내용
        pubmsg.payloadlen = strlen(msg);//buff길이
        pubmsg.qos = QOS;//
        pubmsg.retained = 0;
        MQTTClient_publishMessage(*pClient, pTopic, &pubmsg, &token);
}
int main(int argc, char * argv[])
{
        if (wiringPiSetup() == -1) {
                printf("wiringPi setup failed");
                return 1;
        }

        pinMode(FLAMEPOWER, INPUT);
//      softToneCreate(PIEZO);
        MQTTClient client;
        MQTTClient_connectOptions conn_opts = MQTTClient_connectOptions_initializer;

        int rc;
        char *serverAddress = NULL;


        if (argc != 2) {
                serverAddress = ADDRESS;//입력된 값 2개 아닐때는 113.198.84.26:1883
        }
        else {
                serverAddress = argv[1];//
        }

  MQTTClient_create(&client, serverAddress, CLIENTID,//
                MQTTCLIENT_PERSISTENCE_NONE, NULL);//mqtt클라이언트 생성
        conn_opts.keepAliveInterval = 20;
        conn_opts.cleansession = 1;

        if ((rc = MQTTClient_connect(client, &conn_opts)) != MQTTCLIENT_SUCCESS)
        {
                printf("Failed to connect, return code %d\n", rc);
                exit(EXIT_FAILURE);
        }
 
        printf("mqtt is connented\n");

        initSpi();

        runSpi(&client);
        MQTTClient_disconnect(client, 10000);
        MQTTClient_destroy(&client);
        return 0;
}



