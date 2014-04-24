package org.shizhz.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * A simple logger util used in this program.
 * 
 * @author shizhz
 * 
 */
public final class Logger {

    private BufferedWriter writer;

    private Logger(OutputStream out) {
        writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    public static Logger newInstance(OutputStream out) {
        return new Logger(out);
    }

    public void log(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            }
        }
    }

    public void logln(String message) {
        log(message + "\n");
    }
}
