package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;
import java.util.List;

@ChannelHandler.Sharable
public class LengthFieldPrepender extends MessageToMessageEncoder<ByteBuf> {
   private final ByteOrder byteOrder;
   private final int lengthFieldLength;
   private final boolean lengthIncludesLengthFieldLength;
   private final int lengthAdjustment;

   public LengthFieldPrepender(int var1) {
      this(var1, false);
   }

   public LengthFieldPrepender(int var1, boolean var2) {
      this(var1, 0, var2);
   }

   public LengthFieldPrepender(int var1, int var2) {
      this(var1, var2, false);
   }

   public LengthFieldPrepender(int var1, int var2, boolean var3) {
      this(ByteOrder.BIG_ENDIAN, var1, var2, var3);
   }

   public LengthFieldPrepender(ByteOrder var1, int var2, int var3, boolean var4) {
      super();
      if (var2 != 1 && var2 != 2 && var2 != 3 && var2 != 4 && var2 != 8) {
         throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + var2);
      } else {
         ObjectUtil.checkNotNull(var1, "byteOrder");
         this.byteOrder = var1;
         this.lengthFieldLength = var2;
         this.lengthIncludesLengthFieldLength = var4;
         this.lengthAdjustment = var3;
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = var2.readableBytes() + this.lengthAdjustment;
      if (this.lengthIncludesLengthFieldLength) {
         var4 += this.lengthFieldLength;
      }

      if (var4 < 0) {
         throw new IllegalArgumentException("Adjusted frame length (" + var4 + ") is less than zero");
      } else {
         switch(this.lengthFieldLength) {
         case 1:
            if (var4 >= 256) {
               throw new IllegalArgumentException("length does not fit into a byte: " + var4);
            }

            var3.add(var1.alloc().buffer(1).order(this.byteOrder).writeByte((byte)var4));
            break;
         case 2:
            if (var4 >= 65536) {
               throw new IllegalArgumentException("length does not fit into a short integer: " + var4);
            }

            var3.add(var1.alloc().buffer(2).order(this.byteOrder).writeShort((short)var4));
            break;
         case 3:
            if (var4 >= 16777216) {
               throw new IllegalArgumentException("length does not fit into a medium integer: " + var4);
            }

            var3.add(var1.alloc().buffer(3).order(this.byteOrder).writeMedium(var4));
            break;
         case 4:
            var3.add(var1.alloc().buffer(4).order(this.byteOrder).writeInt(var4));
            break;
         case 5:
         case 6:
         case 7:
         default:
            throw new Error("should not reach here");
         case 8:
            var3.add(var1.alloc().buffer(8).order(this.byteOrder).writeLong((long)var4));
         }

         var3.add(var2.retain());
      }
   }
}
