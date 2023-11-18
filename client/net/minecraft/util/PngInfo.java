package net.minecraft.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public record PngInfo(int a, int b) {
   private final int width;
   private final int height;
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
}
