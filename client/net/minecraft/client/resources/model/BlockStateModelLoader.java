package net.minecraft.client.resources.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

public class BlockStateModelLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
   private static final String FRAME_MAP_PROPERTY = "map";
   private static final String FRAME_MAP_PROPERTY_TRUE = "map=true";
   private static final String FRAME_MAP_PROPERTY_FALSE = "map=false";
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION;
   private static final ResourceLocation GLOW_ITEM_FRAME_LOCATION;
   private static final ResourceLocation ITEM_FRAME_LOCATION;
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;
   public static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION;
   public static final ModelResourceLocation GLOW_FRAME_LOCATION;
   public static final ModelResourceLocation MAP_FRAME_LOCATION;
   public static final ModelResourceLocation FRAME_LOCATION;

   public BlockStateModelLoader() {
      super();
   }

   private static Function<ResourceLocation, StateDefinition<Block, BlockState>> definitionLocationToBlockMapper() {
      HashMap var0 = new HashMap(STATIC_DEFINITIONS);

      for(Block var2 : BuiltInRegistries.BLOCK) {
         var0.put(var2.builtInRegistryHolder().key().location(), var2.getStateDefinition());
      }

      Objects.requireNonNull(var0);
      return var0::get;
   }

   public static CompletableFuture<LoadedModels> loadBlockStates(UnbakedModel var0, ResourceManager var1, Executor var2) {
      Function var3 = definitionLocationToBlockMapper();
      return CompletableFuture.supplyAsync(() -> BLOCKSTATE_LISTER.listMatchingResourceStacks(var1), var2).thenCompose((var3x) -> {
         ArrayList var4 = new ArrayList(var3x.size());

         for(Map.Entry var6 : var3x.entrySet()) {
            var4.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var3x = BLOCKSTATE_LISTER.fileToId((ResourceLocation)var6.getKey());
               StateDefinition var4 = (StateDefinition)var3.apply(var3x);
               if (var4 == null) {
                  LOGGER.debug("Discovered unknown block state definition {}, ignoring", var3x);
                  return null;
               } else {
                  List var5 = (List)var6.getValue();
                  ArrayList var6x = new ArrayList(var5.size());

                  for(Resource var8 : var5) {
                     try {
                        BufferedReader var9 = var8.openAsReader();

                        try {
                           JsonObject var10 = GsonHelper.parse((Reader)var9);
                           BlockModelDefinition var11 = BlockModelDefinition.fromJsonElement(var10);
                           var6x.add(new LoadedBlockModelDefinition(var8.sourcePackId(), var11));
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
                     return loadBlockStateDefinitionStack(var3x, var4, var6x, var0);
                  } catch (Exception var12) {
                     LOGGER.error("Failed to load blockstate definition {}", var3x, var12);
                     return null;
                  }
               }
            }, var2));
         }

         return Util.sequence(var4).thenApply((var0x) -> {
            HashMap var1 = new HashMap();

            for(LoadedModels var3 : var0x) {
               if (var3 != null) {
                  var1.putAll(var3.models());
               }
            }

            return new LoadedModels(var1);
         });
      });
   }

   private static LoadedModels loadBlockStateDefinitionStack(ResourceLocation var0, StateDefinition<Block, BlockState> var1, List<LoadedBlockModelDefinition> var2, UnbakedModel var3) {
      HashMap var4 = new HashMap();

      for(LoadedBlockModelDefinition var6 : var2) {
         BlockModelDefinition var10000 = var6.contents;
         String var10002 = String.valueOf(var0);
         var10000.instantiate(var1, var10002 + "/" + var6.source).forEach((var2x, var3x) -> {
            ModelResourceLocation var4x = BlockModelShaper.stateToModelLocation(var0, var2x);
            var4.put(var4x, new LoadedModel(var2x, var3x));
         });
      }

      return new LoadedModels(var4);
   }

   static {
      ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
      GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
      ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
      STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION);
      GLOW_MAP_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=true");
      GLOW_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=false");
      MAP_FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=true");
      FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=false");
   }

   static record LoadedBlockModelDefinition(String source, BlockModelDefinition contents) {
      final String source;
      final BlockModelDefinition contents;

      LoadedBlockModelDefinition(String var1, BlockModelDefinition var2) {
         super();
         this.source = var1;
         this.contents = var2;
      }
   }

   public static record LoadedModel(BlockState state, UnbakedBlockStateModel model) {
      public LoadedModel(BlockState var1, UnbakedBlockStateModel var2) {
         super();
         this.state = var1;
         this.model = var2;
      }
   }

   public static record LoadedModels(Map<ModelResourceLocation, LoadedModel> models) {
      public LoadedModels(Map<ModelResourceLocation, LoadedModel> var1) {
         super();
         this.models = var1;
      }

      public Stream<ResolvableModel> forResolving() {
         return this.models.values().stream().map(LoadedModel::model);
      }

      public Map<ModelResourceLocation, UnbakedBlockStateModel> plainModels() {
         return Maps.transformValues(this.models, LoadedModel::model);
      }
   }
}
