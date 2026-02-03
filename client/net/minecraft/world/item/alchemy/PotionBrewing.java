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
import net.minecraft.world.level.ItemLike;

public class PotionBrewing {
   public static final int BREWING_TIME_SECONDS = 20;
   public static final PotionBrewing EMPTY = new PotionBrewing(List.of(), List.of(), List.of());
   private final List<Ingredient> containers;
   private final List<Mix<Potion>> potionMixes;
   private final List<Mix<Item>> containerMixes;

   private PotionBrewing(final List<Ingredient> containers, final List<Mix<Potion>> potionMixes, final List<Mix<Item>> containerMixes) {
      super();
      this.containers = containers;
      this.potionMixes = potionMixes;
      this.containerMixes = containerMixes;
   }

   public boolean isIngredient(final ItemStack ingredient) {
      return this.isContainerIngredient(ingredient) || this.isPotionIngredient(ingredient);
   }

   private boolean isContainer(final ItemStack input) {
      for(Ingredient allowedContainer : this.containers) {
         if (allowedContainer.test(input)) {
            return true;
         }
      }

      return false;
   }

   public boolean isContainerIngredient(final ItemStack ingredient) {
      for(Mix<Item> containerMix : this.containerMixes) {
         if (containerMix.ingredient.test(ingredient)) {
            return true;
         }
      }

      return false;
   }

   public boolean isPotionIngredient(final ItemStack ingredient) {
      for(Mix<Potion> potionMix : this.potionMixes) {
         if (potionMix.ingredient.test(ingredient)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBrewablePotion(final Holder<Potion> potion) {
      for(Mix<Potion> mix : this.potionMixes) {
         if (mix.to.is(potion)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMix(final ItemStack source, final ItemStack ingredient) {
      if (!this.isContainer(source)) {
         return false;
      } else {
         return this.hasContainerMix(source, ingredient) || this.hasPotionMix(source, ingredient);
      }
   }

   public boolean hasContainerMix(final ItemStack source, final ItemStack ingredient) {
      for(Mix<Item> mix : this.containerMixes) {
         if (source.is(mix.from) && mix.ingredient.test(ingredient)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasPotionMix(final ItemStack source, final ItemStack ingredient) {
      Optional<Holder<Potion>> potion = ((PotionContents)source.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
      if (potion.isEmpty()) {
         return false;
      } else {
         for(Mix<Potion> mix : this.potionMixes) {
            if (mix.from.is((Holder)potion.get()) && mix.ingredient.test(ingredient)) {
               return true;
            }
         }

         return false;
      }
   }

   public ItemStack mix(final ItemStack ingredient, final ItemStack source) {
      if (source.isEmpty()) {
         return source;
      } else {
         Optional<Holder<Potion>> potion = ((PotionContents)source.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
         if (potion.isEmpty()) {
            return source;
         } else {
            for(Mix<Item> mix : this.containerMixes) {
               if (source.is(mix.from) && mix.ingredient.test(ingredient)) {
                  return PotionContents.createItemStack((Item)mix.to.value(), (Holder)potion.get());
               }
            }

            for(Mix<Potion> mix : this.potionMixes) {
               if (mix.from.is((Holder)potion.get()) && mix.ingredient.test(ingredient)) {
                  return PotionContents.createItemStack(source.getItem(), mix.to);
               }
            }

            return source;
         }
      }
   }

   public static PotionBrewing bootstrap(final FeatureFlagSet enabledFeatures) {
      Builder builder = new Builder(enabledFeatures);
      addVanillaMixes(builder);
      return builder.build();
   }

   public static void addVanillaMixes(final Builder builder) {
      builder.addContainer(Items.POTION);
      builder.addContainer(Items.SPLASH_POTION);
      builder.addContainer(Items.LINGERING_POTION);
      builder.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      builder.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      builder.addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      builder.addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      builder.addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      builder.addStartMix(Items.BREEZE_ROD, Potions.WIND_CHARGED);
      builder.addStartMix(Items.SLIME_BLOCK, Potions.OOZING);
      builder.addStartMix(Items.STONE, Potions.INFESTED);
      builder.addStartMix(Items.COBWEB, Potions.WEAVING);
      builder.addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
      builder.addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
      builder.addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
      builder.addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
      builder.addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
      builder.addStartMix(Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
      builder.addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
      builder.addStartMix(Items.RABBIT_FOOT, Potions.LEAPING);
      builder.addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
      builder.addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
      builder.addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      builder.addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      builder.addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
      builder.addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
      builder.addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
      builder.addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
      builder.addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
      builder.addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      builder.addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      builder.addStartMix(Items.SUGAR, Potions.SWIFTNESS);
      builder.addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
      builder.addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
      builder.addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
      builder.addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
      builder.addStartMix(Items.GLISTERING_MELON_SLICE, Potions.HEALING);
      builder.addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
      builder.addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      builder.addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      builder.addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
      builder.addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      builder.addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      builder.addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      builder.addStartMix(Items.SPIDER_EYE, Potions.POISON);
      builder.addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
      builder.addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
      builder.addStartMix(Items.GHAST_TEAR, Potions.REGENERATION);
      builder.addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
      builder.addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
      builder.addStartMix(Items.BLAZE_POWDER, Potions.STRENGTH);
      builder.addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
      builder.addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
      builder.addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
      builder.addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
      builder.addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
      builder.addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
   }

   public static class Builder {
      private final List<Ingredient> containers = new ArrayList();
      private final List<Mix<Potion>> potionMixes = new ArrayList();
      private final List<Mix<Item>> containerMixes = new ArrayList();
      private final FeatureFlagSet enabledFeatures;

      public Builder(final FeatureFlagSet enabledFeatures) {
         super();
         this.enabledFeatures = enabledFeatures;
      }

      private static void expectPotion(final Item from) {
         if (!(from instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(BuiltInRegistries.ITEM.getKey(from)));
         }
      }

      public void addContainerRecipe(final Item from, final Item ingredient, final Item to) {
         if (from.isEnabled(this.enabledFeatures) && ingredient.isEnabled(this.enabledFeatures) && to.isEnabled(this.enabledFeatures)) {
            expectPotion(from);
            expectPotion(to);
            this.containerMixes.add(new Mix(from.builtInRegistryHolder(), Ingredient.of((ItemLike)ingredient), to.builtInRegistryHolder()));
         }
      }

      public void addContainer(final Item item) {
         if (item.isEnabled(this.enabledFeatures)) {
            expectPotion(item);
            this.containers.add(Ingredient.of((ItemLike)item));
         }
      }

      public void addMix(final Holder<Potion> from, final Item ingredient, final Holder<Potion> to) {
         if (((Potion)from.value()).isEnabled(this.enabledFeatures) && ingredient.isEnabled(this.enabledFeatures) && ((Potion)to.value()).isEnabled(this.enabledFeatures)) {
            this.potionMixes.add(new Mix(from, Ingredient.of((ItemLike)ingredient), to));
         }

      }

      public void addStartMix(final Item ingredient, final Holder<Potion> potion) {
         if (((Potion)potion.value()).isEnabled(this.enabledFeatures)) {
            this.addMix(Potions.WATER, ingredient, Potions.MUNDANE);
            this.addMix(Potions.AWKWARD, ingredient, potion);
         }

      }

      public PotionBrewing build() {
         return new PotionBrewing(List.copyOf(this.containers), List.copyOf(this.potionMixes), List.copyOf(this.containerMixes));
      }
   }

   private static record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {
      private Mix {
         super();
      }
   }
}
