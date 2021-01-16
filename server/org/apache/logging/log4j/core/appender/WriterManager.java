package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.StringLayout;

public class WriterManager extends AbstractManager {
   protected final StringLayout layout;
   private volatile Writer writer;

   public static <T> WriterManager getManager(String var0, T var1, ManagerFactory<? extends WriterManager, T> var2) {
      return (WriterManager)AbstractManager.getManager(var0, var2, var1);
   }

   public WriterManager(Writer var1, String var2, StringLayout var3, boolean var4) {
      super((LoggerContext)null, var2);
      this.writer = var1;
      this.layout = var3;
      if (var4 && var3 != null) {
         byte[] var5 = var3.getHeader();
         if (var5 != null) {
            try {
               this.writer.write(new String(var5, var3.getCharset()));
            } catch (IOException var7) {
               this.logError("Unable to write header", var7);
            }
         }
      }

   }

   protected synchronized void closeWriter() {
      Writer var1 = this.writer;

      try {
         var1.close();
      } catch (IOException var3) {
         this.logError("Unable to close stream", var3);
      }

   }

   public synchronized void flush() {
      try {
         this.writer.flush();
      } catch (IOException var3) {
         String var2 = "Error flushing stream " + this.getName();
         throw new AppenderLoggingException(var2, var3);
      }
   }

   protected Writer getWriter() {
      return this.writer;
   }

   public boolean isOpen() {
      return this.getCount() > 0;
   }

   public boolean releaseSub(long var1, TimeUnit var3) {
      this.writeFooter();
      this.closeWriter();
      return true;
   }

   protected void setWriter(Writer var1) {
      byte[] var2 = this.layout.getHeader();
      if (var2 != null) {
         try {
            var1.write(new String(var2, this.layout.getCharset()));
            this.writer = var1;
         } catch (IOException var4) {
            this.logError("Unable to write header", var4);
         }
      } else {
         this.writer = var1;
      }

   }

   protected synchronized void write(String var1) {
      try {
         this.writer.write(var1);
      } catch (IOException var4) {
         String var3 = "Error writing to stream " + this.getName();
         throw new AppenderLoggingException(var3, var4);
      }
   }

   protected void writeFooter() {
      if (this.layout != null) {
         byte[] var1 = this.layout.getFooter();
         if (var1 != null && var1.length > 0) {
            this.write(new String(var1, this.layout.getCharset()));
         }

      }
   }
}
