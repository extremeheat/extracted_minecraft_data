package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;

public class DefaultBinaryMemcacheRequest extends AbstractBinaryMemcacheMessage implements BinaryMemcacheRequest {
   public static final byte REQUEST_MAGIC_BYTE = -128;
   private short reserved;

   public DefaultBinaryMemcacheRequest() {
      this((ByteBuf)null, (ByteBuf)null);
   }

   public DefaultBinaryMemcacheRequest(ByteBuf var1) {
      this(var1, (ByteBuf)null);
   }

   public DefaultBinaryMemcacheRequest(ByteBuf var1, ByteBuf var2) {
      super(var1, var2);
      this.setMagic((byte)-128);
   }

   public short reserved() {
      return this.reserved;
   }

   public BinaryMemcacheRequest setReserved(short var1) {
      this.reserved = var1;
      return this;
   }

   public BinaryMemcacheRequest retain() {
      super.retain();
      return this;
   }

   public BinaryMemcacheRequest retain(int var1) {
      super.retain(var1);
      return this;
   }

   public BinaryMemcacheRequest touch() {
      super.touch();
      return this;
   }

   public BinaryMemcacheRequest touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
