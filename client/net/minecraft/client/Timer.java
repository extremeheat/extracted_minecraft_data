package net.minecraft.client;

public class Timer {
   public int ticks;
   public float partialTick;
   public float tickDelta;
   private long lastMs;
   private final float msPerTick;

   public Timer(float var1, long var2) {
      super();
      this.msPerTick = 1000.0F / var1;
      this.lastMs = var2;
   }

   public void advanceTime(long var1) {
      this.tickDelta = (float)(var1 - this.lastMs) / this.msPerTick;
      this.lastMs = var1;
      this.partialTick += this.tickDelta;
      this.ticks = (int)this.partialTick;
      this.partialTick -= (float)this.ticks;
   }
}
