package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope<M, A extends SocketAddress> implements AddressedEnvelope<M, A> {
   private final M message;
   private final A sender;
   private final A recipient;

   public DefaultAddressedEnvelope(M var1, A var2, A var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("message");
      } else if (var2 == null && var3 == null) {
         throw new NullPointerException("recipient and sender");
      } else {
         this.message = var1;
         this.sender = var3;
         this.recipient = var2;
      }
   }

   public DefaultAddressedEnvelope(M var1, A var2) {
      this(var1, var2, (SocketAddress)null);
   }

   public M content() {
      return this.message;
   }

   public A sender() {
      return this.sender;
   }

   public A recipient() {
      return this.recipient;
   }

   public int refCnt() {
      return this.message instanceof ReferenceCounted ? ((ReferenceCounted)this.message).refCnt() : 1;
   }

   public AddressedEnvelope<M, A> retain() {
      ReferenceCountUtil.retain(this.message);
      return this;
   }

   public AddressedEnvelope<M, A> retain(int var1) {
      ReferenceCountUtil.retain(this.message, var1);
      return this;
   }

   public boolean release() {
      return ReferenceCountUtil.release(this.message);
   }

   public boolean release(int var1) {
      return ReferenceCountUtil.release(this.message, var1);
   }

   public AddressedEnvelope<M, A> touch() {
      ReferenceCountUtil.touch(this.message);
      return this;
   }

   public AddressedEnvelope<M, A> touch(Object var1) {
      ReferenceCountUtil.touch(this.message, var1);
      return this;
   }

   public String toString() {
      return this.sender != null ? StringUtil.simpleClassName((Object)this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')' : StringUtil.simpleClassName((Object)this) + "(=> " + this.recipient + ", " + this.message + ')';
   }
}
