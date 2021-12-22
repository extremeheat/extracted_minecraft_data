package net.minecraft.client.color.item;

import java.util.Iterator;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
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
         return var1x > 0 ? -1 : ((DyeableLeatherItem)var0x.getItem()).getColor(var0x);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
      var1.register((var0x, var1x) -> {
         return GrassColor.get(0.5D, 1.0D);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      var1.register((var0x, var1x) -> {
         if (var1x != 1) {
            return -1;
         } else {
            CompoundTag var2 = var0x.getTagElement("Explosion");
            int[] var3 = var2 != null && var2.contains("Colors", 11) ? var2.getIntArray("Colors") : null;
            if (var3 != null && var3.length != 0) {
               if (var3.length == 1) {
                  return var3[0];
               } else {
                  int var4 = 0;
                  int var5 = 0;
                  int var6 = 0;
                  int[] var7 = var3;
                  int var8 = var3.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     int var10 = var7[var9];
                     var4 += (var10 & 16711680) >> 16;
                     var5 += (var10 & '\uff00') >> 8;
                     var6 += (var10 & 255) >> 0;
                  }

                  var4 /= var3.length;
                  var5 /= var3.length;
                  var6 /= var3.length;
                  return var4 << 16 | var5 << 8 | var6;
               }
            } else {
               return 9079434;
            }
         }
      }, Items.FIREWORK_STAR);
      var1.register((var0x, var1x) -> {
         return var1x > 0 ? -1 : PotionUtils.getColor(var0x);
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
      Iterator var2 = SpawnEggItem.eggs().iterator();

      while(var2.hasNext()) {
         SpawnEggItem var3 = (SpawnEggItem)var2.next();
         var1.register((var1x, var2x) -> {
            return var3.getColor(var2x);
         }, var3);
      }

      var1.register((var1x, var2x) -> {
         BlockState var3 = ((BlockItem)var1x.getItem()).getBlock().defaultBlockState();
         return var0.getColor(var3, (BlockAndTintGetter)null, (BlockPos)null, var2x);
      }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      var1.register((var0x, var1x) -> {
         return var1x == 0 ? PotionUtils.getColor(var0x) : -1;
      }, Items.TIPPED_ARROW);
      var1.register((var0x, var1x) -> {
         return var1x == 0 ? -1 : MapItem.getColor(var0x);
      }, Items.FILLED_MAP);
      return var1;
   }

   public int getColor(ItemStack var1, int var2) {
      ItemColor var3 = (ItemColor)this.itemColors.byId(Registry.ITEM.getId(var1.getItem()));
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
