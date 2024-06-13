package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface ContainerEntity extends Container, MenuProvider {
   Vec3 position();

   AABB getBoundingBox();

   @Nullable
   ResourceKey<LootTable> getLootTable();

   void setLootTable(@Nullable ResourceKey<LootTable> var1);

   long getLootTableSeed();

   void setLootTableSeed(long var1);

   NonNullList<ItemStack> getItemStacks();

   void clearItemStacks();

   Level level();

   boolean isRemoved();

   @Override
   default boolean isEmpty() {
      return this.isChestVehicleEmpty();
   }

   default void addChestVehicleSaveData(CompoundTag var1, HolderLookup.Provider var2) {
      if (this.getLootTable() != null) {
         var1.putString("LootTable", this.getLootTable().location().toString());
         if (this.getLootTableSeed() != 0L) {
            var1.putLong("LootTableSeed", this.getLootTableSeed());
         }
      } else {
         ContainerHelper.saveAllItems(var1, this.getItemStacks(), var2);
      }
   }

   default void readChestVehicleSaveData(CompoundTag var1, HolderLookup.Provider var2) {
      this.clearItemStacks();
      if (var1.contains("LootTable", 8)) {
         this.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(var1.getString("LootTable"))));
         this.setLootTableSeed(var1.getLong("LootTableSeed"));
      } else {
         ContainerHelper.loadAllItems(var1, this.getItemStacks(), var2);
      }
   }

   default void chestVehicleDestroyed(DamageSource var1, Level var2, Entity var3) {
      if (var2.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         Containers.dropContents(var2, var3, this);
         if (!var2.isClientSide) {
            Entity var4 = var1.getDirectEntity();
            if (var4 != null && var4.getType() == EntityType.PLAYER) {
               PiglinAi.angerNearbyPiglins((Player)var4, true);
            }
         }
      }
   }

   default InteractionResult interactWithContainerVehicle(Player var1) {
      var1.openMenu(this);
      return !var1.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
   }

   default void unpackChestVehicleLootTable(@Nullable Player var1) {
      MinecraftServer var2 = this.level().getServer();
      if (this.getLootTable() != null && var2 != null) {
         LootTable var3 = var2.reloadableRegistries().getLootTable(this.getLootTable());
         if (var1 != null) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)var1, this.getLootTable());
         }

         this.setLootTable(null);
         LootParams.Builder var4 = new LootParams.Builder((ServerLevel)this.level()).withParameter(LootContextParams.ORIGIN, this.position());
         if (var1 != null) {
            var4.withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1);
         }

         var3.fill(this, var4.create(LootContextParamSets.CHEST), this.getLootTableSeed());
      }
   }

   default void clearChestVehicleContent() {
      this.unpackChestVehicleLootTable(null);
      this.getItemStacks().clear();
   }

   default boolean isChestVehicleEmpty() {
      for (ItemStack var2 : this.getItemStacks()) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   default ItemStack removeChestVehicleItemNoUpdate(int var1) {
      this.unpackChestVehicleLootTable(null);
      ItemStack var2 = this.getItemStacks().get(var1);
      if (var2.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.getItemStacks().set(var1, ItemStack.EMPTY);
         return var2;
      }
   }

   default ItemStack getChestVehicleItem(int var1) {
      this.unpackChestVehicleLootTable(null);
      return this.getItemStacks().get(var1);
   }

   default ItemStack removeChestVehicleItem(int var1, int var2) {
      this.unpackChestVehicleLootTable(null);
      return ContainerHelper.removeItem(this.getItemStacks(), var1, var2);
   }

   default void setChestVehicleItem(int var1, ItemStack var2) {
      this.unpackChestVehicleLootTable(null);
      this.getItemStacks().set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
   }

   default SlotAccess getChestVehicleSlot(final int var1) {
      return var1 >= 0 && var1 < this.getContainerSize() ? new SlotAccess() {
         @Override
         public ItemStack get() {
            return ContainerEntity.this.getChestVehicleItem(var1);
         }

         @Override
         public boolean set(ItemStack var1x) {
            ContainerEntity.this.setChestVehicleItem(var1, var1x);
            return true;
         }
      } : SlotAccess.NULL;
   }

   default boolean isChestVehicleStillValid(Player var1) {
      return !this.isRemoved() && var1.canInteractWithEntity(this.getBoundingBox(), 4.0);
   }
}
