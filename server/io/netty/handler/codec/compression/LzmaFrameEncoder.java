package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lzma.sdk.ICodeProgress;
import lzma.sdk.lzma.Encoder;

public class LzmaFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(LzmaFrameEncoder.class);
   private static final int MEDIUM_DICTIONARY_SIZE = 65536;
   private static final int MIN_FAST_BYTES = 5;
   private static final int MEDIUM_FAST_BYTES = 32;
   private static final int MAX_FAST_BYTES = 273;
   private static final int DEFAULT_MATCH_FINDER = 1;
   private static final int DEFAULT_LC = 3;
   private static final int DEFAULT_LP = 0;
   private static final int DEFAULT_PB = 2;
   private final Encoder encoder;
   private final byte properties;
   private final int littleEndianDictionarySize;
   private static boolean warningLogged;

   public LzmaFrameEncoder() {
      this(65536);
   }

   public LzmaFrameEncoder(int var1, int var2, int var3) {
      this(var1, var2, var3, 65536);
   }

   public LzmaFrameEncoder(int var1) {
      this(3, 0, 2, var1);
   }

   public LzmaFrameEncoder(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, false, 32);
   }

   public LzmaFrameEncoder(int var1, int var2, int var3, int var4, boolean var5, int var6) {
      super();
      if (var1 >= 0 && var1 <= 8) {
         if (var2 >= 0 && var2 <= 4) {
            if (var3 >= 0 && var3 <= 4) {
               if (var1 + var2 > 4 && !warningLogged) {
                  logger.warn("The latest versions of LZMA libraries (for example, XZ Utils) has an additional requirement: lc + lp <= 4. Data which don't follow this requirement cannot be decompressed with this libraries.");
                  warningLogged = true;
               }

               if (var4 < 0) {
                  throw new IllegalArgumentException("dictionarySize: " + var4 + " (expected: 0+)");
               } else if (var6 >= 5 && var6 <= 273) {
                  this.encoder = new Encoder();
                  this.encoder.setDictionarySize(var4);
                  this.encoder.setEndMarkerMode(var5);
                  this.encoder.setMatchFinder(1);
                  this.encoder.setNumFastBytes(var6);
                  this.encoder.setLcLpPb(var1, var2, var3);
                  this.properties = (byte)((var3 * 5 + var2) * 9 + var1);
                  this.littleEndianDictionarySize = Integer.reverseBytes(var4);
               } else {
                  throw new IllegalArgumentException(String.format("numFastBytes: %d (expected: %d-%d)", var6, 5, 273));
               }
            } else {
               throw new IllegalArgumentException("pb: " + var3 + " (expected: 0-4)");
            }
         } else {
            throw new IllegalArgumentException("lp: " + var2 + " (expected: 0-4)");
         }
      } else {
         throw new IllegalArgumentException("lc: " + var1 + " (expected: 0-8)");
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var4 = var2.readableBytes();
      ByteBufInputStream var5 = null;
      ByteBufOutputStream var6 = null;

      try {
         var5 = new ByteBufInputStream(var2);
         var6 = new ByteBufOutputStream(var3);
         var6.writeByte(this.properties);
         var6.writeInt(this.littleEndianDictionarySize);
         var6.writeLong(Long.reverseBytes((long)var4));
         this.encoder.code(var5, var6, -1L, -1L, (ICodeProgress)null);
      } finally {
         if (var5 != null) {
            var5.close();
         }

         if (var6 != null) {
            var6.close();
         }

      }

   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) throws Exception {
      int var4 = var2.readableBytes();
      int var5 = maxOutputBufferLength(var4);
      return var1.alloc().ioBuffer(var5);
   }

   private static int maxOutputBufferLength(int var0) {
      double var1;
      if (var0 < 200) {
         var1 = 1.5D;
      } else if (var0 < 500) {
         var1 = 1.2D;
      } else if (var0 < 1000) {
         var1 = 1.1D;
      } else if (var0 < 10000) {
         var1 = 1.05D;
      } else {
         var1 = 1.02D;
      }

      return 13 + (int)((double)var0 * var1);
   }
}
