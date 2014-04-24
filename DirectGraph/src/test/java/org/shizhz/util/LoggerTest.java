package org.shizhz.util;

import org.junit.Test;

public class LoggerTest {

    @Test
    public void testLogToSystemOut() {
        Logger logger = Logger.newInstance(System.out);

        logger.log("log message1");
        logger.log("log message2\n");
        logger.log("log message3");
        logger.logln("log break a new line");
        logger.log("final line");
    }
}
