package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
   private Map<ModelResourceLocation, BakedModel> bakedBlockStateModels = Map.of();
   private Map<ResourceLocation, ItemModel> bakedItemStackModels = Map.of();
   private Map<ResourceLocation, ClientItem.Properties> itemProperties = Map.of();
   private final AtlasSet atlases;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private EntityModelSet entityModelSet;
   private SpecialBlockModelRenderer specialBlockModelRenderer;
   private int maxMipmapLevels;
   private BakedModel missingModel;
   private ItemModel missingItemModel;
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

   public BakedModel getModel(ModelResourceLocation var1) {
      return (BakedModel)this.bakedBlockStateModels.getOrDefault(var1, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public ItemModel getItemModel(ResourceLocation var1) {
      return (ItemModel)this.bakedItemStackModels.getOrDefault(var1, this.missingItemModel);
   }

   public ClientItem.Properties getItemProperties(ResourceLocation var1) {
      return (ClientItem.Properties)this.itemProperties.getOrDefault(var1, ClientItem.Properties.DEFAULT);
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      UnbakedModel var5 = MissingBlockModel.missingModel();
      CompletableFuture var6 = CompletableFuture.supplyAsync(EntityModelSet::vanilla, var3);
      CompletableFuture var7 = var6.thenApplyAsync(SpecialBlockModelRenderer::vanilla, var3);
      CompletableFuture var8 = loadBlockModels(var2, var3);
      CompletableFuture var9 = BlockStateModelLoader.loadBlockStates(var5, var2, var3);
      CompletableFuture var10 = ClientItemInfoLoader.scheduleLoad(var2, var3);
      CompletableFuture var11 = CompletableFuture.allOf(var8, var9, var10).thenApplyAsync((var4x) -> discoverModelDependencies(var5, (Map)var8.join(), (BlockStateModelLoader.LoadedModels)var9.join(), (ClientItemInfoLoader.LoadedClientInfos)var10.join()), var3);
      CompletableFuture var12 = var9.thenApplyAsync((var1x) -> buildModelGroups(this.blockColors, var1x), var3);
      Map var13 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var3);
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])Stream.concat(var13.values().stream(), Stream.of(var11, var12, var9, var10, var6, var7)).toArray((var0) -> new CompletableFuture[var0])).thenApplyAsync((var8x) -> {
         Map var9x = (Map)var13.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var0) -> (AtlasSet.StitchResult)((CompletableFuture)var0.getValue()).join()));
         ModelDiscovery var10x = (ModelDiscovery)var11.join();
         Object2IntMap var11x = (Object2IntMap)var12.join();
         Set var12x = var10x.getUnreferencedModels();
         if (!var12x.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", var12x.stream().sorted().map((var0) -> "\t" + String.valueOf(var0) + "\n").collect(Collectors.joining()));
         }

         ModelBakery var13x = new ModelBakery((EntityModelSet)var6.join(), ((BlockStateModelLoader.LoadedModels)var9.join()).plainModels(), ((ClientItemInfoLoader.LoadedClientInfos)var10.join()).contents(), var10x.getReferencedModels(), var5);
         return loadModels(Profiler.get(), var9x, var13x, var11x, (EntityModelSet)var6.join(), (SpecialBlockModelRenderer)var7.join());
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

   private static ModelDiscovery discoverModelDependencies(UnbakedModel var0, Map<ResourceLocation, UnbakedModel> var1, BlockStateModelLoader.LoadedModels var2, ClientItemInfoLoader.LoadedClientInfos var3) {
      ModelDiscovery var4 = new ModelDiscovery(var1, var0);
      Stream var10000 = var2.forResolving();
      Objects.requireNonNull(var4);
      var10000.forEach(var4::addRoot);
      var3.contents().values().forEach((var1x) -> var4.addRoot(var1x.model()));
      var4.registerSpecialModels();
      var4.discoverDependencies();
      return var4;
   }

   private static ReloadState loadModels(ProfilerFiller var0, final Map<ResourceLocation, AtlasSet.StitchResult> var1, ModelBakery var2, Object2IntMap<BlockState> var3, EntityModelSet var4, SpecialBlockModelRenderer var5) {
      var0.push("baking");
      final HashMultimap var6 = HashMultimap.create();
      final HashMultimap var7 = HashMultimap.create();
      final TextureAtlasSprite var8 = ((AtlasSet.StitchResult)var1.get(TextureAtlas.LOCATION_BLOCKS)).missing();
      ModelBakery.BakingResult var9 = var2.bakeModels(new ModelBakery.TextureGetter() {
         public TextureAtlasSprite get(ModelDebugName var1x, Material var2) {
            AtlasSet.StitchResult var3 = (AtlasSet.StitchResult)var1.get(var2.atlasLocation());
            TextureAtlasSprite var4 = var3.getSprite(var2.texture());
            if (var4 != null) {
               return var4;
            } else {
               var6.put((String)var1x.get(), var2);
               return var3.missing();
            }
         }

         public TextureAtlasSprite reportMissingReference(ModelDebugName var1x, String var2) {
            var7.put((String)var1x.get(), var2);
            return var8;
         }
      });
      var6.asMap().forEach((var0x, var1x) -> LOGGER.warn("Missing textures in model {}:\n{}", var0x, var1x.stream().sorted(Material.COMPARATOR).map((var0) -> {
            String var10000 = String.valueOf(var0.atlasLocation());
            return "    " + var10000 + ":" + String.valueOf(var0.texture());
         }).collect(Collectors.joining("\n"))));
      var7.asMap().forEach((var0x, var1x) -> LOGGER.warn("Missing texture references in model {}:\n{}", var0x, var1x.stream().sorted().map((var0) -> "    " + var0).collect(Collectors.joining("\n"))));
      var0.popPush("dispatch");
      Map var10 = createBlockStateToModelDispatch(var9.blockStateModels(), var9.missingModel());
      CompletableFuture var11 = CompletableFuture.allOf((CompletableFuture[])var1.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray((var0x) -> new CompletableFuture[var0x]));
      var0.pop();
      return new ReloadState(var9, var3, var10, var1, var4, var5, var11);
   }

   private static Map<BlockState, BakedModel> createBlockStateToModelDispatch(Map<ModelResourceLocation, BakedModel> var0, BakedModel var1) {
      IdentityHashMap var2 = new IdentityHashMap();

      for(Block var4 : BuiltInRegistries.BLOCK) {
         var4.getStateDefinition().getPossibleStates().forEach((var3) -> {
            ResourceLocation var4 = var3.getBlock().builtInRegistryHolder().key().location();
            ModelResourceLocation var5 = BlockModelShaper.stateToModelLocation(var4, var3);
            BakedModel var6 = (BakedModel)var0.get(var5);
            if (var6 == null) {
               LOGGER.warn("Missing model for variant: '{}'", var5);
               var2.putIfAbsent(var3, var1);
            } else {
               var2.put(var3, var6);
            }

         });
      }

      return var2;
   }

   private static Object2IntMap<BlockState> buildModelGroups(BlockColors var0, BlockStateModelLoader.LoadedModels var1) {
      return ModelGroupCollector.build(var0, var1);
   }

   private void apply(ReloadState var1, ProfilerFiller var2) {
      var2.push("upload");
      var1.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
      ModelBakery.BakingResult var3 = var1.bakedModels;
      this.bakedBlockStateModels = var3.blockStateModels();
      this.bakedItemStackModels = var3.itemStackModels();
      this.itemProperties = var3.itemProperties();
      this.modelGroups = var1.modelGroups;
      this.missingModel = var3.missingModel();
      this.missingItemModel = var3.missingItemModel();
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
      VANILLA_ATLASES = Map.of(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("banner_patterns"), Sheets.BED_SHEET, ResourceLocation.withDefaultNamespace("beds"), Sheets.CHEST_SHEET, ResourceLocation.withDefaultNamespace("chests"), Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("shield_patterns"), Sheets.SIGN_SHEET, ResourceLocation.withDefaultNamespace("signs"), Sheets.SHULKER_SHEET, ResourceLocation.withDefaultNamespace("shulker_boxes"), Sheets.ARMOR_TRIMS_SHEET, ResourceLocation.withDefaultNamespace("armor_trims"), Sheets.DECORATED_POT_SHEET, ResourceLocation.withDefaultNamespace("decorated_pot"), TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("blocks"));
   }

   static record ReloadState(ModelBakery.BakingResult bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BakedModel> modelCache, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, CompletableFuture<Void> readyForUpload) {
      final ModelBakery.BakingResult bakedModels;
      final Object2IntMap<BlockState> modelGroups;
      final Map<BlockState, BakedModel> modelCache;
      final Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations;
      final EntityModelSet entityModelSet;
      final SpecialBlockModelRenderer specialBlockModelRenderer;
      final CompletableFuture<Void> readyForUpload;

      ReloadState(ModelBakery.BakingResult var1, Object2IntMap<BlockState> var2, Map<BlockState, BakedModel> var3, Map<ResourceLocation, AtlasSet.StitchResult> var4, EntityModelSet var5, SpecialBlockModelRenderer var6, CompletableFuture<Void> var7) {
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
