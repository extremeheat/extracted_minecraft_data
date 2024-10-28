package net.minecraft.client.resources.model;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
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
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION;
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;
   private final Map<ResourceLocation, List<LoadedJson>> blockStateResources;
   private final ProfilerFiller profiler;
   private final BlockColors blockColors;
   private final BiConsumer<ModelResourceLocation, UnbakedModel> discoveredModelOutput;
   private int nextModelGroup = 1;
   private final Object2IntMap<BlockState> modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(-1);
   });
   private final LoadedModel missingModel;
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();

   public BlockStateModelLoader(Map<ResourceLocation, List<LoadedJson>> var1, ProfilerFiller var2, UnbakedModel var3, BlockColors var4, BiConsumer<ModelResourceLocation, UnbakedModel> var5) {
      super();
      this.blockStateResources = var1;
      this.profiler = var2;
      this.blockColors = var4;
      this.discoveredModelOutput = var5;
      ModelGroupKey var6 = new ModelGroupKey(List.of(var3), List.of());
      this.missingModel = new LoadedModel(var3, () -> {
         return var6;
      });
   }

   public void loadAllBlockStates() {
      this.profiler.push("static_definitions");
      STATIC_DEFINITIONS.forEach(this::loadBlockStateDefinitions);
      this.profiler.popPush("blocks");
      Iterator var1 = BuiltInRegistries.BLOCK.iterator();

      while(var1.hasNext()) {
         Block var2 = (Block)var1.next();
         this.loadBlockStateDefinitions(var2.builtInRegistryHolder().key().location(), var2.getStateDefinition());
      }

      this.profiler.pop();
   }

   private void loadBlockStateDefinitions(ResourceLocation var1, StateDefinition<Block, BlockState> var2) {
      this.context.setDefinition(var2);
      List var3 = List.copyOf(this.blockColors.getColoringProperties((Block)var2.getOwner()));
      ImmutableList var4 = var2.getPossibleStates();
      HashMap var5 = new HashMap();
      var4.forEach((var2x) -> {
         var5.put(BlockModelShaper.stateToModelLocation(var1, var2x), var2x);
      });
      HashMap var6 = new HashMap();
      ResourceLocation var7 = BLOCKSTATE_LISTER.idToFile(var1);
      boolean var18 = false;

      HashMap var22;
      label92: {
         label93: {
            try {
               var18 = true;
               Iterator var8 = ((List)this.blockStateResources.getOrDefault(var7, List.of())).iterator();

               while(var8.hasNext()) {
                  LoadedJson var9 = (LoadedJson)var8.next();
                  BlockModelDefinition var10 = var9.parse(var1, this.context);
                  IdentityHashMap var11 = new IdentityHashMap();
                  MultiPart var12;
                  if (var10.isMultiPart()) {
                     var12 = var10.getMultiPart();
                     var4.forEach((var3x) -> {
                        var11.put(var3x, new LoadedModel(var12, () -> {
                           return BlockStateModelLoader.ModelGroupKey.create(var3x, (MultiPart)var12, var3);
                        }));
                     });
                  } else {
                     var12 = null;
                  }

                  var10.getVariants().forEach((var9x, var10x) -> {
                     try {
                        var4.stream().filter(predicate(var2, var9x)).forEach((var6) -> {
                           LoadedModel var7 = (LoadedModel)var11.put(var6, new LoadedModel(var10x, () -> {
                              return BlockStateModelLoader.ModelGroupKey.create(var6, (UnbakedModel)var10x, var3);
                           }));
                           if (var7 != null && var7.model != var12) {
                              var11.put(var6, this.missingModel);
                              Optional var10002 = var10.getVariants().entrySet().stream().filter((var1) -> {
                                 return var1.getValue() == var7.model;
                              }).findFirst();
                              throw new RuntimeException("Overlapping definition with: " + (String)((Map.Entry)var10002.get()).getKey());
                           }
                        });
                     } catch (Exception var12x) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", new Object[]{var7, var9.source, var9x, var12x.getMessage()});
                     }

                  });
                  var6.putAll(var11);
               }

               var18 = false;
               break label92;
            } catch (BlockStateDefinitionException var19) {
               LOGGER.warn("{}", var19.getMessage());
               var18 = false;
               break label93;
            } catch (Exception var20) {
               LOGGER.warn("Exception loading blockstate definition: '{}'", var7, var20);
               var18 = false;
            } finally {
               if (var18) {
                  HashMap var14 = new HashMap();
                  var5.forEach((var4x, var5x) -> {
                     LoadedModel var6x = (LoadedModel)var6.get(var5x);
                     if (var6x == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var7, var4x);
                        var6x = this.missingModel;
                     }

                     this.discoveredModelOutput.accept(var4x, var6x.model);

                     try {
                        ModelGroupKey var7x = (ModelGroupKey)var6x.key().get();
                        ((Set)var22.computeIfAbsent(var7x, (var0) -> {
                           return Sets.newIdentityHashSet();
                        })).add(var5x);
                     } catch (Exception var8) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", var4x, var8);
                     }

                  });
                  var14.forEach((var1x, var2x) -> {
                     Iterator var3 = var2x.iterator();

                     while(var3.hasNext()) {
                        BlockState var4 = (BlockState)var3.next();
                        if (var4.getRenderShape() != RenderShape.MODEL) {
                           var3.remove();
                           this.modelGroups.put(var4, 0);
                        }
                     }

                     if (var2x.size() > 1) {
                        this.registerModelGroup(var2x);
                     }

                  });
               }
            }

            var22 = new HashMap();
            var5.forEach((var4x, var5x) -> {
               LoadedModel var6x = (LoadedModel)var6.get(var5x);
               if (var6x == null) {
                  LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var7, var4x);
                  var6x = this.missingModel;
               }

               this.discoveredModelOutput.accept(var4x, var6x.model);

               try {
                  ModelGroupKey var7x = (ModelGroupKey)var6x.key().get();
                  ((Set)var22.computeIfAbsent(var7x, (var0) -> {
                     return Sets.newIdentityHashSet();
                  })).add(var5x);
               } catch (Exception var8) {
                  LOGGER.warn("Exception evaluating model definition: '{}'", var4x, var8);
               }

            });
            var22.forEach((var1x, var2x) -> {
               Iterator var3 = var2x.iterator();

               while(var3.hasNext()) {
                  BlockState var4 = (BlockState)var3.next();
                  if (var4.getRenderShape() != RenderShape.MODEL) {
                     var3.remove();
                     this.modelGroups.put(var4, 0);
                  }
               }

               if (var2x.size() > 1) {
                  this.registerModelGroup(var2x);
               }

            });
            return;
         }

         var22 = new HashMap();
         var5.forEach((var4x, var5x) -> {
            LoadedModel var6x = (LoadedModel)var6.get(var5x);
            if (var6x == null) {
               LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var7, var4x);
               var6x = this.missingModel;
            }

            this.discoveredModelOutput.accept(var4x, var6x.model);

            try {
               ModelGroupKey var7x = (ModelGroupKey)var6x.key().get();
               ((Set)var22.computeIfAbsent(var7x, (var0) -> {
                  return Sets.newIdentityHashSet();
               })).add(var5x);
            } catch (Exception var8) {
               LOGGER.warn("Exception evaluating model definition: '{}'", var4x, var8);
            }

         });
         var22.forEach((var1x, var2x) -> {
            Iterator var3 = var2x.iterator();

            while(var3.hasNext()) {
               BlockState var4 = (BlockState)var3.next();
               if (var4.getRenderShape() != RenderShape.MODEL) {
                  var3.remove();
                  this.modelGroups.put(var4, 0);
               }
            }

            if (var2x.size() > 1) {
               this.registerModelGroup(var2x);
            }

         });
         return;
      }

      var22 = new HashMap();
      var5.forEach((var4x, var5x) -> {
         LoadedModel var6x = (LoadedModel)var6.get(var5x);
         if (var6x == null) {
            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var7, var4x);
            var6x = this.missingModel;
         }

         this.discoveredModelOutput.accept(var4x, var6x.model);

         try {
            ModelGroupKey var7x = (ModelGroupKey)var6x.key().get();
            ((Set)var22.computeIfAbsent(var7x, (var0) -> {
               return Sets.newIdentityHashSet();
            })).add(var5x);
         } catch (Exception var8) {
            LOGGER.warn("Exception evaluating model definition: '{}'", var4x, var8);
         }

      });
      var22.forEach((var1x, var2x) -> {
         Iterator var3 = var2x.iterator();

         while(var3.hasNext()) {
            BlockState var4 = (BlockState)var3.next();
            if (var4.getRenderShape() != RenderShape.MODEL) {
               var3.remove();
               this.modelGroups.put(var4, 0);
            }
         }

         if (var2x.size() > 1) {
            this.registerModelGroup(var2x);
         }

      });
   }

   private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> var0, String var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = COMMA_SPLITTER.split(var1).iterator();

      while(true) {
         while(true) {
            Iterator var5;
            do {
               if (!var3.hasNext()) {
                  Block var10 = (Block)var0.getOwner();
                  return (var2x) -> {
                     if (var2x != null && var2x.is(var10)) {
                        Iterator var3 = var2.entrySet().iterator();

                        Map.Entry var4;
                        do {
                           if (!var3.hasNext()) {
                              return true;
                           }

                           var4 = (Map.Entry)var3.next();
                        } while(Objects.equals(var2x.getValue((Property)var4.getKey()), var4.getValue()));

                        return false;
                     } else {
                        return false;
                     }
                  };
               }

               String var4 = (String)var3.next();
               var5 = EQUAL_SPLITTER.split(var4).iterator();
            } while(!var5.hasNext());

            String var6 = (String)var5.next();
            Property var7 = var0.getProperty(var6);
            if (var7 != null && var5.hasNext()) {
               String var8 = (String)var5.next();
               Comparable var9 = getValueHelper(var7, var8);
               if (var9 == null) {
                  throw new RuntimeException("Unknown value: '" + var8 + "' for blockstate property: '" + var6 + "' " + String.valueOf(var7.getPossibleValues()));
               }

               var2.put(var7, var9);
            } else if (!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + var6 + "'");
            }
         }
      }
   }

   @Nullable
   static <T extends Comparable<T>> T getValueHelper(Property<T> var0, String var1) {
      return (Comparable)var0.getValue(var1).orElse((Object)null);
   }

   private void registerModelGroup(Iterable<BlockState> var1) {
      int var2 = this.nextModelGroup++;
      var1.forEach((var2x) -> {
         this.modelGroups.put(var2x, var2);
      });
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
   }

   static {
      ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
      STATIC_DEFINITIONS = Map.of(ResourceLocation.withDefaultNamespace("item_frame"), ITEM_FRAME_FAKE_DEFINITION, ResourceLocation.withDefaultNamespace("glow_item_frame"), ITEM_FRAME_FAKE_DEFINITION);
   }

   private static record ModelGroupKey(List<UnbakedModel> models, List<Object> coloringValues) {
      ModelGroupKey(List<UnbakedModel> var1, List<Object> var2) {
         super();
         this.models = var1;
         this.coloringValues = var2;
      }

      public static ModelGroupKey create(BlockState var0, MultiPart var1, Collection<Property<?>> var2) {
         StateDefinition var3 = var0.getBlock().getStateDefinition();
         List var4 = (List)var1.getSelectors().stream().filter((var2x) -> {
            return var2x.getPredicate(var3).test(var0);
         }).map(Selector::getVariant).collect(Collectors.toUnmodifiableList());
         List var5 = getColoringValues(var0, var2);
         return new ModelGroupKey(var4, var5);
      }

      public static ModelGroupKey create(BlockState var0, UnbakedModel var1, Collection<Property<?>> var2) {
         List var3 = getColoringValues(var0, var2);
         return new ModelGroupKey(List.of(var1), var3);
      }

      private static List<Object> getColoringValues(BlockState var0, Collection<Property<?>> var1) {
         Stream var10000 = var1.stream();
         Objects.requireNonNull(var0);
         return (List)var10000.map(var0::getValue).collect(Collectors.toUnmodifiableList());
      }

      public List<UnbakedModel> models() {
         return this.models;
      }

      public List<Object> coloringValues() {
         return this.coloringValues;
      }
   }

   static record LoadedModel(UnbakedModel model, Supplier<ModelGroupKey> key) {
      final UnbakedModel model;

      LoadedModel(UnbakedModel var1, Supplier<ModelGroupKey> var2) {
         super();
         this.model = var1;
         this.key = var2;
      }

      public UnbakedModel model() {
         return this.model;
      }

      public Supplier<ModelGroupKey> key() {
         return this.key;
      }
   }

   public static record LoadedJson(String source, JsonElement data) {
      final String source;

      public LoadedJson(String var1, JsonElement var2) {
         super();
         this.source = var1;
         this.data = var2;
      }

      BlockModelDefinition parse(ResourceLocation var1, BlockModelDefinition.Context var2) {
         try {
            return BlockModelDefinition.fromJsonElement(var2, this.data);
         } catch (Exception var4) {
            throw new BlockStateDefinitionException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", var1, this.source, var4.getMessage()));
         }
      }

      public String source() {
         return this.source;
      }

      public JsonElement data() {
         return this.data;
      }
   }

   private static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
   }
}
