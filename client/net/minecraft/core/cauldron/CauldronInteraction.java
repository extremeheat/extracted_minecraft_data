package net.minecraft.core.cauldron;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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

public interface CauldronInteraction {
   Map<String, CauldronInteraction.InteractionMap> INTERACTIONS = new Object2ObjectArrayMap();
   Codec<CauldronInteraction.InteractionMap> CODEC = Codec.stringResolver(CauldronInteraction.InteractionMap::name, INTERACTIONS::get);
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
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!var1.isClientSide) {
            ItemStack var7 = var5.transmuteCopy(Blocks.SHULKER_BOX, 1);
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var7, false));
            var3.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return ItemInteractionResult.sidedSuccess(var1.isClientSide);
      }
   };
   CauldronInteraction BANNER = (var0, var1, var2, var3, var4, var5) -> {
      BannerPatternLayers var6 = var5.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
      if (var6.layers().isEmpty()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!var1.isClientSide) {
            ItemStack var7 = var5.copyWithCount(1);
            var7.set(DataComponents.BANNER_PATTERNS, var6.removeLast());
            var3.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3, var7, false));
            var3.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return ItemInteractionResult.sidedSuccess(var1.isClientSide);
      }
   };
   CauldronInteraction DYED_ITEM = (var0, var1, var2, var3, var4, var5) -> {
      if (!var5.is(ItemTags.DYEABLE)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (!var5.has(DataComponents.DYED_COLOR)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!var1.isClientSide) {
            var5.remove(DataComponents.DYED_COLOR);
            var3.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(var0, var1, var2);
         }

         return ItemInteractionResult.sidedSuccess(var1.isClientSide);
      }
   };

   static CauldronInteraction.InteractionMap newInteractionMap(String var0) {
      Object2ObjectOpenHashMap var1 = new Object2ObjectOpenHashMap();
      var1.defaultReturnValue((CauldronInteraction)(var0x, var1x, var2x, var3, var4, var5) -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
      CauldronInteraction.InteractionMap var2 = new CauldronInteraction.InteractionMap(var0, var1);
      INTERACTIONS.put(var0, var2);
      return var2;
   }

   ItemInteractionResult interact(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, ItemStack var6);

   static void bootStrap() {
      Map var0 = EMPTY.map();
      addDefaultInteractions(var0);
      var0.put(Items.POTION, (var0x, var1x, var2x, var3x, var4, var5) -> {
         PotionContents var6 = var5.get(DataComponents.POTION_CONTENTS);
         if (var6 != null && var6.is(Potions.WATER)) {
            if (!var1x.isClientSide) {
               Item var7 = var5.getItem();
               var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
               var3x.awardStat(Stats.USE_CAULDRON);
               var3x.awardStat(Stats.ITEM_USED.get(var7));
               var1x.setBlockAndUpdate(var2x, Blocks.WATER_CAULDRON.defaultBlockState());
               var1x.playSound(null, var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1x.gameEvent(null, GameEvent.FLUID_PLACE, var2x);
            }

            return ItemInteractionResult.sidedSuccess(var1x.isClientSide);
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
            var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
            var3x.awardStat(Stats.USE_CAULDRON);
            var3x.awardStat(Stats.ITEM_USED.get(var6));
            LayeredCauldronBlock.lowerFillLevel(var0x, var1x, var2x);
            var1x.playSound(null, var2x, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1x.gameEvent(null, GameEvent.FLUID_PICKUP, var2x);
         }

         return ItemInteractionResult.sidedSuccess(var1x.isClientSide);
      });
      var1.put(Items.POTION, (var0x, var1x, var2x, var3x, var4, var5) -> {
         if (var0x.getValue(LayeredCauldronBlock.LEVEL) == 3) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else {
            PotionContents var6 = var5.get(DataComponents.POTION_CONTENTS);
            if (var6 != null && var6.is(Potions.WATER)) {
               if (!var1x.isClientSide) {
                  var3x.setItemInHand(var4, ItemUtils.createFilledResult(var5, var3x, new ItemStack(Items.GLASS_BOTTLE)));
                  var3x.awardStat(Stats.USE_CAULDRON);
                  var3x.awardStat(Stats.ITEM_USED.get(var5.getItem()));
                  var1x.setBlockAndUpdate(var2x, var0x.cycle(LayeredCauldronBlock.LEVEL));
                  var1x.playSound(null, var2x, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                  var1x.gameEvent(null, GameEvent.FLUID_PLACE, var2x);
               }

               return ItemInteractionResult.sidedSuccess(var1x.isClientSide);
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         }
      });
      var1.put(Items.LEATHER_BOOTS, DYED_ITEM);
      var1.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
      var1.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
      var1.put(Items.LEATHER_HELMET, DYED_ITEM);
      var1.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
      var1.put(Items.WOLF_ARMOR, DYED_ITEM);
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

   static ItemInteractionResult fillBucket(
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
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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

         return ItemInteractionResult.sidedSuccess(var1.isClientSide);
      }
   }

   static ItemInteractionResult emptyBucket(Level var0, BlockPos var1, Player var2, InteractionHand var3, ItemStack var4, BlockState var5, SoundEvent var6) {
      if (!var0.isClientSide) {
         Item var7 = var4.getItem();
         var2.setItemInHand(var3, ItemUtils.createFilledResult(var4, var2, new ItemStack(Items.BUCKET)));
         var2.awardStat(Stats.FILL_CAULDRON);
         var2.awardStat(Stats.ITEM_USED.get(var7));
         var0.setBlockAndUpdate(var1, var5);
         var0.playSound(null, var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         var0.gameEvent(null, GameEvent.FLUID_PLACE, var1);
      }

      return ItemInteractionResult.sidedSuccess(var0.isClientSide);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
