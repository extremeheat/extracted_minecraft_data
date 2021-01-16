package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import java.util.HashMap;
import java.util.Map;

public final class FixedRedisMessagePool implements RedisMessagePool {
   private static final String[] DEFAULT_SIMPLE_STRINGS = new String[]{"OK", "PONG", "QUEUED"};
   private static final String[] DEFAULT_ERRORS = new String[]{"ERR", "ERR index out of range", "ERR no such key", "ERR source and destination objects are the same", "ERR syntax error", "BUSY Redis is busy running a script. You can only call SCRIPT KILL or SHUTDOWN NOSAVE.", "BUSYKEY Target key name already exists.", "EXECABORT Transaction discarded because of previous errors.", "LOADING Redis is loading the dataset in memory", "MASTERDOWN Link with MASTER is down and slave-serve-stale-data is set to 'no'.", "MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Please check Redis logs for details about the error.", "NOAUTH Authentication required.", "NOREPLICAS Not enough good slaves to write.", "NOSCRIPT No matching script. Please use EVAL.", "OOM command not allowed when used memory > 'maxmemory'.", "READONLY You can't write against a read only slave.", "WRONGTYPE Operation against a key holding the wrong kind of value"};
   private static final long MIN_CACHED_INTEGER_NUMBER = -1L;
   private static final long MAX_CACHED_INTEGER_NUMBER = 128L;
   private static final int SIZE_CACHED_INTEGER_NUMBER = 129;
   public static final FixedRedisMessagePool INSTANCE = new FixedRedisMessagePool();
   private final Map<ByteBuf, SimpleStringRedisMessage> byteBufToSimpleStrings;
   private final Map<String, SimpleStringRedisMessage> stringToSimpleStrings;
   private final Map<ByteBuf, ErrorRedisMessage> byteBufToErrors;
   private final Map<String, ErrorRedisMessage> stringToErrors;
   private final Map<ByteBuf, IntegerRedisMessage> byteBufToIntegers;
   private final LongObjectMap<IntegerRedisMessage> longToIntegers;
   private final LongObjectMap<byte[]> longToByteBufs;

   private FixedRedisMessagePool() {
      super();
      this.byteBufToSimpleStrings = new HashMap(DEFAULT_SIMPLE_STRINGS.length, 1.0F);
      this.stringToSimpleStrings = new HashMap(DEFAULT_SIMPLE_STRINGS.length, 1.0F);
      String[] var1 = DEFAULT_SIMPLE_STRINGS;
      int var2 = var1.length;

      int var3;
      String var4;
      ByteBuf var5;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var5 = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(var4.getBytes(CharsetUtil.UTF_8))));
         SimpleStringRedisMessage var6 = new SimpleStringRedisMessage(var4);
         this.byteBufToSimpleStrings.put(var5, var6);
         this.stringToSimpleStrings.put(var4, var6);
      }

      this.byteBufToErrors = new HashMap(DEFAULT_ERRORS.length, 1.0F);
      this.stringToErrors = new HashMap(DEFAULT_ERRORS.length, 1.0F);
      var1 = DEFAULT_ERRORS;
      var2 = var1.length;

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var5 = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(var4.getBytes(CharsetUtil.UTF_8))));
         ErrorRedisMessage var11 = new ErrorRedisMessage(var4);
         this.byteBufToErrors.put(var5, var11);
         this.stringToErrors.put(var4, var11);
      }

      this.byteBufToIntegers = new HashMap(129, 1.0F);
      this.longToIntegers = new LongObjectHashMap(129, 1.0F);
      this.longToByteBufs = new LongObjectHashMap(129, 1.0F);

      for(long var7 = -1L; var7 < 128L; ++var7) {
         byte[] var8 = RedisCodecUtil.longToAsciiBytes(var7);
         ByteBuf var9 = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(var8)));
         IntegerRedisMessage var10 = new IntegerRedisMessage(var7);
         this.byteBufToIntegers.put(var9, var10);
         this.longToIntegers.put(var7, var10);
         this.longToByteBufs.put(var7, var8);
      }

   }

   public SimpleStringRedisMessage getSimpleString(String var1) {
      return (SimpleStringRedisMessage)this.stringToSimpleStrings.get(var1);
   }

   public SimpleStringRedisMessage getSimpleString(ByteBuf var1) {
      return (SimpleStringRedisMessage)this.byteBufToSimpleStrings.get(var1);
   }

   public ErrorRedisMessage getError(String var1) {
      return (ErrorRedisMessage)this.stringToErrors.get(var1);
   }

   public ErrorRedisMessage getError(ByteBuf var1) {
      return (ErrorRedisMessage)this.byteBufToErrors.get(var1);
   }

   public IntegerRedisMessage getInteger(long var1) {
      return (IntegerRedisMessage)this.longToIntegers.get(var1);
   }

   public IntegerRedisMessage getInteger(ByteBuf var1) {
      return (IntegerRedisMessage)this.byteBufToIntegers.get(var1);
   }

   public byte[] getByteBufOfInteger(long var1) {
      return (byte[])this.longToByteBufs.get(var1);
   }
}
