package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe extends CustomRecipe {
   private static final Ingredient SHAPE_INGREDIENT;
   private static final Ingredient TRAIL_INGREDIENT;
   private static final Ingredient FLICKER_INGREDIENT;
   private static final Map<Item, FireworkRocketItem.Shape> SHAPE_BY_ITEM;
   private static final Ingredient GUNPOWDER_INGREDIENT;

   public FireworkStarRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;

      for(int var8 = 0; var8 < var1.getContainerSize(); ++var8) {
         ItemStack var9 = var1.getItem(var8);
         if (!var9.isEmpty()) {
            if (SHAPE_INGREDIENT.test(var9)) {
               if (var5) {
                  return false;
               }

               var5 = true;
            } else if (FLICKER_INGREDIENT.test(var9)) {
               if (var7) {
                  return false;
               }

               var7 = true;
            } else if (TRAIL_INGREDIENT.test(var9)) {
               if (var6) {
                  return false;
               }

               var6 = true;
            } else if (GUNPOWDER_INGREDIENT.test(var9)) {
               if (var3) {
                  return false;
               }

               var3 = true;
            } else {
               if (!(var9.getItem() instanceof DyeItem)) {
                  return false;
               }

               var4 = true;
            }
         }
      }

      return var3 && var4;
   }

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag var3 = var2.getOrCreateTagElement("Explosion");
      FireworkRocketItem.Shape var4 = FireworkRocketItem.Shape.SMALL_BALL;
      ArrayList var5 = Lists.newArrayList();

      for(int var6 = 0; var6 < var1.getContainerSize(); ++var6) {
         ItemStack var7 = var1.getItem(var6);
         if (!var7.isEmpty()) {
            if (SHAPE_INGREDIENT.test(var7)) {
               var4 = (FireworkRocketItem.Shape)SHAPE_BY_ITEM.get(var7.getItem());
            } else if (FLICKER_INGREDIENT.test(var7)) {
               var3.putBoolean("Flicker", true);
            } else if (TRAIL_INGREDIENT.test(var7)) {
               var3.putBoolean("Trail", true);
            } else if (var7.getItem() instanceof DyeItem) {
               var5.add(((DyeItem)var7.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      var3.putIntArray("Colors", (List)var5);
      var3.putByte("Type", (byte)var4.getId());
      return var2;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack getResultItem() {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR;
   }

   static {
      SHAPE_INGREDIENT = Ingredient.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
      TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
      FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
      SHAPE_BY_ITEM = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(Items.FIRE_CHARGE, FireworkRocketItem.Shape.LARGE_BALL);
         var0.put(Items.FEATHER, FireworkRocketItem.Shape.BURST);
         var0.put(Items.GOLD_NUGGET, FireworkRocketItem.Shape.STAR);
         var0.put(Items.SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
         var0.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
         var0.put(Items.CREEPER_HEAD, FireworkRocketItem.Shape.CREEPER);
         var0.put(Items.PLAYER_HEAD, FireworkRocketItem.Shape.CREEPER);
         var0.put(Items.DRAGON_HEAD, FireworkRocketItem.Shape.CREEPER);
         var0.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Shape.CREEPER);
      });
      GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
   }
}
