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
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component extends Message, FormattedText {
   Style getStyle();

   String getContents();

   default String getString() {
      return FormattedText.super.getString();
   }

   default String getString(int var1) {
      StringBuilder var2 = new StringBuilder();
      this.visit((var2x) -> {
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

   MutableComponent plainCopy();

   MutableComponent copy();

   FormattedCharSequence getVisualOrderText();

   default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      Style var3 = this.getStyle().applyTo(var2);
      Optional var4 = this.visitSelf(var1, var3);
      if (var4.isPresent()) {
         return var4;
      } else {
         Iterator var5 = this.getSiblings().iterator();

         Optional var7;
         do {
            if (!var5.hasNext()) {
               return Optional.empty();
            }

            Component var6 = (Component)var5.next();
            var7 = var6.visit(var1, var3);
         } while(!var7.isPresent());

         return var7;
      }
   }

   default <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      Optional var2 = this.visitSelf(var1);
      if (var2.isPresent()) {
         return var2;
      } else {
         Iterator var3 = this.getSiblings().iterator();

         Optional var5;
         do {
            if (!var3.hasNext()) {
               return Optional.empty();
            }

            Component var4 = (Component)var3.next();
            var5 = var4.visit(var1);
         } while(!var5.isPresent());

         return var5;
      }
   }

   default <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2, this.getContents());
   }

   default <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.getContents());
   }

   static Component nullToEmpty(@Nullable String var0) {
      return (Component)(var0 != null ? new TextComponent(var0) : TextComponent.EMPTY);
   }

   public static class Serializer implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
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

      public MutableComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new TextComponent(var1.getAsString());
         } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var11 = var1.getAsJsonArray();
               MutableComponent var19 = null;
               Iterator var14 = var11.iterator();

               while(var14.hasNext()) {
                  JsonElement var17 = (JsonElement)var14.next();
                  MutableComponent var18 = this.deserialize(var17, var17.getClass(), var3);
                  if (var19 == null) {
                     var19 = var18;
                  } else {
                     var19.append((Component)var18);
                  }
               }

               return var19;
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
                     var5 = new TranslatableComponent(var6);
                  }
               } else if (var4.has("score")) {
                  JsonObject var12 = GsonHelper.getAsJsonObject(var4, "score");
                  if (!var12.has("name") || !var12.has("objective")) {
                     throw new JsonParseException("A score component needs a least a name and an objective");
                  }

                  var5 = new ScoreComponent(GsonHelper.getAsString(var12, "name"), GsonHelper.getAsString(var12, "objective"));
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
                  } else if (var4.has("entity")) {
                     var5 = new NbtComponent.EntityNbtComponent(var6, var7, GsonHelper.getAsString(var4, "entity"));
                  } else {
                     if (!var4.has("storage")) {
                        throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
                     }

                     var5 = new NbtComponent.StorageNbtComponent(var6, var7, new ResourceLocation(GsonHelper.getAsString(var4, "storage")));
                  }
               }
            }

            if (var4.has("extra")) {
               JsonArray var13 = GsonHelper.getAsJsonArray(var4, "extra");
               if (var13.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var16 = 0; var16 < var13.size(); ++var16) {
                  ((MutableComponent)var5).append((Component)this.deserialize(var13.get(var16), var2, var3));
               }
            }

            ((MutableComponent)var5).setStyle((Style)var3.deserialize(var1, Style.class));
            return (MutableComponent)var5;
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
            } else if (var1 instanceof NbtComponent.EntityNbtComponent) {
               NbtComponent.EntityNbtComponent var20 = (NbtComponent.EntityNbtComponent)var1;
               var4.addProperty("entity", var20.getSelector());
            } else {
               if (!(var1 instanceof NbtComponent.StorageNbtComponent)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
               }

               NbtComponent.StorageNbtComponent var21 = (NbtComponent.StorageNbtComponent)var1;
               var4.addProperty("storage", var21.getId().toString());
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
      public static MutableComponent fromJson(String var0) {
         return (MutableComponent)GsonHelper.fromJson(GSON, var0, MutableComponent.class, false);
      }

      @Nullable
      public static MutableComponent fromJson(JsonElement var0) {
         return (MutableComponent)GSON.fromJson(var0, MutableComponent.class);
      }

      @Nullable
      public static MutableComponent fromJsonLenient(String var0) {
         return (MutableComponent)GsonHelper.fromJson(GSON, var0, MutableComponent.class, true);
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
