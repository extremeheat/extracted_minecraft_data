package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantProperty<T> {
   private final String key;
   private final Function<T, JsonElement> serializer;

   public VariantProperty(String var1, Function<T, JsonElement> var2) {
      super();
      this.key = var1;
      this.serializer = var2;
   }

   public VariantProperty<T>.Value withValue(T var1) {
      return new VariantProperty.Value(var1);
   }

   public String toString() {
      return this.key;
   }

   public class Value {
      private final T value;

      public Value(T var2) {
         super();
         this.value = var2;
      }

      public void addToVariant(JsonObject var1) {
         var1.add(VariantProperty.this.key, (JsonElement)VariantProperty.this.serializer.apply(this.value));
      }

      public String toString() {
         return VariantProperty.this.key + "=" + this.value;
      }
   }
}
