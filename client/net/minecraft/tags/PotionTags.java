package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.Potion;

public class PotionTags {
   public static final TagKey<Potion> TRADEABLE = create("tradeable");
   public static final TagKey<Potion> DOUSES_FIRE = create("douses_fire");
   public static final TagKey<Potion> HURTS_WATER_SENSITIVE_ENTITIES = create("hurts_water_sensitive_entities");
   public static final TagKey<Potion> EXTINGUISHES_ENTITIES = create("extinguishes_entities");
   public static final TagKey<Potion> REHYDRATES_AXOLOTLS = create("rehydrates_axolotls");

   private PotionTags() {
      super();
   }

   private static TagKey<Potion> create(final String name) {
      return TagKey.<Potion>create(Registries.POTION, Identifier.withDefaultNamespace(name));
   }
}
