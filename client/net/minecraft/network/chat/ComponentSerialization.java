package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public class ComponentSerialization {
   public static final Codec<Component> CODEC = ExtraCodecs.recursive("Component", ComponentSerialization::createCodec);
   public static final Codec<Component> FLAT_CODEC = ExtraCodecs.FLAT_JSON
      .flatXmap(var0 -> CODEC.parse(JsonOps.INSTANCE, var0), var0 -> CODEC.encodeStart(JsonOps.INSTANCE, var0));

   public ComponentSerialization() {
      super();
   }

   private static MutableComponent createFromList(List<Component> var0) {
      MutableComponent var1 = ((Component)var0.get(0)).copy();

      for(int var2 = 1; var2 < var0.size(); ++var2) {
         var1.append((Component)var0.get(var2));
      }

      return var1;
   }

   public static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(
      T[] var0, Function<T, MapCodec<? extends E>> var1, Function<E, T> var2, String var3
   ) {
      ComponentSerialization.FuzzyCodec var4 = new ComponentSerialization.FuzzyCodec<>(
         Stream.of(var0).map(var1).toList(), var2x -> (MapEncoder<? extends Object>)var1.apply((StringRepresentable)var2.apply(var2x))
      );
      Codec var5 = StringRepresentable.fromValues(() -> var0);
      MapCodec var6 = var5.dispatchMap(var3, var2, var1x -> ((MapCodec)var1.apply(var1x)).codec());
      ComponentSerialization.StrictEither var7 = new ComponentSerialization.StrictEither(var3, var6, var4);
      return ExtraCodecs.orCompressed(var7, var6);
   }

   private static Codec<Component> createCodec(Codec<Component> var0) {
      ComponentContents.Type[] var1 = new ComponentContents.Type[]{
         PlainTextContents.TYPE, TranslatableContents.TYPE, KeybindContents.TYPE, ScoreContents.TYPE, SelectorContents.TYPE, NbtContents.TYPE
      };
      MapCodec var2 = createLegacyComponentMatcher(var1, ComponentContents.Type::codec, ComponentContents::type, "type");
      Codec var3 = RecordCodecBuilder.create(
         var2x -> var2x.group(
                  var2.forGetter(Component::getContents),
                  ExtraCodecs.strictOptionalField(ExtraCodecs.nonEmptyList(var0.listOf()), "extra", List.of()).forGetter(Component::getSiblings),
                  Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)
               )
               .apply(var2x, MutableComponent::new)
      );
      return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(var0.listOf())), var3)
         .xmap(
            var0x -> (Component)var0x.map(var0xx -> (Component)var0xx.map(Component::literal, ComponentSerialization::createFromList), var0xx -> var0xx),
            var0x -> {
               String var1xx = var0x.tryCollapseToString();
               return var1xx != null ? Either.left(Either.left(var1xx)) : Either.right(var0x);
            }
         );
   }

   static class FuzzyCodec<T> extends MapCodec<T> {
      private final List<MapCodec<? extends T>> codecs;
      private final Function<T, MapEncoder<? extends T>> encoderGetter;

      public FuzzyCodec(List<MapCodec<? extends T>> var1, Function<T, MapEncoder<? extends T>> var2) {
         super();
         this.codecs = var1;
         this.encoderGetter = var2;
      }

      public <S> DataResult<T> decode(DynamicOps<S> var1, MapLike<S> var2) {
         for(MapDecoder var4 : this.codecs) {
            DataResult var5 = var4.decode(var1, var2);
            if (var5.result().isPresent()) {
               return var5;
            }
         }

         return DataResult.error(() -> "No matching codec found");
      }

      public <S> RecordBuilder<S> encode(T var1, DynamicOps<S> var2, RecordBuilder<S> var3) {
         MapEncoder var4 = (MapEncoder)this.encoderGetter.apply((T)var1);
         return var4.encode(var1, var2, var3);
      }

      public <S> Stream<S> keys(DynamicOps<S> var1) {
         return this.codecs.stream().flatMap(var1x -> var1x.keys(var1)).distinct();
      }

      public String toString() {
         return "FuzzyCodec[" + this.codecs + "]";
      }
   }

   static class StrictEither<T> extends MapCodec<T> {
      private final String typeFieldName;
      private final MapCodec<T> typed;
      private final MapCodec<T> fuzzy;

      public StrictEither(String var1, MapCodec<T> var2, MapCodec<T> var3) {
         super();
         this.typeFieldName = var1;
         this.typed = var2;
         this.fuzzy = var3;
      }

      public <O> DataResult<T> decode(DynamicOps<O> var1, MapLike<O> var2) {
         return var2.get(this.typeFieldName) != null ? this.typed.decode(var1, var2) : this.fuzzy.decode(var1, var2);
      }

      public <O> RecordBuilder<O> encode(T var1, DynamicOps<O> var2, RecordBuilder<O> var3) {
         return this.fuzzy.encode(var1, var2, var3);
      }

      public <T1> Stream<T1> keys(DynamicOps<T1> var1) {
         return Stream.<T1>concat(this.typed.keys(var1), this.fuzzy.keys(var1)).distinct();
      }
   }
}
