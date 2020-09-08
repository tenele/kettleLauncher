#!/usr/bin/python -u
# -*- coding: UTF-8 -*-
import sys
from threading import Thread
import json
import os
import uuid
import atexit
import signal
import traceback
import pycurl
import StringIO
import urllib
import time
import smtplib
from email.mime.text import MIMEText
from email.header import Header
print('current pid',os.getpid())
print('参数个数',len(sys.argv))
for i in range(1, len(sys.argv)):
	print('参数 %s 为：%s' % (i, sys.argv[i]))
url=sys.argv[1]
data=json.loads(sys.argv[2])
uuidstr=str(uuid.uuid1())
data.update({"uuid":uuidstr})
data=json.dumps(data,encoding='utf-8',ensure_ascii=False).encode("utf-8")
print data
print type(data)
def term_sig_handler(signum, frame):
	i=0
	while(i<5):
		try:
			print 'catched singal: %d' % signum
			curl=pycurl.Curl()
			response=StringIO.StringIO()
			curl.setopt(pycurl.URL,url.replace('run','stop'))
			curl.setopt(curl.WRITEFUNCTION,response.write)
			curl.setopt(pycurl.CONNECTTIMEOUT,int(8000))
			curl.setopt(pycurl.TIMEOUT,int(8000))
			curl.setopt(pycurl.HTTPHEADER, ['Content-Type: application/json','Accept-Charset: UTF-8'])
			curl.setopt(pycurl.POSTFIELDS,data)
			print('send stopping request')
			#time.sleep(120)
			curl.perform()
			status=curl.getinfo(curl.HTTP_CODE) 
			body=response.getvalue()
			print(body)
			break
		except Exception, e:
			print e
	os._exit(1)
@atexit.register
def atexit_fun():
	print 'i am exit, stack track:'
def invokeKettle(argv):
	try:
		curl=pycurl.Curl()
		response=StringIO.StringIO()
		curl.setopt(pycurl.URL,url)
		curl.setopt(curl.WRITEFUNCTION,response.write)
		curl.setopt(pycurl.CONNECTTIMEOUT,int(argv[3]))
		curl.setopt(pycurl.TIMEOUT,int(argv[4]))
		curl.setopt(pycurl.HTTPHEADER, ['Content-Type: application/json','Accept-Charset: UTF-8'])
		curl.setopt(pycurl.POSTFIELDS,data)
		print('start http request')
		#time.sleep(120)
		curl.perform()
		#status=curl.getinfo(curl.HTTP_CODE) 
		body=response.getvalue()
		print(body)
		strlist=body.split("\n")
		response.close()
		success=False
		if(len(strlist)>0):
			print(strlist[len(strlist)-1])
			lastline=strlist[len(strlist)-1]
			result=lastline.split(",")
			print(len(result))
			if(len(result)==4):
				print(result[1].split("=")[1])
				if(int(result[1].split("=")[1])>0):
					print("error occurs")
					time.sleep(0.2)
				else:
					print("success")
					time.sleep(0.2)
					success=True
			else:
				print("error occurs")
				time.sleep(0.2)
		else:
			print("error occurs")
			time.sleep(0.2)
		if(success is True):
			os._exit(0)
		else:
			os._exit(1)
	except Exception, e:
		print e
		os._exit(1)
def sentheartbeat():
	while(True):
		try:
			curl=pycurl.Curl()
			response=StringIO.StringIO()
			curl.setopt(pycurl.URL,url.replace('run','heartbeat'))
			curl.setopt(curl.WRITEFUNCTION,response.write)
			curl.setopt(pycurl.CONNECTTIMEOUT,3000)
			curl.setopt(pycurl.TIMEOUT,3000)
			curl.setopt(pycurl.HTTPHEADER, ['Content-Type: application/json','Accept-Charset: UTF-8'])
			curl.setopt(pycurl.POSTFIELDS,data)
			print('start heartbeat request')
			#time.sleep(120)
			curl.perform()
			status=curl.getinfo(curl.HTTP_CODE) 
			body=response.getvalue()
			print(body+"\n")
			time.sleep(3)
		except Exception, e:
			pass
if __name__ == '__main__':
	signal.signal(signal.SIGTERM, term_sig_handler)
	signal.signal(signal.SIGINT, term_sig_handler)

	t=Thread(target=invokeKettle,args=(sys.argv,))
	t.setDaemon(True)
	t.start()
# 	heartbeatT=Thread(target=sentheartbeat)
# 	heartbeatT.setDaemon(True)
# 	heartbeatT.start()
	print('主线程')
	#t.join()
	print('argv4:',int(sys.argv[4]))
	time.sleep(int(sys.argv[4]))
	print('主线程结束')