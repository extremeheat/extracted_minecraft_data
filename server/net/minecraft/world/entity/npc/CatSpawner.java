package net.minecraft.world.entity.npc;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.AABB;

public class CatSpawner implements CustomSpawner {
   private int nextTick;

   public CatSpawner() {
      super();
   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (var3 && var1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick = 1200;
            ServerPlayer var4 = var1.getRandomPlayer();
            if (var4 == null) {
               return 0;
            } else {
               Random var5 = var1.random;
               int var6 = (8 + var5.nextInt(24)) * (var5.nextBoolean() ? -1 : 1);
               int var7 = (8 + var5.nextInt(24)) * (var5.nextBoolean() ? -1 : 1);
               BlockPos var8 = var4.blockPosition().offset(var6, 0, var7);
               if (!var1.hasChunksAt(var8.getX() - 10, var8.getY() - 10, var8.getZ() - 10, var8.getX() + 10, var8.getY() + 10, var8.getZ() + 10)) {
                  return 0;
               } else {
                  if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, var1, var8, EntityType.CAT)) {
                     if (var1.isCloseToVillage(var8, 2)) {
                        return this.spawnInVillage(var1, var8);
                     }

                     if (var1.structureFeatureManager().getStructureAt(var8, true, StructureFeature.SWAMP_HUT).isValid()) {
                        return this.spawnInHut(var1, var8);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int spawnInVillage(ServerLevel var1, BlockPos var2) {
      boolean var3 = true;
      if (var1.getPoiManager().getCountInRange(PoiType.HOME.getPredicate(), var2, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L) {
         List var4 = var1.getEntitiesOfClass(Cat.class, (new AABB(var2)).inflate(48.0D, 8.0D, 48.0D));
         if (var4.size() < 5) {
            return this.spawnCat(var2, var1);
         }
      }

      return 0;
   }

   private int spawnInHut(ServerLevel var1, BlockPos var2) {
      boolean var3 = true;
      List var4 = var1.getEntitiesOfClass(Cat.class, (new AABB(var2)).inflate(16.0D, 8.0D, 16.0D));
      return var4.size() < 1 ? this.spawnCat(var2, var1) : 0;
   }

   private int spawnCat(BlockPos var1, ServerLevel var2) {
      Cat var3 = (Cat)EntityType.CAT.create(var2);
      if (var3 == null) {
         return 0;
      } else {
         var3.finalizeSpawn(var2, var2.getCurrentDifficultyAt(var1), MobSpawnType.NATURAL, (SpawnGroupData)null, (CompoundTag)null);
         var3.moveTo(var1, 0.0F, 0.0F);
         var2.addFreshEntityWithPassengers(var3);
         return 1;
      }
   }
}
