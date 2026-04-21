package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.block.BlockModelSet;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.LoadedBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private Map<Identifier, ItemModel> bakedItemStackModels = Map.of();
   private Map<Identifier, ClientItem.Properties> itemProperties = Map.of();
   private final AtlasManager atlasManager;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final BlockColors blockColors;
   private EntityModelSet entityModelSet;
   private ModelBakery.MissingModels missingModels;
   private @Nullable BlockStateModelSet blockStateModelSet;
   private @Nullable BlockModelSet blockModelSet;
   private @Nullable FluidStateModelSet fluidStateModelSet;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(final BlockColors blockColors, final AtlasManager atlasManager, final PlayerSkinRenderCache playerSkinRenderCache) {
      super();
      this.entityModelSet = EntityModelSet.EMPTY;
      this.modelGroups = Object2IntMaps.emptyMap();
      this.blockColors = blockColors;
      this.atlasManager = atlasManager;
      this.playerSkinRenderCache = playerSkinRenderCache;
   }

   public ItemModel getItemModel(final Identifier id) {
      return (ItemModel)this.bakedItemStackModels.getOrDefault(id, this.missingModels.item());
   }

   public ClientItem.Properties getItemProperties(final Identifier id) {
      return (ClientItem.Properties)this.itemProperties.getOrDefault(id, ClientItem.Properties.DEFAULT);
   }

   public BlockStateModelSet getBlockStateModelSet() {
      return (BlockStateModelSet)Objects.requireNonNull(this.blockStateModelSet, "Block models not yet initialized");
   }

   public BlockModelSet getBlockModelSet() {
      return (BlockModelSet)Objects.requireNonNull(this.blockModelSet, "Block models not yet initialized");
   }

   public FluidStateModelSet getFluidStateModelSet() {
      return (FluidStateModelSet)Objects.requireNonNull(this.fluidStateModelSet, "Fluid models not yet initialized");
   }

   public final CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture<EntityModelSet> entityModelSet = CompletableFuture.supplyAsync(EntityModelSet::vanilla, taskExecutor);
      CompletableFuture<Map<Identifier, UnbakedModel>> modelCache = loadBlockModels(manager, taskExecutor);
      CompletableFuture<BlockStateModelLoader.LoadedModels> blockStateModels = BlockStateModelLoader.loadBlockStates(manager, taskExecutor);
      CompletableFuture<Map<BlockState, BlockModel.Unbaked>> blockModelContents = CompletableFuture.supplyAsync(() -> BuiltInBlockModels.createBlockModels(this.blockColors), taskExecutor);
      CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> itemStackModels = ClientItemInfoLoader.scheduleLoad(manager, taskExecutor);
      CompletableFuture<ResolvedModels> modelDiscovery = CompletableFuture.allOf(modelCache, blockStateModels, itemStackModels).thenApplyAsync((var3) -> discoverModelDependencies((Map)modelCache.join(), (BlockStateModelLoader.LoadedModels)blockStateModels.join(), (ClientItemInfoLoader.LoadedClientInfos)itemStackModels.join()), taskExecutor);
      CompletableFuture<Object2IntMap<BlockState>> modelGroups = blockStateModels.thenApplyAsync((models) -> buildModelGroups(this.blockColors, models), taskExecutor);
      AtlasManager.PendingStitchResults pendingStitches = (AtlasManager.PendingStitchResults)currentReload.get(AtlasManager.PENDING_STITCH);
      CompletableFuture<SpriteLoader.Preparations> pendingBlockAtlasSprites = pendingStitches.get(AtlasIds.BLOCKS);
      CompletableFuture<SpriteLoader.Preparations> pendingItemAtlasSprites = pendingStitches.get(AtlasIds.ITEMS);
      CompletableFuture<LoadedBlockModels> blockModels = CompletableFuture.allOf(blockModelContents, entityModelSet).thenApply((var3) -> new LoadedBlockModels((Map)blockModelContents.join(), (EntityModelSet)entityModelSet.join(), this.atlasManager, this.playerSkinRenderCache));
      CompletableFuture var10000 = CompletableFuture.allOf(pendingBlockAtlasSprites, pendingItemAtlasSprites, modelDiscovery, modelGroups, blockStateModels, itemStackModels, entityModelSet, blockModels, modelCache).thenComposeAsync((var11) -> {
         SpriteLoader.Preparations blockAtlasSprites = (SpriteLoader.Preparations)pendingBlockAtlasSprites.join();
         SpriteLoader.Preparations itemAtlasSprites = (SpriteLoader.Preparations)pendingItemAtlasSprites.join();
         ResolvedModels resolvedModels = (ResolvedModels)modelDiscovery.join();
         Object2IntMap<BlockState> groups = (Object2IntMap)modelGroups.join();
         Set<Identifier> unreferencedModels = Sets.difference(((Map)modelCache.join()).keySet(), resolvedModels.models.keySet());
         if (!unreferencedModels.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", unreferencedModels.stream().sorted().map((modelId) -> "\t" + String.valueOf(modelId) + "\n").collect(Collectors.joining()));
         }

         ModelBakery bakery = new ModelBakery((EntityModelSet)entityModelSet.join(), this.atlasManager, this.playerSkinRenderCache, ((BlockStateModelLoader.LoadedModels)blockStateModels.join()).models(), ((ClientItemInfoLoader.LoadedClientInfos)itemStackModels.join()).contents(), resolvedModels.models(), resolvedModels.missing());
         return loadModels(blockAtlasSprites, itemAtlasSprites, bakery, (LoadedBlockModels)blockModels.join(), groups, (EntityModelSet)entityModelSet.join(), taskExecutor);
      }, taskExecutor);
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync(this::apply, reloadExecutor);
   }

   private static CompletableFuture<Map<Identifier, UnbakedModel>> loadBlockModels(final ResourceManager manager, final Executor executor) {
      return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources(manager), executor).thenCompose((resources) -> {
         List<CompletableFuture<Pair<Identifier, CuboidModel>>> result = new ArrayList(resources.size());

         for(Map.Entry<Identifier, Resource> resource : resources.entrySet()) {
            result.add(CompletableFuture.supplyAsync(() -> {
               Identifier modelId = MODEL_LISTER.fileToId((Identifier)resource.getKey());

               try {
                  Reader reader = ((Resource)resource.getValue()).openAsReader();

                  Pair var3;
                  try {
                     var3 = Pair.of(modelId, CuboidModel.fromStream(reader));
                  } catch (Throwable var6) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable x2) {
                           var6.addSuppressed(x2);
                        }
                     }

                     throw var6;
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return var3;
               } catch (Exception e) {
                  LOGGER.error("Failed to load model {}", resource.getKey(), e);
                  return null;
               }
            }, executor));
         }

         return Util.sequence(result).thenApply((pairs) -> (Map)pairs.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
      });
   }

   private static ResolvedModels discoverModelDependencies(final Map<Identifier, UnbakedModel> allModels, final BlockStateModelLoader.LoadedModels blockStateModels, final ClientItemInfoLoader.LoadedClientInfos itemInfos) {
      try (Zone ignored = Profiler.get().zone("dependencies")) {
         ModelDiscovery result = new ModelDiscovery(allModels, MissingCuboidModel.missingModel());
         result.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
         Collection var10000 = blockStateModels.models().values();
         Objects.requireNonNull(result);
         var10000.forEach(result::addRoot);
         itemInfos.contents().values().forEach((info) -> result.addRoot(info.model()));
         return new ResolvedModels(result.missingModel(), result.resolve());
      }
   }

   private static CompletableFuture<ReloadState> loadModels(final SpriteLoader.Preparations blockAtlas, final SpriteLoader.Preparations itemAtlas, final ModelBakery bakery, final LoadedBlockModels blockModels, final Object2IntMap<BlockState> modelGroups, final EntityModelSet entityModelSet, final Executor taskExecutor) {
      MaterialBakerImpl materialBaker = new MaterialBakerImpl(blockAtlas, itemAtlas);
      CompletableFuture<ModelBakery.BakingResult> bakedStateResults = bakery.bakeModels(materialBaker, taskExecutor);
      CompletableFuture<Map<BlockState, BlockModel>> bakedModelsFuture = bakedStateResults.thenCompose((bakingResult) -> {
         Objects.requireNonNull(bakingResult);
         return blockModels.bake(bakingResult::getBlockStateModel, bakingResult.missingModels().block(), taskExecutor);
      });
      return bakedStateResults.thenCombine(bakedModelsFuture, (bakingResult, bakedModels) -> {
         Map<Fluid, FluidModel> fluidModels = FluidStateModelSet.bake(materialBaker);
         materialBaker.logMissingTextures();
         Map<BlockState, BlockStateModel> modelByStateCache = createBlockStateToModelDispatch(bakingResult.blockStateModels(), bakingResult.missingModels().block());
         return new ReloadState(bakingResult, modelGroups, modelByStateCache, bakedModels, fluidModels, entityModelSet);
      });
   }

   private static Map<BlockState, BlockStateModel> createBlockStateToModelDispatch(final Map<BlockState, BlockStateModel> bakedModels, final BlockStateModel missingModel) {
      try (Zone ignored = Profiler.get().zone("block state dispatch")) {
         Map<BlockState, BlockStateModel> modelByStateCache = new IdentityHashMap(bakedModels);

         for(Block block : BuiltInRegistries.BLOCK) {
            block.getStateDefinition().getPossibleStates().forEach((state) -> {
               if (bakedModels.putIfAbsent(state, missingModel) == null) {
                  LOGGER.warn("Missing model for variant: '{}'", state);
               }

            });
         }

         return modelByStateCache;
      }
   }

   private static Object2IntMap<BlockState> buildModelGroups(final BlockColors blockColors, final BlockStateModelLoader.LoadedModels blockStateModels) {
      try (Zone ignored = Profiler.get().zone("block groups")) {
         return ModelGroupCollector.build(blockColors, blockStateModels);
      }
   }

   private void apply(final ReloadState preparations) {
      ModelBakery.BakingResult bakedModels = preparations.bakedModels;
      this.bakedItemStackModels = bakedModels.itemStackModels();
      this.itemProperties = bakedModels.itemProperties();
      this.modelGroups = preparations.modelGroups;
      this.missingModels = bakedModels.missingModels();
      this.blockStateModelSet = new BlockStateModelSet(preparations.blockStateModels, this.missingModels.block());
      this.blockModelSet = new BlockModelSet(this.blockStateModelSet, preparations.blockModels, this.blockColors);
      this.fluidStateModelSet = new FluidStateModelSet(preparations.fluidModels, this.missingModels.fluid());
      this.entityModelSet = preparations.entityModelSet;
   }

   public boolean requiresRender(final BlockState oldState, final BlockState newState) {
      if (oldState == newState) {
         return false;
      } else {
         int oldModelGroup = this.modelGroups.getInt(oldState);
         if (oldModelGroup != -1) {
            int newModelGroup = this.modelGroups.getInt(newState);
            if (oldModelGroup == newModelGroup) {
               FluidState oldFluidState = oldState.getFluidState();
               FluidState newFluidState = newState.getFluidState();
               return oldFluidState != newFluidState;
            }
         }

         return true;
      }
   }

   public Supplier<EntityModelSet> entityModels() {
      return () -> this.entityModelSet;
   }

   private static record ResolvedModels(ResolvedModel missing, Map<Identifier, ResolvedModel> models) {
      private ResolvedModels {
         super();
      }
   }

   private static record ReloadState(ModelBakery.BakingResult bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BlockStateModel> blockStateModels, Map<BlockState, BlockModel> blockModels, Map<Fluid, FluidModel> fluidModels, EntityModelSet entityModelSet) {
      private ReloadState {
         super();
      }
   }

   private static class MaterialBakerImpl implements MaterialBaker {
      private final SpriteLoader.Preparations blockAtlas;
      private final SpriteLoader.Preparations itemAtlas;
      private final Material.Baked blockMissing;
      private final Multimap<String, Identifier> missingSprites = Multimaps.synchronizedMultimap(HashMultimap.create());
      private final Multimap<String, String> missingReferences = Multimaps.synchronizedMultimap(HashMultimap.create());
      private final Map<Material, @Nullable Material.Baked> bakedMaterials = new ConcurrentHashMap();
      private final Function<Material, @Nullable Material.Baked> bakerFunction = this::bake;

      public MaterialBakerImpl(final SpriteLoader.Preparations blockAtlas, final SpriteLoader.Preparations itemAtlas) {
         super();
         this.blockAtlas = blockAtlas;
         this.itemAtlas = itemAtlas;
         this.blockMissing = new Material.Baked(blockAtlas.missing(), false);
      }

      public Material.Baked get(final Material material, final ModelDebugName name) {
         Material.Baked baked = (Material.Baked)this.bakedMaterials.computeIfAbsent(material, this.bakerFunction);
         if (baked == null) {
            this.missingSprites.put(name.debugName(), material.sprite());
            return this.blockMissing;
         } else {
            return baked;
         }
      }

      private Material.@Nullable Baked bake(final Material material) {
         Material.Baked itemMaterial = this.bakeForAtlas(material, this.itemAtlas);
         return itemMaterial != null ? itemMaterial : this.bakeForAtlas(material, this.blockAtlas);
      }

      private Material.@Nullable Baked bakeForAtlas(final Material material, final SpriteLoader.Preparations atlas) {
         TextureAtlasSprite sprite = atlas.getSprite(material.sprite());
         return sprite != null ? new Material.Baked(sprite, material.forceTranslucent()) : null;
      }

      public Material.Baked reportMissingReference(final String reference, final ModelDebugName responsibleModel) {
         this.missingReferences.put(responsibleModel.debugName(), reference);
         return this.blockMissing;
      }

      public void logMissingTextures() {
         this.missingSprites.asMap().forEach((location, sprites) -> ModelManager.LOGGER.warn("Missing textures in model {}:\n{}", location, sprites.stream().sorted().map((sprite) -> "    " + String.valueOf(sprite)).collect(Collectors.joining("\n"))));
         this.missingReferences.asMap().forEach((location, references) -> ModelManager.LOGGER.warn("Missing texture references in model {}:\n{}", location, references.stream().sorted().map((reference) -> "    " + reference).collect(Collectors.joining("\n"))));
      }
   }
}
