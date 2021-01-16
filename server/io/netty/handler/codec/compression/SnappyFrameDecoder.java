package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class SnappyFrameDecoder extends ByteToMessageDecoder {
   private static final int SNAPPY_IDENTIFIER_LEN = 6;
   private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
   private final Snappy snappy;
   private final boolean validateChecksums;
   private boolean started;
   private boolean corrupted;

   public SnappyFrameDecoder() {
      this(false);
   }

   public SnappyFrameDecoder(boolean var1) {
      super();
      this.snappy = new Snappy();
      this.validateChecksums = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.corrupted) {
         var2.skipBytes(var2.readableBytes());
      } else {
         try {
            int var4 = var2.readerIndex();
            int var5 = var2.readableBytes();
            if (var5 >= 4) {
               short var6 = var2.getUnsignedByte(var4);
               SnappyFrameDecoder.ChunkType var7 = mapChunkType((byte)var6);
               int var8 = var2.getUnsignedMediumLE(var4 + 1);
               int var10;
               switch(var7) {
               case STREAM_IDENTIFIER:
                  if (var8 != 6) {
                     throw new DecompressionException("Unexpected length of stream identifier: " + var8);
                  }

                  if (var5 >= 10) {
                     var2.skipBytes(4);
                     int var9 = var2.readerIndex();
                     var2.skipBytes(6);
                     checkByte(var2.getByte(var9++), (byte)115);
                     checkByte(var2.getByte(var9++), (byte)78);
                     checkByte(var2.getByte(var9++), (byte)97);
                     checkByte(var2.getByte(var9++), (byte)80);
                     checkByte(var2.getByte(var9++), (byte)112);
                     checkByte(var2.getByte(var9), (byte)89);
                     this.started = true;
                  }
                  break;
               case RESERVED_SKIPPABLE:
                  if (!this.started) {
                     throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                  }

                  if (var5 < 4 + var8) {
                     return;
                  }

                  var2.skipBytes(4 + var8);
                  break;
               case RESERVED_UNSKIPPABLE:
                  throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(var6));
               case UNCOMPRESSED_DATA:
                  if (!this.started) {
                     throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                  }

                  if (var8 > 65540) {
                     throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                  }

                  if (var5 < 4 + var8) {
                     return;
                  }

                  var2.skipBytes(4);
                  if (this.validateChecksums) {
                     var10 = var2.readIntLE();
                     Snappy.validateChecksum(var10, var2, var2.readerIndex(), var8 - 4);
                  } else {
                     var2.skipBytes(4);
                  }

                  var3.add(var2.readRetainedSlice(var8 - 4));
                  break;
               case COMPRESSED_DATA:
                  if (!this.started) {
                     throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                  }

                  if (var5 < 4 + var8) {
                     return;
                  }

                  var2.skipBytes(4);
                  var10 = var2.readIntLE();
                  ByteBuf var11 = var1.alloc().buffer();

                  try {
                     if (this.validateChecksums) {
                        int var12 = var2.writerIndex();

                        try {
                           var2.writerIndex(var2.readerIndex() + var8 - 4);
                           this.snappy.decode(var2, var11);
                        } finally {
                           var2.writerIndex(var12);
                        }

                        Snappy.validateChecksum(var10, var11, 0, var11.writerIndex());
                     } else {
                        this.snappy.decode(var2.readSlice(var8 - 4), var11);
                     }

                     var3.add(var11);
                     var11 = null;
                  } finally {
                     if (var11 != null) {
                        var11.release();
                     }

                  }

                  this.snappy.reset();
               }

            }
         } catch (Exception var23) {
            this.corrupted = true;
            throw var23;
         }
      }
   }

   private static void checkByte(byte var0, byte var1) {
      if (var0 != var1) {
         throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
      }
   }

   private static SnappyFrameDecoder.ChunkType mapChunkType(byte var0) {
      if (var0 == 0) {
         return SnappyFrameDecoder.ChunkType.COMPRESSED_DATA;
      } else if (var0 == 1) {
         return SnappyFrameDecoder.ChunkType.UNCOMPRESSED_DATA;
      } else if (var0 == -1) {
         return SnappyFrameDecoder.ChunkType.STREAM_IDENTIFIER;
      } else {
         return (var0 & 128) == 128 ? SnappyFrameDecoder.ChunkType.RESERVED_SKIPPABLE : SnappyFrameDecoder.ChunkType.RESERVED_UNSKIPPABLE;
      }
   }

   private static enum ChunkType {
      STREAM_IDENTIFIER,
      COMPRESSED_DATA,
      UNCOMPRESSED_DATA,
      RESERVED_UNSKIPPABLE,
      RESERVED_SKIPPABLE;

      private ChunkType() {
      }
   }
}
