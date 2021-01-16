package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkDecoder;
import com.ning.compress.lzf.util.ChunkDecoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class LzfDecoder extends ByteToMessageDecoder {
   private LzfDecoder.State currentState;
   private static final short MAGIC_NUMBER = 23126;
   private ChunkDecoder decoder;
   private BufferRecycler recycler;
   private int chunkLength;
   private int originalLength;
   private boolean isCompressed;

   public LzfDecoder() {
      this(false);
   }

   public LzfDecoder(boolean var1) {
      super();
      this.currentState = LzfDecoder.State.INIT_BLOCK;
      this.decoder = var1 ? ChunkDecoderFactory.safeInstance() : ChunkDecoderFactory.optimalInstance();
      this.recycler = BufferRecycler.instance();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch(this.currentState) {
         case INIT_BLOCK:
            if (var2.readableBytes() < 5) {
               break;
            }

            int var4 = var2.readUnsignedShort();
            if (var4 != 23126) {
               throw new DecompressionException("unexpected block identifier");
            }

            byte var5 = var2.readByte();
            switch(var5) {
            case 0:
               this.isCompressed = false;
               this.currentState = LzfDecoder.State.DECOMPRESS_DATA;
               break;
            case 1:
               this.isCompressed = true;
               this.currentState = LzfDecoder.State.INIT_ORIGINAL_LENGTH;
               break;
            default:
               throw new DecompressionException(String.format("unknown type of chunk: %d (expected: %d or %d)", Integer.valueOf(var5), 0, 1));
            }

            this.chunkLength = var2.readUnsignedShort();
            if (var5 != 1) {
               break;
            }
         case INIT_ORIGINAL_LENGTH:
            if (var2.readableBytes() < 2) {
               break;
            }

            this.originalLength = var2.readUnsignedShort();
            this.currentState = LzfDecoder.State.DECOMPRESS_DATA;
         case DECOMPRESS_DATA:
            int var6 = this.chunkLength;
            if (var2.readableBytes() >= var6) {
               int var7 = this.originalLength;
               if (this.isCompressed) {
                  int var8 = var2.readerIndex();
                  byte[] var9;
                  int var10;
                  if (var2.hasArray()) {
                     var9 = var2.array();
                     var10 = var2.arrayOffset() + var8;
                  } else {
                     var9 = this.recycler.allocInputBuffer(var6);
                     var2.getBytes(var8, (byte[])var9, 0, var6);
                     var10 = 0;
                  }

                  ByteBuf var11 = var1.alloc().heapBuffer(var7, var7);
                  byte[] var12 = var11.array();
                  int var13 = var11.arrayOffset() + var11.writerIndex();
                  boolean var14 = false;

                  try {
                     this.decoder.decodeChunk(var9, var10, var12, var13, var13 + var7);
                     var11.writerIndex(var11.writerIndex() + var7);
                     var3.add(var11);
                     var2.skipBytes(var6);
                     var14 = true;
                  } finally {
                     if (!var14) {
                        var11.release();
                     }

                  }

                  if (!var2.hasArray()) {
                     this.recycler.releaseInputBuffer(var9);
                  }
               } else if (var6 > 0) {
                  var3.add(var2.readRetainedSlice(var6));
               }

               this.currentState = LzfDecoder.State.INIT_BLOCK;
            }
            break;
         case CORRUPTED:
            var2.skipBytes(var2.readableBytes());
            break;
         default:
            throw new IllegalStateException();
         }

      } catch (Exception var19) {
         this.currentState = LzfDecoder.State.CORRUPTED;
         this.decoder = null;
         this.recycler = null;
         throw var19;
      }
   }

   private static enum State {
      INIT_BLOCK,
      INIT_ORIGINAL_LENGTH,
      DECOMPRESS_DATA,
      CORRUPTED;

      private State() {
      }
   }
}
