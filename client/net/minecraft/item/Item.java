package net.minecraft.item;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
   public static final RegistryNamespaced<ResourceLocation, Item> field_150901_e = new RegistryNamespaced();
   private static final Map<Block, Item> field_179220_a = Maps.newHashMap();
   protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   private CreativeTabs field_77701_a;
   protected static Random field_77697_d = new Random();
   protected int field_77777_bU = 64;
   private int field_77699_b;
   protected boolean field_77789_bW;
   protected boolean field_77787_bX;
   private Item field_77700_c;
   private String field_77785_bY;
   private String field_77774_bZ;

   public Item() {
      super();
   }

   public static int func_150891_b(Item var0) {
      return var0 == null ? 0 : field_150901_e.func_148757_b(var0);
   }

   public static Item func_150899_d(int var0) {
      return (Item)field_150901_e.func_148754_a(var0);
   }

   public static Item func_150898_a(Block var0) {
      return (Item)field_179220_a.get(var0);
   }

   public static Item func_111206_d(String var0) {
      Item var1 = (Item)field_150901_e.func_82594_a(new ResourceLocation(var0));
      if (var1 == null) {
         try {
            return func_150899_d(Integer.parseInt(var0));
         } catch (NumberFormatException var3) {
         }
      }

      return var1;
   }

   public boolean func_179215_a(NBTTagCompound var1) {
      return false;
   }

   public Item func_77625_d(int var1) {
      this.field_77777_bU = var1;
      return this;
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      return false;
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      return 1.0F;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public int func_77639_j() {
      return this.field_77777_bU;
   }

   public int func_77647_b(int var1) {
      return 0;
   }

   public boolean func_77614_k() {
      return this.field_77787_bX;
   }

   protected Item func_77627_a(boolean var1) {
      this.field_77787_bX = var1;
      return this;
   }

   public int func_77612_l() {
      return this.field_77699_b;
   }

   protected Item func_77656_e(int var1) {
      this.field_77699_b = var1;
      return this;
   }

   public boolean func_77645_m() {
      return this.field_77699_b > 0 && !this.field_77787_bX;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      return false;
   }

   public boolean func_179218_a(ItemStack var1, World var2, Block var3, BlockPos var4, EntityLivingBase var5) {
      return false;
   }

   public boolean func_150897_b(Block var1) {
      return false;
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      return false;
   }

   public Item func_77664_n() {
      this.field_77789_bW = true;
      return this;
   }

   public boolean func_77662_d() {
      return this.field_77789_bW;
   }

   public boolean func_77629_n_() {
      return false;
   }

   public Item func_77655_b(String var1) {
      this.field_77774_bZ = var1;
      return this;
   }

   public String func_77657_g(ItemStack var1) {
      String var2 = this.func_77667_c(var1);
      return var2 == null ? "" : StatCollector.func_74838_a(var2);
   }

   public String func_77658_a() {
      return "item." + this.field_77774_bZ;
   }

   public String func_77667_c(ItemStack var1) {
      return "item." + this.field_77774_bZ;
   }

   public Item func_77642_a(Item var1) {
      this.field_77700_c = var1;
      return this;
   }

   public boolean func_77651_p() {
      return true;
   }

   public Item func_77668_q() {
      return this.field_77700_c;
   }

   public boolean func_77634_r() {
      return this.field_77700_c != null;
   }

   public int func_82790_a(ItemStack var1, int var2) {
      return 16777215;
   }

   public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
   }

   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
   }

   public boolean func_77643_m_() {
      return false;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.NONE;
   }

   public int func_77626_a(ItemStack var1) {
      return 0;
   }

   public void func_77615_a(ItemStack var1, World var2, EntityPlayer var3, int var4) {
   }

   protected Item func_77631_c(String var1) {
      this.field_77785_bY = var1;
      return this;
   }

   public String func_150896_i(ItemStack var1) {
      return this.field_77785_bY;
   }

   public boolean func_150892_m(ItemStack var1) {
      return this.func_150896_i(var1) != null;
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
   }

   public String func_77653_i(ItemStack var1) {
      return ("" + StatCollector.func_74838_a(this.func_77657_g(var1) + ".name")).trim();
   }

   public boolean func_77636_d(ItemStack var1) {
      return var1.func_77948_v();
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      return var1.func_77948_v() ? EnumRarity.RARE : EnumRarity.COMMON;
   }

   public boolean func_77616_k(ItemStack var1) {
      return this.func_77639_j() == 1 && this.func_77645_m();
   }

   protected MovingObjectPosition func_77621_a(World var1, EntityPlayer var2, boolean var3) {
      float var4 = var2.field_70125_A;
      float var5 = var2.field_70177_z;
      double var6 = var2.field_70165_t;
      double var8 = var2.field_70163_u + (double)var2.func_70047_e();
      double var10 = var2.field_70161_v;
      Vec3 var12 = new Vec3(var6, var8, var10);
      float var13 = MathHelper.func_76134_b(-var5 * 0.017453292F - 3.1415927F);
      float var14 = MathHelper.func_76126_a(-var5 * 0.017453292F - 3.1415927F);
      float var15 = -MathHelper.func_76134_b(-var4 * 0.017453292F);
      float var16 = MathHelper.func_76126_a(-var4 * 0.017453292F);
      float var17 = var14 * var15;
      float var19 = var13 * var15;
      double var20 = 5.0D;
      Vec3 var22 = var12.func_72441_c((double)var17 * var20, (double)var16 * var20, (double)var19 * var20);
      return var1.func_147447_a(var12, var22, var3, !var3, false);
   }

   public int func_77619_b() {
      return 0;
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs func_77640_w() {
      return this.field_77701_a;
   }

   public Item func_77637_a(CreativeTabs var1) {
      this.field_77701_a = var1;
      return this;
   }

   public boolean func_82788_x() {
      return false;
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return false;
   }

   public Multimap<String, AttributeModifier> func_111205_h() {
      return HashMultimap.create();
   }

   public static void func_150900_l() {
      func_179214_a(Blocks.field_150348_b, (new ItemMultiTexture(Blocks.field_150348_b, Blocks.field_150348_b, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockStone.EnumType.func_176643_a(var1.func_77960_j()).func_176644_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("stone"));
      func_179214_a(Blocks.field_150349_c, new ItemColored(Blocks.field_150349_c, false));
      func_179214_a(Blocks.field_150346_d, (new ItemMultiTexture(Blocks.field_150346_d, Blocks.field_150346_d, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockDirt.DirtType.func_176924_a(var1.func_77960_j()).func_176927_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("dirt"));
      func_179216_c(Blocks.field_150347_e);
      func_179214_a(Blocks.field_150344_f, (new ItemMultiTexture(Blocks.field_150344_f, Blocks.field_150344_f, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockPlanks.EnumType.func_176837_a(var1.func_77960_j()).func_176840_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("wood"));
      func_179214_a(Blocks.field_150345_g, (new ItemMultiTexture(Blocks.field_150345_g, Blocks.field_150345_g, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockPlanks.EnumType.func_176837_a(var1.func_77960_j()).func_176840_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("sapling"));
      func_179216_c(Blocks.field_150357_h);
      func_179214_a(Blocks.field_150354_m, (new ItemMultiTexture(Blocks.field_150354_m, Blocks.field_150354_m, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockSand.EnumType.func_176686_a(var1.func_77960_j()).func_176685_d();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("sand"));
      func_179216_c(Blocks.field_150351_n);
      func_179216_c(Blocks.field_150352_o);
      func_179216_c(Blocks.field_150366_p);
      func_179216_c(Blocks.field_150365_q);
      func_179214_a(Blocks.field_150364_r, (new ItemMultiTexture(Blocks.field_150364_r, Blocks.field_150364_r, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockPlanks.EnumType.func_176837_a(var1.func_77960_j()).func_176840_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("log"));
      func_179214_a(Blocks.field_150363_s, (new ItemMultiTexture(Blocks.field_150363_s, Blocks.field_150363_s, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockPlanks.EnumType.func_176837_a(var1.func_77960_j() + 4).func_176840_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("log"));
      func_179214_a(Blocks.field_150362_t, (new ItemLeaves(Blocks.field_150362_t)).func_77655_b("leaves"));
      func_179214_a(Blocks.field_150361_u, (new ItemLeaves(Blocks.field_150361_u)).func_77655_b("leaves"));
      func_179214_a(Blocks.field_150360_v, (new ItemMultiTexture(Blocks.field_150360_v, Blocks.field_150360_v, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return (var1.func_77960_j() & 1) == 1 ? "wet" : "dry";
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("sponge"));
      func_179216_c(Blocks.field_150359_w);
      func_179216_c(Blocks.field_150369_x);
      func_179216_c(Blocks.field_150368_y);
      func_179216_c(Blocks.field_150367_z);
      func_179214_a(Blocks.field_150322_A, (new ItemMultiTexture(Blocks.field_150322_A, Blocks.field_150322_A, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockSandStone.EnumType.func_176673_a(var1.func_77960_j()).func_176676_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("sandStone"));
      func_179216_c(Blocks.field_150323_B);
      func_179216_c(Blocks.field_150318_D);
      func_179216_c(Blocks.field_150319_E);
      func_179214_a(Blocks.field_150320_F, new ItemPiston(Blocks.field_150320_F));
      func_179216_c(Blocks.field_150321_G);
      func_179214_a(Blocks.field_150329_H, (new ItemColored(Blocks.field_150329_H, true)).func_150943_a(new String[]{"shrub", "grass", "fern"}));
      func_179216_c(Blocks.field_150330_I);
      func_179214_a(Blocks.field_150331_J, new ItemPiston(Blocks.field_150331_J));
      func_179214_a(Blocks.field_150325_L, (new ItemCloth(Blocks.field_150325_L)).func_77655_b("cloth"));
      func_179214_a(Blocks.field_150327_N, (new ItemMultiTexture(Blocks.field_150327_N, Blocks.field_150327_N, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.YELLOW, var1.func_77960_j()).func_176963_d();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("flower"));
      func_179214_a(Blocks.field_150328_O, (new ItemMultiTexture(Blocks.field_150328_O, Blocks.field_150328_O, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.RED, var1.func_77960_j()).func_176963_d();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("rose"));
      func_179216_c(Blocks.field_150338_P);
      func_179216_c(Blocks.field_150337_Q);
      func_179216_c(Blocks.field_150340_R);
      func_179216_c(Blocks.field_150339_S);
      func_179214_a(Blocks.field_150333_U, (new ItemSlab(Blocks.field_150333_U, Blocks.field_150333_U, Blocks.field_150334_T)).func_77655_b("stoneSlab"));
      func_179216_c(Blocks.field_150336_V);
      func_179216_c(Blocks.field_150335_W);
      func_179216_c(Blocks.field_150342_X);
      func_179216_c(Blocks.field_150341_Y);
      func_179216_c(Blocks.field_150343_Z);
      func_179216_c(Blocks.field_150478_aa);
      func_179216_c(Blocks.field_150474_ac);
      func_179216_c(Blocks.field_150476_ad);
      func_179216_c(Blocks.field_150486_ae);
      func_179216_c(Blocks.field_150482_ag);
      func_179216_c(Blocks.field_150484_ah);
      func_179216_c(Blocks.field_150462_ai);
      func_179216_c(Blocks.field_150458_ak);
      func_179216_c(Blocks.field_150460_al);
      func_179216_c(Blocks.field_150470_am);
      func_179216_c(Blocks.field_150468_ap);
      func_179216_c(Blocks.field_150448_aq);
      func_179216_c(Blocks.field_150446_ar);
      func_179216_c(Blocks.field_150442_at);
      func_179216_c(Blocks.field_150456_au);
      func_179216_c(Blocks.field_150452_aw);
      func_179216_c(Blocks.field_150450_ax);
      func_179216_c(Blocks.field_150429_aA);
      func_179216_c(Blocks.field_150430_aB);
      func_179214_a(Blocks.field_150431_aC, new ItemSnow(Blocks.field_150431_aC));
      func_179216_c(Blocks.field_150432_aD);
      func_179216_c(Blocks.field_150433_aE);
      func_179216_c(Blocks.field_150434_aF);
      func_179216_c(Blocks.field_150435_aG);
      func_179216_c(Blocks.field_150421_aI);
      func_179216_c(Blocks.field_180407_aO);
      func_179216_c(Blocks.field_180408_aP);
      func_179216_c(Blocks.field_180404_aQ);
      func_179216_c(Blocks.field_180403_aR);
      func_179216_c(Blocks.field_180406_aS);
      func_179216_c(Blocks.field_180405_aT);
      func_179216_c(Blocks.field_150423_aK);
      func_179216_c(Blocks.field_150424_aL);
      func_179216_c(Blocks.field_150425_aM);
      func_179216_c(Blocks.field_150426_aN);
      func_179216_c(Blocks.field_150428_aP);
      func_179216_c(Blocks.field_150415_aT);
      func_179214_a(Blocks.field_150418_aU, (new ItemMultiTexture(Blocks.field_150418_aU, Blocks.field_150418_aU, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockSilverfish.EnumType.func_176879_a(var1.func_77960_j()).func_176882_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("monsterStoneEgg"));
      func_179214_a(Blocks.field_150417_aV, (new ItemMultiTexture(Blocks.field_150417_aV, Blocks.field_150417_aV, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockStoneBrick.EnumType.func_176613_a(var1.func_77960_j()).func_176614_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("stonebricksmooth"));
      func_179216_c(Blocks.field_150420_aW);
      func_179216_c(Blocks.field_150419_aX);
      func_179216_c(Blocks.field_150411_aY);
      func_179216_c(Blocks.field_150410_aZ);
      func_179216_c(Blocks.field_150440_ba);
      func_179214_a(Blocks.field_150395_bd, new ItemColored(Blocks.field_150395_bd, false));
      func_179216_c(Blocks.field_180390_bo);
      func_179216_c(Blocks.field_180391_bp);
      func_179216_c(Blocks.field_180392_bq);
      func_179216_c(Blocks.field_180386_br);
      func_179216_c(Blocks.field_180385_bs);
      func_179216_c(Blocks.field_180387_bt);
      func_179216_c(Blocks.field_150389_bf);
      func_179216_c(Blocks.field_150390_bg);
      func_179216_c(Blocks.field_150391_bh);
      func_179214_a(Blocks.field_150392_bi, new ItemLilyPad(Blocks.field_150392_bi));
      func_179216_c(Blocks.field_150385_bj);
      func_179216_c(Blocks.field_150386_bk);
      func_179216_c(Blocks.field_150387_bl);
      func_179216_c(Blocks.field_150381_bn);
      func_179216_c(Blocks.field_150378_br);
      func_179216_c(Blocks.field_150377_bs);
      func_179216_c(Blocks.field_150380_bt);
      func_179216_c(Blocks.field_150379_bu);
      func_179214_a(Blocks.field_150376_bx, (new ItemSlab(Blocks.field_150376_bx, Blocks.field_150376_bx, Blocks.field_150373_bw)).func_77655_b("woodSlab"));
      func_179216_c(Blocks.field_150372_bz);
      func_179216_c(Blocks.field_150412_bA);
      func_179216_c(Blocks.field_150477_bB);
      func_179216_c(Blocks.field_150479_bC);
      func_179216_c(Blocks.field_150475_bE);
      func_179216_c(Blocks.field_150485_bF);
      func_179216_c(Blocks.field_150487_bG);
      func_179216_c(Blocks.field_150481_bH);
      func_179216_c(Blocks.field_150483_bI);
      func_179216_c(Blocks.field_150461_bJ);
      func_179214_a(Blocks.field_150463_bK, (new ItemMultiTexture(Blocks.field_150463_bK, Blocks.field_150463_bK, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockWall.EnumType.func_176660_a(var1.func_77960_j()).func_176659_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("cobbleWall"));
      func_179216_c(Blocks.field_150471_bO);
      func_179214_a(Blocks.field_150467_bQ, (new ItemAnvilBlock(Blocks.field_150467_bQ)).func_77655_b("anvil"));
      func_179216_c(Blocks.field_150447_bR);
      func_179216_c(Blocks.field_150445_bS);
      func_179216_c(Blocks.field_150443_bT);
      func_179216_c(Blocks.field_150453_bW);
      func_179216_c(Blocks.field_150451_bX);
      func_179216_c(Blocks.field_150449_bY);
      func_179216_c(Blocks.field_150438_bZ);
      func_179214_a(Blocks.field_150371_ca, (new ItemMultiTexture(Blocks.field_150371_ca, Blocks.field_150371_ca, new String[]{"default", "chiseled", "lines"})).func_77655_b("quartzBlock"));
      func_179216_c(Blocks.field_150370_cb);
      func_179216_c(Blocks.field_150408_cc);
      func_179216_c(Blocks.field_150409_cd);
      func_179214_a(Blocks.field_150406_ce, (new ItemCloth(Blocks.field_150406_ce)).func_77655_b("clayHardenedStained"));
      func_179216_c(Blocks.field_180401_cv);
      func_179216_c(Blocks.field_180400_cw);
      func_179216_c(Blocks.field_150407_cf);
      func_179214_a(Blocks.field_150404_cg, (new ItemCloth(Blocks.field_150404_cg)).func_77655_b("woolCarpet"));
      func_179216_c(Blocks.field_150405_ch);
      func_179216_c(Blocks.field_150402_ci);
      func_179216_c(Blocks.field_150403_cj);
      func_179216_c(Blocks.field_150400_ck);
      func_179216_c(Blocks.field_150401_cl);
      func_179216_c(Blocks.field_180399_cE);
      func_179214_a(Blocks.field_150398_cm, (new ItemDoublePlant(Blocks.field_150398_cm, Blocks.field_150398_cm, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockDoublePlant.EnumPlantType.func_176938_a(var1.func_77960_j()).func_176939_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("doublePlant"));
      func_179214_a(Blocks.field_150399_cn, (new ItemCloth(Blocks.field_150399_cn)).func_77655_b("stainedGlass"));
      func_179214_a(Blocks.field_150397_co, (new ItemCloth(Blocks.field_150397_co)).func_77655_b("stainedGlassPane"));
      func_179214_a(Blocks.field_180397_cI, (new ItemMultiTexture(Blocks.field_180397_cI, Blocks.field_180397_cI, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockPrismarine.EnumType.func_176810_a(var1.func_77960_j()).func_176809_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("prismarine"));
      func_179216_c(Blocks.field_180398_cJ);
      func_179214_a(Blocks.field_180395_cM, (new ItemMultiTexture(Blocks.field_180395_cM, Blocks.field_180395_cM, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            return BlockRedSandstone.EnumType.func_176825_a(var1.func_77960_j()).func_176828_c();
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      })).func_77655_b("redSandStone"));
      func_179216_c(Blocks.field_180396_cN);
      func_179214_a(Blocks.field_180389_cP, (new ItemSlab(Blocks.field_180389_cP, Blocks.field_180389_cP, Blocks.field_180388_cO)).func_77655_b("stoneSlab2"));
      func_179217_a(256, "iron_shovel", (new ItemSpade(Item.ToolMaterial.IRON)).func_77655_b("shovelIron"));
      func_179217_a(257, "iron_pickaxe", (new ItemPickaxe(Item.ToolMaterial.IRON)).func_77655_b("pickaxeIron"));
      func_179217_a(258, "iron_axe", (new ItemAxe(Item.ToolMaterial.IRON)).func_77655_b("hatchetIron"));
      func_179217_a(259, "flint_and_steel", (new ItemFlintAndSteel()).func_77655_b("flintAndSteel"));
      func_179217_a(260, "apple", (new ItemFood(4, 0.3F, false)).func_77655_b("apple"));
      func_179217_a(261, "bow", (new ItemBow()).func_77655_b("bow"));
      func_179217_a(262, "arrow", (new Item()).func_77655_b("arrow").func_77637_a(CreativeTabs.field_78037_j));
      func_179217_a(263, "coal", (new ItemCoal()).func_77655_b("coal"));
      func_179217_a(264, "diamond", (new Item()).func_77655_b("diamond").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(265, "iron_ingot", (new Item()).func_77655_b("ingotIron").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(266, "gold_ingot", (new Item()).func_77655_b("ingotGold").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(267, "iron_sword", (new ItemSword(Item.ToolMaterial.IRON)).func_77655_b("swordIron"));
      func_179217_a(268, "wooden_sword", (new ItemSword(Item.ToolMaterial.WOOD)).func_77655_b("swordWood"));
      func_179217_a(269, "wooden_shovel", (new ItemSpade(Item.ToolMaterial.WOOD)).func_77655_b("shovelWood"));
      func_179217_a(270, "wooden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.WOOD)).func_77655_b("pickaxeWood"));
      func_179217_a(271, "wooden_axe", (new ItemAxe(Item.ToolMaterial.WOOD)).func_77655_b("hatchetWood"));
      func_179217_a(272, "stone_sword", (new ItemSword(Item.ToolMaterial.STONE)).func_77655_b("swordStone"));
      func_179217_a(273, "stone_shovel", (new ItemSpade(Item.ToolMaterial.STONE)).func_77655_b("shovelStone"));
      func_179217_a(274, "stone_pickaxe", (new ItemPickaxe(Item.ToolMaterial.STONE)).func_77655_b("pickaxeStone"));
      func_179217_a(275, "stone_axe", (new ItemAxe(Item.ToolMaterial.STONE)).func_77655_b("hatchetStone"));
      func_179217_a(276, "diamond_sword", (new ItemSword(Item.ToolMaterial.EMERALD)).func_77655_b("swordDiamond"));
      func_179217_a(277, "diamond_shovel", (new ItemSpade(Item.ToolMaterial.EMERALD)).func_77655_b("shovelDiamond"));
      func_179217_a(278, "diamond_pickaxe", (new ItemPickaxe(Item.ToolMaterial.EMERALD)).func_77655_b("pickaxeDiamond"));
      func_179217_a(279, "diamond_axe", (new ItemAxe(Item.ToolMaterial.EMERALD)).func_77655_b("hatchetDiamond"));
      func_179217_a(280, "stick", (new Item()).func_77664_n().func_77655_b("stick").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(281, "bowl", (new Item()).func_77655_b("bowl").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(282, "mushroom_stew", (new ItemSoup(6)).func_77655_b("mushroomStew"));
      func_179217_a(283, "golden_sword", (new ItemSword(Item.ToolMaterial.GOLD)).func_77655_b("swordGold"));
      func_179217_a(284, "golden_shovel", (new ItemSpade(Item.ToolMaterial.GOLD)).func_77655_b("shovelGold"));
      func_179217_a(285, "golden_pickaxe", (new ItemPickaxe(Item.ToolMaterial.GOLD)).func_77655_b("pickaxeGold"));
      func_179217_a(286, "golden_axe", (new ItemAxe(Item.ToolMaterial.GOLD)).func_77655_b("hatchetGold"));
      func_179217_a(287, "string", (new ItemReed(Blocks.field_150473_bD)).func_77655_b("string").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(288, "feather", (new Item()).func_77655_b("feather").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(289, "gunpowder", (new Item()).func_77655_b("sulphur").func_77631_c(PotionHelper.field_77930_k).func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(290, "wooden_hoe", (new ItemHoe(Item.ToolMaterial.WOOD)).func_77655_b("hoeWood"));
      func_179217_a(291, "stone_hoe", (new ItemHoe(Item.ToolMaterial.STONE)).func_77655_b("hoeStone"));
      func_179217_a(292, "iron_hoe", (new ItemHoe(Item.ToolMaterial.IRON)).func_77655_b("hoeIron"));
      func_179217_a(293, "diamond_hoe", (new ItemHoe(Item.ToolMaterial.EMERALD)).func_77655_b("hoeDiamond"));
      func_179217_a(294, "golden_hoe", (new ItemHoe(Item.ToolMaterial.GOLD)).func_77655_b("hoeGold"));
      func_179217_a(295, "wheat_seeds", (new ItemSeeds(Blocks.field_150464_aj, Blocks.field_150458_ak)).func_77655_b("seeds"));
      func_179217_a(296, "wheat", (new Item()).func_77655_b("wheat").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(297, "bread", (new ItemFood(5, 0.6F, false)).func_77655_b("bread"));
      func_179217_a(298, "leather_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 0)).func_77655_b("helmetCloth"));
      func_179217_a(299, "leather_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 1)).func_77655_b("chestplateCloth"));
      func_179217_a(300, "leather_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 2)).func_77655_b("leggingsCloth"));
      func_179217_a(301, "leather_boots", (new ItemArmor(ItemArmor.ArmorMaterial.LEATHER, 0, 3)).func_77655_b("bootsCloth"));
      func_179217_a(302, "chainmail_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 0)).func_77655_b("helmetChain"));
      func_179217_a(303, "chainmail_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 1)).func_77655_b("chestplateChain"));
      func_179217_a(304, "chainmail_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 2)).func_77655_b("leggingsChain"));
      func_179217_a(305, "chainmail_boots", (new ItemArmor(ItemArmor.ArmorMaterial.CHAIN, 1, 3)).func_77655_b("bootsChain"));
      func_179217_a(306, "iron_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 0)).func_77655_b("helmetIron"));
      func_179217_a(307, "iron_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 1)).func_77655_b("chestplateIron"));
      func_179217_a(308, "iron_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 2)).func_77655_b("leggingsIron"));
      func_179217_a(309, "iron_boots", (new ItemArmor(ItemArmor.ArmorMaterial.IRON, 2, 3)).func_77655_b("bootsIron"));
      func_179217_a(310, "diamond_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 0)).func_77655_b("helmetDiamond"));
      func_179217_a(311, "diamond_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 1)).func_77655_b("chestplateDiamond"));
      func_179217_a(312, "diamond_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 2)).func_77655_b("leggingsDiamond"));
      func_179217_a(313, "diamond_boots", (new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 3, 3)).func_77655_b("bootsDiamond"));
      func_179217_a(314, "golden_helmet", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 0)).func_77655_b("helmetGold"));
      func_179217_a(315, "golden_chestplate", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 1)).func_77655_b("chestplateGold"));
      func_179217_a(316, "golden_leggings", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 2)).func_77655_b("leggingsGold"));
      func_179217_a(317, "golden_boots", (new ItemArmor(ItemArmor.ArmorMaterial.GOLD, 4, 3)).func_77655_b("bootsGold"));
      func_179217_a(318, "flint", (new Item()).func_77655_b("flint").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(319, "porkchop", (new ItemFood(3, 0.3F, true)).func_77655_b("porkchopRaw"));
      func_179217_a(320, "cooked_porkchop", (new ItemFood(8, 0.8F, true)).func_77655_b("porkchopCooked"));
      func_179217_a(321, "painting", (new ItemHangingEntity(EntityPainting.class)).func_77655_b("painting"));
      func_179217_a(322, "golden_apple", (new ItemAppleGold(4, 1.2F, false)).func_77848_i().func_77844_a(Potion.field_76428_l.field_76415_H, 5, 1, 1.0F).func_77655_b("appleGold"));
      func_179217_a(323, "sign", (new ItemSign()).func_77655_b("sign"));
      func_179217_a(324, "wooden_door", (new ItemDoor(Blocks.field_180413_ao)).func_77655_b("doorOak"));
      Item var0 = (new ItemBucket(Blocks.field_150350_a)).func_77655_b("bucket").func_77625_d(16);
      func_179217_a(325, "bucket", var0);
      func_179217_a(326, "water_bucket", (new ItemBucket(Blocks.field_150358_i)).func_77655_b("bucketWater").func_77642_a(var0));
      func_179217_a(327, "lava_bucket", (new ItemBucket(Blocks.field_150356_k)).func_77655_b("bucketLava").func_77642_a(var0));
      func_179217_a(328, "minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.RIDEABLE)).func_77655_b("minecart"));
      func_179217_a(329, "saddle", (new ItemSaddle()).func_77655_b("saddle"));
      func_179217_a(330, "iron_door", (new ItemDoor(Blocks.field_150454_av)).func_77655_b("doorIron"));
      func_179217_a(331, "redstone", (new ItemRedstone()).func_77655_b("redstone").func_77631_c(PotionHelper.field_77932_i));
      func_179217_a(332, "snowball", (new ItemSnowball()).func_77655_b("snowball"));
      func_179217_a(333, "boat", (new ItemBoat()).func_77655_b("boat"));
      func_179217_a(334, "leather", (new Item()).func_77655_b("leather").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(335, "milk_bucket", (new ItemBucketMilk()).func_77655_b("milk").func_77642_a(var0));
      func_179217_a(336, "brick", (new Item()).func_77655_b("brick").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(337, "clay_ball", (new Item()).func_77655_b("clay").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(338, "reeds", (new ItemReed(Blocks.field_150436_aH)).func_77655_b("reeds").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(339, "paper", (new Item()).func_77655_b("paper").func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(340, "book", (new ItemBook()).func_77655_b("book").func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(341, "slime_ball", (new Item()).func_77655_b("slimeball").func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(342, "chest_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.CHEST)).func_77655_b("minecartChest"));
      func_179217_a(343, "furnace_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.FURNACE)).func_77655_b("minecartFurnace"));
      func_179217_a(344, "egg", (new ItemEgg()).func_77655_b("egg"));
      func_179217_a(345, "compass", (new Item()).func_77655_b("compass").func_77637_a(CreativeTabs.field_78040_i));
      func_179217_a(346, "fishing_rod", (new ItemFishingRod()).func_77655_b("fishingRod"));
      func_179217_a(347, "clock", (new Item()).func_77655_b("clock").func_77637_a(CreativeTabs.field_78040_i));
      func_179217_a(348, "glowstone_dust", (new Item()).func_77655_b("yellowDust").func_77631_c(PotionHelper.field_77929_j).func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(349, "fish", (new ItemFishFood(false)).func_77655_b("fish").func_77627_a(true));
      func_179217_a(350, "cooked_fish", (new ItemFishFood(true)).func_77655_b("fish").func_77627_a(true));
      func_179217_a(351, "dye", (new ItemDye()).func_77655_b("dyePowder"));
      func_179217_a(352, "bone", (new Item()).func_77655_b("bone").func_77664_n().func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(353, "sugar", (new Item()).func_77655_b("sugar").func_77631_c(PotionHelper.field_77922_b).func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(354, "cake", (new ItemReed(Blocks.field_150414_aQ)).func_77625_d(1).func_77655_b("cake").func_77637_a(CreativeTabs.field_78039_h));
      func_179217_a(355, "bed", (new ItemBed()).func_77625_d(1).func_77655_b("bed"));
      func_179217_a(356, "repeater", (new ItemReed(Blocks.field_150413_aR)).func_77655_b("diode").func_77637_a(CreativeTabs.field_78028_d));
      func_179217_a(357, "cookie", (new ItemFood(2, 0.1F, false)).func_77655_b("cookie"));
      func_179217_a(358, "filled_map", (new ItemMap()).func_77655_b("map"));
      func_179217_a(359, "shears", (new ItemShears()).func_77655_b("shears"));
      func_179217_a(360, "melon", (new ItemFood(2, 0.3F, false)).func_77655_b("melon"));
      func_179217_a(361, "pumpkin_seeds", (new ItemSeeds(Blocks.field_150393_bb, Blocks.field_150458_ak)).func_77655_b("seeds_pumpkin"));
      func_179217_a(362, "melon_seeds", (new ItemSeeds(Blocks.field_150394_bc, Blocks.field_150458_ak)).func_77655_b("seeds_melon"));
      func_179217_a(363, "beef", (new ItemFood(3, 0.3F, true)).func_77655_b("beefRaw"));
      func_179217_a(364, "cooked_beef", (new ItemFood(8, 0.8F, true)).func_77655_b("beefCooked"));
      func_179217_a(365, "chicken", (new ItemFood(2, 0.3F, true)).func_77844_a(Potion.field_76438_s.field_76415_H, 30, 0, 0.3F).func_77655_b("chickenRaw"));
      func_179217_a(366, "cooked_chicken", (new ItemFood(6, 0.6F, true)).func_77655_b("chickenCooked"));
      func_179217_a(367, "rotten_flesh", (new ItemFood(4, 0.1F, true)).func_77844_a(Potion.field_76438_s.field_76415_H, 30, 0, 0.8F).func_77655_b("rottenFlesh"));
      func_179217_a(368, "ender_pearl", (new ItemEnderPearl()).func_77655_b("enderPearl"));
      func_179217_a(369, "blaze_rod", (new Item()).func_77655_b("blazeRod").func_77637_a(CreativeTabs.field_78035_l).func_77664_n());
      func_179217_a(370, "ghast_tear", (new Item()).func_77655_b("ghastTear").func_77631_c(PotionHelper.field_77923_c).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(371, "gold_nugget", (new Item()).func_77655_b("goldNugget").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(372, "nether_wart", (new ItemSeeds(Blocks.field_150388_bm, Blocks.field_150425_aM)).func_77655_b("netherStalkSeeds").func_77631_c("+4"));
      func_179217_a(373, "potion", (new ItemPotion()).func_77655_b("potion"));
      func_179217_a(374, "glass_bottle", (new ItemGlassBottle()).func_77655_b("glassBottle"));
      func_179217_a(375, "spider_eye", (new ItemFood(2, 0.8F, false)).func_77844_a(Potion.field_76436_u.field_76415_H, 5, 0, 1.0F).func_77655_b("spiderEye").func_77631_c(PotionHelper.field_77920_d));
      func_179217_a(376, "fermented_spider_eye", (new Item()).func_77655_b("fermentedSpiderEye").func_77631_c(PotionHelper.field_77921_e).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(377, "blaze_powder", (new Item()).func_77655_b("blazePowder").func_77631_c(PotionHelper.field_77919_g).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(378, "magma_cream", (new Item()).func_77655_b("magmaCream").func_77631_c(PotionHelper.field_77931_h).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(379, "brewing_stand", (new ItemReed(Blocks.field_150382_bo)).func_77655_b("brewingStand").func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(380, "cauldron", (new ItemReed(Blocks.field_150383_bp)).func_77655_b("cauldron").func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(381, "ender_eye", (new ItemEnderEye()).func_77655_b("eyeOfEnder"));
      func_179217_a(382, "speckled_melon", (new Item()).func_77655_b("speckledMelon").func_77631_c(PotionHelper.field_77918_f).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(383, "spawn_egg", (new ItemMonsterPlacer()).func_77655_b("monsterPlacer"));
      func_179217_a(384, "experience_bottle", (new ItemExpBottle()).func_77655_b("expBottle"));
      func_179217_a(385, "fire_charge", (new ItemFireball()).func_77655_b("fireball"));
      func_179217_a(386, "writable_book", (new ItemWritableBook()).func_77655_b("writingBook").func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(387, "written_book", (new ItemEditableBook()).func_77655_b("writtenBook").func_77625_d(16));
      func_179217_a(388, "emerald", (new Item()).func_77655_b("emerald").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(389, "item_frame", (new ItemHangingEntity(EntityItemFrame.class)).func_77655_b("frame"));
      func_179217_a(390, "flower_pot", (new ItemReed(Blocks.field_150457_bL)).func_77655_b("flowerPot").func_77637_a(CreativeTabs.field_78031_c));
      func_179217_a(391, "carrot", (new ItemSeedFood(3, 0.6F, Blocks.field_150459_bM, Blocks.field_150458_ak)).func_77655_b("carrots"));
      func_179217_a(392, "potato", (new ItemSeedFood(1, 0.3F, Blocks.field_150469_bN, Blocks.field_150458_ak)).func_77655_b("potato"));
      func_179217_a(393, "baked_potato", (new ItemFood(5, 0.6F, false)).func_77655_b("potatoBaked"));
      func_179217_a(394, "poisonous_potato", (new ItemFood(2, 0.3F, false)).func_77844_a(Potion.field_76436_u.field_76415_H, 5, 0, 0.6F).func_77655_b("potatoPoisonous"));
      func_179217_a(395, "map", (new ItemEmptyMap()).func_77655_b("emptyMap"));
      func_179217_a(396, "golden_carrot", (new ItemFood(6, 1.2F, false)).func_77655_b("carrotGolden").func_77631_c(PotionHelper.field_82818_l).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(397, "skull", (new ItemSkull()).func_77655_b("skull"));
      func_179217_a(398, "carrot_on_a_stick", (new ItemCarrotOnAStick()).func_77655_b("carrotOnAStick"));
      func_179217_a(399, "nether_star", (new ItemSimpleFoiled()).func_77655_b("netherStar").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(400, "pumpkin_pie", (new ItemFood(8, 0.3F, false)).func_77655_b("pumpkinPie").func_77637_a(CreativeTabs.field_78039_h));
      func_179217_a(401, "fireworks", (new ItemFirework()).func_77655_b("fireworks"));
      func_179217_a(402, "firework_charge", (new ItemFireworkCharge()).func_77655_b("fireworksCharge").func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(403, "enchanted_book", (new ItemEnchantedBook()).func_77625_d(1).func_77655_b("enchantedBook"));
      func_179217_a(404, "comparator", (new ItemReed(Blocks.field_150441_bU)).func_77655_b("comparator").func_77637_a(CreativeTabs.field_78028_d));
      func_179217_a(405, "netherbrick", (new Item()).func_77655_b("netherbrick").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(406, "quartz", (new Item()).func_77655_b("netherquartz").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(407, "tnt_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.TNT)).func_77655_b("minecartTnt"));
      func_179217_a(408, "hopper_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.HOPPER)).func_77655_b("minecartHopper"));
      func_179217_a(409, "prismarine_shard", (new Item()).func_77655_b("prismarineShard").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(410, "prismarine_crystals", (new Item()).func_77655_b("prismarineCrystals").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(411, "rabbit", (new ItemFood(3, 0.3F, true)).func_77655_b("rabbitRaw"));
      func_179217_a(412, "cooked_rabbit", (new ItemFood(5, 0.6F, true)).func_77655_b("rabbitCooked"));
      func_179217_a(413, "rabbit_stew", (new ItemSoup(10)).func_77655_b("rabbitStew"));
      func_179217_a(414, "rabbit_foot", (new Item()).func_77655_b("rabbitFoot").func_77631_c(PotionHelper.field_179538_n).func_77637_a(CreativeTabs.field_78038_k));
      func_179217_a(415, "rabbit_hide", (new Item()).func_77655_b("rabbitHide").func_77637_a(CreativeTabs.field_78035_l));
      func_179217_a(416, "armor_stand", (new ItemArmorStand()).func_77655_b("armorStand").func_77625_d(16));
      func_179217_a(417, "iron_horse_armor", (new Item()).func_77655_b("horsearmormetal").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(418, "golden_horse_armor", (new Item()).func_77655_b("horsearmorgold").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(419, "diamond_horse_armor", (new Item()).func_77655_b("horsearmordiamond").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f));
      func_179217_a(420, "lead", (new ItemLead()).func_77655_b("leash"));
      func_179217_a(421, "name_tag", (new ItemNameTag()).func_77655_b("nameTag"));
      func_179217_a(422, "command_block_minecart", (new ItemMinecart(EntityMinecart.EnumMinecartType.COMMAND_BLOCK)).func_77655_b("minecartCommandBlock").func_77637_a((CreativeTabs)null));
      func_179217_a(423, "mutton", (new ItemFood(2, 0.3F, true)).func_77655_b("muttonRaw"));
      func_179217_a(424, "cooked_mutton", (new ItemFood(6, 0.8F, true)).func_77655_b("muttonCooked"));
      func_179217_a(425, "banner", (new ItemBanner()).func_77655_b("banner"));
      func_179217_a(427, "spruce_door", (new ItemDoor(Blocks.field_180414_ap)).func_77655_b("doorSpruce"));
      func_179217_a(428, "birch_door", (new ItemDoor(Blocks.field_180412_aq)).func_77655_b("doorBirch"));
      func_179217_a(429, "jungle_door", (new ItemDoor(Blocks.field_180411_ar)).func_77655_b("doorJungle"));
      func_179217_a(430, "acacia_door", (new ItemDoor(Blocks.field_180410_as)).func_77655_b("doorAcacia"));
      func_179217_a(431, "dark_oak_door", (new ItemDoor(Blocks.field_180409_at)).func_77655_b("doorDarkOak"));
      func_179217_a(2256, "record_13", (new ItemRecord("13")).func_77655_b("record"));
      func_179217_a(2257, "record_cat", (new ItemRecord("cat")).func_77655_b("record"));
      func_179217_a(2258, "record_blocks", (new ItemRecord("blocks")).func_77655_b("record"));
      func_179217_a(2259, "record_chirp", (new ItemRecord("chirp")).func_77655_b("record"));
      func_179217_a(2260, "record_far", (new ItemRecord("far")).func_77655_b("record"));
      func_179217_a(2261, "record_mall", (new ItemRecord("mall")).func_77655_b("record"));
      func_179217_a(2262, "record_mellohi", (new ItemRecord("mellohi")).func_77655_b("record"));
      func_179217_a(2263, "record_stal", (new ItemRecord("stal")).func_77655_b("record"));
      func_179217_a(2264, "record_strad", (new ItemRecord("strad")).func_77655_b("record"));
      func_179217_a(2265, "record_ward", (new ItemRecord("ward")).func_77655_b("record"));
      func_179217_a(2266, "record_11", (new ItemRecord("11")).func_77655_b("record"));
      func_179217_a(2267, "record_wait", (new ItemRecord("wait")).func_77655_b("record"));
   }

   private static void func_179216_c(Block var0) {
      func_179214_a(var0, new ItemBlock(var0));
   }

   protected static void func_179214_a(Block var0, Item var1) {
      func_179219_a(Block.func_149682_b(var0), (ResourceLocation)Block.field_149771_c.func_177774_c(var0), var1);
      field_179220_a.put(var0, var1);
   }

   private static void func_179217_a(int var0, String var1, Item var2) {
      func_179219_a(var0, new ResourceLocation(var1), var2);
   }

   private static void func_179219_a(int var0, ResourceLocation var1, Item var2) {
      field_150901_e.func_177775_a(var0, var1, var2);
   }

   public static enum ToolMaterial {
      WOOD(0, 59, 2.0F, 0.0F, 15),
      STONE(1, 131, 4.0F, 1.0F, 5),
      IRON(2, 250, 6.0F, 2.0F, 14),
      EMERALD(3, 1561, 8.0F, 3.0F, 10),
      GOLD(0, 32, 12.0F, 0.0F, 22);

      private final int field_78001_f;
      private final int field_78002_g;
      private final float field_78010_h;
      private final float field_78011_i;
      private final int field_78008_j;

      private ToolMaterial(int var3, int var4, float var5, float var6, int var7) {
         this.field_78001_f = var3;
         this.field_78002_g = var4;
         this.field_78010_h = var5;
         this.field_78011_i = var6;
         this.field_78008_j = var7;
      }

      public int func_77997_a() {
         return this.field_78002_g;
      }

      public float func_77998_b() {
         return this.field_78010_h;
      }

      public float func_78000_c() {
         return this.field_78011_i;
      }

      public int func_77996_d() {
         return this.field_78001_f;
      }

      public int func_77995_e() {
         return this.field_78008_j;
      }

      public Item func_150995_f() {
         if (this == WOOD) {
            return Item.func_150898_a(Blocks.field_150344_f);
         } else if (this == STONE) {
            return Item.func_150898_a(Blocks.field_150347_e);
         } else if (this == GOLD) {
            return Items.field_151043_k;
         } else if (this == IRON) {
            return Items.field_151042_j;
         } else {
            return this == EMERALD ? Items.field_151045_i : null;
         }
      }
   }
}
