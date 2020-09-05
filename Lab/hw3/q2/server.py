from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from datetime import datetime
import hashlib
import json
import os
import pyaes
import socket
import sys
import threading

HOST = '0.0.0.0'
PORT = 5555


print("Server Running ")
print("Allowing All Incoming Connections ")
print("PORT "+str(PORT))
print("Waiting For Connection...")

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(1)
conn, addr = s.accept()
print('Connected by ', addr)
# receive server's public key
data = ''
while 1:
    try:
        data = conn.recv(1024)
        data = int(data.decode())
        break
    except ConnectionResetError:
        print('Broken PIPE!')

#send program for client
clientPublicKey = RSA.construct((data, 65537))
encryptor = PKCS1_OAEP.new(clientPublicKey)
sending_data = str(input("Press Enter To Send File For Client"))

print("Send Good File")
f = open ("good", "rb")
l = f.read()
sending_data = l
conn.send(sending_data)


print("Send evil File")
f = open ("evil", "rb")
l = f.read()
sending_data = l
conn.send(sending_data)
print("done!!")

