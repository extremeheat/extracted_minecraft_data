package net.minecraft.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record PngInfo(int width, int height) {
   private static final long PNG_HEADER = -8552249625308161526L;
   private static final int IHDR_TYPE = 1229472850;
   private static final int IHDR_SIZE = 13;

   public PngInfo(int width, int height) {
      super();
      this.width = width;
      this.height = height;
   }

   public static PngInfo fromStream(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(var0);
      if (var1.readLong() != -8552249625308161526L) {
         throw new IOException("Bad PNG Signature");
      } else if (var1.readInt() != 13) {
         throw new IOException("Bad length for IHDR chunk!");
      } else if (var1.readInt() != 1229472850) {
         throw new IOException("Bad type for IHDR chunk!");
      } else {
         int var2 = var1.readInt();
         int var3 = var1.readInt();
         return new PngInfo(var2, var3);
      }
   }

   public static PngInfo fromBytes(byte[] var0) throws IOException {
      return fromStream(new ByteArrayInputStream(var0));
   }

   public static void validateHeader(ByteBuffer var0) throws IOException {
      ByteOrder var1 = var0.order();
      var0.order(ByteOrder.BIG_ENDIAN);
      if (var0.getLong(0) != -8552249625308161526L) {
         throw new IOException("Bad PNG Signature");
      } else if (var0.getInt(8) != 13) {
         throw new IOException("Bad length for IHDR chunk!");
      } else if (var0.getInt(12) != 1229472850) {
         throw new IOException("Bad type for IHDR chunk!");
      } else {
         var0.order(var1);
      }
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
