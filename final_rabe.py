import wiotp.sdk.device
import serial
import datetime as dt

def myCommandCallback(cmd):
    print("Command : %s" % cmd.data)
    if cmd.commandId == "robot":
        if cmd.data.get("state") == "start":
            ser.write(b"start")
            global starttime, box_id
            box_id += 1
            starttime = cmd.timestamp.strftime('%H:%M:%S')
            # DB에 데이터 insert
            #cur.execute('insert into transport_data values (?, ?, ?, ?)', (box_id, "", starttime, ''))
            #conn.commit()

            print('robot start event published')
            #client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
        if cmd.data.get("state") == "back":
            ser.write(b"back")

    # if cmd.commandId == "update":
    #     if cmd.data.get("state") == "request":
    #         data_js = client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)


def colorcount(cotype):
    global yellow, green, blue, total

    if cotype == 'yellow':
        yellow += 1
        total += 1
    elif cotype == 'green':
        green += 1
        total += 1
    elif cotype == 'blue':
        blue += 1
        total += 1

    return [total,yellow,green,blue]


myConfig = { 
    "identity": {
        "orgId": "wf2ir5",
        "typeId": "Raspberry_Pi",
        "deviceId": "test2_pi" ## change !!!
    },
    "auth": {
        "token": "dlxorud772" ## change !!!
    }
}

# DB 생성
#conn = sqlite3.connect("transport.db")
#cur = conn.cursor()
#conn.execute('CREATE TABLE transport_data(id INTEGER PRIMARY KEY, colortype TEXT, starttime TEXT, endtime TEXT)')

client = wiotp.sdk.device.DeviceClient(config=myConfig)
client.commandCallback = myCommandCallback

# Connect
client.connect()
ser = serial.Serial('/dev/rfcomm3')
starttime, box_id, endtime, total, yellow, green, blue = '', 0, '', 0, 0, 0, 0

# Send Data
while True:
    msg = ser.readline()
    text = msg.decode()
    text = text.split(',', 2)
    print('text: ', text)

    flag = text[0]
    if flag == 'robot_ready\n':
        print('trans_ready event published')

        data_js = {"command" : "ready"}
        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
    if flag == 'trans_complete': # 물류 배송이 다 끝나면 cloud로 PUBLISH
        colortype = text[1].split('\n')[0]
        result_list = colorcount(colortype)
        endtime = dt.datetime.now().strftime('%H:%M:%S')
        #cur.execute('UPDATE transport_data SET endtime = ? WHERE id = ?', (endtime, box_id))
        #cur.execute('UPDATE transport_data SET colortype = ? WHERE id = ?', (colortype, box_id))
        #conn.commit()
        data_js = {"total": result_list[0], "yellow": result_list[1], "green": result_list[2], "blue": result_list[3],
                   "id": box_id, "colortype": colortype, "starttime": starttime, "endtime": endtime, "command": 'data'}
        print('trans_complete event published')
        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)

# Disconnect
client.disconnect()
