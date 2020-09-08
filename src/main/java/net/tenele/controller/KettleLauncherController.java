package net.tenele.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import net.tenele.dto.KettleParam;
import net.tenele.util.JobResult;
import net.tenele.util.Launcher;
import net.tenele.util.Launcher.JobRunning;

@RestController
@RequestMapping({ "api" })
public class KettleLauncherController {
	
	@Value("${kettle_scripts_path}")
	String kettle_scripts_path;
	private final static Logger LOG = LoggerFactory.getLogger(KettleLauncherController.class);
	@CrossOrigin
	@ResponseBody
	@PostMapping(value="run",produces="text/plain;charset=UTF-8")
	public String run(@RequestBody KettleParam param) throws Exception {
//		StringBuffer logs = new StringBuffer();
//			synchronized(KettleLauncherController.class) {
//				KettleLoggingEventListener listener = new KettleLoggingEventListener() {
//					@Override
//					public void eventAdded(KettleLoggingEvent paramKettleLoggingEvent) {
//						logs.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(paramKettleLoggingEvent.getTimeStamp())) +","+paramKettleLoggingEvent.getMessage()+"\n");
//					}
//				};
//				KettleLogStore.getAppender().addLoggingEventListener(listener); 
				
				Date date =new Date();
				SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				LOG.info("start time:"+format.format(date)+",kettleParams:"+JSON.toJSONString(param));
				JobResult result = Launcher.run(param.getUuid(),kettle_scripts_path.concat(param.getKjbPath()),param.getParams());
				
				LOG.info("start time:"+format.format(date)+",kettleParams:"+JSON.toJSONString(param) +", result = " +result+", end time="+format.format(new Date()));
//				logs.append(result.toString());
//				KettleLogStore.getAppender().removeLoggingEventListener(listener);
//			}
				if(result!=null)
					return result.getLog().toString();
				else
					return "运行出错";
	}
	@CrossOrigin
	@ResponseBody
	@PostMapping(value="stop",produces="text/plain;charset=UTF-8")
	public String stop(@RequestBody KettleParam param) {
		LOG.info("stop "+JSON.toJSONString(param));
		if(Launcher.tasks.get(param.getUuid())!=null) {
			JobRunning jobRun=(JobRunning)Launcher.tasks.get(param.getUuid());
			Object obj = jobRun.getTask();
		    if(obj instanceof Job) {
				Job job = ((Job)obj);
				job.stopAll();
				try {
					job.interrupt();
				}catch(Exception e) {}
				job.setFinished(true);
				job.eraseParameters();
		    }
		    if(obj instanceof Trans) {
		    	Trans trans = ((Trans)obj);
		    	trans.eraseParameters();
				trans.cleanup();
		    	trans.stopAll();
				try {
					trans.killAll();
				}catch(Exception e) {}
//				trans.setFinished(true);
				trans.eraseParameters();
		    }
			Launcher.tasks.remove(param.getUuid());
		}
		LOG.info("stop result true");
		return "stop result:true";
	}
	@CrossOrigin
	@ResponseBody
	@PostMapping(value="heartbeat",produces="text/plain;charset=UTF-8")
	public String heartbeat(@RequestBody KettleParam param) {
		LOG.info("receive heartbeat package "+JSON.toJSONString(param));
		if(Launcher.tasks.get(param.getUuid())!=null) {
			JobRunning job = Launcher.tasks.get(param.getUuid());
			job.setRunningdate(new Date());
		}
		return "server receive heartbeat package";
	}
}
