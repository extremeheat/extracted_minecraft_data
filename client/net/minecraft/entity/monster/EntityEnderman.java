package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   private static final UUID field_110192_bp = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier field_110193_bq;
   private static final Set<Block> field_70827_d;
   private boolean field_104003_g;

   public EntityEnderman(World var1) {
      super(var1);
      this.func_70105_a(0.6F, 2.9F);
      this.field_70138_W = 1.0F;
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIAttackOnCollide(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70714_bg.func_75776_a(10, new EntityEnderman.AIPlaceBlock(this));
      this.field_70714_bg.func_75776_a(11, new EntityEnderman.AITakeBlock(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityEnderman.AIFindPlayer(this));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityEndermite.class, 10, true, false, new Predicate<EntityEndermite>() {
         public boolean apply(EntityEndermite var1) {
            return var1.func_175495_n();
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((EntityEndermite)var1);
         }
      }));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(40.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(7.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Short((short)0));
      this.field_70180_af.func_75682_a(17, new Byte((byte)0));
      this.field_70180_af.func_75682_a(18, new Byte((byte)0));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      IBlockState var2 = this.func_175489_ck();
      var1.func_74777_a("carried", (short)Block.func_149682_b(var2.func_177230_c()));
      var1.func_74777_a("carriedData", (short)var2.func_177230_c().func_176201_c(var2));
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      IBlockState var2;
      if (var1.func_150297_b("carried", 8)) {
         var2 = Block.func_149684_b(var1.func_74779_i("carried")).func_176203_a(var1.func_74765_d("carriedData") & '\uffff');
      } else {
         var2 = Block.func_149729_e(var1.func_74765_d("carried")).func_176203_a(var1.func_74765_d("carriedData") & '\uffff');
      }

      this.func_175490_a(var2);
   }

   private boolean func_70821_d(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.field_70460_b[3];
      if (var2 != null && var2.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
         return false;
      } else {
         Vec3 var3 = var1.func_70676_i(1.0F).func_72432_b();
         Vec3 var4 = new Vec3(this.field_70165_t - var1.field_70165_t, this.func_174813_aQ().field_72338_b + (double)(this.field_70131_O / 2.0F) - (var1.field_70163_u + (double)var1.func_70047_e()), this.field_70161_v - var1.field_70161_v);
         double var5 = var4.func_72433_c();
         var4 = var4.func_72432_b();
         double var7 = var3.func_72430_b(var4);
         return var7 > 1.0D - 0.025D / var5 ? var1.func_70685_l(this) : false;
      }
   }

   public float func_70047_e() {
      return 2.55F;
   }

   public void func_70636_d() {
      if (this.field_70170_p.field_72995_K) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.PORTAL, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O - 0.25D, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D, -this.field_70146_Z.nextDouble(), (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.field_70703_bu = false;
      super.func_70636_d();
   }

   protected void func_70619_bc() {
      if (this.func_70026_G()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      if (this.func_70823_r() && !this.field_104003_g && this.field_70146_Z.nextInt(100) == 0) {
         this.func_70819_e(false);
      }

      if (this.field_70170_p.func_72935_r()) {
         float var1 = this.func_70013_c(1.0F);
         if (var1 > 0.5F && this.field_70170_p.func_175678_i(new BlockPos(this)) && this.field_70146_Z.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.func_70624_b((EntityLivingBase)null);
            this.func_70819_e(false);
            this.field_104003_g = false;
            this.func_70820_n();
         }
      }

      super.func_70619_bc();
   }

   protected boolean func_70820_n() {
      double var1 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;
      double var3 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(64) - 32);
      double var5 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;
      return this.func_70825_j(var1, var3, var5);
   }

   protected boolean func_70816_c(Entity var1) {
      Vec3 var2 = new Vec3(this.field_70165_t - var1.field_70165_t, this.func_174813_aQ().field_72338_b + (double)(this.field_70131_O / 2.0F) - var1.field_70163_u + (double)var1.func_70047_e(), this.field_70161_v - var1.field_70161_v);
      var2 = var2.func_72432_b();
      double var3 = 16.0D;
      double var5 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - var2.field_72450_a * var3;
      double var7 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(16) - 8) - var2.field_72448_b * var3;
      double var9 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - var2.field_72449_c * var3;
      return this.func_70825_j(var5, var7, var9);
   }

   protected boolean func_70825_j(double var1, double var3, double var5) {
      double var7 = this.field_70165_t;
      double var9 = this.field_70163_u;
      double var11 = this.field_70161_v;
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      boolean var13 = false;
      BlockPos var14 = new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      if (this.field_70170_p.func_175667_e(var14)) {
         boolean var15 = false;

         while(!var15 && var14.func_177956_o() > 0) {
            BlockPos var16 = var14.func_177977_b();
            Block var17 = this.field_70170_p.func_180495_p(var16).func_177230_c();
            if (var17.func_149688_o().func_76230_c()) {
               var15 = true;
            } else {
               --this.field_70163_u;
               var14 = var16;
            }
         }

         if (var15) {
            super.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty() && !this.field_70170_p.func_72953_d(this.func_174813_aQ())) {
               var13 = true;
            }
         }
      }

      if (!var13) {
         this.func_70107_b(var7, var9, var11);
         return false;
      } else {
         short var28 = 128;

         for(int var29 = 0; var29 < var28; ++var29) {
            double var30 = (double)var29 / ((double)var28 - 1.0D);
            float var19 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            float var20 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            float var21 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            double var22 = var7 + (this.field_70165_t - var7) * var30 + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N * 2.0D;
            double var24 = var9 + (this.field_70163_u - var9) * var30 + this.field_70146_Z.nextDouble() * (double)this.field_70131_O;
            double var26 = var11 + (this.field_70161_v - var11) * var30 + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N * 2.0D;
            this.field_70170_p.func_175688_a(EnumParticleTypes.PORTAL, var22, var24, var26, (double)var19, (double)var20, (double)var21);
         }

         this.field_70170_p.func_72908_a(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
         this.func_85030_a("mob.endermen.portal", 1.0F, 1.0F);
         return true;
      }
   }

   protected String func_70639_aQ() {
      return this.func_70823_r() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   protected String func_70621_aR() {
      return "mob.endermen.hit";
   }

   protected String func_70673_aS() {
      return "mob.endermen.death";
   }

   protected Item func_146068_u() {
      return Items.field_151079_bi;
   }

   protected void func_70628_a(boolean var1, int var2) {
      Item var3 = this.func_146068_u();
      if (var3 != null) {
         int var4 = this.field_70146_Z.nextInt(2 + var2);

         for(int var5 = 0; var5 < var4; ++var5) {
            this.func_145779_a(var3, 1);
         }
      }

   }

   public void func_175490_a(IBlockState var1) {
      this.field_70180_af.func_75692_b(16, (short)(Block.func_176210_f(var1) & '\uffff'));
   }

   public IBlockState func_175489_ck() {
      return Block.func_176220_d(this.field_70180_af.func_75693_b(16) & '\uffff');
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if (var1.func_76346_g() == null || !(var1.func_76346_g() instanceof EntityEndermite)) {
            if (!this.field_70170_p.field_72995_K) {
               this.func_70819_e(true);
            }

            if (var1 instanceof EntityDamageSource && var1.func_76346_g() instanceof EntityPlayer) {
               if (var1.func_76346_g() instanceof EntityPlayerMP && ((EntityPlayerMP)var1.func_76346_g()).field_71134_c.func_73083_d()) {
                  this.func_70819_e(false);
               } else {
                  this.field_104003_g = true;
               }
            }

            if (var1 instanceof EntityDamageSourceIndirect) {
               this.field_104003_g = false;

               for(int var4 = 0; var4 < 64; ++var4) {
                  if (this.func_70820_n()) {
                     return true;
                  }
               }

               return false;
            }
         }

         boolean var3 = super.func_70097_a(var1, var2);
         if (var1.func_76363_c() && this.field_70146_Z.nextInt(10) != 0) {
            this.func_70820_n();
         }

         return var3;
      }
   }

   public boolean func_70823_r() {
      return this.field_70180_af.func_75683_a(18) > 0;
   }

   public void func_70819_e(boolean var1) {
      this.field_70180_af.func_75692_b(18, (byte)(var1 ? 1 : 0));
   }

   static {
      field_110193_bq = (new AttributeModifier(field_110192_bp, "Attacking speed boost", 0.15000000596046448D, 0)).func_111168_a(false);
      field_70827_d = Sets.newIdentityHashSet();
      field_70827_d.add(Blocks.field_150349_c);
      field_70827_d.add(Blocks.field_150346_d);
      field_70827_d.add(Blocks.field_150354_m);
      field_70827_d.add(Blocks.field_150351_n);
      field_70827_d.add(Blocks.field_150327_N);
      field_70827_d.add(Blocks.field_150328_O);
      field_70827_d.add(Blocks.field_150338_P);
      field_70827_d.add(Blocks.field_150337_Q);
      field_70827_d.add(Blocks.field_150335_W);
      field_70827_d.add(Blocks.field_150434_aF);
      field_70827_d.add(Blocks.field_150435_aG);
      field_70827_d.add(Blocks.field_150423_aK);
      field_70827_d.add(Blocks.field_150440_ba);
      field_70827_d.add(Blocks.field_150391_bh);
   }

   static class AITakeBlock extends EntityAIBase {
      private EntityEnderman field_179473_a;

      public AITakeBlock(EntityEnderman var1) {
         super();
         this.field_179473_a = var1;
      }

      public boolean func_75250_a() {
         if (!this.field_179473_a.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         } else if (this.field_179473_a.func_175489_ck().func_177230_c().func_149688_o() != Material.field_151579_a) {
            return false;
         } else {
            return this.field_179473_a.func_70681_au().nextInt(20) == 0;
         }
      }

      public void func_75246_d() {
         Random var1 = this.field_179473_a.func_70681_au();
         World var2 = this.field_179473_a.field_70170_p;
         int var3 = MathHelper.func_76128_c(this.field_179473_a.field_70165_t - 2.0D + var1.nextDouble() * 4.0D);
         int var4 = MathHelper.func_76128_c(this.field_179473_a.field_70163_u + var1.nextDouble() * 3.0D);
         int var5 = MathHelper.func_76128_c(this.field_179473_a.field_70161_v - 2.0D + var1.nextDouble() * 4.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         IBlockState var7 = var2.func_180495_p(var6);
         Block var8 = var7.func_177230_c();
         if (EntityEnderman.field_70827_d.contains(var8)) {
            this.field_179473_a.func_175490_a(var7);
            var2.func_175656_a(var6, Blocks.field_150350_a.func_176223_P());
         }

      }
   }

   static class AIPlaceBlock extends EntityAIBase {
      private EntityEnderman field_179475_a;

      public AIPlaceBlock(EntityEnderman var1) {
         super();
         this.field_179475_a = var1;
      }

      public boolean func_75250_a() {
         if (!this.field_179475_a.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         } else if (this.field_179475_a.func_175489_ck().func_177230_c().func_149688_o() == Material.field_151579_a) {
            return false;
         } else {
            return this.field_179475_a.func_70681_au().nextInt(2000) == 0;
         }
      }

      public void func_75246_d() {
         Random var1 = this.field_179475_a.func_70681_au();
         World var2 = this.field_179475_a.field_70170_p;
         int var3 = MathHelper.func_76128_c(this.field_179475_a.field_70165_t - 1.0D + var1.nextDouble() * 2.0D);
         int var4 = MathHelper.func_76128_c(this.field_179475_a.field_70163_u + var1.nextDouble() * 2.0D);
         int var5 = MathHelper.func_76128_c(this.field_179475_a.field_70161_v - 1.0D + var1.nextDouble() * 2.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         Block var7 = var2.func_180495_p(var6).func_177230_c();
         Block var8 = var2.func_180495_p(var6.func_177977_b()).func_177230_c();
         if (this.func_179474_a(var2, var6, this.field_179475_a.func_175489_ck().func_177230_c(), var7, var8)) {
            var2.func_180501_a(var6, this.field_179475_a.func_175489_ck(), 3);
            this.field_179475_a.func_175490_a(Blocks.field_150350_a.func_176223_P());
         }

      }

      private boolean func_179474_a(World var1, BlockPos var2, Block var3, Block var4, Block var5) {
         if (!var3.func_176196_c(var1, var2)) {
            return false;
         } else if (var4.func_149688_o() != Material.field_151579_a) {
            return false;
         } else if (var5.func_149688_o() == Material.field_151579_a) {
            return false;
         } else {
            return var5.func_149686_d();
         }
      }
   }

   static class AIFindPlayer extends EntityAINearestAttackableTarget {
      private EntityPlayer field_179448_g;
      private int field_179450_h;
      private int field_179451_i;
      private EntityEnderman field_179449_j;

      public AIFindPlayer(EntityEnderman var1) {
         super(var1, EntityPlayer.class, true);
         this.field_179449_j = var1;
      }

      public boolean func_75250_a() {
         double var1 = this.func_111175_f();
         List var3 = this.field_75299_d.field_70170_p.func_175647_a(EntityPlayer.class, this.field_75299_d.func_174813_aQ().func_72314_b(var1, 4.0D, var1), this.field_82643_g);
         Collections.sort(var3, this.field_75306_g);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.field_179448_g = (EntityPlayer)var3.get(0);
            return true;
         }
      }

      public void func_75249_e() {
         this.field_179450_h = 5;
         this.field_179451_i = 0;
      }

      public void func_75251_c() {
         this.field_179448_g = null;
         this.field_179449_j.func_70819_e(false);
         IAttributeInstance var1 = this.field_179449_j.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var1.func_111124_b(EntityEnderman.field_110193_bq);
         super.func_75251_c();
      }

      public boolean func_75253_b() {
         if (this.field_179448_g != null) {
            if (!this.field_179449_j.func_70821_d(this.field_179448_g)) {
               return false;
            } else {
               this.field_179449_j.field_104003_g = true;
               this.field_179449_j.func_70625_a(this.field_179448_g, 10.0F, 10.0F);
               return true;
            }
         } else {
            return super.func_75253_b();
         }
      }

      public void func_75246_d() {
         if (this.field_179448_g != null) {
            if (--this.field_179450_h <= 0) {
               this.field_75309_a = this.field_179448_g;
               this.field_179448_g = null;
               super.func_75249_e();
               this.field_179449_j.func_85030_a("mob.endermen.stare", 1.0F, 1.0F);
               this.field_179449_j.func_70819_e(true);
               IAttributeInstance var1 = this.field_179449_j.func_110148_a(SharedMonsterAttributes.field_111263_d);
               var1.func_111121_a(EntityEnderman.field_110193_bq);
            }
         } else {
            if (this.field_75309_a != null) {
               if (this.field_75309_a instanceof EntityPlayer && this.field_179449_j.func_70821_d((EntityPlayer)this.field_75309_a)) {
                  if (this.field_75309_a.func_70068_e(this.field_179449_j) < 16.0D) {
                     this.field_179449_j.func_70820_n();
                  }

                  this.field_179451_i = 0;
               } else if (this.field_75309_a.func_70068_e(this.field_179449_j) > 256.0D && this.field_179451_i++ >= 30 && this.field_179449_j.func_70816_c(this.field_75309_a)) {
                  this.field_179451_i = 0;
               }
            }

            super.func_75246_d();
         }

      }
   }
}
