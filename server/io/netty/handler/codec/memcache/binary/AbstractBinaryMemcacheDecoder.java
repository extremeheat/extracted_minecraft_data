package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectDecoder;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import java.util.List;

public abstract class AbstractBinaryMemcacheDecoder<M extends BinaryMemcacheMessage> extends AbstractMemcacheObjectDecoder {
   public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
   private final int chunkSize;
   private M currentMessage;
   private int alreadyReadChunkSize;
   private AbstractBinaryMemcacheDecoder.State state;

   protected AbstractBinaryMemcacheDecoder() {
      this(8192);
   }

   protected AbstractBinaryMemcacheDecoder(int var1) {
      super();
      this.state = AbstractBinaryMemcacheDecoder.State.READ_HEADER;
      if (var1 < 0) {
         throw new IllegalArgumentException("chunkSize must be a positive integer: " + var1);
      } else {
         this.chunkSize = var1;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      switch(this.state) {
      case READ_HEADER:
         try {
            if (var2.readableBytes() < 24) {
               return;
            } else {
               this.resetDecoder();
               this.currentMessage = this.decodeHeader(var2);
               this.state = AbstractBinaryMemcacheDecoder.State.READ_EXTRAS;
            }
         } catch (Exception var12) {
            this.resetDecoder();
            var3.add(this.invalidMessage(var12));
            return;
         }
      case READ_EXTRAS:
         try {
            byte var4 = this.currentMessage.extrasLength();
            if (var4 > 0) {
               if (var2.readableBytes() < var4) {
                  return;
               }

               this.currentMessage.setExtras(var2.readRetainedSlice(var4));
            }

            this.state = AbstractBinaryMemcacheDecoder.State.READ_KEY;
         } catch (Exception var11) {
            this.resetDecoder();
            var3.add(this.invalidMessage(var11));
            return;
         }
      case READ_KEY:
         try {
            short var13 = this.currentMessage.keyLength();
            if (var13 > 0) {
               if (var2.readableBytes() < var13) {
                  return;
               }

               this.currentMessage.setKey(var2.readRetainedSlice(var13));
            }

            var3.add(this.currentMessage.retain());
            this.state = AbstractBinaryMemcacheDecoder.State.READ_CONTENT;
         } catch (Exception var10) {
            this.resetDecoder();
            var3.add(this.invalidMessage(var10));
            return;
         }
      case READ_CONTENT:
         try {
            int var14 = this.currentMessage.totalBodyLength() - this.currentMessage.keyLength() - this.currentMessage.extrasLength();
            int var5 = var2.readableBytes();
            if (var14 > 0) {
               if (var5 == 0) {
                  return;
               }

               if (var5 > this.chunkSize) {
                  var5 = this.chunkSize;
               }

               int var6 = var14 - this.alreadyReadChunkSize;
               if (var5 > var6) {
                  var5 = var6;
               }

               ByteBuf var7 = var2.readRetainedSlice(var5);
               Object var8;
               if ((this.alreadyReadChunkSize += var5) >= var14) {
                  var8 = new DefaultLastMemcacheContent(var7);
               } else {
                  var8 = new DefaultMemcacheContent(var7);
               }

               var3.add(var8);
               if (this.alreadyReadChunkSize < var14) {
                  return;
               }
            } else {
               var3.add(LastMemcacheContent.EMPTY_LAST_CONTENT);
            }

            this.resetDecoder();
            this.state = AbstractBinaryMemcacheDecoder.State.READ_HEADER;
            return;
         } catch (Exception var9) {
            this.resetDecoder();
            var3.add(this.invalidChunk(var9));
            return;
         }
      case BAD_MESSAGE:
         var2.skipBytes(this.actualReadableBytes());
         return;
      default:
         throw new Error("Unknown state reached: " + this.state);
      }
   }

   private M invalidMessage(Exception var1) {
      this.state = AbstractBinaryMemcacheDecoder.State.BAD_MESSAGE;
      BinaryMemcacheMessage var2 = this.buildInvalidMessage();
      var2.setDecoderResult(DecoderResult.failure(var1));
      return var2;
   }

   private MemcacheContent invalidChunk(Exception var1) {
      this.state = AbstractBinaryMemcacheDecoder.State.BAD_MESSAGE;
      DefaultLastMemcacheContent var2 = new DefaultLastMemcacheContent(Unpooled.EMPTY_BUFFER);
      var2.setDecoderResult(DecoderResult.failure(var1));
      return var2;
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      super.channelInactive(var1);
      this.resetDecoder();
   }

   protected void resetDecoder() {
      if (this.currentMessage != null) {
         this.currentMessage.release();
         this.currentMessage = null;
      }

      this.alreadyReadChunkSize = 0;
   }

   protected abstract M decodeHeader(ByteBuf var1);

   protected abstract M buildInvalidMessage();

   static enum State {
      READ_HEADER,
      READ_EXTRAS,
      READ_KEY,
      READ_CONTENT,
      BAD_MESSAGE;

      private State() {
      }
   }
}
