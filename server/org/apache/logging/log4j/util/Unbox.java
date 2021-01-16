package org.apache.logging.log4j.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@PerformanceSensitive({"allocation"})
public class Unbox {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int BITS_PER_INT = 32;
   private static final int RINGBUFFER_MIN_SIZE = 32;
   private static final int RINGBUFFER_SIZE = calculateRingBufferSize("log4j.unbox.ringbuffer.size");
   private static final int MASK;
   private static ThreadLocal<Unbox.State> threadLocalState;
   private static Unbox.WebSafeState webSafeState;

   private Unbox() {
      super();
   }

   private static int calculateRingBufferSize(String var0) {
      String var1 = PropertiesUtil.getProperties().getStringProperty(var0, String.valueOf(32));

      try {
         int var2 = Integer.parseInt(var1);
         if (var2 < 32) {
            var2 = 32;
            LOGGER.warn((String)"Invalid {} {}, using minimum size {}.", (Object)var0, var1, 32);
         }

         return ceilingNextPowerOfTwo(var2);
      } catch (Exception var3) {
         LOGGER.warn((String)"Invalid {} {}, using default size {}.", (Object)var0, var1, 32);
         return 32;
      }
   }

   private static int ceilingNextPowerOfTwo(int var0) {
      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(float var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(double var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(short var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(int var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(char var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(long var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(byte var0) {
      return getSB().append(var0);
   }

   @PerformanceSensitive({"allocation"})
   public static StringBuilder box(boolean var0) {
      return getSB().append(var0);
   }

   private static Unbox.State getState() {
      Unbox.State var0 = (Unbox.State)threadLocalState.get();
      if (var0 == null) {
         var0 = new Unbox.State();
         threadLocalState.set(var0);
      }

      return var0;
   }

   private static StringBuilder getSB() {
      return Constants.ENABLE_THREADLOCALS ? getState().getStringBuilder() : webSafeState.getStringBuilder();
   }

   static int getRingbufferSize() {
      return RINGBUFFER_SIZE;
   }

   static {
      MASK = RINGBUFFER_SIZE - 1;
      threadLocalState = new ThreadLocal();
      webSafeState = new Unbox.WebSafeState();
   }

   private static class State {
      private final StringBuilder[] ringBuffer;
      private int current;

      State() {
         super();
         this.ringBuffer = new StringBuilder[Unbox.RINGBUFFER_SIZE];

         for(int var1 = 0; var1 < this.ringBuffer.length; ++var1) {
            this.ringBuffer[var1] = new StringBuilder(21);
         }

      }

      public StringBuilder getStringBuilder() {
         StringBuilder var1 = this.ringBuffer[Unbox.MASK & this.current++];
         var1.setLength(0);
         return var1;
      }

      public boolean isBoxedPrimitive(StringBuilder var1) {
         for(int var2 = 0; var2 < this.ringBuffer.length; ++var2) {
            if (var1 == this.ringBuffer[var2]) {
               return true;
            }
         }

         return false;
      }
   }

   private static class WebSafeState {
      private final ThreadLocal<StringBuilder[]> ringBuffer;
      private final ThreadLocal<int[]> current;

      private WebSafeState() {
         super();
         this.ringBuffer = new ThreadLocal();
         this.current = new ThreadLocal();
      }

      public StringBuilder getStringBuilder() {
         StringBuilder[] var1 = (StringBuilder[])this.ringBuffer.get();
         if (var1 == null) {
            var1 = new StringBuilder[Unbox.RINGBUFFER_SIZE];

            for(int var2 = 0; var2 < var1.length; ++var2) {
               var1[var2] = new StringBuilder(21);
            }

            this.ringBuffer.set(var1);
            this.current.set(new int[1]);
         }

         int[] var4 = (int[])this.current.get();
         int var10001 = Unbox.MASK;
         int var10005 = var4[0];
         int var10002 = var4[0];
         var4[0] = var10005 + 1;
         StringBuilder var3 = var1[var10001 & var10002];
         var3.setLength(0);
         return var3;
      }

      public boolean isBoxedPrimitive(StringBuilder var1) {
         StringBuilder[] var2 = (StringBuilder[])this.ringBuffer.get();
         if (var2 == null) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var1 == var2[var3]) {
                  return true;
               }
            }

            return false;
         }
      }

      // $FF: synthetic method
      WebSafeState(Object var1) {
         this();
      }
   }
}
