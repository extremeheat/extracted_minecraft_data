package net.minecraft.world.level.storage.loot.providers.number.ints;

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

public sealed interface ResolvableInt {
   Codec<ResolvableInt> CODEC = Codec.either(ResolvableInt.Constant.CODEC, ResolvableInt.Reference.CODEC).xmap(Either::unwrap, ResolvableInt::wrap);
   StreamCodec<ByteBuf, ResolvableInt> STREAM_CODEC = ByteBufCodecs.either(ResolvableInt.Constant.STREAM_CODEC, ResolvableInt.Reference.STREAM_CODEC).map(Either::unwrap, ResolvableInt::wrap);

   private static Either<Constant, Reference> wrap(final ResolvableInt resolvableNumber) {
      Objects.requireNonNull(resolvableNumber);
      byte var2 = 0;
      Either var10000;
      //$FF: var2->value
      //0->net/minecraft/world/level/storage/loot/providers/number/ints/ResolvableInt$Constant
      //1->net/minecraft/world/level/storage/loot/providers/number/ints/ResolvableInt$Reference
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

   int get(LootContext context, int defaultValue);

   static ResolvableInt fromKey(final ResourceKey<ContextIntProvider> key) {
      return new Reference(key);
   }

   static <T> int getFromItem(final ItemStack itemStack, final DataComponentType<T> componentType, final Function<T, ResolvableInt> getter, final LootContext context, final int defaultValue) {
      T component = (T)itemStack.get(componentType);
      return component != null ? ((ResolvableInt)getter.apply(component)).get(context, defaultValue) : defaultValue;
   }

   public static record Constant(int value) implements ResolvableInt {
      private static final Codec<Constant> CODEC;
      private static final StreamCodec<ByteBuf, Constant> STREAM_CODEC;

      public Constant {
         super();
      }

      public int get(final LootContext context, final int defaultValue) {
         return this.value;
      }

      static {
         CODEC = Codec.INT.xmap(Constant::new, Constant::value);
         STREAM_CODEC = ByteBufCodecs.INT.map(Constant::new, Constant::value);
      }
   }

   public static record Reference(ResourceKey<ContextIntProvider> key) implements ResolvableInt {
      private static final Codec<Reference> CODEC;
      private static final StreamCodec<ByteBuf, Reference> STREAM_CODEC;

      public Reference {
         super();
      }

      public int get(final LootContext context, final int defaultValue) {
         return (Integer)this.getProvider(context).map((provider) -> provider.getInt(context)).orElse(defaultValue);
      }

      private Optional<ContextIntProvider> getProvider(final LootContext context) {
         return context.getResolver().lookupOrThrow(Registries.CONTEXT_INT_PROVIDER).get(this.key).map(Holder.Reference::value);
      }

      static {
         CODEC = ResourceKey.codec(Registries.CONTEXT_INT_PROVIDER).xmap(Reference::new, Reference::key);
         STREAM_CODEC = ResourceKey.streamCodec(Registries.CONTEXT_INT_PROVIDER).map(Reference::new, Reference::key);
      }
   }
}
