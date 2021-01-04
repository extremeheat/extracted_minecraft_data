package net.minecraft.network.chat;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component extends Message, Iterable<Component> {
   Component setStyle(Style var1);

   Style getStyle();

   default Component append(String var1) {
      return this.append((Component)(new TextComponent(var1)));
   }

   Component append(Component var1);

   String getContents();

   default String getString() {
      StringBuilder var1 = new StringBuilder();
      this.stream().forEach((var1x) -> {
         var1.append(var1x.getContents());
      });
      return var1.toString();
   }

   default String getString(int var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = this.stream().iterator();

      while(var3.hasNext()) {
         int var4 = var1 - var2.length();
         if (var4 <= 0) {
            break;
         }

         String var5 = ((Component)var3.next()).getContents();
         var2.append(var5.length() <= var4 ? var5 : var5.substring(0, var4));
      }

      return var2.toString();
   }

   default String getColoredString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = "";
      Iterator var3 = this.stream().iterator();

      while(var3.hasNext()) {
         Component var4 = (Component)var3.next();
         String var5 = var4.getContents();
         if (!var5.isEmpty()) {
            String var6 = var4.getStyle().getLegacyFormatCodes();
            if (!var6.equals(var2)) {
               if (!var2.isEmpty()) {
                  var1.append(ChatFormatting.RESET);
               }

               var1.append(var6);
               var2 = var6;
            }

            var1.append(var5);
         }
      }

      if (!var2.isEmpty()) {
         var1.append(ChatFormatting.RESET);
      }

      return var1.toString();
   }

   List<Component> getSiblings();

   Stream<Component> stream();

   default Stream<Component> flatStream() {
      return this.stream().map(Component::flattenStyle);
   }

   default Iterator<Component> iterator() {
      return this.flatStream().iterator();
   }

   Component copy();

   default Component deepCopy() {
      Component var1 = this.copy();
      var1.setStyle(this.getStyle().copy());
      Iterator var2 = this.getSiblings().iterator();

      while(var2.hasNext()) {
         Component var3 = (Component)var2.next();
         var1.append(var3.deepCopy());
      }

      return var1;
   }

   default Component withStyle(Consumer<Style> var1) {
      var1.accept(this.getStyle());
      return this;
   }

   default Component withStyle(ChatFormatting... var1) {
      ChatFormatting[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChatFormatting var5 = var2[var4];
         this.withStyle(var5);
      }

      return this;
   }

   default Component withStyle(ChatFormatting var1) {
      Style var2 = this.getStyle();
      if (var1.isColor()) {
         var2.setColor(var1);
      }

      if (var1.isFormat()) {
         switch(var1) {
         case OBFUSCATED:
            var2.setObfuscated(true);
            break;
         case BOLD:
            var2.setBold(true);
            break;
         case STRIKETHROUGH:
            var2.setStrikethrough(true);
            break;
         case UNDERLINE:
            var2.setUnderlined(true);
            break;
         case ITALIC:
            var2.setItalic(true);
         }
      }

      return this;
   }

   static Component flattenStyle(Component var0) {
      Component var1 = var0.copy();
      var1.setStyle(var0.getStyle().flatCopy());
      return var1;
   }

   public static class Serializer implements JsonDeserializer<Component>, JsonSerializer<Component> {
      private static final Gson GSON = (Gson)Util.make(() -> {
         GsonBuilder var0 = new GsonBuilder();
         var0.disableHtmlEscaping();
         var0.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
         var0.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         var0.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
         return var0.create();
      });
      private static final Field JSON_READER_POS = (Field)Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field var0 = JsonReader.class.getDeclaredField("pos");
            var0.setAccessible(true);
            return var0;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
         }
      });
      private static final Field JSON_READER_LINESTART = (Field)Util.make(() -> {
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

      public Component deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new TextComponent(var1.getAsString());
         } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var11 = var1.getAsJsonArray();
               Component var17 = null;
               Iterator var14 = var11.iterator();

               while(var14.hasNext()) {
                  JsonElement var18 = (JsonElement)var14.next();
                  Component var19 = this.deserialize(var18, var18.getClass(), var3);
                  if (var17 == null) {
                     var17 = var19;
                  } else {
                     var17.append(var19);
                  }
               }

               return var17;
            } else {
               throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
            }
         } else {
            JsonObject var4 = var1.getAsJsonObject();
            Object var5;
            if (var4.has("text")) {
               var5 = new TextComponent(GsonHelper.getAsString(var4, "text"));
            } else {
               String var6;
               if (var4.has("translate")) {
                  var6 = GsonHelper.getAsString(var4, "translate");
                  if (var4.has("with")) {
                     JsonArray var15 = GsonHelper.getAsJsonArray(var4, "with");
                     Object[] var8 = new Object[var15.size()];

                     for(int var9 = 0; var9 < var8.length; ++var9) {
                        var8[var9] = this.deserialize(var15.get(var9), var2, var3);
                        if (var8[var9] instanceof TextComponent) {
                           TextComponent var10 = (TextComponent)var8[var9];
                           if (var10.getStyle().isEmpty() && var10.getSiblings().isEmpty()) {
                              var8[var9] = var10.getText();
                           }
                        }
                     }

                     var5 = new TranslatableComponent(var6, var8);
                  } else {
                     var5 = new TranslatableComponent(var6, new Object[0]);
                  }
               } else if (var4.has("score")) {
                  JsonObject var12 = GsonHelper.getAsJsonObject(var4, "score");
                  if (!var12.has("name") || !var12.has("objective")) {
                     throw new JsonParseException("A score component needs a least a name and an objective");
                  }

                  var5 = new ScoreComponent(GsonHelper.getAsString(var12, "name"), GsonHelper.getAsString(var12, "objective"));
                  if (var12.has("value")) {
                     ((ScoreComponent)var5).setValue(GsonHelper.getAsString(var12, "value"));
                  }
               } else if (var4.has("selector")) {
                  var5 = new SelectorComponent(GsonHelper.getAsString(var4, "selector"));
               } else if (var4.has("keybind")) {
                  var5 = new KeybindComponent(GsonHelper.getAsString(var4, "keybind"));
               } else {
                  if (!var4.has("nbt")) {
                     throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
                  }

                  var6 = GsonHelper.getAsString(var4, "nbt");
                  boolean var7 = GsonHelper.getAsBoolean(var4, "interpret", false);
                  if (var4.has("block")) {
                     var5 = new NbtComponent.BlockNbtComponent(var6, var7, GsonHelper.getAsString(var4, "block"));
                  } else {
                     if (!var4.has("entity")) {
                        throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
                     }

                     var5 = new NbtComponent.EntityNbtComponent(var6, var7, GsonHelper.getAsString(var4, "entity"));
                  }
               }
            }

            if (var4.has("extra")) {
               JsonArray var13 = GsonHelper.getAsJsonArray(var4, "extra");
               if (var13.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var16 = 0; var16 < var13.size(); ++var16) {
                  ((Component)var5).append(this.deserialize(var13.get(var16), var2, var3));
               }
            }

            ((Component)var5).setStyle((Style)var3.deserialize(var1, Style.class));
            return (Component)var5;
         }
      }

      private void serializeStyle(Style var1, JsonObject var2, JsonSerializationContext var3) {
         JsonElement var4 = var3.serialize(var1);
         if (var4.isJsonObject()) {
            JsonObject var5 = (JsonObject)var4;
            Iterator var6 = var5.entrySet().iterator();

            while(var6.hasNext()) {
               Entry var7 = (Entry)var6.next();
               var2.add((String)var7.getKey(), (JsonElement)var7.getValue());
            }
         }

      }

      public JsonElement serialize(Component var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (!var1.getStyle().isEmpty()) {
            this.serializeStyle(var1.getStyle(), var4, var3);
         }

         if (!var1.getSiblings().isEmpty()) {
            JsonArray var5 = new JsonArray();
            Iterator var6 = var1.getSiblings().iterator();

            while(var6.hasNext()) {
               Component var7 = (Component)var6.next();
               var5.add(this.serialize((Component)var7, var7.getClass(), var3));
            }

            var4.add("extra", var5);
         }

         if (var1 instanceof TextComponent) {
            var4.addProperty("text", ((TextComponent)var1).getText());
         } else if (var1 instanceof TranslatableComponent) {
            TranslatableComponent var11 = (TranslatableComponent)var1;
            var4.addProperty("translate", var11.getKey());
            if (var11.getArgs() != null && var11.getArgs().length > 0) {
               JsonArray var14 = new JsonArray();
               Object[] var19 = var11.getArgs();
               int var8 = var19.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Object var10 = var19[var9];
                  if (var10 instanceof Component) {
                     var14.add(this.serialize((Component)((Component)var10), var10.getClass(), var3));
                  } else {
                     var14.add(new JsonPrimitive(String.valueOf(var10)));
                  }
               }

               var4.add("with", var14);
            }
         } else if (var1 instanceof ScoreComponent) {
            ScoreComponent var12 = (ScoreComponent)var1;
            JsonObject var16 = new JsonObject();
            var16.addProperty("name", var12.getName());
            var16.addProperty("objective", var12.getObjective());
            var16.addProperty("value", var12.getContents());
            var4.add("score", var16);
         } else if (var1 instanceof SelectorComponent) {
            SelectorComponent var13 = (SelectorComponent)var1;
            var4.addProperty("selector", var13.getPattern());
         } else if (var1 instanceof KeybindComponent) {
            KeybindComponent var15 = (KeybindComponent)var1;
            var4.addProperty("keybind", var15.getName());
         } else {
            if (!(var1 instanceof NbtComponent)) {
               throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
            }

            NbtComponent var17 = (NbtComponent)var1;
            var4.addProperty("nbt", var17.getNbtPath());
            var4.addProperty("interpret", var17.isInterpreting());
            if (var1 instanceof NbtComponent.BlockNbtComponent) {
               NbtComponent.BlockNbtComponent var18 = (NbtComponent.BlockNbtComponent)var1;
               var4.addProperty("block", var18.getPos());
            } else {
               if (!(var1 instanceof NbtComponent.EntityNbtComponent)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
               }

               NbtComponent.EntityNbtComponent var20 = (NbtComponent.EntityNbtComponent)var1;
               var4.addProperty("entity", var20.getSelector());
            }
         }

         return var4;
      }

      public static String toJson(Component var0) {
         return GSON.toJson(var0);
      }

      public static JsonElement toJsonTree(Component var0) {
         return GSON.toJsonTree(var0);
      }

      @Nullable
      public static Component fromJson(String var0) {
         return (Component)GsonHelper.fromJson(GSON, var0, Component.class, false);
      }

      @Nullable
      public static Component fromJson(JsonElement var0) {
         return (Component)GSON.fromJson(var0, Component.class);
      }

      @Nullable
      public static Component fromJsonLenient(String var0) {
         return (Component)GsonHelper.fromJson(GSON, var0, Component.class, true);
      }

      public static Component fromJson(com.mojang.brigadier.StringReader var0) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(var0.getRemaining()));
            var1.setLenient(false);
            Component var2 = (Component)GSON.getAdapter(Component.class).read(var1);
            var0.setCursor(var0.getCursor() + getPos(var1));
            return var2;
         } catch (IOException var3) {
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

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((Component)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
