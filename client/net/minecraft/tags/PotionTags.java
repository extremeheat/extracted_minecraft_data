package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.Potion;

public class PotionTags {
   public static final TagKey<Potion> TRADEABLE = create("tradeable");

   private PotionTags() {
      super();
   }

   private static TagKey<Potion> create(final String name) {
      return TagKey.<Potion>create(Registries.POTION, Identifier.withDefaultNamespace(name));
   }
}
