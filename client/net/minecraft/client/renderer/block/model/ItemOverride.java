package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ItemOverride {
   private final ResourceLocation model;
   private final List<ItemOverride.Predicate> predicates;

   public ItemOverride(ResourceLocation var1, List<ItemOverride.Predicate> var2) {
      super();
      this.model = var1;
      this.predicates = ImmutableList.copyOf(var2);
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public Stream<ItemOverride.Predicate> getPredicates() {
      return this.predicates.stream();
   }

   protected static class Deserializer implements JsonDeserializer<ItemOverride> {
      protected Deserializer() {
         super();
      }

      public ItemOverride deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = ResourceLocation.parse(GsonHelper.getAsString(var4, "model"));
         List var6 = this.getPredicates(var4);
         return new ItemOverride(var5, var6);
      }

      protected List<ItemOverride.Predicate> getPredicates(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(var1, "predicate");

         for (Entry var5 : var3.entrySet()) {
            var2.put(ResourceLocation.parse((String)var5.getKey()), GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return var2.entrySet()
            .stream()
            .map(var0 -> new ItemOverride.Predicate((ResourceLocation)var0.getKey(), (Float)var0.getValue()))
            .collect(ImmutableList.toImmutableList());
      }
   }

   public static class Predicate {
      private final ResourceLocation property;
      private final float value;

      public Predicate(ResourceLocation var1, float var2) {
         super();
         this.property = var1;
         this.value = var2;
      }

      public ResourceLocation getProperty() {
         return this.property;
      }

      public float getValue() {
         return this.value;
      }
   }
}
