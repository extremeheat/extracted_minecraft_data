package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class RandomAccessFileManager extends OutputStreamManager {
   static final int DEFAULT_BUFFER_SIZE = 262144;
   private static final RandomAccessFileManager.RandomAccessFileManagerFactory FACTORY = new RandomAccessFileManager.RandomAccessFileManagerFactory();
   private final String advertiseURI;
   private final RandomAccessFile randomAccessFile;
   private final ThreadLocal<Boolean> isEndOfBatch = new ThreadLocal();

   protected RandomAccessFileManager(LoggerContext var1, RandomAccessFile var2, String var3, OutputStream var4, int var5, String var6, Layout<? extends Serializable> var7, boolean var8) {
      super(var1, var4, var3, false, var7, var8, ByteBuffer.wrap(new byte[var5]));
      this.randomAccessFile = var2;
      this.advertiseURI = var6;
      this.isEndOfBatch.set(Boolean.FALSE);
   }

   public static RandomAccessFileManager getFileManager(String var0, boolean var1, boolean var2, int var3, String var4, Layout<? extends Serializable> var5, Configuration var6) {
      return (RandomAccessFileManager)getManager(var0, new RandomAccessFileManager.FactoryData(var1, var2, var3, var4, var5, var6), FACTORY);
   }

   public Boolean isEndOfBatch() {
      return (Boolean)this.isEndOfBatch.get();
   }

   public void setEndOfBatch(boolean var1) {
      this.isEndOfBatch.set(var1);
   }

   protected void writeToDestination(byte[] var1, int var2, int var3) {
      try {
         this.randomAccessFile.write(var1, var2, var3);
      } catch (IOException var6) {
         String var5 = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(var5, var6);
      }
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

   public String getFileName() {
      return this.getName();
   }

   public int getBufferSize() {
      return this.byteBuffer.capacity();
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap(super.getContentFormat());
      var1.put("fileURI", this.advertiseURI);
      return var1;
   }

   private static class RandomAccessFileManagerFactory implements ManagerFactory<RandomAccessFileManager, RandomAccessFileManager.FactoryData> {
      private RandomAccessFileManagerFactory() {
         super();
      }

      public RandomAccessFileManager createManager(String var1, RandomAccessFileManager.FactoryData var2) {
         File var3 = new File(var1);
         if (!var2.append) {
            var3.delete();
         }

         boolean var4 = !var2.append || !var3.exists();
         NullOutputStream var5 = NullOutputStream.getInstance();

         try {
            FileUtils.makeParentDirs(var3);
            RandomAccessFile var6 = new RandomAccessFile(var1, "rw");
            if (var2.append) {
               var6.seek(var6.length());
            } else {
               var6.setLength(0L);
            }

            return new RandomAccessFileManager(var2.getLoggerContext(), var6, var1, var5, var2.bufferSize, var2.advertiseURI, var2.layout, var4);
         } catch (Exception var8) {
            AbstractManager.LOGGER.error((String)("RandomAccessFileManager (" + var1 + ") " + var8), (Throwable)var8);
            return null;
         }
      }

      // $FF: synthetic method
      RandomAccessFileManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final boolean append;
      private final boolean immediateFlush;
      private final int bufferSize;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;

      public FactoryData(boolean var1, boolean var2, int var3, String var4, Layout<? extends Serializable> var5, Configuration var6) {
         super(var6);
         this.append = var1;
         this.immediateFlush = var2;
         this.bufferSize = var3;
         this.advertiseURI = var4;
         this.layout = var5;
      }
   }
}
