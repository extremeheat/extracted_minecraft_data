package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference extends LootPoolSingletonContainer {
   final ResourceLocation name;

   LootTableReference(ResourceLocation var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.REFERENCE;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      LootTable var3 = var2.getLootTable(this.name);
      var3.getRandomItemsRaw(var2, var1);
   }

   public void validate(ValidationContext var1) {
      if (var1.hasVisitedTable(this.name)) {
         var1.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(var1);
         LootTable var2 = var1.resolveLootTable(this.name);
         if (var2 == null) {
            var1.reportProblem("Unknown loot table called " + this.name);
         } else {
            var2.validate(var1.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new LootTableReference(var0, var1, var2, var3, var4);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<LootTableReference> {
      public Serializer() {
         super();
      }

      public void serializeCustom(JsonObject var1, LootTableReference var2, JsonSerializationContext var3) {
         super.serializeCustom(var1, (LootPoolSingletonContainer)var2, var3);
         var1.addProperty("name", var2.name.toString());
      }

      protected LootTableReference deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         return new LootTableReference(var7, var3, var4, var5, var6);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
