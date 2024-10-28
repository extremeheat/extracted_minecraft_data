package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;

public class PatrolSpawner implements CustomSpawner {
   private int nextTick;

   public PatrolSpawner() {
      super();
   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (!var2) {
         return 0;
      } else if (!var1.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
         return 0;
      } else {
         RandomSource var4 = var1.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += 12000 + var4.nextInt(1200);
            long var5 = var1.getDayTime() / 24000L;
            if (var5 >= 5L && var1.isDay()) {
               if (var4.nextInt(5) != 0) {
                  return 0;
               } else {
                  int var7 = var1.players().size();
                  if (var7 < 1) {
                     return 0;
                  } else {
                     Player var8 = (Player)var1.players().get(var4.nextInt(var7));
                     if (var8.isSpectator()) {
                        return 0;
                     } else if (var1.isCloseToVillage(var8.blockPosition(), 2)) {
                        return 0;
                     } else {
                        int var9 = (24 + var4.nextInt(24)) * (var4.nextBoolean() ? -1 : 1);
                        int var10 = (24 + var4.nextInt(24)) * (var4.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos var11 = var8.blockPosition().mutable().move(var9, 0, var10);
                        boolean var12 = true;
                        if (!var1.hasChunksAt(var11.getX() - 10, var11.getZ() - 10, var11.getX() + 10, var11.getZ() + 10)) {
                           return 0;
                        } else {
                           Holder var13 = var1.getBiome(var11);
                           if (var13.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                              return 0;
                           } else {
                              int var14 = 0;
                              int var15 = (int)Math.ceil((double)var1.getCurrentDifficultyAt(var11).getEffectiveDifficulty()) + 1;

                              for(int var16 = 0; var16 < var15; ++var16) {
                                 ++var14;
                                 var11.setY(var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var11).getY());
                                 if (var16 == 0) {
                                    if (!this.spawnPatrolMember(var1, var11, var4, true)) {
                                       break;
                                    }
                                 } else {
                                    this.spawnPatrolMember(var1, var11, var4, false);
                                 }

                                 var11.setX(var11.getX() + var4.nextInt(5) - var4.nextInt(5));
                                 var11.setZ(var11.getZ() + var4.nextInt(5) - var4.nextInt(5));
                              }

                              return var14;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean spawnPatrolMember(ServerLevel var1, BlockPos var2, RandomSource var3, boolean var4) {
      BlockState var5 = var1.getBlockState(var2);
      if (!NaturalSpawner.isValidEmptySpawnBlock(var1, var2, var5, var5.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, var1, MobSpawnType.PATROL, var2, var3)) {
         return false;
      } else {
         PatrollingMonster var6 = (PatrollingMonster)EntityType.PILLAGER.create(var1);
         if (var6 != null) {
            if (var4) {
               var6.setPatrolLeader(true);
               var6.findPatrolTarget();
            }

            var6.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            var6.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var2), MobSpawnType.PATROL, (SpawnGroupData)null);
            var1.addFreshEntityWithPassengers(var6);
            return true;
         } else {
            return false;
         }
      }
   }
}
