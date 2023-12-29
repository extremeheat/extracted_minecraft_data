package net.minecraft.world.level.block.entity;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.ticks.ContainerSingleItem;

public class DecoratedPotBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem {
   public static final String TAG_SHERDS = "sherds";
   public static final String TAG_ITEM = "item";
   public static final int EVENT_POT_WOBBLES = 1;
   public long wobbleStartedAtTick;
   @Nullable
   public DecoratedPotBlockEntity.WobbleStyle lastWobbleStyle;
   private DecoratedPotBlockEntity.Decorations decorations;
   private ItemStack item = ItemStack.EMPTY;
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   public DecoratedPotBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DECORATED_POT, var1, var2);
      this.decorations = DecoratedPotBlockEntity.Decorations.EMPTY;
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      this.decorations.save(var1);
      if (!this.trySaveLootTable(var1) && !this.item.isEmpty()) {
         var1.put("item", this.item.save(new CompoundTag()));
      }
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.decorations = DecoratedPotBlockEntity.Decorations.load(var1);
      if (!this.tryLoadLootTable(var1)) {
         if (var1.contains("item", 10)) {
            this.item = ItemStack.of(var1.getCompound("item"));
         } else {
            this.item = ItemStack.EMPTY;
         }
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public Direction getDirection() {
      return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public DecoratedPotBlockEntity.Decorations getDecorations() {
      return this.decorations;
   }

   public void setFromItem(ItemStack var1) {
      this.decorations = DecoratedPotBlockEntity.Decorations.load(BlockItem.getBlockEntityData(var1));
   }

   public ItemStack getPotAsItem() {
      return createDecoratedPotItem(this.decorations);
   }

   public static ItemStack createDecoratedPotItem(DecoratedPotBlockEntity.Decorations var0) {
      ItemStack var1 = Items.DECORATED_POT.getDefaultInstance();
      CompoundTag var2 = var0.save(new CompoundTag());
      BlockItem.setBlockEntityData(var1, BlockEntityType.DECORATED_POT, var2);
      return var1;
   }

   @Nullable
   @Override
   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   @Override
   public void setLootTable(@Nullable ResourceLocation var1) {
      this.lootTable = var1;
   }

   @Override
   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   @Override
   public void setLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   @Override
   public ItemStack getTheItem() {
      this.unpackLootTable(null);
      return this.item;
   }

   @Override
   public ItemStack splitTheItem(int var1) {
      this.unpackLootTable(null);
      ItemStack var2 = this.item.split(var1);
      if (this.item.isEmpty()) {
         this.item = ItemStack.EMPTY;
      }

      return var2;
   }

   @Override
   public void setTheItem(ItemStack var1) {
      this.unpackLootTable(null);
      this.item = var1;
   }

   @Override
   public BlockEntity getContainerBlockEntity() {
      return this;
   }

   public void wobble(DecoratedPotBlockEntity.WobbleStyle var1) {
      if (this.level != null && !this.level.isClientSide()) {
         this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, var1.ordinal());
      }
   }

   @Override
   public boolean triggerEvent(int var1, int var2) {
      if (this.level != null && var1 == 1 && var2 >= 0 && var2 < DecoratedPotBlockEntity.WobbleStyle.values().length) {
         this.wobbleStartedAtTick = this.level.getGameTime();
         this.lastWobbleStyle = DecoratedPotBlockEntity.WobbleStyle.values()[var2];
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   public static record Decorations(Item b, Item c, Item d, Item e) {
      private final Item back;
      private final Item left;
      private final Item right;
      private final Item front;
      public static final DecoratedPotBlockEntity.Decorations EMPTY = new DecoratedPotBlockEntity.Decorations(
         Items.BRICK, Items.BRICK, Items.BRICK, Items.BRICK
      );

      public Decorations(Item var1, Item var2, Item var3, Item var4) {
         super();
         this.back = var1;
         this.left = var2;
         this.right = var3;
         this.front = var4;
      }

      public CompoundTag save(CompoundTag var1) {
         if (this.equals(EMPTY)) {
            return var1;
         } else {
            ListTag var2 = new ListTag();
            this.sorted().forEach(var1x -> var2.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(var1x).toString())));
            var1.put("sherds", var2);
            return var1;
         }
      }

      public Stream<Item> sorted() {
         return Stream.of(this.back, this.left, this.right, this.front);
      }

      public static DecoratedPotBlockEntity.Decorations load(@Nullable CompoundTag var0) {
         if (var0 != null && var0.contains("sherds", 9)) {
            ListTag var1 = var0.getList("sherds", 8);
            return new DecoratedPotBlockEntity.Decorations(itemFromTag(var1, 0), itemFromTag(var1, 1), itemFromTag(var1, 2), itemFromTag(var1, 3));
         } else {
            return EMPTY;
         }
      }

      private static Item itemFromTag(ListTag var0, int var1) {
         if (var1 >= var0.size()) {
            return Items.BRICK;
         } else {
            Tag var2 = var0.get(var1);
            return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(var2.getAsString()));
         }
      }
   }

   public static enum WobbleStyle {
      POSITIVE(7),
      NEGATIVE(10);

      public final int duration;

      private WobbleStyle(int var3) {
         this.duration = var3;
      }
   }
}
