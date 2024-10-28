package net.minecraft.world.level.levelgen;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PhantomSpawner implements CustomSpawner {
   private int nextTick;

   public PhantomSpawner() {
      super();
   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (!var2) {
         return 0;
      } else if (!var1.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
         return 0;
      } else {
         RandomSource var4 = var1.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += (60 + var4.nextInt(60)) * 20;
            if (var1.getSkyDarken() < 5 && var1.dimensionType().hasSkyLight()) {
               return 0;
            } else {
               int var5 = 0;
               Iterator var6 = var1.players().iterator();

               while(true) {
                  DifficultyInstance var9;
                  BlockPos var13;
                  BlockState var14;
                  FluidState var15;
                  do {
                     BlockPos var8;
                     int var11;
                     do {
                        ServerPlayer var7;
                        do {
                           do {
                              do {
                                 if (!var6.hasNext()) {
                                    return var5;
                                 }

                                 var7 = (ServerPlayer)var6.next();
                              } while(var7.isSpectator());

                              var8 = var7.blockPosition();
                           } while(var1.dimensionType().hasSkyLight() && (var8.getY() < var1.getSeaLevel() || !var1.canSeeSky(var8)));

                           var9 = var1.getCurrentDifficultyAt(var8);
                        } while(!var9.isHarderThan(var4.nextFloat() * 3.0F));

                        ServerStatsCounter var10 = var7.getStats();
                        var11 = Mth.clamp(var10.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, 2147483647);
                        boolean var12 = true;
                     } while(var4.nextInt(var11) < 72000);

                     var13 = var8.above(20 + var4.nextInt(15)).east(-10 + var4.nextInt(21)).south(-10 + var4.nextInt(21));
                     var14 = var1.getBlockState(var13);
                     var15 = var1.getFluidState(var13);
                  } while(!NaturalSpawner.isValidEmptySpawnBlock(var1, var13, var14, var15, EntityType.PHANTOM));

                  SpawnGroupData var16 = null;
                  int var17 = 1 + var4.nextInt(var9.getDifficulty().getId() + 1);

                  for(int var18 = 0; var18 < var17; ++var18) {
                     Phantom var19 = (Phantom)EntityType.PHANTOM.create(var1);
                     if (var19 != null) {
                        var19.moveTo(var13, 0.0F, 0.0F);
                        var16 = var19.finalizeSpawn(var1, var9, MobSpawnType.NATURAL, var16);
                        var1.addFreshEntityWithPassengers(var19);
                        ++var5;
                     }
                  }
               }
            }
         }
      }
   }
}
