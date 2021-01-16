package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsQuery, InetSocketAddress>> {
   private final DnsRecordEncoder recordEncoder;

   public DatagramDnsQueryEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public DatagramDnsQueryEncoder(DnsRecordEncoder var1) {
      super();
      this.recordEncoder = (DnsRecordEncoder)ObjectUtil.checkNotNull(var1, "recordEncoder");
   }

   protected void encode(ChannelHandlerContext var1, AddressedEnvelope<DnsQuery, InetSocketAddress> var2, List<Object> var3) throws Exception {
      InetSocketAddress var4 = (InetSocketAddress)var2.recipient();
      DnsQuery var5 = (DnsQuery)var2.content();
      ByteBuf var6 = this.allocateBuffer(var1, var2);
      boolean var7 = false;

      try {
         encodeHeader(var5, var6);
         this.encodeQuestions(var5, var6);
         this.encodeRecords(var5, DnsSection.ADDITIONAL, var6);
         var7 = true;
      } finally {
         if (!var7) {
            var6.release();
         }

      }

      var3.add(new DatagramPacket(var6, var4, (InetSocketAddress)null));
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, AddressedEnvelope<DnsQuery, InetSocketAddress> var2) throws Exception {
      return var1.alloc().ioBuffer(1024);
   }

   private static void encodeHeader(DnsQuery var0, ByteBuf var1) {
      var1.writeShort(var0.id());
      byte var2 = 0;
      int var3 = var2 | (var0.opCode().byteValue() & 255) << 14;
      if (var0.isRecursionDesired()) {
         var3 |= 256;
      }

      var1.writeShort(var3);
      var1.writeShort(var0.count(DnsSection.QUESTION));
      var1.writeShort(0);
      var1.writeShort(0);
      var1.writeShort(var0.count(DnsSection.ADDITIONAL));
   }

   private void encodeQuestions(DnsQuery var1, ByteBuf var2) throws Exception {
      int var3 = var1.count(DnsSection.QUESTION);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.recordEncoder.encodeQuestion((DnsQuestion)var1.recordAt(DnsSection.QUESTION, var4), var2);
      }

   }

   private void encodeRecords(DnsQuery var1, DnsSection var2, ByteBuf var3) throws Exception {
      int var4 = var1.count(var2);

      for(int var5 = 0; var5 < var4; ++var5) {
         this.recordEncoder.encodeRecord(var1.recordAt(var2, var5), var3);
      }

   }
}
