
import serial
import time
from socket import *
from time import ctime




HOST = ''
PORT = 21567
BUFSIZE = 1024
ADDR = (HOST,PORT)

tcpSerSock = socket(AF_INET, SOCK_STREAM)
tcpSerSock.bind(ADDR)
tcpSerSock.listen(1)

arduino_serial =serial.Serial(port="/dev/ttyACM0", baudrate=115200, timeout=1)




def move(data):
        arduino_serial.write(bytes(data, 'utf-8'))
        time.sleep(0.05)
        data = arduino_serial.readline()
        return data
    
    
    
    
while True:
        print ('Waiting for connection')
        tcpCliSock,addr = tcpSerSock.accept()
        print(tcpCliSock,addr)
        print( '...connected from :', addr)
        try:
                        data =  tcpCliSock.recv(BUFSIZE).decode('utf-8')
                        print("data:", data)
                        responce=move(data)
                        print("responce",responce)
                        
        except KeyboardInterrupt:
            print("something has spoilt")
               
tcpSerSock.close();




