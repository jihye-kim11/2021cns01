import wiotp.sdk.device
import serial
import sqlite3
import datetime as dt


def myCommandCallback(cmd):
    print("Command : %s" % cmd.data)
    if cmd.commandId == "robot":
        if cmd.data.get("state") == "start":
            ser.write(b"start")
            global starttime, box_id
            box_id += 1
            starttime = cmd.timestamp.strftime('%H:%M:%S')
            cur.execute('insert into transport_data values (?, ?, ?, ?)', (box_id, colortype, starttime, endtime))
            conn.commit()
        if cmd.data.get("state") == "back":
            ser.write(b"back")

    if cmd.commandId == "update":
        if cmd.data.get("state") == "request":
            client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)


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
        "deviceId": "test2_pi"
    },
    "auth": {
        "token": "dlxorud772"
    }
}

# DB 생성
conn = sqlite3.connect("transport.db")
cur = conn.cursor()
conn.execute('CREATE TABLE transport_data(id INTEGER PRIMARY KEY, colortype TEXT, starttime TEXT, endtime TEXT)')

client = wiotp.sdk.device.DeviceClient(config=myConfig)
client.commandCallback = myCommandCallback

# Connect
client.connect()

ser = serial.Serial('/dev/rfcomm0')


starttime, box_id, endtime, total, yellow, green, blue = '', 0, '', 0, 0, 0, 0

# Send Data
while True:
    msg = ser.readline()
    text = msg.decode()
    text = text.split(',', 2)
    print('text: ', text)

    flag = text[0]
    # if flag == 'trans_start':
    #     colortype = text[1].split('\n')[0]
    #     data_js = {flag : colortype}
    #     print('data_js: ',data_js)
    #     client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)
    
    if flag == 'trans_complete': # 물류 배송이 다 끝나면 cloud로 PUBLISH
        colortype = text[1].split('\n')[0]
        result_list = colorcount(colortype)
        endtime = dt.datetime.now().strftime('%H:%M:%S')
        cur.execute('UPDATE transport_data SET endtime = ? WHERE id = ?', (endtime, box_id))
        data_js = {"total": result_list[0], "yellow": result_list[1], "green": result_list[2], "blue": result_list[3],
                   "id": box_id, "colortype": colortype, "starttime": starttime, "endtime": endtime}
        print('data_js: ', data_js)
        client.publishEvent(eventId="transportion", msgFormat="json", data=data_js, qos=0, onPublish=None)

# Disconnect
client.disconnect()
