package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
   private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;
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
      return (BakedModel)this.bakedRegistry.getOrDefault(var1, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      var3.startTick();
      CompletableFuture var7 = loadBlockModels(var2, var5);
      CompletableFuture var8 = loadBlockStates(var2, var5);
      CompletableFuture var9 = var7.thenCombineAsync(var8, (var2x, var3x) -> {
         return new ModelBakery(this.blockColors, var3, var2x, var3x);
      }, var5);
      Map var10 = this.atlases.scheduleLoad(var2, this.maxMipmapLevels, var5);
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])Stream.concat(var10.values().stream(), Stream.of(var9)).toArray((var0) -> {
         return new CompletableFuture[var0];
      })).thenApplyAsync((var4x) -> {
         return this.loadModels(var3, (Map)var10.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var0) -> {
            return (AtlasSet.StitchResult)((CompletableFuture)var0.getValue()).join();
         })), (ModelBakery)var9.join());
      }, var5).thenCompose((var0) -> {
         return var0.readyForUpload.thenApply((var1) -> {
            return var0;
         });
      });
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var2x) -> {
         this.apply(var2x, var4);
      }, var6);
   }

   private static CompletableFuture<Map<ResourceLocation, BlockModel>> loadBlockModels(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> {
         return ModelBakery.MODEL_LISTER.listMatchingResources(var0);
      }, var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());
         Iterator var3 = var1x.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var2.add(CompletableFuture.supplyAsync(() -> {
               try {
                  BufferedReader var1 = ((Resource)var4.getValue()).openAsReader();

                  Pair var2;
                  try {
                     var2 = Pair.of((ResourceLocation)var4.getKey(), BlockModel.fromStream(var1));
                  } catch (Throwable var5) {
                     if (var1 != null) {
                        try {
                           ((Reader)var1).close();
                        } catch (Throwable var4x) {
                           var5.addSuppressed(var4x);
                        }
                     }

                     throw var5;
                  }

                  if (var1 != null) {
                     ((Reader)var1).close();
                  }

                  return var2;
               } catch (Exception var6) {
                  LOGGER.error("Failed to load model {}", var4.getKey(), var6);
                  return null;
               }
            }, var1));
         }

         return Util.sequence(var2).thenApply((var0) -> {
            return (Map)var0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
         });
      });
   }

   private static CompletableFuture<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> loadBlockStates(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> {
         return ModelBakery.BLOCKSTATE_LISTER.listMatchingResourceStacks(var0);
      }, var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());
         Iterator var3 = var1x.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            var2.add(CompletableFuture.supplyAsync(() -> {
               List var1 = (List)var4.getValue();
               ArrayList var2 = new ArrayList(var1.size());
               Iterator var3 = var1.iterator();

               while(var3.hasNext()) {
                  Resource var4x = (Resource)var3.next();

                  try {
                     BufferedReader var5 = var4x.openAsReader();

                     try {
                        JsonObject var6 = GsonHelper.parse((Reader)var5);
                        var2.add(new ModelBakery.LoadedJson(var4x.sourcePackId(), var6));
                     } catch (Throwable var9) {
                        if (var5 != null) {
                           try {
                              ((Reader)var5).close();
                           } catch (Throwable var8) {
                              var9.addSuppressed(var8);
                           }
                        }

                        throw var9;
                     }

                     if (var5 != null) {
                        ((Reader)var5).close();
                     }
                  } catch (Exception var10) {
                     LOGGER.error("Failed to load blockstate {} from pack {}", new Object[]{var4.getKey(), var4x.sourcePackId(), var10});
                  }
               }

               return Pair.of((ResourceLocation)var4.getKey(), var2);
            }, var1));
         }

         return Util.sequence(var2).thenApply((var0) -> {
            return (Map)var0.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
         });
      });
   }

   private ReloadState loadModels(ProfilerFiller var1, Map<ResourceLocation, AtlasSet.StitchResult> var2, ModelBakery var3) {
      var1.push("load");
      var1.popPush("baking");
      HashMultimap var4 = HashMultimap.create();
      var3.bakeModels((var2x, var3x) -> {
         AtlasSet.StitchResult var4x = (AtlasSet.StitchResult)var2.get(var3x.atlasLocation());
         TextureAtlasSprite var5 = var4x.getSprite(var3x.texture());
         if (var5 != null) {
            return var5;
         } else {
            var4.put(var2x, var3x);
            return var4x.missing();
         }
      });
      var4.asMap().forEach((var0, var1x) -> {
         LOGGER.warn("Missing textures in model {}:\n{}", var0, var1x.stream().sorted(Material.COMPARATOR).map((var0x) -> {
            String var10000 = String.valueOf(var0x.atlasLocation());
            return "    " + var10000 + ":" + String.valueOf(var0x.texture());
         }).collect(Collectors.joining("\n")));
      });
      var1.popPush("dispatch");
      Map var5 = var3.getBakedTopLevelModels();
      BakedModel var6 = (BakedModel)var5.get(ModelBakery.MISSING_MODEL_LOCATION);
      IdentityHashMap var7 = new IdentityHashMap();
      Iterator var8 = BuiltInRegistries.BLOCK.iterator();

      while(var8.hasNext()) {
         Block var9 = (Block)var8.next();
         var9.getStateDefinition().getPossibleStates().forEach((var3x) -> {
            ResourceLocation var4 = var3x.getBlock().builtInRegistryHolder().key().location();
            BakedModel var5x = (BakedModel)var5.getOrDefault(BlockModelShaper.stateToModelLocation(var4, var3x), var6);
            var7.put(var3x, var5x);
         });
      }

      CompletableFuture var10 = CompletableFuture.allOf((CompletableFuture[])var2.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
      var1.pop();
      var1.endTick();
      return new ReloadState(var3, var6, var7, var2, var10);
   }

   private void apply(ReloadState var1, ProfilerFiller var2) {
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

   public void close() {
      this.atlases.close();
   }

   public void updateMaxMipLevel(int var1) {
      this.maxMipmapLevels = var1;
   }

   static {
      VANILLA_ATLASES = Map.of(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("banner_patterns"), Sheets.BED_SHEET, ResourceLocation.withDefaultNamespace("beds"), Sheets.CHEST_SHEET, ResourceLocation.withDefaultNamespace("chests"), Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("shield_patterns"), Sheets.SIGN_SHEET, ResourceLocation.withDefaultNamespace("signs"), Sheets.SHULKER_SHEET, ResourceLocation.withDefaultNamespace("shulker_boxes"), Sheets.ARMOR_TRIMS_SHEET, ResourceLocation.withDefaultNamespace("armor_trims"), Sheets.DECORATED_POT_SHEET, ResourceLocation.withDefaultNamespace("decorated_pot"), TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("blocks"));
   }

   static record ReloadState(ModelBakery modelBakery, BakedModel missingModel, Map<BlockState, BakedModel> modelCache, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, CompletableFuture<Void> readyForUpload) {
      final ModelBakery modelBakery;
      final BakedModel missingModel;
      final Map<BlockState, BakedModel> modelCache;
      final Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations;
      final CompletableFuture<Void> readyForUpload;

      ReloadState(ModelBakery var1, BakedModel var2, Map<BlockState, BakedModel> var3, Map<ResourceLocation, AtlasSet.StitchResult> var4, CompletableFuture<Void> var5) {
         super();
         this.modelBakery = var1;
         this.missingModel = var2;
         this.modelCache = var3;
         this.atlasPreparations = var4;
         this.readyForUpload = var5;
      }

      public ModelBakery modelBakery() {
         return this.modelBakery;
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
