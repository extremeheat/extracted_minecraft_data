package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

public class ModelBakery {
   public static final Material FIRE_0;
   public static final Material FIRE_1;
   public static final Material LAVA_FLOW;
   public static final Material WATER_FLOW;
   public static final Material WATER_OVERLAY;
   public static final Material BANNER_BASE;
   public static final Material SHIELD_BASE;
   public static final Material NO_PATTERN_SHIELD;
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<ResourceLocation> DESTROY_STAGES;
   public static final List<ResourceLocation> BREAKING_LOCATIONS;
   public static final List<RenderType> DESTROY_TYPES;
   static final int SINGLETON_MODEL_GROUP = -1;
   private static final int INVISIBLE_MODEL_GROUP = 0;
   private static final Logger LOGGER;
   private static final String BUILTIN_SLASH = "builtin/";
   private static final String BUILTIN_SLASH_GENERATED = "builtin/generated";
   private static final String BUILTIN_BLOCK_ENTITY = "builtin/entity";
   private static final String MISSING_MODEL_NAME = "missing";
   public static final ModelResourceLocation MISSING_MODEL_LOCATION;
   public static final FileToIdConverter BLOCKSTATE_LISTER;
   public static final FileToIdConverter MODEL_LISTER;
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH;
   private static final Map<String, String> BUILTIN_MODELS;
   private static final Splitter COMMA_SPLITTER;
   private static final Splitter EQUAL_SPLITTER;
   public static final BlockModel GENERATION_MARKER;
   public static final BlockModel BLOCK_ENTITY_MARKER;
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION;
   static final ItemModelGenerator ITEM_MODEL_GENERATOR;
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;
   private final BlockColors blockColors;
   private final Map<ResourceLocation, BlockModel> modelResources;
   private final Map<ResourceLocation, List<LoadedJson>> blockStateResources;
   private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
   private final Map<ResourceLocation, UnbakedModel> unbakedCache = Maps.newHashMap();
   final Map<BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
   private final Map<ResourceLocation, UnbakedModel> topLevelModels = Maps.newHashMap();
   private final Map<ResourceLocation, BakedModel> bakedTopLevelModels = Maps.newHashMap();
   private int nextModelGroup = 1;
   private final Object2IntMap<BlockState> modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(-1);
   });

   public ModelBakery(BlockColors var1, ProfilerFiller var2, Map<ResourceLocation, BlockModel> var3, Map<ResourceLocation, List<LoadedJson>> var4) {
      super();
      this.blockColors = var1;
      this.modelResources = var3;
      this.blockStateResources = var4;
      var2.push("missing_model");

      try {
         this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
         this.loadTopLevel(MISSING_MODEL_LOCATION);
      } catch (IOException var7) {
         LOGGER.error("Error loading missing model, should never happen :(", var7);
         throw new RuntimeException(var7);
      }

      var2.popPush("static_definitions");
      STATIC_DEFINITIONS.forEach((var1x, var2x) -> {
         var2x.getPossibleStates().forEach((var2) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x, var2));
         });
      });
      var2.popPush("blocks");
      Iterator var5 = BuiltInRegistries.BLOCK.iterator();

      while(var5.hasNext()) {
         Block var6 = (Block)var5.next();
         var6.getStateDefinition().getPossibleStates().forEach((var1x) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x));
         });
      }

      var2.popPush("items");
      var5 = BuiltInRegistries.ITEM.keySet().iterator();

      while(var5.hasNext()) {
         ResourceLocation var8 = (ResourceLocation)var5.next();
         this.loadTopLevel(new ModelResourceLocation(var8, "inventory"));
      }

      var2.popPush("special");
      this.loadTopLevel(ItemRenderer.TRIDENT_IN_HAND_MODEL);
      this.loadTopLevel(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
      this.topLevelModels.values().forEach((var1x) -> {
         var1x.resolveParents(this::getModel);
      });
      var2.pop();
   }

   public void bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> var1) {
      this.topLevelModels.keySet().forEach((var2) -> {
         BakedModel var3 = null;

         try {
            var3 = (new ModelBakerImpl(var1, var2)).bake(var2, BlockModelRotation.X0_Y0);
         } catch (Exception var5) {
            LOGGER.warn("Unable to bake model: '{}': {}", var2, var5);
         }

         if (var3 != null) {
            this.bakedTopLevelModels.put(var2, var3);
         }

      });
   }

   private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> var0, String var1) {
      HashMap var2 = Maps.newHashMap();
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

   public UnbakedModel getModel(ResourceLocation var1) {
      if (this.unbakedCache.containsKey(var1)) {
         return (UnbakedModel)this.unbakedCache.get(var1);
      } else if (this.loadingStack.contains(var1)) {
         throw new IllegalStateException("Circular reference while loading " + String.valueOf(var1));
      } else {
         this.loadingStack.add(var1);
         UnbakedModel var2 = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);

         while(!this.loadingStack.isEmpty()) {
            ResourceLocation var3 = (ResourceLocation)this.loadingStack.iterator().next();

            try {
               if (!this.unbakedCache.containsKey(var3)) {
                  this.loadModel(var3);
               }
            } catch (BlockStateDefinitionException var9) {
               LOGGER.warn(var9.getMessage());
               this.unbakedCache.put(var3, var2);
            } catch (Exception var10) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", new Object[]{var3, var1, var10});
               this.unbakedCache.put(var3, var2);
            } finally {
               this.loadingStack.remove(var3);
            }
         }

         return (UnbakedModel)this.unbakedCache.getOrDefault(var1, var2);
      }
   }

   private void loadModel(ResourceLocation var1) throws Exception {
      if (!(var1 instanceof ModelResourceLocation var2)) {
         this.cacheAndQueueDependencies(var1, this.loadBlockModel(var1));
      } else {
         ResourceLocation var3;
         if (Objects.equals(var2.getVariant(), "inventory")) {
            var3 = var1.withPrefix("item/");
            BlockModel var4 = this.loadBlockModel(var3);
            this.cacheAndQueueDependencies(var2, var4);
            this.unbakedCache.put(var3, var4);
         } else {
            var3 = ResourceLocation.fromNamespaceAndPath(var1.getNamespace(), var1.getPath());
            StateDefinition var28 = (StateDefinition)Optional.ofNullable((StateDefinition)STATIC_DEFINITIONS.get(var3)).orElseGet(() -> {
               return ((Block)BuiltInRegistries.BLOCK.get(var3)).getStateDefinition();
            });
            this.context.setDefinition(var28);
            ImmutableList var5 = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)var28.getOwner()));
            ImmutableList var6 = var28.getPossibleStates();
            HashMap var7 = Maps.newHashMap();
            var6.forEach((var2x) -> {
               var7.put(BlockModelShaper.stateToModelLocation(var3, var2x), var2x);
            });
            HashMap var8 = Maps.newHashMap();
            ResourceLocation var9 = BLOCKSTATE_LISTER.idToFile(var1);
            UnbakedModel var10 = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);
            ModelGroupKey var11 = new ModelGroupKey(ImmutableList.of(var10), ImmutableList.of());
            Pair var12 = Pair.of(var10, () -> {
               return var11;
            });
            boolean var24 = false;

            try {
               var24 = true;
               List var13 = ((List)this.blockStateResources.getOrDefault(var9, List.of())).stream().map((var2x) -> {
                  try {
                     return Pair.of(var2x.source, BlockModelDefinition.fromJsonElement(this.context, var2x.data));
                  } catch (Exception var4) {
                     throw new BlockStateDefinitionException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", var9, var2x.source, var4.getMessage()));
                  }
               }).toList();
               Iterator var14 = var13.iterator();

               while(true) {
                  if (!var14.hasNext()) {
                     var24 = false;
                     break;
                  }

                  Pair var15 = (Pair)var14.next();
                  BlockModelDefinition var16 = (BlockModelDefinition)var15.getSecond();
                  IdentityHashMap var17 = Maps.newIdentityHashMap();
                  MultiPart var18;
                  if (var16.isMultiPart()) {
                     var18 = var16.getMultiPart();
                     var6.forEach((var3x) -> {
                        var17.put(var3x, Pair.of(var18, () -> {
                           return ModelBakery.ModelGroupKey.create(var3x, (MultiPart)var18, var5);
                        }));
                     });
                  } else {
                     var18 = null;
                  }

                  var16.getVariants().forEach((var9x, var10x) -> {
                     try {
                        var6.stream().filter(predicate(var28, var9x)).forEach((var6x) -> {
                           Pair var7 = (Pair)var17.put(var6x, Pair.of(var10x, () -> {
                              return ModelBakery.ModelGroupKey.create(var6x, (UnbakedModel)var10x, var5);
                           }));
                           if (var7 != null && var7.getFirst() != var18) {
                              var17.put(var6x, var12);
                              Optional var10002 = var16.getVariants().entrySet().stream().filter((var1) -> {
                                 return var1.getValue() == var7.getFirst();
                              }).findFirst();
                              throw new RuntimeException("Overlapping definition with: " + (String)((Map.Entry)var10002.get()).getKey());
                           }
                        });
                     } catch (Exception var12x) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", new Object[]{var9, var15.getFirst(), var9x, var12x.getMessage()});
                     }

                  });
                  var8.putAll(var17);
               }
            } catch (BlockStateDefinitionException var25) {
               throw var25;
            } catch (Exception var26) {
               throw new BlockStateDefinitionException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s': %s", var9, var26));
            } finally {
               if (var24) {
                  HashMap var20 = Maps.newHashMap();
                  var7.forEach((var5x, var6x) -> {
                     Pair var7 = (Pair)var8.get(var6x);
                     if (var7 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                        var7 = var12;
                     }

                     this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7.getFirst());

                     try {
                        ModelGroupKey var8x = (ModelGroupKey)((Supplier)var7.getSecond()).get();
                        ((Set)var29.computeIfAbsent(var8x, (var0) -> {
                           return Sets.newIdentityHashSet();
                        })).add(var6x);
                     } catch (Exception var9x) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", var5x, var9x);
                     }

                  });
                  var20.forEach((var1x, var2x) -> {
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

            HashMap var29 = Maps.newHashMap();
            var7.forEach((var5x, var6x) -> {
               Pair var7 = (Pair)var8.get(var6x);
               if (var7 == null) {
                  LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                  var7 = var12;
               }

               this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7.getFirst());

               try {
                  ModelGroupKey var8x = (ModelGroupKey)((Supplier)var7.getSecond()).get();
                  ((Set)var29.computeIfAbsent(var8x, (var0) -> {
                     return Sets.newIdentityHashSet();
                  })).add(var6x);
               } catch (Exception var9x) {
                  LOGGER.warn("Exception evaluating model definition: '{}'", var5x, var9x);
               }

            });
            var29.forEach((var1x, var2x) -> {
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
   }

   private void cacheAndQueueDependencies(ResourceLocation var1, UnbakedModel var2) {
      this.unbakedCache.put(var1, var2);
      this.loadingStack.addAll(var2.getDependencies());
   }

   private void loadTopLevel(ModelResourceLocation var1) {
      UnbakedModel var2 = this.getModel(var1);
      this.unbakedCache.put(var1, var2);
      this.topLevelModels.put(var1, var2);
   }

   private void registerModelGroup(Iterable<BlockState> var1) {
      int var2 = this.nextModelGroup++;
      var1.forEach((var2x) -> {
         this.modelGroups.put(var2x, var2);
      });
   }

   private BlockModel loadBlockModel(ResourceLocation var1) throws IOException {
      String var2 = var1.getPath();
      if ("builtin/generated".equals(var2)) {
         return GENERATION_MARKER;
      } else if ("builtin/entity".equals(var2)) {
         return BLOCK_ENTITY_MARKER;
      } else if (var2.startsWith("builtin/")) {
         String var7 = var2.substring("builtin/".length());
         String var8 = (String)BUILTIN_MODELS.get(var7);
         if (var8 == null) {
            throw new FileNotFoundException(var1.toString());
         } else {
            StringReader var5 = new StringReader(var8);
            BlockModel var6 = BlockModel.fromStream(var5);
            var6.name = var1.toString();
            return var6;
         }
      } else {
         ResourceLocation var3 = MODEL_LISTER.idToFile(var1);
         BlockModel var4 = (BlockModel)this.modelResources.get(var3);
         if (var4 == null) {
            throw new FileNotFoundException(var3.toString());
         } else {
            var4.name = var1.toString();
            return var4;
         }
      }
   }

   public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
   }

   static {
      FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
      FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
      LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
      WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
      WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
      BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
      SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
      NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((var0) -> {
         return ResourceLocation.withDefaultNamespace("block/destroy_stage_" + var0);
      }).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((var0) -> {
         return var0.withPath((var0x) -> {
            return "textures/" + var0x + ".png";
         });
      }).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
      LOGGER = LogUtils.getLogger();
      MISSING_MODEL_LOCATION = ModelResourceLocation.vanilla("builtin/missing", "missing");
      BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
      MODEL_LISTER = FileToIdConverter.json("models");
      MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureAtlasSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureAtlasSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
      BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
      COMMA_SPLITTER = Splitter.on(',');
      EQUAL_SPLITTER = Splitter.on('=').limit(2);
      GENERATION_MARKER = (BlockModel)Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), (var0) -> {
         var0.name = "generation marker";
      });
      BLOCK_ENTITY_MARKER = (BlockModel)Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), (var0) -> {
         var0.name = "block entity marker";
      });
      ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
      ITEM_MODEL_GENERATOR = new ItemModelGenerator();
      STATIC_DEFINITIONS = ImmutableMap.of(ResourceLocation.withDefaultNamespace("item_frame"), ITEM_FRAME_FAKE_DEFINITION, ResourceLocation.withDefaultNamespace("glow_item_frame"), ITEM_FRAME_FAKE_DEFINITION);
   }

   private static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
   }

   private static class ModelGroupKey {
      private final List<UnbakedModel> models;
      private final List<Object> coloringValues;

      public ModelGroupKey(List<UnbakedModel> var1, List<Object> var2) {
         super();
         this.models = var1;
         this.coloringValues = var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ModelGroupKey)) {
            return false;
         } else {
            ModelGroupKey var2 = (ModelGroupKey)var1;
            return Objects.equals(this.models, var2.models) && Objects.equals(this.coloringValues, var2.coloringValues);
         }
      }

      public int hashCode() {
         return 31 * this.models.hashCode() + this.coloringValues.hashCode();
      }

      public static ModelGroupKey create(BlockState var0, MultiPart var1, Collection<Property<?>> var2) {
         StateDefinition var3 = var0.getBlock().getStateDefinition();
         List var4 = (List)var1.getSelectors().stream().filter((var2x) -> {
            return var2x.getPredicate(var3).test(var0);
         }).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
         List var5 = getColoringValues(var0, var2);
         return new ModelGroupKey(var4, var5);
      }

      public static ModelGroupKey create(BlockState var0, UnbakedModel var1, Collection<Property<?>> var2) {
         List var3 = getColoringValues(var0, var2);
         return new ModelGroupKey(ImmutableList.of(var1), var3);
      }

      private static List<Object> getColoringValues(BlockState var0, Collection<Property<?>> var1) {
         Stream var10000 = var1.stream();
         Objects.requireNonNull(var0);
         return (List)var10000.map(var0::getValue).collect(ImmutableList.toImmutableList());
      }
   }

   public static record LoadedJson(String source, JsonElement data) {
      final String source;
      final JsonElement data;

      public LoadedJson(String var1, JsonElement var2) {
         super();
         this.source = var1;
         this.data = var2;
      }

      public String source() {
         return this.source;
      }

      public JsonElement data() {
         return this.data;
      }
   }

   private class ModelBakerImpl implements ModelBaker {
      private final Function<Material, TextureAtlasSprite> modelTextureGetter;

      ModelBakerImpl(final BiFunction<ResourceLocation, Material, TextureAtlasSprite> var2, final ResourceLocation var3) {
         super();
         this.modelTextureGetter = (var2x) -> {
            return (TextureAtlasSprite)var2.apply(var3, var2x);
         };
      }

      public UnbakedModel getModel(ResourceLocation var1) {
         return ModelBakery.this.getModel(var1);
      }

      public BakedModel bake(ResourceLocation var1, ModelState var2) {
         BakedCacheKey var3 = new BakedCacheKey(var1, var2.getRotation(), var2.isUvLocked());
         BakedModel var4 = (BakedModel)ModelBakery.this.bakedCache.get(var3);
         if (var4 != null) {
            return var4;
         } else {
            UnbakedModel var5 = this.getModel(var1);
            if (var5 instanceof BlockModel) {
               BlockModel var6 = (BlockModel)var5;
               if (var6.getRootModel() == ModelBakery.GENERATION_MARKER) {
                  return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(this.modelTextureGetter, var6).bake(this, var6, this.modelTextureGetter, var2, var1, false);
               }
            }

            BakedModel var7 = var5.bake(this, this.modelTextureGetter, var2, var1);
            ModelBakery.this.bakedCache.put(var3, var7);
            return var7;
         }
      }
   }

   private static record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
      BakedCacheKey(ResourceLocation var1, Transformation var2, boolean var3) {
         super();
         this.id = var1;
         this.transformation = var2;
         this.isUvLocked = var3;
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Transformation transformation() {
         return this.transformation;
      }

      public boolean isUvLocked() {
         return this.isUvLocked;
      }
   }
}
