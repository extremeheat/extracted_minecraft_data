package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class BonusLevelTableCondition implements LootItemCondition {
   private final Enchantment enchantment;
   private final float[] values;

   private BonusLevelTableCondition(Enchantment var1, float[] var2) {
      super();
      this.enchantment = var1;
      this.values = var2;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = (ItemStack)var1.getParamOrNull(LootContextParams.TOOL);
      int var3 = var2 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var2) : 0;
      float var4 = this.values[Math.min(var3, this.values.length - 1)];
      return var1.getRandom().nextFloat() < var4;
   }

   public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment var0, float... var1) {
      return () -> {
         return new BonusLevelTableCondition(var0, var1);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   BonusLevelTableCondition(Enchantment var1, float[] var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer<BonusLevelTableCondition> {
      public Serializer() {
         super(new ResourceLocation("table_bonus"), BonusLevelTableCondition.class);
      }

      public void serialize(JsonObject var1, BonusLevelTableCondition var2, JsonSerializationContext var3) {
         var1.addProperty("enchantment", Registry.ENCHANTMENT.getKey(var2.enchantment).toString());
         var1.add("chances", var3.serialize(var2.values));
      }

      public BonusLevelTableCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "enchantment"));
         Enchantment var4 = (Enchantment)Registry.ENCHANTMENT.getOptional(var3).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + var3);
         });
         float[] var5 = (float[])GsonHelper.getAsObject(var1, "chances", var2, float[].class);
         return new BonusLevelTableCondition(var4, var5);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
