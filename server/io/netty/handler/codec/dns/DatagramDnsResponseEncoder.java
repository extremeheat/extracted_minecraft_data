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
public class DatagramDnsResponseEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsResponse, InetSocketAddress>> {
   private final DnsRecordEncoder recordEncoder;

   public DatagramDnsResponseEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public DatagramDnsResponseEncoder(DnsRecordEncoder var1) {
      super();
      this.recordEncoder = (DnsRecordEncoder)ObjectUtil.checkNotNull(var1, "recordEncoder");
   }

   protected void encode(ChannelHandlerContext var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, List<Object> var3) throws Exception {
      InetSocketAddress var4 = (InetSocketAddress)var2.recipient();
      DnsResponse var5 = (DnsResponse)var2.content();
      ByteBuf var6 = this.allocateBuffer(var1, var2);
      boolean var7 = false;

      try {
         encodeHeader(var5, var6);
         this.encodeQuestions(var5, var6);
         this.encodeRecords(var5, DnsSection.ANSWER, var6);
         this.encodeRecords(var5, DnsSection.AUTHORITY, var6);
         this.encodeRecords(var5, DnsSection.ADDITIONAL, var6);
         var7 = true;
      } finally {
         if (!var7) {
            var6.release();
         }

      }

      var3.add(new DatagramPacket(var6, var4, (InetSocketAddress)null));
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2) throws Exception {
      return var1.alloc().ioBuffer(1024);
   }

   private static void encodeHeader(DnsResponse var0, ByteBuf var1) {
      var1.writeShort(var0.id());
      char var2 = '\u8000';
      int var3 = var2 | (var0.opCode().byteValue() & 255) << 11;
      if (var0.isAuthoritativeAnswer()) {
         var3 |= 1024;
      }

      if (var0.isTruncated()) {
         var3 |= 512;
      }

      if (var0.isRecursionDesired()) {
         var3 |= 256;
      }

      if (var0.isRecursionAvailable()) {
         var3 |= 128;
      }

      var3 |= var0.z() << 4;
      var3 |= var0.code().intValue();
      var1.writeShort(var3);
      var1.writeShort(var0.count(DnsSection.QUESTION));
      var1.writeShort(var0.count(DnsSection.ANSWER));
      var1.writeShort(var0.count(DnsSection.AUTHORITY));
      var1.writeShort(var0.count(DnsSection.ADDITIONAL));
   }

   private void encodeQuestions(DnsResponse var1, ByteBuf var2) throws Exception {
      int var3 = var1.count(DnsSection.QUESTION);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.recordEncoder.encodeQuestion((DnsQuestion)var1.recordAt(DnsSection.QUESTION, var4), var2);
      }

   }

   private void encodeRecords(DnsResponse var1, DnsSection var2, ByteBuf var3) throws Exception {
      int var4 = var1.count(var2);

      for(int var5 = 0; var5 < var4; ++var5) {
         this.recordEncoder.encodeRecord(var1.recordAt(var2, var5), var3);
      }

   }
}
