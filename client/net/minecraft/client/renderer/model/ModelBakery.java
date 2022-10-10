package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   public static final ResourceLocation field_207763_a = new ResourceLocation("block/fire_0");
   public static final ResourceLocation field_207764_b = new ResourceLocation("block/fire_1");
   public static final ResourceLocation field_207766_d = new ResourceLocation("block/lava_flow");
   public static final ResourceLocation field_207768_f = new ResourceLocation("block/water_flow");
   public static final ResourceLocation field_207769_g = new ResourceLocation("block/water_overlay");
   public static final ResourceLocation field_207770_h = new ResourceLocation("block/destroy_stage_0");
   public static final ResourceLocation field_207771_i = new ResourceLocation("block/destroy_stage_1");
   public static final ResourceLocation field_207772_j = new ResourceLocation("block/destroy_stage_2");
   public static final ResourceLocation field_207773_k = new ResourceLocation("block/destroy_stage_3");
   public static final ResourceLocation field_207774_l = new ResourceLocation("block/destroy_stage_4");
   public static final ResourceLocation field_207775_m = new ResourceLocation("block/destroy_stage_5");
   public static final ResourceLocation field_207776_n = new ResourceLocation("block/destroy_stage_6");
   public static final ResourceLocation field_207777_o = new ResourceLocation("block/destroy_stage_7");
   public static final ResourceLocation field_207778_p = new ResourceLocation("block/destroy_stage_8");
   public static final ResourceLocation field_207779_q = new ResourceLocation("block/destroy_stage_9");
   private static final Set<ResourceLocation> field_177602_b;
   private static final Logger field_177603_c;
   public static final ModelResourceLocation field_177604_a;
   @VisibleForTesting
   public static final String field_188641_d;
   private static final Map<String, String> field_177600_d;
   private static final Splitter field_209611_w;
   private static final Splitter field_209612_x;
   public static final ModelBlock field_177606_o;
   public static final ModelBlock field_177616_r;
   private static final StateContainer<Block, IBlockState> field_209613_y;
   private final IResourceManager field_177598_f;
   private final TextureMap field_177609_j;
   private final Map<ModelResourceLocation, IBakedModel> field_177605_n = Maps.newHashMap();
   private static final Map<ResourceLocation, StateContainer<Block, IBlockState>> field_209607_C;
   private final Map<ResourceLocation, IUnbakedModel> field_209608_D = Maps.newHashMap();
   private final Set<ResourceLocation> field_209609_E = Sets.newHashSet();
   private final ModelBlockDefinition.ContainerHolder field_209610_F = new ModelBlockDefinition.ContainerHolder();

   public ModelBakery(IResourceManager var1, TextureMap var2) {
      super();
      this.field_177598_f = var1;
      this.field_177609_j = var2;
   }

   private static Predicate<IBlockState> func_209605_a(StateContainer<Block, IBlockState> var0, String var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = field_209611_w.split(var1).iterator();

      while(true) {
         while(true) {
            Iterator var5;
            do {
               if (!var3.hasNext()) {
                  Block var10 = (Block)var0.func_177622_c();
                  return (var2x) -> {
                     if (var2x != null && var10 == var2x.func_177230_c()) {
                        Iterator var3 = var2.entrySet().iterator();

                        Entry var4;
                        do {
                           if (!var3.hasNext()) {
                              return true;
                           }

                           var4 = (Entry)var3.next();
                        } while(Objects.equals(var2x.func_177229_b((IProperty)var4.getKey()), var4.getValue()));

                        return false;
                     } else {
                        return false;
                     }
                  };
               }

               String var4 = (String)var3.next();
               var5 = field_209612_x.split(var4).iterator();
            } while(!var5.hasNext());

            String var6 = (String)var5.next();
            IProperty var7 = var0.func_185920_a(var6);
            if (var7 != null && var5.hasNext()) {
               String var8 = (String)var5.next();
               Comparable var9 = func_209592_a(var7, var8);
               if (var9 == null) {
                  throw new RuntimeException("Unknown value: '" + var8 + "' for blockstate property: '" + var6 + "' " + var7.func_177700_c());
               }

               var2.put(var7, var9);
            } else if (!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: '" + var6 + "'");
            }
         }
      }
   }

   @Nullable
   static <T extends Comparable<T>> T func_209592_a(IProperty<T> var0, String var1) {
      return (Comparable)var0.func_185929_b(var1).orElse((Object)null);
   }

   public IUnbakedModel func_209597_a(ResourceLocation var1) {
      if (this.field_209608_D.containsKey(var1)) {
         return (IUnbakedModel)this.field_209608_D.get(var1);
      } else if (this.field_209609_E.contains(var1)) {
         throw new IllegalStateException("Circular reference while loading " + var1);
      } else {
         this.field_209609_E.add(var1);
         IUnbakedModel var2 = (IUnbakedModel)this.field_209608_D.get(field_177604_a);

         while(!this.field_209609_E.isEmpty()) {
            ResourceLocation var3 = (ResourceLocation)this.field_209609_E.iterator().next();

            try {
               if (!this.field_209608_D.containsKey(var3)) {
                  this.func_209598_b(var3);
               }
            } catch (ModelBakery.BlockStateDefinitionException var9) {
               field_177603_c.warn(var9.getMessage());
               this.field_209608_D.put(var3, var2);
            } catch (Exception var10) {
               field_177603_c.warn("Unable to load model: '{}' referenced from: {}: {}", var3, var1, var10);
               this.field_209608_D.put(var3, var2);
            } finally {
               this.field_209609_E.remove(var3);
            }
         }

         return (IUnbakedModel)this.field_209608_D.getOrDefault(var1, var2);
      }
   }

   private void func_209598_b(ResourceLocation var1) throws Exception {
      if (!(var1 instanceof ModelResourceLocation)) {
         this.func_209593_a(var1, this.func_177594_c(var1));
      } else {
         ModelResourceLocation var2 = (ModelResourceLocation)var1;
         ResourceLocation var3;
         if (Objects.equals(var2.func_177518_c(), "inventory")) {
            var3 = new ResourceLocation(var1.func_110624_b(), "item/" + var1.func_110623_a());
            ModelBlock var27 = this.func_177594_c(var3);
            this.func_209593_a(var2, var27);
            this.field_209608_D.put(var3, var27);
         } else {
            var3 = new ResourceLocation(var1.func_110624_b(), var1.func_110623_a());
            StateContainer var4 = (StateContainer)Optional.ofNullable(field_209607_C.get(var3)).orElseGet(() -> {
               return ((Block)IRegistry.field_212618_g.func_82594_a(var3)).func_176194_O();
            });
            this.field_209610_F.func_209573_a(var4);
            ImmutableList var5 = var4.func_177619_a();
            HashMap var6 = Maps.newHashMap();
            var5.forEach((var2x) -> {
               IBlockState var10000 = (IBlockState)var6.put(BlockModelShapes.func_209553_a(var3, var2x), var2x);
            });
            HashMap var7 = Maps.newHashMap();
            ResourceLocation var8 = new ResourceLocation(var1.func_110624_b(), "blockstates/" + var1.func_110623_a() + ".json");
            boolean var22 = false;

            label161: {
               try {
                  label162: {
                     List var9;
                     try {
                        var22 = true;
                        var9 = (List)this.field_177598_f.func_199004_b(var8).stream().map((var1x) -> {
                           try {
                              InputStream var2 = var1x.func_199027_b();
                              Throwable var3 = null;

                              Pair var4;
                              try {
                                 var4 = Pair.of(var1x.func_199026_d(), ModelBlockDefinition.func_209577_a(this.field_209610_F, new InputStreamReader(var2, StandardCharsets.UTF_8)));
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
                              throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", var1x.func_199029_a(), var1x.func_199026_d(), var16.getMessage()));
                           }
                        }).collect(Collectors.toList());
                     } catch (IOException var23) {
                        field_177603_c.warn("Exception loading blockstate definition: {}: {}", var8, var23);
                        var22 = false;
                        break label162;
                     }

                     Iterator var10 = var9.iterator();

                     while(var10.hasNext()) {
                        Pair var11 = (Pair)var10.next();
                        ModelBlockDefinition var12 = (ModelBlockDefinition)var11.getSecond();
                        IdentityHashMap var13 = Maps.newIdentityHashMap();
                        Multipart var14;
                        if (var12.func_188002_b()) {
                           var14 = var12.func_188001_c();
                           var5.forEach((var2x) -> {
                              IUnbakedModel var10000 = (IUnbakedModel)var13.put(var2x, var14);
                           });
                        } else {
                           var14 = null;
                        }

                        var12.func_209578_a().forEach((var8x, var9x) -> {
                           try {
                              var5.stream().filter(func_209605_a(var4, var8x)).forEach((var5x) -> {
                                 IUnbakedModel var6 = (IUnbakedModel)var13.put(var5x, var9x);
                                 if (var6 != null && var6 != var14) {
                                    var13.put(var5x, this.field_209608_D.get(field_177604_a));
                                    throw new RuntimeException("Overlapping definition with: " + (String)((Entry)var12.func_209578_a().entrySet().stream().filter((var1) -> {
                                       return var1.getValue() == var6;
                                    }).findFirst().get()).getKey());
                                 }
                              });
                           } catch (Exception var11x) {
                              field_177603_c.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", var8, var11.getFirst(), var8x, var11x.getMessage());
                           }

                        });
                        var7.putAll(var13);
                     }

                     var22 = false;
                     break label161;
                  }
               } catch (ModelBakery.BlockStateDefinitionException var24) {
                  throw var24;
               } catch (Exception var25) {
                  throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", var8, var25));
               } finally {
                  if (var22) {
                     Iterator var16 = var6.entrySet().iterator();

                     while(var16.hasNext()) {
                        Entry var17 = (Entry)var16.next();
                        this.func_209593_a((ResourceLocation)var17.getKey(), (IUnbakedModel)var7.getOrDefault(var17.getValue(), this.field_209608_D.get(field_177604_a)));
                     }

                  }
               }

               Iterator var30 = var6.entrySet().iterator();

               while(var30.hasNext()) {
                  Entry var31 = (Entry)var30.next();
                  this.func_209593_a((ResourceLocation)var31.getKey(), (IUnbakedModel)var7.getOrDefault(var31.getValue(), this.field_209608_D.get(field_177604_a)));
               }

               return;
            }

            Iterator var28 = var6.entrySet().iterator();

            while(var28.hasNext()) {
               Entry var29 = (Entry)var28.next();
               this.func_209593_a((ResourceLocation)var29.getKey(), (IUnbakedModel)var7.getOrDefault(var29.getValue(), this.field_209608_D.get(field_177604_a)));
            }
         }

      }
   }

   private void func_209593_a(ResourceLocation var1, IUnbakedModel var2) {
      this.field_209608_D.put(var1, var2);
      this.field_209609_E.addAll(var2.func_187965_e());
   }

   private void func_209594_a(Map<ModelResourceLocation, IUnbakedModel> var1, ModelResourceLocation var2) {
      var1.put(var2, this.func_209597_a(var2));
   }

   public Map<ModelResourceLocation, IBakedModel> func_177570_a() {
      HashMap var1 = Maps.newHashMap();

      try {
         this.field_209608_D.put(field_177604_a, this.func_177594_c(field_177604_a));
         this.func_209594_a(var1, field_177604_a);
      } catch (IOException var4) {
         field_177603_c.error("Error loading missing model, should never happen :(", var4);
         throw new RuntimeException(var4);
      }

      field_209607_C.forEach((var2x, var3x) -> {
         var3x.func_177619_a().forEach((var3) -> {
            this.func_209594_a(var1, BlockModelShapes.func_209553_a(var2x, var3));
         });
      });
      Iterator var2 = IRegistry.field_212618_g.iterator();

      while(var2.hasNext()) {
         Block var3 = (Block)var2.next();
         var3.func_176194_O().func_177619_a().forEach((var2x) -> {
            this.func_209594_a(var1, BlockModelShapes.func_209554_c(var2x));
         });
      }

      var2 = IRegistry.field_212630_s.func_148742_b().iterator();

      while(var2.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var2.next();
         this.func_209594_a(var1, new ModelResourceLocation(var6, "inventory"));
      }

      this.func_209594_a(var1, new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      LinkedHashSet var5 = Sets.newLinkedHashSet();
      Set var7 = (Set)var1.values().stream().flatMap((var2x) -> {
         return var2x.func_209559_a(this::func_209597_a, var5).stream();
      }).collect(Collectors.toSet());
      var7.addAll(field_177602_b);
      var5.forEach((var0) -> {
         field_177603_c.warn("Unable to resolve texture reference: {}", var0);
      });
      this.field_177609_j.func_195426_a(this.field_177598_f, var7);
      var1.forEach((var1x, var2x) -> {
         IBakedModel var3 = null;

         try {
            var3 = var2x.func_209558_a(this::func_209597_a, this.field_177609_j::func_195424_a, ModelRotation.X0_Y0, false);
         } catch (Exception var5) {
            field_177603_c.warn("Unable to bake model: '{}': {}", var1x, var5);
         }

         if (var3 != null) {
            this.field_177605_n.put(var1x, var3);
         }

      });
      return this.field_177605_n;
   }

   private ModelBlock func_177594_c(ResourceLocation var1) throws IOException {
      Object var2 = null;
      IResource var3 = null;

      ModelBlock var11;
      try {
         String var4 = var1.func_110623_a();
         ModelBlock var10;
         if ("builtin/generated".equals(var4)) {
            var10 = field_177606_o;
            return var10;
         }

         if ("builtin/entity".equals(var4)) {
            var10 = field_177616_r;
            return var10;
         }

         if (var4.startsWith("builtin/")) {
            String var5 = var4.substring("builtin/".length());
            String var6 = (String)field_177600_d.get(var5);
            if (var6 == null) {
               throw new FileNotFoundException(var1.toString());
            }

            var2 = new StringReader(var6);
         } else {
            var3 = this.field_177598_f.func_199002_a(new ResourceLocation(var1.func_110624_b(), "models/" + var1.func_110623_a() + ".json"));
            var2 = new InputStreamReader(var3.func_199027_b(), StandardCharsets.UTF_8);
         }

         var10 = ModelBlock.func_178307_a((Reader)var2);
         var10.field_178317_b = var1.toString();
         var11 = var10;
      } finally {
         IOUtils.closeQuietly((Reader)var2);
         IOUtils.closeQuietly(var3);
      }

      return var11;
   }

   static {
      field_177602_b = Sets.newHashSet(new ResourceLocation[]{field_207768_f, field_207766_d, field_207769_g, field_207763_a, field_207764_b, field_207770_h, field_207771_i, field_207772_j, field_207773_k, field_207774_l, field_207775_m, field_207776_n, field_207777_o, field_207778_p, field_207779_q, new ResourceLocation("item/empty_armor_slot_helmet"), new ResourceLocation("item/empty_armor_slot_chestplate"), new ResourceLocation("item/empty_armor_slot_leggings"), new ResourceLocation("item/empty_armor_slot_boots"), new ResourceLocation("item/empty_armor_slot_shield")});
      field_177603_c = LogManager.getLogger();
      field_177604_a = new ModelResourceLocation("builtin/missing", "missing");
      field_188641_d = ("{    'textures': {       'particle': '" + MissingTextureSprite.func_195677_a().func_195668_m().func_110623_a() + "',       'missingno': '" + MissingTextureSprite.func_195677_a().func_195668_m().func_110623_a() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
      field_177600_d = Maps.newHashMap(ImmutableMap.of("missing", field_188641_d));
      field_209611_w = Splitter.on(',');
      field_209612_x = Splitter.on('=').limit(2);
      field_177606_o = (ModelBlock)Util.func_200696_a(ModelBlock.func_178294_a("{}"), (var0) -> {
         var0.field_178317_b = "generation marker";
      });
      field_177616_r = (ModelBlock)Util.func_200696_a(ModelBlock.func_178294_a("{}"), (var0) -> {
         var0.field_178317_b = "block entity marker";
      });
      field_209613_y = (new StateContainer.Builder(Blocks.field_150350_a)).func_206894_a(BooleanProperty.func_177716_a("map")).func_206893_a(BlockState::new);
      field_209607_C = ImmutableMap.of(new ResourceLocation("item_frame"), field_209613_y);
   }

   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String var1) {
         super(var1);
      }
   }
}
