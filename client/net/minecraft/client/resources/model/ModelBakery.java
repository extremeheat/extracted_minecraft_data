package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
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
   private final MaterialSet materials;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels;
   private final Map<ResourceLocation, ClientItem> clientInfos;
   final Map<ResourceLocation, ResolvedModel> resolvedModels;
   final ResolvedModel missingModel;

   public ModelBakery(EntityModelSet var1, MaterialSet var2, PlayerSkinRenderCache var3, Map<BlockState, BlockStateModel.UnbakedRoot> var4, Map<ResourceLocation, ClientItem> var5, Map<ResourceLocation, ResolvedModel> var6, ResolvedModel var7) {
      super();
      this.entityModelSet = var1;
      this.materials = var2;
      this.playerSkinRenderCache = var3;
      this.unbakedBlockStateModels = var4;
      this.clientInfos = var5;
      this.resolvedModels = var6;
      this.missingModel = var7;
   }

   public CompletableFuture<BakingResult> bakeModels(SpriteGetter var1, Executor var2) {
      MissingModels var3 = ModelBakery.MissingModels.bake(this.missingModel, var1);
      ModelBakerImpl var4 = new ModelBakerImpl(var1);
      CompletableFuture var5 = ParallelMapTransform.schedule(this.unbakedBlockStateModels, (var1x, var2x) -> {
         try {
            return var2x.bake(var1x, var4);
         } catch (Exception var4x) {
            LOGGER.warn("Unable to bake model: '{}': {}", var1x, var4x);
            return null;
         }
      }, var2);
      CompletableFuture var6 = ParallelMapTransform.schedule(this.clientInfos, (var3x, var4x) -> {
         try {
            return var4x.model().bake(new ItemModel.BakingContext(var4, this.entityModelSet, this.materials, this.playerSkinRenderCache, var3.item, var4x.registrySwapper()));
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake item model: '{}'", var3x, var6);
            return null;
         }
      }, var2);
      HashMap var7 = new HashMap(this.clientInfos.size());
      this.clientInfos.forEach((var1x, var2x) -> {
         ClientItem.Properties var3 = var2x.properties();
         if (!var3.equals(ClientItem.Properties.DEFAULT)) {
            var7.put(var1x, var3);
         }

      });
      return var5.thenCombine(var6, (var2x, var3x) -> new BakingResult(var3, var2x, var3x, var7));
   }

   static {
      FIRE_0 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_0");
      FIRE_1 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_1");
      LAVA_FLOW = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_flow");
      WATER_FLOW = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_flow");
      WATER_OVERLAY = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_overlay");
      BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
      SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
      NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((var0) -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + var0)).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((var0) -> var0.withPath((UnaryOperator)((var0x) -> "textures/" + var0x + ".png"))).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
      LOGGER = LogUtils.getLogger();
   }

   public static record MissingModels(BlockStateModel block, ItemModel item) {
      final ItemModel item;

      public MissingModels(BlockStateModel var1, ItemModel var2) {
         super();
         this.block = var1;
         this.item = var2;
      }

      public static MissingModels bake(ResolvedModel var0, final SpriteGetter var1) {
         ModelBaker var2 = new ModelBaker() {
            public ResolvedModel getModel(ResourceLocation var1x) {
               throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(var1x));
            }

            public <T> T compute(ModelBaker.SharedOperationKey<T> var1x) {
               return (T)var1x.compute(this);
            }

            public SpriteGetter sprites() {
               return var1;
            }
         };
         TextureSlots var3 = var0.getTopTextureSlots();
         boolean var4 = var0.getTopAmbientOcclusion();
         boolean var5 = var0.getTopGuiLight().lightLikeBlock();
         ItemTransforms var6 = var0.getTopTransforms();
         QuadCollection var7 = var0.bakeTopGeometry(var3, var2, BlockModelRotation.IDENTITY);
         TextureAtlasSprite var8 = var0.resolveParticleSprite(var3, var2);
         SingleVariant var9 = new SingleVariant(new SimpleModelWrapper(var7, var4, var8));
         MissingItemModel var10 = new MissingItemModel(var7.getAll(), new ModelRenderProperties(var5, var8, var6));
         return new MissingModels(var9, var10);
      }
   }

   class ModelBakerImpl implements ModelBaker {
      private final SpriteGetter sprites;
      private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache = new ConcurrentHashMap();
      private final Function<ModelBaker.SharedOperationKey<Object>, Object> cacheComputeFunction = (var1x) -> var1x.compute(this);

      ModelBakerImpl(final SpriteGetter var2) {
         super();
         this.sprites = var2;
      }

      public SpriteGetter sprites() {
         return this.sprites;
      }

      public ResolvedModel getModel(ResourceLocation var1) {
         ResolvedModel var2 = (ResolvedModel)ModelBakery.this.resolvedModels.get(var1);
         if (var2 == null) {
            ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", var1);
            return ModelBakery.this.missingModel;
         } else {
            return var2;
         }
      }

      public <T> T compute(ModelBaker.SharedOperationKey<T> var1) {
         return (T)this.operationCache.computeIfAbsent(var1, this.cacheComputeFunction);
      }
   }

   public static record BakingResult(MissingModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<ResourceLocation, ItemModel> itemStackModels, Map<ResourceLocation, ClientItem.Properties> itemProperties) {
      public BakingResult(MissingModels var1, Map<BlockState, BlockStateModel> var2, Map<ResourceLocation, ItemModel> var3, Map<ResourceLocation, ClientItem.Properties> var4) {
         super();
         this.missingModels = var1;
         this.blockStateModels = var2;
         this.itemStackModels = var3;
         this.itemProperties = var4;
      }
   }
}
