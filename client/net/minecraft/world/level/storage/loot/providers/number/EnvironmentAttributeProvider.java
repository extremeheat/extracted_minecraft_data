package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public interface EnvironmentAttributeProvider extends LootContextUser {
   static <Self extends EnvironmentAttributeProvider> MapCodec<Self> mapCodec(final Codec<EnvironmentAttribute<?>> attributeCodec, final Factory<Self> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P1 var10000 = i.group(attributeCodec.fieldOf("attribute").forGetter(EnvironmentAttributeProvider::attribute));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   EnvironmentAttribute<?> attribute();

   default Set<ContextKey<?>> getReferencedContextParams() {
      return this.attribute().isPositional() ? Set.of(LootContextParams.ORIGIN) : Set.of();
   }

   @FunctionalInterface
   public interface Factory<Self extends EnvironmentAttributeProvider> {
      Self create(EnvironmentAttribute<?> attribute);
   }
}
