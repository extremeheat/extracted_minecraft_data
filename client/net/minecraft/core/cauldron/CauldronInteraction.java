package net.minecraft.core.cauldron;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

public interface CauldronInteraction {
   Map<String, InteractionMap> INTERACTIONS = new Object2ObjectArrayMap();
   Codec<InteractionMap> CODEC;
   InteractionMap EMPTY;
   InteractionMap WATER;
   InteractionMap LAVA;
   InteractionMap POWDER_SNOW;

   static InteractionMap newInteractionMap(final String name) {
      Object2ObjectOpenHashMap<Item, CauldronInteraction> map = new Object2ObjectOpenHashMap();
      map.defaultReturnValue((CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> InteractionResult.TRY_WITH_EMPTY_HAND);
      InteractionMap interactionMap = new InteractionMap(name, map);
      INTERACTIONS.put(name, interactionMap);
      return interactionMap;
   }

   InteractionResult interact(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand);

   static void bootStrap() {
      Map<Item, CauldronInteraction> empty = EMPTY.map();
      addDefaultInteractions(empty);
      empty.put(Items.POTION, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> {
         PotionContents potion = (PotionContents)itemInHand.get(DataComponents.POTION_CONTENTS);
         if (potion != null && potion.is(Potions.WATER)) {
            if (!level.isClientSide()) {
               Item usedItem = itemInHand.getItem();
               player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.GLASS_BOTTLE)));
               player.awardStat(Stats.USE_CAULDRON);
               player.awardStat(Stats.ITEM_USED.get(usedItem));
               level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState());
               level.playSound((Entity)null, (BlockPos)pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         }
      });
      Map<Item, CauldronInteraction> water = WATER.map();
      addDefaultInteractions(water);
      water.put(Items.BUCKET, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.WATER_BUCKET), (s) -> (Integer)s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
      water.put(Items.GLASS_BOTTLE, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> {
         if (!level.isClientSide()) {
            Item usedItem = itemInHand.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(usedItem));
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            level.playSound((Entity)null, (BlockPos)pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
         }

         return InteractionResult.SUCCESS;
      });
      water.put(Items.POTION, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> {
         if ((Integer)state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         } else {
            PotionContents potion = (PotionContents)itemInHand.get(DataComponents.POTION_CONTENTS);
            if (potion != null && potion.is(Potions.WATER)) {
               if (!level.isClientSide()) {
                  player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.GLASS_BOTTLE)));
                  player.awardStat(Stats.USE_CAULDRON);
                  player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
                  level.setBlockAndUpdate(pos, (BlockState)state.cycle(LayeredCauldronBlock.LEVEL));
                  level.playSound((Entity)null, (BlockPos)pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                  level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
               }

               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
         }
      });
      water.put(Items.LEATHER_BOOTS, CauldronInteraction::dyedItemIteration);
      water.put(Items.LEATHER_LEGGINGS, CauldronInteraction::dyedItemIteration);
      water.put(Items.LEATHER_CHESTPLATE, CauldronInteraction::dyedItemIteration);
      water.put(Items.LEATHER_HELMET, CauldronInteraction::dyedItemIteration);
      water.put(Items.LEATHER_HORSE_ARMOR, CauldronInteraction::dyedItemIteration);
      water.put(Items.WOLF_ARMOR, CauldronInteraction::dyedItemIteration);
      water.put(Items.WHITE_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.GRAY_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.BLACK_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.BLUE_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.BROWN_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.CYAN_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.GREEN_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.LIGHT_BLUE_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.LIGHT_GRAY_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.LIME_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.MAGENTA_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.ORANGE_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.PINK_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.PURPLE_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.RED_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.YELLOW_BANNER, CauldronInteraction::bannerInteraction);
      water.put(Items.WHITE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.GRAY_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.BLACK_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.BLUE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.BROWN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.CYAN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.GREEN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.LIME_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.MAGENTA_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.ORANGE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.PINK_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.PURPLE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.RED_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      water.put(Items.YELLOW_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      Map<Item, CauldronInteraction> lava = LAVA.map();
      lava.put(Items.BUCKET, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.LAVA_BUCKET), (p) -> true, SoundEvents.BUCKET_FILL_LAVA));
      addDefaultInteractions(lava);
      Map<Item, CauldronInteraction> powderSnow = POWDER_SNOW.map();
      powderSnow.put(Items.BUCKET, (CauldronInteraction)(state, level, pos, player, hand, itemInHand) -> fillBucket(state, level, pos, player, hand, itemInHand, new ItemStack(Items.POWDER_SNOW_BUCKET), (s) -> (Integer)s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
      addDefaultInteractions(powderSnow);
   }

   static void addDefaultInteractions(final Map<Item, CauldronInteraction> interactionMap) {
      interactionMap.put(Items.LAVA_BUCKET, CauldronInteraction::fillLavaInteraction);
      interactionMap.put(Items.WATER_BUCKET, CauldronInteraction::fillWaterInteraction);
      interactionMap.put(Items.POWDER_SNOW_BUCKET, CauldronInteraction::fillPowderSnowInteraction);
   }

   static InteractionResult fillBucket(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand, final ItemStack newItem, final Predicate<BlockState> canFill, final SoundEvent soundEvent) {
      if (!canFill.test(state)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!level.isClientSide()) {
            Item itemUsed = itemInHand.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, newItem));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(itemUsed));
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            level.playSound((Entity)null, (BlockPos)pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   static InteractionResult emptyBucket(final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand, final BlockState newState, final SoundEvent soundEvent) {
      if (!level.isClientSide()) {
         Item itemUsed = itemInHand.getItem();
         player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, new ItemStack(Items.BUCKET)));
         player.awardStat(Stats.FILL_CAULDRON);
         player.awardStat(Stats.ITEM_USED.get(itemUsed));
         level.setBlockAndUpdate(pos, newState);
         level.playSound((Entity)null, (BlockPos)pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
      }

      return InteractionResult.SUCCESS;
   }

   private static InteractionResult fillWaterInteraction(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      return emptyBucket(level, pos, player, hand, itemInHand, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
   }

   private static InteractionResult fillLavaInteraction(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      return (InteractionResult)(isUnderWater(level, pos) ? InteractionResult.CONSUME : emptyBucket(level, pos, player, hand, itemInHand, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA));
   }

   private static InteractionResult fillPowderSnowInteraction(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      return (InteractionResult)(isUnderWater(level, pos) ? InteractionResult.CONSUME : emptyBucket(level, pos, player, hand, itemInHand, (BlockState)Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW));
   }

   private static InteractionResult shulkerBoxInteraction(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      Block block = Block.byItem(itemInHand.getItem());
      if (!(block instanceof ShulkerBoxBlock)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!level.isClientSide()) {
            ItemStack cleanedShulkerBox = itemInHand.transmuteCopy(Blocks.SHULKER_BOX, 1);
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, cleanedShulkerBox, false));
            player.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static InteractionResult bannerInteraction(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      BannerPatternLayers patterns = (BannerPatternLayers)itemInHand.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      if (patterns.layers().isEmpty()) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!level.isClientSide()) {
            ItemStack cleanedBanner = itemInHand.copyWithCount(1);
            cleanedBanner.set(DataComponents.BANNER_PATTERNS, patterns.removeLast());
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemInHand, player, cleanedBanner, false));
            player.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static InteractionResult dyedItemIteration(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final ItemStack itemInHand) {
      if (!itemInHand.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else if (!itemInHand.has(DataComponents.DYED_COLOR)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!level.isClientSide()) {
            itemInHand.remove(DataComponents.DYED_COLOR);
            player.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static boolean isUnderWater(final Level level, final BlockPos pos) {
      FluidState fluidState = level.getFluidState(pos.above());
      return fluidState.is(FluidTags.WATER);
   }

   static {
      Function var10000 = InteractionMap::name;
      Map var10001 = INTERACTIONS;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      EMPTY = newInteractionMap("empty");
      WATER = newInteractionMap("water");
      LAVA = newInteractionMap("lava");
      POWDER_SNOW = newInteractionMap("powder_snow");
   }

   public static record InteractionMap(String name, Map<Item, CauldronInteraction> map) {
      public InteractionMap {
         super();
      }
   }
}
