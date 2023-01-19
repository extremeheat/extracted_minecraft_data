package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   static final Map<ResourceLocation, ApplyBonusCount.FormulaDeserializer> FORMULAS = Maps.newHashMap();
   final Enchantment enchantment;
   final ApplyBonusCount.Formula formula;

   ApplyBonusCount(LootItemCondition[] var1, Enchantment var2, ApplyBonusCount.Formula var3) {
      super(var1);
      this.enchantment = var2;
      this.formula = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.APPLY_BONUS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      ItemStack var3 = var2.getParamOrNull(LootContextParams.TOOL);
      if (var3 != null) {
         int var4 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var3);
         int var5 = this.formula.calculateNewCount(var2.getRandom(), var1.getCount(), var4);
         var1.setCount(var5);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment var0, float var1, int var2) {
      return simpleBuilder(var3 -> new ApplyBonusCount(var3, var0, new ApplyBonusCount.BinomialWithBonusCount(var2, var1)));
   }

   public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment var0) {
      return simpleBuilder(var1 -> new ApplyBonusCount(var1, var0, new ApplyBonusCount.OreDrops()));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment var0) {
      return simpleBuilder(var1 -> new ApplyBonusCount(var1, var0, new ApplyBonusCount.UniformBonusCount(1)));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment var0, int var1) {
      return simpleBuilder(var2 -> new ApplyBonusCount(var2, var0, new ApplyBonusCount.UniformBonusCount(var1)));
   }

   static {
      FORMULAS.put(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.BinomialWithBonusCount::deserialize);
      FORMULAS.put(ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.OreDrops::deserialize);
      FORMULAS.put(ApplyBonusCount.UniformBonusCount.TYPE, ApplyBonusCount.UniformBonusCount::deserialize);
   }

   static final class BinomialWithBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
      private final int extraRounds;
      private final float probability;

      public BinomialWithBonusCount(int var1, float var2) {
         super();
         this.extraRounds = var1;
         this.probability = var2;
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         for(int var4 = 0; var4 < var3 + this.extraRounds; ++var4) {
            if (var1.nextFloat() < this.probability) {
               ++var2;
            }
         }

         return var2;
      }

      @Override
      public void serializeParams(JsonObject var1, JsonSerializationContext var2) {
         var1.addProperty("extra", this.extraRounds);
         var1.addProperty("probability", this.probability);
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject var0, JsonDeserializationContext var1) {
         int var2 = GsonHelper.getAsInt(var0, "extra");
         float var3 = GsonHelper.getAsFloat(var0, "probability");
         return new ApplyBonusCount.BinomialWithBonusCount(var2, var3);
      }

      @Override
      public ResourceLocation getType() {
         return TYPE;
      }
   }

   interface Formula {
      int calculateNewCount(RandomSource var1, int var2, int var3);

      void serializeParams(JsonObject var1, JsonSerializationContext var2);

      ResourceLocation getType();
   }

   interface FormulaDeserializer {
      ApplyBonusCount.Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   static final class OreDrops implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

      OreDrops() {
         super();
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         if (var3 > 0) {
            int var4 = var1.nextInt(var3 + 2) - 1;
            if (var4 < 0) {
               var4 = 0;
            }

            return var2 * (var4 + 1);
         } else {
            return var2;
         }
      }

      @Override
      public void serializeParams(JsonObject var1, JsonSerializationContext var2) {
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject var0, JsonDeserializationContext var1) {
         return new ApplyBonusCount.OreDrops();
      }

      @Override
      public ResourceLocation getType() {
         return TYPE;
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyBonusCount> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ApplyBonusCount var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(var2.enchantment).toString());
         var1.addProperty("formula", var2.formula.getType().toString());
         JsonObject var4 = new JsonObject();
         var2.formula.serializeParams(var4, var3);
         if (var4.size() > 0) {
            var1.add("parameters", var4);
         }
      }

      public ApplyBonusCount deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "enchantment"));
         Enchantment var5 = BuiltInRegistries.ENCHANTMENT.getOptional(var4).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + var4));
         ResourceLocation var6 = new ResourceLocation(GsonHelper.getAsString(var1, "formula"));
         ApplyBonusCount.FormulaDeserializer var7 = ApplyBonusCount.FORMULAS.get(var6);
         if (var7 == null) {
            throw new JsonParseException("Invalid formula id: " + var6);
         } else {
            ApplyBonusCount.Formula var8;
            if (var1.has("parameters")) {
               var8 = var7.deserialize(GsonHelper.getAsJsonObject(var1, "parameters"), var2);
            } else {
               var8 = var7.deserialize(new JsonObject(), var2);
            }

            return new ApplyBonusCount(var3, var5, var8);
         }
      }
   }

   static final class UniformBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCount(int var1) {
         super();
         this.bonusMultiplier = var1;
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         return var2 + var1.nextInt(this.bonusMultiplier * var3 + 1);
      }

      @Override
      public void serializeParams(JsonObject var1, JsonSerializationContext var2) {
         var1.addProperty("bonusMultiplier", this.bonusMultiplier);
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject var0, JsonDeserializationContext var1) {
         int var2 = GsonHelper.getAsInt(var0, "bonusMultiplier");
         return new ApplyBonusCount.UniformBonusCount(var2);
      }

      @Override
      public ResourceLocation getType() {
         return TYPE;
      }
   }
}
