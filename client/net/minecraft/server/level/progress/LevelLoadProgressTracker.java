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

   public LevelLoadProgressTracker(final boolean includePlayerChunks) {
      super();
      this.includePlayerChunks = includePlayerChunks;
   }

   public void start(final LevelLoadListener.Stage stage, final int totalChunks) {
      if (this.tracksStage(stage)) {
         switch (stage) {
            case LOAD_INITIAL_CHUNKS:
               int playerChunksWeight = this.includePlayerChunks ? EXPECTED_PLAYER_CHUNKS : 0;
               this.totalWeight = 10 + totalChunks + playerChunksWeight;
               this.beginSegment(10);
               this.finishSegment();
               this.beginSegment(totalChunks);
               break;
            case LOAD_PLAYER_CHUNKS:
               this.beginSegment(EXPECTED_PLAYER_CHUNKS);
         }

      }
   }

   private void beginSegment(final int weight) {
      this.segmentWeight = weight;
      this.segmentFraction = 0.0F;
      this.updateProgress();
   }

   public void update(final LevelLoadListener.Stage stage, final int currentChunks, final int totalChunks) {
      if (this.tracksStage(stage)) {
         this.segmentFraction = totalChunks == 0 ? 0.0F : (float)currentChunks / (float)totalChunks;
         this.updateProgress();
      }

   }

   public void finish(final LevelLoadListener.Stage stage) {
      if (this.tracksStage(stage)) {
         this.finishSegment();
      }

   }

   private void finishSegment() {
      this.finalizedWeight += this.segmentWeight;
      this.segmentWeight = 0;
      this.updateProgress();
   }

   private boolean tracksStage(final LevelLoadListener.Stage stage) {
      boolean var10000;
      switch (stage) {
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
         float currentWeight = (float)this.finalizedWeight + this.segmentFraction * (float)this.segmentWeight;
         this.progress = currentWeight / (float)this.totalWeight;
      }

   }

   public float get() {
      return this.progress;
   }

   public void updateFocus(final ResourceKey<Level> dimension, final ChunkPos chunkPos) {
   }
}
