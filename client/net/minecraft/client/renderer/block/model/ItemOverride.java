package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record ItemOverride(ResourceLocation model, List<Predicate> predicates) {
   public ItemOverride(ResourceLocation var1, List<Predicate> var2) {
      super();
      var2 = List.copyOf(var2);
      this.model = var1;
      this.predicates = var2;
   }

   public ResourceLocation model() {
      return this.model;
   }

   public List<Predicate> predicates() {
      return this.predicates;
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

      protected List<Predicate> getPredicates(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(var1, "predicate");
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            var2.put(ResourceLocation.parse((String)var5.getKey()), GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return (List)var2.entrySet().stream().map((var0) -> {
            return new Predicate((ResourceLocation)var0.getKey(), (Float)var0.getValue());
         }).collect(ImmutableList.toImmutableList());
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static record Predicate(ResourceLocation property, float value) {
      public Predicate(ResourceLocation var1, float var2) {
         super();
         this.property = var1;
         this.value = var2;
      }

      public ResourceLocation property() {
         return this.property;
      }

      public float value() {
         return this.value;
      }
   }
}
