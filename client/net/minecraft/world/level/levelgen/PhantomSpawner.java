package net.minecraft.world.level.levelgen;

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
import net.minecraft.world.entity.player.Player;
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

   @Override
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

               for(Player var7 : var1.players()) {
                  if (!var7.isSpectator()) {
                     BlockPos var8 = var7.blockPosition();
                     if (!var1.dimensionType().hasSkyLight() || var8.getY() >= var1.getSeaLevel() && var1.canSeeSky(var8)) {
                        DifficultyInstance var9 = var1.getCurrentDifficultyAt(var8);
                        if (var9.isHarderThan(var4.nextFloat() * 3.0F)) {
                           ServerStatsCounter var10 = ((ServerPlayer)var7).getStats();
                           int var11 = Mth.clamp(var10.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, 2147483647);
                           boolean var12 = true;
                           if (var4.nextInt(var11) >= 72000) {
                              BlockPos var13 = var8.above(20 + var4.nextInt(15)).east(-10 + var4.nextInt(21)).south(-10 + var4.nextInt(21));
                              BlockState var14 = var1.getBlockState(var13);
                              FluidState var15 = var1.getFluidState(var13);
                              if (NaturalSpawner.isValidEmptySpawnBlock(var1, var13, var14, var15, EntityType.PHANTOM)) {
                                 SpawnGroupData var16 = null;
                                 int var17 = 1 + var4.nextInt(var9.getDifficulty().getId() + 1);

                                 for(int var18 = 0; var18 < var17; ++var18) {
                                    Phantom var19 = EntityType.PHANTOM.create(var1);
                                    var19.moveTo(var13, 0.0F, 0.0F);
                                    var16 = var19.finalizeSpawn(var1, var9, MobSpawnType.NATURAL, var16, null);
                                    var1.addFreshEntityWithPassengers(var19);
                                 }

                                 var5 += var17;
                              }
                           }
                        }
                     }
                  }
               }

               return var5;
            }
         }
      }
   }
}
