package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NullOutputStream;

public class MemoryMappedFileManager extends OutputStreamManager {
   static final int DEFAULT_REGION_LENGTH = 33554432;
   private static final int MAX_REMAP_COUNT = 10;
   private static final MemoryMappedFileManager.MemoryMappedFileManagerFactory FACTORY = new MemoryMappedFileManager.MemoryMappedFileManagerFactory();
   private static final double NANOS_PER_MILLISEC = 1000000.0D;
   private final boolean immediateFlush;
   private final int regionLength;
   private final String advertiseURI;
   private final RandomAccessFile randomAccessFile;
   private final ThreadLocal<Boolean> isEndOfBatch = new ThreadLocal();
   private MappedByteBuffer mappedBuffer;
   private long mappingOffset;

   protected MemoryMappedFileManager(RandomAccessFile var1, String var2, OutputStream var3, boolean var4, long var5, int var7, String var8, Layout<? extends Serializable> var9, boolean var10) throws IOException {
      super(var3, var2, var9, var10, ByteBuffer.wrap(new byte[0]));
      this.immediateFlush = var4;
      this.randomAccessFile = (RandomAccessFile)Objects.requireNonNull(var1, "RandomAccessFile");
      this.regionLength = var7;
      this.advertiseURI = var8;
      this.isEndOfBatch.set(Boolean.FALSE);
      this.mappedBuffer = mmap(this.randomAccessFile.getChannel(), this.getFileName(), var5, var7);
      this.byteBuffer = this.mappedBuffer;
      this.mappingOffset = var5;
   }

   public static MemoryMappedFileManager getFileManager(String var0, boolean var1, boolean var2, int var3, String var4, Layout<? extends Serializable> var5) {
      return (MemoryMappedFileManager)getManager(var0, new MemoryMappedFileManager.FactoryData(var1, var2, var3, var4, var5), FACTORY);
   }

   public Boolean isEndOfBatch() {
      return (Boolean)this.isEndOfBatch.get();
   }

   public void setEndOfBatch(boolean var1) {
      this.isEndOfBatch.set(var1);
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      while(var3 > this.mappedBuffer.remaining()) {
         int var5 = this.mappedBuffer.remaining();
         this.mappedBuffer.put(var1, var2, var5);
         var2 += var5;
         var3 -= var5;
         this.remap();
      }

      this.mappedBuffer.put(var1, var2, var3);
   }

   private synchronized void remap() {
      long var1 = this.mappingOffset + (long)this.mappedBuffer.position();
      int var3 = this.mappedBuffer.remaining() + this.regionLength;

      try {
         unsafeUnmap(this.mappedBuffer);
         long var4 = this.randomAccessFile.length() + (long)this.regionLength;
         LOGGER.debug((String)"{} {} extending {} by {} bytes to {}", (Object)this.getClass().getSimpleName(), this.getName(), this.getFileName(), this.regionLength, var4);
         long var6 = System.nanoTime();
         this.randomAccessFile.setLength(var4);
         float var8 = (float)((double)(System.nanoTime() - var6) / 1000000.0D);
         LOGGER.debug((String)"{} {} extended {} OK in {} millis", (Object)this.getClass().getSimpleName(), this.getName(), this.getFileName(), var8);
         this.mappedBuffer = mmap(this.randomAccessFile.getChannel(), this.getFileName(), var1, var3);
         this.byteBuffer = this.mappedBuffer;
         this.mappingOffset = var1;
      } catch (Exception var9) {
         this.logError("Unable to remap", var9);
      }

   }

   public synchronized void flush() {
      this.mappedBuffer.force();
   }

   public synchronized boolean closeOutputStream() {
      long var1 = (long)this.mappedBuffer.position();
      long var3 = this.mappingOffset + var1;

      try {
         unsafeUnmap(this.mappedBuffer);
      } catch (Exception var7) {
         this.logError("Unable to unmap MappedBuffer", var7);
      }

      try {
         LOGGER.debug((String)"MMapAppender closing. Setting {} length to {} (offset {} + position {})", (Object)this.getFileName(), var3, this.mappingOffset, var1);
         this.randomAccessFile.setLength(var3);
         this.randomAccessFile.close();
         return true;
      } catch (IOException var6) {
         this.logError("Unable to close MemoryMappedFile", var6);
         return false;
      }
   }

   public static MappedByteBuffer mmap(FileChannel var0, String var1, long var2, int var4) throws IOException {
      int var5 = 1;

      while(true) {
         try {
            LOGGER.debug((String)"MMapAppender remapping {} start={}, size={}", (Object)var1, var2, var4);
            long var6 = System.nanoTime();
            MappedByteBuffer var8 = var0.map(MapMode.READ_WRITE, var2, (long)var4);
            var8.order(ByteOrder.nativeOrder());
            float var9 = (float)((double)(System.nanoTime() - var6) / 1000000.0D);
            LOGGER.debug((String)"MMapAppender remapped {} OK in {} millis", (Object)var1, (Object)var9);
            return var8;
         } catch (IOException var11) {
            if (var11.getMessage() == null || !var11.getMessage().endsWith("user-mapped section open")) {
               throw var11;
            }

            LOGGER.debug((String)"Remap attempt {}/{} failed. Retrying...", (Object)var5, 10, var11);
            if (var5 < 10) {
               Thread.yield();
            } else {
               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var10) {
                  Thread.currentThread().interrupt();
                  throw var11;
               }
            }

            ++var5;
         }
      }
   }

   private static void unsafeUnmap(final MappedByteBuffer var0) throws PrivilegedActionException {
      LOGGER.debug("MMapAppender unmapping old buffer...");
      long var1 = System.nanoTime();
      AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
         public Object run() throws Exception {
            Method var1 = var0.getClass().getMethod("cleaner");
            var1.setAccessible(true);
            Object var2 = var1.invoke(var0);
            Method var3 = var2.getClass().getMethod("clean");
            var3.invoke(var2);
            return null;
         }
      });
      float var3 = (float)((double)(System.nanoTime() - var1) / 1000000.0D);
      LOGGER.debug((String)"MMapAppender unmapped buffer OK in {} millis", (Object)var3);
   }

   public String getFileName() {
      return this.getName();
   }

   public int getRegionLength() {
      return this.regionLength;
   }

   public boolean isImmediateFlush() {
      return this.immediateFlush;
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap(super.getContentFormat());
      var1.put("fileURI", this.advertiseURI);
      return var1;
   }

   protected void flushBuffer(ByteBuffer var1) {
   }

   public ByteBuffer getByteBuffer() {
      return this.mappedBuffer;
   }

   public ByteBuffer drain(ByteBuffer var1) {
      this.remap();
      return this.mappedBuffer;
   }

   private static class MemoryMappedFileManagerFactory implements ManagerFactory<MemoryMappedFileManager, MemoryMappedFileManager.FactoryData> {
      private MemoryMappedFileManagerFactory() {
         super();
      }

      public MemoryMappedFileManager createManager(String var1, MemoryMappedFileManager.FactoryData var2) {
         File var3 = new File(var1);
         if (!var2.append) {
            var3.delete();
         }

         boolean var4 = !var2.append || !var3.exists();
         NullOutputStream var5 = NullOutputStream.getInstance();
         RandomAccessFile var6 = null;

         try {
            FileUtils.makeParentDirs(var3);
            var6 = new RandomAccessFile(var1, "rw");
            long var7 = var2.append ? var6.length() : 0L;
            var6.setLength(var7 + (long)var2.regionLength);
            return new MemoryMappedFileManager(var6, var1, var5, var2.immediateFlush, var7, var2.regionLength, var2.advertiseURI, var2.layout, var4);
         } catch (Exception var9) {
            AbstractManager.LOGGER.error((String)("MemoryMappedFileManager (" + var1 + ") " + var9), (Throwable)var9);
            Closer.closeSilently(var6);
            return null;
         }
      }

      // $FF: synthetic method
      MemoryMappedFileManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData {
      private final boolean append;
      private final boolean immediateFlush;
      private final int regionLength;
      private final String advertiseURI;
      private final Layout<? extends Serializable> layout;

      public FactoryData(boolean var1, boolean var2, int var3, String var4, Layout<? extends Serializable> var5) {
         super();
         this.append = var1;
         this.immediateFlush = var2;
         this.regionLength = var3;
         this.advertiseURI = var4;
         this.layout = var5;
      }
   }
}
