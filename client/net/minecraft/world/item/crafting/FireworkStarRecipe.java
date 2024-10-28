package net.minecraft.world.item.crafting;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe extends CustomRecipe {
   private static final Ingredient SHAPE_INGREDIENT;
   private static final Ingredient TRAIL_INGREDIENT;
   private static final Ingredient TWINKLE_INGREDIENT;
   private static final Map<Item, FireworkExplosion.Shape> SHAPE_BY_ITEM;
   private static final Ingredient GUNPOWDER_INGREDIENT;

   public FireworkStarRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;

      for(int var8 = 0; var8 < var1.size(); ++var8) {
         ItemStack var9 = var1.getItem(var8);
         if (!var9.isEmpty()) {
            if (SHAPE_INGREDIENT.test(var9)) {
               if (var5) {
                  return false;
               }

               var5 = true;
            } else if (TWINKLE_INGREDIENT.test(var9)) {
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

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      FireworkExplosion.Shape var3 = FireworkExplosion.Shape.SMALL_BALL;
      boolean var4 = false;
      boolean var5 = false;
      IntArrayList var6 = new IntArrayList();

      for(int var7 = 0; var7 < var1.size(); ++var7) {
         ItemStack var8 = var1.getItem(var7);
         if (!var8.isEmpty()) {
            if (SHAPE_INGREDIENT.test(var8)) {
               var3 = (FireworkExplosion.Shape)SHAPE_BY_ITEM.get(var8.getItem());
            } else if (TWINKLE_INGREDIENT.test(var8)) {
               var4 = true;
            } else if (TRAIL_INGREDIENT.test(var8)) {
               var5 = true;
            } else if (var8.getItem() instanceof DyeItem) {
               var6.add(((DyeItem)var8.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      ItemStack var9 = new ItemStack(Items.FIREWORK_STAR);
      var9.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(var3, var6, IntList.of(), var5, var4));
      return var9;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR;
   }

   static {
      SHAPE_INGREDIENT = Ingredient.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD, Items.PIGLIN_HEAD);
      TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
      TWINKLE_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
      SHAPE_BY_ITEM = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(Items.FIRE_CHARGE, FireworkExplosion.Shape.LARGE_BALL);
         var0.put(Items.FEATHER, FireworkExplosion.Shape.BURST);
         var0.put(Items.GOLD_NUGGET, FireworkExplosion.Shape.STAR);
         var0.put(Items.SKELETON_SKULL, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.WITHER_SKELETON_SKULL, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.CREEPER_HEAD, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.PLAYER_HEAD, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.DRAGON_HEAD, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.ZOMBIE_HEAD, FireworkExplosion.Shape.CREEPER);
         var0.put(Items.PIGLIN_HEAD, FireworkExplosion.Shape.CREEPER);
      });
      GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
   }
}
