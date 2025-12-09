package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;

public class PhantomSpawner implements CustomSpawner {
   private int nextTick;

   public PhantomSpawner() {
      super();
   }

   public void tick(ServerLevel var1, boolean var2) {
      if (var2) {
         if ((Boolean)var1.getGameRules().get(GameRules.SPAWN_PHANTOMS)) {
            RandomSource var3 = var1.random;
            --this.nextTick;
            if (this.nextTick <= 0) {
               this.nextTick += (60 + var3.nextInt(60)) * 20;
               if (var1.getSkyDarken() >= 5 || !var1.dimensionType().hasSkyLight()) {
                  for(ServerPlayer var5 : var1.players()) {
                     if (!var5.isSpectator()) {
                        BlockPos var6 = var5.blockPosition();
                        if (!var1.dimensionType().hasSkyLight() || var6.getY() >= var1.getSeaLevel() && var1.canSeeSky(var6)) {
                           DifficultyInstance var7 = var1.getCurrentDifficultyAt(var6);
                           if (var7.isHarderThan(var3.nextFloat() * 3.0F)) {
                              ServerStatsCounter var8 = var5.getStats();
                              int var9 = Mth.clamp(var8.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, 2147483647);
                              boolean var10 = true;
                              if (var3.nextInt(var9) >= 72000) {
                                 BlockPos var11 = var6.above(20 + var3.nextInt(15)).east(-10 + var3.nextInt(21)).south(-10 + var3.nextInt(21));
                                 BlockState var12 = var1.getBlockState(var11);
                                 FluidState var13 = var1.getFluidState(var11);
                                 if (NaturalSpawner.isValidEmptySpawnBlock(var1, var11, var12, var13, EntityType.PHANTOM)) {
                                    SpawnGroupData var14 = null;
                                    int var15 = 1 + var3.nextInt(var7.getDifficulty().getId() + 1);

                                    for(int var16 = 0; var16 < var15; ++var16) {
                                       Phantom var17 = EntityType.PHANTOM.create(var1, EntitySpawnReason.NATURAL);
                                       if (var17 != null) {
                                          var17.snapTo(var11, 0.0F, 0.0F);
                                          var14 = var17.finalizeSpawn(var1, var7, EntitySpawnReason.NATURAL, var14);
                                          var1.addFreshEntityWithPassengers(var17);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

               }
            }
         }
      }
   }
}
