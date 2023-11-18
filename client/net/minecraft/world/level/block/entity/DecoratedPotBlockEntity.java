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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DecoratedPotBlockEntity extends BlockEntity {
   public static final String TAG_SHERDS = "sherds";
   private DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.EMPTY;

   public DecoratedPotBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DECORATED_POT, var1, var2);
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      this.decorations.save(var1);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.decorations = DecoratedPotBlockEntity.Decorations.load(var1);
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
         ListTag var2 = new ListTag();
         this.sorted().forEach(var1x -> var2.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(var1x).toString())));
         var1.put("sherds", var2);
         return var1;
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
            return BuiltInRegistries.ITEM.get(new ResourceLocation(var2.getAsString()));
         }
      }
   }
}
