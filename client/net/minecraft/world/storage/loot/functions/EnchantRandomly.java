package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomly extends LootFunction {
   private static final Logger field_186557_a = LogManager.getLogger();
   private final List<Enchantment> field_186558_b;

   public EnchantRandomly(LootCondition[] var1, @Nullable List<Enchantment> var2) {
      super(var1);
      this.field_186558_b = var2 == null ? Collections.emptyList() : var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      Enchantment var4;
      if (this.field_186558_b.isEmpty()) {
         ArrayList var5 = Lists.newArrayList();
         Iterator var6 = IRegistry.field_212628_q.iterator();

         label32:
         while(true) {
            Enchantment var7;
            do {
               if (!var6.hasNext()) {
                  if (var5.isEmpty()) {
                     field_186557_a.warn("Couldn't find a compatible enchantment for {}", var1);
                     return var1;
                  }

                  var4 = (Enchantment)var5.get(var2.nextInt(var5.size()));
                  break label32;
               }

               var7 = (Enchantment)var6.next();
            } while(var1.func_77973_b() != Items.field_151122_aG && !var7.func_92089_a(var1));

            var5.add(var7);
         }
      } else {
         var4 = (Enchantment)this.field_186558_b.get(var2.nextInt(this.field_186558_b.size()));
      }

      int var8 = MathHelper.func_76136_a(var2, var4.func_77319_d(), var4.func_77325_b());
      if (var1.func_77973_b() == Items.field_151122_aG) {
         var1 = new ItemStack(Items.field_151134_bR);
         ItemEnchantedBook.func_92115_a(var1, new EnchantmentData(var4, var8));
      } else {
         var1.func_77966_a(var4, var8);
      }

      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {
      public Serializer() {
         super(new ResourceLocation("enchant_randomly"), EnchantRandomly.class);
      }

      public void func_186532_a(JsonObject var1, EnchantRandomly var2, JsonSerializationContext var3) {
         if (!var2.field_186558_b.isEmpty()) {
            JsonArray var4 = new JsonArray();
            Iterator var5 = var2.field_186558_b.iterator();

            while(var5.hasNext()) {
               Enchantment var6 = (Enchantment)var5.next();
               ResourceLocation var7 = IRegistry.field_212628_q.func_177774_c(var6);
               if (var7 == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + var6);
               }

               var4.add(new JsonPrimitive(var7.toString()));
            }

            var1.add("enchantments", var4);
         }

      }

      public EnchantRandomly func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         ArrayList var4 = Lists.newArrayList();
         if (var1.has("enchantments")) {
            JsonArray var5 = JsonUtils.func_151214_t(var1, "enchantments");
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               String var8 = JsonUtils.func_151206_a(var7, "enchantment");
               Enchantment var9 = (Enchantment)IRegistry.field_212628_q.func_212608_b(new ResourceLocation(var8));
               if (var9 == null) {
                  throw new JsonSyntaxException("Unknown enchantment '" + var8 + "'");
               }

               var4.add(var9);
            }
         }

         return new EnchantRandomly(var3, var4);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
