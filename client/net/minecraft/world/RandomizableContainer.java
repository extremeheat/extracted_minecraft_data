package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public interface RandomizableContainer extends Container {
   String LOOT_TABLE_TAG = "LootTable";
   String LOOT_TABLE_SEED_TAG = "LootTableSeed";

   @Nullable
   ResourceLocation getLootTable();

   void setLootTable(@Nullable ResourceLocation var1);

   default void setLootTable(ResourceLocation var1, long var2) {
      this.setLootTable(var1);
      this.setLootTableSeed(var2);
   }

   long getLootTableSeed();

   void setLootTableSeed(long var1);

   BlockPos getBlockPos();

   @Nullable
   Level getLevel();

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static void setBlockEntityLootTable(BlockGetter var0, RandomSource var1, BlockPos var2, ResourceLocation var3) {
      BlockEntity var4 = var0.getBlockEntity(var2);
      if (var4 instanceof RandomizableContainer var5) {
         var5.setLootTable(var3, var1.nextLong());
      }
   }

   default boolean tryLoadLootTable(CompoundTag var1) {
      if (var1.contains("LootTable", 8)) {
         this.setLootTable(new ResourceLocation(var1.getString("LootTable")));
         if (var1.contains("LootTableSeed", 4)) {
            this.setLootTableSeed(var1.getLong("LootTableSeed"));
         } else {
            this.setLootTableSeed(0L);
         }

         return true;
      } else {
         return false;
      }
   }

   default boolean trySaveLootTable(CompoundTag var1) {
      ResourceLocation var2 = this.getLootTable();
      if (var2 == null) {
         return false;
      } else {
         var1.putString("LootTable", var2.toString());
         long var3 = this.getLootTableSeed();
         if (var3 != 0L) {
            var1.putLong("LootTableSeed", var3);
         }

         return true;
      }
   }

   default void unpackLootTable(@Nullable Player var1) {
      Level var2 = this.getLevel();
      BlockPos var3 = this.getBlockPos();
      ResourceLocation var4 = this.getLootTable();
      if (var4 != null && var2 != null && var2.getServer() != null) {
         LootTable var5 = var2.getServer().getLootData().getLootTable(var4);
         if (var1 instanceof ServerPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)var1, var4);
         }

         this.setLootTable(null);
         LootParams.Builder var6 = new LootParams.Builder((ServerLevel)var2).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var3));
         if (var1 != null) {
            var6.withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1);
         }

         var5.fill(this, var6.create(LootContextParamSets.CHEST), this.getLootTableSeed());
      }
   }
}
