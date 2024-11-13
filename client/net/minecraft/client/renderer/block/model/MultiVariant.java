package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;

public record MultiVariant(List<Variant> variants) implements UnbakedBlockStateModel {
   public MultiVariant(List<Variant> var1) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Variant list must contain at least one element");
      } else {
         this.variants = var1;
      }
   }

   public Object visualEqualityGroup(BlockState var1) {
      return this;
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
      this.variants.forEach((var1x) -> var1.resolve(var1x.modelLocation()));
   }

   public BakedModel bake(ModelBaker var1) {
      if (this.variants.size() == 1) {
         Variant var6 = (Variant)this.variants.getFirst();
         return var1.bake(var6.modelLocation(), var6);
      } else {
         SimpleWeightedRandomList.Builder var2 = SimpleWeightedRandomList.builder();

         for(Variant var4 : this.variants) {
            BakedModel var5 = var1.bake(var4.modelLocation(), var4);
            var2.add(var5, var4.weight());
         }

         return new WeightedBakedModel(var2.build());
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
            if (var5.isEmpty()) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement var7 : var5) {
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
