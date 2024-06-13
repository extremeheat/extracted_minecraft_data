package net.minecraft.client.color.item;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemColors {
   private static final int DEFAULT = -1;
   private final IdMapper<ItemColor> itemColors = new IdMapper<>(32);

   public ItemColors() {
      super();
   }

   public static ItemColors createDefault(BlockColors var0) {
      ItemColors var1 = new ItemColors();
      var1.register(
         (var0x, var1x) -> var1x > 0 ? -1 : DyedItemColor.getOrDefault(var0x, -6265536),
         Items.LEATHER_HELMET,
         Items.LEATHER_CHESTPLATE,
         Items.LEATHER_LEGGINGS,
         Items.LEATHER_BOOTS,
         Items.LEATHER_HORSE_ARMOR
      );
      var1.register((var0x, var1x) -> var1x != 1 ? -1 : DyedItemColor.getOrDefault(var0x, 0), Items.WOLF_ARMOR);
      var1.register((var0x, var1x) -> GrassColor.get(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      var1.register((var0x, var1x) -> {
         if (var1x != 1) {
            return -1;
         } else {
            FireworkExplosion var2 = var0x.get(DataComponents.FIREWORK_EXPLOSION);
            IntList var3x = var2 != null ? var2.colors() : IntList.of();
            int var4 = var3x.size();
            if (var4 == 0) {
               return -7697782;
            } else if (var4 == 1) {
               return FastColor.ARGB32.opaque(var3x.getInt(0));
            } else {
               int var5 = 0;
               int var6 = 0;
               int var7 = 0;

               for (int var8 = 0; var8 < var4; var8++) {
                  int var9 = var3x.getInt(var8);
                  var5 += FastColor.ARGB32.red(var9);
                  var6 += FastColor.ARGB32.green(var9);
                  var7 += FastColor.ARGB32.blue(var9);
               }

               return FastColor.ARGB32.color(var5 / var4, var6 / var4, var7 / var4);
            }
         }
      }, Items.FIREWORK_STAR);
      var1.register(
         (var0x, var1x) -> var1x > 0 ? -1 : FastColor.ARGB32.opaque(var0x.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor()),
         Items.POTION,
         Items.SPLASH_POTION,
         Items.LINGERING_POTION,
         Items.TIPPED_ARROW
      );

      for (SpawnEggItem var3 : SpawnEggItem.eggs()) {
         var1.register((var1x, var2) -> FastColor.ARGB32.opaque(var3.getColor(var2)), var3);
      }

      var1.register(
         (var1x, var2) -> {
            BlockState var3x = ((BlockItem)var1x.getItem()).getBlock().defaultBlockState();
            return var0.getColor(var3x, null, null, var2);
         },
         Blocks.GRASS_BLOCK,
         Blocks.SHORT_GRASS,
         Blocks.FERN,
         Blocks.VINE,
         Blocks.OAK_LEAVES,
         Blocks.SPRUCE_LEAVES,
         Blocks.BIRCH_LEAVES,
         Blocks.JUNGLE_LEAVES,
         Blocks.ACACIA_LEAVES,
         Blocks.DARK_OAK_LEAVES,
         Blocks.LILY_PAD
      );
      var1.register((var0x, var1x) -> FoliageColor.getMangroveColor(), Blocks.MANGROVE_LEAVES);
      var1.register(
         (var0x, var1x) -> var1x == 0 ? -1 : FastColor.ARGB32.opaque(var0x.getOrDefault(DataComponents.MAP_COLOR, MapItemColor.DEFAULT).rgb()),
         Items.FILLED_MAP
      );
      return var1;
   }

   public int getColor(ItemStack var1, int var2) {
      ItemColor var3 = this.itemColors.byId(BuiltInRegistries.ITEM.getId(var1.getItem()));
      return var3 == null ? -1 : var3.getColor(var1, var2);
   }

   public void register(ItemColor var1, ItemLike... var2) {
      for (ItemLike var6 : var2) {
         this.itemColors.addMapping(var1, Item.getId(var6.asItem()));
      }
   }
}
