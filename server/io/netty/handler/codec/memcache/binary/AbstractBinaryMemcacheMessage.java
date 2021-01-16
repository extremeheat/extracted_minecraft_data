package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.AbstractMemcacheObject;

public abstract class AbstractBinaryMemcacheMessage extends AbstractMemcacheObject implements BinaryMemcacheMessage {
   private ByteBuf key;
   private ByteBuf extras;
   private byte magic;
   private byte opcode;
   private short keyLength;
   private byte extrasLength;
   private byte dataType;
   private int totalBodyLength;
   private int opaque;
   private long cas;

   protected AbstractBinaryMemcacheMessage(ByteBuf var1, ByteBuf var2) {
      super();
      this.key = var1;
      this.keyLength = var1 == null ? 0 : (short)var1.readableBytes();
      this.extras = var2;
      this.extrasLength = var2 == null ? 0 : (byte)var2.readableBytes();
      this.totalBodyLength = this.keyLength + this.extrasLength;
   }

   public ByteBuf key() {
      return this.key;
   }

   public ByteBuf extras() {
      return this.extras;
   }

   public BinaryMemcacheMessage setKey(ByteBuf var1) {
      if (this.key != null) {
         this.key.release();
      }

      this.key = var1;
      short var2 = this.keyLength;
      this.keyLength = var1 == null ? 0 : (short)var1.readableBytes();
      this.totalBodyLength = this.totalBodyLength + this.keyLength - var2;
      return this;
   }

   public BinaryMemcacheMessage setExtras(ByteBuf var1) {
      if (this.extras != null) {
         this.extras.release();
      }

      this.extras = var1;
      short var2 = (short)this.extrasLength;
      this.extrasLength = var1 == null ? 0 : (byte)var1.readableBytes();
      this.totalBodyLength = this.totalBodyLength + this.extrasLength - var2;
      return this;
   }

   public byte magic() {
      return this.magic;
   }

   public BinaryMemcacheMessage setMagic(byte var1) {
      this.magic = var1;
      return this;
   }

   public long cas() {
      return this.cas;
   }

   public BinaryMemcacheMessage setCas(long var1) {
      this.cas = var1;
      return this;
   }

   public int opaque() {
      return this.opaque;
   }

   public BinaryMemcacheMessage setOpaque(int var1) {
      this.opaque = var1;
      return this;
   }

   public int totalBodyLength() {
      return this.totalBodyLength;
   }

   public BinaryMemcacheMessage setTotalBodyLength(int var1) {
      this.totalBodyLength = var1;
      return this;
   }

   public byte dataType() {
      return this.dataType;
   }

   public BinaryMemcacheMessage setDataType(byte var1) {
      this.dataType = var1;
      return this;
   }

   public byte extrasLength() {
      return this.extrasLength;
   }

   BinaryMemcacheMessage setExtrasLength(byte var1) {
      this.extrasLength = var1;
      return this;
   }

   public short keyLength() {
      return this.keyLength;
   }

   BinaryMemcacheMessage setKeyLength(short var1) {
      this.keyLength = var1;
      return this;
   }

   public byte opcode() {
      return this.opcode;
   }

   public BinaryMemcacheMessage setOpcode(byte var1) {
      this.opcode = var1;
      return this;
   }

   public BinaryMemcacheMessage retain() {
      super.retain();
      return this;
   }

   public BinaryMemcacheMessage retain(int var1) {
      super.retain(var1);
      return this;
   }

   protected void deallocate() {
      if (this.key != null) {
         this.key.release();
      }

      if (this.extras != null) {
         this.extras.release();
      }

   }

   public BinaryMemcacheMessage touch() {
      super.touch();
      return this;
   }

   public BinaryMemcacheMessage touch(Object var1) {
      if (this.key != null) {
         this.key.touch(var1);
      }

      if (this.extras != null) {
         this.extras.touch(var1);
      }

      return this;
   }
}
