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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PhantomSpawner implements CustomSpawner {
   private int nextTick;

   public PhantomSpawner() {
      super();
   }

   public void tick(ServerLevel var1, boolean var2, boolean var3) {
      if (var2) {
         if (var1.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
            RandomSource var4 = var1.random;
            --this.nextTick;
            if (this.nextTick <= 0) {
               this.nextTick += (60 + var4.nextInt(60)) * 20;
               if (var1.getSkyDarken() >= 5 || !var1.dimensionType().hasSkyLight()) {
                  for(ServerPlayer var6 : var1.players()) {
                     if (!var6.isSpectator()) {
                        BlockPos var7 = var6.blockPosition();
                        if (!var1.dimensionType().hasSkyLight() || var7.getY() >= var1.getSeaLevel() && var1.canSeeSky(var7)) {
                           DifficultyInstance var8 = var1.getCurrentDifficultyAt(var7);
                           if (var8.isHarderThan(var4.nextFloat() * 3.0F)) {
                              ServerStatsCounter var9 = var6.getStats();
                              int var10 = Mth.clamp(var9.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, 2147483647);
                              boolean var11 = true;
                              if (var4.nextInt(var10) >= 72000) {
                                 BlockPos var12 = var7.above(20 + var4.nextInt(15)).east(-10 + var4.nextInt(21)).south(-10 + var4.nextInt(21));
                                 BlockState var13 = var1.getBlockState(var12);
                                 FluidState var14 = var1.getFluidState(var12);
                                 if (NaturalSpawner.isValidEmptySpawnBlock(var1, var12, var13, var14, EntityType.PHANTOM)) {
                                    SpawnGroupData var15 = null;
                                    int var16 = 1 + var4.nextInt(var8.getDifficulty().getId() + 1);

                                    for(int var17 = 0; var17 < var16; ++var17) {
                                       Phantom var18 = EntityType.PHANTOM.create(var1, EntitySpawnReason.NATURAL);
                                       if (var18 != null) {
                                          var18.snapTo(var12, 0.0F, 0.0F);
                                          var15 = var18.finalizeSpawn(var1, var8, EntitySpawnReason.NATURAL, var15);
                                          var1.addFreshEntityWithPassengers(var18);
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
