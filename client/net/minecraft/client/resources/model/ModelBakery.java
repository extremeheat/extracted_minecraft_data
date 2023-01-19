package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;

public class ModelBakery {
   public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
   public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
   public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
   public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
   public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
   public static final Material BANNER_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/banner_base"));
   public static final Material SHIELD_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base"));
   public static final Material NO_PATTERN_SHIELD = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base_nopattern"));
   public static final int DESTROY_STAGE_COUNT = 10;
   public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10)
      .mapToObj(var0 -> new ResourceLocation("block/destroy_stage_" + var0))
      .collect(Collectors.toList());
   public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream()
      .map(var0 -> new ResourceLocation("textures/" + var0.getPath() + ".png"))
      .collect(Collectors.toList());
   public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
   private static final Set<Material> UNREFERENCED_TEXTURES = Util.make(Sets.newHashSet(), var0 -> {
      var0.add(WATER_FLOW);
      var0.add(LAVA_FLOW);
      var0.add(WATER_OVERLAY);
      var0.add(FIRE_0);
      var0.add(FIRE_1);
      var0.add(BellRenderer.BELL_RESOURCE_LOCATION);
      var0.add(ConduitRenderer.SHELL_TEXTURE);
      var0.add(ConduitRenderer.ACTIVE_SHELL_TEXTURE);
      var0.add(ConduitRenderer.WIND_TEXTURE);
      var0.add(ConduitRenderer.VERTICAL_WIND_TEXTURE);
      var0.add(ConduitRenderer.OPEN_EYE_TEXTURE);
      var0.add(ConduitRenderer.CLOSED_EYE_TEXTURE);
      var0.add(EnchantTableRenderer.BOOK_LOCATION);
      var0.add(BANNER_BASE);
      var0.add(SHIELD_BASE);
      var0.add(NO_PATTERN_SHIELD);

      for(ResourceLocation var2 : DESTROY_STAGES) {
         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, var2));
      }

      var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
      var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
      var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
      var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
      var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
      Sheets.getAllMaterials(var0::add);
   });
   static final int SINGLETON_MODEL_GROUP = -1;
   private static final int INVISIBLE_MODEL_GROUP = 0;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String BUILTIN_SLASH = "builtin/";
   private static final String BUILTIN_SLASH_GENERATED = "builtin/generated";
   private static final String BUILTIN_BLOCK_ENTITY = "builtin/entity";
   private static final String MISSING_MODEL_NAME = "missing";
   public static final ModelResourceLocation MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
   private static final String MISSING_MODEL_LOCATION_STRING = MISSING_MODEL_LOCATION.toString();
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '"
         + MissingTextureAtlasSprite.getLocation().getPath()
         + "',       'missingno': '"
         + MissingTextureAtlasSprite.getLocation().getPath()
         + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
      .replace('\'', '"');
   private static final Map<String, String> BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);
   public static final BlockModel GENERATION_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), var0 -> var0.name = "generation marker");
   public static final BlockModel BLOCK_ENTITY_MARKER = Util.make(
      BlockModel.fromString("{\"gui_light\": \"side\"}"), var0 -> var0.name = "block entity marker"
   );
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
      .add(BooleanProperty.create("map"))
      .create(Block::defaultBlockState, BlockState::new);
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
      new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION, new ResourceLocation("glow_item_frame"), ITEM_FRAME_FAKE_DEFINITION
   );
   private final ResourceManager resourceManager;
   @Nullable
   private AtlasSet atlasSet;
   private final BlockColors blockColors;
   private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
   private final Map<ResourceLocation, UnbakedModel> unbakedCache = Maps.newHashMap();
   private final Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache = Maps.newHashMap();
   private final Map<ResourceLocation, UnbakedModel> topLevelModels = Maps.newHashMap();
   private final Map<ResourceLocation, BakedModel> bakedTopLevelModels = Maps.newHashMap();
   private final Map<ResourceLocation, Pair<TextureAtlas, TextureAtlas.Preparations>> atlasPreparations;
   private int nextModelGroup = 1;
   private final Object2IntMap<BlockState> modelGroups = Util.make(new Object2IntOpenHashMap(), var0 -> var0.defaultReturnValue(-1));

   public ModelBakery(ResourceManager var1, BlockColors var2, ProfilerFiller var3, int var4) {
      super();
      this.resourceManager = var1;
      this.blockColors = var2;
      var3.push("missing_model");

      try {
         this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
         this.loadTopLevel(MISSING_MODEL_LOCATION);
      } catch (IOException var12) {
         LOGGER.error("Error loading missing model, should never happen :(", var12);
         throw new RuntimeException(var12);
      }

      var3.popPush("static_definitions");
      STATIC_DEFINITIONS.forEach(
         (var1x, var2x) -> var2x.getPossibleStates().forEach(var2xx -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x, var2xx)))
      );
      var3.popPush("blocks");

      for(Block var6 : Registry.BLOCK) {
         var6.getStateDefinition().getPossibleStates().forEach(var1x -> this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x)));
      }

      var3.popPush("items");

      for(ResourceLocation var15 : Registry.ITEM.keySet()) {
         this.loadTopLevel(new ModelResourceLocation(var15, "inventory"));
      }

      var3.popPush("special");
      this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      this.loadTopLevel(new ModelResourceLocation("minecraft:spyglass_in_hand#inventory"));
      var3.popPush("textures");
      LinkedHashSet var14 = Sets.newLinkedHashSet();
      Set var16 = this.topLevelModels.values().stream().flatMap(var2x -> var2x.getMaterials(this::getModel, var14).stream()).collect(Collectors.toSet());
      var16.addAll(UNREFERENCED_TEXTURES);
      var14.stream()
         .filter(var0 -> !((String)var0.getSecond()).equals(MISSING_MODEL_LOCATION_STRING))
         .forEach(var0 -> LOGGER.warn("Unable to resolve texture reference: {} in {}", var0.getFirst(), var0.getSecond()));
      Map var7 = var16.stream().collect(Collectors.groupingBy(Material::atlasLocation));
      var3.popPush("stitching");
      this.atlasPreparations = Maps.newHashMap();

      for(Entry var9 : var7.entrySet()) {
         TextureAtlas var10 = new TextureAtlas((ResourceLocation)var9.getKey());
         TextureAtlas.Preparations var11 = var10.prepareToStitch(this.resourceManager, ((List)var9.getValue()).stream().map(Material::texture), var3, var4);
         this.atlasPreparations.put((ResourceLocation)var9.getKey(), Pair.of(var10, var11));
      }

      var3.pop();
   }

   public AtlasSet uploadTextures(TextureManager var1, ProfilerFiller var2) {
      var2.push("atlas");

      for(Pair var4 : this.atlasPreparations.values()) {
         TextureAtlas var5 = (TextureAtlas)var4.getFirst();
         TextureAtlas.Preparations var6 = (TextureAtlas.Preparations)var4.getSecond();
         var5.reload(var6);
         var1.register(var5.location(), var5);
         var1.bindForSetup(var5.location());
         var5.updateFilter(var6);
      }

      this.atlasSet = new AtlasSet(this.atlasPreparations.values().stream().<TextureAtlas>map(Pair::getFirst).collect(Collectors.toList()));
      var2.popPush("baking");
      this.topLevelModels.keySet().forEach(var1x -> {
         BakedModel var2x = null;

         try {
            var2x = this.bake(var1x, BlockModelRotation.X0_Y0);
         } catch (Exception var4x) {
            LOGGER.warn("Unable to bake model: '{}': {}", var1x, var4x);
         }

         if (var2x != null) {
            this.bakedTopLevelModels.put(var1x, var2x);
         }
      });
      var2.pop();
      return this.atlasSet;
   }

   private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> var0, String var1) {
      HashMap var2 = Maps.newHashMap();

      for(String var4 : COMMA_SPLITTER.split(var1)) {
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
            for(Entry var4x : var2.entrySet()) {
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
      return (T)var0.getValue(var1).orElse((T)null);
   }

   public UnbakedModel getModel(ResourceLocation var1) {
      if (this.unbakedCache.containsKey(var1)) {
         return this.unbakedCache.get(var1);
      } else if (this.loadingStack.contains(var1)) {
         throw new IllegalStateException("Circular reference while loading " + var1);
      } else {
         this.loadingStack.add(var1);
         UnbakedModel var2 = this.unbakedCache.get(MISSING_MODEL_LOCATION);

         while(!this.loadingStack.isEmpty()) {
            ResourceLocation var3 = this.loadingStack.iterator().next();

            try {
               if (!this.unbakedCache.containsKey(var3)) {
                  this.loadModel(var3);
               }
            } catch (ModelBakery.BlockStateDefinitionException var9) {
               LOGGER.warn(var9.getMessage());
               this.unbakedCache.put(var3, var2);
            } catch (Exception var10) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", new Object[]{var3, var1, var10});
               this.unbakedCache.put(var3, var2);
            } finally {
               this.loadingStack.remove(var3);
            }
         }

         return this.unbakedCache.getOrDefault(var1, var2);
      }
   }

   private void loadModel(ResourceLocation var1) throws Exception {
      if (!(var1 instanceof ModelResourceLocation)) {
         this.cacheAndQueueDependencies(var1, this.loadBlockModel(var1));
      } else {
         ModelResourceLocation var2 = (ModelResourceLocation)var1;
         if (Objects.equals(var2.getVariant(), "inventory")) {
            ResourceLocation var3 = new ResourceLocation(var1.getNamespace(), "item/" + var1.getPath());
            BlockModel var4 = this.loadBlockModel(var3);
            this.cacheAndQueueDependencies(var2, var4);
            this.unbakedCache.put(var3, var4);
         } else {
            ResourceLocation var27 = new ResourceLocation(var1.getNamespace(), var1.getPath());
            StateDefinition var28 = Optional.ofNullable(STATIC_DEFINITIONS.get(var27)).orElseGet(() -> Registry.BLOCK.get(var27).getStateDefinition());
            this.context.setDefinition(var28);
            ImmutableList var5 = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)var28.getOwner()));
            ImmutableList var6 = var28.getPossibleStates();
            HashMap var7 = Maps.newHashMap();
            var6.forEach(var2x -> var7.put(BlockModelShaper.stateToModelLocation(var27, var2x), var2x));
            HashMap var8 = Maps.newHashMap();
            ResourceLocation var9 = new ResourceLocation(var1.getNamespace(), "blockstates/" + var1.getPath() + ".json");
            UnbakedModel var10 = this.unbakedCache.get(MISSING_MODEL_LOCATION);
            ModelBakery.ModelGroupKey var11 = new ModelBakery.ModelGroupKey(ImmutableList.of(var10), ImmutableList.of());
            Pair var12 = Pair.of(var10, (Supplier<ModelBakery.ModelGroupKey>)() -> var11);

            try {
               for(Pair var15 : this.resourceManager
                  .getResourceStack(var9)
                  .stream()
                  .map(
                     var2x -> {
                        try {
                           Pair var4x;
                           try (BufferedReader var3x = var2x.openAsReader()) {
                              var4x = Pair.of(var2x.sourcePackId(), BlockModelDefinition.fromStream(this.context, var3x));
                           }
      
                           return var4x;
                        } catch (Exception var8x) {
                           throw new ModelBakery.BlockStateDefinitionException(
                              String.format(
                                 "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", var9, var2x.sourcePackId(), var8x.getMessage()
                              )
                           );
                        }
                     }
                  )
                  .toList()) {
                  BlockModelDefinition var16 = (BlockModelDefinition)var15.getSecond();
                  IdentityHashMap var17 = Maps.newIdentityHashMap();
                  MultiPart var18;
                  if (var16.isMultiPart()) {
                     var18 = var16.getMultiPart();
                     var6.forEach(
                        var3x -> var17.put(
                              var3x, Pair.of(var18, (Supplier<ModelBakery.ModelGroupKey>)() -> ModelBakery.ModelGroupKey.create(var3x, var18, var5))
                           )
                     );
                  } else {
                     var18 = null;
                  }

                  var16.getVariants()
                     .forEach(
                        (var9x, var10x) -> {
                           try {
                              var6.stream()
                                 .filter(predicate(var28, var9x))
                                 .forEach(
                                    var6xx -> {
                                       Pair var7xx = (Pair)var17.put(
                                          var6xx,
                                          Pair.of(var10x, (Supplier<ModelBakery.ModelGroupKey>)() -> ModelBakery.ModelGroupKey.create(var6xx, var10x, var5))
                                       );
                                       if (var7xx != null && var7xx.getFirst() != var18) {
                                          var17.put(var6xx, var12);
                                          throw new RuntimeException(
                                             "Overlapping definition with: "
                                                + (String)var16.getVariants()
                                                   .entrySet()
                                                   .stream()
                                                   .filter(var1xxx -> var1xxx.getValue() == var7xx.getFirst())
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
                                 new Object[]{var9, var15.getFirst(), var9x, var12x.getMessage()}
                              );
                           }
                        }
                     );
                  var8.putAll(var17);
               }
            } catch (ModelBakery.BlockStateDefinitionException var24) {
               throw var24;
            } catch (Exception var25) {
               throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", var9, var25));
            } finally {
               HashMap var20 = Maps.newHashMap();
               var7.forEach((var5x, var6x) -> {
                  Pair var7x = (Pair)var8.get(var6x);
                  if (var7x == null) {
                     LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                     var7x = var12;
                  }

                  this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7x.getFirst());

                  try {
                     ModelBakery.ModelGroupKey var8x = (ModelBakery.ModelGroupKey)((Supplier)var7x.getSecond()).get();
                     var20.computeIfAbsent(var8x, var0 -> Sets.newIdentityHashSet()).add(var6x);
                  } catch (Exception var9x) {
                     LOGGER.warn("Exception evaluating model definition: '{}'", var5x, var9x);
                  }
               });
               var20.forEach((var1x, var2x) -> {
                  Iterator var3x = var2x.iterator();

                  while(var3x.hasNext()) {
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
      var1.forEach(var2x -> this.modelGroups.put(var2x, var2));
   }

   @Nullable
   public BakedModel bake(ResourceLocation var1, ModelState var2) {
      Triple var3 = Triple.of(var1, var2.getRotation(), var2.isUvLocked());
      if (this.bakedCache.containsKey(var3)) {
         return this.bakedCache.get(var3);
      } else if (this.atlasSet == null) {
         throw new IllegalStateException("bake called too early");
      } else {
         UnbakedModel var4 = this.getModel(var1);
         if (var4 instanceof BlockModel var5 && ((BlockModel)var5).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.generateBlockModel(this.atlasSet::getSprite, (BlockModel)var5)
               .bake(this, (BlockModel)var5, this.atlasSet::getSprite, var2, var1, false);
         }

         BakedModel var6 = var4.bake(this, this.atlasSet::getSprite, var2, var1);
         this.bakedCache.put(var3, var6);
         return var6;
      }
   }

   private BlockModel loadBlockModel(ResourceLocation var1) throws IOException {
      Object var2 = null;

      BlockModel var4;
      try {
         String var3 = var1.getPath();
         if ("builtin/generated".equals(var3)) {
            return GENERATION_MARKER;
         }

         if (!"builtin/entity".equals(var3)) {
            if (var3.startsWith("builtin/")) {
               String var9 = var3.substring("builtin/".length());
               String var5 = BUILTIN_MODELS.get(var9);
               if (var5 == null) {
                  throw new FileNotFoundException(var1.toString());
               }

               var2 = new StringReader(var5);
            } else {
               var2 = this.resourceManager.openAsReader(new ResourceLocation(var1.getNamespace(), "models/" + var1.getPath() + ".json"));
            }

            var4 = BlockModel.fromStream((Reader)var2);
            var4.name = var1.toString();
            return var4;
         }

         var4 = BLOCK_ENTITY_MARKER;
      } finally {
         IOUtils.closeQuietly((Reader)var2);
      }

      return var4;
   }

   public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap<BlockState> getModelGroups() {
      return this.modelGroups;
   }

   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
   }

   static class ModelGroupKey {
      private final List<UnbakedModel> models;
      private final List<Object> coloringValues;

      public ModelGroupKey(List<UnbakedModel> var1, List<Object> var2) {
         super();
         this.models = var1;
         this.coloringValues = var2;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ModelBakery.ModelGroupKey)) {
            return false;
         } else {
            ModelBakery.ModelGroupKey var2 = (ModelBakery.ModelGroupKey)var1;
            return Objects.equals(this.models, var2.models) && Objects.equals(this.coloringValues, var2.coloringValues);
         }
      }

      @Override
      public int hashCode() {
         return 31 * this.models.hashCode() + this.coloringValues.hashCode();
      }

      public static ModelBakery.ModelGroupKey create(BlockState var0, MultiPart var1, Collection<Property<?>> var2) {
         StateDefinition var3 = var0.getBlock().getStateDefinition();
         List var4 = var1.getSelectors()
            .stream()
            .filter(var2x -> var2x.getPredicate(var3).test(var0))
            .map(Selector::getVariant)
            .collect(ImmutableList.toImmutableList());
         List var5 = getColoringValues(var0, var2);
         return new ModelBakery.ModelGroupKey(var4, var5);
      }

      public static ModelBakery.ModelGroupKey create(BlockState var0, UnbakedModel var1, Collection<Property<?>> var2) {
         List var3 = getColoringValues(var0, var2);
         return new ModelBakery.ModelGroupKey(ImmutableList.of(var1), var3);
      }

      private static List<Object> getColoringValues(BlockState var0, Collection<Property<?>> var1) {
         return var1.stream().map(var0::getValue).collect(ImmutableList.toImmutableList());
      }
   }
}
