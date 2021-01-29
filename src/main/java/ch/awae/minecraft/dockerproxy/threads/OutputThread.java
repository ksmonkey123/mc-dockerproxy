package ch.awae.minecraft.dockerproxy.threads;

import ch.awae.minecraft.dockerproxy.Log;
import ch.awae.minecraft.dockerproxy.api.WatchdogTimer;
import ch.awae.minecraft.dockerproxy.chat.LogStatementProcessor;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OutputThread extends Thread {

    private final BufferedReader reader;
    private final WatchdogTimer watchdog;
    private final LogStatementProcessor processor;
    private final boolean isError;

    public OutputThread(InputStream stream, WatchdogTimer watchdog, LogStatementProcessor processor, boolean isError) {
        reader = new BufferedReader(new InputStreamReader(stream));
        this.watchdog = watchdog;
        this.processor = processor;
        this.isError = isError;
    }

    @Override
    public void run() {
        Log.proxy("output thread started");
        String line = null;
        try {
            while ((line = reader.readLine()) != null && !Thread.interrupted()) {
                Log.server(line);
                watchdog.refresh();
                if (processor != null) {
                    processor.process(line, isError);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.proxy("output thread terminated");
    }

}
