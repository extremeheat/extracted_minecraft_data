package net.minecraft.world.level.block.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DecoratedPotBlockEntity extends BlockEntity {
   private static final String TAG_SHARDS = "shards";
   private static final int SHARDS_IN_POT = 4;
   private boolean isBroken = false;
   private final List<Item> shards = Util.make(new ArrayList<>(4), var0 -> {
      var0.add(Items.BRICK);
      var0.add(Items.BRICK);
      var0.add(Items.BRICK);
      var0.add(Items.BRICK);
   });

   public DecoratedPotBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DECORATED_POT, var1, var2);
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      saveShards(this.shards, var1);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("shards", 9)) {
         ListTag var2 = var1.getList("shards", 8);
         this.shards.clear();
         int var3 = Math.min(4, var2.size());

         for(int var4 = 0; var4 < var3; ++var4) {
            Tag var6 = var2.get(var4);
            if (var6 instanceof StringTag var5) {
               this.shards.add(BuiltInRegistries.ITEM.get(new ResourceLocation(var5.getAsString())));
            } else {
               this.shards.add(Items.BRICK);
            }
         }

         int var7 = 4 - var3;

         for(int var8 = 0; var8 < var7; ++var8) {
            this.shards.add(Items.BRICK);
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

   public static void saveShards(List<Item> var0, CompoundTag var1) {
      ListTag var2 = new ListTag();

      for(Item var4 : var0) {
         var2.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(var4).toString()));
      }

      var1.put("shards", var2);
   }

   public ItemStack getItem() {
      ItemStack var1 = new ItemStack(Blocks.DECORATED_POT);
      CompoundTag var2 = new CompoundTag();
      saveShards(this.shards, var2);
      BlockItem.setBlockEntityData(var1, BlockEntityType.DECORATED_POT, var2);
      return var1;
   }

   public List<Item> getShards() {
      return this.shards;
   }

   public void playerDestroy(Level var1, BlockPos var2, ItemStack var3, Player var4) {
      if (var4.isCreative()) {
         this.isBroken = true;
      } else {
         if (var3.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasSilkTouch(var3)) {
            List var5 = this.getShards();
            NonNullList var6 = NonNullList.createWithCapacity(var5.size());
            var6.addAll(0, var5.stream().map(Item::getDefaultInstance).toList());
            Containers.dropContents(var1, var2, var6);
            this.isBroken = true;
            var1.playSound(null, var2, SoundEvents.DECORATED_POT_SHATTER, SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      }
   }

   public boolean isBroken() {
      return this.isBroken;
   }

   public Direction getDirection() {
      return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
   }

   public void setFromItem(ItemStack var1) {
      CompoundTag var2 = BlockItem.getBlockEntityData(var1);
      if (var2 != null) {
         this.load(var2);
      }
   }
}
