package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;

public class ModelDiscovery {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ResourceLocation, UnbakedModel> inputModels;
   final UnbakedModel missingModel;
   private final Map<ModelResourceLocation, UnbakedModel> topModels = new HashMap<>();
   private final Map<ResourceLocation, UnbakedModel> referencedModels = new HashMap<>();

   public ModelDiscovery(Map<ResourceLocation, UnbakedModel> var1, UnbakedModel var2) {
      super();
      this.inputModels = var1;
      this.missingModel = var2;
      this.registerTopModel(MissingBlockModel.VARIANT, var2);
      this.referencedModels.put(MissingBlockModel.LOCATION, var2);
   }

   private void registerItemTopModel(ResourceLocation var1) {
      ModelResourceLocation var2 = ModelResourceLocation.inventory(var1);
      ResourceLocation var3 = var1.withPrefix("item/");
      UnbakedModel var4 = this.getBlockModel(var3);
      this.registerTopModel(var2, var4);
   }

   private void registerSpecialItemTopModel(ModelResourceLocation var1) {
      ResourceLocation var2 = var1.id().withPrefix("item/");
      UnbakedModel var3 = this.getBlockModel(var2);
      this.registerTopModel(var1, var3);
   }

   private void registerTopModel(ModelResourceLocation var1, UnbakedModel var2) {
      this.topModels.put(var1, var2);
   }

   public void registerStandardModels(BlockStateModelLoader.LoadedModels var1) {
      this.referencedModels.put(SpecialModels.BUILTIN_GENERATED, SpecialModels.GENERATED_MARKER);
      this.referencedModels.put(SpecialModels.BUILTIN_BLOCK_ENTITY, SpecialModels.BLOCK_ENTITY_MARKER);
      var1.models().forEach((var1x, var2) -> this.registerTopModel(var1x, var2.model()));

      for (ResourceLocation var3 : BuiltInRegistries.ITEM.keySet()) {
         this.registerItemTopModel(var3);
      }

      this.registerSpecialItemTopModel(ItemRenderer.TRIDENT_IN_HAND_MODEL);
      this.registerSpecialItemTopModel(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
      this.registerSpecialItemTopModel(ItemRenderer.getBundleOpenFrontModelLocation((BundleItem)Items.BUNDLE));
      this.registerSpecialItemTopModel(ItemRenderer.getBundleOpenBackModelLocation((BundleItem)Items.BUNDLE));
   }

   public void discoverDependencies() {
      this.topModels.values().forEach(var1 -> var1.resolveDependencies(new ModelDiscovery.ResolverImpl(), UnbakedModel.ResolutionContext.TOP));
   }

   public Map<ModelResourceLocation, UnbakedModel> getTopModels() {
      return this.topModels;
   }

   public Map<ResourceLocation, UnbakedModel> getReferencedModels() {
      return this.referencedModels;
   }

   UnbakedModel getBlockModel(ResourceLocation var1) {
      return this.referencedModels.computeIfAbsent(var1, this::loadBlockModel);
   }

   private UnbakedModel loadBlockModel(ResourceLocation var1) {
      UnbakedModel var2 = this.inputModels.get(var1);
      if (var2 == null) {
         LOGGER.warn("Missing block model: '{}'", var1);
         return this.missingModel;
      } else {
         return var2;
      }
   }

   class ResolverImpl implements UnbakedModel.Resolver {
      private final List<ResourceLocation> stack = new ArrayList<>();
      private final Set<ResourceLocation> resolvedModels = new HashSet<>();
      private UnbakedModel.ResolutionContext context = UnbakedModel.ResolutionContext.TOP;

      ResolverImpl() {
         super();
      }

      @Override
      public UnbakedModel resolve(ResourceLocation var1) {
         return this.resolve(var1, false);
      }

      @Override
      public UnbakedModel resolveForOverride(ResourceLocation var1) {
         if (this.context == UnbakedModel.ResolutionContext.OVERRIDE) {
            ModelDiscovery.LOGGER.warn("Re-entrant override in {}->{}", this.stacktraceToString(), var1);
         }

         this.context = UnbakedModel.ResolutionContext.OVERRIDE;
         UnbakedModel var2 = this.resolve(var1, true);
         this.context = UnbakedModel.ResolutionContext.TOP;
         return var2;
      }

      private boolean isReferenceRecursive(ResourceLocation var1, boolean var2) {
         if (this.stack.isEmpty()) {
            return false;
         } else if (!this.stack.contains(var1)) {
            return false;
         } else if (var2) {
            ResourceLocation var3 = (ResourceLocation)this.stack.getLast();
            return !var3.equals(var1);
         } else {
            return true;
         }
      }

      private UnbakedModel resolve(ResourceLocation var1, boolean var2) {
         if (this.isReferenceRecursive(var1, var2)) {
            ModelDiscovery.LOGGER.warn("Detected model loading loop: {}->{}", this.stacktraceToString(), var1);
            return ModelDiscovery.this.missingModel;
         } else {
            UnbakedModel var3 = ModelDiscovery.this.getBlockModel(var1);
            if (this.resolvedModels.add(var1)) {
               this.stack.add(var1);
               var3.resolveDependencies(this, this.context);
               this.stack.remove(var1);
            }

            return var3;
         }
      }

      private String stacktraceToString() {
         return this.stack.stream().map(ResourceLocation::toString).collect(Collectors.joining("->"));
      }
   }
}
