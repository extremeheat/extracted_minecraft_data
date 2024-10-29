package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
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
      return (BakedModel)this.bakedRegistry.getOrDefault(var1, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      UnbakedModel var5 = MissingBlockModel.missingModel();
      BlockStateModelLoader var6 = new BlockStateModelLoader(var5);
      CompletableFuture var7 = loadBlockModels(var2, var3);
      CompletableFuture var8 = loadBlockStates(var6, var2, var3);
      CompletableFuture var9 = var8.thenCombineAsync(var7, (var2x, var3x) -> {
         return this.discoverModelDependencies(var5, var3x, var2x);
      }, var3);
      CompletableFuture var10 = var8.thenApplyAsync((var1x) -> {
         return buildModelGroups(this.blockColors, var1x);
      }, var3);
      Map var11 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var3);
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])Stream.concat(var11.values().stream(), Stream.of(var9, var10)).toArray((var0) -> {
         return new CompletableFuture[var0];
      })).thenApplyAsync((var5x) -> {
         Map var6 = (Map)var11.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var0) -> {
            return (AtlasSet.StitchResult)((CompletableFuture)var0.getValue()).join();
         }));
         ModelDiscovery var7 = (ModelDiscovery)var9.join();
         Object2IntMap var8 = (Object2IntMap)var10.join();
         return this.loadModels(Profiler.get(), var6, new ModelBakery(var7.getTopModels(), var7.getReferencedModels(), var5), var8);
      }, var3).thenCompose((var0) -> {
         return var0.readyForUpload.thenApply((var1) -> {
            return var0;
         });
      });
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> {
         this.apply(var1x, Profiler.get());
      }, var4);
   }

   private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> {
         return MODEL_LISTER.listMatchingResources(var0);
      }, var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());
         Iterator var3 = var1x.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var2.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var1 = MODEL_LISTER.fileToId((ResourceLocation)var4.getKey());

               try {
                  BufferedReader var2 = ((Resource)var4.getValue()).openAsReader();

                  Pair var4x;
                  try {
                     BlockModel var3 = BlockModel.fromStream(var2);
                     var3.name = var1.toString();
                     var4x = Pair.of(var1, var3);
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

                  return var4x;
               } catch (Exception var7) {
                  LOGGER.error("Failed to load model {}", var4.getKey(), var7);
                  return null;
               }
            }, var1));
         }

         return Util.sequence(var2).thenApply((var0) -> {
            return (Map)var0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
         });
      });
   }

   private ModelDiscovery discoverModelDependencies(UnbakedModel var1, Map<ResourceLocation, UnbakedModel> var2, BlockStateModelLoader.LoadedModels var3) {
      ModelDiscovery var4 = new ModelDiscovery(var2, var1);
      var4.registerStandardModels(var3);
      var4.discoverDependencies();
      return var4;
   }

   private static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockStates(BlockStateModelLoader var0, ResourceManager var1, Executor var2) {
      Function var3 = BlockStateModelLoader.definitionLocationToBlockMapper();
      return CompletableFuture.supplyAsync(() -> {
         return BLOCKSTATE_LISTER.listMatchingResourceStacks(var1);
      }, var2).thenCompose((var3x) -> {
         ArrayList var4 = new ArrayList(var3x.size());
         Iterator var5 = var3x.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            var4.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var3x = BLOCKSTATE_LISTER.fileToId((ResourceLocation)var6.getKey());
               StateDefinition var4 = (StateDefinition)var3.apply(var3x);
               if (var4 == null) {
                  LOGGER.debug("Discovered unknown block state definition {}, ignoring", var3x);
                  return null;
               } else {
                  List var5 = (List)var6.getValue();
                  ArrayList var6x = new ArrayList(var5.size());
                  Iterator var7 = var5.iterator();

                  while(var7.hasNext()) {
                     Resource var8 = (Resource)var7.next();

                     try {
                        BufferedReader var9 = var8.openAsReader();

                        try {
                           JsonObject var10 = GsonHelper.parse((Reader)var9);
                           BlockModelDefinition var11 = BlockModelDefinition.fromJsonElement(var10);
                           var6x.add(new BlockStateModelLoader.LoadedBlockModelDefinition(var8.sourcePackId(), var11));
                        } catch (Throwable var14) {
                           if (var9 != null) {
                              try {
                                 ((Reader)var9).close();
                              } catch (Throwable var13) {
                                 var14.addSuppressed(var13);
                              }
                           }

                           throw var14;
                        }

                        if (var9 != null) {
                           ((Reader)var9).close();
                        }
                     } catch (Exception var15) {
                        LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{var3x, var8.sourcePackId(), var15});
                     }
                  }

                  try {
                     return var0.loadBlockStateDefinitionStack(var3x, var4, var6x);
                  } catch (Exception var12) {
                     LOGGER.error("Failed to load blockstate definition {}", var3x, var12);
                     return null;
                  }
               }
            }, var2));
         }

         return Util.sequence(var4).thenApply((var0x) -> {
            HashMap var1 = new HashMap();
            Iterator var2 = var0x.iterator();

            while(var2.hasNext()) {
               BlockStateModelLoader.LoadedModels var3 = (BlockStateModelLoader.LoadedModels)var2.next();
               if (var3 != null) {
                  var1.putAll(var3.models());
               }
            }

            return new BlockStateModelLoader.LoadedModels(var1);
         });
      });
   }

   private ReloadState loadModels(ProfilerFiller var1, Map<ResourceLocation, AtlasSet.StitchResult> var2, ModelBakery var3, Object2IntMap<BlockState> var4) {
      var1.push("baking");
      HashMultimap var5 = HashMultimap.create();
      var3.bakeModels((var2x, var3x) -> {
         AtlasSet.StitchResult var4 = (AtlasSet.StitchResult)var2.get(var3x.atlasLocation());
         TextureAtlasSprite var5x = var4.getSprite(var3x.texture());
         if (var5x != null) {
            return var5x;
         } else {
            var5.put(var2x, var3x);
            return var4.missing();
         }
      });
      var5.asMap().forEach((var0, var1x) -> {
         LOGGER.warn("Missing textures in model {}:\n{}", var0, var1x.stream().sorted(Material.COMPARATOR).map((var0x) -> {
            String var10000 = String.valueOf(var0x.atlasLocation());
            return "    " + var10000 + ":" + String.valueOf(var0x.texture());
         }).collect(Collectors.joining("\n")));
      });
      var1.popPush("dispatch");
      Map var6 = var3.getBakedTopLevelModels();
      BakedModel var7 = (BakedModel)var6.get(MissingBlockModel.VARIANT);
      IdentityHashMap var8 = new IdentityHashMap();
      Iterator var9 = BuiltInRegistries.BLOCK.iterator();

      while(var9.hasNext()) {
         Block var10 = (Block)var9.next();
         var10.getStateDefinition().getPossibleStates().forEach((var3x) -> {
            ResourceLocation var4 = var3x.getBlock().builtInRegistryHolder().key().location();
            BakedModel var5 = (BakedModel)var6.getOrDefault(BlockModelShaper.stateToModelLocation(var4, var3x), var7);
            var8.put(var3x, var5);
         });
      }

      CompletableFuture var11 = CompletableFuture.allOf((CompletableFuture[])var2.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
      var1.pop();
      return new ReloadState(var3, var4, var7, var8, var2, var11);
   }

   private static Object2IntMap<BlockState> buildModelGroups(BlockColors var0, BlockStateModelLoader.LoadedModels var1) {
      return ModelGroupCollector.build(var0, var1);
   }

   private void apply(ReloadState var1, ProfilerFiller var2) {
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

   public void close() {
      this.atlases.close();
   }

   public void updateMaxMipLevel(int var1) {
      this.maxMipmapLevels = var1;
   }

   static {
      VANILLA_ATLASES = Map.of(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("banner_patterns"), Sheets.BED_SHEET, ResourceLocation.withDefaultNamespace("beds"), Sheets.CHEST_SHEET, ResourceLocation.withDefaultNamespace("chests"), Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("shield_patterns"), Sheets.SIGN_SHEET, ResourceLocation.withDefaultNamespace("signs"), Sheets.SHULKER_SHEET, ResourceLocation.withDefaultNamespace("shulker_boxes"), Sheets.ARMOR_TRIMS_SHEET, ResourceLocation.withDefaultNamespace("armor_trims"), Sheets.DECORATED_POT_SHEET, ResourceLocation.withDefaultNamespace("decorated_pot"), TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("blocks"));
   }

   static record ReloadState(ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, BakedModel missingModel, Map<BlockState, BakedModel> modelCache, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, CompletableFuture<Void> readyForUpload) {
      final ModelBakery modelBakery;
      final Object2IntMap<BlockState> modelGroups;
      final BakedModel missingModel;
      final Map<BlockState, BakedModel> modelCache;
      final Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations;
      final CompletableFuture<Void> readyForUpload;

      ReloadState(ModelBakery var1, Object2IntMap<BlockState> var2, BakedModel var3, Map<BlockState, BakedModel> var4, Map<ResourceLocation, AtlasSet.StitchResult> var5, CompletableFuture<Void> var6) {
         super();
         this.modelBakery = var1;
         this.modelGroups = var2;
         this.missingModel = var3;
         this.modelCache = var4;
         this.atlasPreparations = var5;
         this.readyForUpload = var6;
      }

      public ModelBakery modelBakery() {
         return this.modelBakery;
      }

      public Object2IntMap<BlockState> modelGroups() {
         return this.modelGroups;
      }

      public BakedModel missingModel() {
         return this.missingModel;
      }

      public Map<BlockState, BakedModel> modelCache() {
         return this.modelCache;
      }

      public Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations() {
         return this.atlasPreparations;
      }

      public CompletableFuture<Void> readyForUpload() {
         return this.readyForUpload;
      }
   }
}
