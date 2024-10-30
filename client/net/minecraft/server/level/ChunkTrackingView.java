package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.minecraft.world.level.ChunkPos;

public interface ChunkTrackingView {
   ChunkTrackingView EMPTY = new ChunkTrackingView() {
      public boolean contains(int var1, int var2, boolean var3) {
         return false;
      }

      public void forEach(Consumer<ChunkPos> var1) {
      }
   };

   static ChunkTrackingView of(ChunkPos var0, int var1) {
      return new Positioned(var0, var1);
   }

   static void difference(ChunkTrackingView var0, ChunkTrackingView var1, Consumer<ChunkPos> var2, Consumer<ChunkPos> var3) {
      if (!var0.equals(var1)) {
         if (var0 instanceof Positioned) {
            Positioned var4 = (Positioned)var0;
            if (var1 instanceof Positioned) {
               Positioned var5 = (Positioned)var1;
               if (var4.squareIntersects(var5)) {
                  int var6 = Math.min(var4.minX(), var5.minX());
                  int var7 = Math.min(var4.minZ(), var5.minZ());
                  int var8 = Math.max(var4.maxX(), var5.maxX());
                  int var9 = Math.max(var4.maxZ(), var5.maxZ());

                  for(int var10 = var6; var10 <= var8; ++var10) {
                     for(int var11 = var7; var11 <= var9; ++var11) {
                        boolean var12 = var4.contains(var10, var11);
                        boolean var13 = var5.contains(var10, var11);
                        if (var12 != var13) {
                           if (var13) {
                              var2.accept(new ChunkPos(var10, var11));
                           } else {
                              var3.accept(new ChunkPos(var10, var11));
                           }
                        }
                     }
                  }

                  return;
               }
            }
         }

         var0.forEach(var3);
         var1.forEach(var2);
      }
   }

   default boolean contains(ChunkPos var1) {
      return this.contains(var1.x, var1.z);
   }

   default boolean contains(int var1, int var2) {
      return this.contains(var1, var2, true);
   }

   boolean contains(int var1, int var2, boolean var3);

   void forEach(Consumer<ChunkPos> var1);

   default boolean isInViewDistance(int var1, int var2) {
      return this.contains(var1, var2, false);
   }

   static boolean isInViewDistance(int var0, int var1, int var2, int var3, int var4) {
      return isWithinDistance(var0, var1, var2, var3, var4, false);
   }

   static boolean isWithinDistance(int var0, int var1, int var2, int var3, int var4, boolean var5) {
      int var6 = var5 ? 2 : 1;
      long var7 = (long)Math.max(0, Math.abs(var3 - var0) - var6);
      long var9 = (long)Math.max(0, Math.abs(var4 - var1) - var6);
      long var11 = var7 * var7 + var9 * var9;
      int var13 = var2 * var2;
      return var11 < (long)var13;
   }

   public static record Positioned(ChunkPos center, int viewDistance) implements ChunkTrackingView {
      public Positioned(ChunkPos var1, int var2) {
         super();
         this.center = var1;
         this.viewDistance = var2;
      }

      int minX() {
         return this.center.x - this.viewDistance - 1;
      }

      int minZ() {
         return this.center.z - this.viewDistance - 1;
      }

      int maxX() {
         return this.center.x + this.viewDistance + 1;
      }

      int maxZ() {
         return this.center.z + this.viewDistance + 1;
      }

      @VisibleForTesting
      protected boolean squareIntersects(Positioned var1) {
         return this.minX() <= var1.maxX() && this.maxX() >= var1.minX() && this.minZ() <= var1.maxZ() && this.maxZ() >= var1.minZ();
      }

      public boolean contains(int var1, int var2, boolean var3) {
         return ChunkTrackingView.isWithinDistance(this.center.x, this.center.z, this.viewDistance, var1, var2, var3);
      }

      public void forEach(Consumer<ChunkPos> var1) {
         for(int var2 = this.minX(); var2 <= this.maxX(); ++var2) {
            for(int var3 = this.minZ(); var3 <= this.maxZ(); ++var3) {
               if (this.contains(var2, var3)) {
                  var1.accept(new ChunkPos(var2, var3));
               }
            }
         }

      }

      public ChunkPos center() {
         return this.center;
      }

      public int viewDistance() {
         return this.viewDistance;
      }
   }
}
