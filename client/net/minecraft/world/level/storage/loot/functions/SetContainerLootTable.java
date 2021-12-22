package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   final ResourceLocation name;
   final long seed;
   final BlockEntityType<?> type;

   SetContainerLootTable(LootItemCondition[] var1, ResourceLocation var2, long var3, BlockEntityType<?> var5) {
      super(var1);
      this.name = var2;
      this.seed = var3;
      this.type = var5;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LOOT_TABLE;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         CompoundTag var3 = BlockItem.getBlockEntityData(var1);
         if (var3 == null) {
            var3 = new CompoundTag();
         }

         var3.putString("LootTable", this.name.toString());
         if (this.seed != 0L) {
            var3.putLong("LootTableSeed", this.seed);
         }

         BlockItem.setBlockEntityData(var1, this.type, var3);
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

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceLocation var1) {
      return simpleBuilder((var2) -> {
         return new SetContainerLootTable(var2, var1, 0L, var0);
      });
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> var0, ResourceLocation var1, long var2) {
      return simpleBuilder((var4) -> {
         return new SetContainerLootTable(var4, var1, var2, var0);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetContainerLootTable var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("name", var2.name.toString());
         var1.addProperty("type", Registry.BLOCK_ENTITY_TYPE.getKey(var2.type).toString());
         if (var2.seed != 0L) {
            var1.addProperty("seed", var2.seed);
         }

      }

      public SetContainerLootTable deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         long var5 = GsonHelper.getAsLong(var1, "seed", 0L);
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "type"));
         BlockEntityType var8 = (BlockEntityType)Registry.BLOCK_ENTITY_TYPE.getOptional(var7).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block entity type id '" + var7 + "'");
         });
         return new SetContainerLootTable(var3, var4, var5, var8);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
