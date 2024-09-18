package net.minecraft.util;

public class TickThrottler {
   private final int incrementStep;
   private final int threshold;
   private int count;

   public TickThrottler(int var1, int var2) {
      super();
      this.incrementStep = var1;
      this.threshold = var2;
   }

   public void increment() {
      this.count = this.count + this.incrementStep;
   }

   public void tick() {
      if (this.count > 0) {
         this.count--;
      }
   }

   public boolean isUnderThreshold() {
      return this.count < this.threshold;
   }
}
