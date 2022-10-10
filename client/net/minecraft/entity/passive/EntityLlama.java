package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILlamaFollowCaravan;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityLlama extends AbstractChestHorse implements IRangedAttackMob {
   private static final DataParameter<Integer> field_190720_bG;
   private static final DataParameter<Integer> field_190721_bH;
   private static final DataParameter<Integer> field_190722_bI;
   private boolean field_190723_bJ;
   @Nullable
   private EntityLlama field_190724_bK;
   @Nullable
   private EntityLlama field_190725_bL;

   public EntityLlama(World var1) {
      super(EntityType.field_200769_I, var1);
      this.func_70105_a(0.9F, 1.87F);
   }

   private void func_190706_p(int var1) {
      this.field_70180_af.func_187227_b(field_190720_bG, Math.max(1, Math.min(5, var1)));
   }

   private void func_190705_dT() {
      int var1 = this.field_70146_Z.nextFloat() < 0.04F ? 5 : 3;
      this.func_190706_p(1 + this.field_70146_Z.nextInt(var1));
   }

   public int func_190707_dL() {
      return (Integer)this.field_70180_af.func_187225_a(field_190720_bG);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Variant", this.func_190719_dM());
      var1.func_74768_a("Strength", this.func_190707_dL());
      if (!this.field_110296_bG.func_70301_a(1).func_190926_b()) {
         var1.func_74782_a("DecorItem", this.field_110296_bG.func_70301_a(1).func_77955_b(new NBTTagCompound()));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.func_190706_p(var1.func_74762_e("Strength"));
      super.func_70037_a(var1);
      this.func_190710_o(var1.func_74762_e("Variant"));
      if (var1.func_150297_b("DecorItem", 10)) {
         this.field_110296_bG.func_70299_a(1, ItemStack.func_199557_a(var1.func_74775_l("DecorItem")));
      }

      this.func_110232_cE();
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
      this.field_70714_bg.func_75776_a(2, new EntityAILlamaFollowCaravan(this, 2.0999999046325684D));
      this.field_70714_bg.func_75776_a(3, new EntityAIAttackRanged(this, 1.25D, 40, 20.0F));
      this.field_70714_bg.func_75776_a(3, new EntityAIPanic(this, 1.2D));
      this.field_70714_bg.func_75776_a(4, new EntityAIMate(this, 1.0D));
      this.field_70714_bg.func_75776_a(5, new EntityAIFollowParent(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWanderAvoidWater(this, 0.7D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityLlama.AIHurtByTarget(this));
      this.field_70715_bh.func_75776_a(2, new EntityLlama.AIDefendTarget(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_190720_bG, 0);
      this.field_70180_af.func_187214_a(field_190721_bH, -1);
      this.field_70180_af.func_187214_a(field_190722_bI, 0);
   }

   public int func_190719_dM() {
      return MathHelper.func_76125_a((Integer)this.field_70180_af.func_187225_a(field_190722_bI), 0, 3);
   }

   public void func_190710_o(int var1) {
      this.field_70180_af.func_187227_b(field_190722_bI, var1);
   }

   protected int func_190686_di() {
      return this.func_190695_dh() ? 2 + 3 * this.func_190696_dl() : super.func_190686_di();
   }

   public void func_184232_k(Entity var1) {
      if (this.func_184196_w(var1)) {
         float var2 = MathHelper.func_76134_b(this.field_70761_aq * 0.017453292F);
         float var3 = MathHelper.func_76126_a(this.field_70761_aq * 0.017453292F);
         float var4 = 0.3F;
         var1.func_70107_b(this.field_70165_t + (double)(0.3F * var3), this.field_70163_u + this.func_70042_X() + var1.func_70033_W(), this.field_70161_v - (double)(0.3F * var2));
      }
   }

   public double func_70042_X() {
      return (double)this.field_70131_O * 0.67D;
   }

   public boolean func_82171_bF() {
      return false;
   }

   protected boolean func_190678_b(EntityPlayer var1, ItemStack var2) {
      byte var3 = 0;
      byte var4 = 0;
      float var5 = 0.0F;
      boolean var6 = false;
      Item var7 = var2.func_77973_b();
      if (var7 == Items.field_151015_O) {
         var3 = 10;
         var4 = 3;
         var5 = 2.0F;
      } else if (var7 == Blocks.field_150407_cf.func_199767_j()) {
         var3 = 90;
         var4 = 6;
         var5 = 10.0F;
         if (this.func_110248_bS() && this.func_70874_b() == 0 && this.func_204701_dC()) {
            var6 = true;
            this.func_146082_f(var1);
         }
      }

      if (this.func_110143_aJ() < this.func_110138_aP() && var5 > 0.0F) {
         this.func_70691_i(var5);
         var6 = true;
      }

      if (this.func_70631_g_() && var3 > 0) {
         this.field_70170_p.func_195594_a(Particles.field_197632_y, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, 0.0D, 0.0D, 0.0D);
         if (!this.field_70170_p.field_72995_K) {
            this.func_110195_a(var3);
         }

         var6 = true;
      }

      if (var4 > 0 && (var6 || !this.func_110248_bS()) && this.func_110252_cg() < this.func_190676_dC()) {
         var6 = true;
         if (!this.field_70170_p.field_72995_K) {
            this.func_110198_t(var4);
         }
      }

      if (var6 && !this.func_174814_R()) {
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_191253_dD, this.func_184176_by(), 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
      }

      return var6;
   }

   protected boolean func_70610_aX() {
      return this.func_110143_aJ() <= 0.0F || this.func_110204_cc();
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var5 = super.func_204210_a(var1, var2, var3);
      this.func_190705_dT();
      int var4;
      if (var5 instanceof EntityLlama.GroupData) {
         var4 = ((EntityLlama.GroupData)var5).field_190886_a;
      } else {
         var4 = this.field_70146_Z.nextInt(4);
         var5 = new EntityLlama.GroupData(var4);
      }

      this.func_190710_o(var4);
      return (IEntityLivingData)var5;
   }

   public boolean func_190717_dN() {
      return this.func_190704_dO() != null;
   }

   protected SoundEvent func_184785_dv() {
      return SoundEvents.field_191250_dA;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_191260_dz;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_191254_dE;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_191252_dC;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_191256_dG, 0.15F, 1.0F);
   }

   protected void func_190697_dk() {
      this.func_184185_a(SoundEvents.field_191251_dB, 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
   }

   public void func_190687_dF() {
      SoundEvent var1 = this.func_184785_dv();
      if (var1 != null) {
         this.func_184185_a(var1, this.func_70599_aP(), this.func_70647_i());
      }

   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191187_aw;
   }

   public int func_190696_dl() {
      return this.func_190707_dL();
   }

   public boolean func_190677_dK() {
      return true;
   }

   public boolean func_190682_f(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      return ItemTags.field_200035_e.func_199685_a_(var2);
   }

   public boolean func_190685_dA() {
      return false;
   }

   public void func_76316_a(IInventory var1) {
      EnumDyeColor var2 = this.func_190704_dO();
      super.func_76316_a(var1);
      EnumDyeColor var3 = this.func_190704_dO();
      if (this.field_70173_aa > 20 && var3 != null && var3 != var2) {
         this.func_184185_a(SoundEvents.field_191257_dH, 0.5F, 1.0F);
      }

   }

   protected void func_110232_cE() {
      if (!this.field_70170_p.field_72995_K) {
         super.func_110232_cE();
         this.func_190711_a(func_195403_g(this.field_110296_bG.func_70301_a(1)));
      }
   }

   private void func_190711_a(@Nullable EnumDyeColor var1) {
      this.field_70180_af.func_187227_b(field_190721_bH, var1 == null ? -1 : var1.func_196059_a());
   }

   @Nullable
   private static EnumDyeColor func_195403_g(ItemStack var0) {
      Block var1 = Block.func_149634_a(var0.func_77973_b());
      return var1 instanceof BlockCarpet ? ((BlockCarpet)var1).func_196547_d() : null;
   }

   @Nullable
   public EnumDyeColor func_190704_dO() {
      int var1 = (Integer)this.field_70180_af.func_187225_a(field_190721_bH);
      return var1 == -1 ? null : EnumDyeColor.func_196056_a(var1);
   }

   public int func_190676_dC() {
      return 30;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      return var1 != this && var1 instanceof EntityLlama && this.func_110200_cJ() && ((EntityLlama)var1).func_110200_cJ();
   }

   public EntityLlama func_90011_a(EntityAgeable var1) {
      EntityLlama var2 = new EntityLlama(this.field_70170_p);
      this.func_190681_a(var1, var2);
      EntityLlama var3 = (EntityLlama)var1;
      int var4 = this.field_70146_Z.nextInt(Math.max(this.func_190707_dL(), var3.func_190707_dL())) + 1;
      if (this.field_70146_Z.nextFloat() < 0.03F) {
         ++var4;
      }

      var2.func_190706_p(var4);
      var2.func_190710_o(this.field_70146_Z.nextBoolean() ? this.func_190719_dM() : var3.func_190719_dM());
      return var2;
   }

   private void func_190713_e(EntityLivingBase var1) {
      EntityLlamaSpit var2 = new EntityLlamaSpit(this.field_70170_p, this);
      double var3 = var1.field_70165_t - this.field_70165_t;
      double var5 = var1.func_174813_aQ().field_72338_b + (double)(var1.field_70131_O / 3.0F) - var2.field_70163_u;
      double var7 = var1.field_70161_v - this.field_70161_v;
      float var9 = MathHelper.func_76133_a(var3 * var3 + var7 * var7) * 0.2F;
      var2.func_70186_c(var3, var5 + (double)var9, var7, 1.5F, 10.0F);
      this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_191255_dF, this.func_184176_by(), 1.0F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F);
      this.field_70170_p.func_72838_d(var2);
      this.field_190723_bJ = true;
   }

   private void func_190714_x(boolean var1) {
      this.field_190723_bJ = var1;
   }

   public void func_180430_e(float var1, float var2) {
      int var3 = MathHelper.func_76123_f((var1 * 0.5F - 3.0F) * var2);
      if (var3 > 0) {
         if (var1 >= 6.0F) {
            this.func_70097_a(DamageSource.field_76379_h, (float)var3);
            if (this.func_184207_aI()) {
               Iterator var4 = this.func_184182_bu().iterator();

               while(var4.hasNext()) {
                  Entity var5 = (Entity)var4.next();
                  var5.func_70097_a(DamageSource.field_76379_h, (float)var3);
               }
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

   public void func_190709_dP() {
      if (this.field_190724_bK != null) {
         this.field_190724_bK.field_190725_bL = null;
      }

      this.field_190724_bK = null;
   }

   public void func_190715_a(EntityLlama var1) {
      this.field_190724_bK = var1;
      this.field_190724_bK.field_190725_bL = this;
   }

   public boolean func_190712_dQ() {
      return this.field_190725_bL != null;
   }

   public boolean func_190718_dR() {
      return this.field_190724_bK != null;
   }

   @Nullable
   public EntityLlama func_190716_dS() {
      return this.field_190724_bK;
   }

   protected double func_190634_dg() {
      return 2.0D;
   }

   protected void func_190679_dD() {
      if (!this.func_190718_dR() && this.func_70631_g_()) {
         super.func_190679_dD();
      }

   }

   public boolean func_190684_dE() {
      return false;
   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      this.func_190713_e(var1);
   }

   public void func_184724_a(boolean var1) {
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   static {
      field_190720_bG = EntityDataManager.func_187226_a(EntityLlama.class, DataSerializers.field_187192_b);
      field_190721_bH = EntityDataManager.func_187226_a(EntityLlama.class, DataSerializers.field_187192_b);
      field_190722_bI = EntityDataManager.func_187226_a(EntityLlama.class, DataSerializers.field_187192_b);
   }

   static class AIDefendTarget extends EntityAINearestAttackableTarget<EntityWolf> {
      public AIDefendTarget(EntityLlama var1) {
         super(var1, EntityWolf.class, 16, false, true, (Predicate)null);
      }

      public boolean func_75250_a() {
         if (super.func_75250_a() && this.field_75309_a != null && !((EntityWolf)this.field_75309_a).func_70909_n()) {
            return true;
         } else {
            this.field_75299_d.func_70624_b((EntityLivingBase)null);
            return false;
         }
      }

      protected double func_111175_f() {
         return super.func_111175_f() * 0.25D;
      }
   }

   static class AIHurtByTarget extends EntityAIHurtByTarget {
      public AIHurtByTarget(EntityLlama var1) {
         super(var1, false);
      }

      public boolean func_75253_b() {
         if (this.field_75299_d instanceof EntityLlama) {
            EntityLlama var1 = (EntityLlama)this.field_75299_d;
            if (var1.field_190723_bJ) {
               var1.func_190714_x(false);
               return false;
            }
         }

         return super.func_75253_b();
      }
   }

   static class GroupData implements IEntityLivingData {
      public int field_190886_a;

      private GroupData(int var1) {
         super();
         this.field_190886_a = var1;
      }

      // $FF: synthetic method
      GroupData(int var1, Object var2) {
         this(var1);
      }
   }
}
