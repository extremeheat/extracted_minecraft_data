package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
   Codec<Map<Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, Single<?>> SINGLE_STREAM_CODEC = ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE).dispatch(Single::type, Type::singleStreamCodec);
   StreamCodec<RegistryFriendlyByteBuf, Map<Type<?>, DataComponentPredicate>> STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map((var0) -> (Map)var0.stream().collect(Collectors.toMap(Single::type, Single::predicate)), (var0) -> var0.entrySet().stream().map(Single::fromEntry).toList());

   static MapCodec<Single<?>> singleCodec(String var0) {
      return BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec().dispatchMap(var0, Single::type, Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter var1);

   public static final class Type<T extends DataComponentPredicate> {
      private final Codec<T> codec;
      private final MapCodec<Single<T>> wrappedCodec;
      private final StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec;

      public Type(Codec<T> var1) {
         super();
         this.codec = var1;
         this.wrappedCodec = RecordCodecBuilder.mapCodec((var2) -> var2.group(var1.fieldOf("value").forGetter(Single::predicate)).apply(var2, (var1x) -> new Single(this, var1x)));
         this.singleStreamCodec = ByteBufCodecs.fromCodecWithRegistries(var1).map((var1x) -> new Single(this, var1x), Single::predicate);
      }

      public Codec<T> codec() {
         return this.codec;
      }

      public MapCodec<Single<T>> wrappedCodec() {
         return this.wrappedCodec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec() {
         return this.singleStreamCodec;
      }
   }

   public static record Single<T extends DataComponentPredicate>(Type<T> type, T predicate) {
      public Single(Type<T> var1, T var2) {
         super();
         this.type = var1;
         this.predicate = var2;
      }

      private static <T extends DataComponentPredicate> Single<T> fromEntry(Map.Entry<Type<?>, T> var0) {
         return new Single<T>((Type)var0.getKey(), (DataComponentPredicate)var0.getValue());
      }
   }
}
