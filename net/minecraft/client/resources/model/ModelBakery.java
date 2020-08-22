package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   public static final Material FIRE_0;
   public static final Material FIRE_1;
   public static final Material LAVA_FLOW;
   public static final Material WATER_FLOW;
   public static final Material WATER_OVERLAY;
   public static final Material BANNER_BASE;
   public static final Material SHIELD_BASE;
   public static final Material NO_PATTERN_SHIELD;
   public static final List DESTROY_STAGES;
   public static final List BREAKING_LOCATIONS;
   public static final List DESTROY_TYPES;
   private static final Set UNREFERENCED_TEXTURES;
   private static final Logger LOGGER;
   public static final ModelResourceLocation MISSING_MODEL_LOCATION;
   private static final String MISSING_MODEL_LOCATION_STRING;
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH;
   private static final Map BUILTIN_MODELS;
   private static final Splitter COMMA_SPLITTER;
   private static final Splitter EQUAL_SPLITTER;
   public static final BlockModel GENERATION_MARKER;
   public static final BlockModel BLOCK_ENTITY_MARKER;
   private static final StateDefinition ITEM_FRAME_FAKE_DEFINITION;
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR;
   private static final Map STATIC_DEFINITIONS;
   private final ResourceManager resourceManager;
   @Nullable
   private AtlasSet atlasSet;
   private final BlockColors blockColors;
   private final Set loadingStack = Sets.newHashSet();
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
   private final Map unbakedCache = Maps.newHashMap();
   private final Map bakedCache = Maps.newHashMap();
   private final Map topLevelModels = Maps.newHashMap();
   private final Map bakedTopLevelModels = Maps.newHashMap();
   private final Map atlasPreparations;
   private int nextModelGroup = 1;
   private final Object2IntMap modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(-1);
   });

   public ModelBakery(ResourceManager var1, BlockColors var2, ProfilerFiller var3, int var4) {
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
      STATIC_DEFINITIONS.forEach((var1x, var2x) -> {
         var2x.getPossibleStates().forEach((var2) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x, var2));
         });
      });
      var3.popPush("blocks");
      Iterator var5 = Registry.BLOCK.iterator();

      while(var5.hasNext()) {
         Block var6 = (Block)var5.next();
         var6.getStateDefinition().getPossibleStates().forEach((var1x) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(var1x));
         });
      }

      var3.popPush("items");
      var5 = Registry.ITEM.keySet().iterator();

      while(var5.hasNext()) {
         ResourceLocation var14 = (ResourceLocation)var5.next();
         this.loadTopLevel(new ModelResourceLocation(var14, "inventory"));
      }

      var3.popPush("special");
      this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      var3.popPush("textures");
      LinkedHashSet var13 = Sets.newLinkedHashSet();
      Set var15 = (Set)this.topLevelModels.values().stream().flatMap((var2x) -> {
         return var2x.getMaterials(this::getModel, var13).stream();
      }).collect(Collectors.toSet());
      var15.addAll(UNREFERENCED_TEXTURES);
      var13.stream().filter((var0) -> {
         return !((String)var0.getSecond()).equals(MISSING_MODEL_LOCATION_STRING);
      }).forEach((var0) -> {
         LOGGER.warn("Unable to resolve texture reference: {} in {}", var0.getFirst(), var0.getSecond());
      });
      Map var7 = (Map)var15.stream().collect(Collectors.groupingBy(Material::atlasLocation));
      var3.popPush("stitching");
      this.atlasPreparations = Maps.newHashMap();
      Iterator var8 = var7.entrySet().iterator();

      while(var8.hasNext()) {
         Entry var9 = (Entry)var8.next();
         TextureAtlas var10 = new TextureAtlas((ResourceLocation)var9.getKey());
         TextureAtlas.Preparations var11 = var10.prepareToStitch(this.resourceManager, ((List)var9.getValue()).stream().map(Material::texture), var3, var4);
         this.atlasPreparations.put(var9.getKey(), Pair.of(var10, var11));
      }

      var3.pop();
   }

   public AtlasSet uploadTextures(TextureManager var1, ProfilerFiller var2) {
      var2.push("atlas");
      Iterator var3 = this.atlasPreparations.values().iterator();

      while(var3.hasNext()) {
         Pair var4 = (Pair)var3.next();
         TextureAtlas var5 = (TextureAtlas)var4.getFirst();
         TextureAtlas.Preparations var6 = (TextureAtlas.Preparations)var4.getSecond();
         var5.reload(var6);
         var1.register((ResourceLocation)var5.location(), (AbstractTexture)var5);
         var1.bind(var5.location());
         var5.updateFilter(var6);
      }

      this.atlasSet = new AtlasSet((Collection)this.atlasPreparations.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
      var2.popPush("baking");
      this.topLevelModels.keySet().forEach((var1x) -> {
         BakedModel var2 = null;

         try {
            var2 = this.bake(var1x, BlockModelRotation.X0_Y0);
         } catch (Exception var4) {
            LOGGER.warn("Unable to bake model: '{}': {}", var1x, var4);
         }

         if (var2 != null) {
            this.bakedTopLevelModels.put(var1x, var2);
         }

      });
      var2.pop();
      return this.atlasSet;
   }

   private static Predicate predicate(StateDefinition var0, String var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = COMMA_SPLITTER.split(var1).iterator();

      while(true) {
         while(true) {
            Iterator var5;
            do {
               if (!var3.hasNext()) {
                  Block var10 = (Block)var0.getOwner();
                  return (var2x) -> {
                     if (var2x != null && var10 == var2x.getBlock()) {
                        Iterator var3 = var2.entrySet().iterator();

                        Entry var4;
                        do {
                           if (!var3.hasNext()) {
                              return true;
                           }

                           var4 = (Entry)var3.next();
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
                  throw new RuntimeException("Unknown value: '" + var8 + "' for blockstate property: '" + var6 + "' " + var7.getPossibleValues());
               }

               var2.put(var7, var9);
            } else if (!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + var6 + "'");
            }
         }
      }
   }

   @Nullable
   static Comparable getValueHelper(Property var0, String var1) {
      return (Comparable)var0.getValue(var1).orElse((Object)null);
   }

   public UnbakedModel getModel(ResourceLocation var1) {
      if (this.unbakedCache.containsKey(var1)) {
         return (UnbakedModel)this.unbakedCache.get(var1);
      } else if (this.loadingStack.contains(var1)) {
         throw new IllegalStateException("Circular reference while loading " + var1);
      } else {
         this.loadingStack.add(var1);
         UnbakedModel var2 = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);

         while(!this.loadingStack.isEmpty()) {
            ResourceLocation var3 = (ResourceLocation)this.loadingStack.iterator().next();

            try {
               if (!this.unbakedCache.containsKey(var3)) {
                  this.loadModel(var3);
               }
            } catch (ModelBakery.BlockStateDefinitionException var9) {
               LOGGER.warn(var9.getMessage());
               this.unbakedCache.put(var3, var2);
            } catch (Exception var10) {
               LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", var3, var1, var10);
               this.unbakedCache.put(var3, var2);
            } finally {
               this.loadingStack.remove(var3);
            }
         }

         return (UnbakedModel)this.unbakedCache.getOrDefault(var1, var2);
      }
   }

   private void loadModel(ResourceLocation var1) throws Exception {
      if (!(var1 instanceof ModelResourceLocation)) {
         this.cacheAndQueueDependencies(var1, this.loadBlockModel(var1));
      } else {
         ModelResourceLocation var2 = (ModelResourceLocation)var1;
         ResourceLocation var3;
         if (Objects.equals(var2.getVariant(), "inventory")) {
            var3 = new ResourceLocation(var1.getNamespace(), "item/" + var1.getPath());
            BlockModel var30 = this.loadBlockModel(var3);
            this.cacheAndQueueDependencies(var2, var30);
            this.unbakedCache.put(var3, var30);
         } else {
            var3 = new ResourceLocation(var1.getNamespace(), var1.getPath());
            StateDefinition var4 = (StateDefinition)Optional.ofNullable(STATIC_DEFINITIONS.get(var3)).orElseGet(() -> {
               return ((Block)Registry.BLOCK.get(var3)).getStateDefinition();
            });
            this.context.setDefinition(var4);
            ImmutableList var5 = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)var4.getOwner()));
            ImmutableList var6 = var4.getPossibleStates();
            HashMap var7 = Maps.newHashMap();
            var6.forEach((var2x) -> {
               BlockState var10000 = (BlockState)var7.put(BlockModelShaper.stateToModelLocation(var3, var2x), var2x);
            });
            HashMap var8 = Maps.newHashMap();
            ResourceLocation var9 = new ResourceLocation(var1.getNamespace(), "blockstates/" + var1.getPath() + ".json");
            UnbakedModel var10 = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);
            ModelBakery.ModelGroupKey var11 = new ModelBakery.ModelGroupKey(ImmutableList.of(var10), ImmutableList.of());
            Pair var12 = Pair.of(var10, () -> {
               return var11;
            });
            boolean var25 = false;

            label98: {
               try {
                  label107: {
                     List var13;
                     try {
                        var25 = true;
                        var13 = (List)this.resourceManager.getResources(var9).stream().map((var1x) -> {
                           try {
                              InputStream var2 = var1x.getInputStream();
                              Throwable var3 = null;

                              Pair var4;
                              try {
                                 var4 = Pair.of(var1x.getSourceName(), BlockModelDefinition.fromStream(this.context, new InputStreamReader(var2, StandardCharsets.UTF_8)));
                              } catch (Throwable var14) {
                                 var3 = var14;
                                 throw var14;
                              } finally {
                                 if (var2 != null) {
                                    if (var3 != null) {
                                       try {
                                          var2.close();
                                       } catch (Throwable var13) {
                                          var3.addSuppressed(var13);
                                       }
                                    } else {
                                       var2.close();
                                    }
                                 }

                              }

                              return var4;
                           } catch (Exception var16) {
                              throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", var1x.getLocation(), var1x.getSourceName(), var16.getMessage()));
                           }
                        }).collect(Collectors.toList());
                     } catch (IOException var26) {
                        LOGGER.warn("Exception loading blockstate definition: {}: {}", var9, var26);
                        var25 = false;
                        break label107;
                     }

                     Iterator var14 = var13.iterator();

                     while(var14.hasNext()) {
                        Pair var15 = (Pair)var14.next();
                        BlockModelDefinition var16 = (BlockModelDefinition)var15.getSecond();
                        IdentityHashMap var17 = Maps.newIdentityHashMap();
                        MultiPart var18;
                        if (var16.isMultiPart()) {
                           var18 = var16.getMultiPart();
                           var6.forEach((var3x) -> {
                              Pair var10000 = (Pair)var17.put(var3x, Pair.of(var18, () -> {
                                 return ModelBakery.ModelGroupKey.create(var3x, (MultiPart)var18, var5);
                              }));
                           });
                        } else {
                           var18 = null;
                        }

                        var16.getVariants().forEach((var9x, var10x) -> {
                           try {
                              var6.stream().filter(predicate(var4, var9x)).forEach((var6x) -> {
                                 Pair var7 = (Pair)var17.put(var6x, Pair.of(var10x, () -> {
                                    return ModelBakery.ModelGroupKey.create(var6x, (UnbakedModel)var10x, var5);
                                 }));
                                 if (var7 != null && var7.getFirst() != var18) {
                                    var17.put(var6x, var12);
                                    throw new RuntimeException("Overlapping definition with: " + (String)((Entry)var16.getVariants().entrySet().stream().filter((var1) -> {
                                       return var1.getValue() == var7.getFirst();
                                    }).findFirst().get()).getKey());
                                 }
                              });
                           } catch (Exception var12x) {
                              LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", var9, var15.getFirst(), var9x, var12x.getMessage());
                           }

                        });
                        var8.putAll(var17);
                     }

                     var25 = false;
                     break label98;
                  }
               } catch (ModelBakery.BlockStateDefinitionException var27) {
                  throw var27;
               } catch (Exception var28) {
                  throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", var9, var28));
               } finally {
                  if (var25) {
                     HashMap var20 = Maps.newHashMap();
                     var7.forEach((var5x, var6x) -> {
                        Pair var7 = (Pair)var8.get(var6x);
                        if (var7 == null) {
                           LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                           var7 = var12;
                        }

                        this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7.getFirst());

                        try {
                           ModelBakery.ModelGroupKey var8x = (ModelBakery.ModelGroupKey)((Supplier)var7.getSecond()).get();
                           ((Set)var31.computeIfAbsent(var8x, (var0) -> {
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

               HashMap var32 = Maps.newHashMap();
               var7.forEach((var5x, var6x) -> {
                  Pair var7 = (Pair)var8.get(var6x);
                  if (var7 == null) {
                     LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                     var7 = var12;
                  }

                  this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7.getFirst());

                  try {
                     ModelBakery.ModelGroupKey var8x = (ModelBakery.ModelGroupKey)((Supplier)var7.getSecond()).get();
                     ((Set)var31.computeIfAbsent(var8x, (var0) -> {
                        return Sets.newIdentityHashSet();
                     })).add(var6x);
                  } catch (Exception var9x) {
                     LOGGER.warn("Exception evaluating model definition: '{}'", var5x, var9x);
                  }

               });
               var32.forEach((var1x, var2x) -> {
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

            HashMap var31 = Maps.newHashMap();
            var7.forEach((var5x, var6x) -> {
               Pair var7 = (Pair)var8.get(var6x);
               if (var7 == null) {
                  LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", var9, var5x);
                  var7 = var12;
               }

               this.cacheAndQueueDependencies(var5x, (UnbakedModel)var7.getFirst());

               try {
                  ModelBakery.ModelGroupKey var8x = (ModelBakery.ModelGroupKey)((Supplier)var7.getSecond()).get();
                  ((Set)var31.computeIfAbsent(var8x, (var0) -> {
                     return Sets.newIdentityHashSet();
                  })).add(var6x);
               } catch (Exception var9x) {
                  LOGGER.warn("Exception evaluating model definition: '{}'", var5x, var9x);
               }

            });
            var31.forEach((var1x, var2x) -> {
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

   private void registerModelGroup(Iterable var1) {
      int var2 = this.nextModelGroup++;
      var1.forEach((var2x) -> {
         this.modelGroups.put(var2x, var2);
      });
   }

   @Nullable
   public BakedModel bake(ResourceLocation var1, ModelState var2) {
      Triple var3 = Triple.of(var1, var2.getRotation(), var2.isUvLocked());
      if (this.bakedCache.containsKey(var3)) {
         return (BakedModel)this.bakedCache.get(var3);
      } else if (this.atlasSet == null) {
         throw new IllegalStateException("bake called too early");
      } else {
         UnbakedModel var4 = this.getModel(var1);
         if (var4 instanceof BlockModel) {
            BlockModel var5 = (BlockModel)var4;
            if (var5.getRootModel() == GENERATION_MARKER) {
               return ITEM_MODEL_GENERATOR.generateBlockModel(this.atlasSet::getSprite, var5).bake(this, var5, this.atlasSet::getSprite, var2, var1);
            }
         }

         BakedModel var6 = var4.bake(this, this.atlasSet::getSprite, var2, var1);
         this.bakedCache.put(var3, var6);
         return var6;
      }
   }

   private BlockModel loadBlockModel(ResourceLocation var1) throws IOException {
      Object var2 = null;
      Resource var3 = null;

      BlockModel var5;
      try {
         String var4 = var1.getPath();
         if ("builtin/generated".equals(var4)) {
            var5 = GENERATION_MARKER;
            return var5;
         }

         if (!"builtin/entity".equals(var4)) {
            if (var4.startsWith("builtin/")) {
               String var10 = var4.substring("builtin/".length());
               String var6 = (String)BUILTIN_MODELS.get(var10);
               if (var6 == null) {
                  throw new FileNotFoundException(var1.toString());
               }

               var2 = new StringReader(var6);
            } else {
               var3 = this.resourceManager.getResource(new ResourceLocation(var1.getNamespace(), "models/" + var1.getPath() + ".json"));
               var2 = new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8);
            }

            var5 = BlockModel.fromStream((Reader)var2);
            var5.name = var1.toString();
            BlockModel var11 = var5;
            return var11;
         }

         var5 = BLOCK_ENTITY_MARKER;
      } finally {
         IOUtils.closeQuietly((Reader)var2);
         IOUtils.closeQuietly(var3);
      }

      return var5;
   }

   public Map getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap getModelGroups() {
      return this.modelGroups;
   }

   static {
      FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
      FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
      LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
      WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
      WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
      BANNER_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/banner_base"));
      SHIELD_BASE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base"));
      NO_PATTERN_SHIELD = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/shield_base_nopattern"));
      DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((var0) -> {
         return new ResourceLocation("block/destroy_stage_" + var0);
      }).collect(Collectors.toList());
      BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((var0) -> {
         return new ResourceLocation("textures/" + var0.getPath() + ".png");
      }).collect(Collectors.toList());
      DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
      UNREFERENCED_TEXTURES = (Set)Util.make(Sets.newHashSet(), (var0) -> {
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
         Iterator var1 = DESTROY_STAGES.iterator();

         while(var1.hasNext()) {
            ResourceLocation var2 = (ResourceLocation)var1.next();
            var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, var2));
         }

         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
         var0.add(new Material(TextureAtlas.LOCATION_BLOCKS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
         Sheets.getAllMaterials(var0::add);
      });
      LOGGER = LogManager.getLogger();
      MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
      MISSING_MODEL_LOCATION_STRING = MISSING_MODEL_LOCATION.toString();
      MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureAtlasSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureAtlasSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
      BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
      COMMA_SPLITTER = Splitter.on(',');
      EQUAL_SPLITTER = Splitter.on('=').limit(2);
      GENERATION_MARKER = (BlockModel)Util.make(BlockModel.fromString("{}"), (var0) -> {
         var0.name = "generation marker";
      });
      BLOCK_ENTITY_MARKER = (BlockModel)Util.make(BlockModel.fromString("{}"), (var0) -> {
         var0.name = "block entity marker";
      });
      ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(BlockState::new);
      ITEM_MODEL_GENERATOR = new ItemModelGenerator();
      STATIC_DEFINITIONS = ImmutableMap.of(new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION);
   }

   static class ModelGroupKey {
      private final List models;
      private final List coloringValues;

      public ModelGroupKey(List var1, List var2) {
         this.models = var1;
         this.coloringValues = var2;
      }

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

      public int hashCode() {
         return 31 * this.models.hashCode() + this.coloringValues.hashCode();
      }

      public static ModelBakery.ModelGroupKey create(BlockState var0, MultiPart var1, Collection var2) {
         StateDefinition var3 = var0.getBlock().getStateDefinition();
         List var4 = (List)var1.getSelectors().stream().filter((var2x) -> {
            return var2x.getPredicate(var3).test(var0);
         }).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
         List var5 = getColoringValues(var0, var2);
         return new ModelBakery.ModelGroupKey(var4, var5);
      }

      public static ModelBakery.ModelGroupKey create(BlockState var0, UnbakedModel var1, Collection var2) {
         List var3 = getColoringValues(var0, var2);
         return new ModelBakery.ModelGroupKey(ImmutableList.of(var1), var3);
      }

      private static List getColoringValues(BlockState var0, Collection var1) {
         Stream var10000 = var1.stream();
         var0.getClass();
         return (List)var10000.map(var0::getValue).collect(ImmutableList.toImmutableList());
      }
   }

   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
   }
}
