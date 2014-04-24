package org.shizhz;

import java.io.IOException;

import org.junit.Test;

public class MainTest {
    private static final boolean TEST_THIS = false;

    @Test
    public void testIllegalArgument() {
        Main.main(new String[] {});
        Main.main(new String[] { "-f" });
        Main.main(new String[] { "-f -f" });
        Main.main(new String[] { "-i k" });
        Main.main(new String[] { "-i k -f file.txt" });
    }

    @Test
    public void testMainWithClasspathF() {
        Main.main(new String[] { "-f", "src/main/resources/inputs.txt" });
    }

    @Test
    public void testMainWithFile() throws IOException {
        if (TEST_THIS) {
            Main.main(new String[] { "-f",
                    "/home/shizz/projects/train/src/main/resources/inputs.txt" });
        }
    }

    @Test
    public void testMainWithI() {
        if (TEST_THIS) {
            Main.main(new String[] { "-i" });
        }
    }
}
