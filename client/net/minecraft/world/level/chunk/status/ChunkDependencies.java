package net.minecraft.world.level.chunk.status;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Locale;

public final class ChunkDependencies {
   private final ImmutableList<ChunkStatus> dependencyByRadius;
   private final int[] radiusByDependency;

   public ChunkDependencies(ImmutableList<ChunkStatus> var1) {
      super();
      this.dependencyByRadius = var1;
      int var2 = var1.isEmpty() ? 0 : ((ChunkStatus)var1.getFirst()).getIndex() + 1;
      this.radiusByDependency = new int[var2];

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         ChunkStatus var4 = (ChunkStatus)var1.get(var3);
         int var5 = var4.getIndex();

         for(int var6 = 0; var6 <= var5; ++var6) {
            this.radiusByDependency[var6] = var3;
         }
      }

   }

   @VisibleForTesting
   public ImmutableList<ChunkStatus> asList() {
      return this.dependencyByRadius;
   }

   public int size() {
      return this.dependencyByRadius.size();
   }

   public int getRadiusOf(ChunkStatus var1) {
      int var2 = var1.getIndex();
      if (var2 >= this.radiusByDependency.length) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "Requesting a ChunkStatus(%s) outside of dependency range(%s)", var1, this.dependencyByRadius));
      } else {
         return this.radiusByDependency[var2];
      }
   }

   public int getRadius() {
      return Math.max(0, this.dependencyByRadius.size() - 1);
   }

   public ChunkStatus get(int var1) {
      return (ChunkStatus)this.dependencyByRadius.get(var1);
   }

   public String toString() {
      return this.dependencyByRadius.toString();
   }
}
