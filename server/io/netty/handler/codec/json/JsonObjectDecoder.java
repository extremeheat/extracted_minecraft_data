package io.netty.handler.codec.json;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class JsonObjectDecoder extends ByteToMessageDecoder {
   private static final int ST_CORRUPTED = -1;
   private static final int ST_INIT = 0;
   private static final int ST_DECODING_NORMAL = 1;
   private static final int ST_DECODING_ARRAY_STREAM = 2;
   private int openBraces;
   private int idx;
   private int lastReaderIndex;
   private int state;
   private boolean insideString;
   private final int maxObjectLength;
   private final boolean streamArrayElements;

   public JsonObjectDecoder() {
      this(1048576);
   }

   public JsonObjectDecoder(int var1) {
      this(var1, false);
   }

   public JsonObjectDecoder(boolean var1) {
      this(1048576, var1);
   }

   public JsonObjectDecoder(int var1, boolean var2) {
      super();
      if (var1 < 1) {
         throw new IllegalArgumentException("maxObjectLength must be a positive int");
      } else {
         this.maxObjectLength = var1;
         this.streamArrayElements = var2;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.state == -1) {
         var2.skipBytes(var2.readableBytes());
      } else {
         if (this.idx > var2.readerIndex() && this.lastReaderIndex != var2.readerIndex()) {
            this.idx = var2.readerIndex() + (this.idx - this.lastReaderIndex);
         }

         int var4 = this.idx;
         int var5 = var2.writerIndex();
         if (var5 > this.maxObjectLength) {
            var2.skipBytes(var2.readableBytes());
            this.reset();
            throw new TooLongFrameException("object length exceeds " + this.maxObjectLength + ": " + var5 + " bytes discarded");
         } else {
            for(; var4 < var5; ++var4) {
               byte var6 = var2.getByte(var4);
               if (this.state == 1) {
                  this.decodeByte(var6, var2, var4);
                  if (this.openBraces == 0) {
                     ByteBuf var9 = this.extractObject(var1, var2, var2.readerIndex(), var4 + 1 - var2.readerIndex());
                     if (var9 != null) {
                        var3.add(var9);
                     }

                     var2.readerIndex(var4 + 1);
                     this.reset();
                  }
               } else if (this.state == 2) {
                  this.decodeByte(var6, var2, var4);
                  if (!this.insideString && (this.openBraces == 1 && var6 == 44 || this.openBraces == 0 && var6 == 93)) {
                     int var7;
                     for(var7 = var2.readerIndex(); Character.isWhitespace(var2.getByte(var7)); ++var7) {
                        var2.skipBytes(1);
                     }

                     for(var7 = var4 - 1; var7 >= var2.readerIndex() && Character.isWhitespace(var2.getByte(var7)); --var7) {
                     }

                     ByteBuf var8 = this.extractObject(var1, var2, var2.readerIndex(), var7 + 1 - var2.readerIndex());
                     if (var8 != null) {
                        var3.add(var8);
                     }

                     var2.readerIndex(var4 + 1);
                     if (var6 == 93) {
                        this.reset();
                     }
                  }
               } else if (var6 != 123 && var6 != 91) {
                  if (!Character.isWhitespace(var6)) {
                     this.state = -1;
                     throw new CorruptedFrameException("invalid JSON received at byte position " + var4 + ": " + ByteBufUtil.hexDump(var2));
                  }

                  var2.skipBytes(1);
               } else {
                  this.initDecoding(var6);
                  if (this.state == 2) {
                     var2.skipBytes(1);
                  }
               }
            }

            if (var2.readableBytes() == 0) {
               this.idx = 0;
            } else {
               this.idx = var4;
            }

            this.lastReaderIndex = var2.readerIndex();
         }
      }
   }

   protected ByteBuf extractObject(ChannelHandlerContext var1, ByteBuf var2, int var3, int var4) {
      return var2.retainedSlice(var3, var4);
   }

   private void decodeByte(byte var1, ByteBuf var2, int var3) {
      if ((var1 == 123 || var1 == 91) && !this.insideString) {
         ++this.openBraces;
      } else if ((var1 == 125 || var1 == 93) && !this.insideString) {
         --this.openBraces;
      } else if (var1 == 34) {
         if (!this.insideString) {
            this.insideString = true;
         } else {
            int var4 = 0;
            --var3;

            while(var3 >= 0 && var2.getByte(var3) == 92) {
               ++var4;
               --var3;
            }

            if (var4 % 2 == 0) {
               this.insideString = false;
            }
         }
      }

   }

   private void initDecoding(byte var1) {
      this.openBraces = 1;
      if (var1 == 91 && this.streamArrayElements) {
         this.state = 2;
      } else {
         this.state = 1;
      }

   }

   private void reset() {
      this.insideString = false;
      this.state = 0;
      this.openBraces = 0;
   }
}
