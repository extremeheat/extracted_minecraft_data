package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
   @Nullable
   private final Enchantment enchantment;
   private final MinMaxBounds.Ints level;

   public EnchantmentPredicate() {
      super();
      this.enchantment = null;
      this.level = MinMaxBounds.Ints.ANY;
   }

   public EnchantmentPredicate(@Nullable Enchantment var1, MinMaxBounds.Ints var2) {
      super();
      this.enchantment = var1;
      this.level = var2;
   }

   public boolean containedIn(Map<Enchantment, Integer> var1) {
      if (this.enchantment != null) {
         if (!var1.containsKey(this.enchantment)) {
            return false;
         }

         int var2 = var1.get(this.enchantment);
         if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches(var2)) {
            return false;
         }
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         for(Integer var3 : var1.values()) {
            if (this.level.matches(var3)) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.enchantment != null) {
            var1.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         var1.add("levels", this.level.serializeToJson());
         return var1;
      }
   }

   public static EnchantmentPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "enchantment");
         Enchantment var2 = null;
         if (var1.has("enchantment")) {
            ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "enchantment"));
            var2 = Registry.ENCHANTMENT.getOptional(var3).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + var3 + "'"));
         }

         MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("levels"));
         return new EnchantmentPredicate(var2, var4);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(var0, "enchantments");
         EnchantmentPredicate[] var2 = new EnchantmentPredicate[var1.size()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = fromJson(var1.get(var3));
         }

         return var2;
      } else {
         return NONE;
      }
   }
}
