package net.minecraft.client.multiplayer;

import net.minecraft.Util;
import net.minecraft.util.Mth;

public class ChunkBatchSizeCalculator {
   private static final int MAX_OLD_SAMPLES_WEIGHT = 49;
   private static final int CLAMP_COEFFICIENT = 3;
   private double aggregatedNanosPerChunk = 2000000.0;
   private int oldSamplesWeight = 1;
   private volatile long chunkBatchStartTime = Util.getNanos();

   public ChunkBatchSizeCalculator() {
      super();
   }

   public void onBatchStart() {
      this.chunkBatchStartTime = Util.getNanos();
   }

   public void onBatchFinished(int var1) {
      if (var1 > 0) {
         double var2 = (double)(Util.getNanos() - this.chunkBatchStartTime);
         double var4 = var2 / (double)var1;
         double var6 = Mth.clamp(var4, this.aggregatedNanosPerChunk / 3.0, this.aggregatedNanosPerChunk * 3.0);
         this.aggregatedNanosPerChunk = (this.aggregatedNanosPerChunk * (double)this.oldSamplesWeight + var6) / (double)(this.oldSamplesWeight + 1);
         this.oldSamplesWeight = Math.min(49, this.oldSamplesWeight + 1);
      }
   }

   public float getDesiredChunksPerTick() {
      return (float)(7000000.0 / this.aggregatedNanosPerChunk);
   }
}
