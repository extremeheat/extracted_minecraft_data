package net.minecraft.util.worldupdate;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class UpgradeProgress {
   private static final Logger LOGGER = LogUtils.getLogger();
   private volatile boolean finished;
   private final FileFixStats totalFileFixStats = new FileFixStats();
   private final FileFixStats typeFileFixStats = new FileFixStats();
   private final FileFixStats runningFileFixerStats = new FileFixStats();
   private volatile float totalProgress;
   private final AtomicInteger totalChunks = new AtomicInteger();
   private final AtomicInteger converted = new AtomicInteger();
   private final AtomicInteger skipped = new AtomicInteger();
   private final Reference2FloatMap<ResourceKey<Level>> progressMap = Reference2FloatMaps.synchronize(new Reference2FloatOpenHashMap());
   private volatile boolean canceled = false;
   private volatile @Nullable DataFixTypes dataFixType;
   private volatile Status status;
   private volatile @Nullable Type type;
   private AtomicLong lastLoggedProgressTime;

   public UpgradeProgress() {
      super();
      this.status = UpgradeProgress.Status.COUNTING;
      this.lastLoggedProgressTime = new AtomicLong();
   }

   public boolean isFinished() {
      return this.finished;
   }

   public void setFinished(final boolean finished) {
      this.finished = finished;
   }

   public FileFixStats getTotalFileFixStats() {
      return this.totalFileFixStats;
   }

   public FileFixStats getTypeFileFixStats() {
      return this.typeFileFixStats;
   }

   public FileFixStats getRunningFileFixerStats() {
      return this.runningFileFixerStats;
   }

   public void addTotalFileFixOperations(final int additionalFileFixOperations) {
      this.totalFileFixStats.totalOperations.addAndGet(additionalFileFixOperations);
      this.typeFileFixStats.totalOperations.addAndGet(additionalFileFixOperations);
   }

   public float getTotalProgress() {
      return this.totalProgress;
   }

   public void setTotalProgress(final float totalProgress) {
      this.totalProgress = totalProgress;
   }

   public int getTotalChunks() {
      return this.totalChunks.get();
   }

   public void addTotalChunks(final int additionalTotalChunks) {
      this.totalChunks.addAndGet(additionalTotalChunks);
   }

   public int getConverted() {
      return this.converted.get();
   }

   public void setDimensionProgress(final ResourceKey<Level> dimensionKey, final float currentProgress) {
      this.progressMap.put(dimensionKey, currentProgress);
   }

   public float getDimensionProgress(final ResourceKey<Level> dimensionKey) {
      return this.progressMap.getFloat(dimensionKey);
   }

   public void incrementConverted() {
      this.converted.incrementAndGet();
   }

   public int getSkipped() {
      return this.skipped.get();
   }

   public void incrementSkipped() {
      this.skipped.incrementAndGet();
   }

   public void incrementFinishedOperations() {
      this.incrementFinishedOperationsBy(1);
   }

   public void incrementFinishedOperationsBy(final int count) {
      this.totalFileFixStats.finishedOperations.addAndGet(count);
      this.typeFileFixStats.finishedOperations.addAndGet(count);
      this.logProgress();
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

   public void setType(final Type type) {
      this.type = type;
      this.typeFileFixStats.reset();
   }

   public @Nullable Type getType() {
      return this.type;
   }

   public void setApplicableFixerAmount(final int amount) {
      this.runningFileFixerStats.totalOperations.set(amount);
   }

   public void incrementRunningFileFixer() {
      this.runningFileFixerStats.finishedOperations.incrementAndGet();
   }

   public void reset(final DataFixTypes dataFixType) {
      this.totalFileFixStats.reset();
      this.typeFileFixStats.reset();
      this.totalChunks.set(0);
      this.converted.set(0);
      this.skipped.set(0);
      this.dataFixType = dataFixType;
   }

   public void logProgress() {
      long now = Util.getMillis();
      if (now >= this.lastLoggedProgressTime.get() + 1000L) {
         float progress = (float)this.totalFileFixStats.finishedOperations() / (float)this.totalFileFixStats.totalOperations();
         this.lastLoggedProgressTime.set(now);
         LOGGER.info("Upgrading progress: {}%", (int)(progress * 100.0F));
      }

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

   public static enum Type implements StringRepresentable {
      FILES("files"),
      LEGACY_STRUCTURES("legacy_structures"),
      REGIONS("regions");

      private final String id;
      private final Component label;

      private Type(final String id) {
         this.id = id;
         this.label = Component.translatable("upgradeWorld.progress.type." + this.getSerializedName());
      }

      public String getSerializedName() {
         return this.id;
      }

      public Component label() {
         return this.label;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{FILES, LEGACY_STRUCTURES, REGIONS};
      }
   }

   public static class FileFixStats {
      private final AtomicInteger finishedOperations = new AtomicInteger();
      private final AtomicInteger totalOperations = new AtomicInteger();

      public FileFixStats() {
         super();
      }

      public int finishedOperations() {
         return this.finishedOperations.get();
      }

      public int totalOperations() {
         return this.totalOperations.get();
      }

      public void reset() {
         this.finishedOperations.set(0);
         this.totalOperations.set(0);
      }

      public float getProgress() {
         return this.totalOperations() == 0 ? 0.0F : Mth.clamp((float)this.finishedOperations() / (float)this.totalOperations(), 0.0F, 1.0F);
      }
   }

   public static class Noop extends UpgradeProgress {
      public Noop() {
         super();
      }

      public void setFinished(final boolean finished) {
      }

      public void addTotalFileFixOperations(final int additionalFileFixOperations) {
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

      public void incrementFinishedOperations() {
      }

      public void incrementFinishedOperationsBy(final int count) {
      }

      public void setCanceled() {
      }

      public void setStatus(final Status status) {
      }

      public void setApplicableFixerAmount(final int amount) {
      }

      public void incrementRunningFileFixer() {
      }

      public void reset(final DataFixTypes dataFixType) {
      }
   }
}
