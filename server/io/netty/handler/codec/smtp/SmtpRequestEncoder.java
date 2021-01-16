package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class SmtpRequestEncoder extends MessageToMessageEncoder<Object> {
   private static final int CRLF_SHORT = 3338;
   private static final byte SP = 32;
   private static final ByteBuf DOT_CRLF_BUFFER = Unpooled.unreleasableBuffer(Unpooled.directBuffer(3).writeByte(46).writeByte(13).writeByte(10));
   private boolean contentExpected;

   public SmtpRequestEncoder() {
      super();
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 instanceof SmtpRequest || var1 instanceof SmtpContent;
   }

   protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
      if (var2 instanceof SmtpRequest) {
         SmtpRequest var4 = (SmtpRequest)var2;
         if (this.contentExpected) {
            if (!var4.command().equals(SmtpCommand.RSET)) {
               throw new IllegalStateException("SmtpContent expected");
            }

            this.contentExpected = false;
         }

         boolean var5 = true;
         ByteBuf var6 = var1.alloc().buffer();

         try {
            var4.command().encode(var6);
            writeParameters(var4.parameters(), var6);
            ByteBufUtil.writeShortBE(var6, 3338);
            var3.add(var6);
            var5 = false;
            if (var4.command().isContentExpected()) {
               this.contentExpected = true;
            }
         } finally {
            if (var5) {
               var6.release();
            }

         }
      }

      if (var2 instanceof SmtpContent) {
         if (!this.contentExpected) {
            throw new IllegalStateException("No SmtpContent expected");
         }

         ByteBuf var10 = ((SmtpContent)var2).content();
         var3.add(var10.retain());
         if (var2 instanceof LastSmtpContent) {
            var3.add(DOT_CRLF_BUFFER.retainedDuplicate());
            this.contentExpected = false;
         }
      }

   }

   private static void writeParameters(List<CharSequence> var0, ByteBuf var1) {
      if (!var0.isEmpty()) {
         var1.writeByte(32);
         if (var0 instanceof RandomAccess) {
            int var2 = var0.size() - 1;

            for(int var3 = 0; var3 < var2; ++var3) {
               ByteBufUtil.writeAscii(var1, (CharSequence)var0.get(var3));
               var1.writeByte(32);
            }

            ByteBufUtil.writeAscii(var1, (CharSequence)var0.get(var2));
         } else {
            Iterator var4 = var0.iterator();

            while(true) {
               ByteBufUtil.writeAscii(var1, (CharSequence)var4.next());
               if (!var4.hasNext()) {
                  break;
               }

               var1.writeByte(32);
            }
         }

      }
   }
}
