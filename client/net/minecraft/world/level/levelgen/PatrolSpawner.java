package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class PatrolSpawner {
   private int nextTick;

   public PatrolSpawner() {
      super();
   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (!var2) {
         return 0;
      } else {
         Random var4 = var1.random;
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
                     } else if (var1.isVillage(var8.getCommandSenderBlockPosition())) {
                        return 0;
                     } else {
                        int var9 = (24 + var4.nextInt(24)) * (var4.nextBoolean() ? -1 : 1);
                        int var10 = (24 + var4.nextInt(24)) * (var4.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
                        var11.set(var8.x, var8.y, var8.z).move(var9, 0, var10);
                        if (!var1.hasChunksAt(var11.getX() - 10, var11.getY() - 10, var11.getZ() - 10, var11.getX() + 10, var11.getY() + 10, var11.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome var12 = var1.getBiome(var11);
                           Biome.BiomeCategory var13 = var12.getBiomeCategory();
                           if (var13 == Biome.BiomeCategory.MUSHROOM) {
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

   private boolean spawnPatrolMember(Level var1, BlockPos var2, Random var3, boolean var4) {
      if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, var1, MobSpawnType.PATROL, var2, var3)) {
         return false;
      } else {
         PatrollingMonster var5 = (PatrollingMonster)EntityType.PILLAGER.create(var1);
         if (var5 != null) {
            if (var4) {
               var5.setPatrolLeader(true);
               var5.findPatrolTarget();
            }

            var5.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            var5.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var2), MobSpawnType.PATROL, (SpawnGroupData)null, (CompoundTag)null);
            var1.addFreshEntity(var5);
            return true;
         } else {
            return false;
         }
      }
   }
}
