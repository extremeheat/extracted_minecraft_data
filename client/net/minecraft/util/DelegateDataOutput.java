package net.minecraft.util;

import java.io.DataOutput;
import java.io.IOException;

public class DelegateDataOutput implements DataOutput {
   private final DataOutput parent;

   public DelegateDataOutput(DataOutput var1) {
      super();
      this.parent = var1;
   }

   @Override
   public void write(int var1) throws IOException {
      this.parent.write(var1);
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.parent.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.parent.write(var1, var2, var3);
   }

   @Override
   public void writeBoolean(boolean var1) throws IOException {
      this.parent.writeBoolean(var1);
   }

   @Override
   public void writeByte(int var1) throws IOException {
      this.parent.writeByte(var1);
   }

   @Override
   public void writeShort(int var1) throws IOException {
      this.parent.writeShort(var1);
   }

   @Override
   public void writeChar(int var1) throws IOException {
      this.parent.writeChar(var1);
   }

   @Override
   public void writeInt(int var1) throws IOException {
      this.parent.writeInt(var1);
   }

   @Override
   public void writeLong(long var1) throws IOException {
      this.parent.writeLong(var1);
   }

   @Override
   public void writeFloat(float var1) throws IOException {
      this.parent.writeFloat(var1);
   }

   @Override
   public void writeDouble(double var1) throws IOException {
      this.parent.writeDouble(var1);
   }

   @Override
   public void writeBytes(String var1) throws IOException {
      this.parent.writeBytes(var1);
   }

   @Override
   public void writeChars(String var1) throws IOException {
      this.parent.writeChars(var1);
   }

   @Override
   public void writeUTF(String var1) throws IOException {
      this.parent.writeUTF(var1);
   }
}
