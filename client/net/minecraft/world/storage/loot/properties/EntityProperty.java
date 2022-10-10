package net.minecraft.world.storage.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface EntityProperty {
   boolean func_186657_a(Random var1, Entity var2);

   public abstract static class Serializer<T extends EntityProperty> {
      private final ResourceLocation field_186653_a;
      private final Class<T> field_186654_b;

      protected Serializer(ResourceLocation var1, Class<T> var2) {
         super();
         this.field_186653_a = var1;
         this.field_186654_b = var2;
      }

      public ResourceLocation func_186649_a() {
         return this.field_186653_a;
      }

      public Class<T> func_186651_b() {
         return this.field_186654_b;
      }

      public abstract JsonElement func_186650_a(T var1, JsonSerializationContext var2);

      public abstract T func_186652_a(JsonElement var1, JsonDeserializationContext var2);
   }
}
