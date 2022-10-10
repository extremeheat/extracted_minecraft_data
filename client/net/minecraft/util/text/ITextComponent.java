package net.minecraft.util.text;

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
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Util;

public interface ITextComponent extends Message, Iterable<ITextComponent> {
   ITextComponent func_150255_a(Style var1);

   Style func_150256_b();

   default ITextComponent func_150258_a(String var1) {
      return this.func_150257_a(new TextComponentString(var1));
   }

   ITextComponent func_150257_a(ITextComponent var1);

   String func_150261_e();

   default String getString() {
      StringBuilder var1 = new StringBuilder();
      this.func_212640_c().forEach((var1x) -> {
         var1.append(var1x.func_150261_e());
      });
      return var1.toString();
   }

   default String func_212636_a(int var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = this.func_212640_c().iterator();

      while(var3.hasNext()) {
         int var4 = var1 - var2.length();
         if (var4 <= 0) {
            break;
         }

         String var5 = ((ITextComponent)var3.next()).func_150261_e();
         var2.append(var5.length() <= var4 ? var5 : var5.substring(0, var4));
      }

      return var2.toString();
   }

   default String func_150254_d() {
      StringBuilder var1 = new StringBuilder();
      String var2 = "";
      Iterator var3 = this.func_212640_c().iterator();

      while(var3.hasNext()) {
         ITextComponent var4 = (ITextComponent)var3.next();
         String var5 = var4.func_150261_e();
         if (!var5.isEmpty()) {
            String var6 = var4.func_150256_b().func_150218_j();
            if (!var6.equals(var2)) {
               if (!var2.isEmpty()) {
                  var1.append(TextFormatting.RESET);
               }

               var1.append(var6);
               var2 = var6;
            }

            var1.append(var5);
         }
      }

      if (!var2.isEmpty()) {
         var1.append(TextFormatting.RESET);
      }

      return var1.toString();
   }

   List<ITextComponent> func_150253_a();

   Stream<ITextComponent> func_212640_c();

   default Stream<ITextComponent> func_212637_f() {
      return this.func_212640_c().map(ITextComponent::func_212639_b);
   }

   default Iterator<ITextComponent> iterator() {
      return this.func_212637_f().iterator();
   }

   ITextComponent func_150259_f();

   default ITextComponent func_212638_h() {
      ITextComponent var1 = this.func_150259_f();
      var1.func_150255_a(this.func_150256_b().func_150232_l());
      Iterator var2 = this.func_150253_a().iterator();

      while(var2.hasNext()) {
         ITextComponent var3 = (ITextComponent)var2.next();
         var1.func_150257_a(var3.func_212638_h());
      }

      return var1;
   }

   default ITextComponent func_211710_a(Consumer<Style> var1) {
      var1.accept(this.func_150256_b());
      return this;
   }

   default ITextComponent func_211709_a(TextFormatting... var1) {
      TextFormatting[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TextFormatting var5 = var2[var4];
         this.func_211708_a(var5);
      }

      return this;
   }

   default ITextComponent func_211708_a(TextFormatting var1) {
      Style var2 = this.func_150256_b();
      if (var1.func_96302_c()) {
         var2.func_150238_a(var1);
      }

      if (var1.func_96301_b()) {
         switch(var1) {
         case OBFUSCATED:
            var2.func_150237_e(true);
            break;
         case BOLD:
            var2.func_150227_a(true);
            break;
         case STRIKETHROUGH:
            var2.func_150225_c(true);
            break;
         case UNDERLINE:
            var2.func_150228_d(true);
            break;
         case ITALIC:
            var2.func_150217_b(true);
         }
      }

      return this;
   }

   static ITextComponent func_212639_b(ITextComponent var0) {
      ITextComponent var1 = var0.func_150259_f();
      var1.func_150255_a(var0.func_150256_b().func_150206_m());
      return var1;
   }

   public static class Serializer implements JsonDeserializer<ITextComponent>, JsonSerializer<ITextComponent> {
      private static final Gson field_150700_a = (Gson)Util.func_199748_a(() -> {
         GsonBuilder var0 = new GsonBuilder();
         var0.registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer());
         var0.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         var0.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         return var0.create();
      });
      private static final Field field_197674_b = (Field)Util.func_199748_a(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field var0 = JsonReader.class.getDeclaredField("pos");
            var0.setAccessible(true);
            return var0;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
         }
      });
      private static final Field field_200530_c = (Field)Util.func_199748_a(() -> {
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

      public ITextComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new TextComponentString(var1.getAsString());
         } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var11 = var1.getAsJsonArray();
               ITextComponent var16 = null;
               Iterator var14 = var11.iterator();

               while(var14.hasNext()) {
                  JsonElement var17 = (JsonElement)var14.next();
                  ITextComponent var18 = this.deserialize(var17, var17.getClass(), var3);
                  if (var16 == null) {
                     var16 = var18;
                  } else {
                     var16.func_150257_a(var18);
                  }
               }

               return var16;
            } else {
               throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
            }
         } else {
            JsonObject var4 = var1.getAsJsonObject();
            Object var5;
            if (var4.has("text")) {
               var5 = new TextComponentString(var4.get("text").getAsString());
            } else if (var4.has("translate")) {
               String var6 = var4.get("translate").getAsString();
               if (var4.has("with")) {
                  JsonArray var7 = var4.getAsJsonArray("with");
                  Object[] var8 = new Object[var7.size()];

                  for(int var9 = 0; var9 < var8.length; ++var9) {
                     var8[var9] = this.deserialize(var7.get(var9), var2, var3);
                     if (var8[var9] instanceof TextComponentString) {
                        TextComponentString var10 = (TextComponentString)var8[var9];
                        if (var10.func_150256_b().func_150229_g() && var10.func_150253_a().isEmpty()) {
                           var8[var9] = var10.func_150265_g();
                        }
                     }
                  }

                  var5 = new TextComponentTranslation(var6, var8);
               } else {
                  var5 = new TextComponentTranslation(var6, new Object[0]);
               }
            } else if (var4.has("score")) {
               JsonObject var12 = var4.getAsJsonObject("score");
               if (!var12.has("name") || !var12.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var5 = new TextComponentScore(JsonUtils.func_151200_h(var12, "name"), JsonUtils.func_151200_h(var12, "objective"));
               if (var12.has("value")) {
                  ((TextComponentScore)var5).func_179997_b(JsonUtils.func_151200_h(var12, "value"));
               }
            } else if (var4.has("selector")) {
               var5 = new TextComponentSelector(JsonUtils.func_151200_h(var4, "selector"));
            } else {
               if (!var4.has("keybind")) {
                  throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
               }

               var5 = new TextComponentKeybind(JsonUtils.func_151200_h(var4, "keybind"));
            }

            if (var4.has("extra")) {
               JsonArray var13 = var4.getAsJsonArray("extra");
               if (var13.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var15 = 0; var15 < var13.size(); ++var15) {
                  ((ITextComponent)var5).func_150257_a(this.deserialize(var13.get(var15), var2, var3));
               }
            }

            ((ITextComponent)var5).func_150255_a((Style)var3.deserialize(var1, Style.class));
            return (ITextComponent)var5;
         }
      }

      private void func_150695_a(Style var1, JsonObject var2, JsonSerializationContext var3) {
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

      public JsonElement serialize(ITextComponent var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (!var1.func_150256_b().func_150229_g()) {
            this.func_150695_a(var1.func_150256_b(), var4, var3);
         }

         if (!var1.func_150253_a().isEmpty()) {
            JsonArray var5 = new JsonArray();
            Iterator var6 = var1.func_150253_a().iterator();

            while(var6.hasNext()) {
               ITextComponent var7 = (ITextComponent)var6.next();
               var5.add(this.serialize((ITextComponent)var7, var7.getClass(), var3));
            }

            var4.add("extra", var5);
         }

         if (var1 instanceof TextComponentString) {
            var4.addProperty("text", ((TextComponentString)var1).func_150265_g());
         } else if (var1 instanceof TextComponentTranslation) {
            TextComponentTranslation var11 = (TextComponentTranslation)var1;
            var4.addProperty("translate", var11.func_150268_i());
            if (var11.func_150271_j() != null && var11.func_150271_j().length > 0) {
               JsonArray var14 = new JsonArray();
               Object[] var17 = var11.func_150271_j();
               int var8 = var17.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Object var10 = var17[var9];
                  if (var10 instanceof ITextComponent) {
                     var14.add(this.serialize((ITextComponent)((ITextComponent)var10), var10.getClass(), var3));
                  } else {
                     var14.add(new JsonPrimitive(String.valueOf(var10)));
                  }
               }

               var4.add("with", var14);
            }
         } else if (var1 instanceof TextComponentScore) {
            TextComponentScore var12 = (TextComponentScore)var1;
            JsonObject var16 = new JsonObject();
            var16.addProperty("name", var12.func_179995_g());
            var16.addProperty("objective", var12.func_179994_h());
            var16.addProperty("value", var12.func_150261_e());
            var4.add("score", var16);
         } else if (var1 instanceof TextComponentSelector) {
            TextComponentSelector var13 = (TextComponentSelector)var1;
            var4.addProperty("selector", var13.func_179992_g());
         } else {
            if (!(var1 instanceof TextComponentKeybind)) {
               throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
            }

            TextComponentKeybind var15 = (TextComponentKeybind)var1;
            var4.addProperty("keybind", var15.func_193633_h());
         }

         return var4;
      }

      public static String func_150696_a(ITextComponent var0) {
         return field_150700_a.toJson(var0);
      }

      public static JsonElement func_200528_b(ITextComponent var0) {
         return field_150700_a.toJsonTree(var0);
      }

      @Nullable
      public static ITextComponent func_150699_a(String var0) {
         return (ITextComponent)JsonUtils.func_188176_a(field_150700_a, var0, ITextComponent.class, false);
      }

      @Nullable
      public static ITextComponent func_197672_a(JsonElement var0) {
         return (ITextComponent)field_150700_a.fromJson(var0, ITextComponent.class);
      }

      @Nullable
      public static ITextComponent func_186877_b(String var0) {
         return (ITextComponent)JsonUtils.func_188176_a(field_150700_a, var0, ITextComponent.class, true);
      }

      public static ITextComponent func_197671_a(com.mojang.brigadier.StringReader var0) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(var0.getRemaining()));
            var1.setLenient(false);
            ITextComponent var2 = (ITextComponent)field_150700_a.getAdapter(ITextComponent.class).read(var1);
            var0.setCursor(var0.getCursor() + func_197673_a(var1));
            return var2;
         } catch (IOException var3) {
            throw new JsonParseException(var3);
         }
      }

      private static int func_197673_a(JsonReader var0) {
         try {
            return field_197674_b.getInt(var0) - field_200530_c.getInt(var0) + 1;
         } catch (IllegalAccessException var2) {
            throw new IllegalStateException("Couldn't read position of JsonReader", var2);
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ITextComponent)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
