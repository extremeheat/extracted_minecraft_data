package net.minecraft.util.worldupdate;

import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class UpgradeProgress {
   private volatile boolean finished;
   private volatile int totalFiles;
   private volatile float totalProgress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Reference2FloatMap<ResourceKey<Level>> progressMap = Reference2FloatMaps.synchronize(new Reference2FloatOpenHashMap());
   private volatile boolean canceled = false;
   private volatile @Nullable DataFixTypes dataFixType;
   private volatile Status status;

   public UpgradeProgress() {
      super();
      this.status = UpgradeProgress.Status.COUNTING;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public void setFinished(final boolean finished) {
      this.finished = finished;
   }

   public int getTotalFiles() {
      return this.totalFiles;
   }

   public void addTotalFiles(final int additionalTotalFiles) {
      this.totalFiles += additionalTotalFiles;
   }

   public float getTotalProgress() {
      return this.totalProgress;
   }

   public void setTotalProgress(final float totalProgress) {
      this.totalProgress = totalProgress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public void addTotalChunks(final int additionalTotalChunks) {
      this.totalChunks += additionalTotalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public void setDimensionProgress(final ResourceKey<Level> dimensionKey, final float currentProgress) {
      this.progressMap.put(dimensionKey, currentProgress);
   }

   public float getDimensionProgress(final ResourceKey<Level> dimensionKey) {
      return this.progressMap.getFloat(dimensionKey);
   }

   public void incrementConverted() {
      ++this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public void incrementSkipped() {
      ++this.skipped;
   }

   public void setCanceled() {
      this.canceled = true;
   }

   public boolean isCanceled() {
      return this.canceled;
   }

   public Status getStatus() {
      return this.status;
   }

   public void setStatus(final Status status) {
      this.status = status;
   }

   public @Nullable DataFixTypes getDataFixType() {
      return this.dataFixType;
   }

   public void reset(final DataFixTypes dataFixType) {
      this.totalFiles = 0;
      this.totalChunks = 0;
      this.converted = 0;
      this.skipped = 0;
      this.dataFixType = dataFixType;
   }

   public static enum Status {
      COUNTING,
      FAILED,
      FINISHED,
      UPGRADING;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{COUNTING, FAILED, FINISHED, UPGRADING};
      }
   }

   public static class Noop extends UpgradeProgress {
      public Noop() {
         super();
      }

      public void setFinished(final boolean finished) {
      }

      public void addTotalFiles(final int additionalTotalFiles) {
      }

      public void setTotalProgress(final float totalProgress) {
      }

      public void addTotalChunks(final int additionalTotalChunks) {
      }

      public void setDimensionProgress(final ResourceKey<Level> dimensionKey, final float currentProgress) {
      }

      public void incrementConverted() {
      }

      public void incrementSkipped() {
      }

      public void setCanceled() {
      }

      public void setStatus(final Status status) {
      }

      public void reset(final DataFixTypes dataFixType) {
      }
   }
}
