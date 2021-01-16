package io.netty.channel.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.DefaultAddressedEnvelope;
import java.net.InetSocketAddress;

public final class DatagramPacket extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress> implements ByteBufHolder {
   public DatagramPacket(ByteBuf var1, InetSocketAddress var2) {
      super(var1, var2);
   }

   public DatagramPacket(ByteBuf var1, InetSocketAddress var2, InetSocketAddress var3) {
      super(var1, var2, var3);
   }

   public DatagramPacket copy() {
      return this.replace(((ByteBuf)this.content()).copy());
   }

   public DatagramPacket duplicate() {
      return this.replace(((ByteBuf)this.content()).duplicate());
   }

   public DatagramPacket retainedDuplicate() {
      return this.replace(((ByteBuf)this.content()).retainedDuplicate());
   }

   public DatagramPacket replace(ByteBuf var1) {
      return new DatagramPacket(var1, (InetSocketAddress)this.recipient(), (InetSocketAddress)this.sender());
   }

   public DatagramPacket retain() {
      super.retain();
      return this;
   }

   public DatagramPacket retain(int var1) {
      super.retain(var1);
      return this;
   }

   public DatagramPacket touch() {
      super.touch();
      return this;
   }

   public DatagramPacket touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
