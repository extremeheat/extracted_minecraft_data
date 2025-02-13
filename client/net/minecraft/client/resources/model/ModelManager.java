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
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
   private Map<ResourceLocation, ItemModel> bakedItemStackModels = Map.of();
   private Map<ResourceLocation, ClientItem.Properties> itemProperties = Map.of();
   private final AtlasSet atlases;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private EntityModelSet entityModelSet;
   private SpecialBlockModelRenderer specialBlockModelRenderer;
   private int maxMipmapLevels;
   private ModelBakery.MissingModels missingModels;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(TextureManager var1, BlockColors var2, int var3) {
      super();
      this.entityModelSet = EntityModelSet.EMPTY;
      this.specialBlockModelRenderer = SpecialBlockModelRenderer.EMPTY;
      this.modelGroups = Object2IntMaps.emptyMap();
      this.blockColors = var2;
      this.maxMipmapLevels = var3;
      this.blockModelShaper = new BlockModelShaper(this);
      this.atlases = new AtlasSet(VANILLA_ATLASES, var1);
   }

   public BlockStateModel getMissingBlockStateModel() {
      return this.missingModels.block();
   }

   public ItemModel getItemModel(ResourceLocation var1) {
      return (ItemModel)this.bakedItemStackModels.getOrDefault(var1, this.missingModels.item());
   }

   public ClientItem.Properties getItemProperties(ResourceLocation var1) {
      return (ClientItem.Properties)this.itemProperties.getOrDefault(var1, ClientItem.Properties.DEFAULT);
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var5 = CompletableFuture.supplyAsync(EntityModelSet::vanilla, var3);
      CompletableFuture var6 = var5.thenApplyAsync(SpecialBlockModelRenderer::vanilla, var3);
      CompletableFuture var7 = loadBlockModels(var2, var3);
      CompletableFuture var8 = BlockStateModelLoader.loadBlockStates(var2, var3);
      CompletableFuture var9 = ClientItemInfoLoader.scheduleLoad(var2, var3);
      CompletableFuture var10 = CompletableFuture.allOf(var7, var8, var9).thenApplyAsync((var3x) -> discoverModelDependencies((Map)var7.join(), (BlockStateModelLoader.LoadedModels)var8.join(), (ClientItemInfoLoader.LoadedClientInfos)var9.join()), var3);
      CompletableFuture var11 = var8.thenApplyAsync((var1x) -> buildModelGroups(this.blockColors, var1x), var3);
      Map var12 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var3);
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])Stream.concat(var12.values().stream(), Stream.of(var10, var11, var8, var9, var5, var6, var7)).toArray((var0) -> new CompletableFuture[var0])).thenComposeAsync((var9x) -> {
         Map var10x = Util.mapValues(var12, CompletableFuture::join);
         ResolvedModels var11x = (ResolvedModels)var10.join();
         Object2IntMap var12x = (Object2IntMap)var11.join();
         Sets.SetView var13 = Sets.difference(((Map)var7.join()).keySet(), var11x.models.keySet());
         if (!var13.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", var13.stream().sorted().map((var0) -> "\t" + String.valueOf(var0) + "\n").collect(Collectors.joining()));
         }

         ModelBakery var14 = new ModelBakery((EntityModelSet)var5.join(), ((BlockStateModelLoader.LoadedModels)var8.join()).models(), ((ClientItemInfoLoader.LoadedClientInfos)var9.join()).contents(), var11x.models(), var11x.missing());
         return loadModels(var10x, var14, var12x, (EntityModelSet)var5.join(), (SpecialBlockModelRenderer)var6.join(), var3);
      }, var3).thenCompose((var0) -> var0.readyForUpload.thenApply((var1) -> var0));
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> this.apply(var1x, Profiler.get()), var4);
   }

   private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources(var0), var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());

         for(Map.Entry var4 : var1x.entrySet()) {
            var2.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var1 = MODEL_LISTER.fileToId((ResourceLocation)var4.getKey());

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

   private static ResolvedModels discoverModelDependencies(Map<ResourceLocation, UnbakedModel> var0, BlockStateModelLoader.LoadedModels var1, ClientItemInfoLoader.LoadedClientInfos var2) {
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

   private static CompletableFuture<ReloadState> loadModels(final Map<ResourceLocation, AtlasSet.StitchResult> var0, ModelBakery var1, Object2IntMap<BlockState> var2, EntityModelSet var3, SpecialBlockModelRenderer var4, Executor var5) {
      CompletableFuture var6 = CompletableFuture.allOf((CompletableFuture[])var0.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray((var0x) -> new CompletableFuture[var0x]));
      final Multimap var7 = Multimaps.synchronizedMultimap(HashMultimap.create());
      final Multimap var8 = Multimaps.synchronizedMultimap(HashMultimap.create());
      return var1.bakeModels(new SpriteGetter() {
         private final TextureAtlasSprite missingSprite;

         {
            this.missingSprite = ((AtlasSet.StitchResult)var0.get(TextureAtlas.LOCATION_BLOCKS)).missing();
         }

         public TextureAtlasSprite get(Material var1, ModelDebugName var2) {
            AtlasSet.StitchResult var3 = (AtlasSet.StitchResult)var0.get(var1.atlasLocation());
            TextureAtlasSprite var4 = var3.getSprite(var1.texture());
            if (var4 != null) {
               return var4;
            } else {
               var7.put(var2.debugName(), var1);
               return var3.missing();
            }
         }

         public TextureAtlasSprite reportMissingReference(String var1, ModelDebugName var2) {
            var8.put(var2.debugName(), var1);
            return this.missingSprite;
         }
      }, var5).thenApply((var7x) -> {
         var7.asMap().forEach((var0x, var1) -> LOGGER.warn("Missing textures in model {}:\n{}", var0x, var1.stream().sorted(Material.COMPARATOR).map((var0) -> {
               String var10000 = String.valueOf(var0.atlasLocation());
               return "    " + var10000 + ":" + String.valueOf(var0.texture());
            }).collect(Collectors.joining("\n"))));
         var8.asMap().forEach((var0x, var1) -> LOGGER.warn("Missing texture references in model {}:\n{}", var0x, var1.stream().sorted().map((var0) -> "    " + var0).collect(Collectors.joining("\n"))));
         Map var8x = createBlockStateToModelDispatch(var7x.blockStateModels(), var7x.missingModels().block());
         return new ReloadState(var7x, var2, var8x, var0, var3, var4, var6);
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

   private void apply(ReloadState var1, ProfilerFiller var2) {
      var2.push("upload");
      var1.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
      ModelBakery.BakingResult var3 = var1.bakedModels;
      this.bakedItemStackModels = var3.itemStackModels();
      this.itemProperties = var3.itemProperties();
      this.modelGroups = var1.modelGroups;
      this.missingModels = var3.missingModels();
      var2.popPush("cache");
      this.blockModelShaper.replaceCache(var1.modelCache);
      this.specialBlockModelRenderer = var1.specialBlockModelRenderer;
      this.entityModelSet = var1.entityModelSet;
      var2.pop();
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

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return this.atlases.getAtlas(var1);
   }

   public void close() {
      this.atlases.close();
   }

   public void updateMaxMipLevel(int var1) {
      this.maxMipmapLevels = var1;
   }

   public Supplier<SpecialBlockModelRenderer> specialBlockModelRenderer() {
      return () -> this.specialBlockModelRenderer;
   }

   public Supplier<EntityModelSet> entityModels() {
      return () -> this.entityModelSet;
   }

   static {
      VANILLA_ATLASES = Map.of(Sheets.BANNER_SHEET, AtlasIds.BANNER_PATTERNS, Sheets.BED_SHEET, AtlasIds.BEDS, Sheets.CHEST_SHEET, AtlasIds.CHESTS, Sheets.SHIELD_SHEET, AtlasIds.SHIELD_PATTERNS, Sheets.SIGN_SHEET, AtlasIds.SIGNS, Sheets.SHULKER_SHEET, AtlasIds.SHULKER_BOXES, Sheets.ARMOR_TRIMS_SHEET, AtlasIds.ARMOR_TRIMS, Sheets.DECORATED_POT_SHEET, AtlasIds.DECORATED_POT, TextureAtlas.LOCATION_BLOCKS, AtlasIds.BLOCKS);
   }

   static record ResolvedModels(ResolvedModel missing, Map<ResourceLocation, ResolvedModel> models) {
      final Map<ResourceLocation, ResolvedModel> models;

      ResolvedModels(ResolvedModel var1, Map<ResourceLocation, ResolvedModel> var2) {
         super();
         this.missing = var1;
         this.models = var2;
      }
   }

   static record ReloadState(ModelBakery.BakingResult bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BlockStateModel> modelCache, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, CompletableFuture<Void> readyForUpload) {
      final ModelBakery.BakingResult bakedModels;
      final Object2IntMap<BlockState> modelGroups;
      final Map<BlockState, BlockStateModel> modelCache;
      final Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations;
      final EntityModelSet entityModelSet;
      final SpecialBlockModelRenderer specialBlockModelRenderer;
      final CompletableFuture<Void> readyForUpload;

      ReloadState(ModelBakery.BakingResult var1, Object2IntMap<BlockState> var2, Map<BlockState, BlockStateModel> var3, Map<ResourceLocation, AtlasSet.StitchResult> var4, EntityModelSet var5, SpecialBlockModelRenderer var6, CompletableFuture<Void> var7) {
         super();
         this.bakedModels = var1;
         this.modelGroups = var2;
         this.modelCache = var3;
         this.atlasPreparations = var4;
         this.entityModelSet = var5;
         this.specialBlockModelRenderer = var6;
         this.readyForUpload = var7;
      }
   }
}
