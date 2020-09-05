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
f = open("program2.py",'wb') #open in binary
while True:
    try:
        # receive data and write it to file
        l = s.recv(1024)
        data = encryptor.decrypt(l)
        f.write(data)
        f.close()
        break
    except ConnectionResetError:
        print('Broken PIPE!')
 
#recieve hash of file
hash = ''
while 1:
    try:
        data = s.recv(1024)
        hash = data.decode()
        break
    except ConnectionResetError:
        print('Broken PIPE!')

f = open("program2.py", "rb")
l = f.read()
file_hash = hashlib.md5(l)
f_hash = file_hash.hexdigest()
print(f_hash)
if f_hash == str(hash) :
    print("excuting program")
    exec(open('program2.py').read())

