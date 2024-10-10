package net.minecraft.server.level;

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

   static void difference(ChunkTrackingView var0, ChunkTrackingView var1, Consumer<ChunkPos> var2, Consumer<ChunkPos> var3) {
      if (!var0.equals(var1)) {
         if (var0 instanceof ChunkTrackingView.Positioned var4 && var1 instanceof ChunkTrackingView.Positioned var5 && var4.squareIntersects(var5)) {
            int var6 = Math.min(var4.minX(), var5.minX());
            int var7 = Math.min(var4.minZ(), var5.minZ());
            int var8 = Math.max(var4.maxX(), var5.maxX());
            int var9 = Math.max(var4.maxZ(), var5.maxZ());

            for (int var10 = var6; var10 <= var8; var10++) {
               for (int var11 = var7; var11 <= var9; var11++) {
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
