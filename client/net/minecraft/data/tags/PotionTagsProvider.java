package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PotionTags;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class PotionTagsProvider extends HolderTagProvider<Potion> {
   public PotionTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, Registries.POTION, lookupProvider);
   }

   protected void addTags(final HolderLookup.Provider registries) {
      this.tag(PotionTags.TRADEABLE).add(Potions.WIND_CHARGED, Potions.OOZING, Potions.INFESTED, Potions.WEAVING, Potions.NIGHT_VISION, Potions.LONG_NIGHT_VISION, Potions.INVISIBILITY, Potions.LONG_INVISIBILITY, Potions.FIRE_RESISTANCE, Potions.LONG_FIRE_RESISTANCE, Potions.LEAPING, Potions.LONG_LEAPING, Potions.STRONG_LEAPING, Potions.SLOWNESS, Potions.LONG_SLOWNESS, Potions.STRONG_SLOWNESS, Potions.TURTLE_MASTER, Potions.LONG_TURTLE_MASTER, Potions.STRONG_TURTLE_MASTER, Potions.SWIFTNESS, Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS, Potions.WATER_BREATHING, Potions.LONG_WATER_BREATHING, Potions.HEALING, Potions.STRONG_HEALING, Potions.HARMING, Potions.STRONG_HARMING, Potions.POISON, Potions.LONG_POISON, Potions.STRONG_POISON, Potions.REGENERATION, Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION, Potions.STRENGTH, Potions.LONG_STRENGTH, Potions.STRONG_STRENGTH, Potions.WEAKNESS, Potions.LONG_WEAKNESS, Potions.SLOW_FALLING, Potions.LONG_SLOW_FALLING);
   }
}
