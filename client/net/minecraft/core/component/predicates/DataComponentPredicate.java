package net.minecraft.core.component.predicates;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
   Codec<Map<Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(DataComponentPredicate.Type.CODEC, Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, Single<?>> SINGLE_STREAM_CODEC = DataComponentPredicate.Type.STREAM_CODEC.dispatch(Single::type, Type::singleStreamCodec);
   StreamCodec<RegistryFriendlyByteBuf, Map<Type<?>, DataComponentPredicate>> STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map((singles) -> (Map)singles.stream().collect(Collectors.toMap(Single::type, Single::predicate)), (map) -> map.entrySet().stream().map(Single::fromEntry).toList());

   static MapCodec<Single<?>> singleCodec(final String name) {
      return DataComponentPredicate.Type.CODEC.dispatchMap(name, Single::type, Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter components);

   public interface Type<T extends DataComponentPredicate> {
      Codec<Type<?>> CODEC = Codec.either(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()).xmap(Type::copyOrCreateType, Type::unpackType);
      StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE), ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)).map(Type::copyOrCreateType, Type::unpackType);

      private static <T extends Type<?>> Either<T, DataComponentType<?>> unpackType(final T type) {
         Either var10000;
         if (type instanceof AnyValueType anyCheck) {
            var10000 = Either.right(anyCheck.componentType());
         } else {
            var10000 = Either.left(type);
         }

         return var10000;
      }

      private static Type<?> copyOrCreateType(final Either<Type<?>, DataComponentType<?>> concreteTypeOrComponent) {
         return (Type)concreteTypeOrComponent.map((concrete) -> concrete, AnyValueType::create);
      }

      Codec<T> codec();

      MapCodec<Single<T>> wrappedCodec();

      StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec();
   }

   public abstract static class TypeBase<T extends DataComponentPredicate> implements Type<T> {
      private final Codec<T> codec;
      private final MapCodec<Single<T>> wrappedCodec;
      private final StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec;

      public TypeBase(final Codec<T> codec) {
         super();
         this.codec = codec;
         this.wrappedCodec = DataComponentPredicate.Single.wrapCodec(this, codec);
         this.singleStreamCodec = ByteBufCodecs.fromCodecWithRegistries(codec).map((v) -> new Single(this, v), Single::predicate);
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

   public static final class ConcreteType<T extends DataComponentPredicate> extends TypeBase<T> {
      public ConcreteType(final Codec<T> codec) {
         super(codec);
      }
   }

   public static final class AnyValueType extends TypeBase<AnyValue> {
      private final AnyValue predicate;

      public AnyValueType(final AnyValue predicate) {
         super(MapCodec.unitCodec(predicate));
         this.predicate = predicate;
      }

      public AnyValue predicate() {
         return this.predicate;
      }

      public DataComponentType<?> componentType() {
         return this.predicate.type();
      }

      public static AnyValueType create(final DataComponentType<?> componentType) {
         return new AnyValueType(new AnyValue(componentType));
      }
   }

   public static record Single<T extends DataComponentPredicate>(Type<T> type, T predicate) {
      public Single {
         super();
      }

      private static <T extends DataComponentPredicate> MapCodec<Single<T>> wrapCodec(final Type<T> type, final Codec<T> codec) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(codec.fieldOf("value").forGetter(Single::predicate)).apply(i, (predicate) -> new Single(type, predicate)));
      }

      private static <T extends DataComponentPredicate> Single<T> fromEntry(final Map.Entry<Type<?>, T> e) {
         return new Single<T>((Type)e.getKey(), (DataComponentPredicate)e.getValue());
      }
   }
}
