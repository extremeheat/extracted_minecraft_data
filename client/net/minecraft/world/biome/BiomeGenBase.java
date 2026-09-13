package net.minecraft.world.biome;

import com.google.common.collect.Sets;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
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
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
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
   protected static final BiomeGenBase$Height field_150596_a = new BiomeGenBase$Height(0.1F, 0.2F);
   protected static final BiomeGenBase$Height field_150594_b = new BiomeGenBase$Height(-0.5F, 0.0F);
   protected static final BiomeGenBase$Height field_150595_c = new BiomeGenBase$Height(-1.0F, 0.1F);
   protected static final BiomeGenBase$Height field_150592_d = new BiomeGenBase$Height(-1.8F, 0.1F);
   protected static final BiomeGenBase$Height field_150593_e = new BiomeGenBase$Height(0.125F, 0.05F);
   protected static final BiomeGenBase$Height field_150590_f = new BiomeGenBase$Height(0.2F, 0.2F);
   protected static final BiomeGenBase$Height field_150591_g = new BiomeGenBase$Height(0.45F, 0.3F);
   protected static final BiomeGenBase$Height field_150602_h = new BiomeGenBase$Height(1.5F, 0.025F);
   protected static final BiomeGenBase$Height field_150603_i = new BiomeGenBase$Height(1.0F, 0.5F);
   protected static final BiomeGenBase$Height field_150600_j = new BiomeGenBase$Height(0.0F, 0.025F);
   protected static final BiomeGenBase$Height field_150601_k = new BiomeGenBase$Height(0.1F, 0.8F);
   protected static final BiomeGenBase$Height field_150598_l = new BiomeGenBase$Height(0.2F, 0.3F);
   protected static final BiomeGenBase$Height field_150599_m = new BiomeGenBase$Height(-0.2F, 0.1F);
   private static final BiomeGenBase[] field_76773_a = new BiomeGenBase[256];
   public static final Set field_150597_n = Sets.newHashSet();
   public static final BiomeGenBase field_76771_b = new BiomeGenOcean(0).func_76739_b(112).func_76735_a("Ocean").func_150570_a(field_150595_c);
   public static final BiomeGenBase field_76772_c = new BiomeGenPlains(1).func_76739_b(9286496).func_76735_a("Plains");
   public static final BiomeGenBase field_76769_d = new BiomeGenDesert(2)
      .func_76739_b(16421912)
      .func_76735_a("Desert")
      .func_76745_m()
      .func_76732_a(2.0F, 0.0F)
      .func_150570_a(field_150593_e);
   public static final BiomeGenBase field_76770_e = new BiomeGenHills(3, false)
      .func_76739_b(6316128)
      .func_76735_a("Extreme Hills")
      .func_150570_a(field_150603_i)
      .func_76732_a(0.2F, 0.3F);
   public static final BiomeGenBase field_76767_f = new BiomeGenForest(4, 0).func_76739_b(353825).func_76735_a("Forest");
   public static final BiomeGenBase field_76768_g = new BiomeGenTaiga(5, 0)
      .func_76739_b(747097)
      .func_76735_a("Taiga")
      .func_76733_a(5159473)
      .func_76732_a(0.25F, 0.8F)
      .func_150570_a(field_150590_f);
   public static final BiomeGenBase field_76780_h = new BiomeGenSwamp(6)
      .func_76739_b(522674)
      .func_76735_a("Swampland")
      .func_76733_a(9154376)
      .func_150570_a(field_150599_m)
      .func_76732_a(0.8F, 0.9F);
   public static final BiomeGenBase field_76781_i = new BiomeGenRiver(7).func_76739_b(255).func_76735_a("River").func_150570_a(field_150594_b);
   public static final BiomeGenBase field_76778_j = new BiomeGenHell(8).func_76739_b(16711680).func_76735_a("Hell").func_76745_m().func_76732_a(2.0F, 0.0F);
   public static final BiomeGenBase field_76779_k = new BiomeGenEnd(9).func_76739_b(8421631).func_76735_a("Sky").func_76745_m();
   public static final BiomeGenBase field_76776_l = new BiomeGenOcean(10)
      .func_76739_b(9474208)
      .func_76735_a("FrozenOcean")
      .func_76742_b()
      .func_150570_a(field_150595_c)
      .func_76732_a(0.0F, 0.5F);
   public static final BiomeGenBase field_76777_m = new BiomeGenRiver(11)
      .func_76739_b(10526975)
      .func_76735_a("FrozenRiver")
      .func_76742_b()
      .func_150570_a(field_150594_b)
      .func_76732_a(0.0F, 0.5F);
   public static final BiomeGenBase field_76774_n = new BiomeGenSnow(12, false)
      .func_76739_b(16777215)
      .func_76735_a("Ice Plains")
      .func_76742_b()
      .func_76732_a(0.0F, 0.5F)
      .func_150570_a(field_150593_e);
   public static final BiomeGenBase field_76775_o = new BiomeGenSnow(13, false)
      .func_76739_b(10526880)
      .func_76735_a("Ice Mountains")
      .func_76742_b()
      .func_150570_a(field_150591_g)
      .func_76732_a(0.0F, 0.5F);
   public static final BiomeGenBase field_76789_p = new BiomeGenMushroomIsland(14)
      .func_76739_b(16711935)
      .func_76735_a("MushroomIsland")
      .func_76732_a(0.9F, 1.0F)
      .func_150570_a(field_150598_l);
   public static final BiomeGenBase field_76788_q = new BiomeGenMushroomIsland(15)
      .func_76739_b(10486015)
      .func_76735_a("MushroomIslandShore")
      .func_76732_a(0.9F, 1.0F)
      .func_150570_a(field_150600_j);
   public static final BiomeGenBase field_76787_r = new BiomeGenBeach(16)
      .func_76739_b(16440917)
      .func_76735_a("Beach")
      .func_76732_a(0.8F, 0.4F)
      .func_150570_a(field_150600_j);
   public static final BiomeGenBase field_76786_s = new BiomeGenDesert(17)
      .func_76739_b(13786898)
      .func_76735_a("DesertHills")
      .func_76745_m()
      .func_76732_a(2.0F, 0.0F)
      .func_150570_a(field_150591_g);
   public static final BiomeGenBase field_76785_t = new BiomeGenForest(18, 0).func_76739_b(2250012).func_76735_a("ForestHills").func_150570_a(field_150591_g);
   public static final BiomeGenBase field_76784_u = new BiomeGenTaiga(19, 0)
      .func_76739_b(1456435)
      .func_76735_a("TaigaHills")
      .func_76733_a(5159473)
      .func_76732_a(0.25F, 0.8F)
      .func_150570_a(field_150591_g);
   public static final BiomeGenBase field_76783_v = new BiomeGenHills(20, true)
      .func_76739_b(7501978)
      .func_76735_a("Extreme Hills Edge")
      .func_150570_a(field_150603_i.func_150775_a())
      .func_76732_a(0.2F, 0.3F);
   public static final BiomeGenBase field_76782_w = new BiomeGenJungle(21, false)
      .func_76739_b(5470985)
      .func_76735_a("Jungle")
      .func_76733_a(5470985)
      .func_76732_a(0.95F, 0.9F);
   public static final BiomeGenBase field_76792_x = new BiomeGenJungle(22, false)
      .func_76739_b(2900485)
      .func_76735_a("JungleHills")
      .func_76733_a(5470985)
      .func_76732_a(0.95F, 0.9F)
      .func_150570_a(field_150591_g);
   public static final BiomeGenBase field_150574_L = new BiomeGenJungle(23, true)
      .func_76739_b(6458135)
      .func_76735_a("JungleEdge")
      .func_76733_a(5470985)
      .func_76732_a(0.95F, 0.8F);
   public static final BiomeGenBase field_150575_M = new BiomeGenOcean(24).func_76739_b(48).func_76735_a("Deep Ocean").func_150570_a(field_150592_d);
   public static final BiomeGenBase field_150576_N = new BiomeGenStoneBeach(25)
      .func_76739_b(10658436)
      .func_76735_a("Stone Beach")
      .func_76732_a(0.2F, 0.3F)
      .func_150570_a(field_150601_k);
   public static final BiomeGenBase field_150577_O = new BiomeGenBeach(26)
      .func_76739_b(16445632)
      .func_76735_a("Cold Beach")
      .func_76732_a(0.05F, 0.3F)
      .func_150570_a(field_150600_j)
      .func_76742_b();
   public static final BiomeGenBase field_150583_P = new BiomeGenForest(27, 2).func_76735_a("Birch Forest").func_76739_b(3175492);
   public static final BiomeGenBase field_150582_Q = new BiomeGenForest(28, 2)
      .func_76735_a("Birch Forest Hills")
      .func_76739_b(2055986)
      .func_150570_a(field_150591_g);
   public static final BiomeGenBase field_150585_R = new BiomeGenForest(29, 3).func_76739_b(4215066).func_76735_a("Roofed Forest");
   public static final BiomeGenBase field_150584_S = new BiomeGenTaiga(30, 0)
      .func_76739_b(3233098)
      .func_76735_a("Cold Taiga")
      .func_76733_a(5159473)
      .func_76742_b()
      .func_76732_a(-0.5F, 0.4F)
      .func_150570_a(field_150590_f)
      .func_150563_c(16777215);
   public static final BiomeGenBase field_150579_T = new BiomeGenTaiga(31, 0)
      .func_76739_b(2375478)
      .func_76735_a("Cold Taiga Hills")
      .func_76733_a(5159473)
      .func_76742_b()
      .func_76732_a(-0.5F, 0.4F)
      .func_150570_a(field_150591_g)
      .func_150563_c(16777215);
   public static final BiomeGenBase field_150578_U = new BiomeGenTaiga(32, 1)
      .func_76739_b(5858897)
      .func_76735_a("Mega Taiga")
      .func_76733_a(5159473)
      .func_76732_a(0.3F, 0.8F)
      .func_150570_a(field_150590_f);
   public static final BiomeGenBase field_150581_V = new BiomeGenTaiga(33, 1)
      .func_76739_b(4542270)
      .func_76735_a("Mega Taiga Hills")
      .func_76733_a(5159473)
      .func_76732_a(0.3F, 0.8F)
      .func_150570_a(field_150591_g);
   public static final BiomeGenBase field_150580_W = new BiomeGenHills(34, true)
      .func_76739_b(5271632)
      .func_76735_a("Extreme Hills+")
      .func_150570_a(field_150603_i)
      .func_76732_a(0.2F, 0.3F);
   public static final BiomeGenBase field_150588_X = new BiomeGenSavanna(35)
      .func_76739_b(12431967)
      .func_76735_a("Savanna")
      .func_76732_a(1.2F, 0.0F)
      .func_76745_m()
      .func_150570_a(field_150593_e);
   public static final BiomeGenBase field_150587_Y = new BiomeGenSavanna(36)
      .func_76739_b(10984804)
      .func_76735_a("Savanna Plateau")
      .func_76732_a(1.0F, 0.0F)
      .func_76745_m()
      .func_150570_a(field_150602_h);
   public static final BiomeGenBase field_150589_Z = new BiomeGenMesa(37, false, false).func_76739_b(14238997).func_76735_a("Mesa");
   public static final BiomeGenBase field_150607_aa = new BiomeGenMesa(38, false, true)
      .func_76739_b(11573093)
      .func_76735_a("Mesa Plateau F")
      .func_150570_a(field_150602_h);
   public static final BiomeGenBase field_150608_ab = new BiomeGenMesa(39, false, false)
      .func_76739_b(13274213)
      .func_76735_a("Mesa Plateau")
      .func_150570_a(field_150602_h);
   protected static final NoiseGeneratorPerlin field_150605_ac;
   protected static final NoiseGeneratorPerlin field_150606_ad;
   protected static final WorldGenDoublePlant field_150610_ae;
   public String field_76791_y;
   public int field_76790_z;
   public int field_150609_ah;
   public Block field_76752_A = Blocks.field_150349_c;
   public int field_150604_aj = 0;
   public Block field_76753_B = Blocks.field_150346_d;
   public int field_76754_C = 5169201;
   public float field_76748_D = field_150596_a.field_150777_a;
   public float field_76749_E = field_150596_a.field_150776_b;
   public float field_76750_F = 0.5F;
   public float field_76751_G = 0.5F;
   public int field_76759_H = 16777215;
   public BiomeDecorator field_76760_I;
   protected List field_76761_J = new ArrayList();
   protected List field_76762_K = new ArrayList();
   protected List field_76755_L = new ArrayList();
   protected List field_82914_M = new ArrayList();
   protected boolean field_76766_R;
   protected boolean field_76765_S = true;
   public final int field_76756_M;
   protected WorldGenTrees field_76757_N = new WorldGenTrees(false);
   protected WorldGenBigTree field_76758_O = new WorldGenBigTree(false);
   protected WorldGenSwamp field_76763_Q = new WorldGenSwamp();

   protected BiomeGenBase(int var1) {
      super();
      this.field_76756_M = var1;
      field_76773_a[var1] = this;
      this.field_76760_I = this.func_76729_a();
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntitySheep.class, 12, 4, 4));
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityPig.class, 10, 4, 4));
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityChicken.class, 10, 4, 4));
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityCow.class, 8, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntitySpider.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityZombie.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityCreeper.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntitySlime.class, 100, 4, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityEnderman.class, 10, 1, 4));
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityWitch.class, 5, 1, 1));
      this.field_76755_L.add(new BiomeGenBase$SpawnListEntry(EntitySquid.class, 10, 4, 4));
      this.field_82914_M.add(new BiomeGenBase$SpawnListEntry(EntityBat.class, 10, 8, 8));
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

   protected final BiomeGenBase func_150570_a(BiomeGenBase$Height var1) {
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
      return new WorldGenTallGrass(Blocks.field_150329_H, 1);
   }

   public String func_150572_a(Random var1, int var2, int var3, int var4) {
      return var1.nextInt(3) > 0 ? BlockFlower.field_149858_b[0] : BlockFlower.field_149859_a[0];
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
      if (var1 < -1.0F) {
         var1 = -1.0F;
      }

      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return Color.getHSBColor(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F).getRGB();
   }

   public List func_76747_a(EnumCreatureType var1) {
      if (var1 == EnumCreatureType.monster) {
         return this.field_76761_J;
      } else if (var1 == EnumCreatureType.creature) {
         return this.field_76762_K;
      } else if (var1 == EnumCreatureType.waterCreature) {
         return this.field_76755_L;
      } else {
         return var1 == EnumCreatureType.ambient ? this.field_82914_M : null;
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

   public final float func_150564_a(int var1, int var2, int var3) {
      if (var2 > 64) {
         float var4 = (float)field_150605_ac.func_151601_a((double)var1 * 1.0 / 8.0, (double)var3 * 1.0 / 8.0) * 4.0F;
         return this.field_76750_F - (var4 + (float)var2 - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.field_76750_F;
      }
   }

   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      this.field_76760_I.func_150512_a(var1, var2, this, var3, var4);
   }

   public int func_150558_b(int var1, int var2, int var3) {
      double var4 = (double)MathHelper.func_76131_a(this.func_150564_a(var1, var2, var3), 0.0F, 1.0F);
      double var6 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return ColorizerGrass.func_77480_a(var4, var6);
   }

   public int func_150571_c(int var1, int var2, int var3) {
      double var4 = (double)MathHelper.func_76131_a(this.func_150564_a(var1, var2, var3), 0.0F, 1.0F);
      double var6 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return ColorizerFoliage.func_77470_a(var4, var6);
   }

   public boolean func_150559_j() {
      return this.field_76766_R;
   }

   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      this.func_150560_b(var1, var2, var3, var4, var5, var6, var7);
   }

   public final void func_150560_b(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      boolean var9 = true;
      Block var10 = this.field_76752_A;
      byte var11 = (byte)(this.field_150604_aj & 0xFF);
      Block var12 = this.field_76753_B;
      int var13 = -1;
      int var14 = (int)(var7 / 3.0 + 3.0 + var2.nextDouble() * 0.25);
      int var15 = var5 & 15;
      int var16 = var6 & 15;
      int var17 = var3.length / 256;

      for(int var18 = 255; var18 >= 0; --var18) {
         int var19 = (var16 * 16 + var15) * var17 + var18;
         if (var18 <= 0 + var2.nextInt(5)) {
            var3[var19] = Blocks.field_150357_h;
         } else {
            Block var20 = var3[var19];
            if (var20 == null || var20.func_149688_o() == Material.field_151579_a) {
               var13 = -1;
            } else if (var20 == Blocks.field_150348_b) {
               if (var13 == -1) {
                  if (var14 <= 0) {
                     var10 = null;
                     var11 = 0;
                     var12 = Blocks.field_150348_b;
                  } else if (var18 >= 59 && var18 <= 64) {
                     var10 = this.field_76752_A;
                     var11 = (byte)(this.field_150604_aj & 0xFF);
                     var12 = this.field_76753_B;
                  }

                  if (var18 < 63 && (var10 == null || var10.func_149688_o() == Material.field_151579_a)) {
                     if (this.func_150564_a(var5, var18, var6) < 0.15F) {
                        var10 = Blocks.field_150432_aD;
                        var11 = 0;
                     } else {
                        var10 = Blocks.field_150355_j;
                        var11 = 0;
                     }
                  }

                  var13 = var14;
                  if (var18 >= 62) {
                     var3[var19] = var10;
                     var4[var19] = var11;
                  } else if (var18 < 56 - var14) {
                     var10 = null;
                     var12 = Blocks.field_150348_b;
                     var3[var19] = Blocks.field_150351_n;
                  } else {
                     var3[var19] = var12;
                  }
               } else if (var13 > 0) {
                  --var13;
                  var3[var19] = var12;
                  if (var13 == 0 && var12 == Blocks.field_150354_m) {
                     var13 = var2.nextInt(4) + Math.max(0, var18 - 63);
                     var12 = Blocks.field_150322_A;
                  }
               }
            }
         }
      }
   }

   protected BiomeGenBase func_150566_k() {
      return new BiomeGenMutated(this.field_76756_M + 128, this);
   }

   public Class func_150562_l() {
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

   public BiomeGenBase$TempCategory func_150561_m() {
      if ((double)this.field_76750_F < 0.2) {
         return BiomeGenBase$TempCategory.COLD;
      } else {
         return (double)this.field_76750_F < 1.0 ? BiomeGenBase$TempCategory.MEDIUM : BiomeGenBase$TempCategory.WARM;
      }
   }

   public static BiomeGenBase[] func_150565_n() {
      return field_76773_a;
   }

   public static BiomeGenBase func_150568_d(int var0) {
      if (var0 >= 0 && var0 <= field_76773_a.length) {
         return field_76773_a[var0];
      } else {
         field_150586_aC.warn("Biome ID is out of bounds: " + var0 + ", defaulting to 0 (Ocean)");
         return field_76771_b;
      }
   }

   static {
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
      field_76773_a[field_150581_V.field_76756_M + 128] = field_76773_a[field_150578_U.field_76756_M + 128];

      for(BiomeGenBase var3 : field_76773_a) {
         if (var3 != null && var3.field_76756_M < 128) {
            field_150597_n.add(var3);
         }
      }

      field_150597_n.remove(field_76778_j);
      field_150597_n.remove(field_76779_k);
      field_150597_n.remove(field_76776_l);
      field_150597_n.remove(field_76783_v);
      field_150605_ac = new NoiseGeneratorPerlin(new Random(1234L), 1);
      field_150606_ad = new NoiseGeneratorPerlin(new Random(2345L), 1);
      field_150610_ae = new WorldGenDoublePlant();
   }
}
