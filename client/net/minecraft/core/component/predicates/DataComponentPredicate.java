package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;

public interface DataComponentPredicate {
   Codec<Map<Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), Type::codec);

   static MapCodec<Single<?>> singleCodec(String var0) {
      return BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec().dispatchMap(var0, Single::type, Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter var1);

   public static final class Type<T extends DataComponentPredicate> {
      private final Codec<T> codec;
      private final MapCodec<Single<T>> wrappedCodec;

      public Type(Codec<T> var1) {
         super();
         this.codec = var1;
         this.wrappedCodec = RecordCodecBuilder.mapCodec((var2) -> var2.group(var1.fieldOf("value").forGetter(Single::predicate)).apply(var2, (var1x) -> new Single(this, var1x)));
      }

      public Codec<T> codec() {
         return this.codec;
      }

      public MapCodec<Single<T>> wrappedCodec() {
         return this.wrappedCodec;
      }
   }

   public static record Single<T extends DataComponentPredicate>(Type<T> type, T predicate) {
      public Single(Type<T> var1, T var2) {
         super();
         this.type = var1;
         this.predicate = var2;
      }
   }
}
