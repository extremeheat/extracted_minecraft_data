package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class DatagramPacketEncoder<M> extends MessageToMessageEncoder<AddressedEnvelope<M, InetSocketAddress>> {
   private final MessageToMessageEncoder<? super M> encoder;

   public DatagramPacketEncoder(MessageToMessageEncoder<? super M> var1) {
      super();
      this.encoder = (MessageToMessageEncoder)ObjectUtil.checkNotNull(var1, "encoder");
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      if (!super.acceptOutboundMessage(var1)) {
         return false;
      } else {
         AddressedEnvelope var2 = (AddressedEnvelope)var1;
         return this.encoder.acceptOutboundMessage(var2.content()) && var2.sender() instanceof InetSocketAddress && var2.recipient() instanceof InetSocketAddress;
      }
   }

   protected void encode(ChannelHandlerContext var1, AddressedEnvelope<M, InetSocketAddress> var2, List<Object> var3) throws Exception {
      assert var3.isEmpty();

      this.encoder.encode(var1, var2.content(), var3);
      if (var3.size() != 1) {
         throw new EncoderException(StringUtil.simpleClassName((Object)this.encoder) + " must produce only one message.");
      } else {
         Object var4 = var3.get(0);
         if (var4 instanceof ByteBuf) {
            var3.set(0, new DatagramPacket((ByteBuf)var4, (InetSocketAddress)var2.recipient(), (InetSocketAddress)var2.sender()));
         } else {
            throw new EncoderException(StringUtil.simpleClassName((Object)this.encoder) + " must produce only ByteBuf.");
         }
      }
   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      this.encoder.bind(var1, var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      this.encoder.connect(var1, var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.encoder.disconnect(var1, var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.encoder.close(var1, var2);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.encoder.deregister(var1, var2);
   }

   public void read(ChannelHandlerContext var1) throws Exception {
      this.encoder.read(var1);
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      this.encoder.flush(var1);
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.encoder.handlerAdded(var1);
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.encoder.handlerRemoved(var1);
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      this.encoder.exceptionCaught(var1, var2);
   }

   public boolean isSharable() {
      return this.encoder.isSharable();
   }
}
