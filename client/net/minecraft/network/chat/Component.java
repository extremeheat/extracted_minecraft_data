package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

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
      return MutableComponent.create(new LiteralContents(var0));
   }

   static MutableComponent translatable(String var0) {
      return MutableComponent.create(new TranslatableContents(var0, null, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatable(String var0, Object... var1) {
      return MutableComponent.create(new TranslatableContents(var0, null, var1));
   }

   static MutableComponent translatableWithFallback(String var0, @Nullable String var1) {
      return MutableComponent.create(new TranslatableContents(var0, var1, TranslatableContents.NO_ARGS));
   }

   static MutableComponent translatableWithFallback(String var0, @Nullable String var1, Object... var2) {
      return MutableComponent.create(new TranslatableContents(var0, var1, var2));
   }

   static MutableComponent empty() {
      return MutableComponent.create(ComponentContents.EMPTY);
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

   public static class Serializer implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder var0 = new GsonBuilder();
         var0.disableHtmlEscaping();
         var0.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
         var0.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         var0.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
         return var0.create();
      });
      private static final Field JSON_READER_POS = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field var0 = JsonReader.class.getDeclaredField("pos");
            var0.setAccessible(true);
            return var0;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
         }
      });
      private static final Field JSON_READER_LINESTART = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field var0 = JsonReader.class.getDeclaredField("lineStart");
            var0.setAccessible(true);
            return var0;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
         }
      });

      public Serializer() {
         super();
      }

      public MutableComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return Component.literal(var1.getAsString());
         } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var11 = var1.getAsJsonArray();
               MutableComponent var12 = null;

               for(JsonElement var21 : var11) {
                  MutableComponent var23 = this.deserialize(var21, var21.getClass(), var3);
                  if (var12 == null) {
                     var12 = var23;
                  } else {
                     var12.append(var23);
                  }
               }

               return var12;
            } else {
               throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
            }
         } else {
            JsonObject var4 = var1.getAsJsonObject();
            MutableComponent var5;
            if (var4.has("text")) {
               String var6 = GsonHelper.getAsString(var4, "text");
               var5 = var6.isEmpty() ? Component.empty() : Component.literal(var6);
            } else if (var4.has("translate")) {
               String var13 = GsonHelper.getAsString(var4, "translate");
               String var7 = GsonHelper.getAsString(var4, "fallback", null);
               if (var4.has("with")) {
                  JsonArray var8 = GsonHelper.getAsJsonArray(var4, "with");
                  Object[] var9 = new Object[var8.size()];

                  for(int var10 = 0; var10 < var9.length; ++var10) {
                     var9[var10] = unwrapTextArgument(this.deserialize(var8.get(var10), var2, var3));
                  }

                  var5 = Component.translatableWithFallback(var13, var7, var9);
               } else {
                  var5 = Component.translatableWithFallback(var13, var7);
               }
            } else if (var4.has("score")) {
               JsonObject var14 = GsonHelper.getAsJsonObject(var4, "score");
               if (!var14.has("name") || !var14.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var5 = Component.score(GsonHelper.getAsString(var14, "name"), GsonHelper.getAsString(var14, "objective"));
            } else if (var4.has("selector")) {
               Optional var15 = this.parseSeparator(var2, var3, var4);
               var5 = Component.selector(GsonHelper.getAsString(var4, "selector"), var15);
            } else if (var4.has("keybind")) {
               var5 = Component.keybind(GsonHelper.getAsString(var4, "keybind"));
            } else {
               if (!var4.has("nbt")) {
                  throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
               }

               String var16 = GsonHelper.getAsString(var4, "nbt");
               Optional var19 = this.parseSeparator(var2, var3, var4);
               boolean var22 = GsonHelper.getAsBoolean(var4, "interpret", false);
               Object var24;
               if (var4.has("block")) {
                  var24 = new BlockDataSource(GsonHelper.getAsString(var4, "block"));
               } else if (var4.has("entity")) {
                  var24 = new EntityDataSource(GsonHelper.getAsString(var4, "entity"));
               } else {
                  if (!var4.has("storage")) {
                     throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
                  }

                  var24 = new StorageDataSource(new ResourceLocation(GsonHelper.getAsString(var4, "storage")));
               }

               var5 = Component.nbt(var16, var22, var19, (DataSource)var24);
            }

            if (var4.has("extra")) {
               JsonArray var17 = GsonHelper.getAsJsonArray(var4, "extra");
               if (var17.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var20 = 0; var20 < var17.size(); ++var20) {
                  var5.append(this.deserialize(var17.get(var20), var2, var3));
               }
            }

            var5.setStyle((Style)var3.deserialize(var1, Style.class));
            return var5;
         }
      }

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private static Object unwrapTextArgument(Object var0) {
         if (var0 instanceof Component var1 && var1.getStyle().isEmpty() && var1.getSiblings().isEmpty()) {
            ComponentContents var2 = var1.getContents();
            if (var2 instanceof LiteralContents var3) {
               return var3.text();
            }
         }

         return var0;
      }

      private Optional<Component> parseSeparator(Type var1, JsonDeserializationContext var2, JsonObject var3) {
         return var3.has("separator") ? Optional.of(this.deserialize(var3.get("separator"), var1, var2)) : Optional.empty();
      }

      private void serializeStyle(Style var1, JsonObject var2, JsonSerializationContext var3) {
         JsonElement var4 = var3.serialize(var1);
         if (var4.isJsonObject()) {
            JsonObject var5 = (JsonObject)var4;

            for(Entry var7 : var5.entrySet()) {
               var2.add((String)var7.getKey(), (JsonElement)var7.getValue());
            }
         }
      }

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      public JsonElement serialize(Component var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (!var1.getStyle().isEmpty()) {
            this.serializeStyle(var1.getStyle(), var4, var3);
         }

         if (!var1.getSiblings().isEmpty()) {
            JsonArray var5 = new JsonArray();

            for(Component var7 : var1.getSiblings()) {
               var5.add(this.serialize(var7, Component.class, var3));
            }

            var4.add("extra", var5);
         }

         ComponentContents var18 = var1.getContents();
         if (var18 == ComponentContents.EMPTY) {
            var4.addProperty("text", "");
         } else if (var18 instanceof LiteralContents var20) {
            var4.addProperty("text", var20.text());
         } else if (var18 instanceof TranslatableContents var8) {
            var4.addProperty("translate", var8.getKey());
            String var12 = var8.getFallback();
            if (var12 != null) {
               var4.addProperty("fallback", var12);
            }

            if (var8.getArgs().length > 0) {
               JsonArray var13 = new JsonArray();

               for(Object var17 : var8.getArgs()) {
                  if (var17 instanceof Component) {
                     var13.add(this.serialize((Component)var17, var17.getClass(), var3));
                  } else {
                     var13.add(new JsonPrimitive(String.valueOf(var17)));
                  }
               }

               var4.add("with", var13);
            }
         } else if (var18 instanceof ScoreContents var9) {
            JsonObject var21 = new JsonObject();
            var21.addProperty("name", var9.getName());
            var21.addProperty("objective", var9.getObjective());
            var4.add("score", var21);
         } else if (var18 instanceof SelectorContents var10) {
            var4.addProperty("selector", var10.getPattern());
            this.serializeSeparator(var3, var4, var10.getSeparator());
         } else if (var18 instanceof KeybindContents var11) {
            var4.addProperty("keybind", var11.getName());
         } else {
            if (!(var18 instanceof NbtContents)) {
               throw new IllegalArgumentException("Don't know how to serialize " + var18 + " as a Component");
            }

            NbtContents var19 = (NbtContents)var18;
            var4.addProperty("nbt", var19.getNbtPath());
            var4.addProperty("interpret", var19.isInterpreting());
            this.serializeSeparator(var3, var4, var19.getSeparator());
            DataSource var22 = var19.getDataSource();
            if (var22 instanceof BlockDataSource var24) {
               var4.addProperty("block", var24.posPattern());
            } else if (var22 instanceof EntityDataSource var25) {
               var4.addProperty("entity", var25.selectorPattern());
            } else {
               if (!(var22 instanceof StorageDataSource)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + var18 + " as a Component");
               }

               StorageDataSource var23 = (StorageDataSource)var22;
               var4.addProperty("storage", var23.id().toString());
            }
         }

         return var4;
      }

      private void serializeSeparator(JsonSerializationContext var1, JsonObject var2, Optional<Component> var3) {
         var3.ifPresent(var3x -> var2.add("separator", this.serialize(var3x, var3x.getClass(), var1)));
      }

      public static String toJson(Component var0) {
         return GSON.toJson(var0);
      }

      public static String toStableJson(Component var0) {
         return GsonHelper.toStableString(toJsonTree(var0));
      }

      public static JsonElement toJsonTree(Component var0) {
         return GSON.toJsonTree(var0);
      }

      @Nullable
      public static MutableComponent fromJson(String var0) {
         return GsonHelper.fromNullableJson(GSON, var0, MutableComponent.class, false);
      }

      @Nullable
      public static MutableComponent fromJson(JsonElement var0) {
         return (MutableComponent)GSON.fromJson(var0, MutableComponent.class);
      }

      @Nullable
      public static MutableComponent fromJsonLenient(String var0) {
         return GsonHelper.fromNullableJson(GSON, var0, MutableComponent.class, true);
      }

      public static MutableComponent fromJson(com.mojang.brigadier.StringReader var0) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(var0.getRemaining()));
            var1.setLenient(false);
            MutableComponent var2 = (MutableComponent)GSON.getAdapter(MutableComponent.class).read(var1);
            var0.setCursor(var0.getCursor() + getPos(var1));
            return var2;
         } catch (StackOverflowError | IOException var3) {
            throw new JsonParseException(var3);
         }
      }

      private static int getPos(JsonReader var0) {
         try {
            return JSON_READER_POS.getInt(var0) - JSON_READER_LINESTART.getInt(var0) + 1;
         } catch (IllegalAccessException var2) {
            throw new IllegalStateException("Couldn't read position of JsonReader", var2);
         }
      }
   }
}
