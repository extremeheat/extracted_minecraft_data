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
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ItemOverride {
   private final ResourceLocation model;
   private final List<Predicate> predicates;

   public ItemOverride(ResourceLocation var1, List<Predicate> var2) {
      super();
      this.model = var1;
      this.predicates = ImmutableList.copyOf(var2);
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public Stream<Predicate> getPredicates() {
      return this.predicates.stream();
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

   protected static class Deserializer implements JsonDeserializer<ItemOverride> {
      protected Deserializer() {
         super();
      }

      public ItemOverride deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "model"));
         List var6 = this.getPredicates(var4);
         return new ItemOverride(var5, var6);
      }

      protected List<Predicate> getPredicates(JsonObject var1) {
         LinkedHashMap var2 = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(var1, "predicate");
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            var2.put(new ResourceLocation((String)var5.getKey()), GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey()));
         }

         return (List)var2.entrySet().stream().map((var0) -> {
            return new Predicate((ResourceLocation)var0.getKey(), (Float)var0.getValue());
         }).collect(ImmutableList.toImmutableList());
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
