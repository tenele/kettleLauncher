# kettleLauncher
kettle kjb and ktr launcher
主要用于替代Kettle kitchen运行脚本,将调度运行于同一个JAVA虚拟机，与AIRFLOW调度系统结合使用

第一步:
打包
第二步:
在服务器端运行 java -jar jar包名称
第三步:
安装AIRFLOW（请参看网上教程）
第四步：
在airflow里的TASK里配置类似如何下面的命令
curl.py为bin目录下的curl.py,将这个文件传到服务器相应的目录,kjbpath为kjb或ktr目录,params为相应的KTR或KJB里用到的参数
/opt/airflow/curl.py http://ip:port/kettle/api/run '{"kjbPath": "02_FULL_UPDATE/01_ODS/01_POS/POS_TO_ODS.kjb","params":{"startWorkdate":{{ var.value.startWorkdate }}}}' 20 36000

![images](https://github.com/tenele/kettleLauncher/blob/master/img/0.jpg)
![images](https://github.com/tenele/kettleLauncher/blob/master/img/1.jpg)
![images](https://github.com/tenele/kettleLauncher/blob/master/img/2.jpg)
