package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.minecraft.world.level.ChunkPos;

public interface ChunkTrackingView {
   ChunkTrackingView EMPTY = new ChunkTrackingView() {
      @Override
      public boolean contains(int var1, int var2, boolean var3) {
         return false;
      }

      @Override
      public void forEach(Consumer<ChunkPos> var1) {
      }
   };

   static ChunkTrackingView of(ChunkPos var0, int var1) {
      return new ChunkTrackingView.Positioned(var0, var1);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static void difference(ChunkTrackingView var0, ChunkTrackingView var1, Consumer<ChunkPos> var2, Consumer<ChunkPos> var3) {
      if (!var0.equals(var1)) {
         if (var0 instanceof ChunkTrackingView.Positioned var4
            && var1 instanceof ChunkTrackingView.Positioned var5
            && var4.squareIntersects((ChunkTrackingView.Positioned)var5)) {
            int var6 = Math.min(var4.minX(), ((ChunkTrackingView.Positioned)var5).minX());
            int var7 = Math.min(var4.minZ(), ((ChunkTrackingView.Positioned)var5).minZ());
            int var8 = Math.max(var4.maxX(), ((ChunkTrackingView.Positioned)var5).maxX());
            int var9 = Math.max(var4.maxZ(), ((ChunkTrackingView.Positioned)var5).maxZ());

            for(int var10 = var6; var10 <= var8; ++var10) {
               for(int var11 = var7; var11 <= var9; ++var11) {
                  boolean var12 = var4.contains(var10, var11);
                  boolean var13 = ((ChunkTrackingView.Positioned)var5).contains(var10, var11);
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
      int var6 = Math.max(0, Math.abs(var3 - var0) - 1);
      int var7 = Math.max(0, Math.abs(var4 - var1) - 1);
      long var8 = (long)Math.max(0, Math.max(var6, var7) - (var5 ? 1 : 0));
      long var10 = (long)Math.min(var6, var7);
      long var12 = var10 * var10 + var8 * var8;
      int var14 = var2 * var2;
      return var12 < (long)var14;
   }

   public static record Positioned(ChunkPos b, int c) implements ChunkTrackingView {
      private final ChunkPos center;
      private final int viewDistance;

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
      protected boolean squareIntersects(ChunkTrackingView.Positioned var1) {
         return this.minX() <= var1.maxX() && this.maxX() >= var1.minX() && this.minZ() <= var1.maxZ() && this.maxZ() >= var1.minZ();
      }

      @Override
      public boolean contains(int var1, int var2, boolean var3) {
         return ChunkTrackingView.isWithinDistance(this.center.x, this.center.z, this.viewDistance, var1, var2, var3);
      }

      @Override
      public void forEach(Consumer<ChunkPos> var1) {
         for(int var2 = this.minX(); var2 <= this.maxX(); ++var2) {
            for(int var3 = this.minZ(); var3 <= this.maxZ(); ++var3) {
               if (this.contains(var2, var3)) {
                  var1.accept(new ChunkPos(var2, var3));
               }
            }
         }
      }
   }
}
