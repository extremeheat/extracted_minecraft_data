package net.minecraft.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;

public record PngInfo(int width, int height) {
   private static final HexFormat FORMAT = HexFormat.of().withUpperCase().withPrefix("0x");
   private static final long PNG_HEADER = -8552249625308161526L;
   private static final int IHDR_TYPE = 1229472850;
   private static final int IHDR_SIZE = 13;

   public PngInfo(int var1, int var2) {
      super();
      this.width = var1;
      this.height = var2;
   }

   public static PngInfo fromStream(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(var0);
      long var2 = var1.readLong();
      if (var2 != -8552249625308161526L) {
         throw new IOException("Bad PNG Signature: " + FORMAT.toHexDigits(var2));
      } else {
         int var4 = var1.readInt();
         if (var4 != 13) {
            throw new IOException("Bad length for IHDR chunk: " + var4);
         } else {
            int var5 = var1.readInt();
            if (var5 != 1229472850) {
               throw new IOException("Bad type for IHDR chunk: " + FORMAT.toHexDigits(var5));
            } else {
               int var6 = var1.readInt();
               int var7 = var1.readInt();
               return new PngInfo(var6, var7);
            }
         }
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
}
