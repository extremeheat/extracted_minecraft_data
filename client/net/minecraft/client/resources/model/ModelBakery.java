package net.minecraft.client.resources.model;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.slf4j.Logger;

public class ModelBakery {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final SpriteId FIRE_0;
   public static final SpriteId FIRE_1;
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<Identifier> DESTROY_STAGES;
   public static final List<Identifier> BREAKING_LOCATIONS;
   public static final List<RenderType> DESTROY_TYPES;
   private static final Matrix4fc IDENTITY;
   private final EntityModelSet entityModelSet;
   private final SpriteGetter sprites;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels;
   private final Map<Identifier, ClientItem> clientInfos;
   private final Map<Identifier, ResolvedModel> resolvedModels;
   private final ResolvedModel missingModel;

   public ModelBakery(final EntityModelSet entityModelSet, final SpriteGetter sprites, final PlayerSkinRenderCache playerSkinRenderCache, final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels, final Map<Identifier, ClientItem> clientInfos, final Map<Identifier, ResolvedModel> resolvedModels, final ResolvedModel missingModel) {
      super();
      this.entityModelSet = entityModelSet;
      this.sprites = sprites;
      this.playerSkinRenderCache = playerSkinRenderCache;
      this.unbakedBlockStateModels = unbakedBlockStateModels;
      this.clientInfos = clientInfos;
      this.resolvedModels = resolvedModels;
      this.missingModel = missingModel;
   }

   public CompletableFuture<BakingResult> bakeModels(final MaterialBaker materials, final Executor taskExecutor) {
      InternerImpl interner = new InternerImpl();
      MissingModels missingModels = ModelBakery.MissingModels.bake(this.missingModel, materials, interner);
      ModelBakerImpl baker = new ModelBakerImpl(materials, interner, missingModels);
      CompletableFuture<Map<BlockState, BlockStateModel>> bakedBlockStateModelFuture = ParallelMapTransform.schedule(this.unbakedBlockStateModels, (blockState, model) -> {
         try {
            return model.bake(blockState, baker);
         } catch (Exception e) {
            LOGGER.warn("Unable to bake model: '{}': {}", blockState, e);
            return null;
         }
      }, taskExecutor);
      CompletableFuture<Map<Identifier, ItemModel>> bakedItemStackModelFuture = ParallelMapTransform.schedule(this.clientInfos, (location, clientInfo) -> {
         try {
            return clientInfo.model().bake(new ItemModel.BakingContext(baker, this.entityModelSet, this.sprites, this.playerSkinRenderCache, missingModels.item, clientInfo.registrySwapper()), IDENTITY);
         } catch (Exception e) {
            LOGGER.warn("Unable to bake item model: '{}'", location, e);
            return null;
         }
      }, taskExecutor);
      Map<Identifier, ClientItem.Properties> itemStackModelProperties = new HashMap(this.clientInfos.size());
      this.clientInfos.forEach((id, clientInfo) -> {
         ClientItem.Properties properties = clientInfo.properties();
         if (!properties.equals(ClientItem.Properties.DEFAULT)) {
            itemStackModelProperties.put(id, properties);
         }

      });
      return bakedBlockStateModelFuture.thenCombine(bakedItemStackModelFuture, (bakedBlockStateModels, bakedItemStateModels) -> new BakingResult(missingModels, bakedBlockStateModels, bakedItemStateModels, itemStackModelProperties));
   }

   static {
      FIRE_0 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_0");
      FIRE_1 = Sheets.BLOCKS_MAPPER.defaultNamespaceApply("fire_1");
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((i) -> Identifier.withDefaultNamespace("block/destroy_stage_" + i)).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((location) -> location.withPath((UnaryOperator)((path) -> "textures/" + path + ".png"))).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderTypes::crumbling).collect(Collectors.toList());
      IDENTITY = new Matrix4f();
   }

   public static record MissingModels(BlockStateModelPart blockPart, BlockStateModel block, MissingItemModel item, FluidModel fluid) {
      public MissingModels {
         super();
      }

      public static MissingModels bake(final ResolvedModel unbaked, final MaterialBaker materials, final ModelBaker.Interner interner) {
         ModelBaker missingModelBakery = new ModelBaker() {
            public ResolvedModel getModel(final Identifier location) {
               throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(location));
            }

            public BlockStateModelPart missingBlockModelPart() {
               throw new IllegalStateException();
            }

            public <T> T compute(final ModelBaker.SharedOperationKey<T> key) {
               return key.compute(this);
            }

            public MaterialBaker materials() {
               return materials;
            }

            public ModelBaker.Interner interner() {
               return interner;
            }
         };
         TextureSlots textureSlots = unbaked.getTopTextureSlots();
         boolean hasAmbientOcclusion = unbaked.getTopAmbientOcclusion();
         boolean usesBlockLight = unbaked.getTopGuiLight().lightLikeBlock();
         ItemTransforms transforms = unbaked.getTopTransforms();
         QuadCollection geometry = unbaked.bakeTopGeometry(textureSlots, missingModelBakery, BlockModelRotation.IDENTITY);
         Multimap<Identifier, Identifier> forbiddenSprites = SimpleModelWrapper.findNonBlockSprites(geometry);
         if (forbiddenSprites != null) {
            throw new IllegalStateException("Missing block contains sprites from outside of block atlas: " + String.valueOf(forbiddenSprites));
         } else {
            Material.Baked particleMaterial = unbaked.resolveParticleMaterial(textureSlots, missingModelBakery);
            SimpleModelWrapper missingModelPart = new SimpleModelWrapper(geometry, hasAmbientOcclusion, particleMaterial);
            BlockStateModel bakedBlockModel = new SingleVariant(missingModelPart);
            MissingItemModel bakedItemModel = new MissingItemModel(geometry.getAll(), new ModelRenderProperties(usesBlockLight, particleMaterial, transforms));
            FluidModel bakedFluidModel = new FluidModel(ChunkSectionLayer.SOLID, particleMaterial, particleMaterial, (Material.Baked)null, (BlockTintSource)null);
            return new MissingModels(missingModelPart, bakedBlockModel, bakedItemModel, bakedFluidModel);
         }
      }
   }

   private class ModelBakerImpl implements ModelBaker {
      private final MaterialBaker materials;
      private final ModelBaker.Interner interner;
      private final MissingModels missingModels;
      private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache;
      private final Function<ModelBaker.SharedOperationKey<Object>, Object> cacheComputeFunction;

      private ModelBakerImpl(final MaterialBaker materials, final ModelBaker.Interner interner, final MissingModels missingModels) {
         Objects.requireNonNull(ModelBakery.this);
         super();
         this.operationCache = new ConcurrentHashMap();
         this.cacheComputeFunction = (k) -> k.compute(this);
         this.materials = materials;
         this.interner = interner;
         this.missingModels = missingModels;
      }

      public BlockStateModelPart missingBlockModelPart() {
         return this.missingModels.blockPart;
      }

      public MaterialBaker materials() {
         return this.materials;
      }

      public ModelBaker.Interner interner() {
         return this.interner;
      }

      public ResolvedModel getModel(final Identifier location) {
         ResolvedModel result = (ResolvedModel)ModelBakery.this.resolvedModels.get(location);
         if (result == null) {
            ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", location);
            return ModelBakery.this.missingModel;
         } else {
            return result;
         }
      }

      public <T> T compute(final ModelBaker.SharedOperationKey<T> key) {
         return (T)this.operationCache.computeIfAbsent(key, this.cacheComputeFunction);
      }
   }

   public static record BakingResult(MissingModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ClientItem.Properties> itemProperties) {
      public BakingResult {
         super();
      }

      public BlockStateModel getBlockStateModel(final BlockState blockState) {
         return (BlockStateModel)this.blockStateModels.getOrDefault(blockState, this.missingModels.block);
      }
   }

   private static class InternerImpl implements ModelBaker.Interner {
      private final Interner<Vector3fc> vectors = Interners.newStrongInterner();
      private final Interner<BakedQuad.MaterialInfo> materialInfos = Interners.newStrongInterner();

      private InternerImpl() {
         super();
      }

      public Vector3fc vector(final Vector3fc v) {
         return (Vector3fc)this.vectors.intern(v);
      }

      public BakedQuad.MaterialInfo materialInfo(final BakedQuad.MaterialInfo material) {
         return (BakedQuad.MaterialInfo)this.materialInfos.intern(material);
      }
   }
}
