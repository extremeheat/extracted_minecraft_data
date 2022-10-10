package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidStructure;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.feature.structure.EndCityStructure;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.feature.structure.FortressStructure;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.IglooStructure;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidStructure;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.StrongholdStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.feature.structure.WoodlandMansionStructure;
import net.minecraft.world.gen.placement.CountConfig;

public abstract class Feature<C extends IFeatureConfig> {
   private static final List<Biome.SpawnListEntry> field_202327_e = Lists.newArrayList();
   public static final Structure<VillageConfig> field_202328_f = new VillageStructure();
   public static final Structure<MineshaftConfig> field_202329_g = new MineshaftStructure();
   public static final Structure<WoodlandMansionConfig> field_202330_h = new WoodlandMansionStructure();
   public static final Structure<JunglePyramidConfig> field_202331_i = new JunglePyramidStructure();
   public static final Structure<DesertPyramidConfig> field_202332_j = new DesertPyramidStructure();
   public static final Structure<IglooConfig> field_202333_k = new IglooStructure();
   public static final Structure<ShipwreckConfig> field_204751_l = new ShipwreckStructure();
   public static final Structure<SwampHutConfig> field_202334_l = new SwampHutStructure();
   public static final Structure<StrongholdConfig> field_202335_m = new StrongholdStructure();
   public static final Structure<OceanMonumentConfig> field_202336_n = new OceanMonumentStructure();
   public static final Structure<OceanRuinConfig> field_204029_o = new OceanRuinStructure();
   public static final Structure<FortressConfig> field_202337_o = new FortressStructure();
   public static final Structure<EndCityConfig> field_202338_p = new EndCityStructure();
   public static final Structure<BuriedTreasureConfig> field_204292_r = new BuriedTreasureStructure();
   public static final AbstractTreeFeature<NoFeatureConfig> field_202339_q = new BigTreeFeature(false);
   public static final AbstractTreeFeature<NoFeatureConfig> field_202340_r = new BirchTreeFeature(false, false);
   public static final AbstractTreeFeature<NoFeatureConfig> field_202341_s = new BirchTreeFeature(false, true);
   public static final AbstractTreeFeature<NoFeatureConfig> field_202342_t;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202343_u;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202344_v;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202345_w;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202346_x;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202347_y;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202348_z;
   public static final AbstractTreeFeature<NoFeatureConfig> field_202301_A;
   public static final HugeTreesFeature<NoFeatureConfig> field_202302_B;
   public static final HugeTreesFeature<NoFeatureConfig> field_202303_C;
   public static final HugeTreesFeature<NoFeatureConfig> field_202304_D;
   public static final AbstractFlowersFeature field_202305_E;
   public static final AbstractFlowersFeature field_202306_F;
   public static final AbstractFlowersFeature field_202307_G;
   public static final AbstractFlowersFeature field_202308_H;
   public static final Feature<NoFeatureConfig> field_202309_I;
   public static final Feature<NoFeatureConfig> field_202310_J;
   public static final Feature<TallGrassConfig> field_202311_K;
   public static final Feature<NoFeatureConfig> field_202312_L;
   public static final Feature<NoFeatureConfig> field_202313_M;
   public static final Feature<NoFeatureConfig> field_202314_N;
   public static final Feature<NoFeatureConfig> field_202315_O;
   public static final Feature<NoFeatureConfig> field_202316_P;
   public static final Feature<NoFeatureConfig> field_202317_Q;
   public static final Feature<NoFeatureConfig> field_202318_R;
   public static final Feature<NoFeatureConfig> field_202319_S;
   public static final Feature<NoFeatureConfig> field_202320_T;
   public static final Feature<NoFeatureConfig> field_202321_U;
   public static final Feature<NoFeatureConfig> field_202322_V;
   public static final Feature<NoFeatureConfig> field_202323_W;
   public static final Feature<NoFeatureConfig> field_202324_X;
   public static final Feature<NoFeatureConfig> field_202325_Y;
   public static final Feature<NoFeatureConfig> field_202326_Z;
   public static final Feature<NoFeatureConfig> field_202281_aa;
   public static final Feature<NoFeatureConfig> field_202282_ab;
   public static final Feature<NoFeatureConfig> field_205171_af;
   public static final Feature<IcebergConfig> field_205172_ag;
   public static final Feature<BlockBlobConfig> field_202283_ac;
   public static final Feature<BushConfig> field_202284_ad;
   public static final Feature<SphereReplaceConfig> field_202285_ae;
   public static final Feature<DoublePlantConfig> field_202286_af;
   public static final Feature<HellLavaConfig> field_202287_ag;
   public static final Feature<FeatureRadiusConfig> field_202288_ah;
   public static final Feature<LakesConfig> field_202289_ai;
   public static final Feature<MinableConfig> field_202290_aj;
   public static final Feature<RandomFeatureListConfig> field_202291_ak;
   public static final Feature<RandomDefaultFeatureListConfig> field_202292_al;
   public static final Feature<RandomFeatureWithConfigConfig> field_204620_ao;
   public static final Feature<TwoFeatureChoiceConfig> field_202293_am;
   public static final Feature<ReplaceBlockConfig> field_202294_an;
   public static final Feature<LiquidsConfig> field_202295_ao;
   public static final Feature<NoFeatureConfig> field_202296_ap;
   public static final Feature<NoFeatureConfig> field_202297_aq;
   public static final Feature<NoFeatureConfig> field_202298_ar;
   public static final Feature<EndGatewayConfig> field_202299_as;
   public static final Feature<SeaGrassConfig> field_203234_at;
   public static final Feature<NoFeatureConfig> field_203235_au;
   public static final Feature<NoFeatureConfig> field_204621_ay;
   public static final Feature<NoFeatureConfig> field_204622_az;
   public static final Feature<NoFeatureConfig> field_204619_aA;
   public static final Feature<CountConfig> field_204914_aC;
   public static final Feature<BlockWithContextConfig> field_206922_aF;
   public static final Map<String, Structure<?>> field_202300_at;
   protected final boolean field_76488_a;

   public Feature() {
      this(false);
   }

   public Feature(boolean var1) {
      super();
      this.field_76488_a = var1;
   }

   public abstract boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, C var5);

   protected void func_202278_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if (this.field_76488_a) {
         var1.func_180501_a(var2, var3, 3);
      } else {
         var1.func_180501_a(var2, var3, 2);
      }

   }

   public List<Biome.SpawnListEntry> func_202279_e() {
      return field_202327_e;
   }

   public static boolean func_202280_a(IWorld var0, String var1, BlockPos var2) {
      return ((Structure)field_202300_at.get(var1.toLowerCase(Locale.ROOT))).func_202366_b(var0, var2);
   }

   static {
      field_202342_t = new ShrubFeature(Blocks.field_196620_N.func_176223_P(), Blocks.field_196642_W.func_176223_P());
      field_202343_u = new JungleTreeFeature(false, 4, Blocks.field_196620_N.func_176223_P(), Blocks.field_196648_Z.func_176223_P(), true);
      field_202344_v = new PointyTaigaTreeFeature();
      field_202345_w = new CanopyTreeFeature(false);
      field_202346_x = new SavannaTreeFeature(false);
      field_202347_y = new TallTaigaTreeFeature(false);
      field_202348_z = new SwampTreeFeature();
      field_202301_A = new TreeFeature(false);
      field_202302_B = new MegaJungleFeature(false, 10, 20, Blocks.field_196620_N.func_176223_P(), Blocks.field_196648_Z.func_176223_P());
      field_202303_C = new MegaPineTree(false, false);
      field_202304_D = new MegaPineTree(false, true);
      field_202305_E = new DefaultFlowersFeature();
      field_202306_F = new ForestFlowersFeature();
      field_202307_G = new PlainsFlowersFeature();
      field_202308_H = new SwampFlowersFeature();
      field_202309_I = new JungleGrassFeature();
      field_202310_J = new TaigaGrassFeature();
      field_202311_K = new TallGrassFeature();
      field_202312_L = new VoidStartPlatformFeature();
      field_202313_M = new CactusFeature();
      field_202314_N = new DeadBushFeature();
      field_202315_O = new DesertWellsFeature();
      field_202316_P = new FossilsFeature();
      field_202317_Q = new FireFeature();
      field_202318_R = new BigRedMushroomFeature();
      field_202319_S = new BigBrownMushroomFeature();
      field_202320_T = new IceSpikeFeature();
      field_202321_U = new GlowstoneFeature();
      field_202322_V = new MelonFeature();
      field_202323_W = new PumpkinFeature();
      field_202324_X = new ReedFeature();
      field_202325_Y = new IceAndSnowFeature();
      field_202326_Z = new VinesFeature();
      field_202281_aa = new WaterlilyFeature();
      field_202282_ab = new DungeonsFeature();
      field_205171_af = new BlueIceFeature();
      field_205172_ag = new IcebergFeature();
      field_202283_ac = new BlockBlobFeature();
      field_202284_ad = new BushFeature();
      field_202285_ae = new SphereReplaceFeature();
      field_202286_af = new DoublePlantFeature();
      field_202287_ag = new HellLavaFeature();
      field_202288_ah = new IcePathFeature();
      field_202289_ai = new LakesFeature();
      field_202290_aj = new MinableFeature();
      field_202291_ak = new RandomDefaultFeatureList();
      field_202292_al = new RandomFeatureList();
      field_204620_ao = new RandomFeatureWithConfigFeature();
      field_202293_am = new TwoFeatureChoiceFeature();
      field_202294_an = new ReplaceBlockFeature();
      field_202295_ao = new LiquidsFeature();
      field_202296_ap = new EndCrystalTowerFeature();
      field_202297_aq = new EndIslandFeature();
      field_202298_ar = new ChorusPlantFeature();
      field_202299_as = new EndGatewayFeature();
      field_203234_at = new SeaGrassFeature();
      field_203235_au = new KelpFeature();
      field_204621_ay = new CoralTreeFeature();
      field_204622_az = new CoralMushroomFeature();
      field_204619_aA = new CoralClawFeature();
      field_204914_aC = new SeaPickleFeature();
      field_206922_aF = new BlockWithContextFeature();
      field_202300_at = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         var0.put("Village".toLowerCase(Locale.ROOT), field_202328_f);
         var0.put("Mineshaft".toLowerCase(Locale.ROOT), field_202329_g);
         var0.put("Mansion".toLowerCase(Locale.ROOT), field_202330_h);
         var0.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), field_202331_i);
         var0.put("Desert_Pyramid".toLowerCase(Locale.ROOT), field_202332_j);
         var0.put("Igloo".toLowerCase(Locale.ROOT), field_202333_k);
         var0.put("Shipwreck".toLowerCase(Locale.ROOT), field_204751_l);
         var0.put("Swamp_Hut".toLowerCase(Locale.ROOT), field_202334_l);
         var0.put("Stronghold".toLowerCase(Locale.ROOT), field_202335_m);
         var0.put("Monument".toLowerCase(Locale.ROOT), field_202336_n);
         var0.put("Ocean_Ruin".toLowerCase(Locale.ROOT), field_204029_o);
         var0.put("Fortress".toLowerCase(Locale.ROOT), field_202337_o);
         var0.put("EndCity".toLowerCase(Locale.ROOT), field_202338_p);
         var0.put("Buried_Treasure".toLowerCase(Locale.ROOT), field_204292_r);
      });
   }
}
