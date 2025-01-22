package net.minecraft.world.entity.npc;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class CatSpawner implements CustomSpawner {
   private static final int TICK_DELAY = 1200;
   private int nextTick;

   public CatSpawner() {
      super();
   }

   public void tick(ServerLevel var1, boolean var2, boolean var3) {
      if (var3 && var1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         --this.nextTick;
         if (this.nextTick <= 0) {
            this.nextTick = 1200;
            ServerPlayer var4 = var1.getRandomPlayer();
            if (var4 != null) {
               RandomSource var5 = var1.random;
               int var6 = (8 + var5.nextInt(24)) * (var5.nextBoolean() ? -1 : 1);
               int var7 = (8 + var5.nextInt(24)) * (var5.nextBoolean() ? -1 : 1);
               BlockPos var8 = ((Player)var4).blockPosition().offset(var6, 0, var7);
               boolean var9 = true;
               if (var1.hasChunksAt(var8.getX() - 10, var8.getZ() - 10, var8.getX() + 10, var8.getZ() + 10)) {
                  if (SpawnPlacements.isSpawnPositionOk(EntityType.CAT, var1, var8)) {
                     if (var1.isCloseToVillage(var8, 2)) {
                        this.spawnInVillage(var1, var8);
                     } else if (var1.structureManager().getStructureWithPieceAt(var8, StructureTags.CATS_SPAWN_IN).isValid()) {
                        this.spawnInHut(var1, var8);
                     }
                  }

               }
            }
         }
      }
   }

   private void spawnInVillage(ServerLevel var1, BlockPos var2) {
      boolean var3 = true;
      if (var1.getPoiManager().getCountInRange((var0) -> var0.is(PoiTypes.HOME), var2, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L) {
         List var4 = var1.getEntitiesOfClass(Cat.class, (new AABB(var2)).inflate(48.0, 8.0, 48.0));
         if (var4.size() < 5) {
            this.spawnCat(var2, var1, false);
         }
      }

   }

   private void spawnInHut(ServerLevel var1, BlockPos var2) {
      boolean var3 = true;
      List var4 = var1.getEntitiesOfClass(Cat.class, (new AABB(var2)).inflate(16.0, 8.0, 16.0));
      if (var4.isEmpty()) {
         this.spawnCat(var2, var1, true);
      }

   }

   private void spawnCat(BlockPos var1, ServerLevel var2, boolean var3) {
      Cat var4 = EntityType.CAT.create(var2, EntitySpawnReason.NATURAL);
      if (var4 != null) {
         var4.finalizeSpawn(var2, var2.getCurrentDifficultyAt(var1), EntitySpawnReason.NATURAL, (SpawnGroupData)null);
         if (var3) {
            var4.setPersistenceRequired();
         }

         var4.snapTo(var1, 0.0F, 0.0F);
         var2.addFreshEntityWithPassengers(var4);
      }
   }
}
