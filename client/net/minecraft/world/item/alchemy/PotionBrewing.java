package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;

public class PotionBrewing {
   public static final int BREWING_TIME_SECONDS = 20;
   private static final List<Mix<Potion>> POTION_MIXES = Lists.newArrayList();
   private static final List<Mix<Item>> CONTAINER_MIXES = Lists.newArrayList();
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

   public PotionBrewing() {
      super();
   }

   public static boolean isIngredient(ItemStack var0) {
      return isContainerIngredient(var0) || isPotionIngredient(var0);
   }

   protected static boolean isContainerIngredient(ItemStack var0) {
      Iterator var1 = CONTAINER_MIXES.iterator();

      Mix var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Mix)var1.next();
      } while(!var2.ingredient.test(var0));

      return true;
   }

   protected static boolean isPotionIngredient(ItemStack var0) {
      Iterator var1 = POTION_MIXES.iterator();

      Mix var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Mix)var1.next();
      } while(!var2.ingredient.test(var0));

      return true;
   }

   public static boolean isBrewablePotion(Holder<Potion> var0) {
      Iterator var1 = POTION_MIXES.iterator();

      Mix var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Mix)var1.next();
      } while(!var2.to.is(var0));

      return true;
   }

   public static boolean hasMix(ItemStack var0, ItemStack var1) {
      if (!ALLOWED_CONTAINER.test(var0)) {
         return false;
      } else {
         return hasContainerMix(var0, var1) || hasPotionMix(var0, var1);
      }
   }

   protected static boolean hasContainerMix(ItemStack var0, ItemStack var1) {
      Iterator var2 = CONTAINER_MIXES.iterator();

      Mix var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Mix)var2.next();
      } while(!var0.is(var3.from) || !var3.ingredient.test(var1));

      return true;
   }

   protected static boolean hasPotionMix(ItemStack var0, ItemStack var1) {
      Optional var2 = ((PotionContents)var0.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
      if (var2.isEmpty()) {
         return false;
      } else {
         Iterator var3 = POTION_MIXES.iterator();

         Mix var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (Mix)var3.next();
         } while(!var4.from.is((Holder)var2.get()) || !var4.ingredient.test(var1));

         return true;
      }
   }

   public static ItemStack mix(ItemStack var0, ItemStack var1) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         Optional var2 = ((PotionContents)var1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
         if (var2.isEmpty()) {
            return var1;
         } else {
            Iterator var3 = CONTAINER_MIXES.iterator();

            Mix var4;
            do {
               if (!var3.hasNext()) {
                  var3 = POTION_MIXES.iterator();

                  do {
                     if (!var3.hasNext()) {
                        return var1;
                     }

                     var4 = (Mix)var3.next();
                  } while(!var4.from.is((Holder)var2.get()) || !var4.ingredient.test(var0));

                  return PotionContents.createItemStack(var1.getItem(), var4.to);
               }

               var4 = (Mix)var3.next();
            } while(!var1.is(var4.from) || !var4.ingredient.test(var0));

            return PotionContents.createItemStack((Item)var4.to.value(), (Holder)var2.get());
         }
      }
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
      addMix(Potions.WATER, Items.BREEZE_ROD, Potions.MUNDANE);
      addMix(Potions.WATER, Items.SLIME_BLOCK, Potions.MUNDANE);
      addMix(Potions.WATER, Items.STONE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.COBWEB, Potions.MUNDANE);
      addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      addMix(Potions.AWKWARD, Items.BREEZE_ROD, Potions.WIND_CHARGED);
      addMix(Potions.AWKWARD, Items.SLIME_BLOCK, Potions.OOZING);
      addMix(Potions.AWKWARD, Items.STONE, Potions.INFESTED);
      addMix(Potions.AWKWARD, Items.COBWEB, Potions.WEAVING);
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
         throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(BuiltInRegistries.ITEM.getKey(var0)));
      } else if (!(var2 instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(BuiltInRegistries.ITEM.getKey(var2)));
      } else {
         CONTAINER_MIXES.add(new Mix(var0.builtInRegistryHolder(), Ingredient.of(var1), var2.builtInRegistryHolder()));
      }
   }

   private static void addContainer(Item var0) {
      if (!(var0 instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(BuiltInRegistries.ITEM.getKey(var0)));
      } else {
         ALLOWED_CONTAINERS.add(Ingredient.of(var0));
      }
   }

   private static void addMix(Holder<Potion> var0, Item var1, Holder<Potion> var2) {
      POTION_MIXES.add(new Mix(var0, Ingredient.of(var1), var2));
   }

   static record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {
      final Holder<T> from;
      final Ingredient ingredient;
      final Holder<T> to;

      Mix(Holder<T> var1, Ingredient var2, Holder<T> var3) {
         super();
         this.from = var1;
         this.ingredient = var2;
         this.to = var3;
      }

      public Holder<T> from() {
         return this.from;
      }

      public Ingredient ingredient() {
         return this.ingredient;
      }

      public Holder<T> to() {
         return this.to;
      }
   }
}
