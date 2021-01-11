package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeGenBase {
   private static final Logger field_150586_aC = LogManager.getLogger();
   protected static final BiomeGenBase.Height field_150596_a = new BiomeGenBase.Height(0.1F, 0.2F);
   protected static final BiomeGenBase.Height field_150594_b = new BiomeGenBase.Height(-0.5F, 0.0F);
   protected static final BiomeGenBase.Height field_150595_c = new BiomeGenBase.Height(-1.0F, 0.1F);
   protected static final BiomeGenBase.Height field_150592_d = new BiomeGenBase.Height(-1.8F, 0.1F);
   protected static final BiomeGenBase.Height field_150593_e = new BiomeGenBase.Height(0.125F, 0.05F);
   protected static final BiomeGenBase.Height field_150590_f = new BiomeGenBase.Height(0.2F, 0.2F);
   protected static final BiomeGenBase.Height field_150591_g = new BiomeGenBase.Height(0.45F, 0.3F);
   protected static final BiomeGenBase.Height field_150602_h = new BiomeGenBase.Height(1.5F, 0.025F);
   protected static final BiomeGenBase.Height field_150603_i = new BiomeGenBase.Height(1.0F, 0.5F);
   protected static final BiomeGenBase.Height field_150600_j = new BiomeGenBase.Height(0.0F, 0.025F);
   protected static final BiomeGenBase.Height field_150601_k = new BiomeGenBase.Height(0.1F, 0.8F);
   protected static final BiomeGenBase.Height field_150598_l = new BiomeGenBase.Height(0.2F, 0.3F);
   protected static final BiomeGenBase.Height field_150599_m = new BiomeGenBase.Height(-0.2F, 0.1F);
   private static final BiomeGenBase[] field_76773_a = new BiomeGenBase[256];
   public static final Set<BiomeGenBase> field_150597_n = Sets.newHashSet();
   public static final Map<String, BiomeGenBase> field_180278_o = Maps.newHashMap();
   public static final BiomeGenBase field_76771_b;
   public static final BiomeGenBase field_76772_c;
   public static final BiomeGenBase field_76769_d;
   public static final BiomeGenBase field_76770_e;
   public static final BiomeGenBase field_76767_f;
   public static final BiomeGenBase field_76768_g;
   public static final BiomeGenBase field_76780_h;
   public static final BiomeGenBase field_76781_i;
   public static final BiomeGenBase field_76778_j;
   public static final BiomeGenBase field_76779_k;
   public static final BiomeGenBase field_76776_l;
   public static final BiomeGenBase field_76777_m;
   public static final BiomeGenBase field_76774_n;
   public static final BiomeGenBase field_76775_o;
   public static final BiomeGenBase field_76789_p;
   public static final BiomeGenBase field_76788_q;
   public static final BiomeGenBase field_76787_r;
   public static final BiomeGenBase field_76786_s;
   public static final BiomeGenBase field_76785_t;
   public static final BiomeGenBase field_76784_u;
   public static final BiomeGenBase field_76783_v;
   public static final BiomeGenBase field_76782_w;
   public static final BiomeGenBase field_76792_x;
   public static final BiomeGenBase field_150574_L;
   public static final BiomeGenBase field_150575_M;
   public static final BiomeGenBase field_150576_N;
   public static final BiomeGenBase field_150577_O;
   public static final BiomeGenBase field_150583_P;
   public static final BiomeGenBase field_150582_Q;
   public static final BiomeGenBase field_150585_R;
   public static final BiomeGenBase field_150584_S;
   public static final BiomeGenBase field_150579_T;
   public static final BiomeGenBase field_150578_U;
   public static final BiomeGenBase field_150581_V;
   public static final BiomeGenBase field_150580_W;
   public static final BiomeGenBase field_150588_X;
   public static final BiomeGenBase field_150587_Y;
   public static final BiomeGenBase field_150589_Z;
   public static final BiomeGenBase field_150607_aa;
   public static final BiomeGenBase field_150608_ab;
   public static final BiomeGenBase field_180279_ad;
   protected static final NoiseGeneratorPerlin field_150605_ac;
   protected static final NoiseGeneratorPerlin field_180281_af;
   protected static final WorldGenDoublePlant field_180280_ag;
   public String field_76791_y;
   public int field_76790_z;
   public int field_150609_ah;
   public IBlockState field_76752_A;
   public IBlockState field_76753_B;
   public int field_76754_C;
   public float field_76748_D;
   public float field_76749_E;
   public float field_76750_F;
   public float field_76751_G;
   public int field_76759_H;
   public BiomeDecorator field_76760_I;
   protected List<BiomeGenBase.SpawnListEntry> field_76761_J;
   protected List<BiomeGenBase.SpawnListEntry> field_76762_K;
   protected List<BiomeGenBase.SpawnListEntry> field_76755_L;
   protected List<BiomeGenBase.SpawnListEntry> field_82914_M;
   protected boolean field_76766_R;
   protected boolean field_76765_S;
   public final int field_76756_M;
   protected WorldGenTrees field_76757_N;
   protected WorldGenBigTree field_76758_O;
   protected WorldGenSwamp field_76763_Q;

   protected BiomeGenBase(int var1) {
      super();
      this.field_76752_A = Blocks.field_150349_c.func_176223_P();
      this.field_76753_B = Blocks.field_150346_d.func_176223_P();
      this.field_76754_C = 5169201;
      this.field_76748_D = field_150596_a.field_150777_a;
      this.field_76749_E = field_150596_a.field_150776_b;
      this.field_76750_F = 0.5F;
      this.field_76751_G = 0.5F;
      this.field_76759_H = 16777215;
      this.field_76761_J = Lists.newArrayList();
      this.field_76762_K = Lists.newArrayList();
      this.field_76755_L = Lists.newArrayList();
      this.field_82914_M = Lists.newArrayList();
      this.field_76765_S = true;
      this.field_76757_N = new WorldGenTrees(false);
      this.field_76758_O = new WorldGenBigTree(false);
      this.field_76763_Q = new WorldGenSwamp();
      this.field_76756_M = var1;
      field_76773_a[var1] = this;
      this.field_76760_I = this.func_76729_a();
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 12, 4, 4));
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityRabbit.class, 10, 3, 3));
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 10, 4, 4));
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 8, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1));
      this.field_76755_L.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 10, 4, 4));
      this.field_82914_M.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
   }

   protected BiomeDecorator func_76729_a() {
      return new BiomeDecorator();
   }

   protected BiomeGenBase func_76732_a(float var1, float var2) {
      if (var1 > 0.1F && var1 < 0.2F) {
         throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
      } else {
         this.field_76750_F = var1;
         this.field_76751_G = var2;
         return this;
      }
   }

   protected final BiomeGenBase func_150570_a(BiomeGenBase.Height var1) {
      this.field_76748_D = var1.field_150777_a;
      this.field_76749_E = var1.field_150776_b;
      return this;
   }

   protected BiomeGenBase func_76745_m() {
      this.field_76765_S = false;
      return this;
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return (WorldGenAbstractTree)(var1.nextInt(10) == 0 ? this.field_76758_O : this.field_76757_N);
   }

   public WorldGenerator func_76730_b(Random var1) {
      return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
   }

   public BlockFlower.EnumFlowerType func_180623_a(Random var1, BlockPos var2) {
      return var1.nextInt(3) > 0 ? BlockFlower.EnumFlowerType.DANDELION : BlockFlower.EnumFlowerType.POPPY;
   }

   protected BiomeGenBase func_76742_b() {
      this.field_76766_R = true;
      return this;
   }

   protected BiomeGenBase func_76735_a(String var1) {
      this.field_76791_y = var1;
      return this;
   }

   protected BiomeGenBase func_76733_a(int var1) {
      this.field_76754_C = var1;
      return this;
   }

   protected BiomeGenBase func_76739_b(int var1) {
      this.func_150557_a(var1, false);
      return this;
   }

   protected BiomeGenBase func_150563_c(int var1) {
      this.field_150609_ah = var1;
      return this;
   }

   protected BiomeGenBase func_150557_a(int var1, boolean var2) {
      this.field_76790_z = var1;
      if (var2) {
         this.field_150609_ah = (var1 & 16711422) >> 1;
      } else {
         this.field_150609_ah = var1;
      }

      return this;
   }

   public int func_76731_a(float var1) {
      var1 /= 3.0F;
      var1 = MathHelper.func_76131_a(var1, -1.0F, 1.0F);
      return MathHelper.func_181758_c(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   public List<BiomeGenBase.SpawnListEntry> func_76747_a(EnumCreatureType var1) {
      switch(var1) {
      case MONSTER:
         return this.field_76761_J;
      case CREATURE:
         return this.field_76762_K;
      case WATER_CREATURE:
         return this.field_76755_L;
      case AMBIENT:
         return this.field_82914_M;
      default:
         return Collections.emptyList();
      }
   }

   public boolean func_76746_c() {
      return this.func_150559_j();
   }

   public boolean func_76738_d() {
      return this.func_150559_j() ? false : this.field_76765_S;
   }

   public boolean func_76736_e() {
      return this.field_76751_G > 0.85F;
   }

   public float func_76741_f() {
      return 0.1F;
   }

   public final int func_76744_g() {
      return (int)(this.field_76751_G * 65536.0F);
   }

   public final float func_76727_i() {
      return this.field_76751_G;
   }

   public final float func_180626_a(BlockPos var1) {
      if (var1.func_177956_o() > 64) {
         float var2 = (float)(field_150605_ac.func_151601_a((double)var1.func_177958_n() * 1.0D / 8.0D, (double)var1.func_177952_p() * 1.0D / 8.0D) * 4.0D);
         return this.field_76750_F - (var2 + (float)var1.func_177956_o() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.field_76750_F;
      }
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      this.field_76760_I.func_180292_a(var1, var2, this, var3);
   }

   public int func_180627_b(BlockPos var1) {
      double var2 = (double)MathHelper.func_76131_a(this.func_180626_a(var1), 0.0F, 1.0F);
      double var4 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return ColorizerGrass.func_77480_a(var2, var4);
   }

   public int func_180625_c(BlockPos var1) {
      double var2 = (double)MathHelper.func_76131_a(this.func_180626_a(var1), 0.0F, 1.0F);
      double var4 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return ColorizerFoliage.func_77470_a(var2, var4);
   }

   public boolean func_150559_j() {
      return this.field_76766_R;
   }

   public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      this.func_180628_b(var1, var2, var3, var4, var5, var6);
   }

   public final void func_180628_b(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      int var8 = var1.func_181545_F();
      IBlockState var9 = this.field_76752_A;
      IBlockState var10 = this.field_76753_B;
      int var11 = -1;
      int var12 = (int)(var6 / 3.0D + 3.0D + var2.nextDouble() * 0.25D);
      int var13 = var4 & 15;
      int var14 = var5 & 15;
      BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();

      for(int var16 = 255; var16 >= 0; --var16) {
         if (var16 <= var2.nextInt(5)) {
            var3.func_177855_a(var14, var16, var13, Blocks.field_150357_h.func_176223_P());
         } else {
            IBlockState var17 = var3.func_177856_a(var14, var16, var13);
            if (var17.func_177230_c().func_149688_o() == Material.field_151579_a) {
               var11 = -1;
            } else if (var17.func_177230_c() == Blocks.field_150348_b) {
               if (var11 == -1) {
                  if (var12 <= 0) {
                     var9 = null;
                     var10 = Blocks.field_150348_b.func_176223_P();
                  } else if (var16 >= var8 - 4 && var16 <= var8 + 1) {
                     var9 = this.field_76752_A;
                     var10 = this.field_76753_B;
                  }

                  if (var16 < var8 && (var9 == null || var9.func_177230_c().func_149688_o() == Material.field_151579_a)) {
                     if (this.func_180626_a(var15.func_181079_c(var4, var16, var5)) < 0.15F) {
                        var9 = Blocks.field_150432_aD.func_176223_P();
                     } else {
                        var9 = Blocks.field_150355_j.func_176223_P();
                     }
                  }

                  var11 = var12;
                  if (var16 >= var8 - 1) {
                     var3.func_177855_a(var14, var16, var13, var9);
                  } else if (var16 < var8 - 7 - var12) {
                     var9 = null;
                     var10 = Blocks.field_150348_b.func_176223_P();
                     var3.func_177855_a(var14, var16, var13, Blocks.field_150351_n.func_176223_P());
                  } else {
                     var3.func_177855_a(var14, var16, var13, var10);
                  }
               } else if (var11 > 0) {
                  --var11;
                  var3.func_177855_a(var14, var16, var13, var10);
                  if (var11 == 0 && var10.func_177230_c() == Blocks.field_150354_m) {
                     var11 = var2.nextInt(4) + Math.max(0, var16 - 63);
                     var10 = var10.func_177229_b(BlockSand.field_176504_a) == BlockSand.EnumType.RED_SAND ? Blocks.field_180395_cM.func_176223_P() : Blocks.field_150322_A.func_176223_P();
                  }
               }
            }
         }
      }

   }

   protected BiomeGenBase func_150566_k() {
      return this.func_180277_d(this.field_76756_M + 128);
   }

   protected BiomeGenBase func_180277_d(int var1) {
      return new BiomeGenMutated(var1, this);
   }

   public Class<? extends BiomeGenBase> func_150562_l() {
      return this.getClass();
   }

   public boolean func_150569_a(BiomeGenBase var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         return this.func_150562_l() == var1.func_150562_l();
      }
   }

   public BiomeGenBase.TempCategory func_150561_m() {
      if ((double)this.field_76750_F < 0.2D) {
         return BiomeGenBase.TempCategory.COLD;
      } else {
         return (double)this.field_76750_F < 1.0D ? BiomeGenBase.TempCategory.MEDIUM : BiomeGenBase.TempCategory.WARM;
      }
   }

   public static BiomeGenBase[] func_150565_n() {
      return field_76773_a;
   }

   public static BiomeGenBase func_150568_d(int var0) {
      return func_180276_a(var0, (BiomeGenBase)null);
   }

   public static BiomeGenBase func_180276_a(int var0, BiomeGenBase var1) {
      if (var0 >= 0 && var0 <= field_76773_a.length) {
         BiomeGenBase var2 = field_76773_a[var0];
         return var2 == null ? var1 : var2;
      } else {
         field_150586_aC.warn("Biome ID is out of bounds: " + var0 + ", defaulting to 0 (Ocean)");
         return field_76771_b;
      }
   }

   static {
      field_76771_b = (new BiomeGenOcean(0)).func_76739_b(112).func_76735_a("Ocean").func_150570_a(field_150595_c);
      field_76772_c = (new BiomeGenPlains(1)).func_76739_b(9286496).func_76735_a("Plains");
      field_76769_d = (new BiomeGenDesert(2)).func_76739_b(16421912).func_76735_a("Desert").func_76745_m().func_76732_a(2.0F, 0.0F).func_150570_a(field_150593_e);
      field_76770_e = (new BiomeGenHills(3, false)).func_76739_b(6316128).func_76735_a("Extreme Hills").func_150570_a(field_150603_i).func_76732_a(0.2F, 0.3F);
      field_76767_f = (new BiomeGenForest(4, 0)).func_76739_b(353825).func_76735_a("Forest");
      field_76768_g = (new BiomeGenTaiga(5, 0)).func_76739_b(747097).func_76735_a("Taiga").func_76733_a(5159473).func_76732_a(0.25F, 0.8F).func_150570_a(field_150590_f);
      field_76780_h = (new BiomeGenSwamp(6)).func_76739_b(522674).func_76735_a("Swampland").func_76733_a(9154376).func_150570_a(field_150599_m).func_76732_a(0.8F, 0.9F);
      field_76781_i = (new BiomeGenRiver(7)).func_76739_b(255).func_76735_a("River").func_150570_a(field_150594_b);
      field_76778_j = (new BiomeGenHell(8)).func_76739_b(16711680).func_76735_a("Hell").func_76745_m().func_76732_a(2.0F, 0.0F);
      field_76779_k = (new BiomeGenEnd(9)).func_76739_b(8421631).func_76735_a("The End").func_76745_m();
      field_76776_l = (new BiomeGenOcean(10)).func_76739_b(9474208).func_76735_a("FrozenOcean").func_76742_b().func_150570_a(field_150595_c).func_76732_a(0.0F, 0.5F);
      field_76777_m = (new BiomeGenRiver(11)).func_76739_b(10526975).func_76735_a("FrozenRiver").func_76742_b().func_150570_a(field_150594_b).func_76732_a(0.0F, 0.5F);
      field_76774_n = (new BiomeGenSnow(12, false)).func_76739_b(16777215).func_76735_a("Ice Plains").func_76742_b().func_76732_a(0.0F, 0.5F).func_150570_a(field_150593_e);
      field_76775_o = (new BiomeGenSnow(13, false)).func_76739_b(10526880).func_76735_a("Ice Mountains").func_76742_b().func_150570_a(field_150591_g).func_76732_a(0.0F, 0.5F);
      field_76789_p = (new BiomeGenMushroomIsland(14)).func_76739_b(16711935).func_76735_a("MushroomIsland").func_76732_a(0.9F, 1.0F).func_150570_a(field_150598_l);
      field_76788_q = (new BiomeGenMushroomIsland(15)).func_76739_b(10486015).func_76735_a("MushroomIslandShore").func_76732_a(0.9F, 1.0F).func_150570_a(field_150600_j);
      field_76787_r = (new BiomeGenBeach(16)).func_76739_b(16440917).func_76735_a("Beach").func_76732_a(0.8F, 0.4F).func_150570_a(field_150600_j);
      field_76786_s = (new BiomeGenDesert(17)).func_76739_b(13786898).func_76735_a("DesertHills").func_76745_m().func_76732_a(2.0F, 0.0F).func_150570_a(field_150591_g);
      field_76785_t = (new BiomeGenForest(18, 0)).func_76739_b(2250012).func_76735_a("ForestHills").func_150570_a(field_150591_g);
      field_76784_u = (new BiomeGenTaiga(19, 0)).func_76739_b(1456435).func_76735_a("TaigaHills").func_76733_a(5159473).func_76732_a(0.25F, 0.8F).func_150570_a(field_150591_g);
      field_76783_v = (new BiomeGenHills(20, true)).func_76739_b(7501978).func_76735_a("Extreme Hills Edge").func_150570_a(field_150603_i.func_150775_a()).func_76732_a(0.2F, 0.3F);
      field_76782_w = (new BiomeGenJungle(21, false)).func_76739_b(5470985).func_76735_a("Jungle").func_76733_a(5470985).func_76732_a(0.95F, 0.9F);
      field_76792_x = (new BiomeGenJungle(22, false)).func_76739_b(2900485).func_76735_a("JungleHills").func_76733_a(5470985).func_76732_a(0.95F, 0.9F).func_150570_a(field_150591_g);
      field_150574_L = (new BiomeGenJungle(23, true)).func_76739_b(6458135).func_76735_a("JungleEdge").func_76733_a(5470985).func_76732_a(0.95F, 0.8F);
      field_150575_M = (new BiomeGenOcean(24)).func_76739_b(48).func_76735_a("Deep Ocean").func_150570_a(field_150592_d);
      field_150576_N = (new BiomeGenStoneBeach(25)).func_76739_b(10658436).func_76735_a("Stone Beach").func_76732_a(0.2F, 0.3F).func_150570_a(field_150601_k);
      field_150577_O = (new BiomeGenBeach(26)).func_76739_b(16445632).func_76735_a("Cold Beach").func_76732_a(0.05F, 0.3F).func_150570_a(field_150600_j).func_76742_b();
      field_150583_P = (new BiomeGenForest(27, 2)).func_76735_a("Birch Forest").func_76739_b(3175492);
      field_150582_Q = (new BiomeGenForest(28, 2)).func_76735_a("Birch Forest Hills").func_76739_b(2055986).func_150570_a(field_150591_g);
      field_150585_R = (new BiomeGenForest(29, 3)).func_76739_b(4215066).func_76735_a("Roofed Forest");
      field_150584_S = (new BiomeGenTaiga(30, 0)).func_76739_b(3233098).func_76735_a("Cold Taiga").func_76733_a(5159473).func_76742_b().func_76732_a(-0.5F, 0.4F).func_150570_a(field_150590_f).func_150563_c(16777215);
      field_150579_T = (new BiomeGenTaiga(31, 0)).func_76739_b(2375478).func_76735_a("Cold Taiga Hills").func_76733_a(5159473).func_76742_b().func_76732_a(-0.5F, 0.4F).func_150570_a(field_150591_g).func_150563_c(16777215);
      field_150578_U = (new BiomeGenTaiga(32, 1)).func_76739_b(5858897).func_76735_a("Mega Taiga").func_76733_a(5159473).func_76732_a(0.3F, 0.8F).func_150570_a(field_150590_f);
      field_150581_V = (new BiomeGenTaiga(33, 1)).func_76739_b(4542270).func_76735_a("Mega Taiga Hills").func_76733_a(5159473).func_76732_a(0.3F, 0.8F).func_150570_a(field_150591_g);
      field_150580_W = (new BiomeGenHills(34, true)).func_76739_b(5271632).func_76735_a("Extreme Hills+").func_150570_a(field_150603_i).func_76732_a(0.2F, 0.3F);
      field_150588_X = (new BiomeGenSavanna(35)).func_76739_b(12431967).func_76735_a("Savanna").func_76732_a(1.2F, 0.0F).func_76745_m().func_150570_a(field_150593_e);
      field_150587_Y = (new BiomeGenSavanna(36)).func_76739_b(10984804).func_76735_a("Savanna Plateau").func_76732_a(1.0F, 0.0F).func_76745_m().func_150570_a(field_150602_h);
      field_150589_Z = (new BiomeGenMesa(37, false, false)).func_76739_b(14238997).func_76735_a("Mesa");
      field_150607_aa = (new BiomeGenMesa(38, false, true)).func_76739_b(11573093).func_76735_a("Mesa Plateau F").func_150570_a(field_150602_h);
      field_150608_ab = (new BiomeGenMesa(39, false, false)).func_76739_b(13274213).func_76735_a("Mesa Plateau").func_150570_a(field_150602_h);
      field_180279_ad = field_76771_b;
      field_76772_c.func_150566_k();
      field_76769_d.func_150566_k();
      field_76767_f.func_150566_k();
      field_76768_g.func_150566_k();
      field_76780_h.func_150566_k();
      field_76774_n.func_150566_k();
      field_76782_w.func_150566_k();
      field_150574_L.func_150566_k();
      field_150584_S.func_150566_k();
      field_150588_X.func_150566_k();
      field_150587_Y.func_150566_k();
      field_150589_Z.func_150566_k();
      field_150607_aa.func_150566_k();
      field_150608_ab.func_150566_k();
      field_150583_P.func_150566_k();
      field_150582_Q.func_150566_k();
      field_150585_R.func_150566_k();
      field_150578_U.func_150566_k();
      field_76770_e.func_150566_k();
      field_150580_W.func_150566_k();
      field_150578_U.func_180277_d(field_150581_V.field_76756_M + 128).func_76735_a("Redwood Taiga Hills M");
      BiomeGenBase[] var0 = field_76773_a;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         BiomeGenBase var3 = var0[var2];
         if (var3 != null) {
            if (field_180278_o.containsKey(var3.field_76791_y)) {
               throw new Error("Biome \"" + var3.field_76791_y + "\" is defined as both ID " + ((BiomeGenBase)field_180278_o.get(var3.field_76791_y)).field_76756_M + " and " + var3.field_76756_M);
            }

            field_180278_o.put(var3.field_76791_y, var3);
            if (var3.field_76756_M < 128) {
               field_150597_n.add(var3);
            }
         }
      }

      field_150597_n.remove(field_76778_j);
      field_150597_n.remove(field_76779_k);
      field_150597_n.remove(field_76776_l);
      field_150597_n.remove(field_76783_v);
      field_150605_ac = new NoiseGeneratorPerlin(new Random(1234L), 1);
      field_180281_af = new NoiseGeneratorPerlin(new Random(2345L), 1);
      field_180280_ag = new WorldGenDoublePlant();
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public Class<? extends EntityLiving> field_76300_b;
      public int field_76301_c;
      public int field_76299_d;

      public SpawnListEntry(Class<? extends EntityLiving> var1, int var2, int var3, int var4) {
         super(var2);
         this.field_76300_b = var1;
         this.field_76301_c = var3;
         this.field_76299_d = var4;
      }

      public String toString() {
         return this.field_76300_b.getSimpleName() + "*(" + this.field_76301_c + "-" + this.field_76299_d + "):" + this.field_76292_a;
      }
   }

   public static class Height {
      public float field_150777_a;
      public float field_150776_b;

      public Height(float var1, float var2) {
         super();
         this.field_150777_a = var1;
         this.field_150776_b = var2;
      }

      public BiomeGenBase.Height func_150775_a() {
         return new BiomeGenBase.Height(this.field_150777_a * 0.8F, this.field_150776_b * 0.6F);
      }
   }

   public static enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;

      private TempCategory() {
      }
   }
}
