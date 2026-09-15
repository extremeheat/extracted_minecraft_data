package net.minecraft.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;

public record PngInfo(int width, int height, byte bitDepth, byte colorType) {
   private static final HexFormat FORMAT = HexFormat.of().withUpperCase().withPrefix("0x");
   private static final int PNG_HEADER_SIZE = 8;
   private static final long PNG_HEADER = -8552249625308161526L;
   private static final int PNG_CHUNK_HEADER_SIZE = 8;
   private static final int PNG_CHUNK_TRAILER_SIZE = 4;
   private static final int IHDR_TYPE = 1229472850;
   private static final int IHDR_SIZE = 13;
   private static final byte COLOR_TYPE_GRAYSCALE = 0;
   private static final byte COLOR_TYPE_TRUECOLOR = 2;
   private static final byte COLOR_TYPE_INDEXED = 3;
   private static final byte COLOR_TYPE_GRAYSCALE_ALPHA = 4;
   private static final byte COLOR_TYPE_TRUECOLOR_ALPHA = 6;

   public PngInfo {
      super();
   }

   public static PngInfo fromStream(final InputStream inputStream) throws IOException {
      DataInputStream stream = new DataInputStream(inputStream);
      long magic = stream.readLong();
      if (magic != -8552249625308161526L) {
         throw new IOException("Bad PNG Signature: " + FORMAT.toHexDigits(magic));
      } else {
         int headerSize = stream.readInt();
         if (headerSize != 13) {
            throw new IOException("Bad length for IHDR chunk: " + headerSize);
         } else {
            int headerType = stream.readInt();
            if (headerType != 1229472850) {
               throw new IOException("Bad type for IHDR chunk: " + FORMAT.toHexDigits(headerType));
            } else {
               int width = stream.readInt();
               int height = stream.readInt();
               byte bitDepth = stream.readByte();
               if (!validateBitDepth(bitDepth)) {
                  throw new IOException("Invalid bit depth: " + bitDepth);
               } else {
                  byte colorType = stream.readByte();
                  if (!validateColorType(colorType)) {
                     throw new IOException("Invalid color type: " + colorType);
                  } else if (!validateBounds(width, height, bitDepth, colorType)) {
                     throw new IOException("Bounds check failed, " + width + "x" + height + "@" + bitDepth + ", type: " + colorType);
                  } else {
                     return new PngInfo(width, height, bitDepth, colorType);
                  }
               }
            }
         }
      }
   }

   public static PngInfo fromBytes(final byte[] bytes) throws IOException {
      return fromStream(new ByteArrayInputStream(bytes));
   }

   public static void validateHeader(final ByteBuffer buffer) throws IOException {
      ByteOrder order = buffer.order();
      buffer.order(ByteOrder.BIG_ENDIAN);
      if (buffer.limit() < 33) {
         throw new IOException("PNG header missing");
      } else if (buffer.getLong(0) != -8552249625308161526L) {
         throw new IOException("Bad PNG Signature");
      } else if (buffer.getInt(8) != 13) {
         throw new IOException("Bad length for IHDR chunk!");
      } else if (buffer.getInt(12) != 1229472850) {
         throw new IOException("Bad type for IHDR chunk!");
      } else {
         int width = buffer.getInt(16);
         int height = buffer.getInt(20);
         byte bitDepth = buffer.get(24);
         if (!validateBitDepth(bitDepth)) {
            throw new IOException("Invalid bit depth: " + bitDepth);
         } else {
            byte colorType = buffer.get(25);
            if (!validateColorType(colorType)) {
               throw new IOException("Invalid color type: " + colorType);
            } else if (!validateBounds(width, height, bitDepth, colorType)) {
               throw new IOException("Bounds check failed, " + width + "x" + height + "@" + bitDepth + ", type: " + colorType);
            } else {
               buffer.order(order);
            }
         }
      }
   }

   private static boolean validateBitDepth(final int bitDepth) {
      boolean var10000;
      switch (bitDepth) {
         case 1:
         case 2:
         case 4:
         case 8:
         case 16:
            var10000 = true;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   private static boolean validateColorType(final int colorType) {
      boolean var10000;
      switch (colorType) {
         case 0:
         case 2:
         case 3:
         case 4:
         case 6:
            var10000 = true;
            break;
         case 1:
         case 5:
         default:
            var10000 = false;
      }

      return var10000;
   }

   private static boolean validateBounds(final int width, final int height, final byte bitDepth, final byte colorType) {
      if (bitDepth == 16) {
         long componentCount = 4L;
         long bufferEstimate = 4L * (long)width * (long)height * 2L;
         if (bufferEstimate != (long)((int)bufferEstimate)) {
            return false;
         }
      }

      return true;
   }
}
