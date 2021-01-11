package net.minecraft.util;

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
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable<IChatComponent> {
   IChatComponent func_150255_a(ChatStyle var1);

   ChatStyle func_150256_b();

   IChatComponent func_150258_a(String var1);

   IChatComponent func_150257_a(IChatComponent var1);

   String func_150261_e();

   String func_150260_c();

   String func_150254_d();

   List<IChatComponent> func_150253_a();

   IChatComponent func_150259_f();

   public static class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {
      private static final Gson field_150700_a;

      public Serializer() {
         super();
      }

      public IChatComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new ChatComponentText(var1.getAsString());
         } else if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var11 = var1.getAsJsonArray();
               IChatComponent var15 = null;
               Iterator var14 = var11.iterator();

               while(var14.hasNext()) {
                  JsonElement var17 = (JsonElement)var14.next();
                  IChatComponent var18 = this.deserialize(var17, var17.getClass(), var3);
                  if (var15 == null) {
                     var15 = var18;
                  } else {
                     var15.func_150257_a(var18);
                  }
               }

               return var15;
            } else {
               throw new JsonParseException("Don't know how to turn " + var1.toString() + " into a Component");
            }
         } else {
            JsonObject var4 = var1.getAsJsonObject();
            Object var5;
            if (var4.has("text")) {
               var5 = new ChatComponentText(var4.get("text").getAsString());
            } else if (var4.has("translate")) {
               String var12 = var4.get("translate").getAsString();
               if (var4.has("with")) {
                  JsonArray var7 = var4.getAsJsonArray("with");
                  Object[] var8 = new Object[var7.size()];

                  for(int var9 = 0; var9 < var8.length; ++var9) {
                     var8[var9] = this.deserialize(var7.get(var9), var2, var3);
                     if (var8[var9] instanceof ChatComponentText) {
                        ChatComponentText var10 = (ChatComponentText)var8[var9];
                        if (var10.func_150256_b().func_150229_g() && var10.func_150253_a().isEmpty()) {
                           var8[var9] = var10.func_150265_g();
                        }
                     }
                  }

                  var5 = new ChatComponentTranslation(var12, var8);
               } else {
                  var5 = new ChatComponentTranslation(var12, new Object[0]);
               }
            } else if (var4.has("score")) {
               JsonObject var6 = var4.getAsJsonObject("score");
               if (!var6.has("name") || !var6.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var5 = new ChatComponentScore(JsonUtils.func_151200_h(var6, "name"), JsonUtils.func_151200_h(var6, "objective"));
               if (var6.has("value")) {
                  ((ChatComponentScore)var5).func_179997_b(JsonUtils.func_151200_h(var6, "value"));
               }
            } else {
               if (!var4.has("selector")) {
                  throw new JsonParseException("Don't know how to turn " + var1.toString() + " into a Component");
               }

               var5 = new ChatComponentSelector(JsonUtils.func_151200_h(var4, "selector"));
            }

            if (var4.has("extra")) {
               JsonArray var13 = var4.getAsJsonArray("extra");
               if (var13.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var16 = 0; var16 < var13.size(); ++var16) {
                  ((IChatComponent)var5).func_150257_a(this.deserialize(var13.get(var16), var2, var3));
               }
            }

            ((IChatComponent)var5).func_150255_a((ChatStyle)var3.deserialize(var1, ChatStyle.class));
            return (IChatComponent)var5;
         }
      }

      private void func_150695_a(ChatStyle var1, JsonObject var2, JsonSerializationContext var3) {
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

      public JsonElement serialize(IChatComponent var1, Type var2, JsonSerializationContext var3) {
         if (var1 instanceof ChatComponentText && var1.func_150256_b().func_150229_g() && var1.func_150253_a().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText)var1).func_150265_g());
         } else {
            JsonObject var4 = new JsonObject();
            if (!var1.func_150256_b().func_150229_g()) {
               this.func_150695_a(var1.func_150256_b(), var4, var3);
            }

            if (!var1.func_150253_a().isEmpty()) {
               JsonArray var5 = new JsonArray();
               Iterator var6 = var1.func_150253_a().iterator();

               while(var6.hasNext()) {
                  IChatComponent var7 = (IChatComponent)var6.next();
                  var5.add(this.serialize((IChatComponent)var7, var7.getClass(), var3));
               }

               var4.add("extra", var5);
            }

            if (var1 instanceof ChatComponentText) {
               var4.addProperty("text", ((ChatComponentText)var1).func_150265_g());
            } else if (var1 instanceof ChatComponentTranslation) {
               ChatComponentTranslation var11 = (ChatComponentTranslation)var1;
               var4.addProperty("translate", var11.func_150268_i());
               if (var11.func_150271_j() != null && var11.func_150271_j().length > 0) {
                  JsonArray var14 = new JsonArray();
                  Object[] var16 = var11.func_150271_j();
                  int var8 = var16.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     Object var10 = var16[var9];
                     if (var10 instanceof IChatComponent) {
                        var14.add(this.serialize((IChatComponent)((IChatComponent)var10), var10.getClass(), var3));
                     } else {
                        var14.add(new JsonPrimitive(String.valueOf(var10)));
                     }
                  }

                  var4.add("with", var14);
               }
            } else if (var1 instanceof ChatComponentScore) {
               ChatComponentScore var12 = (ChatComponentScore)var1;
               JsonObject var15 = new JsonObject();
               var15.addProperty("name", var12.func_179995_g());
               var15.addProperty("objective", var12.func_179994_h());
               var15.addProperty("value", var12.func_150261_e());
               var4.add("score", var15);
            } else {
               if (!(var1 instanceof ChatComponentSelector)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
               }

               ChatComponentSelector var13 = (ChatComponentSelector)var1;
               var4.addProperty("selector", var13.func_179992_g());
            }

            return var4;
         }
      }

      public static String func_150696_a(IChatComponent var0) {
         return field_150700_a.toJson(var0);
      }

      public static IChatComponent func_150699_a(String var0) {
         return (IChatComponent)field_150700_a.fromJson(var0, IChatComponent.class);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((IChatComponent)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }

      static {
         GsonBuilder var0 = new GsonBuilder();
         var0.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
         var0.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
         var0.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         field_150700_a = var0.create();
      }
   }
}
