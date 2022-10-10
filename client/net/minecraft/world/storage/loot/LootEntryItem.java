package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootEntryItem extends LootEntry {
   protected final Item field_186368_a;
   protected final LootFunction[] field_186369_b;

   public LootEntryItem(Item var1, int var2, int var3, LootFunction[] var4, LootCondition[] var5) {
      super(var2, var3, var5);
      this.field_186368_a = var1;
      this.field_186369_b = var4;
   }

   public void func_186363_a(Collection<ItemStack> var1, Random var2, LootContext var3) {
      ItemStack var4 = new ItemStack(this.field_186368_a);
      LootFunction[] var5 = this.field_186369_b;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         LootFunction var8 = var5[var7];
         if (LootConditionManager.func_186638_a(var8.func_186554_a(), var2, var3)) {
            var4 = var8.func_186553_a(var4, var2, var3);
         }
      }

      if (!var4.func_190926_b()) {
         if (var4.func_190916_E() < this.field_186368_a.func_77639_j()) {
            var1.add(var4);
         } else {
            int var9 = var4.func_190916_E();

            while(var9 > 0) {
               ItemStack var10 = var4.func_77946_l();
               var10.func_190920_e(Math.min(var4.func_77976_d(), var9));
               var9 -= var10.func_190916_E();
               var1.add(var10);
            }
         }
      }

   }

   protected void func_186362_a(JsonObject var1, JsonSerializationContext var2) {
      if (this.field_186369_b != null && this.field_186369_b.length > 0) {
         var1.add("functions", var2.serialize(this.field_186369_b));
      }

      ResourceLocation var3 = IRegistry.field_212630_s.func_177774_c(this.field_186368_a);
      if (var3 == null) {
         throw new IllegalArgumentException("Can't serialize unknown item " + this.field_186368_a);
      } else {
         var1.addProperty("name", var3.toString());
      }
   }

   public static LootEntryItem func_186367_a(JsonObject var0, JsonDeserializationContext var1, int var2, int var3, LootCondition[] var4) {
      Item var5 = JsonUtils.func_188180_i(var0, "name");
      LootFunction[] var6;
      if (var0.has("functions")) {
         var6 = (LootFunction[])JsonUtils.func_188174_a(var0, "functions", var1, LootFunction[].class);
      } else {
         var6 = new LootFunction[0];
      }

      return new LootEntryItem(var5, var2, var3, var6, var4);
   }
}
