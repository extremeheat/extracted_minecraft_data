package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenSettings extends ChunkGenSettings {
   private static final Logger field_211404_q = LogManager.getLogger();
   private static final CompositeFeature<MineshaftConfig, NoPlacementConfig> field_202250_m;
   private static final CompositeFeature<VillageConfig, NoPlacementConfig> field_202251_n;
   private static final CompositeFeature<StrongholdConfig, NoPlacementConfig> field_202252_o;
   private static final CompositeFeature<SwampHutConfig, NoPlacementConfig> field_202253_p;
   private static final CompositeFeature<DesertPyramidConfig, NoPlacementConfig> field_202254_q;
   private static final CompositeFeature<JunglePyramidConfig, NoPlacementConfig> field_202255_r;
   private static final CompositeFeature<IglooConfig, NoPlacementConfig> field_202256_s;
   private static final CompositeFeature<ShipwreckConfig, NoPlacementConfig> field_204750_v;
   private static final CompositeFeature<OceanMonumentConfig, NoPlacementConfig> field_202257_t;
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> field_202258_u;
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> field_202259_v;
   private static final CompositeFeature<EndCityConfig, NoPlacementConfig> field_202260_w;
   private static final CompositeFeature<WoodlandMansionConfig, NoPlacementConfig> field_202261_x;
   private static final CompositeFeature<FortressConfig, NoPlacementConfig> field_202262_y;
   private static final CompositeFeature<OceanRuinConfig, NoPlacementConfig> field_204028_A;
   public static final Map<CompositeFeature<?, ?>, GenerationStage.Decoration> field_202248_k;
   public static final Map<String, CompositeFeature<?, ?>[]> field_202247_j;
   public static final Map<CompositeFeature<?, ?>, IFeatureConfig> field_202249_l;
   private final List<FlatLayerInfo> field_82655_a = Lists.newArrayList();
   private final Map<String, Map<String, String>> field_82653_b = Maps.newHashMap();
   private Biome field_82654_c;
   private final IBlockState[] field_202244_C = new IBlockState[256];
   private boolean field_202245_D;
   private int field_202246_E;

   public FlatGenSettings() {
      super();
   }

   @Nullable
   public static Block func_212683_a(String var0) {
      try {
         ResourceLocation var1 = new ResourceLocation(var0);
         if (IRegistry.field_212618_g.func_212607_c(var1)) {
            return (Block)IRegistry.field_212618_g.func_82594_a(var1);
         }
      } catch (IllegalArgumentException var2) {
         field_211404_q.warn("Invalid blockstate: {}", var0, var2);
      }

      return null;
   }

   public Biome func_82648_a() {
      return this.field_82654_c;
   }

   public void func_82647_a(Biome var1) {
      this.field_82654_c = var1;
   }

   public Map<String, Map<String, String>> func_82644_b() {
      return this.field_82653_b;
   }

   public List<FlatLayerInfo> func_82650_c() {
      return this.field_82655_a;
   }

   public void func_82645_d() {
      int var1 = 0;

      Iterator var2;
      FlatLayerInfo var3;
      for(var2 = this.field_82655_a.iterator(); var2.hasNext(); var1 += var3.func_82657_a()) {
         var3 = (FlatLayerInfo)var2.next();
         var3.func_82660_d(var1);
      }

      this.field_202246_E = 0;
      this.field_202245_D = true;
      var1 = 0;
      var2 = this.field_82655_a.iterator();

      while(var2.hasNext()) {
         var3 = (FlatLayerInfo)var2.next();

         for(int var4 = var3.func_82656_d(); var4 < var3.func_82656_d() + var3.func_82657_a(); ++var4) {
            IBlockState var5 = var3.func_175900_c();
            if (var5.func_177230_c() != Blocks.field_150350_a) {
               this.field_202245_D = false;
               this.field_202244_C[var4] = var5;
            }
         }

         if (var3.func_175900_c().func_177230_c() == Blocks.field_150350_a) {
            var1 += var3.func_82657_a();
         } else {
            this.field_202246_E += var3.func_82657_a() + var1;
            var1 = 0;
         }
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      int var2;
      for(var2 = 0; var2 < this.field_82655_a.size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(this.field_82655_a.get(var2));
      }

      var1.append(";");
      var1.append(IRegistry.field_212624_m.func_177774_c(this.field_82654_c));
      var1.append(";");
      if (!this.field_82653_b.isEmpty()) {
         var2 = 0;
         Iterator var3 = this.field_82653_b.entrySet().iterator();

         while(true) {
            Map var5;
            do {
               if (!var3.hasNext()) {
                  return var1.toString();
               }

               Entry var4 = (Entry)var3.next();
               if (var2++ > 0) {
                  var1.append(",");
               }

               var1.append(((String)var4.getKey()).toLowerCase(Locale.ROOT));
               var5 = (Map)var4.getValue();
            } while(var5.isEmpty());

            var1.append("(");
            int var6 = 0;
            Iterator var7 = var5.entrySet().iterator();

            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               if (var6++ > 0) {
                  var1.append(" ");
               }

               var1.append((String)var8.getKey());
               var1.append("=");
               var1.append((String)var8.getValue());
            }

            var1.append(")");
         }
      } else {
         return var1.toString();
      }
   }

   @Nullable
   private static FlatLayerInfo func_197526_a(String var0, int var1) {
      String[] var2 = var0.split("\\*", 2);
      int var3;
      if (var2.length == 2) {
         try {
            var3 = MathHelper.func_76125_a(Integer.parseInt(var2[0]), 0, 256 - var1);
         } catch (NumberFormatException var7) {
            field_211404_q.error("Error while parsing flat world string => {}", var7.getMessage());
            return null;
         }
      } else {
         var3 = 1;
      }

      Block var4;
      try {
         var4 = func_212683_a(var2[var2.length - 1]);
      } catch (Exception var6) {
         field_211404_q.error("Error while parsing flat world string => {}", var6.getMessage());
         return null;
      }

      if (var4 == null) {
         field_211404_q.error("Error while parsing flat world string => Unknown block, {}", var2[var2.length - 1]);
         return null;
      } else {
         FlatLayerInfo var5 = new FlatLayerInfo(var3, var4);
         var5.func_82660_d(var1);
         return var5;
      }
   }

   private static List<FlatLayerInfo> func_197527_b(String var0) {
      ArrayList var1 = Lists.newArrayList();
      String[] var2 = var0.split(",");
      int var3 = 0;
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         FlatLayerInfo var8 = func_197526_a(var7, var3);
         if (var8 == null) {
            return Collections.emptyList();
         }

         var1.add(var8);
         var3 += var8.func_82657_a();
      }

      return var1;
   }

   public <T> Dynamic<T> func_210834_a(DynamicOps<T> var1) {
      Object var2 = var1.createList(this.field_82655_a.stream().map((var1x) -> {
         return var1.createMap(ImmutableMap.of(var1.createString("height"), var1.createInt(var1x.func_82657_a()), var1.createString("block"), var1.createString(IRegistry.field_212618_g.func_177774_c(var1x.func_175900_c().func_177230_c()).toString())));
      }));
      Object var3 = var1.createMap((Map)this.field_82653_b.entrySet().stream().map((var1x) -> {
         return Pair.of(var1.createString(((String)var1x.getKey()).toLowerCase(Locale.ROOT)), var1.createMap((Map)((Map)var1x.getValue()).entrySet().stream().map((var1xx) -> {
            return Pair.of(var1.createString((String)var1xx.getKey()), var1.createString((String)var1xx.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("layers"), var2, var1.createString("biome"), var1.createString(IRegistry.field_212624_m.func_177774_c(this.field_82654_c).toString()), var1.createString("structures"), var3)));
   }

   public static FlatGenSettings func_210835_a(Dynamic<?> var0) {
      FlatGenSettings var1 = (FlatGenSettings)ChunkGeneratorType.field_205489_f.func_205483_a();
      List var2 = (List)((Stream)var0.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty())).map((var0x) -> {
         return Pair.of(var0x.getInt("height", 1), func_212683_a(var0x.getString("block")));
      }).collect(Collectors.toList());
      if (var2.stream().anyMatch((var0x) -> {
         return var0x.getSecond() == null;
      })) {
         return func_82649_e();
      } else {
         List var3 = (List)var2.stream().map((var0x) -> {
            return new FlatLayerInfo((Integer)var0x.getFirst(), (Block)var0x.getSecond());
         }).collect(Collectors.toList());
         if (var3.isEmpty()) {
            return func_82649_e();
         } else {
            var1.func_82650_c().addAll(var3);
            var1.func_82645_d();
            var1.func_82647_a((Biome)IRegistry.field_212624_m.func_212608_b(new ResourceLocation(var0.getString("biome"))));
            var0.get("structures").flatMap(Dynamic::getMapValues).ifPresent((var1x) -> {
               var1x.keySet().forEach((var1xx) -> {
                  var1xx.getStringValue().map((var1x) -> {
                     return (Map)var1.func_82644_b().put(var1x, Maps.newHashMap());
                  });
               });
            });
            return var1;
         }
      }
   }

   public static FlatGenSettings func_82651_a(String var0) {
      Iterator var1 = Splitter.on(';').split(var0).iterator();
      if (!var1.hasNext()) {
         return func_82649_e();
      } else {
         FlatGenSettings var2 = (FlatGenSettings)ChunkGeneratorType.field_205489_f.func_205483_a();
         List var3 = func_197527_b((String)var1.next());
         if (var3.isEmpty()) {
            return func_82649_e();
         } else {
            var2.func_82650_c().addAll(var3);
            var2.func_82645_d();
            Biome var4 = var1.hasNext() ? (Biome)IRegistry.field_212624_m.func_212608_b(new ResourceLocation((String)var1.next())) : null;
            var2.func_82647_a(var4 == null ? Biomes.field_76772_c : var4);
            if (var1.hasNext()) {
               String[] var5 = ((String)var1.next()).toLowerCase(Locale.ROOT).split(",");
               String[] var6 = var5;
               int var7 = var5.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String var9 = var6[var8];
                  String[] var10 = var9.split("\\(", 2);
                  if (!var10[0].isEmpty()) {
                     var2.func_202234_c(var10[0]);
                     if (var10.length > 1 && var10[1].endsWith(")") && var10[1].length() > 1) {
                        String[] var11 = var10[1].substring(0, var10[1].length() - 1).split(" ");
                        String[] var12 = var11;
                        int var13 = var11.length;

                        for(int var14 = 0; var14 < var13; ++var14) {
                           String var15 = var12[var14];
                           String[] var16 = var15.split("=", 2);
                           if (var16.length == 2) {
                              var2.func_202229_a(var10[0], var16[0], var16[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               var2.func_82644_b().put("village", Maps.newHashMap());
            }

            return var2;
         }
      }
   }

   private void func_202234_c(String var1) {
      HashMap var2 = Maps.newHashMap();
      this.field_82653_b.put(var1, var2);
   }

   private void func_202229_a(String var1, String var2, String var3) {
      ((Map)this.field_82653_b.get(var1)).put(var2, var3);
      if ("village".equals(var1) && "distance".equals(var2)) {
         this.field_202180_a = MathHelper.func_82714_a(var3, this.field_202180_a, 9);
      }

      if ("biome_1".equals(var1) && "distance".equals(var2)) {
         this.field_202186_g = MathHelper.func_82714_a(var3, this.field_202186_g, 9);
      }

      if ("stronghold".equals(var1)) {
         if ("distance".equals(var2)) {
            this.field_202183_d = MathHelper.func_82714_a(var3, this.field_202183_d, 1);
         } else if ("count".equals(var2)) {
            this.field_202184_e = MathHelper.func_82714_a(var3, this.field_202184_e, 1);
         } else if ("spread".equals(var2)) {
            this.field_202185_f = MathHelper.func_82714_a(var3, this.field_202185_f, 1);
         }
      }

      if ("oceanmonument".equals(var1)) {
         if ("separation".equals(var2)) {
            this.field_202182_c = MathHelper.func_82714_a(var3, this.field_202182_c, 1);
         } else if ("spacing".equals(var2)) {
            this.field_202181_b = MathHelper.func_82714_a(var3, this.field_202181_b, 1);
         }
      }

      if ("endcity".equals(var1) && "distance".equals(var2)) {
         this.field_202187_h = MathHelper.func_82714_a(var3, this.field_202187_h, 1);
      }

      if ("mansion".equals(var1) && "distance".equals(var2)) {
         this.field_202188_i = MathHelper.func_82714_a(var3, this.field_202188_i, 1);
      }

   }

   public static FlatGenSettings func_82649_e() {
      FlatGenSettings var0 = (FlatGenSettings)ChunkGeneratorType.field_205489_f.func_205483_a();
      var0.func_82647_a(Biomes.field_76772_c);
      var0.func_82650_c().add(new FlatLayerInfo(1, Blocks.field_150357_h));
      var0.func_82650_c().add(new FlatLayerInfo(2, Blocks.field_150346_d));
      var0.func_82650_c().add(new FlatLayerInfo(1, Blocks.field_196658_i));
      var0.func_82645_d();
      var0.func_82644_b().put("village", Maps.newHashMap());
      return var0;
   }

   public boolean func_202238_o() {
      return this.field_202245_D;
   }

   public IBlockState[] func_202233_q() {
      return this.field_202244_C;
   }

   static {
      field_202250_m = Biome.func_201864_a(Feature.field_202329_g, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202251_n = Biome.func_201864_a(Feature.field_202328_f, new VillageConfig(0, VillagePieces.Type.OAK), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202252_o = Biome.func_201864_a(Feature.field_202335_m, new StrongholdConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202253_p = Biome.func_201864_a(Feature.field_202334_l, new SwampHutConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202254_q = Biome.func_201864_a(Feature.field_202332_j, new DesertPyramidConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202255_r = Biome.func_201864_a(Feature.field_202331_i, new JunglePyramidConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202256_s = Biome.func_201864_a(Feature.field_202333_k, new IglooConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_204750_v = Biome.func_201864_a(Feature.field_204751_l, new ShipwreckConfig(false), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202257_t = Biome.func_201864_a(Feature.field_202336_n, new OceanMonumentConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202258_u = Biome.func_201864_a(Feature.field_202289_ai, new LakesConfig(Blocks.field_150355_j), Biome.field_201884_D, new LakeChanceConfig(4));
      field_202259_v = Biome.func_201864_a(Feature.field_202289_ai, new LakesConfig(Blocks.field_150353_l), Biome.field_201883_C, new LakeChanceConfig(80));
      field_202260_w = Biome.func_201864_a(Feature.field_202338_p, new EndCityConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202261_x = Biome.func_201864_a(Feature.field_202330_h, new WoodlandMansionConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202262_y = Biome.func_201864_a(Feature.field_202337_o, new FortressConfig(), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_204028_A = Biome.func_201864_a(Feature.field_204029_o, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F), Biome.field_201917_l, IPlacementConfig.field_202468_e);
      field_202248_k = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         var0.put(field_202250_m, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(field_202251_n, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202252_o, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(field_202253_p, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202254_q, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202255_r, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202256_s, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_204750_v, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_204028_A, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202258_u, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
         var0.put(field_202259_v, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
         var0.put(field_202260_w, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202261_x, GenerationStage.Decoration.SURFACE_STRUCTURES);
         var0.put(field_202262_y, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(field_202257_t, GenerationStage.Decoration.SURFACE_STRUCTURES);
      });
      field_202247_j = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         var0.put("mineshaft", new CompositeFeature[]{field_202250_m});
         var0.put("village", new CompositeFeature[]{field_202251_n});
         var0.put("stronghold", new CompositeFeature[]{field_202252_o});
         var0.put("biome_1", new CompositeFeature[]{field_202253_p, field_202254_q, field_202255_r, field_202256_s, field_204028_A, field_204750_v});
         var0.put("oceanmonument", new CompositeFeature[]{field_202257_t});
         var0.put("lake", new CompositeFeature[]{field_202258_u});
         var0.put("lava_lake", new CompositeFeature[]{field_202259_v});
         var0.put("endcity", new CompositeFeature[]{field_202260_w});
         var0.put("mansion", new CompositeFeature[]{field_202261_x});
         var0.put("fortress", new CompositeFeature[]{field_202262_y});
      });
      field_202249_l = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         var0.put(field_202250_m, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
         var0.put(field_202251_n, new VillageConfig(0, VillagePieces.Type.OAK));
         var0.put(field_202252_o, new StrongholdConfig());
         var0.put(field_202253_p, new SwampHutConfig());
         var0.put(field_202254_q, new DesertPyramidConfig());
         var0.put(field_202255_r, new JunglePyramidConfig());
         var0.put(field_202256_s, new IglooConfig());
         var0.put(field_204028_A, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
         var0.put(field_204750_v, new ShipwreckConfig(false));
         var0.put(field_202257_t, new OceanMonumentConfig());
         var0.put(field_202260_w, new EndCityConfig());
         var0.put(field_202261_x, new WoodlandMansionConfig());
         var0.put(field_202262_y, new FortressConfig());
      });
   }
}
