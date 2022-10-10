package net.minecraft.world.end;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.EndCrystalTowerFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.EndSpikes;

public enum DragonSpawnState {
   START {
      public void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5) {
         BlockPos var6 = new BlockPos(0, 128, 0);
         Iterator var7 = var3.iterator();

         while(var7.hasNext()) {
            EntityEnderCrystal var8 = (EntityEnderCrystal)var7.next();
            var8.func_184516_a(var6);
         }

         var2.func_186095_a(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5) {
         if (var4 < 100) {
            if (var4 == 0 || var4 == 50 || var4 == 51 || var4 == 52 || var4 >= 95) {
               var1.func_175718_b(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            var2.func_186095_a(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5) {
         boolean var6 = true;
         boolean var7 = var4 % 40 == 0;
         boolean var8 = var4 % 40 == 39;
         if (var7 || var8) {
            EndCrystalTowerFeature.EndSpike[] var9 = EndSpikes.func_202466_a(var1);
            int var10 = var4 / 40;
            if (var10 < var9.length) {
               EndCrystalTowerFeature.EndSpike var11 = var9[var10];
               if (var7) {
                  Iterator var12 = var3.iterator();

                  while(var12.hasNext()) {
                     EntityEnderCrystal var13 = (EntityEnderCrystal)var12.next();
                     var13.func_184516_a(new BlockPos(var11.func_186151_a(), var11.func_186149_d() + 1, var11.func_186152_b()));
                  }
               } else {
                  boolean var15 = true;
                  Iterator var16 = BlockPos.func_177975_b(new BlockPos(var11.func_186151_a() - 10, var11.func_186149_d() - 10, var11.func_186152_b() - 10), new BlockPos(var11.func_186151_a() + 10, var11.func_186149_d() + 10, var11.func_186152_b() + 10)).iterator();

                  while(var16.hasNext()) {
                     BlockPos.MutableBlockPos var14 = (BlockPos.MutableBlockPos)var16.next();
                     var1.func_175698_g(var14);
                  }

                  var1.func_72876_a((Entity)null, (double)((float)var11.func_186151_a() + 0.5F), (double)var11.func_186149_d(), (double)((float)var11.func_186152_b() + 0.5F), 5.0F, true);
                  EndCrystalTowerFeature var17 = new EndCrystalTowerFeature();
                  var17.func_186143_a(var11);
                  var17.func_186144_a(true);
                  var17.func_186142_a(new BlockPos(0, 128, 0));
                  var17.func_212245_a(var1, var1.func_72863_F().func_201711_g(), new Random(), new BlockPos(var11.func_186151_a(), 45, var11.func_186152_b()), (NoFeatureConfig)IFeatureConfig.field_202429_e);
               }
            } else if (var7) {
               var2.func_186095_a(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5) {
         Iterator var6;
         EntityEnderCrystal var7;
         if (var4 >= 100) {
            var2.func_186095_a(END);
            var2.func_186087_f();
            var6 = var3.iterator();

            while(var6.hasNext()) {
               var7 = (EntityEnderCrystal)var6.next();
               var7.func_184516_a((BlockPos)null);
               var1.func_72876_a(var7, var7.field_70165_t, var7.field_70163_u, var7.field_70161_v, 6.0F, false);
               var7.func_70106_y();
            }
         } else if (var4 >= 80) {
            var1.func_175718_b(3001, new BlockPos(0, 128, 0), 0);
         } else if (var4 == 0) {
            var6 = var3.iterator();

            while(var6.hasNext()) {
               var7 = (EntityEnderCrystal)var6.next();
               var7.func_184516_a(new BlockPos(0, 128, 0));
            }
         } else if (var4 < 5) {
            var1.func_175718_b(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5) {
      }
   };

   private DragonSpawnState() {
   }

   public abstract void func_186079_a(WorldServer var1, DragonFightManager var2, List<EntityEnderCrystal> var3, int var4, BlockPos var5);

   // $FF: synthetic method
   DragonSpawnState(Object var3) {
      this();
   }
}
