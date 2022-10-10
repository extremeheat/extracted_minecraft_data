package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class LootConditionManager {
   private static final Map<ResourceLocation, LootCondition.Serializer<?>> field_186642_a = Maps.newHashMap();
   private static final Map<Class<? extends LootCondition>, LootCondition.Serializer<?>> field_186643_b = Maps.newHashMap();

   public static <T extends LootCondition> void func_186639_a(LootCondition.Serializer<? extends T> var0) {
      ResourceLocation var1 = var0.func_186602_a();
      Class var2 = var0.func_186604_b();
      if (field_186642_a.containsKey(var1)) {
         throw new IllegalArgumentException("Can't re-register item condition name " + var1);
      } else if (field_186643_b.containsKey(var2)) {
         throw new IllegalArgumentException("Can't re-register item condition class " + var2.getName());
      } else {
         field_186642_a.put(var1, var0);
         field_186643_b.put(var2, var0);
      }
   }

   public static boolean func_186638_a(@Nullable LootCondition[] var0, Random var1, LootContext var2) {
      if (var0 == null) {
         return true;
      } else {
         LootCondition[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LootCondition var6 = var3[var5];
            if (!var6.func_186618_a(var1, var2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static LootCondition.Serializer<?> func_186641_a(ResourceLocation var0) {
      LootCondition.Serializer var1 = (LootCondition.Serializer)field_186642_a.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item condition '" + var0 + "'");
      } else {
         return var1;
      }
   }

   public static <T extends LootCondition> LootCondition.Serializer<T> func_186640_a(T var0) {
      LootCondition.Serializer var1 = (LootCondition.Serializer)field_186643_b.get(var0.getClass());
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item condition " + var0);
      } else {
         return var1;
      }
   }

   static {
      func_186639_a(new RandomChance.Serializer());
      func_186639_a(new RandomChanceWithLooting.Serializer());
      func_186639_a(new EntityHasProperty.Serializer());
      func_186639_a(new KilledByPlayer.Serializer());
      func_186639_a(new EntityHasScore.Serializer());
   }

   public static class Serializer implements JsonDeserializer<LootCondition>, JsonSerializer<LootCondition> {
      public Serializer() {
         super();
      }

      public LootCondition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "condition");
         ResourceLocation var5 = new ResourceLocation(JsonUtils.func_151200_h(var4, "condition"));

         LootCondition.Serializer var6;
         try {
            var6 = LootConditionManager.func_186641_a(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown condition '" + var5 + "'");
         }

         return var6.func_186603_b(var4, var3);
      }

      public JsonElement serialize(LootCondition var1, Type var2, JsonSerializationContext var3) {
         LootCondition.Serializer var4 = LootConditionManager.func_186640_a(var1);
         JsonObject var5 = new JsonObject();
         var4.func_186605_a(var5, var1, var3);
         var5.addProperty("condition", var4.func_186602_a().toString());
         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootCondition)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
