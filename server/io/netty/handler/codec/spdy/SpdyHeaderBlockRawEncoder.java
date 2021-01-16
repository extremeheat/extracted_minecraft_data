package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.Set;

public class SpdyHeaderBlockRawEncoder extends SpdyHeaderBlockEncoder {
   private final int version;

   public SpdyHeaderBlockRawEncoder(SpdyVersion var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("version");
      } else {
         this.version = var1.getVersion();
      }
   }

   private static void setLengthField(ByteBuf var0, int var1, int var2) {
      var0.setInt(var1, var2);
   }

   private static void writeLengthField(ByteBuf var0, int var1) {
      var0.writeInt(var1);
   }

   public ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception {
      Set var3 = var2.headers().names();
      int var4 = var3.size();
      if (var4 == 0) {
         return Unpooled.EMPTY_BUFFER;
      } else if (var4 > 65535) {
         throw new IllegalArgumentException("header block contains too many headers");
      } else {
         ByteBuf var5 = var1.heapBuffer();
         writeLengthField(var5, var4);
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            CharSequence var7 = (CharSequence)var6.next();
            writeLengthField(var5, var7.length());
            ByteBufUtil.writeAscii(var5, var7);
            int var8 = var5.writerIndex();
            int var9 = 0;
            writeLengthField(var5, var9);
            Iterator var10 = var2.headers().getAll(var7).iterator();

            while(var10.hasNext()) {
               CharSequence var11 = (CharSequence)var10.next();
               int var12 = var11.length();
               if (var12 > 0) {
                  ByteBufUtil.writeAscii(var5, var11);
                  var5.writeByte(0);
                  var9 += var12 + 1;
               }
            }

            if (var9 != 0) {
               --var9;
            }

            if (var9 > 65535) {
               throw new IllegalArgumentException("header exceeds allowable length: " + var7);
            }

            if (var9 > 0) {
               setLengthField(var5, var8, var9);
               var5.writerIndex(var5.writerIndex() - 1);
            }
         }

         return var5;
      }
   }

   void end() {
   }
}
