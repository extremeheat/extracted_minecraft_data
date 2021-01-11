package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
   private static final Predicate<Entity> field_110276_bu = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof EntityHorse && ((EntityHorse)var1).func_110205_ce();
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
   private static final IAttribute field_110271_bv = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).func_111117_a("Jump Strength").func_111112_a(true);
   private static final String[] field_110270_bw = new String[]{null, "textures/entity/horse/armor/horse_armor_iron.png", "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png"};
   private static final String[] field_110273_bx = new String[]{"", "meo", "goo", "dio"};
   private static final int[] field_110272_by = new int[]{0, 5, 7, 11};
   private static final String[] field_110268_bz = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] field_110269_bA = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] field_110291_bB = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] field_110292_bC = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private int field_110289_bD;
   private int field_110290_bE;
   private int field_110295_bF;
   public int field_110278_bp;
   public int field_110279_bq;
   protected boolean field_110275_br;
   private AnimalChest field_110296_bG;
   private boolean field_110293_bH;
   protected int field_110274_bs;
   protected float field_110277_bt;
   private boolean field_110294_bI;
   private float field_110283_bJ;
   private float field_110284_bK;
   private float field_110281_bL;
   private float field_110282_bM;
   private float field_110287_bN;
   private float field_110288_bO;
   private int field_110285_bP;
   private String field_110286_bQ;
   private String[] field_110280_bR = new String[3];
   private boolean field_175508_bO = false;

   public EntityHorse(World var1) {
      super(var1);
      this.func_70105_a(1.4F, 1.6F);
      this.field_70178_ae = false;
      this.func_110207_m(false);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.2D));
      this.field_70714_bg.func_75776_a(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
      this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 1.0D));
      this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 0.7D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.func_110226_cD();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, 0);
      this.field_70180_af.func_75682_a(19, (byte)0);
      this.field_70180_af.func_75682_a(20, 0);
      this.field_70180_af.func_75682_a(21, String.valueOf(""));
      this.field_70180_af.func_75682_a(22, 0);
   }

   public void func_110214_p(int var1) {
      this.field_70180_af.func_75692_b(19, (byte)var1);
      this.func_110230_cF();
   }

   public int func_110265_bP() {
      return this.field_70180_af.func_75683_a(19);
   }

   public void func_110235_q(int var1) {
      this.field_70180_af.func_75692_b(20, var1);
      this.func_110230_cF();
   }

   public int func_110202_bQ() {
      return this.field_70180_af.func_75679_c(20);
   }

   public String func_70005_c_() {
      if (this.func_145818_k_()) {
         return this.func_95999_t();
      } else {
         int var1 = this.func_110265_bP();
         switch(var1) {
         case 0:
         default:
            return StatCollector.func_74838_a("entity.horse.name");
         case 1:
            return StatCollector.func_74838_a("entity.donkey.name");
         case 2:
            return StatCollector.func_74838_a("entity.mule.name");
         case 3:
            return StatCollector.func_74838_a("entity.zombiehorse.name");
         case 4:
            return StatCollector.func_74838_a("entity.skeletonhorse.name");
         }
      }
   }

   private boolean func_110233_w(int var1) {
      return (this.field_70180_af.func_75679_c(16) & var1) != 0;
   }

   private void func_110208_b(int var1, boolean var2) {
      int var3 = this.field_70180_af.func_75679_c(16);
      if (var2) {
         this.field_70180_af.func_75692_b(16, var3 | var1);
      } else {
         this.field_70180_af.func_75692_b(16, var3 & ~var1);
      }

   }

   public boolean func_110228_bR() {
      return !this.func_70631_g_();
   }

   public boolean func_110248_bS() {
      return this.func_110233_w(2);
   }

   public boolean func_110253_bW() {
      return this.func_110228_bR();
   }

   public String func_152119_ch() {
      return this.field_70180_af.func_75681_e(21);
   }

   public void func_152120_b(String var1) {
      this.field_70180_af.func_75692_b(21, var1);
   }

   public float func_110254_bY() {
      return 0.5F;
   }

   public void func_98054_a(boolean var1) {
      if (var1) {
         this.func_98055_j(this.func_110254_bY());
      } else {
         this.func_98055_j(1.0F);
      }

   }

   public boolean func_110246_bZ() {
      return this.field_110275_br;
   }

   public void func_110234_j(boolean var1) {
      this.func_110208_b(2, var1);
   }

   public void func_110255_k(boolean var1) {
      this.field_110275_br = var1;
   }

   public boolean func_110164_bC() {
      return !this.func_110256_cu() && super.func_110164_bC();
   }

   protected void func_142017_o(float var1) {
      if (var1 > 6.0F && this.func_110204_cc()) {
         this.func_110227_p(false);
      }

   }

   public boolean func_110261_ca() {
      return this.func_110233_w(8);
   }

   public int func_110241_cb() {
      return this.field_70180_af.func_75679_c(22);
   }

   private int func_110260_d(ItemStack var1) {
      if (var1 == null) {
         return 0;
      } else {
         Item var2 = var1.func_77973_b();
         if (var2 == Items.field_151138_bX) {
            return 1;
         } else if (var2 == Items.field_151136_bY) {
            return 2;
         } else {
            return var2 == Items.field_151125_bZ ? 3 : 0;
         }
      }
   }

   public boolean func_110204_cc() {
      return this.func_110233_w(32);
   }

   public boolean func_110209_cd() {
      return this.func_110233_w(64);
   }

   public boolean func_110205_ce() {
      return this.func_110233_w(16);
   }

   public boolean func_110243_cf() {
      return this.field_110293_bH;
   }

   public void func_146086_d(ItemStack var1) {
      this.field_70180_af.func_75692_b(22, this.func_110260_d(var1));
      this.func_110230_cF();
   }

   public void func_110242_l(boolean var1) {
      this.func_110208_b(16, var1);
   }

   public void func_110207_m(boolean var1) {
      this.func_110208_b(8, var1);
   }

   public void func_110221_n(boolean var1) {
      this.field_110293_bH = var1;
   }

   public void func_110251_o(boolean var1) {
      this.func_110208_b(4, var1);
   }

   public int func_110252_cg() {
      return this.field_110274_bs;
   }

   public void func_110238_s(int var1) {
      this.field_110274_bs = var1;
   }

   public int func_110198_t(int var1) {
      int var2 = MathHelper.func_76125_a(this.func_110252_cg() + var1, 0, this.func_110218_cm());
      this.func_110238_s(var2);
      return var2;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      Entity var3 = var1.func_76346_g();
      return this.field_70153_n != null && this.field_70153_n.equals(var3) ? false : super.func_70097_a(var1, var2);
   }

   public int func_70658_aO() {
      return field_110272_by[this.func_110241_cb()];
   }

   public boolean func_70104_M() {
      return this.field_70153_n == null;
   }

   public boolean func_110262_ch() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70161_v);
      this.field_70170_p.func_180494_b(new BlockPos(var1, 0, var2));
      return true;
   }

   public void func_110224_ci() {
      if (!this.field_70170_p.field_72995_K && this.func_110261_ca()) {
         this.func_145779_a(Item.func_150898_a(Blocks.field_150486_ae), 1);
         this.func_110207_m(false);
      }
   }

   private void func_110266_cB() {
      this.func_110249_cI();
      if (!this.func_174814_R()) {
         this.field_70170_p.func_72956_a(this, "eating", 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
      }

   }

   public void func_180430_e(float var1, float var2) {
      if (var1 > 1.0F) {
         this.func_85030_a("mob.horse.land", 0.4F, 1.0F);
      }

      int var3 = MathHelper.func_76123_f((var1 * 0.5F - 3.0F) * var2);
      if (var3 > 0) {
         this.func_70097_a(DamageSource.field_76379_h, (float)var3);
         if (this.field_70153_n != null) {
            this.field_70153_n.func_70097_a(DamageSource.field_76379_h, (float)var3);
         }

         Block var4 = this.field_70170_p.func_180495_p(new BlockPos(this.field_70165_t, this.field_70163_u - 0.2D - (double)this.field_70126_B, this.field_70161_v)).func_177230_c();
         if (var4.func_149688_o() != Material.field_151579_a && !this.func_174814_R()) {
            Block.SoundType var5 = var4.field_149762_H;
            this.field_70170_p.func_72956_a(this, var5.func_150498_e(), var5.func_150497_c() * 0.5F, var5.func_150494_d() * 0.75F);
         }

      }
   }

   private int func_110225_cC() {
      int var1 = this.func_110265_bP();
      return !this.func_110261_ca() || var1 != 1 && var1 != 2 ? 2 : 17;
   }

   private void func_110226_cD() {
      AnimalChest var1 = this.field_110296_bG;
      this.field_110296_bG = new AnimalChest("HorseChest", this.func_110225_cC());
      this.field_110296_bG.func_110133_a(this.func_70005_c_());
      if (var1 != null) {
         var1.func_110132_b(this);
         int var2 = Math.min(var1.func_70302_i_(), this.field_110296_bG.func_70302_i_());

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1.func_70301_a(var3);
            if (var4 != null) {
               this.field_110296_bG.func_70299_a(var3, var4.func_77946_l());
            }
         }
      }

      this.field_110296_bG.func_110134_a(this);
      this.func_110232_cE();
   }

   private void func_110232_cE() {
      if (!this.field_70170_p.field_72995_K) {
         this.func_110251_o(this.field_110296_bG.func_70301_a(0) != null);
         if (this.func_110259_cr()) {
            this.func_146086_d(this.field_110296_bG.func_70301_a(1));
         }
      }

   }

   public void func_76316_a(InventoryBasic var1) {
      int var2 = this.func_110241_cb();
      boolean var3 = this.func_110257_ck();
      this.func_110232_cE();
      if (this.field_70173_aa > 20) {
         if (var2 == 0 && var2 != this.func_110241_cb()) {
            this.func_85030_a("mob.horse.armor", 0.5F, 1.0F);
         } else if (var2 != this.func_110241_cb()) {
            this.func_85030_a("mob.horse.armor", 0.5F, 1.0F);
         }

         if (!var3 && this.func_110257_ck()) {
            this.func_85030_a("mob.horse.leather", 0.5F, 1.0F);
         }
      }

   }

   public boolean func_70601_bi() {
      this.func_110262_ch();
      return super.func_70601_bi();
   }

   protected EntityHorse func_110250_a(Entity var1, double var2) {
      double var4 = 1.7976931348623157E308D;
      Entity var6 = null;
      List var7 = this.field_70170_p.func_175674_a(var1, var1.func_174813_aQ().func_72321_a(var2, var2, var2), field_110276_bu);
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         Entity var9 = (Entity)var8.next();
         double var10 = var9.func_70092_e(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
         if (var10 < var4) {
            var6 = var9;
            var4 = var10;
         }
      }

      return (EntityHorse)var6;
   }

   public double func_110215_cj() {
      return this.func_110148_a(field_110271_bv).func_111126_e();
   }

   protected String func_70673_aS() {
      this.func_110249_cI();
      int var1 = this.func_110265_bP();
      if (var1 == 3) {
         return "mob.horse.zombie.death";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.death";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.death" : "mob.horse.donkey.death";
      }
   }

   protected Item func_146068_u() {
      boolean var1 = this.field_70146_Z.nextInt(4) == 0;
      int var2 = this.func_110265_bP();
      if (var2 == 4) {
         return Items.field_151103_aS;
      } else if (var2 == 3) {
         return var1 ? null : Items.field_151078_bh;
      } else {
         return Items.field_151116_aA;
      }
   }

   protected String func_70621_aR() {
      this.func_110249_cI();
      if (this.field_70146_Z.nextInt(3) == 0) {
         this.func_110220_cK();
      }

      int var1 = this.func_110265_bP();
      if (var1 == 3) {
         return "mob.horse.zombie.hit";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.hit";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit";
      }
   }

   public boolean func_110257_ck() {
      return this.func_110233_w(4);
   }

   protected String func_70639_aQ() {
      this.func_110249_cI();
      if (this.field_70146_Z.nextInt(10) == 0 && !this.func_70610_aX()) {
         this.func_110220_cK();
      }

      int var1 = this.func_110265_bP();
      if (var1 == 3) {
         return "mob.horse.zombie.idle";
      } else if (var1 == 4) {
         return "mob.horse.skeleton.idle";
      } else {
         return var1 != 1 && var1 != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle";
      }
   }

   protected String func_110217_cl() {
      this.func_110249_cI();
      this.func_110220_cK();
      int var1 = this.func_110265_bP();
      if (var1 != 3 && var1 != 4) {
         return var1 != 1 && var1 != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry";
      } else {
         return null;
      }
   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      Block.SoundType var3 = var2.field_149762_H;
      if (this.field_70170_p.func_180495_p(var1.func_177984_a()).func_177230_c() == Blocks.field_150431_aC) {
         var3 = Blocks.field_150431_aC.field_149762_H;
      }

      if (!var2.func_149688_o().func_76224_d()) {
         int var4 = this.func_110265_bP();
         if (this.field_70153_n != null && var4 != 1 && var4 != 2) {
            ++this.field_110285_bP;
            if (this.field_110285_bP > 5 && this.field_110285_bP % 3 == 0) {
               this.func_85030_a("mob.horse.gallop", var3.func_150497_c() * 0.15F, var3.func_150494_d());
               if (var4 == 0 && this.field_70146_Z.nextInt(10) == 0) {
                  this.func_85030_a("mob.horse.breathe", var3.func_150497_c() * 0.6F, var3.func_150494_d());
               }
            } else if (this.field_110285_bP <= 5) {
               this.func_85030_a("mob.horse.wood", var3.func_150497_c() * 0.15F, var3.func_150494_d());
            }
         } else if (var3 == Block.field_149766_f) {
            this.func_85030_a("mob.horse.wood", var3.func_150497_c() * 0.15F, var3.func_150494_d());
         } else {
            this.func_85030_a("mob.horse.soft", var3.func_150497_c() * 0.15F, var3.func_150494_d());
         }
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(field_110271_bv);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(53.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.22499999403953552D);
   }

   public int func_70641_bl() {
      return 6;
   }

   public int func_110218_cm() {
      return 100;
   }

   protected float func_70599_aP() {
      return 0.8F;
   }

   public int func_70627_aG() {
      return 400;
   }

   public boolean func_110239_cn() {
      return this.func_110265_bP() == 0 || this.func_110241_cb() > 0;
   }

   private void func_110230_cF() {
      this.field_110286_bQ = null;
   }

   public boolean func_175507_cI() {
      return this.field_175508_bO;
   }

   private void func_110247_cG() {
      this.field_110286_bQ = "horse/";
      this.field_110280_bR[0] = null;
      this.field_110280_bR[1] = null;
      this.field_110280_bR[2] = null;
      int var1 = this.func_110265_bP();
      int var2 = this.func_110202_bQ();
      int var3;
      if (var1 == 0) {
         var3 = var2 & 255;
         int var4 = (var2 & '\uff00') >> 8;
         if (var3 >= field_110268_bz.length) {
            this.field_175508_bO = false;
            return;
         }

         this.field_110280_bR[0] = field_110268_bz[var3];
         this.field_110286_bQ = this.field_110286_bQ + field_110269_bA[var3];
         if (var4 >= field_110291_bB.length) {
            this.field_175508_bO = false;
            return;
         }

         this.field_110280_bR[1] = field_110291_bB[var4];
         this.field_110286_bQ = this.field_110286_bQ + field_110292_bC[var4];
      } else {
         this.field_110280_bR[0] = "";
         this.field_110286_bQ = this.field_110286_bQ + "_" + var1 + "_";
      }

      var3 = this.func_110241_cb();
      if (var3 >= field_110270_bw.length) {
         this.field_175508_bO = false;
      } else {
         this.field_110280_bR[2] = field_110270_bw[var3];
         this.field_110286_bQ = this.field_110286_bQ + field_110273_bx[var3];
         this.field_175508_bO = true;
      }
   }

   public String func_110264_co() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110286_bQ;
   }

   public String[] func_110212_cp() {
      if (this.field_110286_bQ == null) {
         this.func_110247_cG();
      }

      return this.field_110280_bR;
   }

   public void func_110199_f(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K && (this.field_70153_n == null || this.field_70153_n == var1) && this.func_110248_bS()) {
         this.field_110296_bG.func_110133_a(this.func_70005_c_());
         var1.func_110298_a(this, this.field_110296_bG);
      }

   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && var2.func_77973_b() == Items.field_151063_bx) {
         return super.func_70085_c(var1);
      } else if (!this.func_110248_bS() && this.func_110256_cu()) {
         return false;
      } else if (this.func_110248_bS() && this.func_110228_bR() && var1.func_70093_af()) {
         this.func_110199_f(var1);
         return true;
      } else if (this.func_110253_bW() && this.field_70153_n != null) {
         return super.func_70085_c(var1);
      } else {
         if (var2 != null) {
            boolean var3 = false;
            if (this.func_110259_cr()) {
               byte var4 = -1;
               if (var2.func_77973_b() == Items.field_151138_bX) {
                  var4 = 1;
               } else if (var2.func_77973_b() == Items.field_151136_bY) {
                  var4 = 2;
               } else if (var2.func_77973_b() == Items.field_151125_bZ) {
                  var4 = 3;
               }

               if (var4 >= 0) {
                  if (!this.func_110248_bS()) {
                     this.func_110231_cz();
                     return true;
                  }

                  this.func_110199_f(var1);
                  return true;
               }
            }

            if (!var3 && !this.func_110256_cu()) {
               float var7 = 0.0F;
               short var5 = 0;
               byte var6 = 0;
               if (var2.func_77973_b() == Items.field_151015_O) {
                  var7 = 2.0F;
                  var5 = 20;
                  var6 = 3;
               } else if (var2.func_77973_b() == Items.field_151102_aT) {
                  var7 = 1.0F;
                  var5 = 30;
                  var6 = 3;
               } else if (Block.func_149634_a(var2.func_77973_b()) == Blocks.field_150407_cf) {
                  var7 = 20.0F;
                  var5 = 180;
               } else if (var2.func_77973_b() == Items.field_151034_e) {
                  var7 = 3.0F;
                  var5 = 60;
                  var6 = 3;
               } else if (var2.func_77973_b() == Items.field_151150_bK) {
                  var7 = 4.0F;
                  var5 = 60;
                  var6 = 5;
                  if (this.func_110248_bS() && this.func_70874_b() == 0) {
                     var3 = true;
                     this.func_146082_f(var1);
                  }
               } else if (var2.func_77973_b() == Items.field_151153_ao) {
                  var7 = 10.0F;
                  var5 = 240;
                  var6 = 10;
                  if (this.func_110248_bS() && this.func_70874_b() == 0) {
                     var3 = true;
                     this.func_146082_f(var1);
                  }
               }

               if (this.func_110143_aJ() < this.func_110138_aP() && var7 > 0.0F) {
                  this.func_70691_i(var7);
                  var3 = true;
               }

               if (!this.func_110228_bR() && var5 > 0) {
                  this.func_110195_a(var5);
                  var3 = true;
               }

               if (var6 > 0 && (var3 || !this.func_110248_bS()) && var6 < this.func_110218_cm()) {
                  var3 = true;
                  this.func_110198_t(var6);
               }

               if (var3) {
                  this.func_110266_cB();
               }
            }

            if (!this.func_110248_bS() && !var3) {
               if (var2 != null && var2.func_111282_a(var1, this)) {
                  return true;
               }

               this.func_110231_cz();
               return true;
            }

            if (!var3 && this.func_110229_cs() && !this.func_110261_ca() && var2.func_77973_b() == Item.func_150898_a(Blocks.field_150486_ae)) {
               this.func_110207_m(true);
               this.func_85030_a("mob.chickenplop", 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
               var3 = true;
               this.func_110226_cD();
            }

            if (!var3 && this.func_110253_bW() && !this.func_110257_ck() && var2.func_77973_b() == Items.field_151141_av) {
               this.func_110199_f(var1);
               return true;
            }

            if (var3) {
               if (!var1.field_71075_bZ.field_75098_d && --var2.field_77994_a == 0) {
                  var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
               }

               return true;
            }
         }

         if (this.func_110253_bW() && this.field_70153_n == null) {
            if (var2 != null && var2.func_111282_a(var1, this)) {
               return true;
            } else {
               this.func_110237_h(var1);
               return true;
            }
         } else {
            return super.func_70085_c(var1);
         }
      }
   }

   private void func_110237_h(EntityPlayer var1) {
      var1.field_70177_z = this.field_70177_z;
      var1.field_70125_A = this.field_70125_A;
      this.func_110227_p(false);
      this.func_110219_q(false);
      if (!this.field_70170_p.field_72995_K) {
         var1.func_70078_a(this);
      }

   }

   public boolean func_110259_cr() {
      return this.func_110265_bP() == 0;
   }

   public boolean func_110229_cs() {
      int var1 = this.func_110265_bP();
      return var1 == 2 || var1 == 1;
   }

   protected boolean func_70610_aX() {
      if (this.field_70153_n != null && this.func_110257_ck()) {
         return true;
      } else {
         return this.func_110204_cc() || this.func_110209_cd();
      }
   }

   public boolean func_110256_cu() {
      int var1 = this.func_110265_bP();
      return var1 == 3 || var1 == 4;
   }

   public boolean func_110222_cv() {
      return this.func_110256_cu() || this.func_110265_bP() == 2;
   }

   public boolean func_70877_b(ItemStack var1) {
      return false;
   }

   private void func_110210_cH() {
      this.field_110278_bp = 1;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (!this.field_70170_p.field_72995_K) {
         this.func_110244_cA();
      }

   }

   public void func_70636_d() {
      if (this.field_70146_Z.nextInt(200) == 0) {
         this.func_110210_cH();
      }

      super.func_70636_d();
      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70146_Z.nextInt(900) == 0 && this.field_70725_aQ == 0) {
            this.func_70691_i(1.0F);
         }

         if (!this.func_110204_cc() && this.field_70153_n == null && this.field_70146_Z.nextInt(300) == 0 && this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c() == Blocks.field_150349_c) {
            this.func_110227_p(true);
         }

         if (this.func_110204_cc() && ++this.field_110289_bD > 50) {
            this.field_110289_bD = 0;
            this.func_110227_p(false);
         }

         if (this.func_110205_ce() && !this.func_110228_bR() && !this.func_110204_cc()) {
            EntityHorse var1 = this.func_110250_a(this, 16.0D);
            if (var1 != null && this.func_70068_e(var1) > 4.0D) {
               this.field_70699_by.func_75494_a(var1);
            }
         }
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K && this.field_70180_af.func_75684_a()) {
         this.field_70180_af.func_111144_e();
         this.func_110230_cF();
      }

      if (this.field_110290_bE > 0 && ++this.field_110290_bE > 30) {
         this.field_110290_bE = 0;
         this.func_110208_b(128, false);
      }

      if (!this.field_70170_p.field_72995_K && this.field_110295_bF > 0 && ++this.field_110295_bF > 20) {
         this.field_110295_bF = 0;
         this.func_110219_q(false);
      }

      if (this.field_110278_bp > 0 && ++this.field_110278_bp > 8) {
         this.field_110278_bp = 0;
      }

      if (this.field_110279_bq > 0) {
         ++this.field_110279_bq;
         if (this.field_110279_bq > 300) {
            this.field_110279_bq = 0;
         }
      }

      this.field_110284_bK = this.field_110283_bJ;
      if (this.func_110204_cc()) {
         this.field_110283_bJ += (1.0F - this.field_110283_bJ) * 0.4F + 0.05F;
         if (this.field_110283_bJ > 1.0F) {
            this.field_110283_bJ = 1.0F;
         }
      } else {
         this.field_110283_bJ += (0.0F - this.field_110283_bJ) * 0.4F - 0.05F;
         if (this.field_110283_bJ < 0.0F) {
            this.field_110283_bJ = 0.0F;
         }
      }

      this.field_110282_bM = this.field_110281_bL;
      if (this.func_110209_cd()) {
         this.field_110284_bK = this.field_110283_bJ = 0.0F;
         this.field_110281_bL += (1.0F - this.field_110281_bL) * 0.4F + 0.05F;
         if (this.field_110281_bL > 1.0F) {
            this.field_110281_bL = 1.0F;
         }
      } else {
         this.field_110294_bI = false;
         this.field_110281_bL += (0.8F * this.field_110281_bL * this.field_110281_bL * this.field_110281_bL - this.field_110281_bL) * 0.6F - 0.05F;
         if (this.field_110281_bL < 0.0F) {
            this.field_110281_bL = 0.0F;
         }
      }

      this.field_110288_bO = this.field_110287_bN;
      if (this.func_110233_w(128)) {
         this.field_110287_bN += (1.0F - this.field_110287_bN) * 0.7F + 0.05F;
         if (this.field_110287_bN > 1.0F) {
            this.field_110287_bN = 1.0F;
         }
      } else {
         this.field_110287_bN += (0.0F - this.field_110287_bN) * 0.7F - 0.05F;
         if (this.field_110287_bN < 0.0F) {
            this.field_110287_bN = 0.0F;
         }
      }

   }

   private void func_110249_cI() {
      if (!this.field_70170_p.field_72995_K) {
         this.field_110290_bE = 1;
         this.func_110208_b(128, true);
      }

   }

   private boolean func_110200_cJ() {
      return this.field_70153_n == null && this.field_70154_o == null && this.func_110248_bS() && this.func_110228_bR() && !this.func_110222_cv() && this.func_110143_aJ() >= this.func_110138_aP() && this.func_70880_s();
   }

   public void func_70019_c(boolean var1) {
      this.func_110208_b(32, var1);
   }

   public void func_110227_p(boolean var1) {
      this.func_70019_c(var1);
   }

   public void func_110219_q(boolean var1) {
      if (var1) {
         this.func_110227_p(false);
      }

      this.func_110208_b(64, var1);
   }

   private void func_110220_cK() {
      if (!this.field_70170_p.field_72995_K) {
         this.field_110295_bF = 1;
         this.func_110219_q(true);
      }

   }

   public void func_110231_cz() {
      this.func_110220_cK();
      String var1 = this.func_110217_cl();
      if (var1 != null) {
         this.func_85030_a(var1, this.func_70599_aP(), this.func_70647_i());
      }

   }

   public void func_110244_cA() {
      this.func_110240_a(this, this.field_110296_bG);
      this.func_110224_ci();
   }

   private void func_110240_a(Entity var1, AnimalChest var2) {
      if (var2 != null && !this.field_70170_p.field_72995_K) {
         for(int var3 = 0; var3 < var2.func_70302_i_(); ++var3) {
            ItemStack var4 = var2.func_70301_a(var3);
            if (var4 != null) {
               this.func_70099_a(var4, 0.0F);
            }
         }

      }
   }

   public boolean func_110263_g(EntityPlayer var1) {
      this.func_152120_b(var1.func_110124_au().toString());
      this.func_110234_j(true);
      return true;
   }

   public void func_70612_e(float var1, float var2) {
      if (this.field_70153_n != null && this.field_70153_n instanceof EntityLivingBase && this.func_110257_ck()) {
         this.field_70126_B = this.field_70177_z = this.field_70153_n.field_70177_z;
         this.field_70125_A = this.field_70153_n.field_70125_A * 0.5F;
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         this.field_70759_as = this.field_70761_aq = this.field_70177_z;
         var1 = ((EntityLivingBase)this.field_70153_n).field_70702_br * 0.5F;
         var2 = ((EntityLivingBase)this.field_70153_n).field_70701_bs;
         if (var2 <= 0.0F) {
            var2 *= 0.25F;
            this.field_110285_bP = 0;
         }

         if (this.field_70122_E && this.field_110277_bt == 0.0F && this.func_110209_cd() && !this.field_110294_bI) {
            var1 = 0.0F;
            var2 = 0.0F;
         }

         if (this.field_110277_bt > 0.0F && !this.func_110246_bZ() && this.field_70122_E) {
            this.field_70181_x = this.func_110215_cj() * (double)this.field_110277_bt;
            if (this.func_70644_a(Potion.field_76430_j)) {
               this.field_70181_x += (double)((float)(this.func_70660_b(Potion.field_76430_j).func_76458_c() + 1) * 0.1F);
            }

            this.func_110255_k(true);
            this.field_70160_al = true;
            if (var2 > 0.0F) {
               float var3 = MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F);
               float var4 = MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F);
               this.field_70159_w += (double)(-0.4F * var3 * this.field_110277_bt);
               this.field_70179_y += (double)(0.4F * var4 * this.field_110277_bt);
               this.func_85030_a("mob.horse.jump", 0.4F, 1.0F);
            }

            this.field_110277_bt = 0.0F;
         }

         this.field_70138_W = 1.0F;
         this.field_70747_aH = this.func_70689_ay() * 0.1F;
         if (!this.field_70170_p.field_72995_K) {
            this.func_70659_e((float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            super.func_70612_e(var1, var2);
         }

         if (this.field_70122_E) {
            this.field_110277_bt = 0.0F;
            this.func_110255_k(false);
         }

         this.field_70722_aY = this.field_70721_aZ;
         double var8 = this.field_70165_t - this.field_70169_q;
         double var5 = this.field_70161_v - this.field_70166_s;
         float var7 = MathHelper.func_76133_a(var8 * var8 + var5 * var5) * 4.0F;
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         this.field_70721_aZ += (var7 - this.field_70721_aZ) * 0.4F;
         this.field_70754_ba += this.field_70721_aZ;
      } else {
         this.field_70138_W = 0.5F;
         this.field_70747_aH = 0.02F;
         super.func_70612_e(var1, var2);
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("EatingHaystack", this.func_110204_cc());
      var1.func_74757_a("ChestedHorse", this.func_110261_ca());
      var1.func_74757_a("HasReproduced", this.func_110243_cf());
      var1.func_74757_a("Bred", this.func_110205_ce());
      var1.func_74768_a("Type", this.func_110265_bP());
      var1.func_74768_a("Variant", this.func_110202_bQ());
      var1.func_74768_a("Temper", this.func_110252_cg());
      var1.func_74757_a("Tame", this.func_110248_bS());
      var1.func_74778_a("OwnerUUID", this.func_152119_ch());
      if (this.func_110261_ca()) {
         NBTTagList var2 = new NBTTagList();

         for(int var3 = 2; var3 < this.field_110296_bG.func_70302_i_(); ++var3) {
            ItemStack var4 = this.field_110296_bG.func_70301_a(var3);
            if (var4 != null) {
               NBTTagCompound var5 = new NBTTagCompound();
               var5.func_74774_a("Slot", (byte)var3);
               var4.func_77955_b(var5);
               var2.func_74742_a(var5);
            }
         }

         var1.func_74782_a("Items", var2);
      }

      if (this.field_110296_bG.func_70301_a(1) != null) {
         var1.func_74782_a("ArmorItem", this.field_110296_bG.func_70301_a(1).func_77955_b(new NBTTagCompound()));
      }

      if (this.field_110296_bG.func_70301_a(0) != null) {
         var1.func_74782_a("SaddleItem", this.field_110296_bG.func_70301_a(0).func_77955_b(new NBTTagCompound()));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_110227_p(var1.func_74767_n("EatingHaystack"));
      this.func_110242_l(var1.func_74767_n("Bred"));
      this.func_110207_m(var1.func_74767_n("ChestedHorse"));
      this.func_110221_n(var1.func_74767_n("HasReproduced"));
      this.func_110214_p(var1.func_74762_e("Type"));
      this.func_110235_q(var1.func_74762_e("Variant"));
      this.func_110238_s(var1.func_74762_e("Temper"));
      this.func_110234_j(var1.func_74767_n("Tame"));
      String var2 = "";
      if (var1.func_150297_b("OwnerUUID", 8)) {
         var2 = var1.func_74779_i("OwnerUUID");
      } else {
         String var3 = var1.func_74779_i("Owner");
         var2 = PreYggdrasilConverter.func_152719_a(var3);
      }

      if (var2.length() > 0) {
         this.func_152120_b(var2);
      }

      IAttributeInstance var8 = this.func_110140_aT().func_111152_a("Speed");
      if (var8 != null) {
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(var8.func_111125_b() * 0.25D);
      }

      if (this.func_110261_ca()) {
         NBTTagList var4 = var1.func_150295_c("Items", 10);
         this.func_110226_cD();

         for(int var5 = 0; var5 < var4.func_74745_c(); ++var5) {
            NBTTagCompound var6 = var4.func_150305_b(var5);
            int var7 = var6.func_74771_c("Slot") & 255;
            if (var7 >= 2 && var7 < this.field_110296_bG.func_70302_i_()) {
               this.field_110296_bG.func_70299_a(var7, ItemStack.func_77949_a(var6));
            }
         }
      }

      ItemStack var9;
      if (var1.func_150297_b("ArmorItem", 10)) {
         var9 = ItemStack.func_77949_a(var1.func_74775_l("ArmorItem"));
         if (var9 != null && func_146085_a(var9.func_77973_b())) {
            this.field_110296_bG.func_70299_a(1, var9);
         }
      }

      if (var1.func_150297_b("SaddleItem", 10)) {
         var9 = ItemStack.func_77949_a(var1.func_74775_l("SaddleItem"));
         if (var9 != null && var9.func_77973_b() == Items.field_151141_av) {
            this.field_110296_bG.func_70299_a(0, var9);
         }
      } else if (var1.func_74767_n("Saddle")) {
         this.field_110296_bG.func_70299_a(0, new ItemStack(Items.field_151141_av));
      }

      this.func_110232_cE();
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse var2 = (EntityHorse)var1;
         if (this.func_110200_cJ() && var2.func_110200_cJ()) {
            int var3 = this.func_110265_bP();
            int var4 = var2.func_110265_bP();
            return var3 == var4 || var3 == 0 && var4 == 1 || var3 == 1 && var4 == 0;
         } else {
            return false;
         }
      }
   }

   public EntityAgeable func_90011_a(EntityAgeable var1) {
      EntityHorse var2 = (EntityHorse)var1;
      EntityHorse var3 = new EntityHorse(this.field_70170_p);
      int var4 = this.func_110265_bP();
      int var5 = var2.func_110265_bP();
      int var6 = 0;
      if (var4 == var5) {
         var6 = var4;
      } else if (var4 == 0 && var5 == 1 || var4 == 1 && var5 == 0) {
         var6 = 2;
      }

      if (var6 == 0) {
         int var8 = this.field_70146_Z.nextInt(9);
         int var7;
         if (var8 < 4) {
            var7 = this.func_110202_bQ() & 255;
         } else if (var8 < 8) {
            var7 = var2.func_110202_bQ() & 255;
         } else {
            var7 = this.field_70146_Z.nextInt(7);
         }

         int var9 = this.field_70146_Z.nextInt(5);
         if (var9 < 2) {
            var7 |= this.func_110202_bQ() & '\uff00';
         } else if (var9 < 4) {
            var7 |= var2.func_110202_bQ() & '\uff00';
         } else {
            var7 |= this.field_70146_Z.nextInt(5) << 8 & '\uff00';
         }

         var3.func_110235_q(var7);
      }

      var3.func_110214_p(var6);
      double var14 = this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() + var1.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() + (double)this.func_110267_cL();
      var3.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(var14 / 3.0D);
      double var13 = this.func_110148_a(field_110271_bv).func_111125_b() + var1.func_110148_a(field_110271_bv).func_111125_b() + this.func_110245_cM();
      var3.func_110148_a(field_110271_bv).func_111128_a(var13 / 3.0D);
      double var11 = this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111125_b() + var1.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111125_b() + this.func_110203_cN();
      var3.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(var11 / 3.0D);
      return var3;
   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      Object var7 = super.func_180482_a(var1, var2);
      boolean var3 = false;
      int var4 = 0;
      int var8;
      if (var7 instanceof EntityHorse.GroupData) {
         var8 = ((EntityHorse.GroupData)var7).field_111107_a;
         var4 = ((EntityHorse.GroupData)var7).field_111106_b & 255 | this.field_70146_Z.nextInt(5) << 8;
      } else {
         if (this.field_70146_Z.nextInt(10) == 0) {
            var8 = 1;
         } else {
            int var5 = this.field_70146_Z.nextInt(7);
            int var6 = this.field_70146_Z.nextInt(5);
            var8 = 0;
            var4 = var5 | var6 << 8;
         }

         var7 = new EntityHorse.GroupData(var8, var4);
      }

      this.func_110214_p(var8);
      this.func_110235_q(var4);
      if (this.field_70146_Z.nextInt(5) == 0) {
         this.func_70873_a(-24000);
      }

      if (var8 != 4 && var8 != 3) {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)this.func_110267_cL());
         if (var8 == 0) {
            this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(this.func_110203_cN());
         } else {
            this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.17499999701976776D);
         }
      } else {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(15.0D);
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
      }

      if (var8 != 2 && var8 != 1) {
         this.func_110148_a(field_110271_bv).func_111128_a(this.func_110245_cM());
      } else {
         this.func_110148_a(field_110271_bv).func_111128_a(0.5D);
      }

      this.func_70606_j(this.func_110138_aP());
      return (IEntityLivingData)var7;
   }

   public float func_110258_o(float var1) {
      return this.field_110284_bK + (this.field_110283_bJ - this.field_110284_bK) * var1;
   }

   public float func_110223_p(float var1) {
      return this.field_110282_bM + (this.field_110281_bL - this.field_110282_bM) * var1;
   }

   public float func_110201_q(float var1) {
      return this.field_110288_bO + (this.field_110287_bN - this.field_110288_bO) * var1;
   }

   public void func_110206_u(int var1) {
      if (this.func_110257_ck()) {
         if (var1 < 0) {
            var1 = 0;
         } else {
            this.field_110294_bI = true;
            this.func_110220_cK();
         }

         if (var1 >= 90) {
            this.field_110277_bt = 1.0F;
         } else {
            this.field_110277_bt = 0.4F + 0.4F * (float)var1 / 90.0F;
         }
      }

   }

   protected void func_110216_r(boolean var1) {
      EnumParticleTypes var2 = var1 ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_175688_a(var2, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var4, var6, var8);
      }

   }

   public void func_70103_a(byte var1) {
      if (var1 == 7) {
         this.func_110216_r(true);
      } else if (var1 == 6) {
         this.func_110216_r(false);
      } else {
         super.func_70103_a(var1);
      }

   }

   public void func_70043_V() {
      super.func_70043_V();
      if (this.field_110282_bM > 0.0F) {
         float var1 = MathHelper.func_76126_a(this.field_70761_aq * 3.1415927F / 180.0F);
         float var2 = MathHelper.func_76134_b(this.field_70761_aq * 3.1415927F / 180.0F);
         float var3 = 0.7F * this.field_110282_bM;
         float var4 = 0.15F * this.field_110282_bM;
         this.field_70153_n.func_70107_b(this.field_70165_t + (double)(var3 * var1), this.field_70163_u + this.func_70042_X() + this.field_70153_n.func_70033_W() + (double)var4, this.field_70161_v - (double)(var3 * var2));
         if (this.field_70153_n instanceof EntityLivingBase) {
            ((EntityLivingBase)this.field_70153_n).field_70761_aq = this.field_70761_aq;
         }
      }

   }

   private float func_110267_cL() {
      return 15.0F + (float)this.field_70146_Z.nextInt(8) + (float)this.field_70146_Z.nextInt(9);
   }

   private double func_110245_cM() {
      return 0.4000000059604645D + this.field_70146_Z.nextDouble() * 0.2D + this.field_70146_Z.nextDouble() * 0.2D + this.field_70146_Z.nextDouble() * 0.2D;
   }

   private double func_110203_cN() {
      return (0.44999998807907104D + this.field_70146_Z.nextDouble() * 0.3D + this.field_70146_Z.nextDouble() * 0.3D + this.field_70146_Z.nextDouble() * 0.3D) * 0.25D;
   }

   public static boolean func_146085_a(Item var0) {
      return var0 == Items.field_151138_bX || var0 == Items.field_151136_bY || var0 == Items.field_151125_bZ;
   }

   public boolean func_70617_f_() {
      return false;
   }

   public float func_70047_e() {
      return this.field_70131_O;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (var1 == 499 && this.func_110229_cs()) {
         if (var2 == null && this.func_110261_ca()) {
            this.func_110207_m(false);
            this.func_110226_cD();
            return true;
         }

         if (var2 != null && var2.func_77973_b() == Item.func_150898_a(Blocks.field_150486_ae) && !this.func_110261_ca()) {
            this.func_110207_m(true);
            this.func_110226_cD();
            return true;
         }
      }

      int var3 = var1 - 400;
      if (var3 >= 0 && var3 < 2 && var3 < this.field_110296_bG.func_70302_i_()) {
         if (var3 == 0 && var2 != null && var2.func_77973_b() != Items.field_151141_av) {
            return false;
         } else if (var3 != 1 || (var2 == null || func_146085_a(var2.func_77973_b())) && this.func_110259_cr()) {
            this.field_110296_bG.func_70299_a(var3, var2);
            this.func_110232_cE();
            return true;
         } else {
            return false;
         }
      } else {
         int var4 = var1 - 500 + 2;
         if (var4 >= 2 && var4 < this.field_110296_bG.func_70302_i_()) {
            this.field_110296_bG.func_70299_a(var4, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int field_111107_a;
      public int field_111106_b;

      public GroupData(int var1, int var2) {
         super();
         this.field_111107_a = var1;
         this.field_111106_b = var2;
      }
   }
}
