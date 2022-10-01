package com.rj.chatrj;

import java.util.HashMap;
import java.util.Map;

/**
 * Method used for logging.
 */
public class SysLogger {
    public static org.apache.log4j.Logger logger = LoggerUtil.getLogger("flowrj");
    private static final Map<Class, Boolean> PRINT_STACK_TRACE = new HashMap<Class, Boolean>();

    /**
     * Logging throwable as log.
     *
     * @param th throwable
     */
    public static void log(Throwable th) {
        logger.trace(th);
    }

    /**
     * Logging string as log.
     *
     * @param msg message
     */
    public static void log(String msg) {
        logger.trace(msg);
    }


    /**
     * Logging throwable as error.
     *
     * @param err throwable
     */
    public static void logErr(Throwable err){
        logger.error(err);
    }

    /**
     * Logging string as error.
     *
     * @param err error message
     */
    public static void logErr(String err) {
        logger.error(err);
    }


    /**
     * Logging throwable as warn.
     *
     * @param err throwable
     */
    public static void logWarn(Throwable err){
        logger.warn(err);
    }

    /**
     * Logging string as warn.
     *
     * @param err error message
     */
    public static void logWarn(String err) {
        logger.warn(err);
    }


    /**
     * Logging throwable as fatal.
     *
     * @param err throwable
     */
    public static void logFatal(Throwable err){
        logger.fatal(err);
    }

    /**
     * Logging string as fatal.
     *
     * @param err error message
     */
    public static void logFatal(String err) {
        logger.fatal(err);
    }

    /**
     * Logging throwable as info.
     *
     * @param err throwable
     */
    public static void logInfo(Throwable err){
        logger.info(err);
    }

    /**
     * Logging string as info.
     *
     * @param err error message
     */
    public static void logInfo(String err) {
        logger.info(err);
    }


    /**
     * Logging throwable as debug.
     *
     * @param err throwable
     */
    public static void logDebug(Throwable err){
        logger.debug(err);
    }

    /**
     * Logging string as debug.
     *
     * @param err error message
     */
    public static void logDebug(String err) {
        logger.debug(err);
    }

}