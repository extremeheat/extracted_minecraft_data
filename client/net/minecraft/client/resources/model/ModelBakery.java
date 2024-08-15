package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ModelBakery {
   public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
   public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
   public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
   public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
   public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
   public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
   public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
   public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10)
      .mapToObj(var0 -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + var0))
      .collect(Collectors.toList());
   public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream()
      .map(var0 -> var0.withPath(var0x -> "textures/" + var0x + ".png"))
      .collect(Collectors.toList());
   public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
   static final Logger LOGGER = LogUtils.getLogger();
   static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   final Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache = new HashMap<>();
   private final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels = new HashMap<>();
   private final Map<ModelResourceLocation, UnbakedModel> topModels;
   final Map<ResourceLocation, UnbakedModel> unbakedModels;
   final UnbakedModel missingModel;

   public ModelBakery(Map<ModelResourceLocation, UnbakedModel> var1, Map<ResourceLocation, UnbakedModel> var2, UnbakedModel var3) {
      super();
      this.topModels = var1;
      this.unbakedModels = var2;
      this.missingModel = var3;
   }

   public void bakeModels(ModelBakery.TextureGetter var1) {
      this.topModels.forEach((var2, var3) -> {
         BakedModel var4 = null;

         try {
            var4 = new ModelBakery.ModelBakerImpl(var1, var2).bakeUncached(var3, BlockModelRotation.X0_Y0);
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   class ModelBakerImpl implements ModelBaker {
      private final Function<Material, TextureAtlasSprite> modelTextureGetter;

      ModelBakerImpl(final ModelBakery.TextureGetter nullx, final ModelResourceLocation nullxx) {
         super();
         this.modelTextureGetter = var2 -> nullx.get(nullxx, var2);
      }

      @Override
      public UnbakedModel getModel(ResourceLocation var1) {
         UnbakedModel var2 = ModelBakery.this.unbakedModels.get(var1);
         if (var2 == null) {
            ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", var1);
            return ModelBakery.this.missingModel;
         } else {
            return var2;
         }
      }

      @Override
      public BakedModel bake(ResourceLocation var1, ModelState var2) {
         ModelBakery.BakedCacheKey var3 = new ModelBakery.BakedCacheKey(var1, var2.getRotation(), var2.isUvLocked());
         BakedModel var4 = ModelBakery.this.bakedCache.get(var3);
         if (var4 != null) {
            return var4;
         } else {
            UnbakedModel var5 = this.getModel(var1);
            BakedModel var6 = this.bakeUncached(var5, var2);
            ModelBakery.this.bakedCache.put(var3, var6);
            return var6;
         }
      }

      @Nullable
      BakedModel bakeUncached(UnbakedModel var1, ModelState var2) {
         if (var1 instanceof BlockModel var3 && var3.getRootModel() == SpecialModels.GENERATED_MARKER) {
            return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(this.modelTextureGetter, var3).bake(this, var3, this.modelTextureGetter, var2, false);
         }

         return var1.bake(this, this.modelTextureGetter, var2);
      }
   }

   @FunctionalInterface
   public interface TextureGetter {
      TextureAtlasSprite get(ModelResourceLocation var1, Material var2);
   }
}
