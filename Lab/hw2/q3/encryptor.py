import json 
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import base64


key = RSA.importKey(open('key.pem').read())
cipher = PKCS1_OAEP.new(key)

with open('data.json') as json_file:
    data = json.load(json_file)

encrypted = dict()

print("Start Encrypting")
for i in data:
	encrypted[i] = str(base64.b64encode(cipher.encrypt(data[i].encode())),'utf-8')
print("Encrpting End")
	
with open('encrypted.json', 'w') as outfile:
    json.dump(encrypted, outfile)
