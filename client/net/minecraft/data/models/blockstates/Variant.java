package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Variant implements Supplier<JsonElement> {
   private final Map<VariantProperty<?>, VariantProperty<?>.Value> values = Maps.newLinkedHashMap();

   public Variant() {
      super();
   }

   public <T> Variant with(VariantProperty<T> var1, T var2) {
      VariantProperty.Value var3 = (VariantProperty.Value)this.values.put(var1, var1.withValue(var2));
      if (var3 != null) {
         String var10002 = String.valueOf(var3);
         throw new IllegalStateException("Replacing value of " + var10002 + " with " + String.valueOf(var2));
      } else {
         return this;
      }
   }

   public static Variant variant() {
      return new Variant();
   }

   public static Variant merge(Variant var0, Variant var1) {
      Variant var2 = new Variant();
      var2.values.putAll(var0.values);
      var2.values.putAll(var1.values);
      return var2;
   }

   public JsonElement get() {
      JsonObject var1 = new JsonObject();
      this.values.values().forEach((var1x) -> {
         var1x.addToVariant(var1);
      });
      return var1;
   }

   public static JsonElement convertList(List<Variant> var0) {
      if (var0.size() == 1) {
         return ((Variant)var0.get(0)).get();
      } else {
         JsonArray var1 = new JsonArray();
         var0.forEach((var1x) -> {
            var1.add(var1x.get());
         });
         return var1;
      }
   }

   // $FF: synthetic method
   public Object get() {
      return this.get();
   }
}
