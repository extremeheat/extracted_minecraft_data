package it.unimi.dsi.fastutil.io;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;

public class FastBufferedOutputStream extends MeasurableOutputStream implements RepositionableStream {
   private static final boolean ASSERTS = false;
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   protected byte[] buffer;
   protected int pos;
   protected int avail;
   protected OutputStream os;
   private FileChannel fileChannel;
   private RepositionableStream repositionableStream;
   private MeasurableStream measurableStream;

   private static int ensureBufferSize(int var0) {
      if (var0 <= 0) {
         throw new IllegalArgumentException("Illegal buffer size: " + var0);
      } else {
         return var0;
      }
   }

   public FastBufferedOutputStream(OutputStream var1, byte[] var2) {
      super();
      this.os = var1;
      ensureBufferSize(var2.length);
      this.buffer = var2;
      this.avail = var2.length;
      if (var1 instanceof RepositionableStream) {
         this.repositionableStream = (RepositionableStream)var1;
      }

      if (var1 instanceof MeasurableStream) {
         this.measurableStream = (MeasurableStream)var1;
      }

      if (this.repositionableStream == null) {
         try {
            this.fileChannel = (FileChannel)var1.getClass().getMethod("getChannel").invoke(var1);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         } catch (NoSuchMethodException var6) {
         } catch (InvocationTargetException var7) {
         } catch (ClassCastException var8) {
         }
      }

   }

   public FastBufferedOutputStream(OutputStream var1, int var2) {
      this(var1, new byte[ensureBufferSize(var2)]);
   }

   public FastBufferedOutputStream(OutputStream var1) {
      this(var1, 8192);
   }

   private void dumpBuffer(boolean var1) throws IOException {
      if (!var1 || this.avail == 0) {
         this.os.write(this.buffer, 0, this.pos);
         this.pos = 0;
         this.avail = this.buffer.length;
      }

   }

   public void write(int var1) throws IOException {
      --this.avail;
      this.buffer[this.pos++] = (byte)var1;
      this.dumpBuffer(true);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 >= this.buffer.length) {
         this.dumpBuffer(false);
         this.os.write(var1, var2, var3);
      } else if (var3 <= this.avail) {
         System.arraycopy(var1, var2, this.buffer, this.pos, var3);
         this.pos += var3;
         this.avail -= var3;
         this.dumpBuffer(true);
      } else {
         this.dumpBuffer(false);
         System.arraycopy(var1, var2, this.buffer, 0, var3);
         this.pos = var3;
         this.avail -= var3;
      }
   }

   public void flush() throws IOException {
      this.dumpBuffer(false);
      this.os.flush();
   }

   public void close() throws IOException {
      if (this.os != null) {
         this.flush();
         if (this.os != System.out) {
            this.os.close();
         }

         this.os = null;
         this.buffer = null;
      }
   }

   public long position() throws IOException {
      if (this.repositionableStream != null) {
         return this.repositionableStream.position() + (long)this.pos;
      } else if (this.measurableStream != null) {
         return this.measurableStream.position() + (long)this.pos;
      } else if (this.fileChannel != null) {
         return this.fileChannel.position() + (long)this.pos;
      } else {
         throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the MeasurableStream or RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
      }
   }

   public void position(long var1) throws IOException {
      this.flush();
      if (this.repositionableStream != null) {
         this.repositionableStream.position(var1);
      } else {
         if (this.fileChannel == null) {
            throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
         }

         this.fileChannel.position(var1);
      }

   }

   public long length() throws IOException {
      this.flush();
      if (this.measurableStream != null) {
         return this.measurableStream.length();
      } else if (this.fileChannel != null) {
         return this.fileChannel.size();
      } else {
         throw new UnsupportedOperationException();
      }
   }
}
