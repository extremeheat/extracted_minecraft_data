package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.internal.StringUtil;

public class DefaultDnsRecordEncoder implements DnsRecordEncoder {
   private static final int PREFIX_MASK = 7;

   protected DefaultDnsRecordEncoder() {
      super();
   }

   public final void encodeQuestion(DnsQuestion var1, ByteBuf var2) throws Exception {
      this.encodeName(var1.name(), var2);
      var2.writeShort(var1.type().intValue());
      var2.writeShort(var1.dnsClass());
   }

   public void encodeRecord(DnsRecord var1, ByteBuf var2) throws Exception {
      if (var1 instanceof DnsQuestion) {
         this.encodeQuestion((DnsQuestion)var1, var2);
      } else if (var1 instanceof DnsPtrRecord) {
         this.encodePtrRecord((DnsPtrRecord)var1, var2);
      } else if (var1 instanceof DnsOptEcsRecord) {
         this.encodeOptEcsRecord((DnsOptEcsRecord)var1, var2);
      } else if (var1 instanceof DnsOptPseudoRecord) {
         this.encodeOptPseudoRecord((DnsOptPseudoRecord)var1, var2);
      } else {
         if (!(var1 instanceof DnsRawRecord)) {
            throw new UnsupportedMessageTypeException(StringUtil.simpleClassName((Object)var1));
         }

         this.encodeRawRecord((DnsRawRecord)var1, var2);
      }

   }

   private void encodeRecord0(DnsRecord var1, ByteBuf var2) throws Exception {
      this.encodeName(var1.name(), var2);
      var2.writeShort(var1.type().intValue());
      var2.writeShort(var1.dnsClass());
      var2.writeInt((int)var1.timeToLive());
   }

   private void encodePtrRecord(DnsPtrRecord var1, ByteBuf var2) throws Exception {
      this.encodeRecord0(var1, var2);
      this.encodeName(var1.hostname(), var2);
   }

   private void encodeOptPseudoRecord(DnsOptPseudoRecord var1, ByteBuf var2) throws Exception {
      this.encodeRecord0(var1, var2);
      var2.writeShort(0);
   }

   private void encodeOptEcsRecord(DnsOptEcsRecord var1, ByteBuf var2) throws Exception {
      this.encodeRecord0(var1, var2);
      int var3 = var1.sourcePrefixLength();
      int var4 = var1.scopePrefixLength();
      int var5 = var3 & 7;
      byte[] var6 = var1.address();
      int var7 = var6.length << 3;
      if (var7 >= var3 && var3 >= 0) {
         short var8 = (short)(var6.length == 4 ? InternetProtocolFamily.IPv4.addressNumber() : InternetProtocolFamily.IPv6.addressNumber());
         int var9 = calculateEcsAddressLength(var3, var5);
         int var10 = 8 + var9;
         var2.writeShort(var10);
         var2.writeShort(8);
         var2.writeShort(var10 - 4);
         var2.writeShort(var8);
         var2.writeByte(var3);
         var2.writeByte(var4);
         if (var5 > 0) {
            int var11 = var9 - 1;
            var2.writeBytes((byte[])var6, 0, var11);
            var2.writeByte(padWithZeros(var6[var11], var5));
         } else {
            var2.writeBytes((byte[])var6, 0, var9);
         }

      } else {
         throw new IllegalArgumentException(var3 + ": " + var3 + " (expected: 0 >= " + var7 + ')');
      }
   }

   static int calculateEcsAddressLength(int var0, int var1) {
      return (var0 >>> 3) + (var1 != 0 ? 1 : 0);
   }

   private void encodeRawRecord(DnsRawRecord var1, ByteBuf var2) throws Exception {
      this.encodeRecord0(var1, var2);
      ByteBuf var3 = var1.content();
      int var4 = var3.readableBytes();
      var2.writeShort(var4);
      var2.writeBytes(var3, var3.readerIndex(), var4);
   }

   protected void encodeName(String var1, ByteBuf var2) throws Exception {
      if (".".equals(var1)) {
         var2.writeByte(0);
      } else {
         String[] var3 = var1.split("\\.");
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            int var8 = var7.length();
            if (var8 == 0) {
               break;
            }

            var2.writeByte(var8);
            ByteBufUtil.writeAscii((ByteBuf)var2, var7);
         }

         var2.writeByte(0);
      }
   }

   private static byte padWithZeros(byte var0, int var1) {
      switch(var1) {
      case 0:
         return 0;
      case 1:
         return (byte)(128 & var0);
      case 2:
         return (byte)(192 & var0);
      case 3:
         return (byte)(224 & var0);
      case 4:
         return (byte)(240 & var0);
      case 5:
         return (byte)(248 & var0);
      case 6:
         return (byte)(252 & var0);
      case 7:
         return (byte)(254 & var0);
      case 8:
         return var0;
      default:
         throw new IllegalArgumentException("lowOrderBitsToPreserve: " + var1);
      }
   }
}
