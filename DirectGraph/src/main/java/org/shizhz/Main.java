package org.shizhz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.shizhz.directive.DirectiveProcessor;
import org.shizhz.exception.DirectiveException;
import org.shizhz.util.Logger;

/**
 * Main class for this program
 * 
 * @author shizhz
 * 
 */
public final class Main {
    private static final Logger logger = Logger.newInstance(System.out);

    private static void usage() {
        logger.logln("Usage: ");
        logger.logln("\t`java -jar train-cli-1.0.jar -i` to enter the interactive mode. OR");
        logger.logln("\t`java -jar train-cli-1.0.jar -f <input file>` to use the file as input.");
    }

    private void run(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String directive = "";
        DirectiveProcessor processor = DirectiveProcessor.newInstance();
        try {
            logger.log("> ");
            while ((directive = br.readLine()) != null) {
                directive = directive.trim();
                if (directive.trim().startsWith("#")) {
                    continue;
                }
                if ("exit".equals(directive)) {
                    logger.logln("Bye!");
                    break;
                }
                try {
                    logger.logln(processor.process(directive));
                } catch (DirectiveException e) {
                    logger.logln(e.getMessage());
                }
                logger.log("> ");
            }
        } catch (IOException ioe) {
            logger.logln("Unexception Exception Happened: " + ioe.getMessage());
        }
    }

    public static void main(String[] args) {
        Main main = new Main();

        if (args.length == 0 || args.length > 2) {
            usage();
            return;
        }
        if ("-i".equals(args[0].trim())) {
            logger.logln("> Type `help` to see usage information. Type `exit` to quit.");
            main.run(System.in);
        } else if ("-f".equals(args[0].trim()) && args.length == 2) {
            try {
                main.run(new FileInputStream(new File(args[1])));
            } catch (FileNotFoundException e) {
                logger.logln("Input file not found");
            }
        } else {
            usage();
        }
    }
}
