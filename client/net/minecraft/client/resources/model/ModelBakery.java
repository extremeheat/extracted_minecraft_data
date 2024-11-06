package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
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
   private final EntityModelSet entityModelSet;
   final Map<BakedCacheKey, BakedModel> bakedCache = new HashMap();
   private final Map<ModelResourceLocation, UnbakedBlockStateModel> unbakedBlockStateModels;
   private final Map<ResourceLocation, ItemModel.Unbaked> unbakedItemStackModels;
   final Map<ResourceLocation, UnbakedModel> unbakedPlainModels;
   final UnbakedModel missingModel;

   public ModelBakery(EntityModelSet var1, Map<ModelResourceLocation, UnbakedBlockStateModel> var2, Map<ResourceLocation, ItemModel.Unbaked> var3, Map<ResourceLocation, UnbakedModel> var4, UnbakedModel var5) {
      super();
      this.entityModelSet = var1;
      this.unbakedBlockStateModels = var2;
      this.unbakedItemStackModels = var3;
      this.unbakedPlainModels = var4;
      this.missingModel = var5;
   }

   public BakingResult bakeModels(TextureGetter var1) {
      BakedModel var2 = UnbakedModel.bakeWithTopModelValues(this.missingModel, new ModelBakerImpl(var1, () -> {
         return "missing";
      }), BlockModelRotation.X0_Y0);
      HashMap var3 = new HashMap(this.unbakedBlockStateModels.size());
      this.unbakedBlockStateModels.forEach((var3x, var4x) -> {
         try {
            Objects.requireNonNull(var3x);
            BakedModel var5 = var4x.bake(new ModelBakerImpl(var1, var3x::toString));
            var3.put(var3x, var5);
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake model: '{}': {}", var3x, var6);
         }

      });
      MissingItemModel var4 = new MissingItemModel(var2);
      HashMap var5 = new HashMap(this.unbakedItemStackModels.size());
      this.unbakedItemStackModels.forEach((var4x, var5x) -> {
         ModelDebugName var6 = () -> {
            return String.valueOf(var4x) + "#inventory";
         };
         ModelBakerImpl var7 = new ModelBakerImpl(var1, var6);
         ItemModel.BakingContext var8 = new ItemModel.BakingContext(var7, this.entityModelSet, var4);

         try {
            ItemModel var9 = var5x.bake(var8);
            var5.put(var4x, var9);
         } catch (Exception var10) {
            LOGGER.warn("Unable to bake item model: '{}'", var4x, var10);
         }

      });
      return new BakingResult(var2, var3, var4, var5);
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
   }

   private class ModelBakerImpl implements ModelBaker {
      private final ModelDebugName rootName;
      private final SpriteGetter modelTextureGetter;

      ModelBakerImpl(final TextureGetter var2, final ModelDebugName var3) {
         super();
         this.modelTextureGetter = var2.bind(var3);
         this.rootName = var3;
      }

      public SpriteGetter sprites() {
         return this.modelTextureGetter;
      }

      private UnbakedModel getModel(ResourceLocation var1) {
         UnbakedModel var2 = (UnbakedModel)ModelBakery.this.unbakedPlainModels.get(var1);
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
            BakedModel var6 = UnbakedModel.bakeWithTopModelValues(var5, this, var2);
            ModelBakery.this.bakedCache.put(var3, var6);
            return var6;
         }
      }

      public ModelDebugName rootName() {
         return this.rootName;
      }
   }

   public interface TextureGetter {
      TextureAtlasSprite get(ModelDebugName var1, Material var2);

      TextureAtlasSprite reportMissingReference(ModelDebugName var1, String var2);

      default SpriteGetter bind(final ModelDebugName var1) {
         return new SpriteGetter() {
            public TextureAtlasSprite get(Material var1x) {
               return TextureGetter.this.get(var1, var1x);
            }

            public TextureAtlasSprite reportMissingReference(String var1x) {
               return TextureGetter.this.reportMissingReference(var1, var1x);
            }
         };
      }
   }

   public static record BakingResult(BakedModel missingModel, Map<ModelResourceLocation, BakedModel> blockStateModels, ItemModel missingItemModel, Map<ResourceLocation, ItemModel> itemStackModels) {
      public BakingResult(BakedModel var1, Map<ModelResourceLocation, BakedModel> var2, ItemModel var3, Map<ResourceLocation, ItemModel> var4) {
         super();
         this.missingModel = var1;
         this.blockStateModels = var2;
         this.missingItemModel = var3;
         this.itemStackModels = var4;
      }

      public BakedModel missingModel() {
         return this.missingModel;
      }

      public Map<ModelResourceLocation, BakedModel> blockStateModels() {
         return this.blockStateModels;
      }

      public ItemModel missingItemModel() {
         return this.missingItemModel;
      }

      public Map<ResourceLocation, ItemModel> itemStackModels() {
         return this.itemStackModels;
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
