package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;

public class DefaultBinaryMemcacheResponse extends AbstractBinaryMemcacheMessage implements BinaryMemcacheResponse {
   public static final byte RESPONSE_MAGIC_BYTE = -127;
   private short status;

   public DefaultBinaryMemcacheResponse() {
      this((ByteBuf)null, (ByteBuf)null);
   }

   public DefaultBinaryMemcacheResponse(ByteBuf var1) {
      this(var1, (ByteBuf)null);
   }

   public DefaultBinaryMemcacheResponse(ByteBuf var1, ByteBuf var2) {
      super(var1, var2);
      this.setMagic((byte)-127);
   }

   public short status() {
      return this.status;
   }

   public BinaryMemcacheResponse setStatus(short var1) {
      this.status = var1;
      return this;
   }

   public BinaryMemcacheResponse retain() {
      super.retain();
      return this;
   }

   public BinaryMemcacheResponse retain(int var1) {
      super.retain(var1);
      return this;
   }

   public BinaryMemcacheResponse touch() {
      super.touch();
      return this;
   }

   public BinaryMemcacheResponse touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
