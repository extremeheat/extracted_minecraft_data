package net.minecraft.client.resources.model;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class BlockStateModelLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final int SINGLETON_MODEL_GROUP = -1;
   private static final int INVISIBLE_MODEL_GROUP = 0;
   public static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
      .add(BooleanProperty.create("map"))
      .create(Block::defaultBlockState, BlockState::new);
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of(
      ResourceLocation.withDefaultNamespace("item_frame"),
      ITEM_FRAME_FAKE_DEFINITION,
      ResourceLocation.withDefaultNamespace("glow_item_frame"),
      ITEM_FRAME_FAKE_DEFINITION
   );
   private final Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources;
   private final ProfilerFiller profiler;
   private final BlockColors blockColors;
   private final BiConsumer<ModelResourceLocation, UnbakedModel> discoveredModelOutput;
   private int nextModelGroup = 1;
   private final Object2IntMap<BlockState> modelGroups = Util.make(new Object2IntOpenHashMap(), var0 -> var0.defaultReturnValue(-1));
   private final BlockStateModelLoader.LoadedModel missingModel;
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();

   public BlockStateModelLoader(
      Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> var1,
      ProfilerFiller var2,
      UnbakedModel var3,
      BlockColors var4,
      BiConsumer<ModelResourceLocation, UnbakedModel> var5
   ) {
      super();
      this.blockStateResources = var1;
      this.profiler = var2;
      this.blockColors = var4;
      this.discoveredModelOutput = var5;
      BlockStateModelLoader.ModelGroupKey var6 = new BlockStateModelLoader.ModelGroupKey(List.of(var3), List.of());
      this.missingModel = new BlockStateModelLoader.LoadedModel(var3, () -> var6);
   }

   public void loadAllBlockStates() {
      this.profiler.push("static_definitions");
      STATIC_DEFINITIONS.forEach(this::loadBlockStateDefinitions);
      this.profiler.popPush("blocks");

      for (Block var2 : BuiltInRegistries.BLOCK) {
         this.loadBlockStateDefinitions(var2.builtInRegistryHolder().key().location(), var2.getStateDefinition());
      }

      this.profiler.pop();
   }

   private void loadBlockStateDefinitions(ResourceLocation var1, StateDefinition<Block, BlockState> var2) {
      this.context.setDefinition((StateDefinition<Block, BlockState>)var2);
      List var3 = List.copyOf(this.blockColors.getColoringProperties(var2.getOwner()));
      ImmutableList var4 = var2.getPossibleStates();
      HashMap var5 = new HashMap();
      var4.forEach(var2x -> var5.put(BlockModelShaper.stateToModelLocation(var1, var2x), var2x));
      HashMap var6 = new HashMap();
      ResourceLocation var7 = BLOCKSTATE_LISTER.idToFile(var1);

      try {
         for (BlockStateModelLoader.LoadedJson var9 : this.blockStateResources.getOrDefault(var7, List.of())) {
            BlockModelDefinition var10 = var9.parse(var1, this.context);
            IdentityHashMap var11 = new IdentityHashMap();
            MultiPart var12;
            if (var10.isMultiPart()) {
               var12 = var10.getMultiPart();
               var4.forEach(
                  var3x -> var11.put(var3x, new BlockStateModelLoader.LoadedModel(var12, () -> BlockStateModelLoader.ModelGroupKey.create(var3x, var12, var3)))
               );
            } else {
               var12 = null;
            }

            var10.getVariants()
               .forEach(
                  (var9x, var10x) -> {
                     try {
                        var4.stream()
                           .filter(predicate(var2, var9x))
                           .forEach(
                              var6xx -> {
                                 BlockStateModelLoader.LoadedModel var7xx = var11.put(
                                    var6xx,
                                    new BlockStateModelLoader.LoadedModel(var10x, () -> BlockStateModelLoader.ModelGroupKey.create(var6xx, var10x, var3))
                                 );
                                 if (var7xx != null && var7xx.model != var12) {
                                    var11.put(var6xx, this.missingModel);
                                    throw new RuntimeException(
                                       "Overlapping definition with: "
                                          + var10.getVariants()
                                             .entrySet()
                                             .stream()
                                             .filter(var1xxx -> var1xxx.getValue() == var7xx.model)
                                             .findFirst()
                                             .get()
                                             .getKey()
                                    );
                                 }
                              }
                           );
                     } catch (Exception var12x) {
                        LOGGER.warn(
                           "Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
                           new Object[]{var7, var9.source, var9x, var12x.getMessage()}
                        );
                     }
                  }
               );
            var6.putAll(var11);
         }
      } catch (BlockStateModelLoader.BlockStateDefinitionException var18) {
         LOGGER.warn("{}", var18.getMessage());
      } catch (Exception var19) {
         LOGGER.warn("Exception loading blockstate definition: '{}'", var7, var19);
      } finally {
         HashMap var14 = new HashMap();
         var5.forEach((var4x, var5x) -> {
            BlockStateModelLoader.LoadedModel var6x = (BlockStateModelLoader.LoadedModel)var6.get(var5x);
            if (var6x == null) {
               LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var7, var4x);
               var6x = this.missingModel;
            }

            this.discoveredModelOutput.accept(var4x, var6x.model);

            try {
               BlockStateModelLoader.ModelGroupKey var7x = var6x.key().get();
               var14.computeIfAbsent(var7x, var0 -> Sets.newIdentityHashSet()).add(var5x);
            } catch (Exception var8) {
               LOGGER.warn("Exception evaluating model definition: '{}'", var4x, var8);
            }
         });
         var14.forEach((var1x, var2x) -> {
            Iterator var3x = var2x.iterator();

            while (var3x.hasNext()) {
               BlockState var4x = (BlockState)var3x.next();
               if (var4x.getRenderShape() != RenderShape.MODEL) {
                  var3x.remove();
                  this.modelGroups.put(var4x, 0);
               }
            }

            if (var2x.size() > 1) {
               this.registerModelGroup(var2x);
            }
         });
      }
   }

   private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> var0, String var1) {
      HashMap var2 = new HashMap();

      for (String var4 : COMMA_SPLITTER.split(var1)) {
         Iterator var5 = EQUAL_SPLITTER.split(var4).iterator();
         if (var5.hasNext()) {
            String var6 = (String)var5.next();
            Property var7 = var0.getProperty(var6);
            if (var7 != null && var5.hasNext()) {
               String var8 = (String)var5.next();
               Comparable var9 = getValueHelper(var7, var8);
               if (var9 == null) {
                  throw new RuntimeException("Unknown value: '" + var8 + "' for blockstate property: '" + var6 + "' " + var7.getPossibleValues());
               }

               var2.put(var7, var9);
            } else if (!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + var6 + "'");
            }
         }
      }

      Block var10 = (Block)var0.getOwner();
      return var2x -> {
         if (var2x != null && var2x.is(var10)) {
            for (Entry var4x : var2.entrySet()) {
               if (!Objects.equals(var2x.getValue((Property)var4x.getKey()), var4x.getValue())) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      };
   }

   @Nullable
   static <T extends Comparable<T>> T getValueHelper(Property<T> var0, String var1) {
      return (T)var0.getValue(var1).orElse(null);
   }

   private void registerModelGroup(Iterable<BlockState> var1) {
      int var2 = this.nextModelGroup++;
      var1.forEach(var2x -> this.modelGroups.put(var2x, var2));
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
   }

   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
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
