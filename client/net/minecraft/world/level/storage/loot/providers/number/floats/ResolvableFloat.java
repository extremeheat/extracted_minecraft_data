package net.minecraft.world.level.storage.loot.providers.number.floats;

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

public sealed interface ResolvableFloat {
   Codec<ResolvableFloat> CODEC = Codec.either(ResolvableFloat.Constant.CODEC, ResolvableFloat.Reference.CODEC).xmap(Either::unwrap, ResolvableFloat::wrap);
   StreamCodec<ByteBuf, ResolvableFloat> STREAM_CODEC = ByteBufCodecs.either(ResolvableFloat.Constant.STREAM_CODEC, ResolvableFloat.Reference.STREAM_CODEC).map(Either::unwrap, ResolvableFloat::wrap);

   private static Either<Constant, Reference> wrap(final ResolvableFloat resolvableNumber) {
      Objects.requireNonNull(resolvableNumber);
      byte var2 = 0;
      Either var10000;
      //$FF: var2->value
      //0->net/minecraft/world/level/storage/loot/providers/number/floats/ResolvableFloat$Constant
      //1->net/minecraft/world/level/storage/loot/providers/number/floats/ResolvableFloat$Reference
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

   float get(LootContext context, float defaultValue);

   static ResolvableFloat fromKey(final ResourceKey<ContextFloatProvider> key) {
      return new Reference(key);
   }

   static <T> float getFromItem(final ItemStack itemStack, final DataComponentType<T> componentType, final Function<T, ResolvableFloat> getter, final LootContext context, final float defaultValue) {
      T component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableFloat)getter.apply(component)).get(context, defaultValue) : defaultValue;
   }

   public static record Constant(float value) implements ResolvableFloat {
      private static final Codec<Constant> CODEC;
      private static final StreamCodec<ByteBuf, Constant> STREAM_CODEC;

      public Constant {
         super();
      }

      public float get(final LootContext context, final float defaultValue) {
         return this.value;
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         STREAM_CODEC = ByteBufCodecs.FLOAT.map(Constant::new, Constant::value);
      }
   }

   public static record Reference(ResourceKey<ContextFloatProvider> key) implements ResolvableFloat {
      private static final Codec<Reference> CODEC;
      private static final StreamCodec<ByteBuf, Reference> STREAM_CODEC;

      public Reference {
         super();
      }

      public float get(final LootContext context, final float defaultValue) {
         return (Float)this.getProvider(context).map((provider) -> provider.getFloat(context)).orElse(defaultValue);
      }

      private Optional<ContextFloatProvider> getProvider(final LootContext context) {
         return context.getResolver().lookupOrThrow(Registries.CONTEXT_FLOAT_PROVIDER).get(this.key).map(Holder.Reference::value);
      }

      static {
         CODEC = ResourceKey.codec(Registries.CONTEXT_FLOAT_PROVIDER).xmap(Reference::new, Reference::key);
         STREAM_CODEC = ResourceKey.streamCodec(Registries.CONTEXT_FLOAT_PROVIDER).map(Reference::new, Reference::key);
      }
   }
}
