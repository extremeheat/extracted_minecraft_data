package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.util.Constants;

public class OutputStreamManager extends AbstractManager implements ByteBufferDestination {
   protected final Layout<?> layout;
   protected ByteBuffer byteBuffer;
   private volatile OutputStream os;
   private boolean skipFooter;

   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4) {
      this(var1, var2, var3, var4, Constants.ENCODER_BYTE_BUFFER_SIZE);
   }

   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4, int var5) {
      this(var1, var2, var3, var4, ByteBuffer.wrap(new byte[var5]));
   }

   /** @deprecated */
   @Deprecated
   protected OutputStreamManager(OutputStream var1, String var2, Layout<?> var3, boolean var4, ByteBuffer var5) {
      super((LoggerContext)null, var2);
      this.os = var1;
      this.layout = var3;
      if (var4 && var3 != null) {
         byte[] var6 = var3.getHeader();
         if (var6 != null) {
            try {
               this.getOutputStream().write(var6, 0, var6.length);
            } catch (IOException var8) {
               this.logError("Unable to write header", var8);
            }
         }
      }

      this.byteBuffer = (ByteBuffer)Objects.requireNonNull(var5, "byteBuffer");
   }

   protected OutputStreamManager(LoggerContext var1, OutputStream var2, String var3, boolean var4, Layout<? extends Serializable> var5, boolean var6, ByteBuffer var7) {
      super(var1, var3);
      if (var4 && var2 != null) {
         LOGGER.error((String)"Invalid OutputStreamManager configuration for '{}': You cannot both set the OutputStream and request on-demand.", (Object)var3);
      }

      this.layout = var5;
      this.byteBuffer = (ByteBuffer)Objects.requireNonNull(var7, "byteBuffer");
      this.os = var2;
      if (var6 && var5 != null) {
         byte[] var8 = var5.getHeader();
         if (var8 != null) {
            try {
               this.getOutputStream().write(var8, 0, var8.length);
            } catch (IOException var10) {
               this.logError("Unable to write header for " + var3, var10);
            }
         }
      }

   }

   public static <T> OutputStreamManager getManager(String var0, T var1, ManagerFactory<? extends OutputStreamManager, T> var2) {
      return (OutputStreamManager)AbstractManager.getManager(var0, var2, var1);
   }

   protected OutputStream createOutputStream() throws IOException {
      throw new IllegalStateException(this.getClass().getCanonicalName() + " must implement createOutputStream()");
   }

   public void skipFooter(boolean var1) {
      this.skipFooter = var1;
   }

   public boolean releaseSub(long var1, TimeUnit var3) {
      this.writeFooter();
      return this.closeOutputStream();
   }

   protected void writeFooter() {
      if (this.layout != null && !this.skipFooter) {
         byte[] var1 = this.layout.getFooter();
         if (var1 != null) {
            this.write(var1);
         }

      }
   }

   public boolean isOpen() {
      return this.getCount() > 0;
   }

   public boolean hasOutputStream() {
      return this.os != null;
   }

   protected OutputStream getOutputStream() throws IOException {
      if (this.os == null) {
         this.os = this.createOutputStream();
      }

      return this.os;
   }

   protected void setOutputStream(OutputStream var1) {
      byte[] var2 = this.layout.getHeader();
      if (var2 != null) {
         try {
            var1.write(var2, 0, var2.length);
            this.os = var1;
         } catch (IOException var4) {
            this.logError("Unable to write header", var4);
         }
      } else {
         this.os = var1;
      }

   }

   protected void write(byte[] var1) {
      this.write(var1, 0, var1.length, false);
   }

   protected void write(byte[] var1, boolean var2) {
      this.write(var1, 0, var1.length, var2);
   }

   protected void write(byte[] var1, int var2, int var3) {
      this.write(var1, var2, var3, false);
   }

   protected synchronized void write(byte[] var1, int var2, int var3, boolean var4) {
      if (var4 && this.byteBuffer.position() == 0) {
         this.writeToDestination(var1, var2, var3);
         this.flushDestination();
      } else {
         if (var3 >= this.byteBuffer.capacity()) {
            this.flush();
            this.writeToDestination(var1, var2, var3);
         } else {
            if (var3 > this.byteBuffer.remaining()) {
               this.flush();
            }

            this.byteBuffer.put(var1, var2, var3);
         }

         if (var4) {
            this.flush();
         }

      }
   }

   protected synchronized void writeToDestination(byte[] var1, int var2, int var3) {
      try {
         this.getOutputStream().write(var1, var2, var3);
      } catch (IOException var5) {
         throw new AppenderLoggingException("Error writing to stream " + this.getName(), var5);
      }
   }

   protected synchronized void flushDestination() {
      OutputStream var1 = this.os;
      if (var1 != null) {
         try {
            var1.flush();
         } catch (IOException var3) {
            throw new AppenderLoggingException("Error flushing stream " + this.getName(), var3);
         }
      }

   }

   protected synchronized void flushBuffer(ByteBuffer var1) {
      var1.flip();
      if (var1.limit() > 0) {
         this.writeToDestination(var1.array(), 0, var1.limit());
      }

      var1.clear();
   }

   public synchronized void flush() {
      this.flushBuffer(this.byteBuffer);
      this.flushDestination();
   }

   protected synchronized boolean closeOutputStream() {
      this.flush();
      OutputStream var1 = this.os;
      if (var1 != null && var1 != System.out && var1 != System.err) {
         try {
            var1.close();
            return true;
         } catch (IOException var3) {
            this.logError("Unable to close stream", var3);
            return false;
         }
      } else {
         return true;
      }
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public ByteBuffer drain(ByteBuffer var1) {
      this.flushBuffer(var1);
      return var1;
   }
}
