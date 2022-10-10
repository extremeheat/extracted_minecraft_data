package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Smelt extends LootFunction {
   private static final Logger field_186574_a = LogManager.getLogger();

   public Smelt(LootCondition[] var1) {
      super(var1);
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      if (var1.func_190926_b()) {
         return var1;
      } else {
         IRecipe var4 = func_202880_a(var3, var1);
         if (var4 != null) {
            ItemStack var5 = var4.func_77571_b();
            if (!var5.func_190926_b()) {
               ItemStack var6 = var5.func_77946_l();
               var6.func_190920_e(var1.func_190916_E());
               return var6;
            }
         }

         field_186574_a.warn("Couldn't smelt {} because there is no smelting recipe", var1);
         return var1;
      }
   }

   @Nullable
   public static IRecipe func_202880_a(LootContext var0, ItemStack var1) {
      Iterator var2 = var0.func_202879_g().func_199532_z().func_199510_b().iterator();

      IRecipe var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (IRecipe)var2.next();
      } while(!(var3 instanceof FurnaceRecipe) || !((Ingredient)var3.func_192400_c().get(0)).test(var1));

      return var3;
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), Smelt.class);
      }

      public void func_186532_a(JsonObject var1, Smelt var2, JsonSerializationContext var3) {
      }

      public Smelt func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return new Smelt(var3);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
