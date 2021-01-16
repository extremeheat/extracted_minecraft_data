package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.EmptyArrays;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameDecoder extends ByteToMessageDecoder {
   private FastLzFrameDecoder.State currentState;
   private final Checksum checksum;
   private int chunkLength;
   private int originalLength;
   private boolean isCompressed;
   private boolean hasChecksum;
   private int currentChecksum;

   public FastLzFrameDecoder() {
      this(false);
   }

   public FastLzFrameDecoder(boolean var1) {
      this(var1 ? new Adler32() : null);
   }

   public FastLzFrameDecoder(Checksum var1) {
      super();
      this.currentState = FastLzFrameDecoder.State.INIT_BLOCK;
      this.checksum = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch(this.currentState) {
         case INIT_BLOCK:
            if (var2.readableBytes() < 4) {
               break;
            }

            int var4 = var2.readUnsignedMedium();
            if (var4 != 4607066) {
               throw new DecompressionException("unexpected block identifier");
            }

            byte var5 = var2.readByte();
            this.isCompressed = (var5 & 1) == 1;
            this.hasChecksum = (var5 & 16) == 16;
            this.currentState = FastLzFrameDecoder.State.INIT_BLOCK_PARAMS;
         case INIT_BLOCK_PARAMS:
            if (var2.readableBytes() < 2 + (this.isCompressed ? 2 : 0) + (this.hasChecksum ? 4 : 0)) {
               break;
            }

            this.currentChecksum = this.hasChecksum ? var2.readInt() : 0;
            this.chunkLength = var2.readUnsignedShort();
            this.originalLength = this.isCompressed ? var2.readUnsignedShort() : this.chunkLength;
            this.currentState = FastLzFrameDecoder.State.DECOMPRESS_DATA;
         case DECOMPRESS_DATA:
            int var6 = this.chunkLength;
            if (var2.readableBytes() >= var6) {
               int var7 = var2.readerIndex();
               int var8 = this.originalLength;
               ByteBuf var9;
               byte[] var10;
               int var11;
               if (var8 != 0) {
                  var9 = var1.alloc().heapBuffer(var8, var8);
                  var10 = var9.array();
                  var11 = var9.arrayOffset() + var9.writerIndex();
               } else {
                  var9 = null;
                  var10 = EmptyArrays.EMPTY_BYTES;
                  var11 = 0;
               }

               boolean var12 = false;

               try {
                  int var14;
                  if (this.isCompressed) {
                     byte[] var13;
                     if (var2.hasArray()) {
                        var13 = var2.array();
                        var14 = var2.arrayOffset() + var7;
                     } else {
                        var13 = new byte[var6];
                        var2.getBytes(var7, var13);
                        var14 = 0;
                     }

                     int var15 = FastLz.decompress(var13, var14, var6, var10, var11, var8);
                     if (var8 != var15) {
                        throw new DecompressionException(String.format("stream corrupted: originalLength(%d) and actual length(%d) mismatch", var8, var15));
                     }
                  } else {
                     var2.getBytes(var7, var10, var11, var6);
                  }

                  Checksum var21 = this.checksum;
                  if (this.hasChecksum && var21 != null) {
                     var21.reset();
                     var21.update(var10, var11, var8);
                     var14 = (int)var21.getValue();
                     if (var14 != this.currentChecksum) {
                        throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", var14, this.currentChecksum));
                     }
                  }

                  if (var9 != null) {
                     var9.writerIndex(var9.writerIndex() + var8);
                     var3.add(var9);
                  }

                  var2.skipBytes(var6);
                  this.currentState = FastLzFrameDecoder.State.INIT_BLOCK;
                  var12 = true;
               } finally {
                  if (!var12 && var9 != null) {
                     var9.release();
                  }

               }
            }
            break;
         case CORRUPTED:
            var2.skipBytes(var2.readableBytes());
            break;
         default:
            throw new IllegalStateException();
         }

      } catch (Exception var20) {
         this.currentState = FastLzFrameDecoder.State.CORRUPTED;
         throw var20;
      }
   }

   private static enum State {
      INIT_BLOCK,
      INIT_BLOCK_PARAMS,
      DECOMPRESS_DATA,
      CORRUPTED;

      private State() {
      }
   }
}
