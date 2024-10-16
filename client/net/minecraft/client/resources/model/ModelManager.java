package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
   private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES = Map.of(
      Sheets.BANNER_SHEET,
      ResourceLocation.withDefaultNamespace("banner_patterns"),
      Sheets.BED_SHEET,
      ResourceLocation.withDefaultNamespace("beds"),
      Sheets.CHEST_SHEET,
      ResourceLocation.withDefaultNamespace("chests"),
      Sheets.SHIELD_SHEET,
      ResourceLocation.withDefaultNamespace("shield_patterns"),
      Sheets.SIGN_SHEET,
      ResourceLocation.withDefaultNamespace("signs"),
      Sheets.SHULKER_SHEET,
      ResourceLocation.withDefaultNamespace("shulker_boxes"),
      Sheets.ARMOR_TRIMS_SHEET,
      ResourceLocation.withDefaultNamespace("armor_trims"),
      Sheets.DECORATED_POT_SHEET,
      ResourceLocation.withDefaultNamespace("decorated_pot"),
      TextureAtlas.LOCATION_BLOCKS,
      ResourceLocation.withDefaultNamespace("blocks")
   );
   private Map<ModelResourceLocation, BakedModel> bakedRegistry;
   private final AtlasSet atlases;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private int maxMipmapLevels;
   private BakedModel missingModel;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(TextureManager var1, BlockColors var2, int var3) {
      super();
      this.blockColors = var2;
      this.maxMipmapLevels = var3;
      this.blockModelShaper = new BlockModelShaper(this);
      this.atlases = new AtlasSet(VANILLA_ATLASES, var1);
   }

   public BakedModel getModel(ModelResourceLocation var1) {
      return this.bakedRegistry.getOrDefault(var1, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   @Override
   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      UnbakedModel var5 = MissingBlockModel.missingModel();
      BlockStateModelLoader var6 = new BlockStateModelLoader(var5);
      CompletableFuture var7 = loadBlockModels(var2, var3);
      CompletableFuture var8 = loadBlockStates(var6, var2, var3);
      CompletableFuture var9 = var8.thenCombineAsync(
         var7, (var2x, var3x) -> this.discoverModelDependencies(var5, (Map<ResourceLocation, UnbakedModel>)var3x, var2x), var3
      );
      CompletableFuture var10 = var8.thenApplyAsync(var1x -> buildModelGroups(this.blockColors, var1x), var3);
      Map var11 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var3);
      return CompletableFuture.allOf(Stream.concat(var11.values().stream(), Stream.of(var9, var10)).toArray(CompletableFuture[]::new))
         .thenApplyAsync(
            var5x -> {
               Map var6x = var11.entrySet()
                  .stream()
                  .collect(Collectors.toMap(Entry::getKey, var0 -> (AtlasSet.StitchResult)((CompletableFuture)var0.getValue()).join()));
               ModelDiscovery var7x = (ModelDiscovery)var9.join();
               Object2IntMap var8x = (Object2IntMap)var10.join();
               return this.loadModels(Profiler.get(), var6x, new ModelBakery(var7x.getTopModels(), var7x.getReferencedModels(), var5), var8x);
            },
            var3
         )
         .thenCompose(var0 -> var0.readyForUpload.thenApply(var1x -> (ModelManager.ReloadState)var0))
         .thenCompose(var1::wait)
         .thenAcceptAsync(var1x -> this.apply(var1x, Profiler.get()), var4);
   }

   private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.<Map<ResourceLocation, Resource>>supplyAsync(() -> MODEL_LISTER.listMatchingResources(var0), var1)
         .thenCompose(
            var1x -> {
               ArrayList var2 = new ArrayList(var1x.size());

               for (Entry var4 : var1x.entrySet()) {
                  var2.add(CompletableFuture.supplyAsync(() -> {
                     ResourceLocation var1xx = MODEL_LISTER.fileToId((ResourceLocation)var4.getKey());

                     try {
                        Pair var4x;
                        try (BufferedReader var2x = ((Resource)var4.getValue()).openAsReader()) {
                           BlockModel var3 = BlockModel.fromStream(var2x);
                           var3.name = var1xx.toString();
                           var4x = Pair.of(var1xx, var3);
                        }

                        return var4x;
                     } catch (Exception var7) {
                        LOGGER.error("Failed to load model {}", var4.getKey(), var7);
                        return null;
                     }
                  }, var1));
               }

               return Util.sequence(var2)
                  .thenApply(var0xx -> var0xx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
            }
         );
   }

   private ModelDiscovery discoverModelDependencies(UnbakedModel var1, Map<ResourceLocation, UnbakedModel> var2, BlockStateModelLoader.LoadedModels var3) {
      ModelDiscovery var4 = new ModelDiscovery(var2, var1);
      var4.registerStandardModels(var3);
      var4.discoverDependencies();
      return var4;
   }

   private static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockStates(BlockStateModelLoader var0, ResourceManager var1, Executor var2) {
      Function var3 = BlockStateModelLoader.definitionLocationToBlockMapper();
      return CompletableFuture.<Map<ResourceLocation, List<Resource>>>supplyAsync(() -> BLOCKSTATE_LISTER.listMatchingResourceStacks(var1), var2)
         .thenCompose(var3x -> {
            ArrayList var4 = new ArrayList(var3x.size());

            for (Entry var6 : var3x.entrySet()) {
               var4.add(CompletableFuture.supplyAsync(() -> {
                  ResourceLocation var3xx = BLOCKSTATE_LISTER.fileToId((ResourceLocation)var6.getKey());
                  StateDefinition var4x = (StateDefinition)var3.apply(var3xx);
                  if (var4x == null) {
                     LOGGER.debug("Discovered unknown block state definition {}, ignoring", var3xx);
                     return null;
                  } else {
                     List var5 = (List)var6.getValue();
                     ArrayList var6x = new ArrayList(var5.size());

                     for (Resource var8 : var5) {
                        try (BufferedReader var9 = var8.openAsReader()) {
                           JsonObject var10 = GsonHelper.parse(var9);
                           BlockModelDefinition var11 = BlockModelDefinition.fromJsonElement(var10);
                           var6x.add(new BlockStateModelLoader.LoadedBlockModelDefinition(var8.sourcePackId(), var11));
                        } catch (Exception var15) {
                           LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{var3xx, var8.sourcePackId(), var15});
                        }
                     }

                     try {
                        return var0.loadBlockStateDefinitionStack(var3xx, var4x, var6x);
                     } catch (Exception var12) {
                        LOGGER.error("Failed to load blockstate definition {}", var3xx, var12);
                        return null;
                     }
                  }
               }, var2));
            }

            return Util.sequence(var4).thenApply(var0xx -> {
               HashMap var1xx = new HashMap();

               for (BlockStateModelLoader.LoadedModels var3xx : var0xx) {
                  if (var3xx != null) {
                     var1xx.putAll(var3xx.models());
                  }
               }

               return new BlockStateModelLoader.LoadedModels(var1xx);
            });
         });
   }

   private ModelManager.ReloadState loadModels(
      ProfilerFiller var1, Map<ResourceLocation, AtlasSet.StitchResult> var2, ModelBakery var3, Object2IntMap<BlockState> var4
   ) {
      var1.push("baking");
      HashMultimap var5 = HashMultimap.create();
      var3.bakeModels((var2x, var3x) -> {
         AtlasSet.StitchResult var4x = (AtlasSet.StitchResult)var2.get(var3x.atlasLocation());
         TextureAtlasSprite var5x = var4x.getSprite(var3x.texture());
         if (var5x != null) {
            return var5x;
         } else {
            var5.put(var2x, var3x);
            return var4x.missing();
         }
      });
      var5.asMap()
         .forEach(
            (var0, var1x) -> LOGGER.warn(
                  "Missing textures in model {}:\n{}",
                  var0,
                  var1x.stream()
                     .sorted(Material.COMPARATOR)
                     .map(var0x -> "    " + var0x.atlasLocation() + ":" + var0x.texture())
                     .collect(Collectors.joining("\n"))
               )
         );
      var1.popPush("dispatch");
      Map var6 = var3.getBakedTopLevelModels();
      BakedModel var7 = (BakedModel)var6.get(MissingBlockModel.VARIANT);
      IdentityHashMap var8 = new IdentityHashMap();

      for (Block var10 : BuiltInRegistries.BLOCK) {
         var10.getStateDefinition().getPossibleStates().forEach(var3x -> {
            ResourceLocation var4x = var3x.getBlock().builtInRegistryHolder().key().location();
            BakedModel var5x = var6.getOrDefault(BlockModelShaper.stateToModelLocation(var4x, var3x), var7);
            var8.put(var3x, var5x);
         });
      }

      CompletableFuture var11 = CompletableFuture.allOf(var2.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new));
      var1.pop();
      return new ModelManager.ReloadState(var3, var4, var7, var8, var2, var11);
   }

   private static Object2IntMap<BlockState> buildModelGroups(BlockColors var0, BlockStateModelLoader.LoadedModels var1) {
      return ModelGroupCollector.build(var0, var1);
   }

   private void apply(ModelManager.ReloadState var1, ProfilerFiller var2) {
      var2.push("upload");
      var1.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
      ModelBakery var3 = var1.modelBakery;
      this.bakedRegistry = var3.getBakedTopLevelModels();
      this.modelGroups = var1.modelGroups;
      this.missingModel = var1.missingModel;
      var2.popPush("cache");
      this.blockModelShaper.replaceCache(var1.modelCache);
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

   @Override
   public void close() {
      this.atlases.close();
   }

   public void updateMaxMipLevel(int var1) {
      this.maxMipmapLevels = var1;
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
}
