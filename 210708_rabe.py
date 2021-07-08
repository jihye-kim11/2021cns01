import wiotp.sdk.device
import serial

def myCommandCallback(cmd): ## 
    print("Command : %s" % cmd.data)
    if cmd.commandId == "door" :
        if cmd.data.get("state") == "run":
            ser.write(b"run")
        if cmd.data.get("state") == "stop":
            ser.write(b"stop")
            
myConfig = { 
    "identity": {
        "orgId": "wf2ir5",
        "typeId": "Raspberry_Pi",
        "deviceId": "test2_pi"
    },
    "auth": {
        "token": "dlxorud772"
    }
}
client = wiotp.sdk.device.DeviceClient(config=myConfig)
client.commandCallback = myCommandCallback

# Connect
client.connect()

ser = serial.Serial('/dev/rfcomm0')



total = 0
yellow = 0
green = 0
blue = 0
def colorcount(cotype):
    global yellow
    global green
    global blue
    global total

    if cotype == 'yellow':
        yellow += 1
        total += 1

    elif cotype == 'green':
        yellow += 1
        total += 1

    elif cotype == 'blue':
        yellow += 1
        total += 1

    return [total,yellow,green,blue]
        
# Send Data
while True:
    msg = ser.readline()
    text = msg.decode()
    text = text.split(',',2)
    print('text: ',text)

    flag = text[0]
    # if flag == 'startcomplete':
    #     colortype = text[1].split('\n')[0]
    #     data_js = {flag : colortype}
        
    #     print('data_js: ',data_js)
    #     client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
    if flag == 'trans_complete': # 물류 배송이 다 끝나면 cloud로 PUBLISH
        colortype = text[1].split('\n')[0]
        result_list = colorcount(colortype)
        data_js = {"total" : result_list[0], "yellow" : result_list[1], "green" : result_list[2],"blue" : result_list[3]} 
        print('data_js: ',data_js)

        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
# Disconnect
client.disconnect()
