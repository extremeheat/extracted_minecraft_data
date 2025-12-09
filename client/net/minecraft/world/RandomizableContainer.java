package net.minecraft.world;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface RandomizableContainer extends Container {
   String LOOT_TABLE_TAG = "LootTable";
   String LOOT_TABLE_SEED_TAG = "LootTableSeed";

   @Nullable ResourceKey<LootTable> getLootTable();

   void setLootTable(@Nullable ResourceKey<LootTable> var1);

   default void setLootTable(ResourceKey<LootTable> var1, long var2) {
      this.setLootTable(var1);
      this.setLootTableSeed(var2);
   }

   long getLootTableSeed();

   void setLootTableSeed(long var1);

   BlockPos getBlockPos();

   @Nullable Level getLevel();

   static void setBlockEntityLootTable(BlockGetter var0, RandomSource var1, BlockPos var2, ResourceKey<LootTable> var3) {
      BlockEntity var4 = var0.getBlockEntity(var2);
      if (var4 instanceof RandomizableContainer var5) {
         var5.setLootTable(var3, var1.nextLong());
      }

   }

   default boolean tryLoadLootTable(ValueInput var1) {
      ResourceKey var2 = (ResourceKey)var1.read("LootTable", LootTable.KEY_CODEC).orElse((Object)null);
      this.setLootTable(var2);
      this.setLootTableSeed(var1.getLongOr("LootTableSeed", 0L));
      return var2 != null;
   }

   default boolean trySaveLootTable(ValueOutput var1) {
      ResourceKey var2 = this.getLootTable();
      if (var2 == null) {
         return false;
      } else {
         var1.store("LootTable", LootTable.KEY_CODEC, var2);
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
      ResourceKey var4 = this.getLootTable();
      if (var4 != null && var2 != null && var2.getServer() != null) {
         LootTable var5 = var2.getServer().reloadableRegistries().getLootTable(var4);
         if (var1 instanceof ServerPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)var1, var4);
         }

         this.setLootTable((ResourceKey)null);
         LootParams.Builder var6 = (new LootParams.Builder((ServerLevel)var2)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var3));
         if (var1 != null) {
            var6.withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1);
         }

         var5.fill(this, var6.create(LootContextParamSets.CHEST), this.getLootTableSeed());
      }

   }
}
