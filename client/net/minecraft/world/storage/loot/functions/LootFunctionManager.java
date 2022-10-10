package net.minecraft.world.storage.loot.functions;

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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootFunctionManager {
   private static final Map<ResourceLocation, LootFunction.Serializer<?>> field_186584_a = Maps.newHashMap();
   private static final Map<Class<? extends LootFunction>, LootFunction.Serializer<?>> field_186585_b = Maps.newHashMap();

   public static <T extends LootFunction> void func_186582_a(LootFunction.Serializer<? extends T> var0) {
      ResourceLocation var1 = var0.func_186529_a();
      Class var2 = var0.func_186531_b();
      if (field_186584_a.containsKey(var1)) {
         throw new IllegalArgumentException("Can't re-register item function name " + var1);
      } else if (field_186585_b.containsKey(var2)) {
         throw new IllegalArgumentException("Can't re-register item function class " + var2.getName());
      } else {
         field_186584_a.put(var1, var0);
         field_186585_b.put(var2, var0);
      }
   }

   public static LootFunction.Serializer<?> func_186583_a(ResourceLocation var0) {
      LootFunction.Serializer var1 = (LootFunction.Serializer)field_186584_a.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item function '" + var0 + "'");
      } else {
         return var1;
      }
   }

   public static <T extends LootFunction> LootFunction.Serializer<T> func_186581_a(T var0) {
      LootFunction.Serializer var1 = (LootFunction.Serializer)field_186585_b.get(var0.getClass());
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item function " + var0);
      } else {
         return var1;
      }
   }

   static {
      func_186582_a(new SetCount.Serializer());
      func_186582_a(new EnchantWithLevels.Serializer());
      func_186582_a(new EnchantRandomly.Serializer());
      func_186582_a(new SetNBT.Serializer());
      func_186582_a(new Smelt.Serializer());
      func_186582_a(new LootingEnchantBonus.Serializer());
      func_186582_a(new SetDamage.Serializer());
      func_186582_a(new SetAttributes.Serializer());
      func_186582_a(new SetName.Serializer());
      func_186582_a(new ExplorationMap.Serializer());
   }

   public static class Serializer implements JsonDeserializer<LootFunction>, JsonSerializer<LootFunction> {
      public Serializer() {
         super();
      }

      public LootFunction deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "function");
         ResourceLocation var5 = new ResourceLocation(JsonUtils.func_151200_h(var4, "function"));

         LootFunction.Serializer var6;
         try {
            var6 = LootFunctionManager.func_186583_a(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown function '" + var5 + "'");
         }

         return var6.func_186530_b(var4, var3, (LootCondition[])JsonUtils.func_188177_a(var4, "conditions", new LootCondition[0], var3, LootCondition[].class));
      }

      public JsonElement serialize(LootFunction var1, Type var2, JsonSerializationContext var3) {
         LootFunction.Serializer var4 = LootFunctionManager.func_186581_a(var1);
         JsonObject var5 = new JsonObject();
         var4.func_186532_a(var5, var1, var3);
         var5.addProperty("function", var4.func_186529_a().toString());
         if (var1.func_186554_a() != null && var1.func_186554_a().length > 0) {
            var5.add("conditions", var3.serialize(var1.func_186554_a()));
         }

         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootFunction)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
