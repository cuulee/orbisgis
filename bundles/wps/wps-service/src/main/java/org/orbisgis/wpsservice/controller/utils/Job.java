package org.orbisgis.wpsservice.controller.utils;

import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.controller.execution.ProcessExecutionListener;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side object created by a processing service in response for a particular process execution.
 *
 * @author Sylvain PALOMINOS
 */
public class Job implements ProcessExecutionListener {

    /** Process polling time in milliseconds. */
    private static final long MAX_PROCESS_POLLING_MILLIS = 10000;
    private static final long BASE_PROCESS_POLLING_MILLIS = 1000;
    /** WPS process */
    private ProcessDescriptionType process;
    /** Unique identifier of the job */
    private UUID id;
    /** Time when the process has been started */
    private long startTime;
    /** Map containing all the log messages */
    private Map<String, LogType> logMap;
    /** State of the process running */
    private ProcessState state;
    /** Map of the input/output data of the process execution */
    private Map<URI, Object> dataMap;
    private long processPollingTime;

    public Job(ProcessDescriptionType process, UUID id, Map<URI, Object> dataMap){
        this.process = process;
        this.id = id;
        logMap = new HashMap<>();
        state = ProcessState.ACCEPTED;
        this.dataMap = dataMap;
        processPollingTime = BASE_PROCESS_POLLING_MILLIS;
    }

    @Override
    public void setStartTime(long time) {
        startTime = time;
    }

    @Override
    public void appendLog(LogType logType, String message) {
        logMap.put(message, logType);
    }

    @Override
    public void setProcessState(ProcessState processState) {
        state = processState;
    }

    /**
     * Returns the process state.
     * @return The process state.
     */
    public ProcessState getState(){
        return state;
    }

    /**
     * Returns the dataMap.
     * @return The dataMap.
     */
    public Map<URI, Object> getDataMap(){
        return dataMap;
    }

    /**
     * Returns the process.
     * @return The process.
     */
    public ProcessDescriptionType getProcess(){
        return process;
    }

    /**
     * Returns the job id.
     * @return the job id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the process polling time.
     * @return The process polling time.
     */
    public long getProcessPollingTime(){
        long time = processPollingTime;
        if(processPollingTime < MAX_PROCESS_POLLING_MILLIS) {
            processPollingTime += BASE_PROCESS_POLLING_MILLIS;
        }
        return time;
    }
}
