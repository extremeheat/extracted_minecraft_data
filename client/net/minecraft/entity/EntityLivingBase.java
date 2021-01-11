package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {
   private static final UUID field_110156_b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier field_110157_c;
   private BaseAttributeMap field_110155_d;
   private final CombatTracker field_94063_bt = new CombatTracker(this);
   private final Map<Integer, PotionEffect> field_70713_bf = Maps.newHashMap();
   private final ItemStack[] field_82180_bT = new ItemStack[5];
   public boolean field_82175_bq;
   public int field_110158_av;
   public int field_70720_be;
   public int field_70737_aN;
   public int field_70738_aO;
   public float field_70739_aP;
   public int field_70725_aQ;
   public float field_70732_aI;
   public float field_70733_aJ;
   public float field_70722_aY;
   public float field_70721_aZ;
   public float field_70754_ba;
   public int field_70771_an = 20;
   public float field_70727_aS;
   public float field_70726_aT;
   public float field_70769_ao;
   public float field_70770_ap;
   public float field_70761_aq;
   public float field_70760_ar;
   public float field_70759_as;
   public float field_70758_at;
   public float field_70747_aH = 0.02F;
   protected EntityPlayer field_70717_bb;
   protected int field_70718_bc;
   protected boolean field_70729_aU;
   protected int field_70708_bq;
   protected float field_70768_au;
   protected float field_110154_aX;
   protected float field_70764_aw;
   protected float field_70763_ax;
   protected float field_70741_aB;
   protected int field_70744_aE;
   protected float field_110153_bc;
   protected boolean field_70703_bu;
   public float field_70702_br;
   public float field_70701_bs;
   protected float field_70704_bt;
   protected int field_70716_bi;
   protected double field_70709_bj;
   protected double field_70710_bk;
   protected double field_110152_bk;
   protected double field_70712_bm;
   protected double field_70705_bn;
   private boolean field_70752_e = true;
   private EntityLivingBase field_70755_b;
   private int field_70756_c;
   private EntityLivingBase field_110150_bn;
   private int field_142016_bo;
   private float field_70746_aG;
   private int field_70773_bE;
   private float field_110151_bq;

   public void func_174812_G() {
      this.func_70097_a(DamageSource.field_76380_i, 3.4028235E38F);
   }

   public EntityLivingBase(World var1) {
      super(var1);
      this.func_110147_ax();
      this.func_70606_j(this.func_110138_aP());
      this.field_70156_m = true;
      this.field_70770_ap = (float)((Math.random() + 1.0D) * 0.009999999776482582D);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70769_ao = (float)Math.random() * 12398.0F;
      this.field_70177_z = (float)(Math.random() * 3.1415927410125732D * 2.0D);
      this.field_70759_as = this.field_70177_z;
      this.field_70138_W = 0.6F;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_75682_a(7, 0);
      this.field_70180_af.func_75682_a(8, (byte)0);
      this.field_70180_af.func_75682_a(9, (byte)0);
      this.field_70180_af.func_75682_a(6, 1.0F);
   }

   protected void func_110147_ax() {
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111267_a);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111266_c);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111263_d);
   }

   protected void func_180433_a(double var1, boolean var3, Block var4, BlockPos var5) {
      if (!this.func_70090_H()) {
         this.func_70072_I();
      }

      if (!this.field_70170_p.field_72995_K && this.field_70143_R > 3.0F && var3) {
         IBlockState var6 = this.field_70170_p.func_180495_p(var5);
         Block var7 = var6.func_177230_c();
         float var8 = (float)MathHelper.func_76123_f(this.field_70143_R - 3.0F);
         if (var7.func_149688_o() != Material.field_151579_a) {
            double var9 = (double)Math.min(0.2F + var8 / 15.0F, 10.0F);
            if (var9 > 2.5D) {
               var9 = 2.5D;
            }

            int var11 = (int)(150.0D * var9);
            ((WorldServer)this.field_70170_p).func_175739_a(EnumParticleTypes.BLOCK_DUST, this.field_70165_t, this.field_70163_u, this.field_70161_v, var11, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.func_176210_f(var6));
         }
      }

      super.func_180433_a(var1, var3, var4, var5);
   }

   public boolean func_70648_aU() {
      return false;
   }

   public void func_70030_z() {
      this.field_70732_aI = this.field_70733_aJ;
      super.func_70030_z();
      this.field_70170_p.field_72984_F.func_76320_a("livingEntityBaseTick");
      boolean var1 = this instanceof EntityPlayer;
      if (this.func_70089_S()) {
         if (this.func_70094_T()) {
            this.func_70097_a(DamageSource.field_76368_d, 1.0F);
         } else if (var1 && !this.field_70170_p.func_175723_af().func_177743_a(this.func_174813_aQ())) {
            double var2 = this.field_70170_p.func_175723_af().func_177745_a(this) + this.field_70170_p.func_175723_af().func_177742_m();
            if (var2 < 0.0D) {
               this.func_70097_a(DamageSource.field_76368_d, (float)Math.max(1, MathHelper.func_76128_c(-var2 * this.field_70170_p.func_175723_af().func_177727_n())));
            }
         }
      }

      if (this.func_70045_F() || this.field_70170_p.field_72995_K) {
         this.func_70066_B();
      }

      boolean var7 = var1 && ((EntityPlayer)this).field_71075_bZ.field_75102_a;
      if (this.func_70089_S()) {
         if (this.func_70055_a(Material.field_151586_h)) {
            if (!this.func_70648_aU() && !this.func_82165_m(Potion.field_76427_o.field_76415_H) && !var7) {
               this.func_70050_g(this.func_70682_h(this.func_70086_ai()));
               if (this.func_70086_ai() == -20) {
                  this.func_70050_g(0);

                  for(int var3 = 0; var3 < 8; ++var3) {
                     float var4 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     float var5 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     float var6 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (double)var4, this.field_70163_u + (double)var5, this.field_70161_v + (double)var6, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                  }

                  this.func_70097_a(DamageSource.field_76369_e, 2.0F);
               }
            }

            if (!this.field_70170_p.field_72995_K && this.func_70115_ae() && this.field_70154_o instanceof EntityLivingBase) {
               this.func_70078_a((Entity)null);
            }
         } else {
            this.func_70050_g(300);
         }
      }

      if (this.func_70089_S() && this.func_70026_G()) {
         this.func_70066_B();
      }

      this.field_70727_aS = this.field_70726_aT;
      if (this.field_70737_aN > 0) {
         --this.field_70737_aN;
      }

      if (this.field_70172_ad > 0 && !(this instanceof EntityPlayerMP)) {
         --this.field_70172_ad;
      }

      if (this.func_110143_aJ() <= 0.0F) {
         this.func_70609_aI();
      }

      if (this.field_70718_bc > 0) {
         --this.field_70718_bc;
      } else {
         this.field_70717_bb = null;
      }

      if (this.field_110150_bn != null && !this.field_110150_bn.func_70089_S()) {
         this.field_110150_bn = null;
      }

      if (this.field_70755_b != null) {
         if (!this.field_70755_b.func_70089_S()) {
            this.func_70604_c((EntityLivingBase)null);
         } else if (this.field_70173_aa - this.field_70756_c > 100) {
            this.func_70604_c((EntityLivingBase)null);
         }
      }

      this.func_70679_bo();
      this.field_70763_ax = this.field_70764_aw;
      this.field_70760_ar = this.field_70761_aq;
      this.field_70758_at = this.field_70759_as;
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   public boolean func_70631_g_() {
      return false;
   }

   protected void func_70609_aI() {
      ++this.field_70725_aQ;
      if (this.field_70725_aQ == 20) {
         int var1;
         if (!this.field_70170_p.field_72995_K && (this.field_70718_bc > 0 || this.func_70684_aJ()) && this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
            var1 = this.func_70693_a(this.field_70717_bb);

            while(var1 > 0) {
               int var2 = EntityXPOrb.func_70527_a(var1);
               var1 -= var2;
               this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var2));
            }
         }

         this.func_70106_y();

         for(var1 = 0; var1 < 20; ++var1) {
            double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_NORMAL, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var8, var4, var6);
         }
      }

   }

   protected boolean func_146066_aG() {
      return !this.func_70631_g_();
   }

   protected int func_70682_h(int var1) {
      int var2 = EnchantmentHelper.func_180319_a(this);
      return var2 > 0 && this.field_70146_Z.nextInt(var2 + 1) > 0 ? var1 : var1 - 1;
   }

   protected int func_70693_a(EntityPlayer var1) {
      return 0;
   }

   protected boolean func_70684_aJ() {
      return false;
   }

   public Random func_70681_au() {
      return this.field_70146_Z;
   }

   public EntityLivingBase func_70643_av() {
      return this.field_70755_b;
   }

   public int func_142015_aE() {
      return this.field_70756_c;
   }

   public void func_70604_c(EntityLivingBase var1) {
      this.field_70755_b = var1;
      this.field_70756_c = this.field_70173_aa;
   }

   public EntityLivingBase func_110144_aD() {
      return this.field_110150_bn;
   }

   public int func_142013_aG() {
      return this.field_142016_bo;
   }

   public void func_130011_c(Entity var1) {
      if (var1 instanceof EntityLivingBase) {
         this.field_110150_bn = (EntityLivingBase)var1;
      } else {
         this.field_110150_bn = null;
      }

      this.field_142016_bo = this.field_70173_aa;
   }

   public int func_70654_ax() {
      return this.field_70708_bq;
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74776_a("HealF", this.func_110143_aJ());
      var1.func_74777_a("Health", (short)((int)Math.ceil((double)this.func_110143_aJ())));
      var1.func_74777_a("HurtTime", (short)this.field_70737_aN);
      var1.func_74768_a("HurtByTimestamp", this.field_70756_c);
      var1.func_74777_a("DeathTime", (short)this.field_70725_aQ);
      var1.func_74776_a("AbsorptionAmount", this.func_110139_bj());
      ItemStack[] var2 = this.func_70035_c();
      int var3 = var2.length;

      int var4;
      ItemStack var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5 != null) {
            this.field_110155_d.func_111148_a(var5.func_111283_C());
         }
      }

      var1.func_74782_a("Attributes", SharedMonsterAttributes.func_111257_a(this.func_110140_aT()));
      var2 = this.func_70035_c();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         if (var5 != null) {
            this.field_110155_d.func_111147_b(var5.func_111283_C());
         }
      }

      if (!this.field_70713_bf.isEmpty()) {
         NBTTagList var6 = new NBTTagList();
         Iterator var7 = this.field_70713_bf.values().iterator();

         while(var7.hasNext()) {
            PotionEffect var8 = (PotionEffect)var7.next();
            var6.func_74742_a(var8.func_82719_a(new NBTTagCompound()));
         }

         var1.func_74782_a("ActiveEffects", var6);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.func_110149_m(var1.func_74760_g("AbsorptionAmount"));
      if (var1.func_150297_b("Attributes", 9) && this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         SharedMonsterAttributes.func_151475_a(this.func_110140_aT(), var1.func_150295_c("Attributes", 10));
      }

      if (var1.func_150297_b("ActiveEffects", 9)) {
         NBTTagList var2 = var1.func_150295_c("ActiveEffects", 10);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            NBTTagCompound var4 = var2.func_150305_b(var3);
            PotionEffect var5 = PotionEffect.func_82722_b(var4);
            if (var5 != null) {
               this.field_70713_bf.put(var5.func_76456_a(), var5);
            }
         }
      }

      if (var1.func_150297_b("HealF", 99)) {
         this.func_70606_j(var1.func_74760_g("HealF"));
      } else {
         NBTBase var6 = var1.func_74781_a("Health");
         if (var6 == null) {
            this.func_70606_j(this.func_110138_aP());
         } else if (var6.func_74732_a() == 5) {
            this.func_70606_j(((NBTTagFloat)var6).func_150288_h());
         } else if (var6.func_74732_a() == 2) {
            this.func_70606_j((float)((NBTTagShort)var6).func_150289_e());
         }
      }

      this.field_70737_aN = var1.func_74765_d("HurtTime");
      this.field_70725_aQ = var1.func_74765_d("DeathTime");
      this.field_70756_c = var1.func_74762_e("HurtByTimestamp");
   }

   protected void func_70679_bo() {
      Iterator var1 = this.field_70713_bf.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         PotionEffect var3 = (PotionEffect)this.field_70713_bf.get(var2);
         if (!var3.func_76455_a(this)) {
            if (!this.field_70170_p.field_72995_K) {
               var1.remove();
               this.func_70688_c(var3);
            }
         } else if (var3.func_76459_b() % 600 == 0) {
            this.func_70695_b(var3, false);
         }
      }

      if (this.field_70752_e) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_175135_B();
         }

         this.field_70752_e = false;
      }

      int var11 = this.field_70180_af.func_75679_c(7);
      boolean var12 = this.field_70180_af.func_75683_a(8) > 0;
      if (var11 > 0) {
         boolean var4 = false;
         if (!this.func_82150_aj()) {
            var4 = this.field_70146_Z.nextBoolean();
         } else {
            var4 = this.field_70146_Z.nextInt(15) == 0;
         }

         if (var12) {
            var4 &= this.field_70146_Z.nextInt(5) == 0;
         }

         if (var4 && var11 > 0) {
            double var5 = (double)(var11 >> 16 & 255) / 255.0D;
            double var7 = (double)(var11 >> 8 & 255) / 255.0D;
            double var9 = (double)(var11 >> 0 & 255) / 255.0D;
            this.field_70170_p.func_175688_a(var12 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, var5, var7, var9);
         }
      }

   }

   protected void func_175135_B() {
      if (this.field_70713_bf.isEmpty()) {
         this.func_175133_bi();
         this.func_82142_c(false);
      } else {
         int var1 = PotionHelper.func_77911_a(this.field_70713_bf.values());
         this.field_70180_af.func_75692_b(8, Byte.valueOf((byte)(PotionHelper.func_82817_b(this.field_70713_bf.values()) ? 1 : 0)));
         this.field_70180_af.func_75692_b(7, var1);
         this.func_82142_c(this.func_82165_m(Potion.field_76441_p.field_76415_H));
      }

   }

   protected void func_175133_bi() {
      this.field_70180_af.func_75692_b(8, (byte)0);
      this.field_70180_af.func_75692_b(7, 0);
   }

   public void func_70674_bp() {
      Iterator var1 = this.field_70713_bf.keySet().iterator();

      while(var1.hasNext()) {
         Integer var2 = (Integer)var1.next();
         PotionEffect var3 = (PotionEffect)this.field_70713_bf.get(var2);
         if (!this.field_70170_p.field_72995_K) {
            var1.remove();
            this.func_70688_c(var3);
         }
      }

   }

   public Collection<PotionEffect> func_70651_bq() {
      return this.field_70713_bf.values();
   }

   public boolean func_82165_m(int var1) {
      return this.field_70713_bf.containsKey(var1);
   }

   public boolean func_70644_a(Potion var1) {
      return this.field_70713_bf.containsKey(var1.field_76415_H);
   }

   public PotionEffect func_70660_b(Potion var1) {
      return (PotionEffect)this.field_70713_bf.get(var1.field_76415_H);
   }

   public void func_70690_d(PotionEffect var1) {
      if (this.func_70687_e(var1)) {
         if (this.field_70713_bf.containsKey(var1.func_76456_a())) {
            ((PotionEffect)this.field_70713_bf.get(var1.func_76456_a())).func_76452_a(var1);
            this.func_70695_b((PotionEffect)this.field_70713_bf.get(var1.func_76456_a()), true);
         } else {
            this.field_70713_bf.put(var1.func_76456_a(), var1);
            this.func_70670_a(var1);
         }

      }
   }

   public boolean func_70687_e(PotionEffect var1) {
      if (this.func_70668_bt() == EnumCreatureAttribute.UNDEAD) {
         int var2 = var1.func_76456_a();
         if (var2 == Potion.field_76428_l.field_76415_H || var2 == Potion.field_76436_u.field_76415_H) {
            return false;
         }
      }

      return true;
   }

   public boolean func_70662_br() {
      return this.func_70668_bt() == EnumCreatureAttribute.UNDEAD;
   }

   public void func_70618_n(int var1) {
      this.field_70713_bf.remove(var1);
   }

   public void func_82170_o(int var1) {
      PotionEffect var2 = (PotionEffect)this.field_70713_bf.remove(var1);
      if (var2 != null) {
         this.func_70688_c(var2);
      }

   }

   protected void func_70670_a(PotionEffect var1) {
      this.field_70752_e = true;
      if (!this.field_70170_p.field_72995_K) {
         Potion.field_76425_a[var1.func_76456_a()].func_111185_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   protected void func_70695_b(PotionEffect var1, boolean var2) {
      this.field_70752_e = true;
      if (var2 && !this.field_70170_p.field_72995_K) {
         Potion.field_76425_a[var1.func_76456_a()].func_111187_a(this, this.func_110140_aT(), var1.func_76458_c());
         Potion.field_76425_a[var1.func_76456_a()].func_111185_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   protected void func_70688_c(PotionEffect var1) {
      this.field_70752_e = true;
      if (!this.field_70170_p.field_72995_K) {
         Potion.field_76425_a[var1.func_76456_a()].func_111187_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   public void func_70691_i(float var1) {
      float var2 = this.func_110143_aJ();
      if (var2 > 0.0F) {
         this.func_70606_j(var2 + var1);
      }

   }

   public final float func_110143_aJ() {
      return this.field_70180_af.func_111145_d(6);
   }

   public void func_70606_j(float var1) {
      this.field_70180_af.func_75692_b(6, MathHelper.func_76131_a(var1, 0.0F, this.func_110138_aP()));
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (this.field_70170_p.field_72995_K) {
         return false;
      } else {
         this.field_70708_bq = 0;
         if (this.func_110143_aJ() <= 0.0F) {
            return false;
         } else if (var1.func_76347_k() && this.func_70644_a(Potion.field_76426_n)) {
            return false;
         } else {
            if ((var1 == DamageSource.field_82728_o || var1 == DamageSource.field_82729_p) && this.func_71124_b(4) != null) {
               this.func_71124_b(4).func_77972_a((int)(var2 * 4.0F + this.field_70146_Z.nextFloat() * var2 * 2.0F), this);
               var2 *= 0.75F;
            }

            this.field_70721_aZ = 1.5F;
            boolean var3 = true;
            if ((float)this.field_70172_ad > (float)this.field_70771_an / 2.0F) {
               if (var2 <= this.field_110153_bc) {
                  return false;
               }

               this.func_70665_d(var1, var2 - this.field_110153_bc);
               this.field_110153_bc = var2;
               var3 = false;
            } else {
               this.field_110153_bc = var2;
               this.field_70172_ad = this.field_70771_an;
               this.func_70665_d(var1, var2);
               this.field_70737_aN = this.field_70738_aO = 10;
            }

            this.field_70739_aP = 0.0F;
            Entity var4 = var1.func_76346_g();
            if (var4 != null) {
               if (var4 instanceof EntityLivingBase) {
                  this.func_70604_c((EntityLivingBase)var4);
               }

               if (var4 instanceof EntityPlayer) {
                  this.field_70718_bc = 100;
                  this.field_70717_bb = (EntityPlayer)var4;
               } else if (var4 instanceof EntityWolf) {
                  EntityWolf var5 = (EntityWolf)var4;
                  if (var5.func_70909_n()) {
                     this.field_70718_bc = 100;
                     this.field_70717_bb = null;
                  }
               }
            }

            if (var3) {
               this.field_70170_p.func_72960_a(this, (byte)2);
               if (var1 != DamageSource.field_76369_e) {
                  this.func_70018_K();
               }

               if (var4 != null) {
                  double var9 = var4.field_70165_t - this.field_70165_t;

                  double var7;
                  for(var7 = var4.field_70161_v - this.field_70161_v; var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D) {
                     var9 = (Math.random() - Math.random()) * 0.01D;
                  }

                  this.field_70739_aP = (float)(MathHelper.func_181159_b(var7, var9) * 180.0D / 3.1415927410125732D - (double)this.field_70177_z);
                  this.func_70653_a(var4, var2, var9, var7);
               } else {
                  this.field_70739_aP = (float)((int)(Math.random() * 2.0D) * 180);
               }
            }

            String var10;
            if (this.func_110143_aJ() <= 0.0F) {
               var10 = this.func_70673_aS();
               if (var3 && var10 != null) {
                  this.func_85030_a(var10, this.func_70599_aP(), this.func_70647_i());
               }

               this.func_70645_a(var1);
            } else {
               var10 = this.func_70621_aR();
               if (var3 && var10 != null) {
                  this.func_85030_a(var10, this.func_70599_aP(), this.func_70647_i());
               }
            }

            return true;
         }
      }
   }

   public void func_70669_a(ItemStack var1) {
      this.func_85030_a("random.break", 0.8F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);

      for(int var2 = 0; var2 < 5; ++var2) {
         Vec3 var3 = new Vec3(((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
         var3 = var3.func_178789_a(-this.field_70125_A * 3.1415927F / 180.0F);
         var3 = var3.func_178785_b(-this.field_70177_z * 3.1415927F / 180.0F);
         double var4 = (double)(-this.field_70146_Z.nextFloat()) * 0.6D - 0.3D;
         Vec3 var6 = new Vec3(((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.3D, var4, 0.6D);
         var6 = var6.func_178789_a(-this.field_70125_A * 3.1415927F / 180.0F);
         var6 = var6.func_178785_b(-this.field_70177_z * 3.1415927F / 180.0F);
         var6 = var6.func_72441_c(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
         this.field_70170_p.func_175688_a(EnumParticleTypes.ITEM_CRACK, var6.field_72450_a, var6.field_72448_b, var6.field_72449_c, var3.field_72450_a, var3.field_72448_b + 0.05D, var3.field_72449_c, Item.func_150891_b(var1.func_77973_b()));
      }

   }

   public void func_70645_a(DamageSource var1) {
      Entity var2 = var1.func_76346_g();
      EntityLivingBase var3 = this.func_94060_bK();
      if (this.field_70744_aE >= 0 && var3 != null) {
         var3.func_70084_c(this, this.field_70744_aE);
      }

      if (var2 != null) {
         var2.func_70074_a(this);
      }

      this.field_70729_aU = true;
      this.func_110142_aN().func_94549_h();
      if (!this.field_70170_p.field_72995_K) {
         int var4 = 0;
         if (var2 instanceof EntityPlayer) {
            var4 = EnchantmentHelper.func_77519_f((EntityLivingBase)var2);
         }

         if (this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
            this.func_70628_a(this.field_70718_bc > 0, var4);
            this.func_82160_b(this.field_70718_bc > 0, var4);
            if (this.field_70718_bc > 0 && this.field_70146_Z.nextFloat() < 0.025F + (float)var4 * 0.01F) {
               this.func_82164_bB();
            }
         }
      }

      this.field_70170_p.func_72960_a(this, (byte)3);
   }

   protected void func_82160_b(boolean var1, int var2) {
   }

   public void func_70653_a(Entity var1, float var2, double var3, double var5) {
      if (this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e()) {
         this.field_70160_al = true;
         float var7 = MathHelper.func_76133_a(var3 * var3 + var5 * var5);
         float var8 = 0.4F;
         this.field_70159_w /= 2.0D;
         this.field_70181_x /= 2.0D;
         this.field_70179_y /= 2.0D;
         this.field_70159_w -= var3 / (double)var7 * (double)var8;
         this.field_70181_x += (double)var8;
         this.field_70179_y -= var5 / (double)var7 * (double)var8;
         if (this.field_70181_x > 0.4000000059604645D) {
            this.field_70181_x = 0.4000000059604645D;
         }

      }
   }

   protected String func_70621_aR() {
      return "game.neutral.hurt";
   }

   protected String func_70673_aS() {
      return "game.neutral.die";
   }

   protected void func_82164_bB() {
   }

   protected void func_70628_a(boolean var1, int var2) {
   }

   public boolean func_70617_f_() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      Block var4 = this.field_70170_p.func_180495_p(new BlockPos(var1, var2, var3)).func_177230_c();
      return (var4 == Blocks.field_150468_ap || var4 == Blocks.field_150395_bd) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).func_175149_v());
   }

   public boolean func_70089_S() {
      return !this.field_70128_L && this.func_110143_aJ() > 0.0F;
   }

   public void func_180430_e(float var1, float var2) {
      super.func_180430_e(var1, var2);
      PotionEffect var3 = this.func_70660_b(Potion.field_76430_j);
      float var4 = var3 != null ? (float)(var3.func_76458_c() + 1) : 0.0F;
      int var5 = MathHelper.func_76123_f((var1 - 3.0F - var4) * var2);
      if (var5 > 0) {
         this.func_85030_a(this.func_146067_o(var5), 1.0F, 1.0F);
         this.func_70097_a(DamageSource.field_76379_h, (float)var5);
         int var6 = MathHelper.func_76128_c(this.field_70165_t);
         int var7 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
         int var8 = MathHelper.func_76128_c(this.field_70161_v);
         Block var9 = this.field_70170_p.func_180495_p(new BlockPos(var6, var7, var8)).func_177230_c();
         if (var9.func_149688_o() != Material.field_151579_a) {
            Block.SoundType var10 = var9.field_149762_H;
            this.func_85030_a(var10.func_150498_e(), var10.func_150497_c() * 0.5F, var10.func_150494_d() * 0.75F);
         }
      }

   }

   protected String func_146067_o(int var1) {
      return var1 > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
   }

   public void func_70057_ab() {
      this.field_70737_aN = this.field_70738_aO = 10;
      this.field_70739_aP = 0.0F;
   }

   public int func_70658_aO() {
      int var1 = 0;
      ItemStack[] var2 = this.func_70035_c();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack var5 = var2[var4];
         if (var5 != null && var5.func_77973_b() instanceof ItemArmor) {
            int var6 = ((ItemArmor)var5.func_77973_b()).field_77879_b;
            var1 += var6;
         }
      }

      return var1;
   }

   protected void func_70675_k(float var1) {
   }

   protected float func_70655_b(DamageSource var1, float var2) {
      if (!var1.func_76363_c()) {
         int var3 = 25 - this.func_70658_aO();
         float var4 = var2 * (float)var3;
         this.func_70675_k(var2);
         var2 = var4 / 25.0F;
      }

      return var2;
   }

   protected float func_70672_c(DamageSource var1, float var2) {
      if (var1.func_151517_h()) {
         return var2;
      } else {
         int var3;
         int var4;
         float var5;
         if (this.func_70644_a(Potion.field_76429_m) && var1 != DamageSource.field_76380_i) {
            var3 = (this.func_70660_b(Potion.field_76429_m).func_76458_c() + 1) * 5;
            var4 = 25 - var3;
            var5 = var2 * (float)var4;
            var2 = var5 / 25.0F;
         }

         if (var2 <= 0.0F) {
            return 0.0F;
         } else {
            var3 = EnchantmentHelper.func_77508_a(this.func_70035_c(), var1);
            if (var3 > 20) {
               var3 = 20;
            }

            if (var3 > 0 && var3 <= 20) {
               var4 = 25 - var3;
               var5 = var2 * (float)var4;
               var2 = var5 / 25.0F;
            }

            return var2;
         }
      }
   }

   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_180431_b(var1)) {
         var2 = this.func_70655_b(var1, var2);
         var2 = this.func_70672_c(var1, var2);
         float var3 = var2;
         var2 = Math.max(var2 - this.func_110139_bj(), 0.0F);
         this.func_110149_m(this.func_110139_bj() - (var3 - var2));
         if (var2 != 0.0F) {
            float var4 = this.func_110143_aJ();
            this.func_70606_j(var4 - var2);
            this.func_110142_aN().func_94547_a(var1, var4, var2);
            this.func_110149_m(this.func_110139_bj() - var2);
         }
      }
   }

   public CombatTracker func_110142_aN() {
      return this.field_94063_bt;
   }

   public EntityLivingBase func_94060_bK() {
      if (this.field_94063_bt.func_94550_c() != null) {
         return this.field_94063_bt.func_94550_c();
      } else if (this.field_70717_bb != null) {
         return this.field_70717_bb;
      } else {
         return this.field_70755_b != null ? this.field_70755_b : null;
      }
   }

   public final float func_110138_aP() {
      return (float)this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111126_e();
   }

   public final int func_85035_bI() {
      return this.field_70180_af.func_75683_a(9);
   }

   public final void func_85034_r(int var1) {
      this.field_70180_af.func_75692_b(9, (byte)var1);
   }

   private int func_82166_i() {
      if (this.func_70644_a(Potion.field_76422_e)) {
         return 6 - (1 + this.func_70660_b(Potion.field_76422_e).func_76458_c()) * 1;
      } else {
         return this.func_70644_a(Potion.field_76419_f) ? 6 + (1 + this.func_70660_b(Potion.field_76419_f).func_76458_c()) * 2 : 6;
      }
   }

   public void func_71038_i() {
      if (!this.field_82175_bq || this.field_110158_av >= this.func_82166_i() / 2 || this.field_110158_av < 0) {
         this.field_110158_av = -1;
         this.field_82175_bq = true;
         if (this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new S0BPacketAnimation(this, 0));
         }
      }

   }

   public void func_70103_a(byte var1) {
      String var2;
      if (var1 == 2) {
         this.field_70721_aZ = 1.5F;
         this.field_70172_ad = this.field_70771_an;
         this.field_70737_aN = this.field_70738_aO = 10;
         this.field_70739_aP = 0.0F;
         var2 = this.func_70621_aR();
         if (var2 != null) {
            this.func_85030_a(this.func_70621_aR(), this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

         this.func_70097_a(DamageSource.field_76377_j, 0.0F);
      } else if (var1 == 3) {
         var2 = this.func_70673_aS();
         if (var2 != null) {
            this.func_85030_a(this.func_70673_aS(), this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

         this.func_70606_j(0.0F);
         this.func_70645_a(DamageSource.field_76377_j);
      } else {
         super.func_70103_a(var1);
      }

   }

   protected void func_70076_C() {
      this.func_70097_a(DamageSource.field_76380_i, 4.0F);
   }

   protected void func_82168_bl() {
      int var1 = this.func_82166_i();
      if (this.field_82175_bq) {
         ++this.field_110158_av;
         if (this.field_110158_av >= var1) {
            this.field_110158_av = 0;
            this.field_82175_bq = false;
         }
      } else {
         this.field_110158_av = 0;
      }

      this.field_70733_aJ = (float)this.field_110158_av / (float)var1;
   }

   public IAttributeInstance func_110148_a(IAttribute var1) {
      return this.func_110140_aT().func_111151_a(var1);
   }

   public BaseAttributeMap func_110140_aT() {
      if (this.field_110155_d == null) {
         this.field_110155_d = new ServersideAttributeMap();
      }

      return this.field_110155_d;
   }

   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.UNDEFINED;
   }

   public abstract ItemStack func_70694_bm();

   public abstract ItemStack func_71124_b(int var1);

   public abstract ItemStack func_82169_q(int var1);

   public abstract void func_70062_b(int var1, ItemStack var2);

   public void func_70031_b(boolean var1) {
      super.func_70031_b(var1);
      IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      if (var2.func_111127_a(field_110156_b) != null) {
         var2.func_111124_b(field_110157_c);
      }

      if (var1) {
         var2.func_111121_a(field_110157_c);
      }

   }

   public abstract ItemStack[] func_70035_c();

   protected float func_70599_aP() {
      return 1.0F;
   }

   protected float func_70647_i() {
      return this.func_70631_g_() ? (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.5F : (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean func_70610_aX() {
      return this.func_110143_aJ() <= 0.0F;
   }

   public void func_110145_l(Entity var1) {
      double var3 = var1.field_70165_t;
      double var5 = var1.func_174813_aQ().field_72338_b + (double)var1.field_70131_O;
      double var7 = var1.field_70161_v;
      byte var9 = 1;

      for(int var10 = -var9; var10 <= var9; ++var10) {
         for(int var11 = -var9; var11 < var9; ++var11) {
            if (var10 != 0 || var11 != 0) {
               int var12 = (int)(this.field_70165_t + (double)var10);
               int var13 = (int)(this.field_70161_v + (double)var11);
               AxisAlignedBB var2 = this.func_174813_aQ().func_72317_d((double)var10, 1.0D, (double)var11);
               if (this.field_70170_p.func_147461_a(var2).isEmpty()) {
                  if (World.func_175683_a(this.field_70170_p, new BlockPos(var12, (int)this.field_70163_u, var13))) {
                     this.func_70634_a(this.field_70165_t + (double)var10, this.field_70163_u + 1.0D, this.field_70161_v + (double)var11);
                     return;
                  }

                  if (World.func_175683_a(this.field_70170_p, new BlockPos(var12, (int)this.field_70163_u - 1, var13)) || this.field_70170_p.func_180495_p(new BlockPos(var12, (int)this.field_70163_u - 1, var13)).func_177230_c().func_149688_o() == Material.field_151586_h) {
                     var3 = this.field_70165_t + (double)var10;
                     var5 = this.field_70163_u + 1.0D;
                     var7 = this.field_70161_v + (double)var11;
                  }
               }
            }
         }
      }

      this.func_70634_a(var3, var5, var7);
   }

   public boolean func_94059_bO() {
      return false;
   }

   protected float func_175134_bD() {
      return 0.42F;
   }

   protected void func_70664_aZ() {
      this.field_70181_x = (double)this.func_175134_bD();
      if (this.func_70644_a(Potion.field_76430_j)) {
         this.field_70181_x += (double)((float)(this.func_70660_b(Potion.field_76430_j).func_76458_c() + 1) * 0.1F);
      }

      if (this.func_70051_ag()) {
         float var1 = this.field_70177_z * 0.017453292F;
         this.field_70159_w -= (double)(MathHelper.func_76126_a(var1) * 0.2F);
         this.field_70179_y += (double)(MathHelper.func_76134_b(var1) * 0.2F);
      }

      this.field_70160_al = true;
   }

   protected void func_70629_bd() {
      this.field_70181_x += 0.03999999910593033D;
   }

   protected void func_180466_bG() {
      this.field_70181_x += 0.03999999910593033D;
   }

   public void func_70612_e(float var1, float var2) {
      double var8;
      float var10;
      if (this.func_70613_aW()) {
         float var5;
         float var6;
         if (this.func_70090_H() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).field_71075_bZ.field_75100_b)) {
            var8 = this.field_70163_u;
            var5 = 0.8F;
            var6 = 0.02F;
            var10 = (float)EnchantmentHelper.func_180318_b(this);
            if (var10 > 3.0F) {
               var10 = 3.0F;
            }

            if (!this.field_70122_E) {
               var10 *= 0.5F;
            }

            if (var10 > 0.0F) {
               var5 += (0.54600006F - var5) * var10 / 3.0F;
               var6 += (this.func_70689_ay() * 1.0F - var6) * var10 / 3.0F;
            }

            this.func_70060_a(var1, var2, var6);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= (double)var5;
            this.field_70181_x *= 0.800000011920929D;
            this.field_70179_y *= (double)var5;
            this.field_70181_x -= 0.02D;
            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + var8, this.field_70179_y)) {
               this.field_70181_x = 0.30000001192092896D;
            }
         } else if (this.func_180799_ab() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).field_71075_bZ.field_75100_b)) {
            var8 = this.field_70163_u;
            this.func_70060_a(var1, var2, 0.02F);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= 0.5D;
            this.field_70181_x *= 0.5D;
            this.field_70179_y *= 0.5D;
            this.field_70181_x -= 0.02D;
            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + var8, this.field_70179_y)) {
               this.field_70181_x = 0.30000001192092896D;
            }
         } else {
            float var3 = 0.91F;
            if (this.field_70122_E) {
               var3 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().field_149765_K * 0.91F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);
            if (this.field_70122_E) {
               var5 = this.func_70689_ay() * var4;
            } else {
               var5 = this.field_70747_aH;
            }

            this.func_70060_a(var1, var2, var5);
            var3 = 0.91F;
            if (this.field_70122_E) {
               var3 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().field_149765_K * 0.91F;
            }

            if (this.func_70617_f_()) {
               var6 = 0.15F;
               this.field_70159_w = MathHelper.func_151237_a(this.field_70159_w, (double)(-var6), (double)var6);
               this.field_70179_y = MathHelper.func_151237_a(this.field_70179_y, (double)(-var6), (double)var6);
               this.field_70143_R = 0.0F;
               if (this.field_70181_x < -0.15D) {
                  this.field_70181_x = -0.15D;
               }

               boolean var7 = this.func_70093_af() && this instanceof EntityPlayer;
               if (var7 && this.field_70181_x < 0.0D) {
                  this.field_70181_x = 0.0D;
               }
            }

            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            if (this.field_70123_F && this.func_70617_f_()) {
               this.field_70181_x = 0.2D;
            }

            if (!this.field_70170_p.field_72995_K || this.field_70170_p.func_175667_e(new BlockPos((int)this.field_70165_t, 0, (int)this.field_70161_v)) && this.field_70170_p.func_175726_f(new BlockPos((int)this.field_70165_t, 0, (int)this.field_70161_v)).func_177410_o()) {
               this.field_70181_x -= 0.08D;
            } else if (this.field_70163_u > 0.0D) {
               this.field_70181_x = -0.1D;
            } else {
               this.field_70181_x = 0.0D;
            }

            this.field_70181_x *= 0.9800000190734863D;
            this.field_70159_w *= (double)var3;
            this.field_70179_y *= (double)var3;
         }
      }

      this.field_70722_aY = this.field_70721_aZ;
      var8 = this.field_70165_t - this.field_70169_q;
      double var9 = this.field_70161_v - this.field_70166_s;
      var10 = MathHelper.func_76133_a(var8 * var8 + var9 * var9) * 4.0F;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      this.field_70721_aZ += (var10 - this.field_70721_aZ) * 0.4F;
      this.field_70754_ba += this.field_70721_aZ;
   }

   public float func_70689_ay() {
      return this.field_70746_aG;
   }

   public void func_70659_e(float var1) {
      this.field_70746_aG = var1;
   }

   public boolean func_70652_k(Entity var1) {
      this.func_130011_c(var1);
      return false;
   }

   public boolean func_70608_bn() {
      return false;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         int var1 = this.func_85035_bI();
         if (var1 > 0) {
            if (this.field_70720_be <= 0) {
               this.field_70720_be = 20 * (30 - var1);
            }

            --this.field_70720_be;
            if (this.field_70720_be <= 0) {
               this.func_85034_r(var1 - 1);
            }
         }

         for(int var2 = 0; var2 < 5; ++var2) {
            ItemStack var3 = this.field_82180_bT[var2];
            ItemStack var4 = this.func_71124_b(var2);
            if (!ItemStack.func_77989_b(var4, var3)) {
               ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new S04PacketEntityEquipment(this.func_145782_y(), var2, var4));
               if (var3 != null) {
                  this.field_110155_d.func_111148_a(var3.func_111283_C());
               }

               if (var4 != null) {
                  this.field_110155_d.func_111147_b(var4.func_111283_C());
               }

               this.field_82180_bT[var2] = var4 == null ? null : var4.func_77946_l();
            }
         }

         if (this.field_70173_aa % 20 == 0) {
            this.func_110142_aN().func_94549_h();
         }
      }

      this.func_70636_d();
      double var9 = this.field_70165_t - this.field_70169_q;
      double var10 = this.field_70161_v - this.field_70166_s;
      float var5 = (float)(var9 * var9 + var10 * var10);
      float var6 = this.field_70761_aq;
      float var7 = 0.0F;
      this.field_70768_au = this.field_110154_aX;
      float var8 = 0.0F;
      if (var5 > 0.0025000002F) {
         var8 = 1.0F;
         var7 = (float)Math.sqrt((double)var5) * 3.0F;
         var6 = (float)MathHelper.func_181159_b(var10, var9) * 180.0F / 3.1415927F - 90.0F;
      }

      if (this.field_70733_aJ > 0.0F) {
         var6 = this.field_70177_z;
      }

      if (!this.field_70122_E) {
         var8 = 0.0F;
      }

      this.field_110154_aX += (var8 - this.field_110154_aX) * 0.3F;
      this.field_70170_p.field_72984_F.func_76320_a("headTurn");
      var7 = this.func_110146_f(var6, var7);
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("rangeChecks");

      while(this.field_70177_z - this.field_70126_B < -180.0F) {
         this.field_70126_B -= 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B >= 180.0F) {
         this.field_70126_B += 360.0F;
      }

      while(this.field_70761_aq - this.field_70760_ar < -180.0F) {
         this.field_70760_ar -= 360.0F;
      }

      while(this.field_70761_aq - this.field_70760_ar >= 180.0F) {
         this.field_70760_ar += 360.0F;
      }

      while(this.field_70125_A - this.field_70127_C < -180.0F) {
         this.field_70127_C -= 360.0F;
      }

      while(this.field_70125_A - this.field_70127_C >= 180.0F) {
         this.field_70127_C += 360.0F;
      }

      while(this.field_70759_as - this.field_70758_at < -180.0F) {
         this.field_70758_at -= 360.0F;
      }

      while(this.field_70759_as - this.field_70758_at >= 180.0F) {
         this.field_70758_at += 360.0F;
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70764_aw += var7;
   }

   protected float func_110146_f(float var1, float var2) {
      float var3 = MathHelper.func_76142_g(var1 - this.field_70761_aq);
      this.field_70761_aq += var3 * 0.3F;
      float var4 = MathHelper.func_76142_g(this.field_70177_z - this.field_70761_aq);
      boolean var5 = var4 < -90.0F || var4 >= 90.0F;
      if (var4 < -75.0F) {
         var4 = -75.0F;
      }

      if (var4 >= 75.0F) {
         var4 = 75.0F;
      }

      this.field_70761_aq = this.field_70177_z - var4;
      if (var4 * var4 > 2500.0F) {
         this.field_70761_aq += var4 * 0.2F;
      }

      if (var5) {
         var2 *= -1.0F;
      }

      return var2;
   }

   public void func_70636_d() {
      if (this.field_70773_bE > 0) {
         --this.field_70773_bE;
      }

      if (this.field_70716_bi > 0) {
         double var1 = this.field_70165_t + (this.field_70709_bj - this.field_70165_t) / (double)this.field_70716_bi;
         double var3 = this.field_70163_u + (this.field_70710_bk - this.field_70163_u) / (double)this.field_70716_bi;
         double var5 = this.field_70161_v + (this.field_110152_bk - this.field_70161_v) / (double)this.field_70716_bi;
         double var7 = MathHelper.func_76138_g(this.field_70712_bm - (double)this.field_70177_z);
         this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_70716_bi);
         this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70705_bn - (double)this.field_70125_A) / (double)this.field_70716_bi);
         --this.field_70716_bi;
         this.func_70107_b(var1, var3, var5);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
      } else if (!this.func_70613_aW()) {
         this.field_70159_w *= 0.98D;
         this.field_70181_x *= 0.98D;
         this.field_70179_y *= 0.98D;
      }

      if (Math.abs(this.field_70159_w) < 0.005D) {
         this.field_70159_w = 0.0D;
      }

      if (Math.abs(this.field_70181_x) < 0.005D) {
         this.field_70181_x = 0.0D;
      }

      if (Math.abs(this.field_70179_y) < 0.005D) {
         this.field_70179_y = 0.0D;
      }

      this.field_70170_p.field_72984_F.func_76320_a("ai");
      if (this.func_70610_aX()) {
         this.field_70703_bu = false;
         this.field_70702_br = 0.0F;
         this.field_70701_bs = 0.0F;
         this.field_70704_bt = 0.0F;
      } else if (this.func_70613_aW()) {
         this.field_70170_p.field_72984_F.func_76320_a("newAi");
         this.func_70626_be();
         this.field_70170_p.field_72984_F.func_76319_b();
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("jump");
      if (this.field_70703_bu) {
         if (this.func_70090_H()) {
            this.func_70629_bd();
         } else if (this.func_180799_ab()) {
            this.func_180466_bG();
         } else if (this.field_70122_E && this.field_70773_bE == 0) {
            this.func_70664_aZ();
            this.field_70773_bE = 10;
         }
      } else {
         this.field_70773_bE = 0;
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("travel");
      this.field_70702_br *= 0.98F;
      this.field_70701_bs *= 0.98F;
      this.field_70704_bt *= 0.9F;
      this.func_70612_e(this.field_70702_br, this.field_70701_bs);
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("push");
      if (!this.field_70170_p.field_72995_K) {
         this.func_85033_bc();
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_70626_be() {
   }

   protected void func_85033_bc() {
      List var1 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ().func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D), Predicates.and(EntitySelectors.field_180132_d, new Predicate<Entity>() {
         public boolean apply(Entity var1) {
            return var1.func_70104_M();
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Entity)var1);
         }
      }));
      if (!var1.isEmpty()) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Entity var3 = (Entity)var1.get(var2);
            this.func_82167_n(var3);
         }
      }

   }

   protected void func_82167_n(Entity var1) {
      var1.func_70108_f(this);
   }

   public void func_70078_a(Entity var1) {
      if (this.field_70154_o != null && var1 == null) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_110145_l(this.field_70154_o);
         }

         if (this.field_70154_o != null) {
            this.field_70154_o.field_70153_n = null;
         }

         this.field_70154_o = null;
      } else {
         super.func_70078_a(var1);
      }
   }

   public void func_70098_U() {
      super.func_70098_U();
      this.field_70768_au = this.field_110154_aX;
      this.field_110154_aX = 0.0F;
      this.field_70143_R = 0.0F;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_70709_bj = var1;
      this.field_70710_bk = var3;
      this.field_110152_bk = var5;
      this.field_70712_bm = (double)var7;
      this.field_70705_bn = (double)var8;
      this.field_70716_bi = var9;
   }

   public void func_70637_d(boolean var1) {
      this.field_70703_bu = var1;
   }

   public void func_71001_a(Entity var1, int var2) {
      if (!var1.field_70128_L && !this.field_70170_p.field_72995_K) {
         EntityTracker var3 = ((WorldServer)this.field_70170_p).func_73039_n();
         if (var1 instanceof EntityItem) {
            var3.func_151247_a(var1, new S0DPacketCollectItem(var1.func_145782_y(), this.func_145782_y()));
         }

         if (var1 instanceof EntityArrow) {
            var3.func_151247_a(var1, new S0DPacketCollectItem(var1.func_145782_y(), this.func_145782_y()));
         }

         if (var1 instanceof EntityXPOrb) {
            var3.func_151247_a(var1, new S0DPacketCollectItem(var1.func_145782_y(), this.func_145782_y()));
         }
      }

   }

   public boolean func_70685_l(Entity var1) {
      return this.field_70170_p.func_72933_a(new Vec3(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v), new Vec3(var1.field_70165_t, var1.field_70163_u + (double)var1.func_70047_e(), var1.field_70161_v)) == null;
   }

   public Vec3 func_70040_Z() {
      return this.func_70676_i(1.0F);
   }

   public Vec3 func_70676_i(float var1) {
      if (var1 == 1.0F) {
         return this.func_174806_f(this.field_70125_A, this.field_70759_as);
      } else {
         float var2 = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * var1;
         float var3 = this.field_70758_at + (this.field_70759_as - this.field_70758_at) * var1;
         return this.func_174806_f(var2, var3);
      }
   }

   public float func_70678_g(float var1) {
      float var2 = this.field_70733_aJ - this.field_70732_aI;
      if (var2 < 0.0F) {
         ++var2;
      }

      return this.field_70732_aI + var2 * var1;
   }

   public boolean func_70613_aW() {
      return !this.field_70170_p.field_72995_K;
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public boolean func_70104_M() {
      return !this.field_70128_L;
   }

   protected void func_70018_K() {
      this.field_70133_I = this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e();
   }

   public float func_70079_am() {
      return this.field_70759_as;
   }

   public void func_70034_d(float var1) {
      this.field_70759_as = var1;
   }

   public void func_181013_g(float var1) {
      this.field_70761_aq = var1;
   }

   public float func_110139_bj() {
      return this.field_110151_bq;
   }

   public void func_110149_m(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.field_110151_bq = var1;
   }

   public Team func_96124_cp() {
      return this.field_70170_p.func_96441_U().func_96509_i(this.func_110124_au().toString());
   }

   public boolean func_142014_c(EntityLivingBase var1) {
      return this.func_142012_a(var1.func_96124_cp());
   }

   public boolean func_142012_a(Team var1) {
      return this.func_96124_cp() != null ? this.func_96124_cp().func_142054_a(var1) : false;
   }

   public void func_152111_bt() {
   }

   public void func_152112_bu() {
   }

   protected void func_175136_bO() {
      this.field_70752_e = true;
   }

   static {
      field_110157_c = (new AttributeModifier(field_110156_b, "Sprinting speed boost", 0.30000001192092896D, 2)).func_111168_a(false);
   }
}
