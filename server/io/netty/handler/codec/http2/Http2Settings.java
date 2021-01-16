package io.netty.handler.codec.http2;

import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.ObjectUtil;

public final class Http2Settings extends CharObjectHashMap<Long> {
   private static final int DEFAULT_CAPACITY = 13;
   private static final Long FALSE = 0L;
   private static final Long TRUE = 1L;

   public Http2Settings() {
      this(13);
   }

   public Http2Settings(int var1, float var2) {
      super(var1, var2);
   }

   public Http2Settings(int var1) {
      super(var1);
   }

   public Long put(char var1, Long var2) {
      verifyStandardSetting(var1, var2);
      return (Long)super.put(var1, var2);
   }

   public Long headerTableSize() {
      return (Long)this.get('\u0001');
   }

   public Http2Settings headerTableSize(long var1) {
      this.put('\u0001', var1);
      return this;
   }

   public Boolean pushEnabled() {
      Long var1 = (Long)this.get('\u0002');
      return var1 == null ? null : TRUE.equals(var1);
   }

   public Http2Settings pushEnabled(boolean var1) {
      this.put('\u0002', var1 ? TRUE : FALSE);
      return this;
   }

   public Long maxConcurrentStreams() {
      return (Long)this.get('\u0003');
   }

   public Http2Settings maxConcurrentStreams(long var1) {
      this.put('\u0003', var1);
      return this;
   }

   public Integer initialWindowSize() {
      return this.getIntValue('\u0004');
   }

   public Http2Settings initialWindowSize(int var1) {
      this.put('\u0004', (long)var1);
      return this;
   }

   public Integer maxFrameSize() {
      return this.getIntValue('\u0005');
   }

   public Http2Settings maxFrameSize(int var1) {
      this.put('\u0005', (long)var1);
      return this;
   }

   public Long maxHeaderListSize() {
      return (Long)this.get('\u0006');
   }

   public Http2Settings maxHeaderListSize(long var1) {
      this.put('\u0006', var1);
      return this;
   }

   public Http2Settings copyFrom(Http2Settings var1) {
      this.clear();
      this.putAll(var1);
      return this;
   }

   public Integer getIntValue(char var1) {
      Long var2 = (Long)this.get(var1);
      return var2 == null ? null : var2.intValue();
   }

   private static void verifyStandardSetting(int var0, Long var1) {
      ObjectUtil.checkNotNull(var1, "value");
      switch(var0) {
      case 1:
         if (var1 >= 0L && var1 <= 4294967295L) {
            break;
         }

         throw new IllegalArgumentException("Setting HEADER_TABLE_SIZE is invalid: " + var1);
      case 2:
         if (var1 != 0L && var1 != 1L) {
            throw new IllegalArgumentException("Setting ENABLE_PUSH is invalid: " + var1);
         }
         break;
      case 3:
         if (var1 >= 0L && var1 <= 4294967295L) {
            break;
         }

         throw new IllegalArgumentException("Setting MAX_CONCURRENT_STREAMS is invalid: " + var1);
      case 4:
         if (var1 >= 0L && var1 <= 2147483647L) {
            break;
         }

         throw new IllegalArgumentException("Setting INITIAL_WINDOW_SIZE is invalid: " + var1);
      case 5:
         if (!Http2CodecUtil.isMaxFrameSizeValid(var1.intValue())) {
            throw new IllegalArgumentException("Setting MAX_FRAME_SIZE is invalid: " + var1);
         }
         break;
      case 6:
         if (var1 < 0L || var1 > 4294967295L) {
            throw new IllegalArgumentException("Setting MAX_HEADER_LIST_SIZE is invalid: " + var1);
         }
      }

   }

   protected String keyToString(char var1) {
      switch(var1) {
      case '\u0001':
         return "HEADER_TABLE_SIZE";
      case '\u0002':
         return "ENABLE_PUSH";
      case '\u0003':
         return "MAX_CONCURRENT_STREAMS";
      case '\u0004':
         return "INITIAL_WINDOW_SIZE";
      case '\u0005':
         return "MAX_FRAME_SIZE";
      case '\u0006':
         return "MAX_HEADER_LIST_SIZE";
      default:
         return super.keyToString(var1);
      }
   }

   public static Http2Settings defaultSettings() {
      return (new Http2Settings()).maxHeaderListSize(8192L);
   }
}
