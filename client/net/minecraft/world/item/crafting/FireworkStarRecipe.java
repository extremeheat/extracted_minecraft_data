package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe extends CustomRecipe {
   private static final Map<Item, FireworkExplosion.Shape> SHAPE_BY_ITEM = Map.of(
      Items.FIRE_CHARGE,
      FireworkExplosion.Shape.LARGE_BALL,
      Items.FEATHER,
      FireworkExplosion.Shape.BURST,
      Items.GOLD_NUGGET,
      FireworkExplosion.Shape.STAR,
      Items.SKELETON_SKULL,
      FireworkExplosion.Shape.CREEPER,
      Items.WITHER_SKELETON_SKULL,
      FireworkExplosion.Shape.CREEPER,
      Items.CREEPER_HEAD,
      FireworkExplosion.Shape.CREEPER,
      Items.PLAYER_HEAD,
      FireworkExplosion.Shape.CREEPER,
      Items.DRAGON_HEAD,
      FireworkExplosion.Shape.CREEPER,
      Items.ZOMBIE_HEAD,
      FireworkExplosion.Shape.CREEPER,
      Items.PIGLIN_HEAD,
      FireworkExplosion.Shape.CREEPER
   );
   private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
   private static final Ingredient TWINKLE_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

   public FireworkStarRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;

      for (int var8 = 0; var8 < var1.size(); var8++) {
         ItemStack var9 = var1.getItem(var8);
         if (!var9.isEmpty()) {
            if (SHAPE_BY_ITEM.containsKey(var9.getItem())) {
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

      for (int var7 = 0; var7 < var1.size(); var7++) {
         ItemStack var8 = var1.getItem(var7);
         if (!var8.isEmpty()) {
            FireworkExplosion.Shape var9 = SHAPE_BY_ITEM.get(var8.getItem());
            if (var9 != null) {
               var3 = var9;
            } else if (TWINKLE_INGREDIENT.test(var8)) {
               var4 = true;
            } else if (TRAIL_INGREDIENT.test(var8)) {
               var5 = true;
            } else if (var8.getItem() instanceof DyeItem) {
               var6.add(((DyeItem)var8.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      ItemStack var10 = new ItemStack(Items.FIREWORK_STAR);
      var10.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(var3, var6, IntList.of(), var5, var4));
      return var10;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR;
   }
}
