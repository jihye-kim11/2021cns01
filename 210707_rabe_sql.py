import wiotp.sdk.device
import serial
import sqlite3
import datetime as dt


def myCommandCallback(cmd):
    print("Command : %s" % cmd.data)
    if cmd.commandId == "truck":
        if cmd.data.get("state") == "run":
            ser.write(b"run")
        if cmd.data.get("state") == "stop":
            ser.write(b"stop")

# SQLite DB 생성
conn = sqlite3.connect("transport.db")
cur = conn.cursor()
conn.execute('CREATE TABLE transport_data(transport_start_time TEXT, transport_finish_time TEXT)')

myConfig = {
    "identity": {
        "orgId": "wf2ir5",
        "typeId": "Raspberry_Pi",
        "deviceId": "test_rasp"  # NEED CHANGING
    },
    "auth": {
        "token": "1q2w3e4r"  # NEED CHANGING
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
    text = text.split(',', 2)
    key = text[0]
    if key == 'dist_cm':
        data = text[1].split('\n')[0]
        # 거리가 10cm 이하일 시 현재시간 db에 저장
        if int(data) < 10:
            x = dt.datetime.now()
            cur.execute('insert into transport_data values (?, ?)', (x.strftime('%H:%M:%S'), x.strftime('%H:%M:%S')))
            conn.commit()
        data_js = {key: data}
        print(data_js)
        client.publishEvent(eventId="distance", msgFormat="json", data=data_js, qos=0, onPublish=None)

# Disconnect
client.disconnect()