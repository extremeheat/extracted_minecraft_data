package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;

public class DefaultDnsRecordDecoder implements DnsRecordDecoder {
   static final String ROOT = ".";

   protected DefaultDnsRecordDecoder() {
      super();
   }

   public final DnsQuestion decodeQuestion(ByteBuf var1) throws Exception {
      String var2 = decodeName(var1);
      DnsRecordType var3 = DnsRecordType.valueOf(var1.readUnsignedShort());
      int var4 = var1.readUnsignedShort();
      return new DefaultDnsQuestion(var2, var3, var4);
   }

   public final <T extends DnsRecord> T decodeRecord(ByteBuf var1) throws Exception {
      int var2 = var1.readerIndex();
      String var3 = decodeName(var1);
      int var4 = var1.writerIndex();
      if (var4 - var2 < 10) {
         var1.readerIndex(var2);
         return null;
      } else {
         DnsRecordType var5 = DnsRecordType.valueOf(var1.readUnsignedShort());
         int var6 = var1.readUnsignedShort();
         long var7 = var1.readUnsignedInt();
         int var9 = var1.readUnsignedShort();
         int var10 = var1.readerIndex();
         if (var4 - var10 < var9) {
            var1.readerIndex(var2);
            return null;
         } else {
            DnsRecord var11 = this.decodeRecord(var3, var5, var6, var7, var1, var10, var9);
            var1.readerIndex(var10 + var9);
            return var11;
         }
      }
   }

   protected DnsRecord decodeRecord(String var1, DnsRecordType var2, int var3, long var4, ByteBuf var6, int var7, int var8) throws Exception {
      return (DnsRecord)(var2 == DnsRecordType.PTR ? new DefaultDnsPtrRecord(var1, var3, var4, this.decodeName0(var6.duplicate().setIndex(var7, var7 + var8))) : new DefaultDnsRawRecord(var1, var2, var3, var4, var6.retainedDuplicate().setIndex(var7, var7 + var8)));
   }

   protected String decodeName0(ByteBuf var1) {
      return decodeName(var1);
   }

   public static String decodeName(ByteBuf var0) {
      int var1 = -1;
      int var2 = 0;
      int var3 = var0.writerIndex();
      int var4 = var0.readableBytes();
      if (var4 == 0) {
         return ".";
      } else {
         StringBuilder var5 = new StringBuilder(var4 << 1);

         while(var0.isReadable()) {
            short var6 = var0.readUnsignedByte();
            boolean var7 = (var6 & 192) == 192;
            if (var7) {
               if (var1 == -1) {
                  var1 = var0.readerIndex() + 1;
               }

               if (!var0.isReadable()) {
                  throw new CorruptedFrameException("truncated pointer in a name");
               }

               int var8 = (var6 & 63) << 8 | var0.readUnsignedByte();
               if (var8 >= var3) {
                  throw new CorruptedFrameException("name has an out-of-range pointer");
               }

               var0.readerIndex(var8);
               var2 += 2;
               if (var2 >= var3) {
                  throw new CorruptedFrameException("name contains a loop.");
               }
            } else {
               if (var6 == 0) {
                  break;
               }

               if (!var0.isReadable(var6)) {
                  throw new CorruptedFrameException("truncated label in a name");
               }

               var5.append(var0.toString(var0.readerIndex(), var6, CharsetUtil.UTF_8)).append('.');
               var0.skipBytes(var6);
            }
         }

         if (var1 != -1) {
            var0.readerIndex(var1);
         }

         if (var5.length() == 0) {
            return ".";
         } else {
            if (var5.charAt(var5.length() - 1) != '.') {
               var5.append('.');
            }

            return var5.toString();
         }
      }
   }
}
