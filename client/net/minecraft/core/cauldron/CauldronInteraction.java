package net.minecraft.core.cauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface CauldronInteraction {
   Map<Item, CauldronInteraction> EMPTY = newInteractionMap();
   Map<Item, CauldronInteraction> WATER = newInteractionMap();
   Map<Item, CauldronInteraction> LAVA = newInteractionMap();
   Map<Item, CauldronInteraction> POWDER_SNOW = newInteractionMap();
   CauldronInteraction FILL_WATER = (var0, var1, var2, var3, var4, var5) -> {
      return emptyBucket(var1, var2, var3, var4, var5, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
   };
   CauldronInteraction FILL_LAVA = (var0, var1, var2, var3, var4, var5) -> {
      return emptyBucket(var1, var2, var3, var4, var5, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
   };
   CauldronInteraction FILL_POWDER_SNOW = (var0, var1, var2, var3, var4, var5) -> {
      return emptyBucket(var1, var2, var3, var4, var5, (BlockState)Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
   };
   CauldronInteraction SHULKER_BOX = (var0, var1, var2, var3, var4, var5) -> {
      Block var6 = Block.byItem(var5.getItem());
      if (!(var6 instanceof ShulkerBoxBlock)) {
         return InteractionResult.PASS;
      } else {
         if (!var1.isClientSide) {
            ItemStack var7 = new ItemStack(Blocks.SHULKER_BOX);
            if (var5.hasTag()) {
               var7.setTag(var5.getTag().copy());
            }

            var3.setItemInHand(var4, var7);
            var3.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return InteractionResult.sidedSuccess(var1.isClientSide);
      }
   };
   CauldronInteraction BANNER = (var0, var1, var2, var3, var4, var5) -> {
      if (BannerBlockEntity.getPatternCount(var5) <= 0) {
         return InteractionResult.PASS;
      } else {
         if (!var1.isClientSide) {
            ItemStack var6 = var5.copy();
            var6.setCount(1);
            BannerBlockEntity.removeLastPattern(var6);
            if (!var3.getAbilities().instabuild) {
               var5.shrink(1);
            }

            if (var5.isEmpty()) {
               var3.setItemInHand(var4, var6);
            } else if (var3.getInventory().add(var6)) {
               var3.inventoryMenu.sendAllDataToRemote();
            } else {
               var3.drop(var6, false);
            }

            var3.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return InteractionResult.sidedSuccess(var1.isClientSide);
      }
   };
   CauldronInteraction DYED_ITEM = (var0, var1, var2, var3, var4, var5) -> {
      Item var6 = var5.getItem();
      if (!(var6 instanceof DyeableLeatherItem)) {
         return InteractionResult.PASS;
      } else {
         DyeableLeatherItem var7 = (DyeableLeatherItem)var6;
         if (!var7.hasCustomColor(var5)) {
            return InteractionResult.PASS;
         } else {
            if (!var1.isClientSide) {
               var7.clearColor(var5);
               var3.awardStat(Stats.CLEAN_ARMOR);
               LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
            }

            return InteractionResult.sidedSuccess(var1.isClientSide);
         }
      }
   };

   static Object2ObjectOpenHashMap<Item, CauldronInteraction> newInteractionMap() {
      return (Object2ObjectOpenHashMap)Util.make(new Object2ObjectOpenHashMap(), (var0) -> {
         var0.defaultReturnValue((var0x, var1, var2, var3, var4, var5) -> {
            return InteractionResult.PASS;
         });
      });
   }

   InteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

   static void bootStrap() {
      addDefaultInteractions(EMPTY);
      EMPTY.put(Items.POTION, (var0, var1, var2, var3, var4, var5) -> {
         if (PotionUtils.getPotion(var5) != Potions.WATER) {
            return InteractionResult.PASS;
         } else {
            if (!var1.isClientSide) {
               Item var6 = var5.getItem();
               var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, new ItemStack(Items.GLASS_BOTTLE)));
               var3.awardStat(Stats.USE_CAULDRON);
               var3.awardStat(Stats.ITEM_USED.get(var6));
               var1.setBlockAndUpdate(var2, Blocks.WATER_CAULDRON.defaultBlockState());
               var1.playSound((Player)null, (BlockPos)var2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var2);
            }

            return InteractionResult.sidedSuccess(var1.isClientSide);
         }
      });
      addDefaultInteractions(WATER);
      WATER.put(Items.BUCKET, (var0, var1, var2, var3, var4, var5) -> {
         return fillBucket(var0, var1, var2, var3, var4, var5, new ItemStack(Items.WATER_BUCKET), (var0x) -> {
            return (Integer)var0x.getValue(LayeredCauldronBlock.LEVEL) == 3;
         }, SoundEvents.BUCKET_FILL);
      });
      WATER.put(Items.GLASS_BOTTLE, (var0, var1, var2, var3, var4, var5) -> {
         if (!var1.isClientSide) {
            Item var6 = var5.getItem();
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            var3.awardStat(Stats.USE_CAULDRON);
            var3.awardStat(Stats.ITEM_USED.get(var6));
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
            var1.playSound((Player)null, (BlockPos)var2, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, var2);
         }

         return InteractionResult.sidedSuccess(var1.isClientSide);
      });
      WATER.put(Items.POTION, (var0, var1, var2, var3, var4, var5) -> {
         if ((Integer)var0.getValue(LayeredCauldronBlock.LEVEL) != 3 && PotionUtils.getPotion(var5) == Potions.WATER) {
            if (!var1.isClientSide) {
               var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, new ItemStack(Items.GLASS_BOTTLE)));
               var3.awardStat(Stats.USE_CAULDRON);
               var3.awardStat(Stats.ITEM_USED.get(var5.getItem()));
               var1.setBlockAndUpdate(var2, (BlockState)var0.cycle(LayeredCauldronBlock.LEVEL));
               var1.playSound((Player)null, (BlockPos)var2, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var2);
            }

            return InteractionResult.sidedSuccess(var1.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      });
      WATER.put(Items.LEATHER_BOOTS, DYED_ITEM);
      WATER.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
      WATER.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
      WATER.put(Items.LEATHER_HELMET, DYED_ITEM);
      WATER.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
      WATER.put(Items.WHITE_BANNER, BANNER);
      WATER.put(Items.GRAY_BANNER, BANNER);
      WATER.put(Items.BLACK_BANNER, BANNER);
      WATER.put(Items.BLUE_BANNER, BANNER);
      WATER.put(Items.BROWN_BANNER, BANNER);
      WATER.put(Items.CYAN_BANNER, BANNER);
      WATER.put(Items.GREEN_BANNER, BANNER);
      WATER.put(Items.LIGHT_BLUE_BANNER, BANNER);
      WATER.put(Items.LIGHT_GRAY_BANNER, BANNER);
      WATER.put(Items.LIME_BANNER, BANNER);
      WATER.put(Items.MAGENTA_BANNER, BANNER);
      WATER.put(Items.ORANGE_BANNER, BANNER);
      WATER.put(Items.PINK_BANNER, BANNER);
      WATER.put(Items.PURPLE_BANNER, BANNER);
      WATER.put(Items.RED_BANNER, BANNER);
      WATER.put(Items.YELLOW_BANNER, BANNER);
      WATER.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
      LAVA.put(Items.BUCKET, (var0, var1, var2, var3, var4, var5) -> {
         return fillBucket(var0, var1, var2, var3, var4, var5, new ItemStack(Items.LAVA_BUCKET), (var0x) -> {
            return true;
         }, SoundEvents.BUCKET_FILL_LAVA);
      });
      addDefaultInteractions(LAVA);
      POWDER_SNOW.put(Items.BUCKET, (var0, var1, var2, var3, var4, var5) -> {
         return fillBucket(var0, var1, var2, var3, var4, var5, new ItemStack(Items.POWDER_SNOW_BUCKET), (var0x) -> {
            return (Integer)var0x.getValue(LayeredCauldronBlock.LEVEL) == 3;
         }, SoundEvents.BUCKET_FILL_POWDER_SNOW);
      });
      addDefaultInteractions(POWDER_SNOW);
   }

   static void addDefaultInteractions(Map<Item, CauldronInteraction> var0) {
      var0.put(Items.LAVA_BUCKET, FILL_LAVA);
      var0.put(Items.WATER_BUCKET, FILL_WATER);
      var0.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
   }

   static InteractionResult fillBucket(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5, ItemStack var6, Predicate<BlockState> var7, SoundEvent var8) {
      if (!var7.test(var0)) {
         return InteractionResult.PASS;
      } else {
         if (!var1.isClientSide) {
            Item var9 = var5.getItem();
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var6));
            var3.awardStat(Stats.USE_CAULDRON);
            var3.awardStat(Stats.ITEM_USED.get(var9));
            var1.setBlockAndUpdate(var2, Blocks.CAULDRON.defaultBlockState());
            var1.playSound((Player)null, (BlockPos)var2, var8, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, var2);
         }

         return InteractionResult.sidedSuccess(var1.isClientSide);
      }
   }

   static InteractionResult emptyBucket(Level var0, BlockPos var1, Player var2, InteractionHand var3, ItemStack var4, BlockState var5, SoundEvent var6) {
      if (!var0.isClientSide) {
         Item var7 = var4.getItem();
         var2.setItemInHand(var3, ItemUtils.createFilledResult(var4, var2, new ItemStack(Items.BUCKET)));
         var2.awardStat(Stats.FILL_CAULDRON);
         var2.awardStat(Stats.ITEM_USED.get(var7));
         var0.setBlockAndUpdate(var1, var5);
         var0.playSound((Player)null, (BlockPos)var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         var0.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var1);
      }

      return InteractionResult.sidedSuccess(var0.isClientSide);
   }
}
