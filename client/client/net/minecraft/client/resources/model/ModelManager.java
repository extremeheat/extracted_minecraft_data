package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public class ModelManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES = Map.of(
      Sheets.BANNER_SHEET,
      new ResourceLocation("banner_patterns"),
      Sheets.BED_SHEET,
      new ResourceLocation("beds"),
      Sheets.CHEST_SHEET,
      new ResourceLocation("chests"),
      Sheets.SHIELD_SHEET,
      new ResourceLocation("shield_patterns"),
      Sheets.SIGN_SHEET,
      new ResourceLocation("signs"),
      Sheets.SHULKER_SHEET,
      new ResourceLocation("shulker_boxes"),
      Sheets.ARMOR_TRIMS_SHEET,
      new ResourceLocation("armor_trims"),
      Sheets.DECORATED_POT_SHEET,
      new ResourceLocation("decorated_pot"),
      TextureAtlas.LOCATION_BLOCKS,
      new ResourceLocation("blocks")
   );
   private Map<ResourceLocation, BakedModel> bakedRegistry;
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
   public final CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      var3.startTick();
      CompletableFuture var7 = loadBlockModels(var2, var5);
      CompletableFuture var8 = loadBlockStates(var2, var5);
      CompletableFuture var9 = var7.thenCombineAsync(
         var8,
         (var2x, var3x) -> new ModelBakery(
               this.blockColors, var3, (Map<ResourceLocation, BlockModel>)var2x, (Map<ResourceLocation, List<ModelBakery.LoadedJson>>)var3x
            ),
         var5
      );
      Map var10 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var5);
      return CompletableFuture.allOf(Stream.concat(var10.values().stream(), Stream.of(var9)).toArray(CompletableFuture[]::new))
         .thenApplyAsync(
            var4x -> this.loadModels(
                  var3,
                  var10.entrySet()
                     .stream()
                     .collect(Collectors.toMap(Entry::getKey, var0 -> (AtlasSet.StitchResult)((CompletableFuture)var0.getValue()).join())),
                  (ModelBakery)var9.join()
               ),
            var5
         )
         .thenCompose(var0 -> var0.readyForUpload.thenApply(var1x -> (ModelManager.ReloadState)var0))
         .thenCompose(var1::wait)
         .thenAcceptAsync(var2x -> this.apply(var2x, var4), var6);
   }

   private static CompletableFuture<Map<ResourceLocation, BlockModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.<Map<ResourceLocation, Resource>>supplyAsync(() -> ModelBakery.MODEL_LISTER.listMatchingResources(var0), var1)
         .thenCompose(
            var1x -> {
               ArrayList var2 = new ArrayList(var1x.size());
      
               for (Entry var4 : var1x.entrySet()) {
                  var2.add(CompletableFuture.supplyAsync(() -> {
                     try {
                        Pair var2x;
                        try (BufferedReader var1xx = ((Resource)var4.getValue()).openAsReader()) {
                           var2x = Pair.of((ResourceLocation)var4.getKey(), BlockModel.fromStream(var1xx));
                        }
      
                        return var2x;
                     } catch (Exception var6) {
                        LOGGER.error("Failed to load model {}", var4.getKey(), var6);
                        return null;
                     }
                  }, var1));
               }
      
               return Util.sequence(var2)
                  .thenApply(var0xx -> var0xx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
            }
         );
   }

   private static CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> loadBlockStates(ResourceManager var0, Executor var1) {
      return CompletableFuture.<Map<ResourceLocation, List<Resource>>>supplyAsync(() -> ModelBakery.BLOCKSTATE_LISTER.listMatchingResourceStacks(var0), var1)
         .thenCompose(
            var1x -> {
               ArrayList var2 = new ArrayList(var1x.size());
      
               for (Entry var4 : var1x.entrySet()) {
                  var2.add(CompletableFuture.supplyAsync(() -> {
                     List var1xx = (List)var4.getValue();
                     ArrayList var2x = new ArrayList(var1xx.size());
      
                     for (Resource var4x : var1xx) {
                        try (BufferedReader var5 = var4x.openAsReader()) {
                           JsonObject var6 = GsonHelper.parse(var5);
                           var2x.add(new ModelBakery.LoadedJson(var4x.sourcePackId(), var6));
                        } catch (Exception var10) {
                           LOGGER.error("Failed to load blockstate {} from pack {}", new Object[]{var4.getKey(), var4x.sourcePackId(), var10});
                        }
                     }
      
                     return Pair.of((ResourceLocation)var4.getKey(), var2x);
                  }, var1));
               }
      
               return Util.sequence(var2)
                  .thenApply(var0xx -> var0xx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
            }
         );
   }

   private ModelManager.ReloadState loadModels(ProfilerFiller var1, Map<ResourceLocation, AtlasSet.StitchResult> var2, ModelBakery var3) {
      var1.push("load");
      var1.popPush("baking");
      HashMultimap var4 = HashMultimap.create();
      var3.bakeModels((var2x, var3x) -> {
         AtlasSet.StitchResult var4x = (AtlasSet.StitchResult)var2.get(var3x.atlasLocation());
         TextureAtlasSprite var5x = var4x.getSprite(var3x.texture());
         if (var5x != null) {
            return var5x;
         } else {
            var4.put(var2x, var3x);
            return var4x.missing();
         }
      });
      var4.asMap()
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
      Map var5 = var3.getBakedTopLevelModels();
      BakedModel var6 = (BakedModel)var5.get(ModelBakery.MISSING_MODEL_LOCATION);
      IdentityHashMap var7 = new IdentityHashMap();

      for (Block var9 : BuiltInRegistries.BLOCK) {
         var9.getStateDefinition().getPossibleStates().forEach(var3x -> {
            ResourceLocation var4x = var3x.getBlock().builtInRegistryHolder().key().location();
            BakedModel var5x = var5.getOrDefault(BlockModelShaper.stateToModelLocation(var4x, var3x), var6);
            var7.put(var3x, var5x);
         });
      }

      CompletableFuture var10 = CompletableFuture.allOf(var2.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new));
      var1.pop();
      var1.endTick();
      return new ModelManager.ReloadState(var3, var6, var7, var2, var10);
   }

   private void apply(ModelManager.ReloadState var1, ProfilerFiller var2) {
      var2.startTick();
      var2.push("upload");
      var1.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
      ModelBakery var3 = var1.modelBakery;
      this.bakedRegistry = var3.getBakedTopLevelModels();
      this.modelGroups = var3.getModelGroups();
      this.missingModel = var1.missingModel;
      var2.popPush("cache");
      this.blockModelShaper.replaceCache(var1.modelCache);
      var2.pop();
      var2.endTick();
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

   static record ReloadState(
      ModelBakery modelBakery,
      BakedModel missingModel,
      Map<BlockState, BakedModel> modelCache,
      Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations,
      CompletableFuture<Void> readyForUpload
   ) {

      ReloadState(
         ModelBakery modelBakery,
         BakedModel missingModel,
         Map<BlockState, BakedModel> modelCache,
         Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations,
         CompletableFuture<Void> readyForUpload
      ) {
         super();
         this.modelBakery = modelBakery;
         this.missingModel = missingModel;
         this.modelCache = modelCache;
         this.atlasPreparations = atlasPreparations;
         this.readyForUpload = readyForUpload;
      }
   }
}
