package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.List;

public class RedisEncoder extends MessageToMessageEncoder<RedisMessage> {
   private final RedisMessagePool messagePool;

   public RedisEncoder() {
      this(FixedRedisMessagePool.INSTANCE);
   }

   public RedisEncoder(RedisMessagePool var1) {
      super();
      this.messagePool = (RedisMessagePool)ObjectUtil.checkNotNull(var1, "messagePool");
   }

   protected void encode(ChannelHandlerContext var1, RedisMessage var2, List<Object> var3) throws Exception {
      try {
         this.writeRedisMessage(var1.alloc(), var2, var3);
      } catch (CodecException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new CodecException(var6);
      }
   }

   private void writeRedisMessage(ByteBufAllocator var1, RedisMessage var2, List<Object> var3) {
      if (var2 instanceof InlineCommandRedisMessage) {
         writeInlineCommandMessage(var1, (InlineCommandRedisMessage)var2, var3);
      } else if (var2 instanceof SimpleStringRedisMessage) {
         writeSimpleStringMessage(var1, (SimpleStringRedisMessage)var2, var3);
      } else if (var2 instanceof ErrorRedisMessage) {
         writeErrorMessage(var1, (ErrorRedisMessage)var2, var3);
      } else if (var2 instanceof IntegerRedisMessage) {
         this.writeIntegerMessage(var1, (IntegerRedisMessage)var2, var3);
      } else if (var2 instanceof FullBulkStringRedisMessage) {
         this.writeFullBulkStringMessage(var1, (FullBulkStringRedisMessage)var2, var3);
      } else if (var2 instanceof BulkStringRedisContent) {
         writeBulkStringContent(var1, (BulkStringRedisContent)var2, var3);
      } else if (var2 instanceof BulkStringHeaderRedisMessage) {
         this.writeBulkStringHeader(var1, (BulkStringHeaderRedisMessage)var2, var3);
      } else if (var2 instanceof ArrayHeaderRedisMessage) {
         this.writeArrayHeader(var1, (ArrayHeaderRedisMessage)var2, var3);
      } else {
         if (!(var2 instanceof ArrayRedisMessage)) {
            throw new CodecException("unknown message type: " + var2);
         }

         this.writeArrayMessage(var1, (ArrayRedisMessage)var2, var3);
      }

   }

   private static void writeInlineCommandMessage(ByteBufAllocator var0, InlineCommandRedisMessage var1, List<Object> var2) {
      writeString(var0, RedisMessageType.INLINE_COMMAND, var1.content(), var2);
   }

   private static void writeSimpleStringMessage(ByteBufAllocator var0, SimpleStringRedisMessage var1, List<Object> var2) {
      writeString(var0, RedisMessageType.SIMPLE_STRING, var1.content(), var2);
   }

   private static void writeErrorMessage(ByteBufAllocator var0, ErrorRedisMessage var1, List<Object> var2) {
      writeString(var0, RedisMessageType.ERROR, var1.content(), var2);
   }

   private static void writeString(ByteBufAllocator var0, RedisMessageType var1, String var2, List<Object> var3) {
      ByteBuf var4 = var0.ioBuffer(var1.length() + ByteBufUtil.utf8MaxBytes(var2) + 2);
      var1.writeTo(var4);
      ByteBufUtil.writeUtf8((ByteBuf)var4, var2);
      var4.writeShort(RedisConstants.EOL_SHORT);
      var3.add(var4);
   }

   private void writeIntegerMessage(ByteBufAllocator var1, IntegerRedisMessage var2, List<Object> var3) {
      ByteBuf var4 = var1.ioBuffer(23);
      RedisMessageType.INTEGER.writeTo(var4);
      var4.writeBytes(this.numberToBytes(var2.value()));
      var4.writeShort(RedisConstants.EOL_SHORT);
      var3.add(var4);
   }

   private void writeBulkStringHeader(ByteBufAllocator var1, BulkStringHeaderRedisMessage var2, List<Object> var3) {
      ByteBuf var4 = var1.ioBuffer(1 + (var2.isNull() ? 2 : 22));
      RedisMessageType.BULK_STRING.writeTo(var4);
      if (var2.isNull()) {
         var4.writeShort(RedisConstants.NULL_SHORT);
      } else {
         var4.writeBytes(this.numberToBytes((long)var2.bulkStringLength()));
         var4.writeShort(RedisConstants.EOL_SHORT);
      }

      var3.add(var4);
   }

   private static void writeBulkStringContent(ByteBufAllocator var0, BulkStringRedisContent var1, List<Object> var2) {
      var2.add(var1.content().retain());
      if (var1 instanceof LastBulkStringRedisContent) {
         var2.add(var0.ioBuffer(2).writeShort(RedisConstants.EOL_SHORT));
      }

   }

   private void writeFullBulkStringMessage(ByteBufAllocator var1, FullBulkStringRedisMessage var2, List<Object> var3) {
      ByteBuf var4;
      if (var2.isNull()) {
         var4 = var1.ioBuffer(5);
         RedisMessageType.BULK_STRING.writeTo(var4);
         var4.writeShort(RedisConstants.NULL_SHORT);
         var4.writeShort(RedisConstants.EOL_SHORT);
         var3.add(var4);
      } else {
         var4 = var1.ioBuffer(23);
         RedisMessageType.BULK_STRING.writeTo(var4);
         var4.writeBytes(this.numberToBytes((long)var2.content().readableBytes()));
         var4.writeShort(RedisConstants.EOL_SHORT);
         var3.add(var4);
         var3.add(var2.content().retain());
         var3.add(var1.ioBuffer(2).writeShort(RedisConstants.EOL_SHORT));
      }

   }

   private void writeArrayHeader(ByteBufAllocator var1, ArrayHeaderRedisMessage var2, List<Object> var3) {
      this.writeArrayHeader(var1, var2.isNull(), var2.length(), var3);
   }

   private void writeArrayMessage(ByteBufAllocator var1, ArrayRedisMessage var2, List<Object> var3) {
      if (var2.isNull()) {
         this.writeArrayHeader(var1, var2.isNull(), -1L, var3);
      } else {
         this.writeArrayHeader(var1, var2.isNull(), (long)var2.children().size(), var3);
         Iterator var4 = var2.children().iterator();

         while(var4.hasNext()) {
            RedisMessage var5 = (RedisMessage)var4.next();
            this.writeRedisMessage(var1, var5, var3);
         }
      }

   }

   private void writeArrayHeader(ByteBufAllocator var1, boolean var2, long var3, List<Object> var5) {
      ByteBuf var6;
      if (var2) {
         var6 = var1.ioBuffer(5);
         RedisMessageType.ARRAY_HEADER.writeTo(var6);
         var6.writeShort(RedisConstants.NULL_SHORT);
         var6.writeShort(RedisConstants.EOL_SHORT);
         var5.add(var6);
      } else {
         var6 = var1.ioBuffer(23);
         RedisMessageType.ARRAY_HEADER.writeTo(var6);
         var6.writeBytes(this.numberToBytes(var3));
         var6.writeShort(RedisConstants.EOL_SHORT);
         var5.add(var6);
      }

   }

   private byte[] numberToBytes(long var1) {
      byte[] var3 = this.messagePool.getByteBufOfInteger(var1);
      return var3 != null ? var3 : RedisCodecUtil.longToAsciiBytes(var1);
   }
}
