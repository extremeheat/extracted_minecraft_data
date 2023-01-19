package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final List<Enchantment> enchantments;

   EnchantRandomlyFunction(LootItemCondition[] var1, Collection<Enchantment> var2) {
      super(var1);
      this.enchantments = ImmutableList.copyOf(var2);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.ENCHANT_RANDOMLY;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var4 = var2.getRandom();
      Enchantment var3;
      if (this.enchantments.isEmpty()) {
         boolean var5 = var1.is(Items.BOOK);
         List var6 = BuiltInRegistries.ENCHANTMENT
            .stream()
            .filter(Enchantment::isDiscoverable)
            .filter(var2x -> var5 || var2x.canEnchant(var1))
            .collect(Collectors.toList());
         if (var6.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", var1);
            return var1;
         }

         var3 = (Enchantment)var6.get(var4.nextInt(var6.size()));
      } else {
         var3 = this.enchantments.get(var4.nextInt(this.enchantments.size()));
      }

      return enchantItem(var1, var3, var4);
   }

   private static ItemStack enchantItem(ItemStack var0, Enchantment var1, RandomSource var2) {
      int var3 = Mth.nextInt(var2, var1.getMinLevel(), var1.getMaxLevel());
      if (var0.is(Items.BOOK)) {
         var0 = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(var0, new EnchantmentInstance(var1, var3));
      } else {
         var0.enchant(var1, var3);
      }

      return var0;
   }

   public static EnchantRandomlyFunction.Builder randomEnchantment() {
      return new EnchantRandomlyFunction.Builder();
   }

   public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
      return simpleBuilder(var0 -> new EnchantRandomlyFunction(var0, ImmutableList.of()));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantRandomlyFunction.Builder> {
      private final Set<Enchantment> enchantments = Sets.newHashSet();

      public Builder() {
         super();
      }

      protected EnchantRandomlyFunction.Builder getThis() {
         return this;
      }

      public EnchantRandomlyFunction.Builder withEnchantment(Enchantment var1) {
         this.enchantments.add(var1);
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new EnchantRandomlyFunction(this.getConditions(), this.enchantments);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, EnchantRandomlyFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         if (!var2.enchantments.isEmpty()) {
            JsonArray var4 = new JsonArray();

            for(Enchantment var6 : var2.enchantments) {
               ResourceLocation var7 = BuiltInRegistries.ENCHANTMENT.getKey(var6);
               if (var7 == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + var6);
               }

               var4.add(new JsonPrimitive(var7.toString()));
            }

            var1.add("enchantments", var4);
         }
      }

      public EnchantRandomlyFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ArrayList var4 = Lists.newArrayList();
         if (var1.has("enchantments")) {
            for(JsonElement var7 : GsonHelper.getAsJsonArray(var1, "enchantments")) {
               String var8 = GsonHelper.convertToString(var7, "enchantment");
               Enchantment var9 = BuiltInRegistries.ENCHANTMENT
                  .getOptional(new ResourceLocation(var8))
                  .orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + var8 + "'"));
               var4.add(var9);
            }
         }

         return new EnchantRandomlyFunction(var3, var4);
      }
   }
}
