package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   private final ResourceLocation name;
   private final long seed;

   private SetContainerLootTable(LootItemCondition[] var1, ResourceLocation var2, long var3) {
      super(var1);
      this.name = var2;
      this.seed = var3;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LOOT_TABLE;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         CompoundTag var3 = new CompoundTag();
         var3.putString("LootTable", this.name.toString());
         if (this.seed != 0L) {
            var3.putLong("LootTableSeed", this.seed);
         }

         var1.getOrCreateTag().put("BlockEntityTag", var3);
         return var1;
      }
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

   // $FF: synthetic method
   SetContainerLootTable(LootItemCondition[] var1, ResourceLocation var2, long var3, Object var5) {
      this(var1, var2, var3);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetContainerLootTable var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("name", var2.name.toString());
         if (var2.seed != 0L) {
            var1.addProperty("seed", var2.seed);
         }

      }

      public SetContainerLootTable deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         long var5 = GsonHelper.getAsLong(var1, "seed", 0L);
         return new SetContainerLootTable(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
