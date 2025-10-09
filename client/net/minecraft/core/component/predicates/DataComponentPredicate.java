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
   StreamCodec<RegistryFriendlyByteBuf, Map<Type<?>, DataComponentPredicate>> STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map((var0) -> (Map)var0.stream().collect(Collectors.toMap(Single::type, Single::predicate)), (var0) -> var0.entrySet().stream().map(Single::fromEntry).toList());

   static MapCodec<Single<?>> singleCodec(String var0) {
      return DataComponentPredicate.Type.CODEC.dispatchMap(var0, Single::type, Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter var1);

   public interface Type<T extends DataComponentPredicate> {
      Codec<Type<?>> CODEC = Codec.either(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()).xmap(Type::copyOrCreateType, Type::unpackType);
      StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE), ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)).map(Type::copyOrCreateType, Type::unpackType);

      private static <T extends Type<?>> Either<T, DataComponentType<?>> unpackType(T var0) {
         Either var10000;
         if (var0 instanceof AnyValueType var1) {
            var10000 = Either.right(var1.componentType());
         } else {
            var10000 = Either.left(var0);
         }

         return var10000;
      }

      private static Type<?> copyOrCreateType(Either<Type<?>, DataComponentType<?>> var0) {
         return (Type)var0.map((var0x) -> var0x, AnyValueType::create);
      }

      Codec<T> codec();

      MapCodec<Single<T>> wrappedCodec();

      StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec();
   }

   public abstract static class TypeBase<T extends DataComponentPredicate> implements Type<T> {
      private final Codec<T> codec;
      private final MapCodec<Single<T>> wrappedCodec;
      private final StreamCodec<RegistryFriendlyByteBuf, Single<T>> singleStreamCodec;

      public TypeBase(Codec<T> var1) {
         super();
         this.codec = var1;
         this.wrappedCodec = DataComponentPredicate.Single.wrapCodec(this, var1);
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

   public static final class ConcreteType<T extends DataComponentPredicate> extends TypeBase<T> {
      public ConcreteType(Codec<T> var1) {
         super(var1);
      }
   }

   public static final class AnyValueType extends TypeBase<AnyValue> {
      private final AnyValue predicate;

      public AnyValueType(AnyValue var1) {
         super(Codec.unit(var1));
         this.predicate = var1;
      }

      public AnyValue predicate() {
         return this.predicate;
      }

      public DataComponentType<?> componentType() {
         return this.predicate.type();
      }

      public static AnyValueType create(DataComponentType<?> var0) {
         return new AnyValueType(new AnyValue(var0));
      }
   }

   public static record Single<T extends DataComponentPredicate>(Type<T> type, T predicate) {
      public Single(Type<T> var1, T var2) {
         super();
         this.type = var1;
         this.predicate = var2;
      }

      static <T extends DataComponentPredicate> MapCodec<Single<T>> wrapCodec(Type<T> var0, Codec<T> var1) {
         return RecordCodecBuilder.mapCodec((var2) -> var2.group(var1.fieldOf("value").forGetter(Single::predicate)).apply(var2, (var1x) -> new Single(var0, var1x)));
      }

      private static <T extends DataComponentPredicate> Single<T> fromEntry(Map.Entry<Type<?>, T> var0) {
         return new Single<T>((Type)var0.getKey(), (DataComponentPredicate)var0.getValue());
      }
   }
}
