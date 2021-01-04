package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference extends LootPoolSingletonContainer {
   private final ResourceLocation name;

   private LootTableReference(ResourceLocation var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      LootTable var3 = var2.getLootTables().get(this.name);
      var3.getRandomItemsRaw(var2, var1);
   }

   public void validate(LootTableProblemCollector var1, Function<ResourceLocation, LootTable> var2, Set<ResourceLocation> var3, LootContextParamSet var4) {
      if (var3.contains(this.name)) {
         var1.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(var1, var2, var3, var4);
         LootTable var5 = (LootTable)var2.apply(this.name);
         if (var5 == null) {
            var1.reportProblem("Unknown loot table called " + this.name);
         } else {
            ImmutableSet var6 = ImmutableSet.builder().addAll(var3).add(this.name).build();
            var5.validate(var1.forChild("->{" + this.name + "}"), var2, var6, var4);
         }

      }
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new LootTableReference(var0, var1, var2, var3, var4);
      });
   }

   // $FF: synthetic method
   LootTableReference(ResourceLocation var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<LootTableReference> {
      public Serializer() {
         super(new ResourceLocation("loot_table"), LootTableReference.class);
      }

      public void serialize(JsonObject var1, LootTableReference var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootPoolSingletonContainer)var2, var3);
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
