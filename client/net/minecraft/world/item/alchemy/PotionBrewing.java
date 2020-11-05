package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PotionBrewing {
   private static final List<PotionBrewing.Mix<Potion>> POTION_MIXES = Lists.newArrayList();
   private static final List<PotionBrewing.Mix<Item>> CONTAINER_MIXES = Lists.newArrayList();
   private static final List<Ingredient> ALLOWED_CONTAINERS = Lists.newArrayList();
   private static final Predicate<ItemStack> ALLOWED_CONTAINER = (var0) -> {
      Iterator var1 = ALLOWED_CONTAINERS.iterator();

      Ingredient var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Ingredient)var1.next();
      } while(!var2.test(var0));

      return true;
   };

   public static boolean isIngredient(ItemStack var0) {
      return isContainerIngredient(var0) || isPotionIngredient(var0);
   }

   protected static boolean isContainerIngredient(ItemStack var0) {
      int var1 = 0;

      for(int var2 = CONTAINER_MIXES.size(); var1 < var2; ++var1) {
         if (((PotionBrewing.Mix)CONTAINER_MIXES.get(var1)).ingredient.test(var0)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean isPotionIngredient(ItemStack var0) {
      int var1 = 0;

      for(int var2 = POTION_MIXES.size(); var1 < var2; ++var1) {
         if (((PotionBrewing.Mix)POTION_MIXES.get(var1)).ingredient.test(var0)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBrewablePotion(Potion var0) {
      int var1 = 0;

      for(int var2 = POTION_MIXES.size(); var1 < var2; ++var1) {
         if (((PotionBrewing.Mix)POTION_MIXES.get(var1)).to == var0) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasMix(ItemStack var0, ItemStack var1) {
      if (!ALLOWED_CONTAINER.test(var0)) {
         return false;
      } else {
         return hasContainerMix(var0, var1) || hasPotionMix(var0, var1);
      }
   }

   protected static boolean hasContainerMix(ItemStack var0, ItemStack var1) {
      Item var2 = var0.getItem();
      int var3 = 0;

      for(int var4 = CONTAINER_MIXES.size(); var3 < var4; ++var3) {
         PotionBrewing.Mix var5 = (PotionBrewing.Mix)CONTAINER_MIXES.get(var3);
         if (var5.from == var2 && var5.ingredient.test(var1)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean hasPotionMix(ItemStack var0, ItemStack var1) {
      Potion var2 = PotionUtils.getPotion(var0);
      int var3 = 0;

      for(int var4 = POTION_MIXES.size(); var3 < var4; ++var3) {
         PotionBrewing.Mix var5 = (PotionBrewing.Mix)POTION_MIXES.get(var3);
         if (var5.from == var2 && var5.ingredient.test(var1)) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack mix(ItemStack var0, ItemStack var1) {
      if (!var1.isEmpty()) {
         Potion var2 = PotionUtils.getPotion(var1);
         Item var3 = var1.getItem();
         int var4 = 0;

         int var5;
         PotionBrewing.Mix var6;
         for(var5 = CONTAINER_MIXES.size(); var4 < var5; ++var4) {
            var6 = (PotionBrewing.Mix)CONTAINER_MIXES.get(var4);
            if (var6.from == var3 && var6.ingredient.test(var0)) {
               return PotionUtils.setPotion(new ItemStack((ItemLike)var6.to), var2);
            }
         }

         var4 = 0;

         for(var5 = POTION_MIXES.size(); var4 < var5; ++var4) {
            var6 = (PotionBrewing.Mix)POTION_MIXES.get(var4);
            if (var6.from == var2 && var6.ingredient.test(var0)) {
               return PotionUtils.setPotion(new ItemStack(var3), (Potion)var6.to);
            }
         }
      }

      return var1;
   }

   public static void bootStrap() {
      addContainer(Items.POTION);
      addContainer(Items.SPLASH_POTION);
      addContainer(Items.LINGERING_POTION);
      addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
      addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
      addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
      addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
      addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
      addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
      addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
      addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
      addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
      addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
      addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
      addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
      addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
      addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
      addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
      addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
      addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
      addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
      addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
      addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
      addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
      addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
      addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
      addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
      addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
      addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
      addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
      addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
      addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
      addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
      addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
      addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
      addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
      addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
      addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
      addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
      addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
      addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
      addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
      addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
      addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
   }

   private static void addContainerRecipe(Item var0, Item var1, Item var2) {
      if (!(var0 instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(var0));
      } else if (!(var2 instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(var2));
      } else {
         CONTAINER_MIXES.add(new PotionBrewing.Mix(var0, Ingredient.of(var1), var2));
      }
   }

   private static void addContainer(Item var0) {
      if (!(var0 instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(var0));
      } else {
         ALLOWED_CONTAINERS.add(Ingredient.of(var0));
      }
   }

   private static void addMix(Potion var0, Item var1, Potion var2) {
      POTION_MIXES.add(new PotionBrewing.Mix(var0, Ingredient.of(var1), var2));
   }

   static class Mix<T> {
      private final T from;
      private final Ingredient ingredient;
      private final T to;

      public Mix(T var1, Ingredient var2, T var3) {
         super();
         this.from = var1;
         this.ingredient = var2;
         this.to = var3;
      }
   }
}
