package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
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

   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> var1, Set<Pair<String, String>> var2) {
      return (Collection)this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap((var2x) -> {
         return ((UnbakedModel)var1.apply(var2x)).getMaterials(var1, var2).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4) {
      if (this.getVariants().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder var5 = new WeightedBakedModel.Builder();
         Iterator var6 = this.getVariants().iterator();

         while(var6.hasNext()) {
            Variant var7 = (Variant)var6.next();
            BakedModel var8 = var1.bake(var7.getModelLocation(), var7);
            var5.add(var8, var7.getWeight());
         }

         return var5.build();
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
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
