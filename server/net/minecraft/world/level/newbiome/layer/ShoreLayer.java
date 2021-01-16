package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum ShoreLayer implements CastleTransformer {
   INSTANCE;

   private static final IntSet SNOWY = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
   private static final IntSet JUNGLES = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

   private ShoreLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      if (var6 == 14) {
         if (Layers.isShallowOcean(var2) || Layers.isShallowOcean(var3) || Layers.isShallowOcean(var4) || Layers.isShallowOcean(var5)) {
            return 15;
         }
      } else if (JUNGLES.contains(var6)) {
         if (!isJungleCompatible(var2) || !isJungleCompatible(var3) || !isJungleCompatible(var4) || !isJungleCompatible(var5)) {
            return 23;
         }

         if (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5)) {
            return 16;
         }
      } else if (var6 != 3 && var6 != 34 && var6 != 20) {
         if (SNOWY.contains(var6)) {
            if (!Layers.isOcean(var6) && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
               return 26;
            }
         } else if (var6 != 37 && var6 != 38) {
            if (!Layers.isOcean(var6) && var6 != 7 && var6 != 6 && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
               return 16;
            }
         } else if (!Layers.isOcean(var2) && !Layers.isOcean(var3) && !Layers.isOcean(var4) && !Layers.isOcean(var5) && (!this.isMesa(var2) || !this.isMesa(var3) || !this.isMesa(var4) || !this.isMesa(var5))) {
            return 2;
         }
      } else if (!Layers.isOcean(var6) && (Layers.isOcean(var2) || Layers.isOcean(var3) || Layers.isOcean(var4) || Layers.isOcean(var5))) {
         return 25;
      }

      return var6;
   }

   private static boolean isJungleCompatible(int var0) {
      return JUNGLES.contains(var0) || var0 == 4 || var0 == 5 || Layers.isOcean(var0);
   }

   private boolean isMesa(int var1) {
      return var1 == 37 || var1 == 38 || var1 == 39 || var1 == 165 || var1 == 166 || var1 == 167;
   }
}
