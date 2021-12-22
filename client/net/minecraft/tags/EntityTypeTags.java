package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;

public final class EntityTypeTags {
   protected static final StaticTagHelper<EntityType<?>> HELPER;
   public static final Tag.Named<EntityType<?>> SKELETONS;
   public static final Tag.Named<EntityType<?>> RAIDERS;
   public static final Tag.Named<EntityType<?>> BEEHIVE_INHABITORS;
   public static final Tag.Named<EntityType<?>> ARROWS;
   public static final Tag.Named<EntityType<?>> IMPACT_PROJECTILES;
   public static final Tag.Named<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS;
   public static final Tag.Named<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES;
   public static final Tag.Named<EntityType<?>> AXOLOTL_HUNT_TARGETS;
   public static final Tag.Named<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES;
   public static final Tag.Named<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES;

   private EntityTypeTags() {
      super();
   }

   private static Tag.Named<EntityType<?>> bind(String var0) {
      return HELPER.bind(var0);
   }

   public static TagCollection<EntityType<?>> getAllTags() {
      return HELPER.getAllTags();
   }

   static {
      HELPER = StaticTags.create(Registry.ENTITY_TYPE_REGISTRY, "tags/entity_types");
      SKELETONS = bind("skeletons");
      RAIDERS = bind("raiders");
      BEEHIVE_INHABITORS = bind("beehive_inhabitors");
      ARROWS = bind("arrows");
      IMPACT_PROJECTILES = bind("impact_projectiles");
      POWDER_SNOW_WALKABLE_MOBS = bind("powder_snow_walkable_mobs");
      AXOLOTL_ALWAYS_HOSTILES = bind("axolotl_always_hostiles");
      AXOLOTL_HUNT_TARGETS = bind("axolotl_hunt_targets");
      FREEZE_IMMUNE_ENTITY_TYPES = bind("freeze_immune_entity_types");
      FREEZE_HURTS_EXTRA_TYPES = bind("freeze_hurts_extra_types");
   }
}
