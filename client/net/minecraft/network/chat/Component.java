package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import com.mojang.serialization.JsonOps;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;

public interface Component extends Message, FormattedText {
   Style getStyle();

   ComponentContents getContents();

   @Override
   default String getString() {
      return FormattedText.super.getString();
   }

   default String getString(int var1) {
      StringBuilder var2 = new StringBuilder();
      this.visit(var2x -> {
         int var3 = var1 - var2.length();
         if (var3 <= 0) {
            return STOP_ITERATION;
         } else {
            var2.append(var2x.length() <= var3 ? var2x : var2x.substring(0, var3));
            return Optional.empty();
         }
      });
      return var2.toString();
   }

   List<Component> getSiblings();

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   default String tryCollapseToString() {
      ComponentContents var2 = this.getContents();
      if (var2 instanceof PlainTextContents var1 && this.getSiblings().isEmpty() && this.getStyle().isEmpty()) {
         return var1.text();
      }

      return null;
   }

   default MutableComponent plainCopy() {
      return MutableComponent.create(this.getContents());
   }

   default MutableComponent copy() {
      return new MutableComponent(this.getContents(), new ArrayList<>(this.getSiblings()), this.getStyle());
   }

   FormattedCharSequence getVisualOrderText();

   @Override
   default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      Style var3 = this.getStyle().applyTo(var2);
      Optional var4 = this.getContents().visit(var1, var3);
      if (var4.isPresent()) {
         return var4;
      } else {
         for(Component var6 : this.getSiblings()) {
            Optional var7 = var6.visit(var1, var3);
            if (var7.isPresent()) {
               return var7;
            }
         }

         return Optional.empty();
      }
   }

   @Override
   default <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      Optional var2 = this.getContents().visit(var1);
      if (var2.isPresent()) {
         return var2;
      } else {
         for(Component var4 : this.getSiblings()) {
            Optional var5 = var4.visit(var1);
            if (var5.isPresent()) {
               return var5;
            }
         }

         return Optional.empty();
      }
   }

   default List<Component> toFlatList() {
      return this.toFlatList(Style.EMPTY);
   }

   default List<Component> toFlatList(Style var1) {
      ArrayList var2 = Lists.newArrayList();
      this.visit((var1x, var2x) -> {
         if (!var2x.isEmpty()) {
            var2.add(literal(var2x).withStyle(var1x));
         }

         return Optional.empty();
      }, var1);
      return var2;
   }

   default boolean contains(Component var1) {
      if (this.equals(var1)) {
         return true;
      } else {
         List var2 = this.toFlatList();
         List var3 = var1.toFlatList(this.getStyle());
         return Collections.indexOfSubList(var2, var3) != -1;
      }
   }

   static Component nullToEmpty(@Nullable String var0) {
      return (Component)(var0 != null ? literal(var0) : CommonComponents.EMPTY);
   }

   static MutableComponent literal(String var0) {
      return MutableComponent.create(PlainTextContents.create(var0));
   }

   static MutableComponent translatable(String var0) {
      return MutableComponent.create(new TranslatableContents(var0, null, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatable(String var0, Object... var1) {
      return MutableComponent.create(new TranslatableContents(var0, null, var1));
   }

   static MutableComponent translatableEscape(String var0, Object... var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         Object var3 = var1[var2];
         if (!TranslatableContents.isAllowedPrimitiveArgument(var3) && !(var3 instanceof Component)) {
            var1[var2] = String.valueOf(var3);
         }
      }

      return translatable(var0, var1);
   }

   static MutableComponent translatableWithFallback(String var0, @Nullable String var1) {
      return MutableComponent.create(new TranslatableContents(var0, var1, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatableWithFallback(String var0, @Nullable String var1, Object... var2) {
      return MutableComponent.create(new TranslatableContents(var0, var1, var2));
   }

   static MutableComponent empty() {
      return MutableComponent.create(PlainTextContents.EMPTY);
   }

   static MutableComponent keybind(String var0) {
      return MutableComponent.create(new KeybindContents(var0));
   }

   static MutableComponent nbt(String var0, boolean var1, Optional<Component> var2, DataSource var3) {
      return MutableComponent.create(new NbtContents(var0, var1, var2, var3));
   }

   static MutableComponent score(String var0, String var1) {
      return MutableComponent.create(new ScoreContents(var0, var1));
   }

   static MutableComponent selector(String var0, Optional<Component> var1) {
      return MutableComponent.create(new SelectorContents(var0, var1));
   }

   static Component translationArg(Date var0) {
      return literal(var0.toString());
   }

   static Component translationArg(Message var0) {
      return (Component)(var0 instanceof Component var1 ? var1 : literal(var0.getString()));
   }

   static Component translationArg(UUID var0) {
      return literal(var0.toString());
   }

   static Component translationArg(ResourceLocation var0) {
      return literal(var0.toString());
   }

   static Component translationArg(ChunkPos var0) {
      return literal(var0.toString());
   }

   public static class Serializer {
      private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

      private Serializer() {
         super();
      }

      static MutableComponent deserialize(JsonElement var0, HolderLookup.Provider var1) {
         return Util.getOrThrow(ComponentSerialization.CODEC.parse(var1.createSerializationContext(JsonOps.INSTANCE), var0), JsonParseException::new);
      }

      static JsonElement serialize(Component var0, HolderLookup.Provider var1) {
         return Util.getOrThrow(ComponentSerialization.CODEC.encodeStart(var1.createSerializationContext(JsonOps.INSTANCE), var0), JsonParseException::new);
      }

      public static String toJson(Component var0, HolderLookup.Provider var1) {
         return GSON.toJson(serialize(var0, var1));
      }

      @Nullable
      public static MutableComponent fromJson(String var0, HolderLookup.Provider var1) {
         JsonElement var2 = JsonParser.parseString(var0);
         return var2 == null ? null : deserialize(var2, var1);
      }

      @Nullable
      public static MutableComponent fromJson(@Nullable JsonElement var0, HolderLookup.Provider var1) {
         return var0 == null ? null : deserialize(var0, var1);
      }

      @Nullable
      public static MutableComponent fromJsonLenient(String var0, HolderLookup.Provider var1) {
         JsonReader var2 = new JsonReader(new StringReader(var0));
         var2.setLenient(true);
         JsonElement var3 = JsonParser.parseReader(var2);
         return var3 == null ? null : deserialize(var3, var1);
      }
   }

   public static class SerializerAdapter implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
      private final HolderLookup.Provider registries;

      public SerializerAdapter(HolderLookup.Provider var1) {
         super();
         this.registries = var1;
      }

      public MutableComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return Component.Serializer.deserialize(var1, this.registries);
      }

      public JsonElement serialize(Component var1, Type var2, JsonSerializationContext var3) {
         return Component.Serializer.serialize(var1, this.registries);
      }
   }
}
