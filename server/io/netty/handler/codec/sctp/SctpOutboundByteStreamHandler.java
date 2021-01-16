package io.netty.handler.codec.sctp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class SctpOutboundByteStreamHandler extends MessageToMessageEncoder<ByteBuf> {
   private final int streamIdentifier;
   private final int protocolIdentifier;
   private final boolean unordered;

   public SctpOutboundByteStreamHandler(int var1, int var2) {
      this(var1, var2, false);
   }

   public SctpOutboundByteStreamHandler(int var1, int var2, boolean var3) {
      super();
      this.streamIdentifier = var1;
      this.protocolIdentifier = var2;
      this.unordered = var3;
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.unordered, var2.retain()));
   }
}
