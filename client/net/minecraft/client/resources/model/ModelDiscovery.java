package net.minecraft.client.resources.model;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ModelDiscovery {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ResourceLocation, UnbakedModel> inputModels;
   final UnbakedModel missingModel;
   private final List<ResolvableModel> topModels = new ArrayList();
   private final Map<ResourceLocation, UnbakedModel> referencedModels = new HashMap();

   public ModelDiscovery(Map<ResourceLocation, UnbakedModel> var1, UnbakedModel var2) {
      super();
      this.inputModels = var1;
      this.missingModel = var2;
      this.referencedModels.put(MissingBlockModel.LOCATION, var2);
   }

   public void registerSpecialModels() {
      this.referencedModels.put(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
   }

   public void addRoot(ResolvableModel var1) {
      this.topModels.add(var1);
   }

   public void discoverDependencies() {
      this.topModels.forEach((var1) -> {
         var1.resolveDependencies(new ResolverImpl());
      });
   }

   public Map<ResourceLocation, UnbakedModel> getReferencedModels() {
      return this.referencedModels;
   }

   public Set<ResourceLocation> getUnreferencedModels() {
      return Sets.difference(this.inputModels.keySet(), this.referencedModels.keySet());
   }

   UnbakedModel getBlockModel(ResourceLocation var1) {
      return (UnbakedModel)this.referencedModels.computeIfAbsent(var1, this::loadBlockModel);
   }

   private UnbakedModel loadBlockModel(ResourceLocation var1) {
      UnbakedModel var2 = (UnbakedModel)this.inputModels.get(var1);
      if (var2 == null) {
         LOGGER.warn("Missing block model: '{}'", var1);
         return this.missingModel;
      } else {
         return var2;
      }
   }

   class ResolverImpl implements ResolvableModel.Resolver {
      private final List<ResourceLocation> stack = new ArrayList();
      private final Set<ResourceLocation> resolvedModels = new HashSet();

      ResolverImpl() {
         super();
      }

      public UnbakedModel resolve(ResourceLocation var1) {
         if (this.stack.contains(var1)) {
            ModelDiscovery.LOGGER.warn("Detected model loading loop: {}->{}", this.stacktraceToString(), var1);
            return ModelDiscovery.this.missingModel;
         } else {
            UnbakedModel var2 = ModelDiscovery.this.getBlockModel(var1);
            if (this.resolvedModels.add(var1)) {
               this.stack.add(var1);
               var2.resolveDependencies(this);
               this.stack.remove(var1);
            }

            return var2;
         }
      }

      private String stacktraceToString() {
         return (String)this.stack.stream().map(ResourceLocation::toString).collect(Collectors.joining("->"));
      }
   }
}
