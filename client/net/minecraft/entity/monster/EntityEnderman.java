package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   private static final UUID field_110192_bp = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier field_110193_bq = new AttributeModifier(field_110192_bp, "Attacking speed boost", 6.199999809265137, 0)
      .func_111168_a(false);
   private static boolean[] field_70827_d = new boolean[256];
   private int field_70828_e;
   private int field_70826_g;
   private Entity field_110194_bu;
   private boolean field_104003_g;

   public EntityEnderman(World var1) {
      super(var1);
      this.func_70105_a(0.6F, 2.9F);
      this.field_70138_W = 1.0F;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(40.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(7.0);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
      this.field_70180_af.func_75682_a(17, new Byte((byte)0));
      this.field_70180_af.func_75682_a(18, new Byte((byte)0));
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74777_a("carried", (short)Block.func_149682_b(this.func_146080_bZ()));
      var1.func_74777_a("carriedData", (short)this.func_70824_q());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_146081_a(Block.func_149729_e(var1.func_74765_d("carried")));
      this.func_70817_b(var1.func_74765_d("carriedData"));
   }

   @Override
   protected Entity func_70782_k() {
      EntityPlayer var1 = this.field_70170_p.func_72856_b(this, 64.0);
      if (var1 != null) {
         if (this.func_70821_d(var1)) {
            this.field_104003_g = true;
            if (this.field_70826_g == 0) {
               this.field_70170_p.func_72908_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, "mob.endermen.stare", 1.0F, 1.0F);
            }

            if (this.field_70826_g++ == 5) {
               this.field_70826_g = 0;
               this.func_70819_e(true);
               return var1;
            }
         } else {
            this.field_70826_g = 0;
         }
      }

      return null;
   }

   private boolean func_70821_d(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.field_70460_b[3];
      if (var2 != null && var2.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
         return false;
      } else {
         Vec3 var3 = var1.func_70676_i(1.0F).func_72432_b();
         Vec3 var4 = Vec3.func_72443_a(
            this.field_70165_t - var1.field_70165_t,
            this.field_70121_D.field_72338_b + (double)(this.field_70131_O / 2.0F) - (var1.field_70163_u + (double)var1.func_70047_e()),
            this.field_70161_v - var1.field_70161_v
         );
         double var5 = var4.func_72433_c();
         var4 = var4.func_72432_b();
         double var7 = var3.func_72430_b(var4);
         return var7 > 1.0 - 0.025 / var5 && var1.func_70685_l(this);
      }
   }

   @Override
   public void func_70636_d() {
      if (this.func_70026_G()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      if (this.field_110194_bu != this.field_70789_a) {
         IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var1.func_111124_b(field_110193_bq);
         if (this.field_70789_a != null) {
            var1.func_111121_a(field_110193_bq);
         }
      }

      this.field_110194_bu = this.field_70789_a;
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
         if (this.func_146080_bZ().func_149688_o() == Material.field_151579_a) {
            if (this.field_70146_Z.nextInt(20) == 0) {
               int var6 = MathHelper.func_76128_c(this.field_70165_t - 2.0 + this.field_70146_Z.nextDouble() * 4.0);
               int var2 = MathHelper.func_76128_c(this.field_70163_u + this.field_70146_Z.nextDouble() * 3.0);
               int var3 = MathHelper.func_76128_c(this.field_70161_v - 2.0 + this.field_70146_Z.nextDouble() * 4.0);
               Block var4 = this.field_70170_p.func_147439_a(var6, var2, var3);
               if (field_70827_d[Block.func_149682_b(var4)]) {
                  this.func_146081_a(var4);
                  this.func_70817_b(this.field_70170_p.func_72805_g(var6, var2, var3));
                  this.field_70170_p.func_147449_b(var6, var2, var3, Blocks.field_150350_a);
               }
            }
         } else if (this.field_70146_Z.nextInt(2000) == 0) {
            int var7 = MathHelper.func_76128_c(this.field_70165_t - 1.0 + this.field_70146_Z.nextDouble() * 2.0);
            int var10 = MathHelper.func_76128_c(this.field_70163_u + this.field_70146_Z.nextDouble() * 2.0);
            int var11 = MathHelper.func_76128_c(this.field_70161_v - 1.0 + this.field_70146_Z.nextDouble() * 2.0);
            Block var12 = this.field_70170_p.func_147439_a(var7, var10, var11);
            Block var5 = this.field_70170_p.func_147439_a(var7, var10 - 1, var11);
            if (var12.func_149688_o() == Material.field_151579_a && var5.func_149688_o() != Material.field_151579_a && var5.func_149686_d()) {
               this.field_70170_p.func_147465_d(var7, var10, var11, this.func_146080_bZ(), this.func_70824_q(), 3);
               this.func_146081_a(Blocks.field_150350_a);
            }
         }
      }

      for(int var8 = 0; var8 < 2; ++var8) {
         this.field_70170_p
            .func_72869_a(
               "portal",
               this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N,
               this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O - 0.25,
               this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N,
               (this.field_70146_Z.nextDouble() - 0.5) * 2.0,
               -this.field_70146_Z.nextDouble(),
               (this.field_70146_Z.nextDouble() - 0.5) * 2.0
            );
      }

      if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K) {
         float var9 = this.func_70013_c(1.0F);
         if (var9 > 0.5F
            && this.field_70170_p
               .func_72937_j(
                  MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
               )
            && this.field_70146_Z.nextFloat() * 30.0F < (var9 - 0.4F) * 2.0F) {
            this.field_70789_a = null;
            this.func_70819_e(false);
            this.field_104003_g = false;
            this.func_70820_n();
         }
      }

      if (this.func_70026_G() || this.func_70027_ad()) {
         this.field_70789_a = null;
         this.func_70819_e(false);
         this.field_104003_g = false;
         this.func_70820_n();
      }

      if (this.func_70823_r() && !this.field_104003_g && this.field_70146_Z.nextInt(100) == 0) {
         this.func_70819_e(false);
      }

      this.field_70703_bu = false;
      if (this.field_70789_a != null) {
         this.func_70625_a(this.field_70789_a, 100.0F, 100.0F);
      }

      if (!this.field_70170_p.field_72995_K && this.func_70089_S()) {
         if (this.field_70789_a != null) {
            if (this.field_70789_a instanceof EntityPlayer && this.func_70821_d((EntityPlayer)this.field_70789_a)) {
               if (this.field_70789_a.func_70068_e(this) < 16.0) {
                  this.func_70820_n();
               }

               this.field_70828_e = 0;
            } else if (this.field_70789_a.func_70068_e(this) > 256.0 && this.field_70828_e++ >= 30 && this.func_70816_c(this.field_70789_a)) {
               this.field_70828_e = 0;
            }
         } else {
            this.func_70819_e(false);
            this.field_70828_e = 0;
         }
      }

      super.func_70636_d();
   }

   protected boolean func_70820_n() {
      double var1 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * 64.0;
      double var3 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(64) - 32);
      double var5 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * 64.0;
      return this.func_70825_j(var1, var3, var5);
   }

   protected boolean func_70816_c(Entity var1) {
      Vec3 var2 = Vec3.func_72443_a(
         this.field_70165_t - var1.field_70165_t,
         this.field_70121_D.field_72338_b + (double)(this.field_70131_O / 2.0F) - var1.field_70163_u + (double)var1.func_70047_e(),
         this.field_70161_v - var1.field_70161_v
      );
      var2 = var2.func_72432_b();
      double var3 = 16.0;
      double var5 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * 8.0 - var2.field_72450_a * var3;
      double var7 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(16) - 8) - var2.field_72448_b * var3;
      double var9 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * 8.0 - var2.field_72449_c * var3;
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
      int var14 = MathHelper.func_76128_c(this.field_70165_t);
      int var15 = MathHelper.func_76128_c(this.field_70163_u);
      int var16 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.field_70170_p.func_72899_e(var14, var15, var16)) {
         boolean var17 = false;

         while(!var17 && var15 > 0) {
            Block var18 = this.field_70170_p.func_147439_a(var14, var15 - 1, var16);
            if (var18.func_149688_o().func_76230_c()) {
               var17 = true;
            } else {
               --this.field_70163_u;
               --var15;
            }
         }

         if (var17) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_72945_a(this, this.field_70121_D).isEmpty() && !this.field_70170_p.func_72953_d(this.field_70121_D)) {
               var13 = true;
            }
         }
      }

      if (!var13) {
         this.func_70107_b(var7, var9, var11);
         return false;
      } else {
         short var30 = 128;

         for(int var31 = 0; var31 < var30; ++var31) {
            double var19 = (double)var31 / ((double)var30 - 1.0);
            float var21 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            float var22 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            float var23 = (this.field_70146_Z.nextFloat() - 0.5F) * 0.2F;
            double var24 = var7 + (this.field_70165_t - var7) * var19 + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N * 2.0;
            double var26 = var9 + (this.field_70163_u - var9) * var19 + this.field_70146_Z.nextDouble() * (double)this.field_70131_O;
            double var28 = var11 + (this.field_70161_v - var11) * var19 + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N * 2.0;
            this.field_70170_p.func_72869_a("portal", var24, var26, var28, (double)var21, (double)var22, (double)var23);
         }

         this.field_70170_p.func_72908_a(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
         this.func_85030_a("mob.endermen.portal", 1.0F, 1.0F);
         return true;
      }
   }

   @Override
   protected String func_70639_aQ() {
      return this.func_70823_r() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.endermen.hit";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.endermen.death";
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151079_bi;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      Item var3 = this.func_146068_u();
      if (var3 != null) {
         int var4 = this.field_70146_Z.nextInt(2 + var2);

         for(int var5 = 0; var5 < var4; ++var5) {
            this.func_145779_a(var3, 1);
         }
      }
   }

   public void func_146081_a(Block var1) {
      this.field_70180_af.func_75692_b(16, (byte)(Block.func_149682_b(var1) & 0xFF));
   }

   public Block func_146080_bZ() {
      return Block.func_149729_e(this.field_70180_af.func_75683_a(16));
   }

   public void func_70817_b(int var1) {
      this.field_70180_af.func_75692_b(17, (byte)(var1 & 0xFF));
   }

   public int func_70824_q() {
      return this.field_70180_af.func_75683_a(17);
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         this.func_70819_e(true);
         if (var1 instanceof EntityDamageSource && var1.func_76346_g() instanceof EntityPlayer) {
            this.field_104003_g = true;
         }

         if (var1 instanceof EntityDamageSourceIndirect) {
            this.field_104003_g = false;

            for(int var3 = 0; var3 < 64; ++var3) {
               if (this.func_70820_n()) {
                  return true;
               }
            }

            return false;
         } else {
            return super.func_70097_a(var1, var2);
         }
      }
   }

   public boolean func_70823_r() {
      return this.field_70180_af.func_75683_a(18) > 0;
   }

   public void func_70819_e(boolean var1) {
      this.field_70180_af.func_75692_b(18, (byte)(var1 ? 1 : 0));
   }

   static {
      field_70827_d[Block.func_149682_b(Blocks.field_150349_c)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150346_d)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150354_m)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150351_n)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150327_N)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150328_O)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150338_P)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150337_Q)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150335_W)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150434_aF)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150435_aG)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150423_aK)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150440_ba)] = true;
      field_70827_d[Block.func_149682_b(Blocks.field_150391_bh)] = true;
   }
}
