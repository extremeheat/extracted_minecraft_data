package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   private final Map<MobEffect, RandomValueBounds> effectDurationMap;

   private SetStewEffectFunction(LootItemCondition[] var1, Map<MobEffect, RandomValueBounds> var2) {
      super(var1);
      this.effectDurationMap = ImmutableMap.copyOf(var2);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_STEW_EFFECT;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.is(Items.SUSPICIOUS_STEW) && !this.effectDurationMap.isEmpty()) {
         Random var3 = var2.getRandom();
         int var4 = var3.nextInt(this.effectDurationMap.size());
         Entry var5 = (Entry)Iterables.get(this.effectDurationMap.entrySet(), var4);
         MobEffect var6 = (MobEffect)var5.getKey();
         int var7 = ((RandomValueBounds)var5.getValue()).getInt(var3);
         if (!var6.isInstantenous()) {
            var7 *= 20;
         }

         SuspiciousStewItem.saveMobEffect(var1, var6, var7);
         return var1;
      } else {
         return var1;
      }
   }

   public static SetStewEffectFunction.Builder stewEffect() {
      return new SetStewEffectFunction.Builder();
   }

   // $FF: synthetic method
   SetStewEffectFunction(LootItemCondition[] var1, Map var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetStewEffectFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetStewEffectFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         if (!var2.effectDurationMap.isEmpty()) {
            JsonArray var4 = new JsonArray();
            Iterator var5 = var2.effectDurationMap.keySet().iterator();

            while(var5.hasNext()) {
               MobEffect var6 = (MobEffect)var5.next();
               JsonObject var7 = new JsonObject();
               ResourceLocation var8 = Registry.MOB_EFFECT.getKey(var6);
               if (var8 == null) {
                  throw new IllegalArgumentException("Don't know how to serialize mob effect " + var6);
               }

               var7.add("type", new JsonPrimitive(var8.toString()));
               var7.add("duration", var3.serialize(var2.effectDurationMap.get(var6)));
               var4.add(var7);
            }

            var1.add("effects", var4);
         }

      }

      public SetStewEffectFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         HashMap var4 = Maps.newHashMap();
         if (var1.has("effects")) {
            JsonArray var5 = GsonHelper.getAsJsonArray(var1, "effects");
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               String var8 = GsonHelper.getAsString(var7.getAsJsonObject(), "type");
               MobEffect var9 = (MobEffect)Registry.MOB_EFFECT.getOptional(new ResourceLocation(var8)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown mob effect '" + var8 + "'");
               });
               RandomValueBounds var10 = (RandomValueBounds)GsonHelper.getAsObject(var7.getAsJsonObject(), "duration", var2, RandomValueBounds.class);
               var4.put(var9, var10);
            }
         }

         return new SetStewEffectFunction(var3, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetStewEffectFunction.Builder> {
      private final Map<MobEffect, RandomValueBounds> effectDurationMap = Maps.newHashMap();

      public Builder() {
         super();
      }

      protected SetStewEffectFunction.Builder getThis() {
         return this;
      }

      public SetStewEffectFunction.Builder withEffect(MobEffect var1, RandomValueBounds var2) {
         this.effectDurationMap.put(var1, var2);
         return this;
      }

      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
