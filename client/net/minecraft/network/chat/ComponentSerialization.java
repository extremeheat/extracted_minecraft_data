package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
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
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;

public class ComponentSerialization {
   public static final Codec<Component> CODEC = Codec.recursive("Component", ComponentSerialization::createCodec);
   public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> OPTIONAL_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Component> TRUSTED_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> TRUSTED_OPTIONAL_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, Component> TRUSTED_CONTEXT_FREE_STREAM_CODEC;
   public static final Codec<Component> FLAT_CODEC;

   public ComponentSerialization() {
      super();
   }

   public static Codec<Component> flatCodec(int var0) {
      final Codec var1 = Codec.string(0, var0);
      return new Codec<Component>() {
         public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> var1x, T var2) {
            DynamicOps var3 = asJsonOps(var1x);
            return var1.decode(var1x, var2).flatMap((var1xx) -> {
               try {
                  JsonElement var2 = JsonParser.parseString((String)var1xx.getFirst());
                  return ComponentSerialization.CODEC.parse(var3, var2).map((var1x) -> {
                     return Pair.of(var1x, var1xx.getSecond());
                  });
               } catch (JsonParseException var3x) {
                  Objects.requireNonNull(var3x);
                  return DataResult.error(var3x::getMessage);
               }
            });
         }

         public <T> DataResult<T> encode(Component var1x, DynamicOps<T> var2, T var3) {
            DynamicOps var4 = asJsonOps(var2);
            return ComponentSerialization.CODEC.encodeStart(var4, var1x).flatMap((var2x) -> {
               try {
                  return var1.encodeStart(var2, GsonHelper.toStableString(var2x));
               } catch (IllegalArgumentException var4) {
                  Objects.requireNonNull(var4);
                  return DataResult.error(var4::getMessage);
               }
            });
         }

         private static <T> DynamicOps<JsonElement> asJsonOps(DynamicOps<T> var0) {
            if (var0 instanceof RegistryOps var1x) {
               return var1x.withParent(JsonOps.INSTANCE);
            } else {
               return JsonOps.INSTANCE;
            }
         }

         // $FF: synthetic method
         public DataResult encode(Object var1x, DynamicOps var2, Object var3) {
            return this.encode((Component)var1x, var2, var3);
         }
      };
   }

   private static MutableComponent createFromList(List<Component> var0) {
      MutableComponent var1 = ((Component)var0.get(0)).copy();

      for(int var2 = 1; var2 < var0.size(); ++var2) {
         var1.append((Component)var0.get(var2));
      }

      return var1;
   }

   public static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(T[] var0, Function<T, MapCodec<? extends E>> var1, Function<E, T> var2, String var3) {
      FuzzyCodec var4 = new FuzzyCodec(Stream.of(var0).map(var1).toList(), (var2x) -> {
         return (MapEncoder)var1.apply((StringRepresentable)var2.apply(var2x));
      });
      Codec var5 = StringRepresentable.fromValues(() -> {
         return var0;
      });
      MapCodec var6 = var5.dispatchMap(var3, var2, var1);
      StrictEither var7 = new StrictEither(var3, var6, var4);
      return ExtraCodecs.orCompressed((MapCodec)var7, (MapCodec)var6);
   }

   private static Codec<Component> createCodec(Codec<Component> var0) {
      ComponentContents.Type[] var1 = new ComponentContents.Type[]{PlainTextContents.TYPE, TranslatableContents.TYPE, KeybindContents.TYPE, ScoreContents.TYPE, SelectorContents.TYPE, NbtContents.TYPE};
      MapCodec var2 = createLegacyComponentMatcher(var1, ComponentContents.Type::codec, ComponentContents::type, "type");
      Codec var3 = RecordCodecBuilder.create((var2x) -> {
         return var2x.group(var2.forGetter(Component::getContents), ExtraCodecs.nonEmptyList(var0.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::getSiblings), Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)).apply(var2x, MutableComponent::new);
      });
      return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(var0.listOf())), var3).xmap((var0x) -> {
         return (Component)var0x.map((var0) -> {
            return (Component)var0.map(Component::literal, ComponentSerialization::createFromList);
         }, (var0) -> {
            return var0;
         });
      }, (var0x) -> {
         String var1 = var0x.tryCollapseToString();
         return var1 != null ? Either.left(Either.left(var1)) : Either.right(var0x);
      });
   }

   static {
      STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
      OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
      TRUSTED_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);
      TRUSTED_OPTIONAL_STREAM_CODEC = TRUSTED_STREAM_CODEC.apply(ByteBufCodecs::optional);
      TRUSTED_CONTEXT_FREE_STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC);
      FLAT_CODEC = flatCodec(2147483647);
   }

   private static class FuzzyCodec<T> extends MapCodec<T> {
      private final List<MapCodec<? extends T>> codecs;
      private final Function<T, MapEncoder<? extends T>> encoderGetter;

      public FuzzyCodec(List<MapCodec<? extends T>> var1, Function<T, MapEncoder<? extends T>> var2) {
         super();
         this.codecs = var1;
         this.encoderGetter = var2;
      }

      public <S> DataResult<T> decode(DynamicOps<S> var1, MapLike<S> var2) {
         Iterator var3 = this.codecs.iterator();

         DataResult var5;
         do {
            if (!var3.hasNext()) {
               return DataResult.error(() -> {
                  return "No matching codec found";
               });
            }

            MapDecoder var4 = (MapDecoder)var3.next();
            var5 = var4.decode(var1, var2);
         } while(!var5.result().isPresent());

         return var5;
      }

      public <S> RecordBuilder<S> encode(T var1, DynamicOps<S> var2, RecordBuilder<S> var3) {
         MapEncoder var4 = (MapEncoder)this.encoderGetter.apply(var1);
         return var4.encode(var1, var2, var3);
      }

      public <S> Stream<S> keys(DynamicOps<S> var1) {
         return this.codecs.stream().flatMap((var1x) -> {
            return var1x.keys(var1);
         }).distinct();
      }

      public String toString() {
         return "FuzzyCodec[" + String.valueOf(this.codecs) + "]";
      }
   }

   private static class StrictEither<T> extends MapCodec<T> {
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
         return Stream.concat(this.typed.keys(var1), this.fuzzy.keys(var1)).distinct();
      }
   }
}
