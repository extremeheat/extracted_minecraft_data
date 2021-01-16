package io.netty.handler.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder extends ByteToMessageDecoder {
   public ProtobufVarint32FrameDecoder() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var2.markReaderIndex();
      int var4 = var2.readerIndex();
      int var5 = readRawVarint32(var2);
      if (var4 != var2.readerIndex()) {
         if (var5 < 0) {
            throw new CorruptedFrameException("negative length: " + var5);
         } else {
            if (var2.readableBytes() < var5) {
               var2.resetReaderIndex();
            } else {
               var3.add(var2.readRetainedSlice(var5));
            }

         }
      }
   }

   private static int readRawVarint32(ByteBuf var0) {
      if (!var0.isReadable()) {
         return 0;
      } else {
         var0.markReaderIndex();
         byte var1 = var0.readByte();
         if (var1 >= 0) {
            return var1;
         } else {
            int var2 = var1 & 127;
            if (!var0.isReadable()) {
               var0.resetReaderIndex();
               return 0;
            } else {
               if ((var1 = var0.readByte()) >= 0) {
                  var2 |= var1 << 7;
               } else {
                  var2 |= (var1 & 127) << 7;
                  if (!var0.isReadable()) {
                     var0.resetReaderIndex();
                     return 0;
                  }

                  if ((var1 = var0.readByte()) >= 0) {
                     var2 |= var1 << 14;
                  } else {
                     var2 |= (var1 & 127) << 14;
                     if (!var0.isReadable()) {
                        var0.resetReaderIndex();
                        return 0;
                     }

                     if ((var1 = var0.readByte()) >= 0) {
                        var2 |= var1 << 21;
                     } else {
                        var2 |= (var1 & 127) << 21;
                        if (!var0.isReadable()) {
                           var0.resetReaderIndex();
                           return 0;
                        }

                        var2 |= (var1 = var0.readByte()) << 28;
                        if (var1 < 0) {
                           throw new CorruptedFrameException("malformed varint.");
                        }
                     }
                  }
               }

               return var2;
            }
         }
      }
   }
}
