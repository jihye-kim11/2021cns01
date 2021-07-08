from spike import PrimeHub, Motor, DistanceSensor
from spike.control import wait_for_seconds
from hub import BT_VCP
#######################################
primeHub = PrimeHub()
primeHub.speaker.beep()
motorA = Motor('A')
motorA.run_to_position(90)
distance = DistanceSensor('E')
#######################################
ser = BT_VCP(0)
ser.readline()

## readyflag : True (레고의 컬러인식을 완료, 레고의 colortype이 정해진 상태 ) / False ()
## transflag : True (레고가 원하는 위치로 배송이 완료된 상태 ) / False ()

readyflag = False
transflag = False

while True:
    print(1111111)

    #{ 로봇의 컬러 렌즈에 레고를 인식시켜 colortype을 정하는 로직
    # ready_flag = True
    # colortype변수가 어떤 값으로 정해져야 함.}



    if ready_flag == True: (로봇이 컬러 렌즈를 인식하면)
        # 라즈베리 파이로 'ready'(로봇이 컬러를 인식했고, 로봇이 움직이는 command를(start) 기다리는 상태)
        ser.write('robot_ready')

        readyflag = False # readyflag를 보냈으면 False로 바꿈.

        
    msg = ser.readline()
    if msg != None:
        msg = msg.decode()
        if msg == 'start' : # 안드로이드에서 start 버튼을 눌러서, 로봇이 지정된 위치로 배송을 시작
            ## 로봇이 colortype에 따라 정해진 위치로 운송, 배송하는 로직 ...
            transflag= True
            


        elif msg == 'back' : # 안드로이드에서 back 버튼을 눌러서, 로봇이 처음 시작 위치로 돌아감.
            ## 로봇이 처음 시작 위치로 돌아가는 로직...
            #



    if (transflag== True ): # 배송이 마치면 complete 되면, 
        ser.write('trans_complete,'+colortype+"\n") # 배송이 마치면, 라즈베리파이로 flag와 colortype을 보냄. (colortype은 배송이 마치기전에 값이 정해져야 함.)
        wait_for_seconds(0.5)

        transflag = False # 라즈베리파이로 flag와 colortype을 보내고, transflag를 바꿈.
