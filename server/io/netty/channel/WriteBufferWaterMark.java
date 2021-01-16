package io.netty.channel;

public final class WriteBufferWaterMark {
   private static final int DEFAULT_LOW_WATER_MARK = 32768;
   private static final int DEFAULT_HIGH_WATER_MARK = 65536;
   public static final WriteBufferWaterMark DEFAULT = new WriteBufferWaterMark(32768, 65536, false);
   private final int low;
   private final int high;

   public WriteBufferWaterMark(int var1, int var2) {
      this(var1, var2, true);
   }

   WriteBufferWaterMark(int var1, int var2, boolean var3) {
      super();
      if (var3) {
         if (var1 < 0) {
            throw new IllegalArgumentException("write buffer's low water mark must be >= 0");
         }

         if (var2 < var1) {
            throw new IllegalArgumentException("write buffer's high water mark cannot be less than  low water mark (" + var1 + "): " + var2);
         }
      }

      this.low = var1;
      this.high = var2;
   }

   public int low() {
      return this.low;
   }

   public int high() {
      return this.high;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(55)).append("WriteBufferWaterMark(low: ").append(this.low).append(", high: ").append(this.high).append(")");
      return var1.toString();
   }
}
