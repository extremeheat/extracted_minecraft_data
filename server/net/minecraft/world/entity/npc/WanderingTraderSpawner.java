package net.minecraft.world.entity.npc;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;

public class WanderingTraderSpawner implements CustomSpawner {
   private final Random random = new Random();
   private final ServerLevelData serverLevelData;
   private int tickDelay;
   private int spawnDelay;
   private int spawnChance;

   public WanderingTraderSpawner(ServerLevelData var1) {
      super();
      this.serverLevelData = var1;
      this.tickDelay = 1200;
      this.spawnDelay = var1.getWanderingTraderSpawnDelay();
      this.spawnChance = var1.getWanderingTraderSpawnChance();
      if (this.spawnDelay == 0 && this.spawnChance == 0) {
         this.spawnDelay = 24000;
         var1.setWanderingTraderSpawnDelay(this.spawnDelay);
         this.spawnChance = 25;
         var1.setWanderingTraderSpawnChance(this.spawnChance);
      }

   }

   public int tick(ServerLevel var1, boolean var2, boolean var3) {
      if (!var1.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
         return 0;
      } else if (--this.tickDelay > 0) {
         return 0;
      } else {
         this.tickDelay = 1200;
         this.spawnDelay -= 1200;
         this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
         if (this.spawnDelay > 0) {
            return 0;
         } else {
            this.spawnDelay = 24000;
            if (!var1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
               return 0;
            } else {
               int var4 = this.spawnChance;
               this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
               this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
               if (this.random.nextInt(100) > var4) {
                  return 0;
               } else if (this.spawn(var1)) {
                  this.spawnChance = 25;
                  return 1;
               } else {
                  return 0;
               }
            }
         }
      }
   }

   private boolean spawn(ServerLevel var1) {
      ServerPlayer var2 = var1.getRandomPlayer();
      if (var2 == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos var3 = var2.blockPosition();
         boolean var4 = true;
         PoiManager var5 = var1.getPoiManager();
         Optional var6 = var5.find(PoiType.MEETING.getPredicate(), (var0) -> {
            return true;
         }, var3, 48, PoiManager.Occupancy.ANY);
         BlockPos var7 = (BlockPos)var6.orElse(var3);
         BlockPos var8 = this.findSpawnPositionNear(var1, var7, 48);
         if (var8 != null && this.hasEnoughSpace(var1, var8)) {
            if (var1.getBiomeName(var8).equals(Optional.of(Biomes.THE_VOID))) {
               return false;
            }

            WanderingTrader var9 = (WanderingTrader)EntityType.WANDERING_TRADER.spawn(var1, (CompoundTag)null, (Component)null, (Player)null, var8, MobSpawnType.EVENT, false, false);
            if (var9 != null) {
               for(int var10 = 0; var10 < 2; ++var10) {
                  this.tryToSpawnLlamaFor(var1, var9, 4);
               }

               this.serverLevelData.setWanderingTraderId(var9.getUUID());
               var9.setDespawnDelay(48000);
               var9.setWanderTarget(var7);
               var9.restrictTo(var7, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void tryToSpawnLlamaFor(ServerLevel var1, WanderingTrader var2, int var3) {
      BlockPos var4 = this.findSpawnPositionNear(var1, var2.blockPosition(), var3);
      if (var4 != null) {
         TraderLlama var5 = (TraderLlama)EntityType.TRADER_LLAMA.spawn(var1, (CompoundTag)null, (Component)null, (Player)null, var4, MobSpawnType.EVENT, false, false);
         if (var5 != null) {
            var5.setLeashedTo(var2, true);
         }
      }
   }

   @Nullable
   private BlockPos findSpawnPositionNear(LevelReader var1, BlockPos var2, int var3) {
      BlockPos var4 = null;

      for(int var5 = 0; var5 < 10; ++var5) {
         int var6 = var2.getX() + this.random.nextInt(var3 * 2) - var3;
         int var7 = var2.getZ() + this.random.nextInt(var3 * 2) - var3;
         int var8 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var6, var7);
         BlockPos var9 = new BlockPos(var6, var8, var7);
         if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, var1, var9, EntityType.WANDERING_TRADER)) {
            var4 = var9;
            break;
         }
      }

      return var4;
   }

   private boolean hasEnoughSpace(BlockGetter var1, BlockPos var2) {
      Iterator var3 = BlockPos.betweenClosed(var2, var2.offset(1, 2, 1)).iterator();

      BlockPos var4;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         var4 = (BlockPos)var3.next();
      } while(var1.getBlockState(var4).getCollisionShape(var1, var4).isEmpty());

      return false;
   }
}
