package net.minecraft.world.level.storage.loot.predicates;

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
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class LootItemConditions {
   private static final Map<ResourceLocation, LootItemCondition.Serializer<?>> CONDITIONS_BY_NAME = Maps.newHashMap();
   private static final Map<Class<? extends LootItemCondition>, LootItemCondition.Serializer<?>> CONDITIONS_BY_CLASS = Maps.newHashMap();

   public static <T extends LootItemCondition> void register(LootItemCondition.Serializer<? extends T> var0) {
      ResourceLocation var1 = var0.getName();
      Class var2 = var0.getPredicateClass();
      if (CONDITIONS_BY_NAME.containsKey(var1)) {
         throw new IllegalArgumentException("Can't re-register item condition name " + var1);
      } else if (CONDITIONS_BY_CLASS.containsKey(var2)) {
         throw new IllegalArgumentException("Can't re-register item condition class " + var2.getName());
      } else {
         CONDITIONS_BY_NAME.put(var1, var0);
         CONDITIONS_BY_CLASS.put(var2, var0);
      }
   }

   public static LootItemCondition.Serializer<?> getSerializer(ResourceLocation var0) {
      LootItemCondition.Serializer var1 = (LootItemCondition.Serializer)CONDITIONS_BY_NAME.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item condition '" + var0 + "'");
      } else {
         return var1;
      }
   }

   public static <T extends LootItemCondition> LootItemCondition.Serializer<T> getSerializer(T var0) {
      LootItemCondition.Serializer var1 = (LootItemCondition.Serializer)CONDITIONS_BY_CLASS.get(var0.getClass());
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item condition " + var0);
      } else {
         return var1;
      }
   }

   public static <T> Predicate<T> andConditions(Predicate<T>[] var0) {
      switch(var0.length) {
      case 0:
         return (var0x) -> {
            return true;
         };
      case 1:
         return var0[0];
      case 2:
         return var0[0].and(var0[1]);
      default:
         return (var1) -> {
            Predicate[] var2 = var0;
            int var3 = var0.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Predicate var5 = var2[var4];
               if (!var5.test(var1)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   public static <T> Predicate<T> orConditions(Predicate<T>[] var0) {
      switch(var0.length) {
      case 0:
         return (var0x) -> {
            return false;
         };
      case 1:
         return var0[0];
      case 2:
         return var0[0].or(var0[1]);
      default:
         return (var1) -> {
            Predicate[] var2 = var0;
            int var3 = var0.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Predicate var5 = var2[var4];
               if (var5.test(var1)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   static {
      register(new InvertedLootItemCondition.Serializer());
      register(new AlternativeLootItemCondition.Serializer());
      register(new LootItemRandomChanceCondition.Serializer());
      register(new LootItemRandomChanceWithLootingCondition.Serializer());
      register(new LootItemEntityPropertyCondition.Serializer());
      register(new LootItemKilledByPlayerCondition.Serializer());
      register(new EntityHasScoreCondition.Serializer());
      register(new LootItemBlockStatePropertyCondition.Serializer());
      register(new MatchTool.Serializer());
      register(new BonusLevelTableCondition.Serializer());
      register(new ExplosionCondition.Serializer());
      register(new DamageSourceCondition.Serializer());
      register(new LocationCheck.Serializer());
      register(new WeatherCheck.Serializer());
   }

   public static class Serializer implements JsonDeserializer<LootItemCondition>, JsonSerializer<LootItemCondition> {
      public Serializer() {
         super();
      }

      public LootItemCondition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "condition");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "condition"));

         LootItemCondition.Serializer var6;
         try {
            var6 = LootItemConditions.getSerializer(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown condition '" + var5 + "'");
         }

         return var6.deserialize(var4, var3);
      }

      public JsonElement serialize(LootItemCondition var1, Type var2, JsonSerializationContext var3) {
         LootItemCondition.Serializer var4 = LootItemConditions.getSerializer(var1);
         JsonObject var5 = new JsonObject();
         var5.addProperty("condition", var4.getName().toString());
         var4.serialize(var5, var1, var3);
         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootItemCondition)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
