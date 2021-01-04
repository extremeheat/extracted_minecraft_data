package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class DynamicLoot extends LootPoolSingletonContainer {
   public static final ResourceLocation TYPE = new ResourceLocation("dynamic");
   private final ResourceLocation name;

   private DynamicLoot(ResourceLocation var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5) {
      super(var2, var3, var4, var5);
      this.name = var1;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      var2.addDynamicDrops(this.name, var1);
   }

   public static LootPoolSingletonContainer.Builder<?> dynamicEntry(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new DynamicLoot(var0, var1, var2, var3, var4);
      });
   }

   // $FF: synthetic method
   DynamicLoot(ResourceLocation var1, int var2, int var3, LootItemCondition[] var4, LootItemFunction[] var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<DynamicLoot> {
      public Serializer() {
         super(new ResourceLocation("dynamic"), DynamicLoot.class);
      }

      public void serialize(JsonObject var1, DynamicLoot var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootPoolSingletonContainer)var2, var3);
         var1.addProperty("name", var2.name.toString());
      }

      protected DynamicLoot deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         return new DynamicLoot(var7, var3, var4, var5, var6);
      }

      // $FF: synthetic method
      protected LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6) {
         return this.deserialize(var1, var2, var3, var4, var5, var6);
      }
   }
}
