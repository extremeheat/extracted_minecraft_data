package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class RollingRandomAccessFileManager extends RollingFileManager {
   public static final int DEFAULT_BUFFER_SIZE = 262144;
   private static final RollingRandomAccessFileManager.RollingRandomAccessFileManagerFactory FACTORY = new RollingRandomAccessFileManager.RollingRandomAccessFileManagerFactory();
   private RandomAccessFile randomAccessFile;
   private final ThreadLocal<Boolean> isEndOfBatch = new ThreadLocal();

   public RollingRandomAccessFileManager(LoggerContext var1, RandomAccessFile var2, String var3, String var4, OutputStream var5, boolean var6, boolean var7, int var8, long var9, long var11, TriggeringPolicy var13, RolloverStrategy var14, String var15, Layout<? extends Serializable> var16, boolean var17) {
      super(var1, var3, var4, var5, var6, false, var9, var11, var13, var14, var15, var16, var17, ByteBuffer.wrap(new byte[var8]));
      this.randomAccessFile = var2;
      this.isEndOfBatch.set(Boolean.FALSE);
      this.writeHeader();
   }

   private void writeHeader() {
      if (this.layout != null) {
         byte[] var1 = this.layout.getHeader();
         if (var1 != null) {
            try {
               if (this.randomAccessFile.length() == 0L) {
                  this.randomAccessFile.write(var1, 0, var1.length);
               }
            } catch (IOException var3) {
               this.logError("Unable to write header", var3);
            }

         }
      }
   }

   public static RollingRandomAccessFileManager getRollingRandomAccessFileManager(String var0, String var1, boolean var2, boolean var3, int var4, TriggeringPolicy var5, RolloverStrategy var6, String var7, Layout<? extends Serializable> var8, Configuration var9) {
      return (RollingRandomAccessFileManager)getManager(var0, new RollingRandomAccessFileManager.FactoryData(var1, var2, var3, var4, var5, var6, var7, var8, var9), FACTORY);
   }

   public Boolean isEndOfBatch() {
      return (Boolean)this.isEndOfBatch.get();
   }

   public void setEndOfBatch(boolean var1) {
      this.isEndOfBatch.set(var1);
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      super.write(var1, var2, var3, var4);
   }

   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      try {
         this.randomAccessFile.write(var1, var2, var3);
         this.size += (long)var3;
      } catch (IOException var6) {
         String var5 = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(var5, var6);
      }
   }

   protected void createFileAfterRollover() throws IOException {
      this.randomAccessFile = new RandomAccessFile(this.getFileName(), "rw");
      if (this.isAppend()) {
         this.randomAccessFile.seek(this.randomAccessFile.length());
      }

      this.writeHeader();
   }

   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
   }

   public synchronized boolean closeOutputStream() {
      this.flush();

      try {
         this.randomAccessFile.close();
         return true;
      } catch (IOException var2) {
         this.logError("Unable to close RandomAccessFile", var2);
         return false;
      }
   }

   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   public void updateData(Object var1) {
      RollingRandomAccessFileManager.FactoryData var2 = (RollingRandomAccessFileManager.FactoryData)var1;
      this.setRolloverStrategy(var2.getRolloverStrategy());
      this.setTriggeringPolicy(var2.getTriggeringPolicy());
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final String pattern;
      private final boolean append;
      private final boolean immediateFlush;
      private final int bufferSize;
      private final TriggeringPolicy policy;
      private final RolloverStrategy strategy;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;

      public FactoryData(String var1, boolean var2, boolean var3, int var4, TriggeringPolicy var5, RolloverStrategy var6, String var7, Layout<? extends Serializable> var8, Configuration var9) {
         super(var9);
         this.pattern = var1;
         this.append = var2;
         this.immediateFlush = var3;
         this.bufferSize = var4;
         this.policy = var5;
         this.strategy = var6;
         this.advertiseURI = var7;
         this.layout = var8;
      }

      public TriggeringPolicy getTriggeringPolicy() {
         return this.policy;
      }

      public RolloverStrategy getRolloverStrategy() {
         return this.strategy;
      }
   }

   private static class RollingRandomAccessFileManagerFactory implements ManagerFactory<RollingRandomAccessFileManager, RollingRandomAccessFileManager.FactoryData> {
      private RollingRandomAccessFileManagerFactory() {
         super();
      }

      public RollingRandomAccessFileManager createManager(String var1, RollingRandomAccessFileManager.FactoryData var2) {
         File var3 = new File(var1);
         if (!var2.append) {
            var3.delete();
         }

         long var4 = var2.append ? var3.length() : 0L;
         long var6 = var3.exists() ? var3.lastModified() : System.currentTimeMillis();
         boolean var8 = !var2.append || !var3.exists();
         RandomAccessFile var9 = null;

         try {
            FileUtils.makeParentDirs(var3);
            var9 = new RandomAccessFile(var1, "rw");
            if (var2.append) {
               long var10 = var9.length();
               RollingRandomAccessFileManager.LOGGER.trace((String)"RandomAccessFile {} seek to {}", (Object)var1, (Object)var10);
               var9.seek(var10);
            } else {
               RollingRandomAccessFileManager.LOGGER.trace((String)"RandomAccessFile {} set length to 0", (Object)var1);
               var9.setLength(0L);
            }

            return new RollingRandomAccessFileManager(var2.getLoggerContext(), var9, var1, var2.pattern, NullOutputStream.getInstance(), var2.append, var2.immediateFlush, var2.bufferSize, var4, var6, var2.policy, var2.strategy, var2.advertiseURI, var2.layout, var8);
         } catch (IOException var13) {
            RollingRandomAccessFileManager.LOGGER.error((String)("Cannot access RandomAccessFile " + var13), (Throwable)var13);
            if (var9 != null) {
               try {
                  var9.close();
               } catch (IOException var12) {
                  RollingRandomAccessFileManager.LOGGER.error((String)"Cannot close RandomAccessFile {}", (Object)var1, (Object)var12);
               }
            }

            return null;
         }
      }

      // $FF: synthetic method
      RollingRandomAccessFileManagerFactory(Object var1) {
         this();
      }
   }
}
