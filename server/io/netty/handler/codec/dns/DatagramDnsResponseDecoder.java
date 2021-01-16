package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseDecoder extends MessageToMessageDecoder<DatagramPacket> {
   private final DnsRecordDecoder recordDecoder;

   public DatagramDnsResponseDecoder() {
      this(DnsRecordDecoder.DEFAULT);
   }

   public DatagramDnsResponseDecoder(DnsRecordDecoder var1) {
      super();
      this.recordDecoder = (DnsRecordDecoder)ObjectUtil.checkNotNull(var1, "recordDecoder");
   }

   protected void decode(ChannelHandlerContext var1, DatagramPacket var2, List<Object> var3) throws Exception {
      ByteBuf var4 = (ByteBuf)var2.content();
      DnsResponse var5 = newResponse(var2, var4);
      boolean var6 = false;

      try {
         int var7 = var4.readUnsignedShort();
         int var8 = var4.readUnsignedShort();
         int var9 = var4.readUnsignedShort();
         int var10 = var4.readUnsignedShort();
         this.decodeQuestions(var5, var4, var7);
         this.decodeRecords(var5, DnsSection.ANSWER, var4, var8);
         this.decodeRecords(var5, DnsSection.AUTHORITY, var4, var9);
         this.decodeRecords(var5, DnsSection.ADDITIONAL, var4, var10);
         var3.add(var5);
         var6 = true;
      } finally {
         if (!var6) {
            var5.release();
         }

      }

   }

   private static DnsResponse newResponse(DatagramPacket var0, ByteBuf var1) {
      int var2 = var1.readUnsignedShort();
      int var3 = var1.readUnsignedShort();
      if (var3 >> 15 == 0) {
         throw new CorruptedFrameException("not a response");
      } else {
         DatagramDnsResponse var4 = new DatagramDnsResponse((InetSocketAddress)var0.sender(), (InetSocketAddress)var0.recipient(), var2, DnsOpCode.valueOf((byte)(var3 >> 11 & 15)), DnsResponseCode.valueOf((byte)(var3 & 15)));
         var4.setRecursionDesired((var3 >> 8 & 1) == 1);
         var4.setAuthoritativeAnswer((var3 >> 10 & 1) == 1);
         var4.setTruncated((var3 >> 9 & 1) == 1);
         var4.setRecursionAvailable((var3 >> 7 & 1) == 1);
         var4.setZ(var3 >> 4 & 7);
         return var4;
      }
   }

   private void decodeQuestions(DnsResponse var1, ByteBuf var2, int var3) throws Exception {
      for(int var4 = var3; var4 > 0; --var4) {
         var1.addRecord(DnsSection.QUESTION, this.recordDecoder.decodeQuestion(var2));
      }

   }

   private void decodeRecords(DnsResponse var1, DnsSection var2, ByteBuf var3, int var4) throws Exception {
      for(int var5 = var4; var5 > 0; --var5) {
         DnsRecord var6 = this.recordDecoder.decodeRecord(var3);
         if (var6 == null) {
            break;
         }

         var1.addRecord(var2, var6);
      }

   }
}
