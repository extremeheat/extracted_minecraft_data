package net.minecraft.client.renderer.block.model.multipart;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPart implements UnbakedBlockStateModel {
   private final List<InstantiatedSelector> selectors;

   MultiPart(List<InstantiatedSelector> var1) {
      super();
      this.selectors = var1;
   }

   public Object visualEqualityGroup(BlockState var1) {
      IntArrayList var2 = new IntArrayList();

      for(int var3 = 0; var3 < this.selectors.size(); ++var3) {
         if (((InstantiatedSelector)this.selectors.get(var3)).predicate.test(var1)) {
            var2.add(var3);
         }
      }

      record 1Key(MultiPart model, IntList selectors) {
         _Key/* $FF was: 1Key*/(MultiPart var1, IntList var2) {
            super();
            this.model = var1;
            this.selectors = var2;
         }
      }

      return new 1Key(this, var2);
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
      this.selectors.forEach((var1x) -> var1x.variant.resolveDependencies(var1));
   }

   public BakedModel bake(ModelBaker var1) {
      ArrayList var2 = new ArrayList(this.selectors.size());

      for(InstantiatedSelector var4 : this.selectors) {
         BakedModel var5 = var4.variant.bake(var1);
         var2.add(new MultiPartBakedModel.Selector(var4.predicate, var5));
      }

      return new MultiPartBakedModel(var2);
   }

   static record InstantiatedSelector(Predicate<BlockState> predicate, MultiVariant variant) {
      final Predicate<BlockState> predicate;
      final MultiVariant variant;

      InstantiatedSelector(Predicate<BlockState> var1, MultiVariant var2) {
         super();
         this.predicate = var1;
         this.variant = var2;
      }
   }

   public static record Definition(List<Selector> selectors) {
      public Definition(List<Selector> var1) {
         super();
         this.selectors = var1;
      }

      public MultiPart instantiate(StateDefinition<Block, BlockState> var1) {
         List var2 = this.selectors.stream().map((var1x) -> new InstantiatedSelector(var1x.getPredicate(var1), var1x.getVariant())).toList();
         return new MultiPart(var2);
      }

      public Set<MultiVariant> getMultiVariants() {
         return (Set)this.selectors.stream().map(Selector::getVariant).collect(Collectors.toSet());
      }
   }

   public static class Deserializer implements JsonDeserializer<Definition> {
      public Deserializer() {
         super();
      }

      public Definition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new Definition(this.getSelectors(var3, var1.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext var1, JsonArray var2) {
         ArrayList var3 = new ArrayList();
         if (var2.isEmpty()) {
            throw new JsonSyntaxException("Empty selector array");
         } else {
            for(JsonElement var5 : var2) {
               var3.add((Selector)var1.deserialize(var5, Selector.class));
            }

            return var3;
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
