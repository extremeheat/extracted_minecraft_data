package net.minecraft.client.resources.model;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3fc;
import org.slf4j.Logger;

public class ModelBakery {
   public static final Material FIRE_0;
   public static final Material FIRE_1;
   public static final Material LAVA_STILL;
   public static final Material LAVA_FLOW;
   public static final Material WATER_STILL;
   public static final Material WATER_FLOW;
   public static final Material WATER_OVERLAY;
   public static final Material BANNER_BASE;
   public static final Material SHIELD_BASE;
   public static final Material NO_PATTERN_SHIELD;
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<Identifier> DESTROY_STAGES;
   public static final List<Identifier> BREAKING_LOCATIONS;
   public static final List<RenderType> DESTROY_TYPES;
   static final Logger LOGGER;
   private final EntityModelSet entityModelSet;
   private final MaterialSet materials;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels;
   private final Map<Identifier, ClientItem> clientInfos;
   final Map<Identifier, ResolvedModel> resolvedModels;
   final ResolvedModel missingModel;

   public ModelBakery(EntityModelSet var1, MaterialSet var2, PlayerSkinRenderCache var3, Map<BlockState, BlockStateModel.UnbakedRoot> var4, Map<Identifier, ClientItem> var5, Map<Identifier, ResolvedModel> var6, ResolvedModel var7) {
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
      PartCacheImpl var3 = new PartCacheImpl();
      MissingModels var4 = ModelBakery.MissingModels.bake(this.missingModel, var1, var3);
      ModelBakerImpl var5 = new ModelBakerImpl(var1, var3, var4);
      CompletableFuture var6 = ParallelMapTransform.schedule(this.unbakedBlockStateModels, (var1x, var2x) -> {
         try {
            return var2x.bake(var1x, var5);
         } catch (Exception var4) {
            LOGGER.warn("Unable to bake model: '{}': {}", var1x, var4);
            return null;
         }
      }, var2);
      CompletableFuture var7 = ParallelMapTransform.schedule(this.clientInfos, (var3x, var4x) -> {
         try {
            return var4x.model().bake(new ItemModel.BakingContext(var5, this.entityModelSet, this.materials, this.playerSkinRenderCache, var4.item, var4x.registrySwapper()));
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake item model: '{}'", var3x, var6);
            return null;
         }
      }, var2);
      HashMap var8 = new HashMap(this.clientInfos.size());
      this.clientInfos.forEach((var1x, var2x) -> {
         ClientItem.Properties var3 = var2x.properties();
         if (!var3.equals(ClientItem.Properties.DEFAULT)) {
            var8.put(var1x, var3);
         }

      });
      return var6.thenCombine(var7, (var2x, var3x) -> new BakingResult(var4, var2x, var3x, var8));
   }

   static {
      FIRE_0 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_0");
      FIRE_1 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_1");
      LAVA_STILL = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_still");
      LAVA_FLOW = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_flow");
      WATER_STILL = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_still");
      WATER_FLOW = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_flow");
      WATER_OVERLAY = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("water_overlay");
      BANNER_BASE = new Material(Sheets.BANNER_SHEET, Identifier.withDefaultNamespace("entity/banner_base"));
      SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, Identifier.withDefaultNamespace("entity/shield_base"));
      NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, Identifier.withDefaultNamespace("entity/shield_base_nopattern"));
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((var0) -> Identifier.withDefaultNamespace("block/destroy_stage_" + var0)).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((var0) -> var0.withPath((UnaryOperator)((var0x) -> "textures/" + var0x + ".png"))).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderTypes::crumbling).collect(Collectors.toList());
      LOGGER = LogUtils.getLogger();
   }

   public static record MissingModels(BlockModelPart blockPart, BlockStateModel block, ItemModel item) {
      final BlockModelPart blockPart;
      final ItemModel item;

      public MissingModels(BlockModelPart var1, BlockStateModel var2, ItemModel var3) {
         super();
         this.blockPart = var1;
         this.block = var2;
         this.item = var3;
      }

      public static MissingModels bake(ResolvedModel var0, final SpriteGetter var1, final ModelBaker.PartCache var2) {
         ModelBaker var3 = new ModelBaker() {
            public ResolvedModel getModel(Identifier var1x) {
               throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(var1x));
            }

            public BlockModelPart missingBlockModelPart() {
               throw new IllegalStateException();
            }

            public <T> T compute(ModelBaker.SharedOperationKey<T> var1x) {
               return (T)var1x.compute(this);
            }

            public SpriteGetter sprites() {
               return var1;
            }

            public ModelBaker.PartCache parts() {
               return var2;
            }
         };
         TextureSlots var4 = var0.getTopTextureSlots();
         boolean var5 = var0.getTopAmbientOcclusion();
         boolean var6 = var0.getTopGuiLight().lightLikeBlock();
         ItemTransforms var7 = var0.getTopTransforms();
         QuadCollection var8 = var0.bakeTopGeometry(var4, var3, BlockModelRotation.IDENTITY);
         TextureAtlasSprite var9 = var0.resolveParticleSprite(var4, var3);
         SimpleModelWrapper var10 = new SimpleModelWrapper(var8, var5, var9);
         SingleVariant var11 = new SingleVariant(var10);
         MissingItemModel var12 = new MissingItemModel(var8.getAll(), new ModelRenderProperties(var6, var9, var7));
         return new MissingModels(var10, var11, var12);
      }
   }

   class ModelBakerImpl implements ModelBaker {
      private final SpriteGetter sprites;
      private final ModelBaker.PartCache parts;
      private final MissingModels missingModels;
      private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache = new ConcurrentHashMap();
      private final Function<ModelBaker.SharedOperationKey<Object>, Object> cacheComputeFunction = (var1x) -> var1x.compute(this);

      ModelBakerImpl(final SpriteGetter var2, final ModelBaker.PartCache var3, final MissingModels var4) {
         super();
         this.sprites = var2;
         this.parts = var3;
         this.missingModels = var4;
      }

      public BlockModelPart missingBlockModelPart() {
         return this.missingModels.blockPart;
      }

      public SpriteGetter sprites() {
         return this.sprites;
      }

      public ModelBaker.PartCache parts() {
         return this.parts;
      }

      public ResolvedModel getModel(Identifier var1) {
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

   public static record BakingResult(MissingModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ClientItem.Properties> itemProperties) {
      public BakingResult(MissingModels var1, Map<BlockState, BlockStateModel> var2, Map<Identifier, ItemModel> var3, Map<Identifier, ClientItem.Properties> var4) {
         super();
         this.missingModels = var1;
         this.blockStateModels = var2;
         this.itemStackModels = var3;
         this.itemProperties = var4;
      }
   }

   static class PartCacheImpl implements ModelBaker.PartCache {
      private final Interner<Vector3fc> vectors = Interners.newStrongInterner();

      PartCacheImpl() {
         super();
      }

      public Vector3fc vector(Vector3fc var1) {
         return (Vector3fc)this.vectors.intern(var1);
      }
   }
}
