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

   static InteractionMap newInteractionMap(String var0) {
      Object2ObjectOpenHashMap var1 = new Object2ObjectOpenHashMap();
      var1.defaultReturnValue((CauldronInteraction)(var0x, var1x, var2x, var3, var4, var5) -> InteractionResult.TRY_WITH_EMPTY_HAND);
      InteractionMap var2 = new InteractionMap(var0, var1);
      INTERACTIONS.put(var0, var2);
      return var2;
   }

   InteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

   static void bootStrap() {
      Map var0 = EMPTY.map();
      addDefaultInteractions(var0);
      var0.put(Items.POTION, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> {
         PotionContents var6 = (PotionContents)var5.get(DataComponents.POTION_CONTENTS);
         if (var6 != null && var6.is(Potions.WATER)) {
            if (!var1x.isClientSide) {
               Item var7 = var5.getItem();
               var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
               var3x.awardStat(Stats.USE_CAULDRON);
               var3x.awardStat(Stats.ITEM_USED.get(var7));
               var1x.setBlockAndUpdate(var2x, Blocks.WATER_CAULDRON.defaultBlockState());
               var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1x.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var2x);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         }
      });
      Map var1 = WATER.map();
      addDefaultInteractions(var1);
      var1.put(Items.BUCKET, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(var0x, var1x, var2x, var3x, var4, var5, new ItemStack(Items.WATER_BUCKET), (var0) -> (Integer)var0.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
      var1.put(Items.GLASS_BOTTLE, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> {
         if (!var1x.isClientSide) {
            Item var6 = var5.getItem();
            var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
            var3x.awardStat(Stats.USE_CAULDRON);
            var3x.awardStat(Stats.ITEM_USED.get(var6));
            LayeredCauldronBlock.lowerFillLevel(var0x, var1x, var2x);
            var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1x.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, var2x);
         }

         return InteractionResult.SUCCESS;
      });
      var1.put(Items.POTION, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> {
         if ((Integer)var0x.getValue(LayeredCauldronBlock.LEVEL) == 3) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         } else {
            PotionContents var6 = (PotionContents)var5.get(DataComponents.POTION_CONTENTS);
            if (var6 != null && var6.is(Potions.WATER)) {
               if (!var1x.isClientSide) {
                  var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
                  var3x.awardStat(Stats.USE_CAULDRON);
                  var3x.awardStat(Stats.ITEM_USED.get(var5.getItem()));
                  var1x.setBlockAndUpdate(var2x, (BlockState)var0x.cycle(LayeredCauldronBlock.LEVEL));
                  var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                  var1x.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var2x);
               }

               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
         }
      });
      var1.put(Items.LEATHER_BOOTS, CauldronInteraction::dyedItemIteration);
      var1.put(Items.LEATHER_LEGGINGS, CauldronInteraction::dyedItemIteration);
      var1.put(Items.LEATHER_CHESTPLATE, CauldronInteraction::dyedItemIteration);
      var1.put(Items.LEATHER_HELMET, CauldronInteraction::dyedItemIteration);
      var1.put(Items.LEATHER_HORSE_ARMOR, CauldronInteraction::dyedItemIteration);
      var1.put(Items.WOLF_ARMOR, CauldronInteraction::dyedItemIteration);
      var1.put(Items.WHITE_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.GRAY_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.BLACK_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.BLUE_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.BROWN_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.CYAN_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.GREEN_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.LIGHT_BLUE_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.LIGHT_GRAY_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.LIME_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.MAGENTA_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.ORANGE_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.PINK_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.PURPLE_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.RED_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.YELLOW_BANNER, CauldronInteraction::bannerInteraction);
      var1.put(Items.WHITE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.GRAY_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.BLACK_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.BLUE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.BROWN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.CYAN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.GREEN_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.LIME_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.MAGENTA_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.ORANGE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.PINK_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.PURPLE_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.RED_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      var1.put(Items.YELLOW_SHULKER_BOX, CauldronInteraction::shulkerBoxInteraction);
      Map var2 = LAVA.map();
      var2.put(Items.BUCKET, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(var0x, var1x, var2x, var3x, var4, var5, new ItemStack(Items.LAVA_BUCKET), (var0) -> true, SoundEvents.BUCKET_FILL_LAVA));
      addDefaultInteractions(var2);
      Map var3 = POWDER_SNOW.map();
      var3.put(Items.BUCKET, (CauldronInteraction)(var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(var0x, var1x, var2x, var3x, var4, var5, new ItemStack(Items.POWDER_SNOW_BUCKET), (var0) -> (Integer)var0.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
      addDefaultInteractions(var3);
   }

   static void addDefaultInteractions(Map<Item, CauldronInteraction> var0) {
      var0.put(Items.LAVA_BUCKET, CauldronInteraction::fillLavaInteraction);
      var0.put(Items.WATER_BUCKET, CauldronInteraction::fillWaterInteraction);
      var0.put(Items.POWDER_SNOW_BUCKET, CauldronInteraction::fillPowderSnowInteraction);
   }

   static InteractionResult fillBucket(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5, ItemStack var6, Predicate<BlockState> var7, SoundEvent var8) {
      if (!var7.test(var0)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
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

         return InteractionResult.SUCCESS;
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

      return InteractionResult.SUCCESS;
   }

   private static InteractionResult fillWaterInteraction(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      return emptyBucket(var1, var2, var3, var4, var5, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY);
   }

   private static InteractionResult fillLavaInteraction(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      return (InteractionResult)(isUnderWater(var1, var2) ? InteractionResult.CONSUME : emptyBucket(var1, var2, var3, var4, var5, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA));
   }

   private static InteractionResult fillPowderSnowInteraction(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      return (InteractionResult)(isUnderWater(var1, var2) ? InteractionResult.CONSUME : emptyBucket(var1, var2, var3, var4, var5, (BlockState)Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW));
   }

   private static InteractionResult shulkerBoxInteraction(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      Block var6 = Block.byItem(var5.getItem());
      if (!(var6 instanceof ShulkerBoxBlock)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!var1.isClientSide) {
            ItemStack var7 = var5.transmuteCopy(Blocks.SHULKER_BOX, 1);
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var7, false));
            var3.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static InteractionResult bannerInteraction(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      BannerPatternLayers var6 = (BannerPatternLayers)var5.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      if (var6.layers().isEmpty()) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!var1.isClientSide) {
            ItemStack var7 = var5.copyWithCount(1);
            var7.set(DataComponents.BANNER_PATTERNS, var6.removeLast());
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var7, false));
            var3.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static InteractionResult dyedItemIteration(BlockState var0, Level var1, BlockPos var2, Player var3, InteractionHand var4, ItemStack var5) {
      if (!var5.is(ItemTags.DYEABLE)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else if (!var5.has(DataComponents.DYED_COLOR)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         if (!var1.isClientSide) {
            var5.remove(DataComponents.DYED_COLOR);
            var3.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private static boolean isUnderWater(Level var0, BlockPos var1) {
      FluidState var2 = var0.getFluidState(var1.above());
      return var2.is(FluidTags.WATER);
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
      public InteractionMap(String var1, Map<Item, CauldronInteraction> var2) {
         super();
         this.name = var1;
         this.map = var2;
      }
   }
}
