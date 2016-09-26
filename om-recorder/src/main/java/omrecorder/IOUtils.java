package omrecorder;

import java.io.Closeable;

/**
 * Created by rayworks on 9/22/16.
 */

public final class IOUtils {
    /***
     * Closes the target closable resource.
     *
     * @param closeable {@link Closeable}
     */
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
