package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public abstract class LootFunction {
   private final LootCondition[] field_186555_a;

   protected LootFunction(LootCondition[] var1) {
      super();
      this.field_186555_a = var1;
   }

   public abstract ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3);

   public LootCondition[] func_186554_a() {
      return this.field_186555_a;
   }

   public abstract static class Serializer<T extends LootFunction> {
      private final ResourceLocation field_186533_a;
      private final Class<T> field_186534_b;

      protected Serializer(ResourceLocation var1, Class<T> var2) {
         super();
         this.field_186533_a = var1;
         this.field_186534_b = var2;
      }

      public ResourceLocation func_186529_a() {
         return this.field_186533_a;
      }

      public Class<T> func_186531_b() {
         return this.field_186534_b;
      }

      public abstract void func_186532_a(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);
   }
}
