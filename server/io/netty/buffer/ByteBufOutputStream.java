package io.netty.buffer;

import io.netty.util.CharsetUtil;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutputStream extends OutputStream implements DataOutput {
   private final ByteBuf buffer;
   private final int startIndex;
   private final DataOutputStream utf8out = new DataOutputStream(this);

   public ByteBufOutputStream(ByteBuf var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("buffer");
      } else {
         this.buffer = var1;
         this.startIndex = var1.writerIndex();
      }
   }

   public int writtenBytes() {
      return this.buffer.writerIndex() - this.startIndex;
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 != 0) {
         this.buffer.writeBytes(var1, var2, var3);
      }
   }

   public void write(byte[] var1) throws IOException {
      this.buffer.writeBytes(var1);
   }

   public void write(int var1) throws IOException {
      this.buffer.writeByte(var1);
   }

   public void writeBoolean(boolean var1) throws IOException {
      this.buffer.writeBoolean(var1);
   }

   public void writeByte(int var1) throws IOException {
      this.buffer.writeByte(var1);
   }

   public void writeBytes(String var1) throws IOException {
      this.buffer.writeCharSequence(var1, CharsetUtil.US_ASCII);
   }

   public void writeChar(int var1) throws IOException {
      this.buffer.writeChar(var1);
   }

   public void writeChars(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.buffer.writeChar(var1.charAt(var3));
      }

   }

   public void writeDouble(double var1) throws IOException {
      this.buffer.writeDouble(var1);
   }

   public void writeFloat(float var1) throws IOException {
      this.buffer.writeFloat(var1);
   }

   public void writeInt(int var1) throws IOException {
      this.buffer.writeInt(var1);
   }

   public void writeLong(long var1) throws IOException {
      this.buffer.writeLong(var1);
   }

   public void writeShort(int var1) throws IOException {
      this.buffer.writeShort((short)var1);
   }

   public void writeUTF(String var1) throws IOException {
      this.utf8out.writeUTF(var1);
   }

   public ByteBuf buffer() {
      return this.buffer;
   }
}
