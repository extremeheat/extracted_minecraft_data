package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class DelimiterBasedFrameDecoder extends ByteToMessageDecoder {
   private final ByteBuf[] delimiters;
   private final int maxFrameLength;
   private final boolean stripDelimiter;
   private final boolean failFast;
   private boolean discardingTooLongFrame;
   private int tooLongFrameLength;
   private final LineBasedFrameDecoder lineBasedDecoder;

   public DelimiterBasedFrameDecoder(int var1, ByteBuf var2) {
      this(var1, true, var2);
   }

   public DelimiterBasedFrameDecoder(int var1, boolean var2, ByteBuf var3) {
      this(var1, var2, true, var3);
   }

   public DelimiterBasedFrameDecoder(int var1, boolean var2, boolean var3, ByteBuf var4) {
      this(var1, var2, var3, var4.slice(var4.readerIndex(), var4.readableBytes()));
   }

   public DelimiterBasedFrameDecoder(int var1, ByteBuf... var2) {
      this(var1, true, var2);
   }

   public DelimiterBasedFrameDecoder(int var1, boolean var2, ByteBuf... var3) {
      this(var1, var2, true, var3);
   }

   public DelimiterBasedFrameDecoder(int var1, boolean var2, boolean var3, ByteBuf... var4) {
      super();
      validateMaxFrameLength(var1);
      if (var4 == null) {
         throw new NullPointerException("delimiters");
      } else if (var4.length == 0) {
         throw new IllegalArgumentException("empty delimiters");
      } else {
         if (isLineBased(var4) && !this.isSubclass()) {
            this.lineBasedDecoder = new LineBasedFrameDecoder(var1, var2, var3);
            this.delimiters = null;
         } else {
            this.delimiters = new ByteBuf[var4.length];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               ByteBuf var6 = var4[var5];
               validateDelimiter(var6);
               this.delimiters[var5] = var6.slice(var6.readerIndex(), var6.readableBytes());
            }

            this.lineBasedDecoder = null;
         }

         this.maxFrameLength = var1;
         this.stripDelimiter = var2;
         this.failFast = var3;
      }
   }

   private static boolean isLineBased(ByteBuf[] var0) {
      if (var0.length != 2) {
         return false;
      } else {
         ByteBuf var1 = var0[0];
         ByteBuf var2 = var0[1];
         if (var1.capacity() < var2.capacity()) {
            var1 = var0[1];
            var2 = var0[0];
         }

         return var1.capacity() == 2 && var2.capacity() == 1 && var1.getByte(0) == 13 && var1.getByte(1) == 10 && var2.getByte(0) == 10;
      }
   }

   private boolean isSubclass() {
      return this.getClass() != DelimiterBasedFrameDecoder.class;
   }

   protected final void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      Object var4 = this.decode(var1, var2);
      if (var4 != null) {
         var3.add(var4);
      }

   }

   protected Object decode(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      if (this.lineBasedDecoder != null) {
         return this.lineBasedDecoder.decode(var1, var2);
      } else {
         int var3 = 2147483647;
         ByteBuf var4 = null;
         ByteBuf[] var5 = this.delimiters;
         int var6 = var5.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            ByteBuf var8 = var5[var7];
            int var9 = indexOf(var2, var8);
            if (var9 >= 0 && var9 < var3) {
               var3 = var9;
               var4 = var8;
            }
         }

         if (var4 != null) {
            int var10 = var4.capacity();
            if (this.discardingTooLongFrame) {
               this.discardingTooLongFrame = false;
               var2.skipBytes(var3 + var10);
               var7 = this.tooLongFrameLength;
               this.tooLongFrameLength = 0;
               if (!this.failFast) {
                  this.fail((long)var7);
               }

               return null;
            } else if (var3 > this.maxFrameLength) {
               var2.skipBytes(var3 + var10);
               this.fail((long)var3);
               return null;
            } else {
               ByteBuf var11;
               if (this.stripDelimiter) {
                  var11 = var2.readRetainedSlice(var3);
                  var2.skipBytes(var10);
               } else {
                  var11 = var2.readRetainedSlice(var3 + var10);
               }

               return var11;
            }
         } else {
            if (!this.discardingTooLongFrame) {
               if (var2.readableBytes() > this.maxFrameLength) {
                  this.tooLongFrameLength = var2.readableBytes();
                  var2.skipBytes(var2.readableBytes());
                  this.discardingTooLongFrame = true;
                  if (this.failFast) {
                     this.fail((long)this.tooLongFrameLength);
                  }
               }
            } else {
               this.tooLongFrameLength += var2.readableBytes();
               var2.skipBytes(var2.readableBytes());
            }

            return null;
         }
      }
   }

   private void fail(long var1) {
      if (var1 > 0L) {
         throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + var1 + " - discarded");
      } else {
         throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
      }
   }

   private static int indexOf(ByteBuf var0, ByteBuf var1) {
      for(int var2 = var0.readerIndex(); var2 < var0.writerIndex(); ++var2) {
         int var3 = var2;

         int var4;
         for(var4 = 0; var4 < var1.capacity() && var0.getByte(var3) == var1.getByte(var4); ++var4) {
            ++var3;
            if (var3 == var0.writerIndex() && var4 != var1.capacity() - 1) {
               return -1;
            }
         }

         if (var4 == var1.capacity()) {
            return var2 - var0.readerIndex();
         }
      }

      return -1;
   }

   private static void validateDelimiter(ByteBuf var0) {
      if (var0 == null) {
         throw new NullPointerException("delimiter");
      } else if (!var0.isReadable()) {
         throw new IllegalArgumentException("empty delimiter");
      }
   }

   private static void validateMaxFrameLength(int var0) {
      if (var0 <= 0) {
         throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + var0);
      }
   }
}
