package net.minecraft.world.level.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

public class DecoratedPotBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
   public static final String TAG_SHERDS = "sherds";
   public static final String TAG_ITEM = "item";
   public static final int EVENT_POT_WOBBLES = 1;
   public long wobbleStartedAtTick;
   @Nullable
   public WobbleStyle lastWobbleStyle;
   private PotDecorations decorations;
   private ItemStack item;
   @Nullable
   protected ResourceKey<LootTable> lootTable;
   protected long lootTableSeed;

   public DecoratedPotBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DECORATED_POT, var1, var2);
      this.item = ItemStack.EMPTY;
      this.decorations = PotDecorations.EMPTY;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      this.decorations.save(var1);
      if (!this.trySaveLootTable(var1) && !this.item.isEmpty()) {
         var1.put("item", this.item.save(var2));
      }

   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.decorations = PotDecorations.load(var1);
      if (!this.tryLoadLootTable(var1)) {
         if (var1.contains("item", 10)) {
            this.item = (ItemStack)ItemStack.parse(var2, var1.getCompound("item")).orElse(ItemStack.EMPTY);
         } else {
            this.item = ItemStack.EMPTY;
         }
      }

   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public Direction getDirection() {
      return (Direction)this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public PotDecorations getDecorations() {
      return this.decorations;
   }

   public void setFromItem(ItemStack var1) {
      this.applyComponentsFromItemStack(var1);
   }

   public ItemStack getPotAsItem() {
      ItemStack var1 = Items.DECORATED_POT.getDefaultInstance();
      var1.applyComponents(this.collectComponents());
      return var1;
   }

   public static ItemStack createDecoratedPotItem(PotDecorations var0) {
      ItemStack var1 = Items.DECORATED_POT.getDefaultInstance();
      var1.set(DataComponents.POT_DECORATIONS, var0);
      return var1;
   }

   @Nullable
   public ResourceKey<LootTable> getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(@Nullable ResourceKey<LootTable> var1) {
      this.lootTable = var1;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.POT_DECORATIONS, this.decorations);
      var1.set(DataComponents.CONTAINER, ItemContainerContents.copyOf(List.of(this.item)));
   }

   protected void applyImplicitComponents(BlockEntity.DataComponentInput var1) {
      super.applyImplicitComponents(var1);
      this.decorations = (PotDecorations)var1.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
      this.item = ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyOne();
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      super.removeComponentsFromTag(var1);
      var1.remove("sherds");
      var1.remove("item");
   }

   public ItemStack getTheItem() {
      this.unpackLootTable((Player)null);
      return this.item;
   }

   public ItemStack splitTheItem(int var1) {
      this.unpackLootTable((Player)null);
      ItemStack var2 = this.item.split(var1);
      if (this.item.isEmpty()) {
         this.item = ItemStack.EMPTY;
      }

      return var2;
   }

   public void setTheItem(ItemStack var1) {
      this.unpackLootTable((Player)null);
      this.item = var1;
   }

   public BlockEntity getContainerBlockEntity() {
      return this;
   }

   public void wobble(WobbleStyle var1) {
      if (this.level != null && !this.level.isClientSide()) {
         this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, var1.ordinal());
      }
   }

   public boolean triggerEvent(int var1, int var2) {
      if (this.level != null && var1 == 1 && var2 >= 0 && var2 < DecoratedPotBlockEntity.WobbleStyle.values().length) {
         this.wobbleStartedAtTick = this.level.getGameTime();
         this.lastWobbleStyle = DecoratedPotBlockEntity.WobbleStyle.values()[var2];
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }

   public static enum WobbleStyle {
      POSITIVE(7),
      NEGATIVE(10);

      public final int duration;

      private WobbleStyle(int var3) {
         this.duration = var3;
      }

      // $FF: synthetic method
      private static WobbleStyle[] $values() {
         return new WobbleStyle[]{POSITIVE, NEGATIVE};
      }
   }
}
