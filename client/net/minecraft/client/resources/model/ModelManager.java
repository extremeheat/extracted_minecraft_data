package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener {
   public static final Identifier BLOCK_OR_ITEM = Identifier.withDefaultNamespace("block_or_item");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private Map<Identifier, ItemModel> bakedItemStackModels = Map.of();
   private Map<Identifier, ClientItem.Properties> itemProperties = Map.of();
   private final AtlasManager atlasManager;
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private EntityModelSet entityModelSet;
   private SpecialBlockModelRenderer specialBlockModelRenderer;
   private ModelBakery.MissingModels missingModels;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(BlockColors var1, AtlasManager var2, PlayerSkinRenderCache var3) {
      super();
      this.entityModelSet = EntityModelSet.EMPTY;
      this.specialBlockModelRenderer = SpecialBlockModelRenderer.EMPTY;
      this.modelGroups = Object2IntMaps.emptyMap();
      this.blockColors = var1;
      this.atlasManager = var2;
      this.playerSkinRenderCache = var3;
      this.blockModelShaper = new BlockModelShaper(this);
   }

   public BlockStateModel getMissingBlockStateModel() {
      return this.missingModels.block();
   }

   public ItemModel getItemModel(Identifier var1) {
      return (ItemModel)this.bakedItemStackModels.getOrDefault(var1, this.missingModels.item());
   }

   public ClientItem.Properties getItemProperties(Identifier var1) {
      return (ClientItem.Properties)this.itemProperties.getOrDefault(var1, ClientItem.Properties.DEFAULT);
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.SharedState var1, Executor var2, PreparableReloadListener.PreparationBarrier var3, Executor var4) {
      ResourceManager var5 = var1.resourceManager();
      CompletableFuture var6 = CompletableFuture.supplyAsync(EntityModelSet::vanilla, var2);
      CompletableFuture var7 = var6.thenApplyAsync((var1x) -> SpecialBlockModelRenderer.vanilla(new SpecialModelRenderer.BakingContext.Simple(var1x, this.atlasManager, this.playerSkinRenderCache)), var2);
      CompletableFuture var8 = loadBlockModels(var5, var2);
      CompletableFuture var9 = BlockStateModelLoader.loadBlockStates(var5, var2);
      CompletableFuture var10 = ClientItemInfoLoader.scheduleLoad(var5, var2);
      CompletableFuture var11 = CompletableFuture.allOf(var8, var9, var10).thenApplyAsync((var3x) -> discoverModelDependencies((Map)var8.join(), (BlockStateModelLoader.LoadedModels)var9.join(), (ClientItemInfoLoader.LoadedClientInfos)var10.join()), var2);
      CompletableFuture var12 = var9.thenApplyAsync((var1x) -> buildModelGroups(this.blockColors, var1x), var2);
      AtlasManager.PendingStitchResults var13 = (AtlasManager.PendingStitchResults)var1.get(AtlasManager.PENDING_STITCH);
      CompletableFuture var14 = var13.get(AtlasIds.BLOCKS);
      CompletableFuture var15 = var13.get(AtlasIds.ITEMS);
      CompletableFuture var10000 = CompletableFuture.allOf(var14, var15, var11, var12, var9, var10, var6, var7, var8).thenComposeAsync((var11x) -> {
         SpriteLoader.Preparations var12x = (SpriteLoader.Preparations)var14.join();
         SpriteLoader.Preparations var13 = (SpriteLoader.Preparations)var15.join();
         ResolvedModels var14x = (ResolvedModels)var11.join();
         Object2IntMap var15x = (Object2IntMap)var12.join();
         Sets.SetView var16 = Sets.difference(((Map)var8.join()).keySet(), var14x.models.keySet());
         if (!var16.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", var16.stream().sorted().map((var0) -> "\t" + String.valueOf(var0) + "\n").collect(Collectors.joining()));
         }

         ModelBakery var17 = new ModelBakery((EntityModelSet)var6.join(), this.atlasManager, this.playerSkinRenderCache, ((BlockStateModelLoader.LoadedModels)var9.join()).models(), ((ClientItemInfoLoader.LoadedClientInfos)var10.join()).contents(), var14x.models(), var14x.missing());
         return loadModels(var12x, var13, var17, var15x, (EntityModelSet)var6.join(), (SpecialBlockModelRenderer)var7.join(), var2);
      }, var2);
      Objects.requireNonNull(var3);
      return var10000.thenCompose(var3::wait).thenAcceptAsync(this::apply, var4);
   }

   private static CompletableFuture<Map<Identifier, UnbakedModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources(var0), var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());

         for(Map.Entry var4 : var1x.entrySet()) {
            var2.add(CompletableFuture.supplyAsync(() -> {
               Identifier var1 = MODEL_LISTER.fileToId((Identifier)var4.getKey());

               try {
                  BufferedReader var2 = ((Resource)var4.getValue()).openAsReader();

                  Pair var3;
                  try {
                     var3 = Pair.of(var1, BlockModel.fromStream(var2));
                  } catch (Throwable var6) {
                     if (var2 != null) {
                        try {
                           ((Reader)var2).close();
                        } catch (Throwable var5) {
                           var6.addSuppressed(var5);
                        }
                     }

                     throw var6;
                  }

                  if (var2 != null) {
                     ((Reader)var2).close();
                  }

                  return var3;
               } catch (Exception var7) {
                  LOGGER.error("Failed to load model {}", var4.getKey(), var7);
                  return null;
               }
            }, var1));
         }

         return Util.sequence(var2).thenApply((var0) -> (Map)var0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
      });
   }

   private static ResolvedModels discoverModelDependencies(Map<Identifier, UnbakedModel> var0, BlockStateModelLoader.LoadedModels var1, ClientItemInfoLoader.LoadedClientInfos var2) {
      try (Zone var3 = Profiler.get().zone("dependencies")) {
         ModelDiscovery var4 = new ModelDiscovery(var0, MissingBlockModel.missingModel());
         var4.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
         Collection var10000 = var1.models().values();
         Objects.requireNonNull(var4);
         var10000.forEach(var4::addRoot);
         var2.contents().values().forEach((var1x) -> var4.addRoot(var1x.model()));
         return new ResolvedModels(var4.missingModel(), var4.resolve());
      }
   }

   private static CompletableFuture<ReloadState> loadModels(final SpriteLoader.Preparations var0, final SpriteLoader.Preparations var1, ModelBakery var2, Object2IntMap<BlockState> var3, EntityModelSet var4, SpecialBlockModelRenderer var5, Executor var6) {
      final Multimap var7 = Multimaps.synchronizedMultimap(HashMultimap.create());
      final Multimap var8 = Multimaps.synchronizedMultimap(HashMultimap.create());
      return var2.bakeModels(new SpriteGetter() {
         private final TextureAtlasSprite blockMissing = var0.missing();
         private final TextureAtlasSprite itemMissing = var1.missing();

         public TextureAtlasSprite get(Material var1x, ModelDebugName var2) {
            Identifier var3 = var1x.atlasLocation();
            boolean var4 = var3.equals(ModelManager.BLOCK_OR_ITEM);
            boolean var5 = var3.equals(TextureAtlas.LOCATION_ITEMS);
            boolean var6 = var3.equals(TextureAtlas.LOCATION_BLOCKS);
            if (var4 || var5) {
               TextureAtlasSprite var7x = var1.getSprite(var1x.texture());
               if (var7x != null) {
                  return var7x;
               }
            }

            if (var4 || var6) {
               TextureAtlasSprite var8x = var0.getSprite(var1x.texture());
               if (var8x != null) {
                  return var8x;
               }
            }

            var7.put(var2.debugName(), var1x);
            return var5 ? this.itemMissing : this.blockMissing;
         }

         public TextureAtlasSprite reportMissingReference(String var1x, ModelDebugName var2) {
            var8.put(var2.debugName(), var1x);
            return this.blockMissing;
         }
      }, var6).thenApply((var5x) -> {
         var7.asMap().forEach((var0, var1) -> LOGGER.warn("Missing textures in model {}:\n{}", var0, var1.stream().sorted(Material.COMPARATOR).map((var0x) -> {
               String var10000 = String.valueOf(var0x.atlasLocation());
               return "    " + var10000 + ":" + String.valueOf(var0x.texture());
            }).collect(Collectors.joining("\n"))));
         var8.asMap().forEach((var0, var1) -> LOGGER.warn("Missing texture references in model {}:\n{}", var0, var1.stream().sorted().map((var0x) -> "    " + var0x).collect(Collectors.joining("\n"))));
         Map var6 = createBlockStateToModelDispatch(var5x.blockStateModels(), var5x.missingModels().block());
         return new ReloadState(var5x, var3, var6, var4, var5);
      });
   }

   private static Map<BlockState, BlockStateModel> createBlockStateToModelDispatch(Map<BlockState, BlockStateModel> var0, BlockStateModel var1) {
      try (Zone var2 = Profiler.get().zone("block state dispatch")) {
         IdentityHashMap var3 = new IdentityHashMap(var0);

         for(Block var5 : BuiltInRegistries.BLOCK) {
            var5.getStateDefinition().getPossibleStates().forEach((var2x) -> {
               if (var0.putIfAbsent(var2x, var1) == null) {
                  LOGGER.warn("Missing model for variant: '{}'", var2x);
               }

            });
         }

         return var3;
      }
   }

   private static Object2IntMap<BlockState> buildModelGroups(BlockColors var0, BlockStateModelLoader.LoadedModels var1) {
      try (Zone var2 = Profiler.get().zone("block groups")) {
         return ModelGroupCollector.build(var0, var1);
      }
   }

   private void apply(ReloadState var1) {
      ModelBakery.BakingResult var2 = var1.bakedModels;
      this.bakedItemStackModels = var2.itemStackModels();
      this.itemProperties = var2.itemProperties();
      this.modelGroups = var1.modelGroups;
      this.missingModels = var2.missingModels();
      this.blockModelShaper.replaceCache(var1.modelCache);
      this.specialBlockModelRenderer = var1.specialBlockModelRenderer;
      this.entityModelSet = var1.entityModelSet;
   }

   public boolean requiresRender(BlockState var1, BlockState var2) {
      if (var1 == var2) {
         return false;
      } else {
         int var3 = this.modelGroups.getInt(var1);
         if (var3 != -1) {
            int var4 = this.modelGroups.getInt(var2);
            if (var3 == var4) {
               FluidState var5 = var1.getFluidState();
               FluidState var6 = var2.getFluidState();
               return var5 != var6;
            }
         }

         return true;
      }
   }

   public Supplier<SpecialBlockModelRenderer> specialBlockModelRenderer() {
      return () -> this.specialBlockModelRenderer;
   }

   public Supplier<EntityModelSet> entityModels() {
      return () -> this.entityModelSet;
   }

   static record ResolvedModels(ResolvedModel missing, Map<Identifier, ResolvedModel> models) {
      final Map<Identifier, ResolvedModel> models;

      ResolvedModels(ResolvedModel var1, Map<Identifier, ResolvedModel> var2) {
         super();
         this.missing = var1;
         this.models = var2;
      }
   }

   static record ReloadState(ModelBakery.BakingResult bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BlockStateModel> modelCache, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer) {
      final ModelBakery.BakingResult bakedModels;
      final Object2IntMap<BlockState> modelGroups;
      final Map<BlockState, BlockStateModel> modelCache;
      final EntityModelSet entityModelSet;
      final SpecialBlockModelRenderer specialBlockModelRenderer;

      ReloadState(ModelBakery.BakingResult var1, Object2IntMap<BlockState> var2, Map<BlockState, BlockStateModel> var3, EntityModelSet var4, SpecialBlockModelRenderer var5) {
         super();
         this.bakedModels = var1;
         this.modelGroups = var2;
         this.modelCache = var3;
         this.entityModelSet = var4;
         this.specialBlockModelRenderer = var5;
      }
   }
}
