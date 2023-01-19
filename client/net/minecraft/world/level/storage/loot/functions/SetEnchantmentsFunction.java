package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetEnchantmentsFunction extends LootItemConditionalFunction {
   final Map<Enchantment, NumberProvider> enchantments;
   final boolean add;

   SetEnchantmentsFunction(LootItemCondition[] var1, Map<Enchantment, NumberProvider> var2, boolean var3) {
      super(var1);
      this.enchantments = ImmutableMap.copyOf(var2);
      this.add = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ENCHANTMENTS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.enchantments.values().stream().flatMap(var0 -> var0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      this.enchantments.forEach((var2x, var3x) -> var3.put(var2x, var3x.getInt(var2)));
      if (var1.getItem() == Items.BOOK) {
         ItemStack var5 = new ItemStack(Items.ENCHANTED_BOOK);
         var3.forEach((var1x, var2x) -> EnchantedBookItem.addEnchantment(var5, new EnchantmentInstance(var1x, var2x)));
         return var5;
      } else {
         Map var4 = EnchantmentHelper.getEnchantments(var1);
         if (this.add) {
            var3.forEach((var1x, var2x) -> updateEnchantment(var4, var1x, Math.max(var4.getOrDefault(var1x, 0) + var2x, 0)));
         } else {
            var3.forEach((var1x, var2x) -> updateEnchantment(var4, var1x, Math.max(var2x, 0)));
         }

         EnchantmentHelper.setEnchantments(var4, var1);
         return var1;
      }
   }

   private static void updateEnchantment(Map<Enchantment, Integer> var0, Enchantment var1, int var2) {
      if (var2 == 0) {
         var0.remove(var1);
      } else {
         var0.put(var1, var2);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetEnchantmentsFunction.Builder> {
      private final Map<Enchantment, NumberProvider> enchantments = Maps.newHashMap();
      private final boolean add;

      public Builder() {
         this(false);
      }

      public Builder(boolean var1) {
         super();
         this.add = var1;
      }

      protected SetEnchantmentsFunction.Builder getThis() {
         return this;
      }

      public SetEnchantmentsFunction.Builder withEnchantment(Enchantment var1, NumberProvider var2) {
         this.enchantments.put(var1, var2);
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetEnchantmentsFunction(this.getConditions(), this.enchantments, this.add);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetEnchantmentsFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetEnchantmentsFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         JsonObject var4 = new JsonObject();
         var2.enchantments.forEach((var2x, var3x) -> {
            ResourceLocation var4x = Registry.ENCHANTMENT.getKey(var2x);
            if (var4x == null) {
               throw new IllegalArgumentException("Don't know how to serialize enchantment " + var2x);
            } else {
               var4.add(var4x.toString(), var3.serialize(var3x));
            }
         });
         var1.add("enchantments", var4);
         var1.addProperty("add", var2.add);
      }

      public SetEnchantmentsFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         HashMap var4 = Maps.newHashMap();
         if (var1.has("enchantments")) {
            JsonObject var5 = GsonHelper.getAsJsonObject(var1, "enchantments");

            for(Entry var7 : var5.entrySet()) {
               String var8 = (String)var7.getKey();
               JsonElement var9 = (JsonElement)var7.getValue();
               Enchantment var10 = Registry.ENCHANTMENT
                  .getOptional(new ResourceLocation(var8))
                  .orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + var8 + "'"));
               NumberProvider var11 = (NumberProvider)var2.deserialize(var9, NumberProvider.class);
               var4.put(var10, var11);
            }
         }

         boolean var12 = GsonHelper.getAsBoolean(var1, "add", false);
         return new SetEnchantmentsFunction(var3, var4, var12);
      }
   }
}
