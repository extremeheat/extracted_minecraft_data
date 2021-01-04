package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction<ItemStack, LootContext, ItemStack> {
   static Consumer<ItemStack> decorate(BiFunction<ItemStack, LootContext, ItemStack> var0, Consumer<ItemStack> var1, LootContext var2) {
      return (var3) -> {
         var1.accept(var0.apply(var3, var2));
      };
   }

   public abstract static class Serializer<T extends LootItemFunction> {
      private final ResourceLocation name;
      private final Class<T> clazz;

      protected Serializer(ResourceLocation var1, Class<T> var2) {
         super();
         this.name = var1;
         this.clazz = var2;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class<T> getFunctionClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   public interface Builder {
      LootItemFunction build();
   }
}
