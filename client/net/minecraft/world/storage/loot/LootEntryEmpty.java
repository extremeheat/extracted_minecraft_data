package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryEmpty extends LootEntry {
   public LootEntryEmpty(int var1, int var2, LootCondition[] var3) {
      super(var1, var2, var3);
   }

   public void func_186363_a(Collection<ItemStack> var1, Random var2, LootContext var3) {
   }

   protected void func_186362_a(JsonObject var1, JsonSerializationContext var2) {
   }

   public static LootEntryEmpty func_186372_a(JsonObject var0, JsonDeserializationContext var1, int var2, int var3, LootCondition[] var4) {
      return new LootEntryEmpty(var2, var3, var4);
   }
}
