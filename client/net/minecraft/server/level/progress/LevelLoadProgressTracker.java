package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class LevelLoadProgressTracker implements LevelLoadListener {
   private static final int PREPARE_SERVER_WEIGHT = 10;
   private static final int EXPECTED_PLAYER_CHUNKS = Mth.square(7);
   private final boolean includePlayerChunks;
   private int totalWeight;
   private int finalizedWeight;
   private int segmentWeight;
   private float segmentFraction;
   private volatile float progress;

   public LevelLoadProgressTracker(boolean var1) {
      super();
      this.includePlayerChunks = var1;
   }

   public void start(LevelLoadListener.Stage var1, int var2) {
      if (this.tracksStage(var1)) {
         switch (var1) {
            case LOAD_INITIAL_CHUNKS:
               int var3 = this.includePlayerChunks ? EXPECTED_PLAYER_CHUNKS : 0;
               this.totalWeight = 10 + var2 + var3;
               this.beginSegment(10);
               this.finishSegment();
               this.beginSegment(var2);
               break;
            case LOAD_PLAYER_CHUNKS:
               this.beginSegment(EXPECTED_PLAYER_CHUNKS);
         }

      }
   }

   private void beginSegment(int var1) {
      this.segmentWeight = var1;
      this.segmentFraction = 0.0F;
      this.updateProgress();
   }

   public void update(LevelLoadListener.Stage var1, int var2, int var3) {
      if (this.tracksStage(var1)) {
         this.segmentFraction = var3 == 0 ? 0.0F : (float)var2 / (float)var3;
         this.updateProgress();
      }

   }

   public void finish(LevelLoadListener.Stage var1) {
      if (this.tracksStage(var1)) {
         this.finishSegment();
      }

   }

   private void finishSegment() {
      this.finalizedWeight += this.segmentWeight;
      this.segmentWeight = 0;
      this.updateProgress();
   }

   private boolean tracksStage(LevelLoadListener.Stage var1) {
      boolean var10000;
      switch (var1) {
         case LOAD_INITIAL_CHUNKS -> var10000 = true;
         case LOAD_PLAYER_CHUNKS -> var10000 = this.includePlayerChunks;
         default -> var10000 = false;
      }

      return var10000;
   }

   private void updateProgress() {
      if (this.totalWeight == 0) {
         this.progress = 0.0F;
      } else {
         float var1 = (float)this.finalizedWeight + this.segmentFraction * (float)this.segmentWeight;
         this.progress = var1 / (float)this.totalWeight;
      }

   }

   public float get() {
      return this.progress;
   }

   public void updateFocus(ResourceKey<Level> var1, ChunkPos var2) {
   }
}
