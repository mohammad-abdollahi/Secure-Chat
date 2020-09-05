import json
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import base64


key = RSA.importKey(open('key.pem').read())
cipher = PKCS1_OAEP.new(key)

with open('encrypted.json') as json_file:
    data = json.load(json_file)

s = str(input('Enter Student Name:'))

for i in data:
	if s == i:
		print(cipher.decrypt(base64.b64decode(data[i])))
		exit(0)

print("Student Not Found")
