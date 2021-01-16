package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class InspectableFileCachedInputStream extends MeasurableInputStream implements RepositionableStream, WritableByteChannel {
   public static final boolean DEBUG = false;
   public static final int DEFAULT_BUFFER_SIZE = 65536;
   public final byte[] buffer;
   public int inspectable;
   private final File overflowFile;
   private final RandomAccessFile randomAccessFile;
   private final FileChannel fileChannel;
   private long position;
   private long mark;
   private long writePosition;

   public InspectableFileCachedInputStream(int var1, File var2) throws IOException {
      super();
      if (var1 <= 0) {
         throw new IllegalArgumentException("Illegal buffer size " + var1);
      } else {
         if (var2 != null) {
            this.overflowFile = var2;
         } else {
            (this.overflowFile = File.createTempFile(this.getClass().getSimpleName(), "overflow")).deleteOnExit();
         }

         this.buffer = new byte[var1];
         this.randomAccessFile = new RandomAccessFile(this.overflowFile, "rw");
         this.fileChannel = this.randomAccessFile.getChannel();
         this.mark = -1L;
      }
   }

   public InspectableFileCachedInputStream(int var1) throws IOException {
      this(var1, (File)null);
   }

   public InspectableFileCachedInputStream() throws IOException {
      this(65536);
   }

   private void ensureOpen() throws IOException {
      if (this.position == -1L) {
         throw new IOException("This " + this.getClass().getSimpleName() + " is closed");
      }
   }

   public void clear() throws IOException {
      if (!this.fileChannel.isOpen()) {
         throw new IOException("This " + this.getClass().getSimpleName() + " is closed");
      } else {
         this.writePosition = this.position = (long)(this.inspectable = 0);
         this.mark = -1L;
      }
   }

   public int write(ByteBuffer var1) throws IOException {
      this.ensureOpen();
      int var2 = var1.remaining();
      if (this.inspectable < this.buffer.length) {
         int var3 = Math.min(this.buffer.length - this.inspectable, var2);
         var1.get(this.buffer, this.inspectable, var3);
         this.inspectable += var3;
      }

      if (var1.hasRemaining()) {
         this.fileChannel.position(this.writePosition);
         this.writePosition += (long)this.fileChannel.write(var1);
      }

      return var2;
   }

   public void truncate(long var1) throws FileNotFoundException, IOException {
      this.fileChannel.truncate(Math.max(var1, this.writePosition));
   }

   public void close() {
      this.position = -1L;
   }

   public void reopen() throws IOException {
      if (!this.fileChannel.isOpen()) {
         throw new IOException("This " + this.getClass().getSimpleName() + " is closed");
      } else {
         this.position = 0L;
      }
   }

   public void dispose() throws IOException {
      this.position = -1L;
      this.randomAccessFile.close();
      this.overflowFile.delete();
   }

   protected void finalize() throws Throwable {
      try {
         this.dispose();
      } finally {
         super.finalize();
      }

   }

   public int available() throws IOException {
      this.ensureOpen();
      return (int)Math.min(2147483647L, this.length() - this.position);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var3 == 0) {
         return 0;
      } else if (this.position == this.length()) {
         return -1;
      } else {
         ByteArrays.ensureOffsetLength(var1, var2, var3);
         int var4 = 0;
         int var5;
         if (this.position < (long)this.inspectable) {
            var5 = Math.min(this.inspectable - (int)this.position, var3);
            System.arraycopy(this.buffer, (int)this.position, var1, var2, var5);
            var3 -= var5;
            var2 += var5;
            this.position += (long)var5;
            var4 = var5;
         }

         if (var3 > 0) {
            if (this.position == this.length()) {
               return var4 != 0 ? var4 : -1;
            }

            this.fileChannel.position(this.position - (long)this.inspectable);
            var5 = (int)Math.min(this.length() - this.position, (long)var3);
            int var6 = this.randomAccessFile.read(var1, var2, var5);
            this.position += (long)var6;
            var4 += var6;
         }

         return var4;
      }
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public long skip(long var1) throws IOException {
      this.ensureOpen();
      long var3 = Math.min(var1, this.length() - this.position);
      this.position += var3;
      return var3;
   }

   public int read() throws IOException {
      this.ensureOpen();
      if (this.position == this.length()) {
         return -1;
      } else if (this.position < (long)this.inspectable) {
         return this.buffer[(int)(this.position++)] & 255;
      } else {
         this.fileChannel.position(this.position - (long)this.inspectable);
         ++this.position;
         return this.randomAccessFile.read();
      }
   }

   public long length() throws IOException {
      this.ensureOpen();
      return (long)this.inspectable + this.writePosition;
   }

   public long position() throws IOException {
      this.ensureOpen();
      return this.position;
   }

   public void position(long var1) throws IOException {
      this.position = Math.min(var1, this.length());
   }

   public boolean isOpen() {
      return this.position != -1L;
   }

   public void mark(int var1) {
      this.mark = this.position;
   }

   public void reset() throws IOException {
      this.ensureOpen();
      if (this.mark == -1L) {
         throw new IOException("Mark has not been set");
      } else {
         this.position(this.mark);
      }
   }

   public boolean markSupported() {
      return true;
   }
}
