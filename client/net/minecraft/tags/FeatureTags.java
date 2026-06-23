package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FeatureTags {
   public static final TagKey<Feature> CAN_SPAWN_FROM_BONE_MEAL = create("can_spawn_from_bone_meal");

   private FeatureTags() {
      super();
   }

   private static TagKey<Feature> create(final String name) {
      return TagKey.<Feature>create(Registries.FEATURE, Identifier.withDefaultNamespace(name));
   }
}
