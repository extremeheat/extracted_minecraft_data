package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ModelBakery {
   public static final Material FIRE_0;
   public static final Material FIRE_1;
   public static final Material LAVA_FLOW;
   public static final Material WATER_FLOW;
   public static final Material WATER_OVERLAY;
   public static final Material BANNER_BASE;
   public static final Material SHIELD_BASE;
   public static final Material NO_PATTERN_SHIELD;
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<ResourceLocation> DESTROY_STAGES;
   public static final List<ResourceLocation> BREAKING_LOCATIONS;
   public static final List<RenderType> DESTROY_TYPES;
   static final Logger LOGGER;
   static final ItemModelGenerator ITEM_MODEL_GENERATOR;
   final Map<BakedCacheKey, BakedModel> bakedCache = new HashMap();
   private final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels = new HashMap();
   private final Map<ModelResourceLocation, UnbakedModel> topModels;
   final Map<ResourceLocation, UnbakedModel> unbakedModels;
   final UnbakedModel missingModel;

   public ModelBakery(Map<ModelResourceLocation, UnbakedModel> var1, Map<ResourceLocation, UnbakedModel> var2, UnbakedModel var3) {
      super();
      this.topModels = var1;
      this.unbakedModels = var2;
      this.missingModel = var3;
   }

   public void bakeModels(TextureGetter var1) {
      this.topModels.forEach((var2, var3) -> {
         BakedModel var4 = null;

         try {
            var4 = (new ModelBakerImpl(var1, var2)).bakeUncached(var3, BlockModelRotation.X0_Y0);
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake model: '{}': {}", var2, var6);
         }

         if (var4 != null) {
            this.bakedTopLevelModels.put(var2, var4);
         }

      });
   }

   public Map<ModelResourceLocation, BakedModel> getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   static {
      FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
      FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
      LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
      WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
      WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
      BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
      SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
      NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((var0) -> {
         return ResourceLocation.withDefaultNamespace("block/destroy_stage_" + var0);
      }).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((var0) -> {
         return var0.withPath((var0x) -> {
            return "textures/" + var0x + ".png";
         });
      }).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
      LOGGER = LogUtils.getLogger();
      ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   }

   @FunctionalInterface
   public interface TextureGetter {
      TextureAtlasSprite get(ModelResourceLocation var1, Material var2);
   }

   private class ModelBakerImpl implements ModelBaker {
      private final Function<Material, TextureAtlasSprite> modelTextureGetter;

      ModelBakerImpl(final TextureGetter var2, final ModelResourceLocation var3) {
         super();
         this.modelTextureGetter = (var2x) -> {
            return var2.get(var3, var2x);
         };
      }

      private UnbakedModel getModel(ResourceLocation var1) {
         UnbakedModel var2 = (UnbakedModel)ModelBakery.this.unbakedModels.get(var1);
         if (var2 == null) {
            ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", var1);
            return ModelBakery.this.missingModel;
         } else {
            return var2;
         }
      }

      public BakedModel bake(ResourceLocation var1, ModelState var2) {
         BakedCacheKey var3 = new BakedCacheKey(var1, var2.getRotation(), var2.isUvLocked());
         BakedModel var4 = (BakedModel)ModelBakery.this.bakedCache.get(var3);
         if (var4 != null) {
            return var4;
         } else {
            UnbakedModel var5 = this.getModel(var1);
            BakedModel var6 = this.bakeUncached(var5, var2);
            ModelBakery.this.bakedCache.put(var3, var6);
            return var6;
         }
      }

      BakedModel bakeUncached(UnbakedModel var1, ModelState var2) {
         if (var1 instanceof BlockModel var3) {
            if (var3.getRootModel() == SpecialModels.GENERATED_MARKER) {
               return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(this.modelTextureGetter, var3).bake(this.modelTextureGetter, var2, false);
            }
         }

         return var1.bake(this, this.modelTextureGetter, var2);
      }
   }

   private static record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
      BakedCacheKey(ResourceLocation var1, Transformation var2, boolean var3) {
         super();
         this.id = var1;
         this.transformation = var2;
         this.isUvLocked = var3;
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Transformation transformation() {
         return this.transformation;
      }

      public boolean isUvLocked() {
         return this.isUvLocked;
      }
   }
}
