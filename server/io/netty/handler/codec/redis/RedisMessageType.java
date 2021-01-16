package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;

public enum RedisMessageType {
   INLINE_COMMAND((Byte)null, true),
   SIMPLE_STRING((byte)43, true),
   ERROR((byte)45, true),
   INTEGER((byte)58, true),
   BULK_STRING((byte)36, false),
   ARRAY_HEADER((byte)42, false);

   private final Byte value;
   private final boolean inline;

   private RedisMessageType(Byte var3, boolean var4) {
      this.value = var3;
      this.inline = var4;
   }

   public int length() {
      return this.value != null ? 1 : 0;
   }

   public boolean isInline() {
      return this.inline;
   }

   public static RedisMessageType readFrom(ByteBuf var0, boolean var1) {
      int var2 = var0.readerIndex();
      RedisMessageType var3 = valueOf(var0.readByte());
      if (var3 == INLINE_COMMAND) {
         if (!var1) {
            throw new RedisCodecException("Decoding of inline commands is disabled");
         }

         var0.readerIndex(var2);
      }

      return var3;
   }

   public void writeTo(ByteBuf var1) {
      if (this.value != null) {
         var1.writeByte(this.value);
      }
   }

   private static RedisMessageType valueOf(byte var0) {
      switch(var0) {
      case 36:
         return BULK_STRING;
      case 42:
         return ARRAY_HEADER;
      case 43:
         return SIMPLE_STRING;
      case 45:
         return ERROR;
      case 58:
         return INTEGER;
      default:
         return INLINE_COMMAND;
      }
   }
}
