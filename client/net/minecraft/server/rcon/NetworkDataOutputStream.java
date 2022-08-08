package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkDataOutputStream {
   private final ByteArrayOutputStream outputStream;
   private final DataOutputStream dataOutputStream;

   public NetworkDataOutputStream(int var1) {
      super();
      this.outputStream = new ByteArrayOutputStream(var1);
      this.dataOutputStream = new DataOutputStream(this.outputStream);
   }

   public void writeBytes(byte[] var1) throws IOException {
      this.dataOutputStream.write(var1, 0, var1.length);
   }

   public void writeString(String var1) throws IOException {
      this.dataOutputStream.writeBytes(var1);
      this.dataOutputStream.write(0);
   }

   public void write(int var1) throws IOException {
      this.dataOutputStream.write(var1);
   }

   public void writeShort(short var1) throws IOException {
      this.dataOutputStream.writeShort(Short.reverseBytes(var1));
   }

   public void writeInt(int var1) throws IOException {
      this.dataOutputStream.writeInt(Integer.reverseBytes(var1));
   }

   public void writeFloat(float var1) throws IOException {
      this.dataOutputStream.writeInt(Integer.reverseBytes(Float.floatToIntBits(var1)));
   }

   public byte[] toByteArray() {
      return this.outputStream.toByteArray();
   }

   public void reset() {
      this.outputStream.reset();
   }
}
