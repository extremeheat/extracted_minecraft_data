package net.minecraft.world.item.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;

public class PotionBrewing {
   public static final int BREWING_TIME_SECONDS = 20;
   public static final PotionBrewing EMPTY = new PotionBrewing(List.of(), List.of(), List.of());
   private final List<Ingredient> containers;
   private final List<PotionBrewing.Mix<Potion>> potionMixes;
   private final List<PotionBrewing.Mix<Item>> containerMixes;

   PotionBrewing(List<Ingredient> var1, List<PotionBrewing.Mix<Potion>> var2, List<PotionBrewing.Mix<Item>> var3) {
      super();
      this.containers = var1;
      this.potionMixes = var2;
      this.containerMixes = var3;
   }

   public boolean isIngredient(ItemStack var1) {
      return this.isContainerIngredient(var1) || this.isPotionIngredient(var1);
   }

   private boolean isContainer(ItemStack var1) {
      for (Ingredient var3 : this.containers) {
         if (var3.test(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isContainerIngredient(ItemStack var1) {
      for (PotionBrewing.Mix var3 : this.containerMixes) {
         if (var3.ingredient.test(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPotionIngredient(ItemStack var1) {
      for (PotionBrewing.Mix var3 : this.potionMixes) {
         if (var3.ingredient.test(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBrewablePotion(Holder<Potion> var1) {
      for (PotionBrewing.Mix var3 : this.potionMixes) {
         if (var3.to.is(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMix(ItemStack var1, ItemStack var2) {
      return !this.isContainer(var1) ? false : this.hasContainerMix(var1, var2) || this.hasPotionMix(var1, var2);
   }

   public boolean hasContainerMix(ItemStack var1, ItemStack var2) {
      for (PotionBrewing.Mix var4 : this.containerMixes) {
         if (var1.is(var4.from) && var4.ingredient.test(var2)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasPotionMix(ItemStack var1, ItemStack var2) {
      Optional var3 = var1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
      if (var3.isEmpty()) {
         return false;
      } else {
         for (PotionBrewing.Mix var5 : this.potionMixes) {
            if (var5.from.is((Holder)var3.get()) && var5.ingredient.test(var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public ItemStack mix(ItemStack var1, ItemStack var2) {
      if (var2.isEmpty()) {
         return var2;
      } else {
         Optional var3 = var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
         if (var3.isEmpty()) {
            return var2;
         } else {
            for (PotionBrewing.Mix var5 : this.containerMixes) {
               if (var2.is(var5.from) && var5.ingredient.test(var1)) {
                  return PotionContents.createItemStack((Item)var5.to.value(), (Holder<Potion>)var3.get());
               }
            }

            for (PotionBrewing.Mix var7 : this.potionMixes) {
               if (var7.from.is((Holder)var3.get()) && var7.ingredient.test(var1)) {
                  return PotionContents.createItemStack(var2.getItem(), var7.to);
               }
            }

            return var2;
         }
      }
   }

   public static PotionBrewing bootstrap(FeatureFlagSet var0) {
      PotionBrewing.Builder var1 = new PotionBrewing.Builder(var0);
      addVanillaMixes(var1);
      return var1.build();
   }

   public static void addVanillaMixes(PotionBrewing.Builder var0) {
      var0.addContainer(Items.POTION);
      var0.addContainer(Items.SPLASH_POTION);
      var0.addContainer(Items.LINGERING_POTION);
      var0.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      var0.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      var0.addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      var0.addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      var0.addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      var0.addStartMix(Items.BREEZE_ROD, Potions.WIND_CHARGED);
      var0.addStartMix(Items.SLIME_BLOCK, Potions.OOZING);
      var0.addStartMix(Items.STONE, Potions.INFESTED);
      var0.addStartMix(Items.COBWEB, Potions.WEAVING);
      var0.addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
      var0.addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
      var0.addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
      var0.addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
      var0.addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
      var0.addStartMix(Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
      var0.addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
      var0.addStartMix(Items.RABBIT_FOOT, Potions.LEAPING);
      var0.addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
      var0.addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
      var0.addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      var0.addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      var0.addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
      var0.addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
      var0.addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
      var0.addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
      var0.addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
      var0.addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      var0.addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      var0.addStartMix(Items.SUGAR, Potions.SWIFTNESS);
      var0.addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
      var0.addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
      var0.addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
      var0.addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
      var0.addStartMix(Items.GLISTERING_MELON_SLICE, Potions.HEALING);
      var0.addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
      var0.addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      var0.addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      var0.addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
      var0.addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      var0.addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      var0.addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      var0.addStartMix(Items.SPIDER_EYE, Potions.POISON);
      var0.addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
      var0.addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
      var0.addStartMix(Items.GHAST_TEAR, Potions.REGENERATION);
      var0.addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
      var0.addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
      var0.addStartMix(Items.BLAZE_POWDER, Potions.STRENGTH);
      var0.addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
      var0.addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
      var0.addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
      var0.addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
      var0.addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
      var0.addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
   }

   public static class Builder {
      private final List<Ingredient> containers = new ArrayList<>();
      private final List<PotionBrewing.Mix<Potion>> potionMixes = new ArrayList<>();
      private final List<PotionBrewing.Mix<Item>> containerMixes = new ArrayList<>();
      private final FeatureFlagSet enabledFeatures;

      public Builder(FeatureFlagSet var1) {
         super();
         this.enabledFeatures = var1;
      }

      private static void expectPotion(Item var0) {
         if (!(var0 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(var0));
         }
      }

      public void addContainerRecipe(Item var1, Item var2, Item var3) {
         if (var1.isEnabled(this.enabledFeatures) && var2.isEnabled(this.enabledFeatures) && var3.isEnabled(this.enabledFeatures)) {
            expectPotion(var1);
            expectPotion(var3);
            this.containerMixes.add(new PotionBrewing.Mix<>(var1.builtInRegistryHolder(), Ingredient.of(var2), var3.builtInRegistryHolder()));
         }
      }

      public void addContainer(Item var1) {
         if (var1.isEnabled(this.enabledFeatures)) {
            expectPotion(var1);
            this.containers.add(Ingredient.of(var1));
         }
      }

      public void addMix(Holder<Potion> var1, Item var2, Holder<Potion> var3) {
         if (((Potion)var1.value()).isEnabled(this.enabledFeatures)
            && var2.isEnabled(this.enabledFeatures)
            && ((Potion)var3.value()).isEnabled(this.enabledFeatures)) {
            this.potionMixes.add(new PotionBrewing.Mix<>(var1, Ingredient.of(var2), var3));
         }
      }

      public void addStartMix(Item var1, Holder<Potion> var2) {
         if (((Potion)var2.value()).isEnabled(this.enabledFeatures)) {
            this.addMix(Potions.WATER, var1, Potions.MUNDANE);
            this.addMix(Potions.AWKWARD, var1, var2);
         }
      }

      public PotionBrewing build() {
         return new PotionBrewing(List.copyOf(this.containers), List.copyOf(this.potionMixes), List.copyOf(this.containerMixes));
      }
   }

   static record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {

      Mix(Holder<T> from, Ingredient ingredient, Holder<T> to) {
         super();
         this.from = from;
         this.ingredient = ingredient;
         this.to = to;
      }
   }
}
