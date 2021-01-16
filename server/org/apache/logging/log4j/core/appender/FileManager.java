package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.FileUtils;

public class FileManager extends OutputStreamManager {
   private static final FileManager.FileManagerFactory FACTORY = new FileManager.FileManagerFactory();
   private final boolean isAppend;
   private final boolean createOnDemand;
   private final boolean isLocking;
   private final String advertiseURI;
   private final int bufferSize;

   /** @deprecated */
   @Deprecated
   protected FileManager(String var1, OutputStream var2, boolean var3, boolean var4, String var5, Layout<? extends Serializable> var6, int var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var8, ByteBuffer.wrap(new byte[var7]));
   }

   /** @deprecated */
   @Deprecated
   protected FileManager(String var1, OutputStream var2, boolean var3, boolean var4, String var5, Layout<? extends Serializable> var6, boolean var7, ByteBuffer var8) {
      super(var2, var1, var6, var7, var8);
      this.isAppend = var3;
      this.createOnDemand = false;
      this.isLocking = var4;
      this.advertiseURI = var5;
      this.bufferSize = var8.capacity();
   }

   protected FileManager(LoggerContext var1, String var2, OutputStream var3, boolean var4, boolean var5, boolean var6, String var7, Layout<? extends Serializable> var8, boolean var9, ByteBuffer var10) {
      super(var1, var3, var2, var6, var8, var9, var10);
      this.isAppend = var4;
      this.createOnDemand = var6;
      this.isLocking = var5;
      this.advertiseURI = var7;
      this.bufferSize = var10.capacity();
   }

   public static FileManager getFileManager(String var0, boolean var1, boolean var2, boolean var3, boolean var4, String var5, Layout<? extends Serializable> var6, int var7, Configuration var8) {
      if (var2 && var3) {
         var2 = false;
      }

      return (FileManager)getManager(var0, new FileManager.FactoryData(var1, var2, var3, var7, var4, var5, var6, var8), FACTORY);
   }

   protected OutputStream createOutputStream() throws FileNotFoundException {
      String var1 = this.getFileName();
      LOGGER.debug((String)"Now writing to {} at {}", (Object)var1, (Object)(new Date()));
      return new FileOutputStream(var1, this.isAppend);
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      if (this.isLocking) {
         try {
            FileChannel var5 = ((FileOutputStream)this.getOutputStream()).getChannel();
            FileLock var6 = var5.lock(0L, 9223372036854775807L, false);
            Throwable var7 = null;

            try {
               super.write(var1, var2, var3, var4);
            } catch (Throwable var17) {
               var7 = var17;
               throw var17;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (IOException var19) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var19);
         }
      } else {
         super.write(var1, var2, var3, var4);
      }

   }

   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      if (this.isLocking) {
         try {
            FileChannel var4 = ((FileOutputStream)this.getOutputStream()).getChannel();
            FileLock var5 = var4.lock(0L, 9223372036854775807L, false);
            Throwable var6 = null;

            try {
               super.writeToDestination(var1, var2, var3);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (IOException var18) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var18);
         }
      } else {
         super.writeToDestination(var1, var2, var3);
      }

   }

   public String getFileName() {
      return this.getName();
   }

   public boolean isAppend() {
      return this.isAppend;
   }

   public boolean isCreateOnDemand() {
      return this.createOnDemand;
   }

   public boolean isLocking() {
      return this.isLocking;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap(super.getContentFormat());
      var1.put("fileURI", this.advertiseURI);
      return var1;
   }

   private static class FileManagerFactory implements ManagerFactory<FileManager, FileManager.FactoryData> {
      private FileManagerFactory() {
         super();
      }

      public FileManager createManager(String var1, FileManager.FactoryData var2) {
         File var3 = new File(var1);

         try {
            FileUtils.makeParentDirs(var3);
            boolean var4 = !var2.append || !var3.exists();
            int var5 = var2.bufferedIo ? var2.bufferSize : Constants.ENCODER_BYTE_BUFFER_SIZE;
            ByteBuffer var6 = ByteBuffer.wrap(new byte[var5]);
            FileOutputStream var7 = var2.createOnDemand ? null : new FileOutputStream(var3, var2.append);
            return new FileManager(var2.getLoggerContext(), var1, var7, var2.append, var2.locking, var2.createOnDemand, var2.advertiseURI, var2.layout, var4, var6);
         } catch (IOException var8) {
            AbstractManager.LOGGER.error((String)("FileManager (" + var1 + ") " + var8), (Throwable)var8);
            return null;
         }
      }

      // $FF: synthetic method
      FileManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData extends ConfigurationFactoryData {
      private final boolean append;
      private final boolean locking;
      private final boolean bufferedIo;
      private final int bufferSize;
      private final boolean createOnDemand;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;

      public FactoryData(boolean var1, boolean var2, boolean var3, int var4, boolean var5, String var6, Layout<? extends Serializable> var7, Configuration var8) {
         super(var8);
         this.append = var1;
         this.locking = var2;
         this.bufferedIo = var3;
         this.bufferSize = var4;
         this.createOnDemand = var5;
         this.advertiseURI = var6;
         this.layout = var7;
      }
   }
}
