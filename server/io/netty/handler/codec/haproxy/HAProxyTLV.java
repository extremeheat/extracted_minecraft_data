package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.ObjectUtil;

public class HAProxyTLV extends DefaultByteBufHolder {
   private final HAProxyTLV.Type type;
   private final byte typeByteValue;

   HAProxyTLV(HAProxyTLV.Type var1, byte var2, ByteBuf var3) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "type");
      this.type = var1;
      this.typeByteValue = var2;
   }

   public HAProxyTLV.Type type() {
      return this.type;
   }

   public byte typeByteValue() {
      return this.typeByteValue;
   }

   public HAProxyTLV copy() {
      return this.replace(this.content().copy());
   }

   public HAProxyTLV duplicate() {
      return this.replace(this.content().duplicate());
   }

   public HAProxyTLV retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public HAProxyTLV replace(ByteBuf var1) {
      return new HAProxyTLV(this.type, this.typeByteValue, var1);
   }

   public HAProxyTLV retain() {
      super.retain();
      return this;
   }

   public HAProxyTLV retain(int var1) {
      super.retain(var1);
      return this;
   }

   public HAProxyTLV touch() {
      super.touch();
      return this;
   }

   public HAProxyTLV touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public static enum Type {
      PP2_TYPE_ALPN,
      PP2_TYPE_AUTHORITY,
      PP2_TYPE_SSL,
      PP2_TYPE_SSL_VERSION,
      PP2_TYPE_SSL_CN,
      PP2_TYPE_NETNS,
      OTHER;

      private Type() {
      }

      public static HAProxyTLV.Type typeForByteValue(byte var0) {
         switch(var0) {
         case 1:
            return PP2_TYPE_ALPN;
         case 2:
            return PP2_TYPE_AUTHORITY;
         case 32:
            return PP2_TYPE_SSL;
         case 33:
            return PP2_TYPE_SSL_VERSION;
         case 34:
            return PP2_TYPE_SSL_CN;
         case 48:
            return PP2_TYPE_NETNS;
         default:
            return OTHER;
         }
      }
   }
}
