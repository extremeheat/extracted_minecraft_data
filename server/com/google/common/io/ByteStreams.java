package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

@Beta
@GwtIncompatible
public final class ByteStreams {
   private static final int ZERO_COPY_CHUNK_SIZE = 524288;
   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
      public void write(int var1) {
      }

      public void write(byte[] var1) {
         Preconditions.checkNotNull(var1);
      }

      public void write(byte[] var1, int var2, int var3) {
         Preconditions.checkNotNull(var1);
      }

      public String toString() {
         return "ByteStreams.nullOutputStream()";
      }
   };

   static byte[] createBuffer() {
      return new byte[8192];
   }

   private ByteStreams() {
      super();
   }

   @CanIgnoreReturnValue
   public static long copy(InputStream var0, OutputStream var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      byte[] var2 = createBuffer();
      long var3 = 0L;

      while(true) {
         int var5 = var0.read(var2);
         if (var5 == -1) {
            return var3;
         }

         var1.write(var2, 0, var5);
         var3 += (long)var5;
      }
   }

   @CanIgnoreReturnValue
   public static long copy(ReadableByteChannel var0, WritableByteChannel var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      long var3;
      if (var0 instanceof FileChannel) {
         FileChannel var9 = (FileChannel)var0;
         var3 = var9.position();
         long var5 = var3;

         long var7;
         do {
            do {
               var7 = var9.transferTo(var5, 524288L, var1);
               var5 += var7;
               var9.position(var5);
            } while(var7 > 0L);
         } while(var5 < var9.size());

         return var5 - var3;
      } else {
         ByteBuffer var2 = ByteBuffer.wrap(createBuffer());
         var3 = 0L;

         while(var0.read(var2) != -1) {
            var2.flip();

            while(var2.hasRemaining()) {
               var3 += (long)var1.write(var2);
            }

            var2.clear();
         }

         return var3;
      }
   }

   public static byte[] toByteArray(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(Math.max(32, var0.available()));
      copy((InputStream)var0, (OutputStream)var1);
      return var1.toByteArray();
   }

   static byte[] toByteArray(InputStream var0, int var1) throws IOException {
      byte[] var2 = new byte[var1];

      int var4;
      int var5;
      for(int var3 = var1; var3 > 0; var3 -= var5) {
         var4 = var1 - var3;
         var5 = var0.read(var2, var4, var3);
         if (var5 == -1) {
            return Arrays.copyOf(var2, var4);
         }
      }

      var4 = var0.read();
      if (var4 == -1) {
         return var2;
      } else {
         ByteStreams.FastByteArrayOutputStream var7 = new ByteStreams.FastByteArrayOutputStream();
         var7.write(var4);
         copy((InputStream)var0, (OutputStream)var7);
         byte[] var6 = new byte[var2.length + var7.size()];
         System.arraycopy(var2, 0, var6, 0, var2.length);
         var7.writeTo(var6, var2.length);
         return var6;
      }
   }

   @CanIgnoreReturnValue
   public static long exhaust(InputStream var0) throws IOException {
      long var1 = 0L;

      long var3;
      for(byte[] var5 = createBuffer(); (var3 = (long)var0.read(var5)) != -1L; var1 += var3) {
      }

      return var1;
   }

   public static ByteArrayDataInput newDataInput(byte[] var0) {
      return newDataInput(new ByteArrayInputStream(var0));
   }

   public static ByteArrayDataInput newDataInput(byte[] var0, int var1) {
      Preconditions.checkPositionIndex(var1, var0.length);
      return newDataInput(new ByteArrayInputStream(var0, var1, var0.length - var1));
   }

   public static ByteArrayDataInput newDataInput(ByteArrayInputStream var0) {
      return new ByteStreams.ByteArrayDataInputStream((ByteArrayInputStream)Preconditions.checkNotNull(var0));
   }

   public static ByteArrayDataOutput newDataOutput() {
      return newDataOutput(new ByteArrayOutputStream());
   }

   public static ByteArrayDataOutput newDataOutput(int var0) {
      if (var0 < 0) {
         throw new IllegalArgumentException(String.format("Invalid size: %s", var0));
      } else {
         return newDataOutput(new ByteArrayOutputStream(var0));
      }
   }

   public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream var0) {
      return new ByteStreams.ByteArrayDataOutputStream((ByteArrayOutputStream)Preconditions.checkNotNull(var0));
   }

   public static OutputStream nullOutputStream() {
      return NULL_OUTPUT_STREAM;
   }

   public static InputStream limit(InputStream var0, long var1) {
      return new ByteStreams.LimitedInputStream(var0, var1);
   }

   public static void readFully(InputStream var0, byte[] var1) throws IOException {
      readFully(var0, var1, 0, var1.length);
   }

   public static void readFully(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      int var4 = read(var0, var1, var2, var3);
      if (var4 != var3) {
         throw new EOFException("reached end of stream after reading " + var4 + " bytes; " + var3 + " bytes expected");
      }
   }

   public static void skipFully(InputStream var0, long var1) throws IOException {
      long var3 = skipUpTo(var0, var1);
      if (var3 < var1) {
         throw new EOFException("reached end of stream after skipping " + var3 + " bytes; " + var1 + " bytes expected");
      }
   }

   static long skipUpTo(InputStream var0, long var1) throws IOException {
      long var3 = 0L;

      long var8;
      for(byte[] var5 = createBuffer(); var3 < var1; var3 += var8) {
         long var6 = var1 - var3;
         var8 = skipSafely(var0, var6);
         if (var8 == 0L) {
            int var10 = (int)Math.min(var6, (long)var5.length);
            if ((var8 = (long)var0.read(var5, 0, var10)) == -1L) {
               break;
            }
         }
      }

      return var3;
   }

   private static long skipSafely(InputStream var0, long var1) throws IOException {
      int var3 = var0.available();
      return var3 == 0 ? 0L : var0.skip(Math.min((long)var3, var1));
   }

   @CanIgnoreReturnValue
   public static <T> T readBytes(InputStream var0, ByteProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      byte[] var2 = createBuffer();

      int var3;
      do {
         var3 = var0.read(var2);
      } while(var3 != -1 && var1.processBytes(var2, 0, var3));

      return var1.getResult();
   }

   @CanIgnoreReturnValue
   public static int read(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (var3 < 0) {
         throw new IndexOutOfBoundsException("len is negative");
      } else {
         int var4;
         int var5;
         for(var4 = 0; var4 < var3; var4 += var5) {
            var5 = var0.read(var1, var2 + var4, var3 - var4);
            if (var5 == -1) {
               break;
            }
         }

         return var4;
      }
   }

   private static final class LimitedInputStream extends FilterInputStream {
      private long left;
      private long mark = -1L;

      LimitedInputStream(InputStream var1, long var2) {
         super(var1);
         Preconditions.checkNotNull(var1);
         Preconditions.checkArgument(var2 >= 0L, "limit must be non-negative");
         this.left = var2;
      }

      public int available() throws IOException {
         return (int)Math.min((long)this.in.available(), this.left);
      }

      public synchronized void mark(int var1) {
         this.in.mark(var1);
         this.mark = this.left;
      }

      public int read() throws IOException {
         if (this.left == 0L) {
            return -1;
         } else {
            int var1 = this.in.read();
            if (var1 != -1) {
               --this.left;
            }

            return var1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.left == 0L) {
            return -1;
         } else {
            var3 = (int)Math.min((long)var3, this.left);
            int var4 = this.in.read(var1, var2, var3);
            if (var4 != -1) {
               this.left -= (long)var4;
            }

            return var4;
         }
      }

      public synchronized void reset() throws IOException {
         if (!this.in.markSupported()) {
            throw new IOException("Mark not supported");
         } else if (this.mark == -1L) {
            throw new IOException("Mark not set");
         } else {
            this.in.reset();
            this.left = this.mark;
         }
      }

      public long skip(long var1) throws IOException {
         var1 = Math.min(var1, this.left);
         long var3 = this.in.skip(var1);
         this.left -= var3;
         return var3;
      }
   }

   private static class ByteArrayDataOutputStream implements ByteArrayDataOutput {
      final DataOutput output;
      final ByteArrayOutputStream byteArrayOutputSteam;

      ByteArrayDataOutputStream(ByteArrayOutputStream var1) {
         super();
         this.byteArrayOutputSteam = var1;
         this.output = new DataOutputStream(var1);
      }

      public void write(int var1) {
         try {
            this.output.write(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void write(byte[] var1) {
         try {
            this.output.write(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void write(byte[] var1, int var2, int var3) {
         try {
            this.output.write(var1, var2, var3);
         } catch (IOException var5) {
            throw new AssertionError(var5);
         }
      }

      public void writeBoolean(boolean var1) {
         try {
            this.output.writeBoolean(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeByte(int var1) {
         try {
            this.output.writeByte(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeBytes(String var1) {
         try {
            this.output.writeBytes(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeChar(int var1) {
         try {
            this.output.writeChar(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeChars(String var1) {
         try {
            this.output.writeChars(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeDouble(double var1) {
         try {
            this.output.writeDouble(var1);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      public void writeFloat(float var1) {
         try {
            this.output.writeFloat(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeInt(int var1) {
         try {
            this.output.writeInt(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeLong(long var1) {
         try {
            this.output.writeLong(var1);
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      public void writeShort(int var1) {
         try {
            this.output.writeShort(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public void writeUTF(String var1) {
         try {
            this.output.writeUTF(var1);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public byte[] toByteArray() {
         return this.byteArrayOutputSteam.toByteArray();
      }
   }

   private static class ByteArrayDataInputStream implements ByteArrayDataInput {
      final DataInput input;

      ByteArrayDataInputStream(ByteArrayInputStream var1) {
         super();
         this.input = new DataInputStream(var1);
      }

      public void readFully(byte[] var1) {
         try {
            this.input.readFully(var1);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public void readFully(byte[] var1, int var2, int var3) {
         try {
            this.input.readFully(var1, var2, var3);
         } catch (IOException var5) {
            throw new IllegalStateException(var5);
         }
      }

      public int skipBytes(int var1) {
         try {
            return this.input.skipBytes(var1);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }

      public boolean readBoolean() {
         try {
            return this.input.readBoolean();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public byte readByte() {
         try {
            return this.input.readByte();
         } catch (EOFException var2) {
            throw new IllegalStateException(var2);
         } catch (IOException var3) {
            throw new AssertionError(var3);
         }
      }

      public int readUnsignedByte() {
         try {
            return this.input.readUnsignedByte();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public short readShort() {
         try {
            return this.input.readShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public int readUnsignedShort() {
         try {
            return this.input.readUnsignedShort();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public char readChar() {
         try {
            return this.input.readChar();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public int readInt() {
         try {
            return this.input.readInt();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public long readLong() {
         try {
            return this.input.readLong();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public float readFloat() {
         try {
            return this.input.readFloat();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public double readDouble() {
         try {
            return this.input.readDouble();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String readLine() {
         try {
            return this.input.readLine();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String readUTF() {
         try {
            return this.input.readUTF();
         } catch (IOException var2) {
            throw new IllegalStateException(var2);
         }
      }
   }

   private static final class FastByteArrayOutputStream extends ByteArrayOutputStream {
      private FastByteArrayOutputStream() {
         super();
      }

      void writeTo(byte[] var1, int var2) {
         System.arraycopy(this.buf, 0, var1, var2, this.count);
      }

      // $FF: synthetic method
      FastByteArrayOutputStream(Object var1) {
         this();
      }
   }
}
