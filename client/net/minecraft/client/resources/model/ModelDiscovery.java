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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

public class ModelDiscovery {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final String INVENTORY_MODEL_PREFIX = "item/";
   private final Map<ResourceLocation, UnbakedModel> inputModels;
   final UnbakedModel missingModel;
   private final Map<ModelResourceLocation, UnbakedModel> topModels = new HashMap();
   private final Map<ResourceLocation, UnbakedModel> referencedModels = new HashMap();

   public ModelDiscovery(Map<ResourceLocation, UnbakedModel> var1, UnbakedModel var2) {
      super();
      this.inputModels = var1;
      this.missingModel = var2;
      this.registerTopModel(MissingBlockModel.VARIANT, var2);
      this.referencedModels.put(MissingBlockModel.LOCATION, var2);
   }

   private static Set<ModelResourceLocation> listMandatoryModels() {
      HashSet var0 = new HashSet();
      BuiltInRegistries.ITEM.listElements().forEach((var1) -> {
         ResourceLocation var2 = (ResourceLocation)((Item)var1.value()).components().get(DataComponents.ITEM_MODEL);
         if (var2 != null) {
            var0.add(ModelResourceLocation.inventory(var2));
         }

         Object var4 = var1.value();
         if (var4 instanceof BundleItem var3) {
            var0.add(ModelResourceLocation.inventory(var3.openFrontModel()));
            var0.add(ModelResourceLocation.inventory(var3.openBackModel()));
         }

      });
      var0.add(ItemRenderer.TRIDENT_MODEL);
      var0.add(ItemRenderer.SPYGLASS_MODEL);
      return var0;
   }

   private void registerTopModel(ModelResourceLocation var1, UnbakedModel var2) {
      this.topModels.put(var1, var2);
   }

   public void registerStandardModels(BlockStateModelLoader.LoadedModels var1) {
      this.referencedModels.put(SpecialModels.BUILTIN_GENERATED, SpecialModels.GENERATED_MARKER);
      this.referencedModels.put(SpecialModels.BUILTIN_BLOCK_ENTITY, SpecialModels.BLOCK_ENTITY_MARKER);
      Set var2 = listMandatoryModels();
      var1.models().forEach((var2x, var3) -> {
         this.registerTopModel(var2x, var3.model());
         var2.remove(var2x);
      });
      this.inputModels.keySet().forEach((var2x) -> {
         if (var2x.getPath().startsWith("item/")) {
            ModelResourceLocation var3 = ModelResourceLocation.inventory(var2x.withPath((var0) -> {
               return var0.substring("item/".length());
            }));
            this.registerTopModel(var3, new ItemModel(var2x));
            var2.remove(var3);
         }

      });
      if (!var2.isEmpty()) {
         LOGGER.warn("Missing mandatory models: {}", var2.stream().map((var0) -> {
            return "\n\t" + String.valueOf(var0);
         }).collect(Collectors.joining()));
      }

   }

   public void discoverDependencies() {
      this.topModels.values().forEach((var1) -> {
         var1.resolveDependencies(new ResolverImpl());
      });
   }

   public Map<ModelResourceLocation, UnbakedModel> getTopModels() {
      return this.topModels;
   }

   public Map<ResourceLocation, UnbakedModel> getReferencedModels() {
      return this.referencedModels;
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

   class ResolverImpl implements UnbakedModel.Resolver {
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
