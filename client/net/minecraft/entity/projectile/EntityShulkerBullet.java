package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityShulkerBullet extends Entity {
   private EntityLivingBase field_184570_a;
   private Entity field_184571_b;
   @Nullable
   private EnumFacing field_184573_c;
   private int field_184575_d;
   private double field_184577_e;
   private double field_184578_f;
   private double field_184579_g;
   @Nullable
   private UUID field_184580_h;
   private BlockPos field_184572_as;
   @Nullable
   private UUID field_184574_at;
   private BlockPos field_184576_au;

   public EntityShulkerBullet(World var1) {
      super(EntityType.field_200739_ae, var1);
      this.func_70105_a(0.3125F, 0.3125F);
      this.field_70145_X = true;
   }

   public EntityShulkerBullet(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1);
      this.func_70012_b(var2, var4, var6, this.field_70177_z, this.field_70125_A);
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
   }

   public EntityShulkerBullet(World var1, EntityLivingBase var2, Entity var3, EnumFacing.Axis var4) {
      this(var1);
      this.field_184570_a = var2;
      BlockPos var5 = new BlockPos(var2);
      double var6 = (double)var5.func_177958_n() + 0.5D;
      double var8 = (double)var5.func_177956_o() + 0.5D;
      double var10 = (double)var5.func_177952_p() + 0.5D;
      this.func_70012_b(var6, var8, var10, this.field_70177_z, this.field_70125_A);
      this.field_184571_b = var3;
      this.field_184573_c = EnumFacing.UP;
      this.func_184569_a(var4);
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected void func_70014_b(NBTTagCompound var1) {
      BlockPos var2;
      NBTTagCompound var3;
      if (this.field_184570_a != null) {
         var2 = new BlockPos(this.field_184570_a);
         var3 = NBTUtil.func_186862_a(this.field_184570_a.func_110124_au());
         var3.func_74768_a("X", var2.func_177958_n());
         var3.func_74768_a("Y", var2.func_177956_o());
         var3.func_74768_a("Z", var2.func_177952_p());
         var1.func_74782_a("Owner", var3);
      }

      if (this.field_184571_b != null) {
         var2 = new BlockPos(this.field_184571_b);
         var3 = NBTUtil.func_186862_a(this.field_184571_b.func_110124_au());
         var3.func_74768_a("X", var2.func_177958_n());
         var3.func_74768_a("Y", var2.func_177956_o());
         var3.func_74768_a("Z", var2.func_177952_p());
         var1.func_74782_a("Target", var3);
      }

      if (this.field_184573_c != null) {
         var1.func_74768_a("Dir", this.field_184573_c.func_176745_a());
      }

      var1.func_74768_a("Steps", this.field_184575_d);
      var1.func_74780_a("TXD", this.field_184577_e);
      var1.func_74780_a("TYD", this.field_184578_f);
      var1.func_74780_a("TZD", this.field_184579_g);
   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.field_184575_d = var1.func_74762_e("Steps");
      this.field_184577_e = var1.func_74769_h("TXD");
      this.field_184578_f = var1.func_74769_h("TYD");
      this.field_184579_g = var1.func_74769_h("TZD");
      if (var1.func_150297_b("Dir", 99)) {
         this.field_184573_c = EnumFacing.func_82600_a(var1.func_74762_e("Dir"));
      }

      NBTTagCompound var2;
      if (var1.func_150297_b("Owner", 10)) {
         var2 = var1.func_74775_l("Owner");
         this.field_184580_h = NBTUtil.func_186860_b(var2);
         this.field_184572_as = new BlockPos(var2.func_74762_e("X"), var2.func_74762_e("Y"), var2.func_74762_e("Z"));
      }

      if (var1.func_150297_b("Target", 10)) {
         var2 = var1.func_74775_l("Target");
         this.field_184574_at = NBTUtil.func_186860_b(var2);
         this.field_184576_au = new BlockPos(var2.func_74762_e("X"), var2.func_74762_e("Y"), var2.func_74762_e("Z"));
      }

   }

   protected void func_70088_a() {
   }

   private void func_184568_a(@Nullable EnumFacing var1) {
      this.field_184573_c = var1;
   }

   private void func_184569_a(@Nullable EnumFacing.Axis var1) {
      double var3 = 0.5D;
      BlockPos var2;
      if (this.field_184571_b == null) {
         var2 = (new BlockPos(this)).func_177977_b();
      } else {
         var3 = (double)this.field_184571_b.field_70131_O * 0.5D;
         var2 = new BlockPos(this.field_184571_b.field_70165_t, this.field_184571_b.field_70163_u + var3, this.field_184571_b.field_70161_v);
      }

      double var5 = (double)var2.func_177958_n() + 0.5D;
      double var7 = (double)var2.func_177956_o() + var3;
      double var9 = (double)var2.func_177952_p() + 0.5D;
      EnumFacing var11 = null;
      if (var2.func_177957_d(this.field_70165_t, this.field_70163_u, this.field_70161_v) >= 4.0D) {
         BlockPos var12 = new BlockPos(this);
         ArrayList var13 = Lists.newArrayList();
         if (var1 != EnumFacing.Axis.X) {
            if (var12.func_177958_n() < var2.func_177958_n() && this.field_70170_p.func_175623_d(var12.func_177974_f())) {
               var13.add(EnumFacing.EAST);
            } else if (var12.func_177958_n() > var2.func_177958_n() && this.field_70170_p.func_175623_d(var12.func_177976_e())) {
               var13.add(EnumFacing.WEST);
            }
         }

         if (var1 != EnumFacing.Axis.Y) {
            if (var12.func_177956_o() < var2.func_177956_o() && this.field_70170_p.func_175623_d(var12.func_177984_a())) {
               var13.add(EnumFacing.UP);
            } else if (var12.func_177956_o() > var2.func_177956_o() && this.field_70170_p.func_175623_d(var12.func_177977_b())) {
               var13.add(EnumFacing.DOWN);
            }
         }

         if (var1 != EnumFacing.Axis.Z) {
            if (var12.func_177952_p() < var2.func_177952_p() && this.field_70170_p.func_175623_d(var12.func_177968_d())) {
               var13.add(EnumFacing.SOUTH);
            } else if (var12.func_177952_p() > var2.func_177952_p() && this.field_70170_p.func_175623_d(var12.func_177978_c())) {
               var13.add(EnumFacing.NORTH);
            }
         }

         var11 = EnumFacing.func_176741_a(this.field_70146_Z);
         if (var13.isEmpty()) {
            for(int var14 = 5; !this.field_70170_p.func_175623_d(var12.func_177972_a(var11)) && var14 > 0; --var14) {
               var11 = EnumFacing.func_176741_a(this.field_70146_Z);
            }
         } else {
            var11 = (EnumFacing)var13.get(this.field_70146_Z.nextInt(var13.size()));
         }

         var5 = this.field_70165_t + (double)var11.func_82601_c();
         var7 = this.field_70163_u + (double)var11.func_96559_d();
         var9 = this.field_70161_v + (double)var11.func_82599_e();
      }

      this.func_184568_a(var11);
      double var20 = var5 - this.field_70165_t;
      double var21 = var7 - this.field_70163_u;
      double var16 = var9 - this.field_70161_v;
      double var18 = (double)MathHelper.func_76133_a(var20 * var20 + var21 * var21 + var16 * var16);
      if (var18 == 0.0D) {
         this.field_184577_e = 0.0D;
         this.field_184578_f = 0.0D;
         this.field_184579_g = 0.0D;
      } else {
         this.field_184577_e = var20 / var18 * 0.15D;
         this.field_184578_f = var21 / var18 * 0.15D;
         this.field_184579_g = var16 / var18 * 0.15D;
      }

      this.field_70160_al = true;
      this.field_184575_d = 10 + this.field_70146_Z.nextInt(5) * 10;
   }

   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      } else {
         super.func_70071_h_();
         if (!this.field_70170_p.field_72995_K) {
            List var1;
            Iterator var2;
            EntityLivingBase var3;
            if (this.field_184571_b == null && this.field_184574_at != null) {
               var1 = this.field_70170_p.func_72872_a(EntityLivingBase.class, new AxisAlignedBB(this.field_184576_au.func_177982_a(-2, -2, -2), this.field_184576_au.func_177982_a(2, 2, 2)));
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  var3 = (EntityLivingBase)var2.next();
                  if (var3.func_110124_au().equals(this.field_184574_at)) {
                     this.field_184571_b = var3;
                     break;
                  }
               }

               this.field_184574_at = null;
            }

            if (this.field_184570_a == null && this.field_184580_h != null) {
               var1 = this.field_70170_p.func_72872_a(EntityLivingBase.class, new AxisAlignedBB(this.field_184572_as.func_177982_a(-2, -2, -2), this.field_184572_as.func_177982_a(2, 2, 2)));
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  var3 = (EntityLivingBase)var2.next();
                  if (var3.func_110124_au().equals(this.field_184580_h)) {
                     this.field_184570_a = var3;
                     break;
                  }
               }

               this.field_184580_h = null;
            }

            if (this.field_184571_b == null || !this.field_184571_b.func_70089_S() || this.field_184571_b instanceof EntityPlayer && ((EntityPlayer)this.field_184571_b).func_175149_v()) {
               if (!this.func_189652_ae()) {
                  this.field_70181_x -= 0.04D;
               }
            } else {
               this.field_184577_e = MathHelper.func_151237_a(this.field_184577_e * 1.025D, -1.0D, 1.0D);
               this.field_184578_f = MathHelper.func_151237_a(this.field_184578_f * 1.025D, -1.0D, 1.0D);
               this.field_184579_g = MathHelper.func_151237_a(this.field_184579_g * 1.025D, -1.0D, 1.0D);
               this.field_70159_w += (this.field_184577_e - this.field_70159_w) * 0.2D;
               this.field_70181_x += (this.field_184578_f - this.field_70181_x) * 0.2D;
               this.field_70179_y += (this.field_184579_g - this.field_70179_y) * 0.2D;
            }

            RayTraceResult var4 = ProjectileHelper.func_188802_a(this, true, false, this.field_184570_a);
            if (var4 != null) {
               this.func_184567_a(var4);
            }
         }

         this.func_70107_b(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
         ProjectileHelper.func_188803_a(this, 0.5F);
         if (this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_195594_a(Particles.field_197624_q, this.field_70165_t - this.field_70159_w, this.field_70163_u - this.field_70181_x + 0.15D, this.field_70161_v - this.field_70179_y, 0.0D, 0.0D, 0.0D);
         } else if (this.field_184571_b != null && !this.field_184571_b.field_70128_L) {
            if (this.field_184575_d > 0) {
               --this.field_184575_d;
               if (this.field_184575_d == 0) {
                  this.func_184569_a(this.field_184573_c == null ? null : this.field_184573_c.func_176740_k());
               }
            }

            if (this.field_184573_c != null) {
               BlockPos var5 = new BlockPos(this);
               EnumFacing.Axis var6 = this.field_184573_c.func_176740_k();
               if (this.field_70170_p.func_195595_w(var5.func_177972_a(this.field_184573_c))) {
                  this.func_184569_a(var6);
               } else {
                  BlockPos var7 = new BlockPos(this.field_184571_b);
                  if (var6 == EnumFacing.Axis.X && var5.func_177958_n() == var7.func_177958_n() || var6 == EnumFacing.Axis.Z && var5.func_177952_p() == var7.func_177952_p() || var6 == EnumFacing.Axis.Y && var5.func_177956_o() == var7.func_177956_o()) {
                     this.func_184569_a(var6);
                  }
               }
            }
         }

      }
   }

   public boolean func_70027_ad() {
      return false;
   }

   public boolean func_70112_a(double var1) {
      return var1 < 16384.0D;
   }

   public float func_70013_c() {
      return 1.0F;
   }

   public int func_70070_b() {
      return 15728880;
   }

   protected void func_184567_a(RayTraceResult var1) {
      if (var1.field_72308_g == null) {
         ((WorldServer)this.field_70170_p).func_195598_a(Particles.field_197627_t, this.field_70165_t, this.field_70163_u, this.field_70161_v, 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.func_184185_a(SoundEvents.field_187775_eP, 1.0F, 1.0F);
      } else {
         boolean var2 = var1.field_72308_g.func_70097_a(DamageSource.func_188403_a(this, this.field_184570_a).func_76349_b(), 4.0F);
         if (var2) {
            this.func_174815_a(this.field_184570_a, var1.field_72308_g);
            if (var1.field_72308_g instanceof EntityLivingBase) {
               ((EntityLivingBase)var1.field_72308_g).func_195064_c(new PotionEffect(MobEffects.field_188424_y, 200));
            }
         }
      }

      this.func_70106_y();
   }

   public boolean func_70067_L() {
      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (!this.field_70170_p.field_72995_K) {
         this.func_184185_a(SoundEvents.field_187777_eQ, 1.0F, 1.0F);
         ((WorldServer)this.field_70170_p).func_195598_a(Particles.field_197614_g, this.field_70165_t, this.field_70163_u, this.field_70161_v, 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.func_70106_y();
      }

      return true;
   }
}
