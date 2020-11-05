package net.minecraft.client;

public class Timer {
   public float partialTick;
   public float tickDelta;
   private long lastMs;
   private final float msPerTick;

   public Timer(float var1, long var2) {
      super();
      this.msPerTick = 1000.0F / var1;
      this.lastMs = var2;
   }

   public int advanceTime(long var1) {
      this.tickDelta = (float)(var1 - this.lastMs) / this.msPerTick;
      this.lastMs = var1;
      this.partialTick += this.tickDelta;
      int var3 = (int)this.partialTick;
      this.partialTick -= (float)var3;
      return var3;
   }
}
