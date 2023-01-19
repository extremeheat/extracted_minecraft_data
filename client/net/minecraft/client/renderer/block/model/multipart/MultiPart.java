package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import net.minecraft.client.resources.model.Material;
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

      for(Selector var3 : this.selectors) {
         var1.add(var3.getVariant());
      }

      return var1;
   }

   @Override
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

   @Override
   public int hashCode() {
      return Objects.hash(this.definition, this.selectors);
   }

   @Override
   public Collection<ResourceLocation> getDependencies() {
      return this.getSelectors().stream().flatMap(var0 -> var0.getVariant().getDependencies().stream()).collect(Collectors.toSet());
   }

   @Override
   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> var1, Set<Pair<String, String>> var2) {
      return this.getSelectors().stream().flatMap(var2x -> var2x.getVariant().getMaterials(var1, var2).stream()).collect(Collectors.toSet());
   }

   @Nullable
   @Override
   public BakedModel bake(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4) {
      MultiPartBakedModel.Builder var5 = new MultiPartBakedModel.Builder();

      for(Selector var7 : this.getSelectors()) {
         BakedModel var8 = var7.getVariant().bake(var1, var2, var3, var4);
         if (var8 != null) {
            var5.add(var7.getPredicate(this.definition), var8);
         }
      }

      return var5.build();
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

         for(JsonElement var5 : var2) {
            var3.add((Selector)var1.deserialize(var5, Selector.class));
         }

         return var3;
      }
   }
}
