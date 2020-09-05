import os
import pyaes
import socket
import threading
import hashlib
import json
from datetime import datetime
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP


print("Client")
HOST = "localhost"
PORT = 5555
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((HOST, PORT))
except ConnectionError:
    print('Could Not Connect !')
    exit(-1)

# receive server's public key
data = ''    
while 1:
	try:
		data = s.recv(1024)
		data = int(data.decode())
		break
	except ConnectionResetError:
		print('Broken PIPE!')


#send aes key for server
serverPublicKey = RSA.construct((data, 65537))
encryptor = PKCS1_OAEP.new(serverPublicKey)
sending_data = str(input("Press Enter To Send SessionKey For Client"))
sessionKey = os.urandom(64)
sending_data = encryptor.encrypt(sessionKey)
s.send(sending_data)


hashed = hashlib.sha256(sessionKey).digest()
aes = pyaes.AES(hashed)

def process_bytes(bytess):
    ret = []
    while(len(bytess)>=16):
        if(len(bytess)>=16):
            byts = bytess[:16]
            ret.append(byts)
            bytess = bytess[16:]
        else:
            print("Block Size Mismatch ")
    return ret
    
def messageToStream(data): 
    streams = []
    while (len(data)>0):
        if(len(data)>=16):
            stream = data[:16]
            data = data[16:]
        else:
            stream = data + ("~"*(16-len(data)))
            data = ''
        stream_bytes = [ ord(c) for c in stream]
        streams.append(stream_bytes)
    return streams

def verify_and_display(recv_dict):
    timestamp = recv_dict['timestamp']
    recv_hash = recv_dict['hash']
    message   = recv_dict['message']
    mess_hash = hashlib.sha256(str(message).encode('utf-8')).hexdigest()
    SET_LEN = 80
    if (mess_hash == recv_hash):
        tag = str('ok :)')
    else:
        tag = str('invalid :(')
    spaces = SET_LEN - len(str(message)) - len('Received : ') - 1
    if spaces > 0 :
        space = ' '*spaces
        sentence = 'Received : ' + str(message) + space + tag 
        print(sentence)

class myThread(threading.Thread):
    def __init__(self,id):
        threading.Thread.__init__(self)
        self.threadID = id
        
    def stop(self):
        self.is_alive = False
        
    def run(self):
        print("[+] Listening On Thread "+str(self.threadID))
        while 1:
            try:

                data = s.recv(1024)
                if(data!=""):
                    mess = ''
                    processed_data = process_bytes(data)
                    for dat in processed_data:
                        decrypted = aes.decrypt(dat)
                        for ch in decrypted:
                            if(chr(ch)!='~'):
                                mess+=str(chr(ch))
                    try:
                        data_recv = json.loads(mess)
                        verify_and_display(data_recv)
                    except:
                        print('Unrecognised Data or Broken PIPE ')
            except ConnectionResetError:
                print('Broken PIPE!')

Listening_Thread = myThread(1)
Listening_Thread.daemon = True
Listening_Thread.start()

while 1:
    try:
        sending_data = str(input(""))
    except KeyboardInterrupt:
        s.close()
        Listening_Thread.stop()
        exit(-1)
   
    timestamp = str(datetime.now())[11:19]
    mess_hash = hashlib.sha256(str(sending_data).encode('utf-8')).hexdigest()
    send_data = {
        "timestamp" : timestamp,
        "message"   : sending_data,
        "hash"      : mess_hash
    }
    send_json_string = json.dumps(send_data)
    sending_bytes = messageToStream(send_json_string)
    enc_bytes = []
    for byte in sending_bytes:
        ciphertext = aes.encrypt(byte)
        enc_bytes += bytes(ciphertext)
 
    s.send(bytes(enc_bytes))
s.close() 
