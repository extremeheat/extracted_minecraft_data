package net.minecraft.world.entity.animal;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;

public interface AgeableSoundVariant<T> {
   static <T, S extends AgeableSoundVariant<T>> Codec<S> codec(final Codec<T> elementCodec, final Factory<T, S> factory) {
      return RecordCodecBuilder.create((i) -> {
         Products.P2 var10000 = i.group(elementCodec.fieldOf("adult_sounds").forGetter(AgeableSoundVariant::adultSounds), elementCodec.fieldOf("baby_sounds").forGetter(AgeableSoundVariant::babySounds));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   T adultSounds();

   T babySounds();

   @FunctionalInterface
   public interface Factory<T, S extends AgeableSoundVariant<T>> {
      S create(T adultSounds, T babySounds);
   }
}
