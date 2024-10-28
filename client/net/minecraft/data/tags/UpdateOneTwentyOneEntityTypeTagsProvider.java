package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyOneEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
   public UpdateOneTwentyOneEntityTypeTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.ENTITY_TYPE, var2, (var0) -> {
         return var0.builtInRegistryHolder().key();
      });
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add((Object)EntityType.BREEZE);
      this.tag(EntityTypeTags.DEFLECTS_PROJECTILES).add((Object)EntityType.BREEZE);
      this.tag(EntityTypeTags.CAN_TURN_IN_BOATS).add((Object)EntityType.BREEZE);
      this.tag(EntityTypeTags.IMPACT_PROJECTILES).add((Object[])(EntityType.WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE));
      this.tag(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE).add((Object[])(EntityType.BREEZE, EntityType.SKELETON, EntityType.BOGGED, EntityType.STRAY, EntityType.ZOMBIE, EntityType.HUSK, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SLIME));
      this.tag(EntityTypeTags.SKELETONS).add((Object)EntityType.BOGGED);
      this.tag(EntityTypeTags.IMMUNE_TO_INFESTED).add((Object)EntityType.SILVERFISH);
      this.tag(EntityTypeTags.IMMUNE_TO_OOZING).add((Object)EntityType.SLIME);
      this.tag(EntityTypeTags.REDIRECTABLE_PROJECTILE).add((Object[])(EntityType.WIND_CHARGE, EntityType.BREEZE_WIND_CHARGE));
   }
}
