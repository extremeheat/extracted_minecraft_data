package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

@FunctionalInterface
public interface LootItemCondition extends LootContextUser, Predicate<LootContext> {
   public abstract static class Serializer<T extends LootItemCondition> {
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

      public Class<T> getPredicateClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   @FunctionalInterface
   public interface Builder {
      LootItemCondition build();

      default LootItemCondition.Builder invert() {
         return InvertedLootItemCondition.invert(this);
      }

      default AlternativeLootItemCondition.Builder or(LootItemCondition.Builder var1) {
         return AlternativeLootItemCondition.alternative(this, var1);
      }
   }
}
