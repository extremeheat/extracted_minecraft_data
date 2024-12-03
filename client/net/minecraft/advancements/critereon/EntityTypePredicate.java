package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record EntityTypePredicate(HolderSet<EntityType<?>> types) {
   public static final Codec<EntityTypePredicate> CODEC;

   public EntityTypePredicate(HolderSet<EntityType<?>> var1) {
      super();
      this.types = var1;
   }

   public static EntityTypePredicate of(HolderGetter<EntityType<?>> var0, EntityType<?> var1) {
      return new EntityTypePredicate(HolderSet.direct(var1.builtInRegistryHolder()));
   }

   public static EntityTypePredicate of(HolderGetter<EntityType<?>> var0, TagKey<EntityType<?>> var1) {
      return new EntityTypePredicate(var0.getOrThrow(var1));
   }

   public boolean matches(EntityType<?> var1) {
      return var1.is(this.types);
   }

   static {
      CODEC = RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).xmap(EntityTypePredicate::new, EntityTypePredicate::types);
   }
}
