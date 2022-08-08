package net.minecraft.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(DataGenerator var1) {
      super(var1, Registry.ENTITY_TYPE);
   }

   protected void addTags() {
      this.tag(EntityTypeTags.SKELETONS).add((Object[])(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON));
      this.tag(EntityTypeTags.RAIDERS).add((Object[])(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH));
      this.tag(EntityTypeTags.BEEHIVE_INHABITORS).add((Object)EntityType.BEE);
      this.tag(EntityTypeTags.ARROWS).add((Object[])(EntityType.ARROW, EntityType.SPECTRAL_ARROW));
      this.tag(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add((Object[])(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL));
      this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add((Object[])(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX));
      this.tag(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add((Object[])(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE));
      this.tag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add((Object[])(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN));
      this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add((Object[])(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER));
      this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add((Object[])(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE));
      this.tag(EntityTypeTags.FROG_FOOD).add((Object[])(EntityType.SLIME, EntityType.MAGMA_CUBE));
   }
}
