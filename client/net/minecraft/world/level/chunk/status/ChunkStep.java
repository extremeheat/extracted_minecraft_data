package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;

public record ChunkStep(
   ChunkStatus targetStatus, ChunkDependencies directDependencies, ChunkDependencies accumulatedDependencies, int blockStateWriteRadius, ChunkStatusTask task
) {

   public ChunkStep(
      ChunkStatus targetStatus,
      ChunkDependencies directDependencies,
      ChunkDependencies accumulatedDependencies,
      int blockStateWriteRadius,
      ChunkStatusTask task
   ) {
      super();
      this.targetStatus = targetStatus;
      this.directDependencies = directDependencies;
      this.accumulatedDependencies = accumulatedDependencies;
      this.blockStateWriteRadius = blockStateWriteRadius;
      this.task = task;
   }

   public int getAccumulatedRadiusOf(ChunkStatus var1) {
      return var1 == this.targetStatus ? 0 : this.accumulatedDependencies.getRadiusOf(var1);
   }

   public CompletableFuture<ChunkAccess> apply(WorldGenContext var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      if (var3.getPersistedStatus().isBefore(this.targetStatus)) {
         ProfiledDuration var4 = JvmProfiler.INSTANCE.onChunkGenerate(var3.getPos(), var1.level().dimension(), this.targetStatus.getName());
         return this.task.doWork(var1, this, var2, var3).thenApply(var2x -> this.completeChunkGeneration(var2x, var4));
      } else {
         return this.task.doWork(var1, this, var2, var3);
      }
   }

   private ChunkAccess completeChunkGeneration(ChunkAccess var1, @Nullable ProfiledDuration var2) {
      if (var1 instanceof ProtoChunk var3 && var3.getPersistedStatus().isBefore(this.targetStatus)) {
         var3.setPersistedStatus(this.targetStatus);
      }

      if (var2 != null) {
         var2.finish();
      }

      return var1;
   }

   public static class Builder {
      private final ChunkStatus status;
      @Nullable
      private final ChunkStep parent;
      private ChunkStatus[] directDependenciesByRadius;
      private int blockStateWriteRadius = -1;
      private ChunkStatusTask task = ChunkStatusTasks::passThrough;

      protected Builder(ChunkStatus var1) {
         super();
         if (var1.getParent() != var1) {
            throw new IllegalArgumentException("Not starting with the first status: " + var1);
         } else {
            this.status = var1;
            this.parent = null;
            this.directDependenciesByRadius = new ChunkStatus[0];
         }
      }

      protected Builder(ChunkStatus var1, ChunkStep var2) {
         super();
         if (var2.targetStatus.getIndex() != var1.getIndex() - 1) {
            throw new IllegalArgumentException("Out of order status: " + var1);
         } else {
            this.status = var1;
            this.parent = var2;
            this.directDependenciesByRadius = new ChunkStatus[]{var2.targetStatus};
         }
      }

      public ChunkStep.Builder addRequirement(ChunkStatus var1, int var2) {
         if (var1.isOrAfter(this.status)) {
            throw new IllegalArgumentException("Status " + var1 + " can not be required by " + this.status);
         } else {
            ChunkStatus[] var3 = this.directDependenciesByRadius;
            int var4 = var2 + 1;
            if (var4 > var3.length) {
               this.directDependenciesByRadius = new ChunkStatus[var4];
               Arrays.fill(this.directDependenciesByRadius, var1);
            }

            for (int var5 = 0; var5 < Math.min(var4, var3.length); var5++) {
               this.directDependenciesByRadius[var5] = ChunkStatus.max(var3[var5], var1);
            }

            return this;
         }
      }

      public ChunkStep.Builder blockStateWriteRadius(int var1) {
         this.blockStateWriteRadius = var1;
         return this;
      }

      public ChunkStep.Builder setTask(ChunkStatusTask var1) {
         this.task = var1;
         return this;
      }

      public ChunkStep build() {
         return new ChunkStep(
            this.status,
            new ChunkDependencies(ImmutableList.copyOf(this.directDependenciesByRadius)),
            new ChunkDependencies(ImmutableList.copyOf(this.buildAccumulatedDependencies())),
            this.blockStateWriteRadius,
            this.task
         );
      }

      private ChunkStatus[] buildAccumulatedDependencies() {
         if (this.parent == null) {
            return this.directDependenciesByRadius;
         } else {
            int var1 = this.getRadiusOfParent(this.parent.targetStatus);
            ChunkDependencies var2 = this.parent.accumulatedDependencies;
            ChunkStatus[] var3 = new ChunkStatus[Math.max(var1 + var2.size(), this.directDependenciesByRadius.length)];

            for (int var4 = 0; var4 < var3.length; var4++) {
               int var5 = var4 - var1;
               if (var5 < 0 || var5 >= var2.size()) {
                  var3[var4] = this.directDependenciesByRadius[var4];
               } else if (var4 >= this.directDependenciesByRadius.length) {
                  var3[var4] = var2.get(var5);
               } else {
                  var3[var4] = ChunkStatus.max(this.directDependenciesByRadius[var4], var2.get(var5));
               }
            }

            return var3;
         }
      }

      private int getRadiusOfParent(ChunkStatus var1) {
         for (int var2 = this.directDependenciesByRadius.length - 1; var2 >= 0; var2--) {
            if (this.directDependenciesByRadius[var2].isOrAfter(var1)) {
               return var2;
            }
         }

         return 0;
      }
   }
}
