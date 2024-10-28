package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;

public class MultiVariant implements UnbakedModel {
   private final List<Variant> variants;

   public MultiVariant(List<Variant> var1) {
      super();
      this.variants = var1;
   }

   public List<Variant> getVariants() {
      return this.variants;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof MultiVariant) {
         MultiVariant var2 = (MultiVariant)var1;
         return this.variants.equals(var2.variants);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variants.hashCode();
   }

   public Collection<ResourceLocation> getDependencies() {
      return (Collection)this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public void resolveParents(Function<ResourceLocation, UnbakedModel> var1) {
      this.getVariants().stream().map(Variant::getModelLocation).distinct().forEach((var1x) -> {
         ((UnbakedModel)var1.apply(var1x)).resolveParents(var1);
      });
   }

   @Nullable
   public BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3) {
      if (this.getVariants().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder var4 = new WeightedBakedModel.Builder();
         Iterator var5 = this.getVariants().iterator();

         while(var5.hasNext()) {
            Variant var6 = (Variant)var5.next();
            BakedModel var7 = var1.bake(var6.getModelLocation(), var6);
            var4.add(var7, var6.getWeight());
         }

         return var4.build();
      }
   }

   public static class Deserializer implements JsonDeserializer<MultiVariant> {
      public Deserializer() {
         super();
      }

      public MultiVariant deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         ArrayList var4 = Lists.newArrayList();
         if (var1.isJsonArray()) {
            JsonArray var5 = var1.getAsJsonArray();
            if (var5.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               var4.add((Variant)var3.deserialize(var7, Variant.class));
            }
         } else {
            var4.add((Variant)var3.deserialize(var1, Variant.class));
         }

         return new MultiVariant(var4);
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
