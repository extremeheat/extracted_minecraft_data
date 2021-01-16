package io.netty.handler.codec.xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class XmlFrameDecoder extends ByteToMessageDecoder {
   private final int maxFrameLength;

   public XmlFrameDecoder(int var1) {
      super();
      if (var1 < 1) {
         throw new IllegalArgumentException("maxFrameLength must be a positive int");
      } else {
         this.maxFrameLength = var1;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      long var7 = 0L;
      int var9 = 0;
      int var10 = 0;
      int var11 = var2.writerIndex();
      if (var11 > this.maxFrameLength) {
         var2.skipBytes(var2.readableBytes());
         this.fail((long)var11);
      } else {
         int var12;
         for(var12 = var2.readerIndex(); var12 < var11; ++var12) {
            byte var13 = var2.getByte(var12);
            if (!var4 && Character.isWhitespace(var13)) {
               ++var10;
            } else {
               if (!var4 && var13 != 60) {
                  fail(var1);
                  var2.skipBytes(var2.readableBytes());
                  return;
               }

               byte var14;
               if (!var6 && var13 == 60) {
                  var4 = true;
                  if (var12 < var11 - 1) {
                     var14 = var2.getByte(var12 + 1);
                     if (var14 == 47) {
                        for(int var15 = var12 + 2; var15 <= var11 - 1; ++var15) {
                           if (var2.getByte(var15) == 62) {
                              --var7;
                              break;
                           }
                        }
                     } else if (isValidStartCharForXmlElement(var14)) {
                        var5 = true;
                        ++var7;
                     } else if (var14 == 33) {
                        if (isCommentBlockStart(var2, var12)) {
                           ++var7;
                        } else if (isCDATABlockStart(var2, var12)) {
                           ++var7;
                           var6 = true;
                        }
                     } else if (var14 == 63) {
                        ++var7;
                     }
                  }
               } else if (!var6 && var13 == 47) {
                  if (var12 < var11 - 1 && var2.getByte(var12 + 1) == 62) {
                     --var7;
                  }
               } else if (var13 == 62) {
                  var9 = var12 + 1;
                  if (var12 - 1 > -1) {
                     var14 = var2.getByte(var12 - 1);
                     if (!var6) {
                        if (var14 == 63) {
                           --var7;
                        } else if (var14 == 45 && var12 - 2 > -1 && var2.getByte(var12 - 2) == 45) {
                           --var7;
                        }
                     } else if (var14 == 93 && var12 - 2 > -1 && var2.getByte(var12 - 2) == 93) {
                        --var7;
                        var6 = false;
                     }
                  }

                  if (var5 && var7 == 0L) {
                     break;
                  }
               }
            }
         }

         var12 = var2.readerIndex();
         int var16 = var9 - var12;
         if (var7 == 0L && var16 > 0) {
            if (var12 + var16 >= var11) {
               var16 = var2.readableBytes();
            }

            ByteBuf var17 = extractFrame(var2, var12 + var10, var16 - var10);
            var2.skipBytes(var16);
            var3.add(var17);
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

   private static void fail(ChannelHandlerContext var0) {
      var0.fireExceptionCaught(new CorruptedFrameException("frame contains content before the xml starts"));
   }

   private static ByteBuf extractFrame(ByteBuf var0, int var1, int var2) {
      return var0.copy(var1, var2);
   }

   private static boolean isValidStartCharForXmlElement(byte var0) {
      return var0 >= 97 && var0 <= 122 || var0 >= 65 && var0 <= 90 || var0 == 58 || var0 == 95;
   }

   private static boolean isCommentBlockStart(ByteBuf var0, int var1) {
      return var1 < var0.writerIndex() - 3 && var0.getByte(var1 + 2) == 45 && var0.getByte(var1 + 3) == 45;
   }

   private static boolean isCDATABlockStart(ByteBuf var0, int var1) {
      return var1 < var0.writerIndex() - 8 && var0.getByte(var1 + 2) == 91 && var0.getByte(var1 + 3) == 67 && var0.getByte(var1 + 4) == 68 && var0.getByte(var1 + 5) == 65 && var0.getByte(var1 + 6) == 84 && var0.getByte(var1 + 7) == 65 && var0.getByte(var1 + 8) == 91;
   }
}
