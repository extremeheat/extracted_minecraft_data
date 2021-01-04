package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPart implements UnbakedModel {
   private final StateDefinition<Block, BlockState> definition;
   private final List<Selector> selectors;

   public MultiPart(StateDefinition<Block, BlockState> var1, List<Selector> var2) {
      super();
      this.definition = var1;
      this.selectors = var2;
   }

   public List<Selector> getSelectors() {
      return this.selectors;
   }

   public Set<MultiVariant> getMultiVariants() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.selectors.iterator();

      while(var2.hasNext()) {
         Selector var3 = (Selector)var2.next();
         var1.add(var3.getVariant());
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MultiPart)) {
         return false;
      } else {
         MultiPart var2 = (MultiPart)var1;
         return Objects.equals(this.definition, var2.definition) && Objects.equals(this.selectors, var2.selectors);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.definition, this.selectors});
   }

   public Collection<ResourceLocation> getDependencies() {
      return (Collection)this.getSelectors().stream().flatMap((var0) -> {
         return var0.getVariant().getDependencies().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> getTextures(Function<ResourceLocation, UnbakedModel> var1, Set<String> var2) {
      return (Collection)this.getSelectors().stream().flatMap((var2x) -> {
         return var2x.getVariant().getTextures(var1, var2).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelState var3) {
      MultiPartBakedModel.Builder var4 = new MultiPartBakedModel.Builder();
      Iterator var5 = this.getSelectors().iterator();

      while(var5.hasNext()) {
         Selector var6 = (Selector)var5.next();
         BakedModel var7 = var6.getVariant().bake(var1, var2, var3);
         if (var7 != null) {
            var4.add(var6.getPredicate(this.definition), var7);
         }
      }

      return var4.build();
   }

   public static class Deserializer implements JsonDeserializer<MultiPart> {
      private final BlockModelDefinition.Context context;

      public Deserializer(BlockModelDefinition.Context var1) {
         super();
         this.context = var1;
      }

      public MultiPart deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new MultiPart(this.context.getDefinition(), this.getSelectors(var3, var1.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext var1, JsonArray var2) {
         ArrayList var3 = Lists.newArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            JsonElement var5 = (JsonElement)var4.next();
            var3.add(var1.deserialize(var5, Selector.class));
         }

         return var3;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
