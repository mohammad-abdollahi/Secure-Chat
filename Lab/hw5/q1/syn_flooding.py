
import os # to use operating system dependent functionality. 
import sys #System specific parameters & functions
import random 
from scapy.all import * 

destIP = input('Destination IP:')
destPort = input('Destination Port:')
numPacket = input('Number of Packets:')

# converts the arguments into dictionary format for easier retrieval.
iterationCount = 0 # variable used to control the while loop for the amount of times a packet is sent.
 # executed if the user entered an amount of segments to be send.
while iterationCount < int(numPacket):
	a=IP(dst=destIP)/TCP(flags="S", sport=RandShort(), dport=int(destPort)) # Creates the packet and assigns it to variable a
	send(a,  verbose=0) # Sends the Packet
	iterationCount = iterationCount + 1
	print(str(iterationCount) + " Packet Sent")
print("All packets successfully sent.")
