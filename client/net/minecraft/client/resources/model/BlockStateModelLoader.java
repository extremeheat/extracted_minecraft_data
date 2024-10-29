package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

public class BlockStateModelLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
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
   private final UnbakedModel missingModel;

   public BlockStateModelLoader(UnbakedModel var1) {
      super();
      this.missingModel = var1;
   }

   public static Function<ResourceLocation, StateDefinition<Block, BlockState>> definitionLocationToBlockMapper() {
      HashMap var0 = new HashMap(STATIC_DEFINITIONS);
      Iterator var1 = BuiltInRegistries.BLOCK.iterator();

      while(var1.hasNext()) {
         Block var2 = (Block)var1.next();
         var0.put(var2.builtInRegistryHolder().key().location(), var2.getStateDefinition());
      }

      Objects.requireNonNull(var0);
      return var0::get;
   }

   public LoadedModels loadBlockStateDefinitionStack(ResourceLocation var1, StateDefinition<Block, BlockState> var2, List<LoadedBlockModelDefinition> var3) {
      ImmutableList var4 = var2.getPossibleStates();
      HashMap var5 = new HashMap();
      HashMap var6 = new HashMap();
      boolean var17 = false;

      Iterator var7;
      try {
         var17 = true;
         var7 = var3.iterator();

         while(true) {
            if (!var7.hasNext()) {
               var17 = false;
               break;
            }

            LoadedBlockModelDefinition var8 = (LoadedBlockModelDefinition)var7.next();
            BlockModelDefinition var10000 = var8.contents;
            String var10002 = String.valueOf(var1);
            var10000.instantiate(var2, var10002 + "/" + var8.source).forEach((var1x, var2x) -> {
               var5.put(var1x, new LoadedModel(var1x, var2x));
            });
         }
      } finally {
         if (var17) {
            Iterator var12 = var4.iterator();

            while(true) {
               if (!var12.hasNext()) {
                  ;
               } else {
                  BlockState var13 = (BlockState)var12.next();
                  ModelResourceLocation var14 = BlockModelShaper.stateToModelLocation(var1, var13);
                  LoadedModel var15 = (LoadedModel)var5.get(var13);
                  if (var15 == null) {
                     LOGGER.warn("Missing blockstate definition: '{}' missing model for variant: '{}'", var1, var14);
                     var15 = new LoadedModel(var13, this.missingModel);
                  }

                  var6.put(var14, var15);
               }
            }
         }
      }

      ModelResourceLocation var9;
      LoadedModel var10;
      for(var7 = var4.iterator(); var7.hasNext(); var6.put(var9, var10)) {
         BlockState var19 = (BlockState)var7.next();
         var9 = BlockModelShaper.stateToModelLocation(var1, var19);
         var10 = (LoadedModel)var5.get(var19);
         if (var10 == null) {
            LOGGER.warn("Missing blockstate definition: '{}' missing model for variant: '{}'", var1, var9);
            var10 = new LoadedModel(var19, this.missingModel);
         }
      }

      return new LoadedModels(var6);
   }

   static {
      ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
      GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
      ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
      STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION);
      GLOW_MAP_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=true");
      GLOW_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=false");
      MAP_FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=true");
      FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=false");
   }

   public static record LoadedBlockModelDefinition(String source, BlockModelDefinition contents) {
      final String source;
      final BlockModelDefinition contents;

      public LoadedBlockModelDefinition(String var1, BlockModelDefinition var2) {
         super();
         this.source = var1;
         this.contents = var2;
      }

      public String source() {
         return this.source;
      }

      public BlockModelDefinition contents() {
         return this.contents;
      }
   }

   public static record LoadedModel(BlockState state, UnbakedModel model) {
      public LoadedModel(BlockState var1, UnbakedModel var2) {
         super();
         this.state = var1;
         this.model = var2;
      }

      public BlockState state() {
         return this.state;
      }

      public UnbakedModel model() {
         return this.model;
      }
   }

   public static record LoadedModels(Map<ModelResourceLocation, LoadedModel> models) {
      public LoadedModels(Map<ModelResourceLocation, LoadedModel> var1) {
         super();
         this.models = var1;
      }

      public Map<ModelResourceLocation, LoadedModel> models() {
         return this.models;
      }
   }
}
