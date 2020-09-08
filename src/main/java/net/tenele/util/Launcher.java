package net.tenele.util;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.KettleLogLayout;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
	private final static Logger LOG = LoggerFactory.getLogger(Launcher.class);
	public static Hashtable<String,JobRunning> tasks =new Hashtable<String,JobRunning>();
	public static JobResult run(String uuid,String jobname,Map<String,String> params) {
		try {
			if (jobname.endsWith("kjb")) {
				return runJob(uuid, jobname, params);
			} else {
				return runKtr(uuid, jobname, params);
			}
		}finally{
			System.gc();
		}
	}
	public static JobResult runJob(String uuid,String jobname,Map<String,String> params) {
		JobResult jobResult=new JobResult();
		StringBuffer sb = new StringBuffer();
		Job job =null;
		try {
			JobMeta jobMeta = new JobMeta(jobname, null);
			String executionId= UUID.randomUUID().toString().replaceAll("-", "");
			SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject( "SPOON", LoggingObjectType.SPOON, null );
		    spoonLoggingObject.setContainerObjectId( executionId );
		    spoonLoggingObject.setLogLevel( LogLevel.DETAILED);
			job = new Job(null, jobMeta,spoonLoggingObject);
			if(params!=null) {
				Iterator<String> iterator = params.keySet().iterator();
				while(iterator.hasNext()) {
					String key=iterator.next();
					job.setVariable(key, params.get(key));
				}
//				params.forEach((k,v)->{
//					job.setVariable(k, v);
//				});
			}
			job.setLogLevel(LogLevel.DETAILED);
			job.start();
			tasks.put(uuid, new JobRunning(job,new Date()));
			System.out.println(jobname+" start");
			job.waitUntilFinished();
			System.out.println(jobname+" run over");
			tasks.remove(uuid);
			job.setFinished(true);  
			job.eraseParameters();
			Result result = job.getResult();
			if (job.getErrors() > 0) {
				result.setNrErrors(job.getErrors());
				//return job.getErrors();
			}
			KettleLogLayout logLayout = new KettleLogLayout( true );
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren( job.getLogChannelId() );
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo( childIds, true, -1, KettleLogStore.getLastBufferLineNr() );
			 for ( int i = 0; i < logLines.size(); i++ ) {
	             KettleLoggingEvent event = logLines.get( i );
	             String line = logLayout.format( event ).trim();
	             sb.append(line).append("\n");
			 }
			sb.append(result.toString());

			 jobResult.setLog(sb);
			 jobResult.setResult(result);
			 System.out.println("result="+result);
			 return jobResult;
		} catch (Exception e) {
			LOG.error("job运行出错",e);
			sb.append(e.getMessage());
			jobResult.setLog(sb);
		}finally{
			if(job!=null && !job.isFinished()) {
				tasks.remove(uuid);
				job.stopAll();
				try {
					job.interrupt();
				}catch(Exception e1) {}
				job.setFinished(true);
				job.eraseParameters();
				job=null;
			}
		}
		return jobResult;
	}
	public static JobResult runKtr(String uuid,String jobname,Map<String,String> params) {
		JobResult jobResult=new JobResult();
		StringBuffer sb = new StringBuffer();
		Trans trans = null;
		try {
			TransMeta transMeta = new TransMeta(jobname);	
			String executionId= UUID.randomUUID().toString().replaceAll("-", "");
			SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject( "SPOON", LoggingObjectType.SPOON, null );
		    spoonLoggingObject.setContainerObjectId( executionId );
		    spoonLoggingObject.setLogLevel( LogLevel.DETAILED);
			trans = new Trans(transMeta,spoonLoggingObject);
			if(params!=null) {
				Iterator<String> iterator = params.keySet().iterator();
				while(iterator.hasNext()) {
					String key=iterator.next();
					trans.setVariable(key, params.get(key));
				}
//				params.forEach((k,v)->{
//					job.setVariable(k, v);
//				});
			}
			trans.setLogLevel(LogLevel.DETAILED);
			trans.execute(null);
			tasks.put(uuid, new JobRunning(trans,new Date()));
			System.out.println(jobname+" start");
			trans.waitUntilFinished();
			System.out.println(jobname+" run over");
			tasks.remove(uuid);
			//trans.setFinished(true);  
			trans.eraseParameters();
			trans.cleanup();
			Result result = trans.getResult();
			if (trans.getErrors() > 0) {
				result.setNrErrors(trans.getErrors());
				//return job.getErrors();
			}

			KettleLogLayout logLayout = new KettleLogLayout( true );
			List<String> childIds = LoggingRegistry.getInstance().getLogChannelChildren( trans.getLogChannelId() );
			List<KettleLoggingEvent> logLines = KettleLogStore.getLogBufferFromTo( childIds, true, -1, KettleLogStore.getLastBufferLineNr() );
			 for ( int i = 0; i < logLines.size(); i++ ) {
	             KettleLoggingEvent event = logLines.get( i );
	             String line = logLayout.format( event ).trim();
	             sb.append(line).append("\n");
			 }
			 sb.append(result.toString());
			 jobResult.setLog(sb);
			 jobResult.setResult(result);
			 System.out.println("result="+result);
			 return jobResult;
		} catch (Exception e) {
			LOG.error("ktr运行出错",e);
			sb.append(e.getMessage());
		}finally{
			if(trans!=null && !trans.isFinished()) {
				tasks.remove(uuid);
				trans.eraseParameters();
				trans.cleanup();
				trans.stopAll();
				try {
					trans.killAll();
				}catch(Exception e1) {}
				trans.eraseParameters();
				trans=null;
			}
		}
		return jobResult;
	}
	public static class JobRunning{
		private Object task;
		private Date runningdate;
		
		public JobRunning(Object task, Date runningdate) {
			super();
			this.task = task;
			this.runningdate = runningdate;
		}
		public Object getTask() {
			return task;
		}
		public void setTask(Object task) {
			this.task = task;
		}
		public Date getRunningdate() {
			return runningdate;
		}
		public void setRunningdate(Date runningdate) {
			this.runningdate = runningdate;
		}
		
		
	}
}
