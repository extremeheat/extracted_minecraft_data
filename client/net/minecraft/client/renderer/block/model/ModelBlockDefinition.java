package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition {
   static final Gson field_178333_a = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer()).create();
   private final Map<String, ModelBlockDefinition.Variants> field_178332_b = Maps.newHashMap();

   public static ModelBlockDefinition func_178331_a(Reader var0) {
      return (ModelBlockDefinition)field_178333_a.fromJson(var0, ModelBlockDefinition.class);
   }

   public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> var1) {
      super();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ModelBlockDefinition.Variants var3 = (ModelBlockDefinition.Variants)var2.next();
         this.field_178332_b.put(var3.field_178423_a, var3);
      }

   }

   public ModelBlockDefinition(List<ModelBlockDefinition> var1) {
      super();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ModelBlockDefinition var3 = (ModelBlockDefinition)var2.next();
         this.field_178332_b.putAll(var3.field_178332_b);
      }

   }

   public ModelBlockDefinition.Variants func_178330_b(String var1) {
      ModelBlockDefinition.Variants var2 = (ModelBlockDefinition.Variants)this.field_178332_b.get(var1);
      if (var2 == null) {
         throw new ModelBlockDefinition.MissingVariantException();
      } else {
         return var2;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ModelBlockDefinition) {
         ModelBlockDefinition var2 = (ModelBlockDefinition)var1;
         return this.field_178332_b.equals(var2.field_178332_b);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_178332_b.hashCode();
   }

   public class MissingVariantException extends RuntimeException {
      protected MissingVariantException() {
         super();
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      public Deserializer() {
         super();
      }

      public ModelBlockDefinition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.func_178334_a(var3, var4);
         return new ModelBlockDefinition(var5);
      }

      protected List<ModelBlockDefinition.Variants> func_178334_a(JsonDeserializationContext var1, JsonObject var2) {
         JsonObject var3 = JsonUtils.func_152754_s(var2, "variants");
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            var4.add(this.func_178335_a(var1, var6));
         }

         return var4;
      }

      protected ModelBlockDefinition.Variants func_178335_a(JsonDeserializationContext var1, Entry<String, JsonElement> var2) {
         String var3 = (String)var2.getKey();
         ArrayList var4 = Lists.newArrayList();
         JsonElement var5 = (JsonElement)var2.getValue();
         if (var5.isJsonArray()) {
            Iterator var6 = var5.getAsJsonArray().iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               var4.add((ModelBlockDefinition.Variant)var1.deserialize(var7, ModelBlockDefinition.Variant.class));
            }
         } else {
            var4.add((ModelBlockDefinition.Variant)var1.deserialize(var5, ModelBlockDefinition.Variant.class));
         }

         return new ModelBlockDefinition.Variants(var3, var4);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Variant {
      private final ResourceLocation field_178437_a;
      private final ModelRotation field_178435_b;
      private final boolean field_178436_c;
      private final int field_178434_d;

      public Variant(ResourceLocation var1, ModelRotation var2, boolean var3, int var4) {
         super();
         this.field_178437_a = var1;
         this.field_178435_b = var2;
         this.field_178436_c = var3;
         this.field_178434_d = var4;
      }

      public ResourceLocation func_178431_a() {
         return this.field_178437_a;
      }

      public ModelRotation func_178432_b() {
         return this.field_178435_b;
      }

      public boolean func_178433_c() {
         return this.field_178436_c;
      }

      public int func_178430_d() {
         return this.field_178434_d;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ModelBlockDefinition.Variant)) {
            return false;
         } else {
            ModelBlockDefinition.Variant var2 = (ModelBlockDefinition.Variant)var1;
            return this.field_178437_a.equals(var2.field_178437_a) && this.field_178435_b == var2.field_178435_b && this.field_178436_c == var2.field_178436_c;
         }
      }

      public int hashCode() {
         int var1 = this.field_178437_a.hashCode();
         var1 = 31 * var1 + (this.field_178435_b != null ? this.field_178435_b.hashCode() : 0);
         var1 = 31 * var1 + (this.field_178436_c ? 1 : 0);
         return var1;
      }

      public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
         public Deserializer() {
            super();
         }

         public ModelBlockDefinition.Variant deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = var1.getAsJsonObject();
            String var5 = this.func_178424_b(var4);
            ModelRotation var6 = this.func_178428_a(var4);
            boolean var7 = this.func_178429_d(var4);
            int var8 = this.func_178427_c(var4);
            return new ModelBlockDefinition.Variant(this.func_178426_a(var5), var6, var7, var8);
         }

         private ResourceLocation func_178426_a(String var1) {
            ResourceLocation var2 = new ResourceLocation(var1);
            var2 = new ResourceLocation(var2.func_110624_b(), "block/" + var2.func_110623_a());
            return var2;
         }

         private boolean func_178429_d(JsonObject var1) {
            return JsonUtils.func_151209_a(var1, "uvlock", false);
         }

         protected ModelRotation func_178428_a(JsonObject var1) {
            int var2 = JsonUtils.func_151208_a(var1, "x", 0);
            int var3 = JsonUtils.func_151208_a(var1, "y", 0);
            ModelRotation var4 = ModelRotation.func_177524_a(var2, var3);
            if (var4 == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + var2 + ", y: " + var3);
            } else {
               return var4;
            }
         }

         protected String func_178424_b(JsonObject var1) {
            return JsonUtils.func_151200_h(var1, "model");
         }

         protected int func_178427_c(JsonObject var1) {
            return JsonUtils.func_151208_a(var1, "weight", 1);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }

   public static class Variants {
      private final String field_178423_a;
      private final List<ModelBlockDefinition.Variant> field_178422_b;

      public Variants(String var1, List<ModelBlockDefinition.Variant> var2) {
         super();
         this.field_178423_a = var1;
         this.field_178422_b = var2;
      }

      public List<ModelBlockDefinition.Variant> func_178420_b() {
         return this.field_178422_b;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ModelBlockDefinition.Variants)) {
            return false;
         } else {
            ModelBlockDefinition.Variants var2 = (ModelBlockDefinition.Variants)var1;
            if (!this.field_178423_a.equals(var2.field_178423_a)) {
               return false;
            } else {
               return this.field_178422_b.equals(var2.field_178422_b);
            }
         }
      }

      public int hashCode() {
         int var1 = this.field_178423_a.hashCode();
         var1 = 31 * var1 + this.field_178422_b.hashCode();
         return var1;
      }
   }
}
