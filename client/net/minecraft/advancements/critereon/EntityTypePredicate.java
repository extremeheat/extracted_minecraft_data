package net.minecraft.advancements.critereon;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record EntityTypePredicate(HolderSet<EntityType<?>> b) {
   private final HolderSet<EntityType<?>> types;
   public static final Codec<EntityTypePredicate> CODEC = Codec.either(
         TagKey.hashedCodec(Registries.ENTITY_TYPE), BuiltInRegistries.ENTITY_TYPE.holderByNameCodec()
      )
      .flatComapMap(
         var0 -> (EntityTypePredicate)var0.map(
               var0x -> new EntityTypePredicate(BuiltInRegistries.ENTITY_TYPE.getOrCreateTag(var0x)),
               var0x -> new EntityTypePredicate(HolderSet.direct(var0x))
            ),
         var0 -> {
            HolderSet var1 = var0.types();
            Optional var2 = var1.unwrapKey();
            if (var2.isPresent()) {
               return DataResult.success(Either.left((TagKey)var2.get()));
            } else {
               return var1.size() == 1
                  ? DataResult.success(Either.right(var1.get(0)))
                  : DataResult.error(() -> "Entity type set must have a single element, but got " + var1.size());
            }
         }
      );

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
