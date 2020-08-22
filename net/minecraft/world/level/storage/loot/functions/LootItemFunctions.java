package net.minecraft.world.level.storage.loot.functions;

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
import java.util.function.BiFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class LootItemFunctions {
   private static final Map FUNCTIONS_BY_NAME = Maps.newHashMap();
   private static final Map FUNCTIONS_BY_CLASS = Maps.newHashMap();
   public static final BiFunction IDENTITY = (var0, var1) -> {
      return var0;
   };

   public static void register(LootItemFunction.Serializer var0) {
      ResourceLocation var1 = var0.getName();
      Class var2 = var0.getFunctionClass();
      if (FUNCTIONS_BY_NAME.containsKey(var1)) {
         throw new IllegalArgumentException("Can't re-register item function name " + var1);
      } else if (FUNCTIONS_BY_CLASS.containsKey(var2)) {
         throw new IllegalArgumentException("Can't re-register item function class " + var2.getName());
      } else {
         FUNCTIONS_BY_NAME.put(var1, var0);
         FUNCTIONS_BY_CLASS.put(var2, var0);
      }
   }

   public static LootItemFunction.Serializer getSerializer(ResourceLocation var0) {
      LootItemFunction.Serializer var1 = (LootItemFunction.Serializer)FUNCTIONS_BY_NAME.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item function '" + var0 + "'");
      } else {
         return var1;
      }
   }

   public static LootItemFunction.Serializer getSerializer(LootItemFunction var0) {
      LootItemFunction.Serializer var1 = (LootItemFunction.Serializer)FUNCTIONS_BY_CLASS.get(var0.getClass());
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot item function " + var0);
      } else {
         return var1;
      }
   }

   public static BiFunction compose(BiFunction[] var0) {
      switch(var0.length) {
      case 0:
         return IDENTITY;
      case 1:
         return var0[0];
      case 2:
         BiFunction var1 = var0[0];
         BiFunction var2 = var0[1];
         return (var2x, var3) -> {
            return (ItemStack)var2.apply(var1.apply(var2x, var3), var3);
         };
      default:
         return (var1x, var2x) -> {
            BiFunction[] var3 = var0;
            int var4 = var0.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               BiFunction var6 = var3[var5];
               var1x = (ItemStack)var6.apply(var1x, var2x);
            }

            return var1x;
         };
      }
   }

   static {
      register(new SetItemCountFunction.Serializer());
      register(new EnchantWithLevelsFunction.Serializer());
      register(new EnchantRandomlyFunction.Serializer());
      register(new SetNbtFunction.Serializer());
      register(new SmeltItemFunction.Serializer());
      register(new LootingEnchantFunction.Serializer());
      register(new SetItemDamageFunction.Serializer());
      register(new SetAttributesFunction.Serializer());
      register(new SetNameFunction.Serializer());
      register(new ExplorationMapFunction.Serializer());
      register(new SetStewEffectFunction.Serializer());
      register(new CopyNameFunction.Serializer());
      register(new SetContainerContents.Serializer());
      register(new LimitCount.Serializer());
      register(new ApplyBonusCount.Serializer());
      register(new SetContainerLootTable.Serializer());
      register(new ApplyExplosionDecay.Serializer());
      register(new SetLoreFunction.Serializer());
      register(new FillPlayerHead.Serializer());
      register(new CopyNbtFunction.Serializer());
      register(new CopyBlockState.Serializer());
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootItemFunction deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "function");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "function"));

         LootItemFunction.Serializer var6;
         try {
            var6 = LootItemFunctions.getSerializer(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown function '" + var5 + "'");
         }

         return var6.deserialize(var4, var3);
      }

      public JsonElement serialize(LootItemFunction var1, Type var2, JsonSerializationContext var3) {
         LootItemFunction.Serializer var4 = LootItemFunctions.getSerializer(var1);
         JsonObject var5 = new JsonObject();
         var5.addProperty("function", var4.getName().toString());
         var4.serialize(var5, var1, var3);
         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootItemFunction)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
