from spike import PrimeHub, LightMatrix, Button, StatusLight, ForceSensor, MotionSensor, Speaker, ColorSensor, App, \
    DistanceSensor, Motor, MotorPair
from spike.control import wait_for_seconds, wait_until, Timer
from math import *
from hub import BT_VCP

# 왼쪽 모터가 포트 B에 연결되면
# 오른쪽 모터는 포트 A에 연결됩니다.
motor_pair = MotorPair('B', 'A')

# 모터를 초기화합니다.
motor = Motor('C')

# motor.run_to_position(degrees=330, direction='shortest path', speed=5)
print('블록을 얹어 주십시오')
wait_for_seconds(5)

# 컬러 센서를 초기화합니다.
scanner = ColorSensor('D')

ser = BT_VCP(0)
## readyflag : True (레고의 컬러인식을 완료, 레고의 colortype이 정해진 상태 ) / False ()
## transflag : True (레고가 원하는 위치로 배송이 완료된 상태 ) / False ()

readyflag = False
transflag = False

recognizeflag = True

while True:
    ###################################################################### 1.
    # { 로봇의 컬러 렌즈에 레고를 인식시켜 colortype을 정하는 로직
    # colortype변수가 어떤 값으로 정해져야 함.}
    # 색을 확인하기 위해 바구니를 조입니다.

    if recognizeflag == True:
        motor.run_to_position(degrees=330, direction='shortest path', speed=5)
        # 블록색을 인식하고 출력합니다.
        colortype = scanner.get_color()
        print('블록색:', colortype)
        # 색을 인식했으면 운송을 위해 바구니를 폅니다.
        motor.run_to_position(degrees=43, direction='shortest path', speed=5)
        # 끝에 걸치도록 이동합니다.
        motor_pair.move(-6, unit='cm', steering=0, speed=10)

        recognizeflag = False
        readyflag = True
    ####################################################################################

    #################################################################################### 2.
    if readyflag == True: # (로봇이 컬러 렌즈를 인식하면)
        # 라즈베리 파이로 'ready'(로봇이 컬러를 인식했고, 로봇이 움직이는 command를(start) 기다리는 상태)
        ser.write('robot_ready')
        readyflag = False  # readyflag를 보냈으면 False로 바꿈.
####################################################################################

    msg = ser.readline()
    if msg != None:
        msg = msg.decode()
        if msg == 'start':  # 안드로이드에서 start 버튼을 눌러서, 로봇이 지정된 위치로 배송을 시작
            ## 로봇이 colortype에 따라 정해진 위치로 운송, 배송하는 로직 ...
            #################################################################################### 3.
            # 바닥색을 인식할때까지 움직이도록합니다. 바닥색이 인식되면 멈추고 색을 출력합니다.
            motor_pair.start(steering=0, speed=-10)
            scanner.wait_until_color(colortype)
            wait_for_seconds(0.5)
            scanner.wait_until_color(colortype)
            motor_pair.stop()
            print('바닥색:', colortype)

            # 운송장소에 도착하였으므로 바구니를 조여 블록을 털어냅니다.
            motor.run_to_position(degrees=330, direction='shortest path', speed=5)
            print('운송 완료')

            # 털어냈으면 바닥인식을 위해 바구니를 폅니다
            motor.run_to_position(degrees=43, direction='shortest path', speed=5)
            wait_for_seconds(1)

            transflag = True
            ####################################################################################

        elif msg == 'back':  # 안드로이드에서 back 버튼을 눌러서, 로봇이 처음 시작 위치로 돌아감.
            ## 로봇이 처음 시작 위치로 돌아가는 로직...
            #################################################################################### 4.
            # 검은 바닥까지 이동합니다
            motor_pair.start(steering=0, speed=10)
            scanner.wait_until_color('black')
            wait_for_seconds(0.5)
            scanner.wait_until_color('black')
            motor_pair.stop()

            # 원래 위치에 도착하였으므로 바구니를 조여 대기합니다.
            motor.run_to_position(degrees=330, direction='shortest path', speed=5)
            print('블록을 얹어 주십시오')
            # 3초 대기후 반복
            wait_for_seconds(3)

            recognizeflag = True

            ####################################################################################

    #################################################################################### 5.
    if (transflag == True):  # 배송이 마치면 complete 되면,
        ser.write('trans_complete,' + colortype + "\n")  # 배송이 마치면, 라즈베리파이로 flag와 colortype을 보냄. (colortype은 배송이 마치기전에 값이 정해져야 함.)
        wait_for_seconds(0.5)
        transflag = False  # 라즈베리파이로 flag와 colortype을 보내고, transflag를 바꿈.
####################################################################################