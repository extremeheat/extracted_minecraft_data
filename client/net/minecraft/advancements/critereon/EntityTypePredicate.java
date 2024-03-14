package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record EntityTypePredicate(HolderSet<EntityType<?>> b) {
   private final HolderSet<EntityType<?>> types;
   public static final Codec<EntityTypePredicate> CODEC = RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE)
      .xmap(EntityTypePredicate::new, EntityTypePredicate::types);

   public EntityTypePredicate(HolderSet<EntityType<?>> var1) {
      super();
      this.types = var1;
   }

   public static EntityTypePredicate of(EntityType<?> var0) {
      return new EntityTypePredicate(HolderSet.direct(var0.builtInRegistryHolder()));
   }

   public static EntityTypePredicate of(TagKey<EntityType<?>> var0) {
      return new EntityTypePredicate(BuiltInRegistries.ENTITY_TYPE.getOrCreateTag(var0));
   }

   public boolean matches(EntityType<?> var1) {
      return var1.is(this.types);
   }
}
