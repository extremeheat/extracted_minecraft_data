package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
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
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String BUILTIN_SLASH = "builtin/";
   private static final String BUILTIN_SLASH_GENERATED = "builtin/generated";
   private static final String BUILTIN_BLOCK_ENTITY = "builtin/entity";
   private static final String MISSING_MODEL_NAME = "missing";
   public static final ResourceLocation MISSING_MODEL_LOCATION = ResourceLocation.withDefaultNamespace("builtin/missing");
   public static final ModelResourceLocation MISSING_MODEL_VARIANT = new ModelResourceLocation(MISSING_MODEL_LOCATION, "missing");
   public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '"
         + MissingTextureAtlasSprite.getLocation().getPath()
         + "',       'missingno': '"
         + MissingTextureAtlasSprite.getLocation().getPath()
         + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
      .replace('\'', '"');
   private static final Map<String, String> BUILTIN_MODELS = Map.of("missing", MISSING_MODEL_MESH);
   public static final BlockModel GENERATION_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), var0 -> var0.name = "generation marker");
   public static final BlockModel BLOCK_ENTITY_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), var0 -> var0.name = "block entity marker");
   static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private final Map<ResourceLocation, BlockModel> modelResources;
   private final Set<ResourceLocation> loadingStack = new HashSet<>();
   private final Map<ResourceLocation, UnbakedModel> unbakedCache = new HashMap<>();
   final Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache = new HashMap<>();
   private final Map<ModelResourceLocation, UnbakedModel> topLevelModels = new HashMap<>();
   private final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels = new HashMap<>();
   private final UnbakedModel missingModel;
   private final Object2IntMap<BlockState> modelGroups;

   public ModelBakery(
      BlockColors var1, ProfilerFiller var2, Map<ResourceLocation, BlockModel> var3, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> var4
   ) {
      super();
      this.modelResources = var3;
      var2.push("missing_model");

      try {
         this.missingModel = this.loadBlockModel(MISSING_MODEL_LOCATION);
         this.registerModel(MISSING_MODEL_VARIANT, this.missingModel);
      } catch (IOException var8) {
         LOGGER.error("Error loading missing model, should never happen :(", var8);
         throw new RuntimeException(var8);
      }

      BlockStateModelLoader var5 = new BlockStateModelLoader(var4, var2, this.missingModel, var1, this::registerModelAndLoadDependencies);
      var5.loadAllBlockStates();
      this.modelGroups = var5.getModelGroups();
      var2.popPush("items");

      for (ResourceLocation var7 : BuiltInRegistries.ITEM.keySet()) {
         this.loadItemModelAndDependencies(var7);
      }

      var2.popPush("special");
      this.loadSpecialItemModelAndDependencies(ItemRenderer.TRIDENT_IN_HAND_MODEL);
      this.loadSpecialItemModelAndDependencies(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
      this.topLevelModels.values().forEach(var1x -> var1x.resolveParents(this::getModel));
      var2.pop();
   }

   public void bakeModels(ModelBakery.TextureGetter var1) {
      this.topLevelModels.forEach((var2, var3) -> {
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

   UnbakedModel getModel(ResourceLocation var1) {
      if (this.unbakedCache.containsKey(var1)) {
         return this.unbakedCache.get(var1);
      } else if (this.loadingStack.contains(var1)) {
         throw new IllegalStateException("Circular reference while loading " + var1);
      } else {
         this.loadingStack.add(var1);

         while (!this.loadingStack.isEmpty()) {
            ResourceLocation var2 = this.loadingStack.iterator().next();

            try {
               if (!this.unbakedCache.containsKey(var2)) {
                  BlockModel var3 = this.loadBlockModel(var2);
                  this.unbakedCache.put(var2, var3);
                  this.loadingStack.addAll(var3.getDependencies());
               }
            } catch (Exception var7) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", new Object[]{var2, var1, var7});
               this.unbakedCache.put(var2, this.missingModel);
            } finally {
               this.loadingStack.remove(var2);
            }
         }

         return this.unbakedCache.getOrDefault(var1, this.missingModel);
      }
   }

   private void loadItemModelAndDependencies(ResourceLocation var1) {
      ModelResourceLocation var2 = ModelResourceLocation.inventory(var1);
      ResourceLocation var3 = var1.withPrefix("item/");
      UnbakedModel var4 = this.getModel(var3);
      this.registerModelAndLoadDependencies(var2, var4);
   }

   private void loadSpecialItemModelAndDependencies(ModelResourceLocation var1) {
      ResourceLocation var2 = var1.id().withPrefix("item/");
      UnbakedModel var3 = this.getModel(var2);
      this.registerModelAndLoadDependencies(var1, var3);
   }

   private void registerModelAndLoadDependencies(ModelResourceLocation var1, UnbakedModel var2) {
      for (ResourceLocation var4 : var2.getDependencies()) {
         this.getModel(var4);
      }

      this.registerModel(var1, var2);
   }

   private void registerModel(ModelResourceLocation var1, UnbakedModel var2) {
      this.topLevelModels.put(var1, var2);
   }

   private BlockModel loadBlockModel(ResourceLocation var1) throws IOException {
      String var2 = var1.getPath();
      if ("builtin/generated".equals(var2)) {
         return GENERATION_MARKER;
      } else if ("builtin/entity".equals(var2)) {
         return BLOCK_ENTITY_MARKER;
      } else if (var2.startsWith("builtin/")) {
         String var7 = var2.substring("builtin/".length());
         String var8 = BUILTIN_MODELS.get(var7);
         if (var8 == null) {
            throw new FileNotFoundException(var1.toString());
         } else {
            StringReader var5 = new StringReader(var8);
            BlockModel var6 = BlockModel.fromStream(var5);
            var6.name = var1.toString();
            return var6;
         }
      } else {
         ResourceLocation var3 = MODEL_LISTER.idToFile(var1);
         BlockModel var4 = this.modelResources.get(var3);
         if (var4 == null) {
            throw new FileNotFoundException(var3.toString());
         } else {
            var4.name = var1.toString();
            return var4;
         }
      }
   }

   public Map<ModelResourceLocation, BakedModel> getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
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
         return ModelBakery.this.getModel(var1);
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
         if (var1 instanceof BlockModel var3 && var3.getRootModel() == ModelBakery.GENERATION_MARKER) {
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
