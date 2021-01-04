package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem extends LootPoolSingletonContainer {
   private EmptyLootItem(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4) {
      super(var1, var2, var3, var4);
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
   }

   public static LootPoolSingletonContainer.Builder<?> emptyItem() {
      return simpleBuilder(EmptyLootItem::new);
   }

   // $FF: synthetic method
   EmptyLootItem(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<EmptyLootItem> {
      public Serializer() {
         super(new ResourceLocation("empty"), EmptyLootItem.class);
      }

      protected EmptyLootItem deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return new EmptyLootItem(var3, var4, var5, var6);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
