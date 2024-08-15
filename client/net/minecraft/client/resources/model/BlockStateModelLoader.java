package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.BlockModelShaper;
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
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
      .add(BooleanProperty.create("map"))
      .create(Block::defaultBlockState, BlockState::new);
   private static final ResourceLocation GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
   private static final ResourceLocation ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of(
      ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION
   );
   public static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=true");
   public static final ModelResourceLocation GLOW_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=false");
   public static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=true");
   public static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=false");
   private final UnbakedModel missingModel;

   public BlockStateModelLoader(UnbakedModel var1) {
      super();
      this.missingModel = var1;
   }

   public static Function<ResourceLocation, StateDefinition<Block, BlockState>> definitionLocationToBlockMapper() {
      HashMap var0 = new HashMap<>(STATIC_DEFINITIONS);

      for (Block var2 : BuiltInRegistries.BLOCK) {
         var0.put(var2.builtInRegistryHolder().key().location(), var2.getStateDefinition());
      }

      return var0::get;
   }

   public BlockStateModelLoader.LoadedModels loadBlockStateDefinitionStack(
      ResourceLocation var1, StateDefinition<Block, BlockState> var2, List<BlockStateModelLoader.LoadedBlockModelDefinition> var3
   ) {
      ImmutableList var4 = var2.getPossibleStates();
      HashMap var5 = new HashMap();
      HashMap var6 = new HashMap();

      try {
         for (BlockStateModelLoader.LoadedBlockModelDefinition var8 : var3) {
            var8.contents
               .instantiate(var2, var1 + "/" + var8.source)
               .forEach((var1x, var2x) -> var5.put(var1x, new BlockStateModelLoader.LoadedModel(var1x, var2x)));
         }
      } finally {
         Iterator var12 = var4.iterator();

         while (true) {
            if (!var12.hasNext()) {
               ;
            } else {
               BlockState var13 = (BlockState)var12.next();
               ModelResourceLocation var14 = BlockModelShaper.stateToModelLocation(var1, var13);
               BlockStateModelLoader.LoadedModel var15 = (BlockStateModelLoader.LoadedModel)var5.get(var13);
               if (var15 == null) {
                  LOGGER.warn("Missing blockstate definition: '{}' missing model for variant: '{}'", var1, var14);
                  var15 = new BlockStateModelLoader.LoadedModel(var13, this.missingModel);
               }

               var6.put(var14, var15);
            }
         }
      }

      return new BlockStateModelLoader.LoadedModels(var6);
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
