package net.minecraft.client.color.item;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemColors {
   private static final int DEFAULT = -1;
   private final IdMapper<ItemColor> itemColors = new IdMapper(32);

   public ItemColors() {
      super();
   }

   public static ItemColors createDefault(BlockColors var0) {
      ItemColors var1 = new ItemColors();
      var1.register((var0x, var1x) -> {
         return var1x > 0 ? -1 : DyedItemColor.getOrDefault(var0x, -6265536);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
      var1.register((var0x, var1x) -> {
         return var1x != 1 ? -1 : DyedItemColor.getOrDefault(var0x, 0);
      }, Items.WOLF_ARMOR);
      var1.register((var0x, var1x) -> {
         return GrassColor.get(0.5, 1.0);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      var1.register((var0x, var1x) -> {
         if (var1x != 1) {
            return -1;
         } else {
            FireworkExplosion var2 = (FireworkExplosion)var0x.get(DataComponents.FIREWORK_EXPLOSION);
            IntList var3 = var2 != null ? var2.colors() : IntList.of();
            int var4 = var3.size();
            if (var4 == 0) {
               return -7697782;
            } else if (var4 == 1) {
               return ARGB.opaque(var3.getInt(0));
            } else {
               int var5 = 0;
               int var6 = 0;
               int var7 = 0;

               for(int var8 = 0; var8 < var4; ++var8) {
                  int var9 = var3.getInt(var8);
                  var5 += ARGB.red(var9);
                  var6 += ARGB.green(var9);
                  var7 += ARGB.blue(var9);
               }

               return ARGB.color(var5 / var4, var6 / var4, var7 / var4);
            }
         }
      }, Items.FIREWORK_STAR);
      var1.register((var0x, var1x) -> {
         return var1x > 0 ? -1 : ARGB.opaque(((PotionContents)var0x.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getColor());
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW);
      Iterator var2 = SpawnEggItem.eggs().iterator();

      while(var2.hasNext()) {
         SpawnEggItem var3 = (SpawnEggItem)var2.next();
         var1.register((var1x, var2x) -> {
            return ARGB.opaque(var3.getColor(var2x));
         }, var3);
      }

      var1.register((var1x, var2x) -> {
         BlockState var3 = ((BlockItem)var1x.getItem()).getBlock().defaultBlockState();
         return var0.getColor(var3, (BlockAndTintGetter)null, (BlockPos)null, var2x);
      }, Blocks.GRASS_BLOCK, Blocks.SHORT_GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      var1.register((var0x, var1x) -> {
         return FoliageColor.getMangroveColor();
      }, Blocks.MANGROVE_LEAVES);
      var1.register((var0x, var1x) -> {
         return var1x == 0 ? -1 : ARGB.opaque(((MapItemColor)var0x.getOrDefault(DataComponents.MAP_COLOR, MapItemColor.DEFAULT)).rgb());
      }, Items.FILLED_MAP);
      return var1;
   }

   public int getColor(ItemStack var1, int var2) {
      ItemColor var3 = (ItemColor)this.itemColors.byId(BuiltInRegistries.ITEM.getId(var1.getItem()));
      return var3 == null ? -1 : var3.getColor(var1, var2);
   }

   public void register(ItemColor var1, ItemLike... var2) {
      ItemLike[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ItemLike var6 = var3[var5];
         this.itemColors.addMapping(var1, Item.getId(var6.asItem()));
      }

   }
}
