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

# Send Data
while True:
    msg = ser.readline()
    text = msg.decode()
    text = text.split(',',2)
    # print(text)
    flag = text[0]
    if flag == 'startcomplete':
        colortype = text[1].split('\n')[0]
        data_js = {flag : colortype}
        
        print(data_js)
        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
    elif flag == 'finishcomplete':
        #colortype = text[1].split('\n')[0]
        data_js = {flag : 1}
        
        print(data_js)
        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
    
# Disconnect
client.disconnect()
