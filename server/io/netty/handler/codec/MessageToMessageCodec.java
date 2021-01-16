package io.netty.handler.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN> extends ChannelDuplexHandler {
   private final MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<Object>() {
      public boolean acceptOutboundMessage(Object var1) throws Exception {
         return MessageToMessageCodec.this.acceptOutboundMessage(var1);
      }

      protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
         MessageToMessageCodec.this.encode(var1, var2, var3);
      }
   };
   private final MessageToMessageDecoder<Object> decoder = new MessageToMessageDecoder<Object>() {
      public boolean acceptInboundMessage(Object var1) throws Exception {
         return MessageToMessageCodec.this.acceptInboundMessage(var1);
      }

      protected void decode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
         MessageToMessageCodec.this.decode(var1, var2, var3);
      }
   };
   private final TypeParameterMatcher inboundMsgMatcher;
   private final TypeParameterMatcher outboundMsgMatcher;

   protected MessageToMessageCodec() {
      super();
      this.inboundMsgMatcher = TypeParameterMatcher.find(this, MessageToMessageCodec.class, "INBOUND_IN");
      this.outboundMsgMatcher = TypeParameterMatcher.find(this, MessageToMessageCodec.class, "OUTBOUND_IN");
   }

   protected MessageToMessageCodec(Class<? extends INBOUND_IN> var1, Class<? extends OUTBOUND_IN> var2) {
      super();
      this.inboundMsgMatcher = TypeParameterMatcher.get(var1);
      this.outboundMsgMatcher = TypeParameterMatcher.get(var2);
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      this.decoder.channelRead(var1, var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      this.encoder.write(var1, var2, var3);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return this.inboundMsgMatcher.match(var1);
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return this.outboundMsgMatcher.match(var1);
   }

   protected abstract void encode(ChannelHandlerContext var1, OUTBOUND_IN var2, List<Object> var3) throws Exception;

   protected abstract void decode(ChannelHandlerContext var1, INBOUND_IN var2, List<Object> var3) throws Exception;
}
