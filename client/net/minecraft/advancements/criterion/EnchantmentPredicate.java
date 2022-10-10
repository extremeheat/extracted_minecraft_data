package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate field_192466_a = new EnchantmentPredicate();
   private final Enchantment field_192467_b;
   private final MinMaxBounds.IntBound field_192468_c;

   public EnchantmentPredicate() {
      super();
      this.field_192467_b = null;
      this.field_192468_c = MinMaxBounds.IntBound.field_211347_e;
   }

   public EnchantmentPredicate(@Nullable Enchantment var1, MinMaxBounds.IntBound var2) {
      super();
      this.field_192467_b = var1;
      this.field_192468_c = var2;
   }

   public boolean func_192463_a(Map<Enchantment, Integer> var1) {
      if (this.field_192467_b != null) {
         if (!var1.containsKey(this.field_192467_b)) {
            return false;
         }

         int var2 = (Integer)var1.get(this.field_192467_b);
         if (this.field_192468_c != null && !this.field_192468_c.func_211339_d(var2)) {
            return false;
         }
      } else if (this.field_192468_c != null) {
         Iterator var4 = var1.values().iterator();

         Integer var3;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var3 = (Integer)var4.next();
         } while(!this.field_192468_c.func_211339_d(var3));

         return true;
      }

      return true;
   }

   public JsonElement func_200306_a() {
      if (this == field_192466_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.field_192467_b != null) {
            var1.addProperty("enchantment", IRegistry.field_212628_q.func_177774_c(this.field_192467_b).toString());
         }

         var1.add("levels", this.field_192468_c.func_200321_c());
         return var1;
      }
   }

   public static EnchantmentPredicate func_192464_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "enchantment");
         Enchantment var2 = null;
         if (var1.has("enchantment")) {
            ResourceLocation var3 = new ResourceLocation(JsonUtils.func_151200_h(var1, "enchantment"));
            var2 = (Enchantment)IRegistry.field_212628_q.func_212608_b(var3);
            if (var2 == null) {
               throw new JsonSyntaxException("Unknown enchantment '" + var3 + "'");
            }
         }

         MinMaxBounds.IntBound var4 = MinMaxBounds.IntBound.func_211344_a(var1.get("levels"));
         return new EnchantmentPredicate(var2, var4);
      } else {
         return field_192466_a;
      }
   }

   public static EnchantmentPredicate[] func_192465_b(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = JsonUtils.func_151207_m(var0, "enchantments");
         EnchantmentPredicate[] var2 = new EnchantmentPredicate[var1.size()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = func_192464_a(var1.get(var3));
         }

         return var2;
      } else {
         return new EnchantmentPredicate[0];
      }
   }
}
