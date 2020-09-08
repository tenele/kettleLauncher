package net.tenele.config;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tenele.util.Launcher;
import net.tenele.util.Launcher.JobRunning;

public class HeartBeatT implements Runnable {
	private final static Logger LOG = LoggerFactory.getLogger(HeartBeatT.class);
	@Override
	public void run() {
		while(true) {
			Hashtable<String,JobRunning> jobs = Launcher.tasks;
			Iterator<String> itr = jobs.keySet().iterator();  
	        while (itr.hasNext()){  
	            String key = (String)itr.next();  
	            JobRunning run = (JobRunning)jobs.get(key);
				long interval = (new Date().getTime()-run.getRunningdate().getTime())/1000;
				if(interval>30) {
					
//					Job job =run.getJob();
//					job.stopAll();
//					try {
//						job.interrupt();
//					}catch(Exception e) {}
//					job.setFinished(true);
//					job.eraseParameters();
//					jobs.remove(key);
//					LOG.info(key +" job was stopped");
				}
	        }
	        try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
			}
		}
	}

}
