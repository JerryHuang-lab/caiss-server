package com.mojito.server.frame.event.log;

import com.mojito.server.frame.annotation.EventListen;
import com.mojito.server.frame.asynctask.ScheduleCenter;
import com.mojito.server.frame.event.Event;
import com.mojito.server.frame.event.EventFilter;
import com.mojito.server.frame.event.Task;
import com.mojito.server.frame.event.handler.EventHandler;
import com.mojito.server.frame.event.result.EventHandlerResult;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.lang.reflect.Field;

/**
 * @author huangwei10
 * @create 2021/5/12
 */

public class EventLogger {

	private final static Logger logger = LoggerFactory.getLogger("EventDriven");

	public static boolean setLevel(String level){
		logger.warn("#set_logger_level:" + level);
		if(logger instanceof Log4jLoggerAdapter){
			try {
				Field field;
				field = Log4jLoggerAdapter.class.getDeclaredField("logger");
				field.setAccessible(true);
				org.apache.log4j.Logger log4jLogger = (org.apache.log4j.Logger)field.get(logger);
				log4jLogger.setLevel(Level.toLevel(level));
				return true;
			} catch (Exception e) {
				logger.error("#set_logger_level_exception", e);
			}
		}
		return false;
	}


	private static void error(String msg){
		logger.error(msg);
	}

	private static void error(String msg, Throwable err){
		logger.error(msg, err);
	}

	private static void error(String format, String... args){
		logger.error(format, args);
	}

	private static void warn(String format, String... args){
		logger.warn(format, args);
	}

	private static void info(String format, String... args){
		logger.info(format, args);
	}

	private static void warn(String msg){
		logger.warn(msg);
	}

	public static boolean isInfoEnabled(){
		return logger.isInfoEnabled();
	}

	public static boolean isWarnEnabled(){
		return logger.isWarnEnabled();
	}

	public static boolean isDebugEnabled(){
		return logger.isDebugEnabled();
	}

	public static boolean isErrorEnabled(){
		return logger.isErrorEnabled();
	}

	public static void logListen(String eventType, EventHandler handler,
								 EventFilter... eventFilters) {
		if(isInfoEnabled()){
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("DO#listen,eventType:");
			sBuilder.append(eventType);
			sBuilder.append(",handler:");
			sBuilder.append(handler.toString());
			sBuilder.append(",filters:[");
			boolean f = false;
			for(EventFilter filter : eventFilters){
				if(!f){f = true;}
				else{sBuilder.append(",");}
				sBuilder.append(filter == null ? "null" : filter.toString());
			}
			sBuilder.append("]");
			logger.info(sBuilder.toString());
		}
	}

	public static void logTrigger(Event event){
		if(isDebugEnabled()){
			logger.debug("DO#trigger,event:{}", event.toString());
		}
	}

	public static void logFindHandler(Event event, EventHandler handler) {
		if(isDebugEnabled()){
			logger.debug("#findHandler,handler:{}", handler.toString());
		}
	}

	public static void logHandlerExecuted(Event event, EventHandler handler, EventHandlerResult result) {
		if(result.isSuccess()){
			if(isInfoEnabled()){
				info("#handler_execute_success,handler:{}",handler.toString());
			}
		} else {
			if(result.getException() != null){
				if(isErrorEnabled()){
					logger.error("***handler_execute_failed_exception ,handler:" + handler.toString(), result
							.getException());
				}
			} else {
				if(isWarnEnabled()){
					warn("#handler_execute_failed,handler:{},errcode={},errmsg={}", handler.toString(), result.getErrorCode(), result.getErrorMessage());
				}
			}
		}
	}


	public static void logHandlerExecuteException(Event event,
												  EventHandler handler, Exception e) {
		if(isErrorEnabled()){
			logger.error("***handler_execute_exception:,handler:" + handler
					.toString(), e);
		}
	}


	public static void logHandlerExecutedAndResultIsNull(Event event,
														 EventHandler handler) {
		if(isErrorEnabled()){
			logger.error("#execute_and_result_isnull,handler:{}", handler.toString());
		}
	}

	public static void logHandlerExecutedAndStopHanding(Event event,
														EventHandler handler) {
		if(isDebugEnabled()){
			logger.debug("#execute_and_stop_handling,handler:{}",  handler.toString());
		}
	}

	public static void logBuildExpressionFilter(String expression) {
		if(isInfoEnabled()){
			logger.info("DO#buildExpressionFilter,expression:{}" , expression);
		}
	}


	public static void logAutoRegisterHandler(EventHandler handler) {
		if(isInfoEnabled()){
			logger.info("DO#auto_register_handler:{}" , handler.toString());
		}
	}

	public static void logAutoRegisterHandlerException(
			EventHandler handler, Throwable e) {
		if(isErrorEnabled()){
			String msg = String.format("#auto_registerhandler_exception:%s", handler.toString());
			error(msg, e);
		}
	}

	public static void logIllegalEventListen(EventHandler handler,
											 EventListen eventListenAnno) {
		if(isErrorEnabled()){
			logger.error("#illegal_EventListen,handler:{}", handler.toString());
		}
	}

	public static void logHandlerHasNoEventListen(EventHandler handler) {
		if(isWarnEnabled()){
			logger.warn("#handler_has_no_EventListen,handler:{}",handler.toString());
		}
	}

	public static void logTaskNameDuplicates(String taskName, Task task) {
		if(isErrorEnabled()){
			logger.error("#TaskNameDuplicates,taskName:{},task:{}", taskName, task.toString());
		}
	}

	public static void logSetAsyncScheduleProvider(
			ScheduleCenter center) {
		if(isInfoEnabled()){
			logger.info("DO#setAsyncScheduleProvider:{}" , center.toString());
		}
	}

	public static void logScheduleEventIdIsNull(Event event, Task task) {
		if(isWarnEnabled()){
			warn("#schedule_Event_id_is_null,event:{},task:{}" , event.toString(), task.toString());
		}
	}



	public static void logSendTaskByLocalThread(Event event, Task task) {
		if(isErrorEnabled()){
			logger.error("#send_task_by_localthread,event:{},task:{}", event.toString(), task.toString());
		}
	}

	public static void logExecuteTaskByLocalThread(Event event, Task task, int redocount) {
		if(isErrorEnabled()){
			String str = String.format("#execute_task_by_localthread,event:%s,task:%s,redoCount:%s", event.toString(), task.toString(), Integer.valueOf(redocount).toString());
			logger.error(str);
		}
		if(redocount > 15 && isWarnEnabled()){
			String str = String.format("#schedule_too_many_time_redo,event:%s,task:%s,redoCount:%s", event.toString(), task.toString(), Integer.valueOf(redocount).toString());
			logger.warn(str);
		}
	}

	public static void logUseLocalThreadScheduleProvider() {
		if(isErrorEnabled()){
			logger.error("ATTENTION>>>>>>>>DONT_USE_LOCALTHREAD_SCHEDULE_PROVIDER_ON_PRODUCTION_ENVIRONMENT<<<<<<<<<<");
		}
	}
	public static void logScheduleTaskExecuted(Event event, Task task,
											   EventHandlerResult result, boolean needretry) {
		if(needretry){
			if(isInfoEnabled()){
				logger.info("#schedule_task_need_retry,task:{}", task.toString());
			}
		} else {
			if(isDebugEnabled()){
				logger.debug("#schedule_task_not_need_retry,task:{}", task.toString());
			}
		}
	}

	public static void logScheduleWillTermination(Event event, Task task) {
		if(isWarnEnabled()){
			logger.warn("#schedule_task_will_termination,task:{}", task.toString());
		}
	}


	public static void logHandlerTrace(EventHandler handler, EventHandlerResult result) {
		if(logger.isInfoEnabled()){
			StringBuilder sBuilder = new StringBuilder();
			buildHandlerTrace(handler, result, sBuilder);
			logger.info(sBuilder.toString());
		}
	}


	private static void buildHandlerTrace(
			EventHandler handler, EventHandlerResult result, StringBuilder sBuilder) {
		sBuilder.append("H(");
		sBuilder.append(handler.getClass().getName());
		if(result != null && result.isSuccess()){
			sBuilder.append("[S]");
		} else {
			sBuilder.append("[F]");
		}
	}

	public static void logHandleTaskReturnNull(Event event, Task task) {
		if(isWarnEnabled()){
			logger.warn("#handle_task_return_null,task:{}", task.toString());
		}
	}

	public static void logHandleTaskException(Event event, Task task,
											  Exception e) {
		if(isErrorEnabled()){
			logger.error(String.format("#handle_task_exception,task:%s", task.toString()), e);
		}
	}

	public static void logEventHasTriggered(Event event) {
		if(isErrorEnabled()){
			logger.error("#the_event_has_triggered,event:{}", event.toString());
		}
	}

	public static void logAsyncScheduleProviderIsNotSetted() {
		if(isErrorEnabled()){
			logger.error("#>>>>>>>>>>>AsyncScheduleProvider_is_not_setted<<<<<<<<<<<<<<");
		}
	}

	public static void logEventTraceUUidRepeated(Event event) {
		if(isErrorEnabled()){
			logger.error("#log eventTraceUUid is Repeated,eventTraceUUid:{}",event.getEventTraceUUid());
		}
	}

	public static void logEventTraceUUidNotExisted(String eventTraceUUid) {
		if(isErrorEnabled()){
			logger.error("#log eventTraceUUid is not existed,eventTraceUUid:{}",eventTraceUUid);
		}
	}

	public static void logFilterExecuteException(Event event,
												 EventFilter filter, Throwable e) {
		if(isErrorEnabled()){
			logger.error(String.format("#execute_filter_exception,event:%s,filter:%s", event.toString(), filter.toString()), e);
		}
	}

	public static void logUuidHasNoEntityId(Event event, Task task) {
		if(isErrorEnabled()){
			logger.error("#schedule_task_set_uuid_but_haveno_entityid,event:{},task:{}", event.toString(), task.toString());
		}
	}

	public static void logHandlerEventLitenIsInherited(EventHandler handler) {
		if(isErrorEnabled()){
			logger.error("#handler_EventListen_is_inherited,handler:{}", handler.toString());
		}
	}

	public static void logTaskInited(Task task) {
		if(isInfoEnabled()){
			logger.info("DO#task_inited:{}" , task.toString());
		}
	}

	public static void logGenericUuidException(Event event, Task task,
											   Throwable e) {
		if(isErrorEnabled()){
			logger.error("#generic_uuid_exception", e);
		}
	}

}
