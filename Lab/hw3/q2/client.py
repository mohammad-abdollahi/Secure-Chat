from Crypto import Random
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from datetime import datetime
import hashlib
import json
import os
import pyaes
import socket
import threading


print("Client")
HOST = "localhost"
PORT = 5555
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((HOST, PORT))
except ConnectionError:
    print('Could Not Connect !')
    exit(-1)

random_generator = Random.new().read
RsaKey = RSA.generate(1024, random_generator)
print('Key generated')
encryptor = PKCS1_OAEP.new(RsaKey)
clientPublicKey = RsaKey.publickey().n

#send public key to client
sending_data = input("Press Enter To Send PublicKey For Server")
s.send(bytes(str(clientPublicKey).encode()))

#recieve program
f = open("first_recv",'wb') #open in binary
while True:
    try:
        # receive data and write it to file
        l = s.recv(1024)
        data = str(l)
        f.write(data)
        f.close()
        break
    except ConnectionResetError:
        print('Broken PIPE!')
 
print("excuting first program")
exec(open('first_recv').read())

f = open("second_recv",'wb') #open in binary
while True:
    try:
        # receive data and write it to file
        l = s.recv(1024)
        data = str(l)
        f.write(data)
        f.close()
        break
    except ConnectionResetError:
        print('Broken PIPE!')    
    
print("excuting second program")
exec(open('second_recv').read())

    

