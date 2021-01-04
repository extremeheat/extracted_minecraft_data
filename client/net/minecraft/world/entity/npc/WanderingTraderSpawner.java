package net.minecraft.world.entity.npc;

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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;

public class WanderingTraderSpawner {
   private final Random random = new Random();
   private final ServerLevel level;
   private int tickDelay;
   private int spawnDelay;
   private int spawnChance;

   public WanderingTraderSpawner(ServerLevel var1) {
      super();
      this.level = var1;
      this.tickDelay = 1200;
      LevelData var2 = var1.getLevelData();
      this.spawnDelay = var2.getWanderingTraderSpawnDelay();
      this.spawnChance = var2.getWanderingTraderSpawnChance();
      if (this.spawnDelay == 0 && this.spawnChance == 0) {
         this.spawnDelay = 24000;
         var2.setWanderingTraderSpawnDelay(this.spawnDelay);
         this.spawnChance = 25;
         var2.setWanderingTraderSpawnChance(this.spawnChance);
      }

   }

   public void tick() {
      if (--this.tickDelay <= 0) {
         this.tickDelay = 1200;
         LevelData var1 = this.level.getLevelData();
         this.spawnDelay -= 1200;
         var1.setWanderingTraderSpawnDelay(this.spawnDelay);
         if (this.spawnDelay <= 0) {
            this.spawnDelay = 24000;
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
               int var2 = this.spawnChance;
               this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
               var1.setWanderingTraderSpawnChance(this.spawnChance);
               if (this.random.nextInt(100) <= var2) {
                  if (this.spawn()) {
                     this.spawnChance = 25;
                  }

               }
            }
         }
      }
   }

   private boolean spawn() {
      ServerPlayer var1 = this.level.getRandomPlayer();
      if (var1 == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos var2 = var1.getCommandSenderBlockPosition();
         boolean var3 = true;
         PoiManager var4 = this.level.getPoiManager();
         Optional var5 = var4.find(PoiType.MEETING.getPredicate(), (var0) -> {
            return true;
         }, var2, 48, PoiManager.Occupancy.ANY);
         BlockPos var6 = (BlockPos)var5.orElse(var2);
         BlockPos var7 = this.findSpawnPositionNear(var6, 48);
         if (var7 != null) {
            if (this.level.getBiome(var7) == Biomes.THE_VOID) {
               return false;
            }

            WanderingTrader var8 = (WanderingTrader)EntityType.WANDERING_TRADER.spawn(this.level, (CompoundTag)null, (Component)null, (Player)null, var7, MobSpawnType.EVENT, false, false);
            if (var8 != null) {
               for(int var9 = 0; var9 < 2; ++var9) {
                  this.tryToSpawnLlamaFor(var8, 4);
               }

               this.level.getLevelData().setWanderingTraderId(var8.getUUID());
               var8.setDespawnDelay(48000);
               var8.setWanderTarget(var6);
               var8.restrictTo(var6, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void tryToSpawnLlamaFor(WanderingTrader var1, int var2) {
      BlockPos var3 = this.findSpawnPositionNear(new BlockPos(var1), var2);
      if (var3 != null) {
         TraderLlama var4 = (TraderLlama)EntityType.TRADER_LLAMA.spawn(this.level, (CompoundTag)null, (Component)null, (Player)null, var3, MobSpawnType.EVENT, false, false);
         if (var4 != null) {
            var4.setLeashedTo(var1, true);
         }
      }
   }

   @Nullable
   private BlockPos findSpawnPositionNear(BlockPos var1, int var2) {
      BlockPos var3 = null;

      for(int var4 = 0; var4 < 10; ++var4) {
         int var5 = var1.getX() + this.random.nextInt(var2 * 2) - var2;
         int var6 = var1.getZ() + this.random.nextInt(var2 * 2) - var2;
         int var7 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, var5, var6);
         BlockPos var8 = new BlockPos(var5, var7, var6);
         if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, var8, EntityType.WANDERING_TRADER)) {
            var3 = var8;
            break;
         }
      }

      return var3;
   }
}
