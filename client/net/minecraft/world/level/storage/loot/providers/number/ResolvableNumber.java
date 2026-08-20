package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public sealed interface ResolvableNumber {
   Codec<ResolvableNumber> CODEC = Codec.either(ResolvableNumber.Constant.CODEC, ResolvableNumber.Reference.CODEC).xmap(Either::unwrap, ResolvableNumber::wrap);
   StreamCodec<ByteBuf, ResolvableNumber> STREAM_CODEC = ByteBufCodecs.either(ResolvableNumber.Constant.STREAM_CODEC, ResolvableNumber.Reference.STREAM_CODEC).map(Either::unwrap, ResolvableNumber::wrap);

   private static Either<Constant, Reference> wrap(final ResolvableNumber resolvableNumber) {
      Objects.requireNonNull(resolvableNumber);
      byte var2 = 0;
      Either var10000;
      //$FF: var2->value
      //0->net/minecraft/world/level/storage/loot/providers/number/ResolvableNumber$Constant
      //1->net/minecraft/world/level/storage/loot/providers/number/ResolvableNumber$Reference
      switch (resolvableNumber.typeSwitch<invokedynamic>(resolvableNumber, var2)) {
         case 0:
            Constant constant = (Constant)resolvableNumber;
            var10000 = Either.left(constant);
            break;
         case 1:
            Reference reference = (Reference)resolvableNumber;
            var10000 = Either.right(reference);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   float getFloat(LootContext context, float defaultValue);

   int getInt(LootContext context, int defaultValue);

   static ResolvableNumber fromKey(final ResourceKey<NumberProvider> key) {
      return new Reference(key);
   }

   static <T> float getFloatFromItem(final ItemStack itemStack, final DataComponentType<T> componentType, final Function<T, ResolvableNumber> getter, final LootContext context, final float defaultValue) {
      T component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableNumber)getter.apply(component)).getFloat(context, defaultValue) : defaultValue;
   }

   static <T> int getIntFromItem(final ItemStack itemStack, final DataComponentType<T> componentType, final Function<T, ResolvableNumber> getter, final LootContext context, final int defaultValue) {
      T component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableNumber)getter.apply(component)).getInt(context, defaultValue) : defaultValue;
   }

   public static record Constant(float value) implements ResolvableNumber {
      private static final Codec<Constant> CODEC;
      private static final StreamCodec<ByteBuf, Constant> STREAM_CODEC;

      public Constant {
         super();
      }

      public float getFloat(final LootContext context, final float defaultValue) {
         return this.value;
      }

      public int getInt(final LootContext context, final int defaultValue) {
         return Math.round(this.value);
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         STREAM_CODEC = ByteBufCodecs.FLOAT.map(Constant::new, Constant::value);
      }
   }

   public static record Reference(ResourceKey<NumberProvider> key) implements ResolvableNumber {
      private static final Codec<Reference> CODEC;
      private static final StreamCodec<ByteBuf, Reference> STREAM_CODEC;

      public Reference {
         super();
      }

      public float getFloat(final LootContext context, final float defaultValue) {
         return (Float)this.getProvider(context).map((provider) -> provider.getFloat(context)).orElse(defaultValue);
      }

      public int getInt(final LootContext context, final int defaultValue) {
         return (Integer)this.getProvider(context).map((provider) -> provider.getInt(context)).orElse(defaultValue);
      }

      private Optional<NumberProvider> getProvider(final LootContext context) {
         return context.getResolver().lookupOrThrow(Registries.NUMBER_PROVIDER).get(this.key).map(Holder.Reference::value);
      }

      static {
         CODEC = ResourceKey.codec(Registries.NUMBER_PROVIDER).xmap(Reference::new, Reference::key);
         STREAM_CODEC = ResourceKey.streamCodec(Registries.NUMBER_PROVIDER).map(Reference::new, Reference::key);
      }
   }
}
