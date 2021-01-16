package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.util.EventListener;
import org.apache.logging.log4j.Level;

public interface StatusListener extends Closeable, EventListener {
   void log(StatusData var1);

   Level getStatusLevel();
}
