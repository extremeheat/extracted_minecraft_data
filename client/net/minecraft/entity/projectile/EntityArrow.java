package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityArrow extends Entity implements IProjectile {
   private static final Predicate<Entity> field_184553_f;
   private static final DataParameter<Byte> field_184554_g;
   protected static final DataParameter<Optional<UUID>> field_212362_a;
   private int field_145791_d;
   private int field_145792_e;
   private int field_145789_f;
   @Nullable
   private IBlockState field_195056_av;
   protected boolean field_70254_i;
   protected int field_184552_b;
   public EntityArrow.PickupStatus field_70251_a;
   public int field_70249_b;
   public UUID field_70250_c;
   private int field_70252_j;
   private int field_70257_an;
   private double field_70255_ao;
   private int field_70256_ap;

   protected EntityArrow(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_145791_d = -1;
      this.field_145792_e = -1;
      this.field_145789_f = -1;
      this.field_70251_a = EntityArrow.PickupStatus.DISALLOWED;
      this.field_70255_ao = 2.0D;
      this.func_70105_a(0.5F, 0.5F);
   }

   protected EntityArrow(EntityType<?> var1, double var2, double var4, double var6, World var8) {
      this(var1, var8);
      this.func_70107_b(var2, var4, var6);
   }

   protected EntityArrow(EntityType<?> var1, EntityLivingBase var2, World var3) {
      this(var1, var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e() - 0.10000000149011612D, var2.field_70161_v, var3);
      this.func_212361_a(var2);
      if (var2 instanceof EntityPlayer) {
         this.field_70251_a = EntityArrow.PickupStatus.ALLOWED;
      }

   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b() * 10.0D;
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * func_184183_bd();
      return var1 < var3 * var3;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184554_g, (byte)0);
      this.field_70180_af.func_187214_a(field_212362_a, Optional.empty());
   }

   public void func_184547_a(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -MathHelper.func_76126_a(var3 * 0.017453292F) * MathHelper.func_76134_b(var2 * 0.017453292F);
      float var8 = -MathHelper.func_76126_a(var2 * 0.017453292F);
      float var9 = MathHelper.func_76134_b(var3 * 0.017453292F) * MathHelper.func_76134_b(var2 * 0.017453292F);
      this.func_70186_c((double)var7, (double)var8, (double)var9, var5, var6);
      this.field_70159_w += var1.field_70159_w;
      this.field_70179_y += var1.field_70179_y;
      if (!var1.field_70122_E) {
         this.field_70181_x += var1.field_70181_x;
      }

   }

   public void func_70186_c(double var1, double var3, double var5, float var7, float var8) {
      float var9 = MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5);
      var1 /= (double)var9;
      var3 /= (double)var9;
      var5 /= (double)var9;
      var1 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var3 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var5 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var1 *= (double)var7;
      var3 *= (double)var7;
      var5 *= (double)var7;
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      float var10 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
      this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
      this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var10) * 57.2957763671875D);
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
      this.field_70252_j = 0;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.func_70107_b(var1, var3, var5);
      this.func_70101_b(var7, var8);
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var7) * 57.2957763671875D);
         this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
         this.field_70127_C = this.field_70125_A;
         this.field_70126_B = this.field_70177_z;
         this.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_70252_j = 0;
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      boolean var1 = this.func_203047_q();
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);
         this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var2) * 57.2957763671875D);
         this.field_70126_B = this.field_70177_z;
         this.field_70127_C = this.field_70125_A;
      }

      BlockPos var13 = new BlockPos(this.field_145791_d, this.field_145792_e, this.field_145789_f);
      IBlockState var3 = this.field_70170_p.func_180495_p(var13);
      if (!var3.func_196958_f() && !var1) {
         VoxelShape var4 = var3.func_196952_d(this.field_70170_p, var13);
         if (!var4.func_197766_b()) {
            Iterator var5 = var4.func_197756_d().iterator();

            while(var5.hasNext()) {
               AxisAlignedBB var6 = (AxisAlignedBB)var5.next();
               if (var6.func_186670_a(var13).func_72318_a(new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v))) {
                  this.field_70254_i = true;
                  break;
               }
            }
         }
      }

      if (this.field_70249_b > 0) {
         --this.field_70249_b;
      }

      if (this.func_70026_G()) {
         this.func_70066_B();
      }

      if (this.field_70254_i && !var1) {
         if (this.field_195056_av != var3 && this.field_70170_p.func_195586_b((Entity)null, this.func_174813_aQ().func_186662_g(0.05D))) {
            this.field_70254_i = false;
            this.field_70159_w *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70181_x *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70179_y *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
            this.field_70252_j = 0;
            this.field_70257_an = 0;
         } else {
            this.func_203048_f();
         }

         ++this.field_184552_b;
      } else {
         this.field_184552_b = 0;
         ++this.field_70257_an;
         Vec3d var14 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         Vec3d var15 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
         RayTraceResult var16 = this.field_70170_p.func_200259_a(var14, var15, RayTraceFluidMode.NEVER, true, false);
         var14 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         var15 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
         if (var16 != null) {
            var15 = new Vec3d(var16.field_72307_f.field_72450_a, var16.field_72307_f.field_72448_b, var16.field_72307_f.field_72449_c);
         }

         Entity var7 = this.func_184551_a(var14, var15);
         if (var7 != null) {
            var16 = new RayTraceResult(var7);
         }

         if (var16 != null && var16.field_72308_g instanceof EntityPlayer) {
            EntityPlayer var8 = (EntityPlayer)var16.field_72308_g;
            Entity var9 = this.func_212360_k();
            if (var9 instanceof EntityPlayer && !((EntityPlayer)var9).func_96122_a(var8)) {
               var16 = null;
            }
         }

         if (var16 != null && !var1) {
            this.func_184549_a(var16);
            this.field_70160_al = true;
         }

         if (this.func_70241_g()) {
            for(int var17 = 0; var17 < 4; ++var17) {
               this.field_70170_p.func_195594_a(Particles.field_197614_g, this.field_70165_t + this.field_70159_w * (double)var17 / 4.0D, this.field_70163_u + this.field_70181_x * (double)var17 / 4.0D, this.field_70161_v + this.field_70179_y * (double)var17 / 4.0D, -this.field_70159_w, -this.field_70181_x + 0.2D, -this.field_70179_y);
            }
         }

         this.field_70165_t += this.field_70159_w;
         this.field_70163_u += this.field_70181_x;
         this.field_70161_v += this.field_70179_y;
         float var18 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var1) {
            this.field_70177_z = (float)(MathHelper.func_181159_b(-this.field_70159_w, -this.field_70179_y) * 57.2957763671875D);
         } else {
            this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);
         }

         for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var18) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
         }

         while(this.field_70125_A - this.field_70127_C >= 180.0F) {
            this.field_70127_C += 360.0F;
         }

         while(this.field_70177_z - this.field_70126_B < -180.0F) {
            this.field_70126_B -= 360.0F;
         }

         while(this.field_70177_z - this.field_70126_B >= 180.0F) {
            this.field_70126_B += 360.0F;
         }

         this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
         this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
         float var19 = 0.99F;
         float var10 = 0.05F;
         if (this.func_70090_H()) {
            for(int var11 = 0; var11 < 4; ++var11) {
               float var12 = 0.25F;
               this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t - this.field_70159_w * 0.25D, this.field_70163_u - this.field_70181_x * 0.25D, this.field_70161_v - this.field_70179_y * 0.25D, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }

            var19 = this.func_203044_p();
         }

         this.field_70159_w *= (double)var19;
         this.field_70181_x *= (double)var19;
         this.field_70179_y *= (double)var19;
         if (!this.func_189652_ae() && !var1) {
            this.field_70181_x -= 0.05000000074505806D;
         }

         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.func_145775_I();
      }
   }

   protected void func_203048_f() {
      ++this.field_70252_j;
      if (this.field_70252_j >= 1200) {
         this.func_70106_y();
      }

   }

   protected void func_184549_a(RayTraceResult var1) {
      if (var1.field_72308_g != null) {
         this.func_203046_b(var1);
      } else {
         BlockPos var2 = var1.func_178782_a();
         this.field_145791_d = var2.func_177958_n();
         this.field_145792_e = var2.func_177956_o();
         this.field_145789_f = var2.func_177952_p();
         IBlockState var3 = this.field_70170_p.func_180495_p(var2);
         this.field_195056_av = var3;
         this.field_70159_w = (double)((float)(var1.field_72307_f.field_72450_a - this.field_70165_t));
         this.field_70181_x = (double)((float)(var1.field_72307_f.field_72448_b - this.field_70163_u));
         this.field_70179_y = (double)((float)(var1.field_72307_f.field_72449_c - this.field_70161_v));
         float var4 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y) * 20.0F;
         this.field_70165_t -= this.field_70159_w / (double)var4;
         this.field_70163_u -= this.field_70181_x / (double)var4;
         this.field_70161_v -= this.field_70179_y / (double)var4;
         this.func_184185_a(this.func_203050_i(), 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
         this.field_70254_i = true;
         this.field_70249_b = 7;
         this.func_70243_d(false);
         if (!var3.func_196958_f()) {
            this.field_195056_av.func_196950_a(this.field_70170_p, var2, this);
         }
      }

   }

   protected void func_203046_b(RayTraceResult var1) {
      Entity var2 = var1.field_72308_g;
      float var3 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
      int var4 = MathHelper.func_76143_f((double)var3 * this.field_70255_ao);
      if (this.func_70241_g()) {
         var4 += this.field_70146_Z.nextInt(var4 / 2 + 2);
      }

      Entity var6 = this.func_212360_k();
      DamageSource var5;
      if (var6 == null) {
         var5 = DamageSource.func_76353_a(this, this);
      } else {
         var5 = DamageSource.func_76353_a(this, var6);
      }

      if (this.func_70027_ad() && !(var2 instanceof EntityEnderman)) {
         var2.func_70015_d(5);
      }

      if (var2.func_70097_a(var5, (float)var4)) {
         if (var2 instanceof EntityLivingBase) {
            EntityLivingBase var7 = (EntityLivingBase)var2;
            if (!this.field_70170_p.field_72995_K) {
               var7.func_85034_r(var7.func_85035_bI() + 1);
            }

            if (this.field_70256_ap > 0) {
               float var8 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
               if (var8 > 0.0F) {
                  var7.func_70024_g(this.field_70159_w * (double)this.field_70256_ap * 0.6000000238418579D / (double)var8, 0.1D, this.field_70179_y * (double)this.field_70256_ap * 0.6000000238418579D / (double)var8);
               }
            }

            if (var6 instanceof EntityLivingBase) {
               EnchantmentHelper.func_151384_a(var7, var6);
               EnchantmentHelper.func_151385_b((EntityLivingBase)var6, var7);
            }

            this.func_184548_a(var7);
            if (var6 != null && var7 != var6 && var7 instanceof EntityPlayer && var6 instanceof EntityPlayerMP) {
               ((EntityPlayerMP)var6).field_71135_a.func_147359_a(new SPacketChangeGameState(6, 0.0F));
            }
         }

         this.func_184185_a(SoundEvents.field_187731_t, 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
         if (!(var2 instanceof EntityEnderman)) {
            this.func_70106_y();
         }
      } else {
         this.field_70159_w *= -0.10000000149011612D;
         this.field_70181_x *= -0.10000000149011612D;
         this.field_70179_y *= -0.10000000149011612D;
         this.field_70177_z += 180.0F;
         this.field_70126_B += 180.0F;
         this.field_70257_an = 0;
         if (!this.field_70170_p.field_72995_K && this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y < 0.0010000000474974513D) {
            if (this.field_70251_a == EntityArrow.PickupStatus.ALLOWED) {
               this.func_70099_a(this.func_184550_j(), 0.1F);
            }

            this.func_70106_y();
         }
      }

   }

   protected SoundEvent func_203050_i() {
      return SoundEvents.field_187731_t;
   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      super.func_70091_d(var1, var2, var4, var6);
      if (this.field_70254_i) {
         this.field_145791_d = MathHelper.func_76128_c(this.field_70165_t);
         this.field_145792_e = MathHelper.func_76128_c(this.field_70163_u);
         this.field_145789_f = MathHelper.func_76128_c(this.field_70161_v);
      }

   }

   protected void func_184548_a(EntityLivingBase var1) {
   }

   @Nullable
   protected Entity func_184551_a(Vec3d var1, Vec3d var2) {
      Entity var3 = null;
      List var4 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ().func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_186662_g(1.0D), field_184553_f);
      double var5 = 0.0D;

      for(int var7 = 0; var7 < var4.size(); ++var7) {
         Entity var8 = (Entity)var4.get(var7);
         if (var8 != this.func_212360_k() || this.field_70257_an >= 5) {
            AxisAlignedBB var9 = var8.func_174813_aQ().func_186662_g(0.30000001192092896D);
            RayTraceResult var10 = var9.func_72327_a(var1, var2);
            if (var10 != null) {
               double var11 = var1.func_72436_e(var10.field_72307_f);
               if (var11 < var5 || var5 == 0.0D) {
                  var3 = var8;
                  var5 = var11;
               }
            }
         }
      }

      return var3;
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("xTile", this.field_145791_d);
      var1.func_74768_a("yTile", this.field_145792_e);
      var1.func_74768_a("zTile", this.field_145789_f);
      var1.func_74777_a("life", (short)this.field_70252_j);
      if (this.field_195056_av != null) {
         var1.func_74782_a("inBlockState", NBTUtil.func_190009_a(this.field_195056_av));
      }

      var1.func_74774_a("shake", (byte)this.field_70249_b);
      var1.func_74774_a("inGround", (byte)(this.field_70254_i ? 1 : 0));
      var1.func_74774_a("pickup", (byte)this.field_70251_a.ordinal());
      var1.func_74780_a("damage", this.field_70255_ao);
      var1.func_74757_a("crit", this.func_70241_g());
      if (this.field_70250_c != null) {
         var1.func_186854_a("OwnerUUID", this.field_70250_c);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_145791_d = var1.func_74762_e("xTile");
      this.field_145792_e = var1.func_74762_e("yTile");
      this.field_145789_f = var1.func_74762_e("zTile");
      this.field_70252_j = var1.func_74765_d("life");
      if (var1.func_150297_b("inBlockState", 10)) {
         this.field_195056_av = NBTUtil.func_190008_d(var1.func_74775_l("inBlockState"));
      }

      this.field_70249_b = var1.func_74771_c("shake") & 255;
      this.field_70254_i = var1.func_74771_c("inGround") == 1;
      if (var1.func_150297_b("damage", 99)) {
         this.field_70255_ao = var1.func_74769_h("damage");
      }

      if (var1.func_150297_b("pickup", 99)) {
         this.field_70251_a = EntityArrow.PickupStatus.func_188795_a(var1.func_74771_c("pickup"));
      } else if (var1.func_150297_b("player", 99)) {
         this.field_70251_a = var1.func_74767_n("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
      }

      this.func_70243_d(var1.func_74767_n("crit"));
      if (var1.func_186855_b("OwnerUUID")) {
         this.field_70250_c = var1.func_186857_a("OwnerUUID");
      }

   }

   public void func_212361_a(@Nullable Entity var1) {
      this.field_70250_c = var1 == null ? null : var1.func_110124_au();
   }

   @Nullable
   public Entity func_212360_k() {
      return this.field_70250_c != null && this.field_70170_p instanceof WorldServer ? ((WorldServer)this.field_70170_p).func_175733_a(this.field_70250_c) : null;
   }

   public void func_70100_b_(EntityPlayer var1) {
      if (!this.field_70170_p.field_72995_K && (this.field_70254_i || this.func_203047_q()) && this.field_70249_b <= 0) {
         boolean var2 = this.field_70251_a == EntityArrow.PickupStatus.ALLOWED || this.field_70251_a == EntityArrow.PickupStatus.CREATIVE_ONLY && var1.field_71075_bZ.field_75098_d || this.func_203047_q() && this.func_212360_k().func_110124_au() == var1.func_110124_au();
         if (this.field_70251_a == EntityArrow.PickupStatus.ALLOWED && !var1.field_71071_by.func_70441_a(this.func_184550_j())) {
            var2 = false;
         }

         if (var2) {
            var1.func_71001_a(this, 1);
            this.func_70106_y();
         }

      }
   }

   protected abstract ItemStack func_184550_j();

   protected boolean func_70041_e_() {
      return false;
   }

   public void func_70239_b(double var1) {
      this.field_70255_ao = var1;
   }

   public double func_70242_d() {
      return this.field_70255_ao;
   }

   public void func_70240_a(int var1) {
      this.field_70256_ap = var1;
   }

   public boolean func_70075_an() {
      return false;
   }

   public float func_70047_e() {
      return 0.0F;
   }

   public void func_70243_d(boolean var1) {
      this.func_203049_a(1, var1);
   }

   private void func_203049_a(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_184554_g);
      if (var2) {
         this.field_70180_af.func_187227_b(field_184554_g, (byte)(var3 | var1));
      } else {
         this.field_70180_af.func_187227_b(field_184554_g, (byte)(var3 & ~var1));
      }

   }

   public boolean func_70241_g() {
      byte var1 = (Byte)this.field_70180_af.func_187225_a(field_184554_g);
      return (var1 & 1) != 0;
   }

   public void func_190547_a(EntityLivingBase var1, float var2) {
      int var3 = EnchantmentHelper.func_185284_a(Enchantments.field_185309_u, var1);
      int var4 = EnchantmentHelper.func_185284_a(Enchantments.field_185310_v, var1);
      this.func_70239_b((double)(var2 * 2.0F) + this.field_70146_Z.nextGaussian() * 0.25D + (double)((float)this.field_70170_p.func_175659_aa().func_151525_a() * 0.11F));
      if (var3 > 0) {
         this.func_70239_b(this.func_70242_d() + (double)var3 * 0.5D + 0.5D);
      }

      if (var4 > 0) {
         this.func_70240_a(var4);
      }

      if (EnchantmentHelper.func_185284_a(Enchantments.field_185311_w, var1) > 0) {
         this.func_70015_d(100);
      }

   }

   protected float func_203044_p() {
      return 0.6F;
   }

   public void func_203045_n(boolean var1) {
      this.field_70145_X = var1;
      this.func_203049_a(2, var1);
   }

   public boolean func_203047_q() {
      if (!this.field_70170_p.field_72995_K) {
         return this.field_70145_X;
      } else {
         return ((Byte)this.field_70180_af.func_187225_a(field_184554_g) & 2) != 0;
      }
   }

   static {
      field_184553_f = EntitySelectors.field_180132_d.and(EntitySelectors.field_94557_a.and(Entity::func_70067_L));
      field_184554_g = EntityDataManager.func_187226_a(EntityArrow.class, DataSerializers.field_187191_a);
      field_212362_a = EntityDataManager.func_187226_a(EntityArrow.class, DataSerializers.field_187203_m);
   }

   public static enum PickupStatus {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      private PickupStatus() {
      }

      public static EntityArrow.PickupStatus func_188795_a(int var0) {
         if (var0 < 0 || var0 > values().length) {
            var0 = 0;
         }

         return values()[var0];
      }
   }
}
