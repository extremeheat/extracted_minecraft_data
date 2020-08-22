package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem extends LootPoolSingletonContainer {
   private final Item item;

   private LootItem(Item var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5) {
      super(var2, var3, var4, var5);
      this.item = var1;
   }

   public void createItemStack(Consumer var1, LootContext var2) {
      var1.accept(new ItemStack(this.item));
   }

   public static LootPoolSingletonContainer.Builder lootTableItem(ItemLike var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new LootItem(var0.asItem(), var1, var2, var3, var4);
      });
   }

   // $FF: synthetic method
   LootItem(Item var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer {
      public Serializer() {
         super(new ResourceLocation("item"), LootItem.class);
      }

      public void serialize(JsonObject var1, LootItem var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootPoolSingletonContainer)var2, var3);
         ResourceLocation var4 = Registry.ITEM.getKey(var2.item);
         if (var4 == null) {
            throw new IllegalArgumentException("Can't serialize unknown item " + var2.item);
         } else {
            var1.addProperty("name", var4.toString());
         }
      }

      protected LootItem deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         Item var7 = GsonHelper.getAsItem(var1, "name");
         return new LootItem(var7, var3, var4, var5, var6);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
