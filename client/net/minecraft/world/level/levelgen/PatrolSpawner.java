package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
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
               if (var1.isBrightOutside()) {
                  if (var3.nextInt(5) == 0) {
                     int var4 = var1.players().size();
                     if (var4 >= 1) {
                        Player var5 = (Player)var1.players().get(var3.nextInt(var4));
                        if (!var5.isSpectator()) {
                           if (!var1.isCloseToVillage(var5.blockPosition(), 2)) {
                              int var6 = (24 + var3.nextInt(24)) * (var3.nextBoolean() ? -1 : 1);
                              int var7 = (24 + var3.nextInt(24)) * (var3.nextBoolean() ? -1 : 1);
                              BlockPos.MutableBlockPos var8 = var5.blockPosition().mutable().move(var6, 0, var7);
                              boolean var9 = true;
                              if (var1.hasChunksAt(var8.getX() - 10, var8.getZ() - 10, var8.getX() + 10, var8.getZ() + 10)) {
                                 if ((Boolean)var1.environmentAttributes().getValue(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, var8)) {
                                    int var10 = (int)Math.ceil((double)var1.getCurrentDifficultyAt(var8).getEffectiveDifficulty()) + 1;

                                    for(int var11 = 0; var11 < var10; ++var11) {
                                       var8.setY(var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var8).getY());
                                       if (var11 == 0) {
                                          if (!this.spawnPatrolMember(var1, var8, var3, true)) {
                                             break;
                                          }
                                       } else {
                                          this.spawnPatrolMember(var1, var8, var3, false);
                                       }

                                       var8.setX(var8.getX() + var3.nextInt(5) - var3.nextInt(5));
                                       var8.setZ(var8.getZ() + var3.nextInt(5) - var3.nextInt(5));
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
