package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;

public class PatrolSpawner implements CustomSpawner {
   private int nextTick;

   public PatrolSpawner() {
      super();
   }

   public void tick(ServerLevel var1, boolean var2) {
      if (var2) {
         if ((Boolean)var1.getGameRules().get(GameRules.SPAWN_PATROLS)) {
            RandomSource var3 = var1.random;
            --this.nextTick;
            if (this.nextTick <= 0) {
               this.nextTick += 12000 + var3.nextInt(1200);
               long var4 = var1.getDayCount();
               if (var4 >= 5L && var1.isBrightOutside()) {
                  if (var3.nextInt(5) == 0) {
                     int var6 = var1.players().size();
                     if (var6 >= 1) {
                        Player var7 = (Player)var1.players().get(var3.nextInt(var6));
                        if (!var7.isSpectator()) {
                           if (!var1.isCloseToVillage(var7.blockPosition(), 2)) {
                              int var8 = (24 + var3.nextInt(24)) * (var3.nextBoolean() ? -1 : 1);
                              int var9 = (24 + var3.nextInt(24)) * (var3.nextBoolean() ? -1 : 1);
                              BlockPos.MutableBlockPos var10 = var7.blockPosition().mutable().move(var8, 0, var9);
                              boolean var11 = true;
                              if (var1.hasChunksAt(var10.getX() - 10, var10.getZ() - 10, var10.getX() + 10, var10.getZ() + 10)) {
                                 Holder var12 = var1.getBiome(var10);
                                 if (!var12.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    int var13 = (int)Math.ceil((double)var1.getCurrentDifficultyAt(var10).getEffectiveDifficulty()) + 1;

                                    for(int var14 = 0; var14 < var13; ++var14) {
                                       var10.setY(var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var10).getY());
                                       if (var14 == 0) {
                                          if (!this.spawnPatrolMember(var1, var10, var3, true)) {
                                             break;
                                          }
                                       } else {
                                          this.spawnPatrolMember(var1, var10, var3, false);
                                       }

                                       var10.setX(var10.getX() + var3.nextInt(5) - var3.nextInt(5));
                                       var10.setZ(var10.getZ() + var3.nextInt(5) - var3.nextInt(5));
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

   private boolean spawnPatrolMember(ServerLevel var1, BlockPos var2, RandomSource var3, boolean var4) {
      BlockState var5 = var1.getBlockState(var2);
      if (!NaturalSpawner.isValidEmptySpawnBlock(var1, var2, var5, var5.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, var1, EntitySpawnReason.PATROL, var2, var3)) {
         return false;
      } else {
         PatrollingMonster var6 = EntityType.PILLAGER.create(var1, EntitySpawnReason.PATROL);
         if (var6 != null) {
            if (var4) {
               var6.setPatrolLeader(true);
               var6.findPatrolTarget();
            }

            var6.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            var6.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var2), EntitySpawnReason.PATROL, (SpawnGroupData)null);
            var1.addFreshEntityWithPassengers(var6);
            return true;
         } else {
            return false;
         }
      }
   }
}
