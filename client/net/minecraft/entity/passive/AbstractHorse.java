package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class AbstractHorse extends EntityAnimal implements IInventoryChangedListener, IJumpingMount {
   private static final Predicate<Entity> field_110276_bu = (var0) -> {
      return var0 instanceof AbstractHorse && ((AbstractHorse)var0).func_110205_ce();
   };
   protected static final IAttribute field_110271_bv = (new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).func_111117_a("Jump Strength").func_111112_a(true);
   private static final DataParameter<Byte> field_184787_bE;
   private static final DataParameter<Optional<UUID>> field_184790_bH;
   private int field_190689_bJ;
   private int field_110290_bE;
   private int field_110295_bF;
   public int field_110278_bp;
   public int field_110279_bq;
   protected boolean field_110275_br;
   protected ContainerHorseChest field_110296_bG;
   protected int field_110274_bs;
   protected float field_110277_bt;
   private boolean field_110294_bI;
   private float field_110283_bJ;
   private float field_110284_bK;
   private float field_110281_bL;
   private float field_110282_bM;
   private float field_110287_bN;
   private float field_110288_bO;
   protected boolean field_190688_bE = true;
   protected int field_110285_bP;

   protected AbstractHorse(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.func_70105_a(1.3964844F, 1.6F);
      this.field_70138_W = 1.0F;
      this.func_110226_cD();
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.2D));
      this.field_70714_bg.func_75776_a(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
      this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 1.0D, AbstractHorse.class));
      this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWanderAvoidWater(this, 0.7D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.func_205714_dM();
   }

   protected void func_205714_dM() {
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184787_bE, (byte)0);
      this.field_70180_af.func_187214_a(field_184790_bH, Optional.empty());
   }

   protected boolean func_110233_w(int var1) {
      return ((Byte)this.field_70180_af.func_187225_a(field_184787_bE) & var1) != 0;
   }

   protected void func_110208_b(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_184787_bE);
      if (var2) {
         this.field_70180_af.func_187227_b(field_184787_bE, (byte)(var3 | var1));
      } else {
         this.field_70180_af.func_187227_b(field_184787_bE, (byte)(var3 & ~var1));
      }

   }

   public boolean func_110248_bS() {
      return this.func_110233_w(2);
   }

   @Nullable
   public UUID func_184780_dh() {
      return (UUID)((Optional)this.field_70180_af.func_187225_a(field_184790_bH)).orElse((Object)null);
   }

   public void func_184779_b(@Nullable UUID var1) {
      this.field_70180_af.func_187227_b(field_184790_bH, Optional.ofNullable(var1));
   }

   public float func_110254_bY() {
      return 0.5F;
   }

   public void func_98054_a(boolean var1) {
      this.func_98055_j(var1 ? this.func_110254_bY() : 1.0F);
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

   public boolean func_184652_a(EntityPlayer var1) {
      return super.func_184652_a(var1) && this.func_70668_bt() != CreatureAttribute.UNDEAD;
   }

   protected void func_142017_o(float var1) {
      if (var1 > 6.0F && this.func_110204_cc()) {
         this.func_110227_p(false);
      }

   }

   public boolean func_110204_cc() {
      return this.func_110233_w(16);
   }

   public boolean func_110209_cd() {
      return this.func_110233_w(32);
   }

   public boolean func_110205_ce() {
      return this.func_110233_w(8);
   }

   public void func_110242_l(boolean var1) {
      this.func_110208_b(8, var1);
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
      int var2 = MathHelper.func_76125_a(this.func_110252_cg() + var1, 0, this.func_190676_dC());
      this.func_110238_s(var2);
      return var2;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      Entity var3 = var1.func_76346_g();
      return this.func_184207_aI() && var3 != null && this.func_184215_y(var3) ? false : super.func_70097_a(var1, var2);
   }

   public boolean func_70104_M() {
      return !this.func_184207_aI();
   }

   private void func_110266_cB() {
      this.func_110249_cI();
      if (!this.func_174814_R()) {
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187711_cp, this.func_184176_by(), 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
      }

   }

   public void func_180430_e(float var1, float var2) {
      if (var1 > 1.0F) {
         this.func_184185_a(SoundEvents.field_187723_ct, 0.4F, 1.0F);
      }

      int var3 = MathHelper.func_76123_f((var1 * 0.5F - 3.0F) * var2);
      if (var3 > 0) {
         this.func_70097_a(DamageSource.field_76379_h, (float)var3);
         if (this.func_184207_aI()) {
            Iterator var4 = this.func_184182_bu().iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               var5.func_70097_a(DamageSource.field_76379_h, (float)var3);
            }
         }

         IBlockState var7 = this.field_70170_p.func_180495_p(new BlockPos(this.field_70165_t, this.field_70163_u - 0.2D - (double)this.field_70126_B, this.field_70161_v));
         Block var8 = var7.func_177230_c();
         if (!var7.func_196958_f() && !this.func_174814_R()) {
            SoundType var6 = var8.func_185467_w();
            this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, var6.func_185844_d(), this.func_184176_by(), var6.func_185843_a() * 0.5F, var6.func_185847_b() * 0.75F);
         }

      }
   }

   protected int func_190686_di() {
      return 2;
   }

   protected void func_110226_cD() {
      ContainerHorseChest var1 = this.field_110296_bG;
      this.field_110296_bG = new ContainerHorseChest(this.func_200200_C_(), this.func_190686_di());
      this.field_110296_bG.func_200228_a(this.func_200201_e());
      if (var1 != null) {
         var1.func_110132_b(this);
         int var2 = Math.min(var1.func_70302_i_(), this.field_110296_bG.func_70302_i_());

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1.func_70301_a(var3);
            if (!var4.func_190926_b()) {
               this.field_110296_bG.func_70299_a(var3, var4.func_77946_l());
            }
         }
      }

      this.field_110296_bG.func_110134_a(this);
      this.func_110232_cE();
   }

   protected void func_110232_cE() {
      if (!this.field_70170_p.field_72995_K) {
         this.func_110251_o(!this.field_110296_bG.func_70301_a(0).func_190926_b() && this.func_190685_dA());
      }
   }

   public void func_76316_a(IInventory var1) {
      boolean var2 = this.func_110257_ck();
      this.func_110232_cE();
      if (this.field_70173_aa > 20 && !var2 && this.func_110257_ck()) {
         this.func_184185_a(SoundEvents.field_187726_cu, 0.5F, 1.0F);
      }

   }

   @Nullable
   protected AbstractHorse func_110250_a(Entity var1, double var2) {
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

      return (AbstractHorse)var6;
   }

   public double func_110215_cj() {
      return this.func_110148_a(field_110271_bv).func_111126_e();
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return null;
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      if (this.field_70146_Z.nextInt(3) == 0) {
         this.func_110220_cK();
      }

      return null;
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      if (this.field_70146_Z.nextInt(10) == 0 && !this.func_70610_aX()) {
         this.func_110220_cK();
      }

      return null;
   }

   public boolean func_190685_dA() {
      return true;
   }

   public boolean func_110257_ck() {
      return this.func_110233_w(4);
   }

   @Nullable
   protected SoundEvent func_184785_dv() {
      this.func_110220_cK();
      return null;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      if (!var2.func_185904_a().func_76224_d()) {
         SoundType var3 = var2.func_177230_c().func_185467_w();
         if (this.field_70170_p.func_180495_p(var1.func_177984_a()).func_177230_c() == Blocks.field_150433_aE) {
            var3 = Blocks.field_150433_aE.func_185467_w();
         }

         if (this.func_184207_aI() && this.field_190688_bE) {
            ++this.field_110285_bP;
            if (this.field_110285_bP > 5 && this.field_110285_bP % 3 == 0) {
               this.func_190680_a(var3);
            } else if (this.field_110285_bP <= 5) {
               this.func_184185_a(SoundEvents.field_187732_cw, var3.func_185843_a() * 0.15F, var3.func_185847_b());
            }
         } else if (var3 == SoundType.field_185848_a) {
            this.func_184185_a(SoundEvents.field_187732_cw, var3.func_185843_a() * 0.15F, var3.func_185847_b());
         } else {
            this.func_184185_a(SoundEvents.field_187729_cv, var3.func_185843_a() * 0.15F, var3.func_185847_b());
         }

      }
   }

   protected void func_190680_a(SoundType var1) {
      this.func_184185_a(SoundEvents.field_187714_cq, var1.func_185843_a() * 0.15F, var1.func_185847_b());
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

   public int func_190676_dC() {
      return 100;
   }

   protected float func_70599_aP() {
      return 0.8F;
   }

   public int func_70627_aG() {
      return 400;
   }

   public void func_110199_f(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K && (!this.func_184207_aI() || this.func_184196_w(var1)) && this.func_110248_bS()) {
         this.field_110296_bG.func_200228_a(this.func_200201_e());
         var1.func_184826_a(this, this.field_110296_bG);
      }

   }

   protected boolean func_190678_b(EntityPlayer var1, ItemStack var2) {
      boolean var3 = false;
      float var4 = 0.0F;
      short var5 = 0;
      byte var6 = 0;
      Item var7 = var2.func_77973_b();
      if (var7 == Items.field_151015_O) {
         var4 = 2.0F;
         var5 = 20;
         var6 = 3;
      } else if (var7 == Items.field_151102_aT) {
         var4 = 1.0F;
         var5 = 30;
         var6 = 3;
      } else if (var7 == Blocks.field_150407_cf.func_199767_j()) {
         var4 = 20.0F;
         var5 = 180;
      } else if (var7 == Items.field_151034_e) {
         var4 = 3.0F;
         var5 = 60;
         var6 = 3;
      } else if (var7 == Items.field_151150_bK) {
         var4 = 4.0F;
         var5 = 60;
         var6 = 5;
         if (this.func_110248_bS() && this.func_70874_b() == 0 && !this.func_70880_s()) {
            var3 = true;
            this.func_146082_f(var1);
         }
      } else if (var7 == Items.field_151153_ao || var7 == Items.field_196100_at) {
         var4 = 10.0F;
         var5 = 240;
         var6 = 10;
         if (this.func_110248_bS() && this.func_70874_b() == 0 && !this.func_70880_s()) {
            var3 = true;
            this.func_146082_f(var1);
         }
      }

      if (this.func_110143_aJ() < this.func_110138_aP() && var4 > 0.0F) {
         this.func_70691_i(var4);
         var3 = true;
      }

      if (this.func_70631_g_() && var5 > 0) {
         this.field_70170_p.func_195594_a(Particles.field_197632_y, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, 0.0D, 0.0D, 0.0D);
         if (!this.field_70170_p.field_72995_K) {
            this.func_110195_a(var5);
         }

         var3 = true;
      }

      if (var6 > 0 && (var3 || !this.func_110248_bS()) && this.func_110252_cg() < this.func_190676_dC()) {
         var3 = true;
         if (!this.field_70170_p.field_72995_K) {
            this.func_110198_t(var6);
         }
      }

      if (var3) {
         this.func_110266_cB();
      }

      return var3;
   }

   protected void func_110237_h(EntityPlayer var1) {
      this.func_110227_p(false);
      this.func_110219_q(false);
      if (!this.field_70170_p.field_72995_K) {
         var1.field_70177_z = this.field_70177_z;
         var1.field_70125_A = this.field_70125_A;
         var1.func_184220_m(this);
      }

   }

   protected boolean func_70610_aX() {
      return super.func_70610_aX() && this.func_184207_aI() && this.func_110257_ck() || this.func_110204_cc() || this.func_110209_cd();
   }

   public boolean func_70877_b(ItemStack var1) {
      return false;
   }

   private void func_110210_cH() {
      this.field_110278_bp = 1;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (!this.field_70170_p.field_72995_K && this.field_110296_bG != null) {
         for(int var2 = 0; var2 < this.field_110296_bG.func_70302_i_(); ++var2) {
            ItemStack var3 = this.field_110296_bG.func_70301_a(var2);
            if (!var3.func_190926_b()) {
               this.func_199701_a_(var3);
            }
         }

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

         if (this.func_190684_dE()) {
            if (!this.func_110204_cc() && !this.func_184207_aI() && this.field_70146_Z.nextInt(300) == 0 && this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c() == Blocks.field_196658_i) {
               this.func_110227_p(true);
            }

            if (this.func_110204_cc() && ++this.field_190689_bJ > 50) {
               this.field_190689_bJ = 0;
               this.func_110227_p(false);
            }
         }

         this.func_190679_dD();
      }
   }

   protected void func_190679_dD() {
      if (this.func_110205_ce() && this.func_70631_g_() && !this.func_110204_cc()) {
         AbstractHorse var1 = this.func_110250_a(this, 16.0D);
         if (var1 != null && this.func_70068_e(var1) > 4.0D) {
            this.field_70699_by.func_75494_a(var1);
         }
      }

   }

   public boolean func_190684_dE() {
      return true;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_110290_bE > 0 && ++this.field_110290_bE > 30) {
         this.field_110290_bE = 0;
         this.func_110208_b(64, false);
      }

      if ((this.func_184186_bw() || this.func_70613_aW()) && this.field_110295_bF > 0 && ++this.field_110295_bF > 20) {
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
         this.field_110283_bJ = 0.0F;
         this.field_110284_bK = this.field_110283_bJ;
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
      if (this.func_110233_w(64)) {
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
         this.func_110208_b(64, true);
      }

   }

   public void func_110227_p(boolean var1) {
      this.func_110208_b(16, var1);
   }

   public void func_110219_q(boolean var1) {
      if (var1) {
         this.func_110227_p(false);
      }

      this.func_110208_b(32, var1);
   }

   private void func_110220_cK() {
      if (this.func_184186_bw() || this.func_70613_aW()) {
         this.field_110295_bF = 1;
         this.func_110219_q(true);
      }

   }

   public void func_190687_dF() {
      this.func_110220_cK();
      SoundEvent var1 = this.func_184785_dv();
      if (var1 != null) {
         this.func_184185_a(var1, this.func_70599_aP(), this.func_70647_i());
      }

   }

   public boolean func_110263_g(EntityPlayer var1) {
      this.func_184779_b(var1.func_110124_au());
      this.func_110234_j(true);
      if (var1 instanceof EntityPlayerMP) {
         CriteriaTriggers.field_193136_w.func_193178_a((EntityPlayerMP)var1, this);
      }

      this.field_70170_p.func_72960_a(this, (byte)7);
      return true;
   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (this.func_184207_aI() && this.func_82171_bF() && this.func_110257_ck()) {
         EntityLivingBase var4 = (EntityLivingBase)this.func_184179_bs();
         this.field_70177_z = var4.field_70177_z;
         this.field_70126_B = this.field_70177_z;
         this.field_70125_A = var4.field_70125_A * 0.5F;
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         this.field_70761_aq = this.field_70177_z;
         this.field_70759_as = this.field_70761_aq;
         var1 = var4.field_70702_br * 0.5F;
         var3 = var4.field_191988_bg;
         if (var3 <= 0.0F) {
            var3 *= 0.25F;
            this.field_110285_bP = 0;
         }

         if (this.field_70122_E && this.field_110277_bt == 0.0F && this.func_110209_cd() && !this.field_110294_bI) {
            var1 = 0.0F;
            var3 = 0.0F;
         }

         if (this.field_110277_bt > 0.0F && !this.func_110246_bZ() && this.field_70122_E) {
            this.field_70181_x = this.func_110215_cj() * (double)this.field_110277_bt;
            if (this.func_70644_a(MobEffects.field_76430_j)) {
               this.field_70181_x += (double)((float)(this.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F);
            }

            this.func_110255_k(true);
            this.field_70160_al = true;
            if (var3 > 0.0F) {
               float var5 = MathHelper.func_76126_a(this.field_70177_z * 0.017453292F);
               float var6 = MathHelper.func_76134_b(this.field_70177_z * 0.017453292F);
               this.field_70159_w += (double)(-0.4F * var5 * this.field_110277_bt);
               this.field_70179_y += (double)(0.4F * var6 * this.field_110277_bt);
               this.func_205715_ee();
            }

            this.field_110277_bt = 0.0F;
         }

         this.field_70747_aH = this.func_70689_ay() * 0.1F;
         if (this.func_184186_bw()) {
            this.func_70659_e((float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            super.func_191986_a(var1, var2, var3);
         } else if (var4 instanceof EntityPlayer) {
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
         }

         if (this.field_70122_E) {
            this.field_110277_bt = 0.0F;
            this.func_110255_k(false);
         }

         this.field_184618_aE = this.field_70721_aZ;
         double var10 = this.field_70165_t - this.field_70169_q;
         double var7 = this.field_70161_v - this.field_70166_s;
         float var9 = MathHelper.func_76133_a(var10 * var10 + var7 * var7) * 4.0F;
         if (var9 > 1.0F) {
            var9 = 1.0F;
         }

         this.field_70721_aZ += (var9 - this.field_70721_aZ) * 0.4F;
         this.field_184619_aG += this.field_70721_aZ;
      } else {
         this.field_70747_aH = 0.02F;
         super.func_191986_a(var1, var2, var3);
      }
   }

   protected void func_205715_ee() {
      this.func_184185_a(SoundEvents.field_187720_cs, 0.4F, 1.0F);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("EatingHaystack", this.func_110204_cc());
      var1.func_74757_a("Bred", this.func_110205_ce());
      var1.func_74768_a("Temper", this.func_110252_cg());
      var1.func_74757_a("Tame", this.func_110248_bS());
      if (this.func_184780_dh() != null) {
         var1.func_74778_a("OwnerUUID", this.func_184780_dh().toString());
      }

      if (!this.field_110296_bG.func_70301_a(0).func_190926_b()) {
         var1.func_74782_a("SaddleItem", this.field_110296_bG.func_70301_a(0).func_77955_b(new NBTTagCompound()));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_110227_p(var1.func_74767_n("EatingHaystack"));
      this.func_110242_l(var1.func_74767_n("Bred"));
      this.func_110238_s(var1.func_74762_e("Temper"));
      this.func_110234_j(var1.func_74767_n("Tame"));
      String var2;
      if (var1.func_150297_b("OwnerUUID", 8)) {
         var2 = var1.func_74779_i("OwnerUUID");
      } else {
         String var3 = var1.func_74779_i("Owner");
         var2 = PreYggdrasilConverter.func_187473_a(this.func_184102_h(), var3);
      }

      if (!var2.isEmpty()) {
         this.func_184779_b(UUID.fromString(var2));
      }

      IAttributeInstance var5 = this.func_110140_aT().func_111152_a("Speed");
      if (var5 != null) {
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(var5.func_111125_b() * 0.25D);
      }

      if (var1.func_150297_b("SaddleItem", 10)) {
         ItemStack var4 = ItemStack.func_199557_a(var1.func_74775_l("SaddleItem"));
         if (var4.func_77973_b() == Items.field_151141_av) {
            this.field_110296_bG.func_70299_a(0, var4);
         }
      }

      this.func_110232_cE();
   }

   public boolean func_70878_b(EntityAnimal var1) {
      return false;
   }

   protected boolean func_110200_cJ() {
      return !this.func_184207_aI() && !this.func_184218_aH() && this.func_110248_bS() && !this.func_70631_g_() && this.func_110143_aJ() >= this.func_110138_aP() && this.func_70880_s();
   }

   @Nullable
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return null;
   }

   protected void func_190681_a(EntityAgeable var1, AbstractHorse var2) {
      double var3 = this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() + var1.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111125_b() + (double)this.func_110267_cL();
      var2.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(var3 / 3.0D);
      double var5 = this.func_110148_a(field_110271_bv).func_111125_b() + var1.func_110148_a(field_110271_bv).func_111125_b() + this.func_110245_cM();
      var2.func_110148_a(field_110271_bv).func_111128_a(var5 / 3.0D);
      double var7 = this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111125_b() + var1.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111125_b() + this.func_110203_cN();
      var2.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(var7 / 3.0D);
   }

   public boolean func_82171_bF() {
      return this.func_184179_bs() instanceof EntityLivingBase;
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

   public boolean func_184776_b() {
      return this.func_110257_ck();
   }

   public void func_184775_b(int var1) {
      this.field_110294_bI = true;
      this.func_110220_cK();
   }

   public void func_184777_r_() {
   }

   protected void func_110216_r(boolean var1) {
      BasicParticleType var2 = var1 ? Particles.field_197633_z : Particles.field_197601_L;

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_195594_a(var2, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var4, var6, var8);
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

   public void func_184232_k(Entity var1) {
      super.func_184232_k(var1);
      if (var1 instanceof EntityLiving) {
         EntityLiving var2 = (EntityLiving)var1;
         this.field_70761_aq = var2.field_70761_aq;
      }

      if (this.field_110282_bM > 0.0F) {
         float var6 = MathHelper.func_76126_a(this.field_70761_aq * 0.017453292F);
         float var3 = MathHelper.func_76134_b(this.field_70761_aq * 0.017453292F);
         float var4 = 0.7F * this.field_110282_bM;
         float var5 = 0.15F * this.field_110282_bM;
         var1.func_70107_b(this.field_70165_t + (double)(var4 * var6), this.field_70163_u + this.func_70042_X() + var1.func_70033_W() + (double)var5, this.field_70161_v - (double)(var4 * var3));
         if (var1 instanceof EntityLivingBase) {
            ((EntityLivingBase)var1).field_70761_aq = this.field_70761_aq;
         }
      }

   }

   protected float func_110267_cL() {
      return 15.0F + (float)this.field_70146_Z.nextInt(8) + (float)this.field_70146_Z.nextInt(9);
   }

   protected double func_110245_cM() {
      return 0.4000000059604645D + this.field_70146_Z.nextDouble() * 0.2D + this.field_70146_Z.nextDouble() * 0.2D + this.field_70146_Z.nextDouble() * 0.2D;
   }

   protected double func_110203_cN() {
      return (0.44999998807907104D + this.field_70146_Z.nextDouble() * 0.3D + this.field_70146_Z.nextDouble() * 0.3D + this.field_70146_Z.nextDouble() * 0.3D) * 0.25D;
   }

   public boolean func_70617_f_() {
      return false;
   }

   public float func_70047_e() {
      return this.field_70131_O;
   }

   public boolean func_190677_dK() {
      return false;
   }

   public boolean func_190682_f(ItemStack var1) {
      return false;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      int var3 = var1 - 400;
      if (var3 >= 0 && var3 < 2 && var3 < this.field_110296_bG.func_70302_i_()) {
         if (var3 == 0 && var2.func_77973_b() != Items.field_151141_av) {
            return false;
         } else if (var3 != 1 || this.func_190677_dK() && this.func_190682_f(var2)) {
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

   @Nullable
   public Entity func_184179_bs() {
      return this.func_184188_bt().isEmpty() ? null : (Entity)this.func_184188_bt().get(0);
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      var2 = super.func_204210_a(var1, var2, var3);
      if (this.field_70146_Z.nextInt(5) == 0) {
         this.func_70873_a(-24000);
      }

      return var2;
   }

   static {
      field_184787_bE = EntityDataManager.func_187226_a(AbstractHorse.class, DataSerializers.field_187191_a);
      field_184790_bH = EntityDataManager.func_187226_a(AbstractHorse.class, DataSerializers.field_187203_m);
   }
}
