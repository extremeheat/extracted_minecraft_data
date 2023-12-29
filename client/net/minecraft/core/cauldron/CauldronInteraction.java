package net.minecraft.core.cauldron;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
   Map<String, CauldronInteraction.InteractionMap> INTERACTIONS = new Object2ObjectArrayMap();
   Codec<CauldronInteraction.InteractionMap> CODEC = ExtraCodecs.stringResolverCodec(CauldronInteraction.InteractionMap::name, INTERACTIONS::get);
   CauldronInteraction.InteractionMap EMPTY = newInteractionMap("empty");
   CauldronInteraction.InteractionMap WATER = newInteractionMap("water");
   CauldronInteraction.InteractionMap LAVA = newInteractionMap("lava");
   CauldronInteraction.InteractionMap POWDER_SNOW = newInteractionMap("powder_snow");
   CauldronInteraction FILL_WATER = (var0, var1, var2, var3, var4, var5) -> emptyBucket(
         var1,
         var2,
         var3,
         var4,
         var5,
         Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Integer.valueOf(3)),
         SoundEvents.BUCKET_EMPTY
      );
   CauldronInteraction FILL_LAVA = (var0, var1, var2, var3, var4, var5) -> emptyBucket(
         var1, var2, var3, var4, var5, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA
      );
   CauldronInteraction FILL_POWDER_SNOW = (var0, var1, var2, var3, var4, var5) -> emptyBucket(
         var1,
         var2,
         var3,
         var4,
         var5,
         Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Integer.valueOf(3)),
         SoundEvents.BUCKET_EMPTY_POWDER_SNOW
      );
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
            ItemStack var6 = var5.copyWithCount(1);
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

   static CauldronInteraction.InteractionMap newInteractionMap(String var0) {
      Object2ObjectOpenHashMap var1 = new Object2ObjectOpenHashMap();
      var1.defaultReturnValue((CauldronInteraction)(var0x, var1x, var2x, var3, var4, var5) -> InteractionResult.PASS);
      CauldronInteraction.InteractionMap var2 = new CauldronInteraction.InteractionMap(var0, var1);
      INTERACTIONS.put(var0, var2);
      return var2;
   }

   InteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

   static void bootStrap() {
      Map var0 = EMPTY.map();
      addDefaultInteractions(var0);
      var0.put(Items.POTION, (var0x, var1x, var2x, var3x, var4, var5) -> {
         if (PotionUtils.getPotion(var5) != Potions.WATER) {
            return InteractionResult.PASS;
         } else {
            if (!var1x.isClientSide) {
               Item var6 = var5.getItem();
               var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
               var3x.awardStat(Stats.USE_CAULDRON);
               var3x.awardStat(Stats.ITEM_USED.get(var6));
               var1x.setBlockAndUpdate(var2x, Blocks.WATER_CAULDRON.defaultBlockState());
               var1x.playSound(null, var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1x.gameEvent(null, GameEvent.FLUID_PLACE, var2x);
            }

            return InteractionResult.sidedSuccess(var1x.isClientSide);
         }
      });
      Map var1 = WATER.map();
      addDefaultInteractions(var1);
      var1.put(
         Items.BUCKET,
         (var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(
               var0x,
               var1x,
               var2x,
               var3x,
               var4,
               var5,
               new ItemStack(Items.WATER_BUCKET),
               var0xx -> var0xx.getValue(LayeredCauldronBlock.LEVEL) == 3,
               SoundEvents.BUCKET_FILL
            )
      );
      var1.put(Items.GLASS_BOTTLE, (var0x, var1x, var2x, var3x, var4, var5) -> {
         if (!var1x.isClientSide) {
            Item var6 = var5.getItem();
            var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            var3x.awardStat(Stats.USE_CAULDRON);
            var3x.awardStat(Stats.ITEM_USED.get(var6));
            LayeredCauldronBlock.lowerFillLevel(var0x, var1x, var2x);
            var1x.playSound(null, var2x, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1x.gameEvent(null, GameEvent.FLUID_PICKUP, var2x);
         }

         return InteractionResult.sidedSuccess(var1x.isClientSide);
      });
      var1.put(Items.POTION, (var0x, var1x, var2x, var3x, var4, var5) -> {
         if (var0x.getValue(LayeredCauldronBlock.LEVEL) != 3 && PotionUtils.getPotion(var5) == Potions.WATER) {
            if (!var1x.isClientSide) {
               var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
               var3x.awardStat(Stats.USE_CAULDRON);
               var3x.awardStat(Stats.ITEM_USED.get(var5.getItem()));
               var1x.setBlockAndUpdate(var2x, var0x.cycle(LayeredCauldronBlock.LEVEL));
               var1x.playSound(null, var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1x.gameEvent(null, GameEvent.FLUID_PLACE, var2x);
            }

            return InteractionResult.sidedSuccess(var1x.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      });
      var1.put(Items.LEATHER_BOOTS, DYED_ITEM);
      var1.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
      var1.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
      var1.put(Items.LEATHER_HELMET, DYED_ITEM);
      var1.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
      var1.put(Items.WHITE_BANNER, BANNER);
      var1.put(Items.GRAY_BANNER, BANNER);
      var1.put(Items.BLACK_BANNER, BANNER);
      var1.put(Items.BLUE_BANNER, BANNER);
      var1.put(Items.BROWN_BANNER, BANNER);
      var1.put(Items.CYAN_BANNER, BANNER);
      var1.put(Items.GREEN_BANNER, BANNER);
      var1.put(Items.LIGHT_BLUE_BANNER, BANNER);
      var1.put(Items.LIGHT_GRAY_BANNER, BANNER);
      var1.put(Items.LIME_BANNER, BANNER);
      var1.put(Items.MAGENTA_BANNER, BANNER);
      var1.put(Items.ORANGE_BANNER, BANNER);
      var1.put(Items.PINK_BANNER, BANNER);
      var1.put(Items.PURPLE_BANNER, BANNER);
      var1.put(Items.RED_BANNER, BANNER);
      var1.put(Items.YELLOW_BANNER, BANNER);
      var1.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
      var1.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
      Map var2 = LAVA.map();
      var2.put(
         Items.BUCKET,
         (var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(
               var0x, var1x, var2x, var3x, var4, var5, new ItemStack(Items.LAVA_BUCKET), var0xx -> true, SoundEvents.BUCKET_FILL_LAVA
            )
      );
      addDefaultInteractions(var2);
      Map var3 = POWDER_SNOW.map();
      var3.put(
         Items.BUCKET,
         (var0x, var1x, var2x, var3x, var4, var5) -> fillBucket(
               var0x,
               var1x,
               var2x,
               var3x,
               var4,
               var5,
               new ItemStack(Items.POWDER_SNOW_BUCKET),
               var0xx -> var0xx.getValue(LayeredCauldronBlock.LEVEL) == 3,
               SoundEvents.BUCKET_FILL_POWDER_SNOW
            )
      );
      addDefaultInteractions(var3);
   }

   static void addDefaultInteractions(Map<Item, CauldronInteraction> var0) {
      var0.put(Items.LAVA_BUCKET, FILL_LAVA);
      var0.put(Items.WATER_BUCKET, FILL_WATER);
      var0.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
   }

   static InteractionResult fillBucket(
      BlockState var0,
      Level var1,
      BlockPos var2,
      Player var3,
      InteractionHand var4,
      ItemStack var5,
      ItemStack var6,
      Predicate<BlockState> var7,
      SoundEvent var8
   ) {
      if (!var7.test(var0)) {
         return InteractionResult.PASS;
      } else {
         if (!var1.isClientSide) {
            Item var9 = var5.getItem();
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var6));
            var3.awardStat(Stats.USE_CAULDRON);
            var3.awardStat(Stats.ITEM_USED.get(var9));
            var1.setBlockAndUpdate(var2, Blocks.CAULDRON.defaultBlockState());
            var1.playSound(null, var2, var8, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1.gameEvent(null, GameEvent.FLUID_PICKUP, var2);
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
         var0.playSound(null, var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         var0.gameEvent(null, GameEvent.FLUID_PLACE, var1);
      }

      return InteractionResult.sidedSuccess(var0.isClientSide);
   }

   public static record InteractionMap(String a, Map<Item, CauldronInteraction> b) {
      private final String name;
      private final Map<Item, CauldronInteraction> map;

      public InteractionMap(String var1, Map<Item, CauldronInteraction> var2) {
         super();
         this.name = var1;
         this.map = var2;
      }
   }
}
