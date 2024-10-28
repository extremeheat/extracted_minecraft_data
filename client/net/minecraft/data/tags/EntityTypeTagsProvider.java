package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.ENTITY_TYPE, var2, (var0) -> {
         return var0.builtInRegistryHolder().key();
      });
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(EntityTypeTags.SKELETONS).add((Object[])(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.SKELETON_HORSE, EntityType.BOGGED));
      this.tag(EntityTypeTags.ZOMBIES).add((Object[])(EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.DROWNED, EntityType.HUSK));
      this.tag(EntityTypeTags.RAIDERS).add((Object[])(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH));
      this.tag(EntityTypeTags.UNDEAD).addTag(EntityTypeTags.SKELETONS).addTag(EntityTypeTags.ZOMBIES).add((Object)EntityType.WITHER).add((Object)EntityType.PHANTOM);
      this.tag(EntityTypeTags.BEEHIVE_INHABITORS).add((Object)EntityType.BEE);
      this.tag(EntityTypeTags.ARROWS).add((Object[])(EntityType.ARROW, EntityType.SPECTRAL_ARROW));
      this.tag(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add((Object)EntityType.FIREWORK_ROCKET).add((Object[])(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL, EntityType.WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE));
      this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add((Object[])(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX));
      this.tag(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add((Object[])(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE));
      this.tag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add((Object[])(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN));
      this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add((Object[])(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER));
      this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add((Object[])(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE));
      this.tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).addTag(EntityTypeTags.UNDEAD).add((Object[])(EntityType.AXOLOTL, EntityType.FROG, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.TURTLE, EntityType.GLOW_SQUID, EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.SQUID, EntityType.TROPICAL_FISH, EntityType.TADPOLE, EntityType.ARMOR_STAND));
      this.tag(EntityTypeTags.FROG_FOOD).add((Object[])(EntityType.SLIME, EntityType.MAGMA_CUBE));
      this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add((Object[])(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER, EntityType.ALLAY, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.CAT, EntityType.CHICKEN, EntityType.GHAST, EntityType.PHANTOM, EntityType.MAGMA_CUBE, EntityType.OCELOT, EntityType.PARROT, EntityType.WITHER, EntityType.BREEZE));
      this.tag(EntityTypeTags.DISMOUNTS_UNDERWATER).add((Object[])(EntityType.CAMEL, EntityType.CHICKEN, EntityType.DONKEY, EntityType.HORSE, EntityType.LLAMA, EntityType.MULE, EntityType.PIG, EntityType.RAVAGER, EntityType.SPIDER, EntityType.STRIDER, EntityType.TRADER_LLAMA, EntityType.ZOMBIE_HORSE));
      this.tag(EntityTypeTags.NON_CONTROLLING_RIDER).add((Object[])(EntityType.SLIME, EntityType.MAGMA_CUBE));
      this.tag(EntityTypeTags.ILLAGER).add((Object)EntityType.EVOKER).add((Object)EntityType.ILLUSIONER).add((Object)EntityType.PILLAGER).add((Object)EntityType.VINDICATOR);
      this.tag(EntityTypeTags.AQUATIC).add((Object)EntityType.TURTLE).add((Object)EntityType.AXOLOTL).add((Object)EntityType.GUARDIAN).add((Object)EntityType.ELDER_GUARDIAN).add((Object)EntityType.COD).add((Object)EntityType.PUFFERFISH).add((Object)EntityType.SALMON).add((Object)EntityType.TROPICAL_FISH).add((Object)EntityType.DOLPHIN).add((Object)EntityType.SQUID).add((Object)EntityType.GLOW_SQUID).add((Object)EntityType.TADPOLE);
      this.tag(EntityTypeTags.ARTHROPOD).add((Object)EntityType.BEE).add((Object)EntityType.ENDERMITE).add((Object)EntityType.SILVERFISH).add((Object)EntityType.SPIDER).add((Object)EntityType.CAVE_SPIDER);
      this.tag(EntityTypeTags.IGNORES_POISON_AND_REGEN).addTag(EntityTypeTags.UNDEAD);
      this.tag(EntityTypeTags.INVERTED_HEALING_AND_HARM).addTag(EntityTypeTags.UNDEAD);
      this.tag(EntityTypeTags.WITHER_FRIENDS).addTag(EntityTypeTags.UNDEAD);
      this.tag(EntityTypeTags.ILLAGER_FRIENDS).addTag(EntityTypeTags.ILLAGER);
      this.tag(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH).add((Object)EntityType.TURTLE).add((Object)EntityType.GUARDIAN).add((Object)EntityType.ELDER_GUARDIAN).add((Object)EntityType.COD).add((Object)EntityType.PUFFERFISH).add((Object)EntityType.SALMON).add((Object)EntityType.TROPICAL_FISH).add((Object)EntityType.DOLPHIN).add((Object)EntityType.SQUID).add((Object)EntityType.GLOW_SQUID).add((Object)EntityType.TADPOLE);
      this.tag(EntityTypeTags.SENSITIVE_TO_IMPALING).addTag(EntityTypeTags.AQUATIC);
      this.tag(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS).addTag(EntityTypeTags.ARTHROPOD);
      this.tag(EntityTypeTags.SENSITIVE_TO_SMITE).addTag(EntityTypeTags.UNDEAD);
      this.tag(EntityTypeTags.REDIRECTABLE_PROJECTILE).add((Object[])(EntityType.FIREBALL, EntityType.WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE));
      this.tag(EntityTypeTags.DEFLECTS_PROJECTILES).add((Object)EntityType.BREEZE);
      this.tag(EntityTypeTags.CAN_TURN_IN_BOATS).add((Object)EntityType.BREEZE);
      this.tag(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE).add((Object[])(EntityType.BREEZE, EntityType.SKELETON, EntityType.BOGGED, EntityType.STRAY, EntityType.ZOMBIE, EntityType.HUSK, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SLIME));
      this.tag(EntityTypeTags.IMMUNE_TO_INFESTED).add((Object)EntityType.SILVERFISH);
      this.tag(EntityTypeTags.IMMUNE_TO_OOZING).add((Object)EntityType.SLIME);
   }
}
