package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityFishHook extends Entity {
   private static final DataParameter<Integer> field_184528_c;
   private boolean field_146051_au;
   private int field_146049_av;
   private EntityPlayer field_146042_b;
   private int field_146047_aw;
   private int field_146045_ax;
   private int field_146040_ay;
   private int field_146038_az;
   private float field_146054_aA;
   public Entity field_146043_c;
   private EntityFishHook.State field_190627_av;
   private int field_191518_aw;
   private int field_191519_ax;

   private EntityFishHook(World var1) {
      super(EntityType.field_200730_aI, var1);
      this.field_190627_av = EntityFishHook.State.FLYING;
   }

   public EntityFishHook(World var1, EntityPlayer var2, double var3, double var5, double var7) {
      this(var1);
      this.func_190626_a(var2);
      this.func_70107_b(var3, var5, var7);
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
   }

   public EntityFishHook(World var1, EntityPlayer var2) {
      this(var1);
      this.func_190626_a(var2);
      this.func_190620_n();
   }

   private void func_190626_a(EntityPlayer var1) {
      this.func_70105_a(0.25F, 0.25F);
      this.field_70158_ak = true;
      this.field_146042_b = var1;
      this.field_146042_b.field_71104_cf = this;
   }

   public void func_191516_a(int var1) {
      this.field_191519_ax = var1;
   }

   public void func_191517_b(int var1) {
      this.field_191518_aw = var1;
   }

   private void func_190620_n() {
      float var1 = this.field_146042_b.field_70125_A;
      float var2 = this.field_146042_b.field_70177_z;
      float var3 = MathHelper.func_76134_b(-var2 * 0.017453292F - 3.1415927F);
      float var4 = MathHelper.func_76126_a(-var2 * 0.017453292F - 3.1415927F);
      float var5 = -MathHelper.func_76134_b(-var1 * 0.017453292F);
      float var6 = MathHelper.func_76126_a(-var1 * 0.017453292F);
      double var7 = this.field_146042_b.field_70165_t - (double)var4 * 0.3D;
      double var9 = this.field_146042_b.field_70163_u + (double)this.field_146042_b.func_70047_e();
      double var11 = this.field_146042_b.field_70161_v - (double)var3 * 0.3D;
      this.func_70012_b(var7, var9, var11, var2, var1);
      this.field_70159_w = (double)(-var4);
      this.field_70181_x = (double)MathHelper.func_76131_a(-(var6 / var5), -5.0F, 5.0F);
      this.field_70179_y = (double)(-var3);
      float var13 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
      this.field_70159_w *= 0.6D / (double)var13 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
      this.field_70181_x *= 0.6D / (double)var13 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
      this.field_70179_y *= 0.6D / (double)var13 + 0.5D + this.field_70146_Z.nextGaussian() * 0.0045D;
      float var14 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);
      this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var14) * 57.2957763671875D);
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(field_184528_c, 0);
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184528_c.equals(var1)) {
         int var2 = (Integer)this.func_184212_Q().func_187225_a(field_184528_c);
         this.field_146043_c = var2 > 0 ? this.field_70170_p.func_73045_a(var2 - 1) : null;
      }

      super.func_184206_a(var1);
   }

   public boolean func_70112_a(double var1) {
      double var3 = 64.0D;
      return var1 < 4096.0D;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_146042_b == null) {
         this.func_70106_y();
      } else if (this.field_70170_p.field_72995_K || !this.func_190625_o()) {
         if (this.field_146051_au) {
            ++this.field_146049_av;
            if (this.field_146049_av >= 1200) {
               this.func_70106_y();
               return;
            }
         }

         float var1 = 0.0F;
         BlockPos var2 = new BlockPos(this);
         IFluidState var3 = this.field_70170_p.func_204610_c(var2);
         if (var3.func_206884_a(FluidTags.field_206959_a)) {
            var1 = var3.func_206885_f();
         }

         double var4;
         if (this.field_190627_av == EntityFishHook.State.FLYING) {
            if (this.field_146043_c != null) {
               this.field_70159_w = 0.0D;
               this.field_70181_x = 0.0D;
               this.field_70179_y = 0.0D;
               this.field_190627_av = EntityFishHook.State.HOOKED_IN_ENTITY;
               return;
            }

            if (var1 > 0.0F) {
               this.field_70159_w *= 0.3D;
               this.field_70181_x *= 0.2D;
               this.field_70179_y *= 0.3D;
               this.field_190627_av = EntityFishHook.State.BOBBING;
               return;
            }

            if (!this.field_70170_p.field_72995_K) {
               this.func_190624_r();
            }

            if (!this.field_146051_au && !this.field_70122_E && !this.field_70123_F) {
               ++this.field_146047_aw;
            } else {
               this.field_146047_aw = 0;
               this.field_70159_w = 0.0D;
               this.field_70181_x = 0.0D;
               this.field_70179_y = 0.0D;
            }
         } else {
            if (this.field_190627_av == EntityFishHook.State.HOOKED_IN_ENTITY) {
               if (this.field_146043_c != null) {
                  if (this.field_146043_c.field_70128_L) {
                     this.field_146043_c = null;
                     this.field_190627_av = EntityFishHook.State.FLYING;
                  } else {
                     this.field_70165_t = this.field_146043_c.field_70165_t;
                     double var10002 = (double)this.field_146043_c.field_70131_O;
                     this.field_70163_u = this.field_146043_c.func_174813_aQ().field_72338_b + var10002 * 0.8D;
                     this.field_70161_v = this.field_146043_c.field_70161_v;
                     this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                  }
               }

               return;
            }

            if (this.field_190627_av == EntityFishHook.State.BOBBING) {
               this.field_70159_w *= 0.9D;
               this.field_70179_y *= 0.9D;
               var4 = this.field_70163_u + this.field_70181_x - (double)var2.func_177956_o() - (double)var1;
               if (Math.abs(var4) < 0.01D) {
                  var4 += Math.signum(var4) * 0.1D;
               }

               this.field_70181_x -= var4 * (double)this.field_70146_Z.nextFloat() * 0.2D;
               if (!this.field_70170_p.field_72995_K && var1 > 0.0F) {
                  this.func_190621_a(var2);
               }
            }
         }

         if (!var3.func_206884_a(FluidTags.field_206959_a)) {
            this.field_70181_x -= 0.03D;
         }

         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.func_190623_q();
         var4 = 0.92D;
         this.field_70159_w *= 0.92D;
         this.field_70181_x *= 0.92D;
         this.field_70179_y *= 0.92D;
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }
   }

   private boolean func_190625_o() {
      ItemStack var1 = this.field_146042_b.func_184614_ca();
      ItemStack var2 = this.field_146042_b.func_184592_cb();
      boolean var3 = var1.func_77973_b() == Items.field_151112_aM;
      boolean var4 = var2.func_77973_b() == Items.field_151112_aM;
      if (!this.field_146042_b.field_70128_L && this.field_146042_b.func_70089_S() && (var3 || var4) && this.func_70068_e(this.field_146042_b) <= 1024.0D) {
         return false;
      } else {
         this.func_70106_y();
         return true;
      }
   }

   private void func_190623_q() {
      float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var1) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
   }

   private void func_190624_r() {
      Vec3d var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      Vec3d var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      RayTraceResult var3 = this.field_70170_p.func_200259_a(var1, var2, RayTraceFluidMode.NEVER, true, false);
      var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      if (var3 != null) {
         var2 = new Vec3d(var3.field_72307_f.field_72450_a, var3.field_72307_f.field_72448_b, var3.field_72307_f.field_72449_c);
      }

      Entity var4 = null;
      List var5 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_186662_g(1.0D));
      double var6 = 0.0D;
      Iterator var8 = var5.iterator();

      while(true) {
         Entity var9;
         double var12;
         do {
            RayTraceResult var11;
            do {
               do {
                  do {
                     if (!var8.hasNext()) {
                        if (var4 != null) {
                           var3 = new RayTraceResult(var4);
                        }

                        if (var3 != null && var3.field_72313_a != RayTraceResult.Type.MISS) {
                           if (var3.field_72313_a == RayTraceResult.Type.ENTITY) {
                              this.field_146043_c = var3.field_72308_g;
                              this.func_190622_s();
                           } else {
                              this.field_146051_au = true;
                           }
                        }

                        return;
                     }

                     var9 = (Entity)var8.next();
                  } while(!this.func_189739_a(var9));
               } while(var9 == this.field_146042_b && this.field_146047_aw < 5);

               AxisAlignedBB var10 = var9.func_174813_aQ().func_186662_g(0.30000001192092896D);
               var11 = var10.func_72327_a(var1, var2);
            } while(var11 == null);

            var12 = var1.func_72436_e(var11.field_72307_f);
         } while(var12 >= var6 && var6 != 0.0D);

         var4 = var9;
         var6 = var12;
      }
   }

   private void func_190622_s() {
      this.func_184212_Q().func_187227_b(field_184528_c, this.field_146043_c.func_145782_y() + 1);
   }

   private void func_190621_a(BlockPos var1) {
      WorldServer var2 = (WorldServer)this.field_70170_p;
      int var3 = 1;
      BlockPos var4 = var1.func_177984_a();
      if (this.field_70146_Z.nextFloat() < 0.25F && this.field_70170_p.func_175727_C(var4)) {
         ++var3;
      }

      if (this.field_70146_Z.nextFloat() < 0.5F && !this.field_70170_p.func_175678_i(var4)) {
         --var3;
      }

      if (this.field_146045_ax > 0) {
         --this.field_146045_ax;
         if (this.field_146045_ax <= 0) {
            this.field_146040_ay = 0;
            this.field_146038_az = 0;
         } else {
            this.field_70181_x -= 0.2D * (double)this.field_70146_Z.nextFloat() * (double)this.field_70146_Z.nextFloat();
         }
      } else {
         float var5;
         float var6;
         float var7;
         double var8;
         double var10;
         double var12;
         Block var14;
         if (this.field_146038_az > 0) {
            this.field_146038_az -= var3;
            if (this.field_146038_az > 0) {
               this.field_146054_aA = (float)((double)this.field_146054_aA + this.field_70146_Z.nextGaussian() * 4.0D);
               var5 = this.field_146054_aA * 0.017453292F;
               var6 = MathHelper.func_76126_a(var5);
               var7 = MathHelper.func_76134_b(var5);
               var8 = this.field_70165_t + (double)(var6 * (float)this.field_146038_az * 0.1F);
               var10 = (double)((float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) + 1.0F);
               var12 = this.field_70161_v + (double)(var7 * (float)this.field_146038_az * 0.1F);
               var14 = var2.func_180495_p(new BlockPos(var8, var10 - 1.0D, var12)).func_177230_c();
               if (var14 == Blocks.field_150355_j) {
                  if (this.field_70146_Z.nextFloat() < 0.15F) {
                     var2.func_195598_a(Particles.field_197612_e, var8, var10 - 0.10000000149011612D, var12, 1, (double)var6, 0.1D, (double)var7, 0.0D);
                  }

                  float var15 = var6 * 0.04F;
                  float var16 = var7 * 0.04F;
                  var2.func_195598_a(Particles.field_197630_w, var8, var10, var12, 0, (double)var16, 0.01D, (double)(-var15), 1.0D);
                  var2.func_195598_a(Particles.field_197630_w, var8, var10, var12, 0, (double)(-var16), 0.01D, (double)var15, 1.0D);
               }
            } else {
               this.field_70181_x = (double)(-0.4F * MathHelper.func_151240_a(this.field_70146_Z, 0.6F, 1.0F));
               this.func_184185_a(SoundEvents.field_187609_F, 0.25F, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
               double var17 = this.func_174813_aQ().field_72338_b + 0.5D;
               var2.func_195598_a(Particles.field_197612_e, this.field_70165_t, var17, this.field_70161_v, (int)(1.0F + this.field_70130_N * 20.0F), (double)this.field_70130_N, 0.0D, (double)this.field_70130_N, 0.20000000298023224D);
               var2.func_195598_a(Particles.field_197630_w, this.field_70165_t, var17, this.field_70161_v, (int)(1.0F + this.field_70130_N * 20.0F), (double)this.field_70130_N, 0.0D, (double)this.field_70130_N, 0.20000000298023224D);
               this.field_146045_ax = MathHelper.func_76136_a(this.field_70146_Z, 20, 40);
            }
         } else if (this.field_146040_ay > 0) {
            this.field_146040_ay -= var3;
            var5 = 0.15F;
            if (this.field_146040_ay < 20) {
               var5 = (float)((double)var5 + (double)(20 - this.field_146040_ay) * 0.05D);
            } else if (this.field_146040_ay < 40) {
               var5 = (float)((double)var5 + (double)(40 - this.field_146040_ay) * 0.02D);
            } else if (this.field_146040_ay < 60) {
               var5 = (float)((double)var5 + (double)(60 - this.field_146040_ay) * 0.01D);
            }

            if (this.field_70146_Z.nextFloat() < var5) {
               var6 = MathHelper.func_151240_a(this.field_70146_Z, 0.0F, 360.0F) * 0.017453292F;
               var7 = MathHelper.func_151240_a(this.field_70146_Z, 25.0F, 60.0F);
               var8 = this.field_70165_t + (double)(MathHelper.func_76126_a(var6) * var7 * 0.1F);
               var10 = (double)((float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) + 1.0F);
               var12 = this.field_70161_v + (double)(MathHelper.func_76134_b(var6) * var7 * 0.1F);
               var14 = var2.func_180495_p(new BlockPos((int)var8, (int)var10 - 1, (int)var12)).func_177230_c();
               if (var14 == Blocks.field_150355_j) {
                  var2.func_195598_a(Particles.field_197606_Q, var8, var10, var12, 2 + this.field_70146_Z.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
               }
            }

            if (this.field_146040_ay <= 0) {
               this.field_146054_aA = MathHelper.func_151240_a(this.field_70146_Z, 0.0F, 360.0F);
               this.field_146038_az = MathHelper.func_76136_a(this.field_70146_Z, 20, 80);
            }
         } else {
            this.field_146040_ay = MathHelper.func_76136_a(this.field_70146_Z, 100, 600);
            this.field_146040_ay -= this.field_191519_ax * 20 * 5;
         }
      }

   }

   protected boolean func_189739_a(Entity var1) {
      return var1.func_70067_L() || var1 instanceof EntityItem;
   }

   public void func_70014_b(NBTTagCompound var1) {
   }

   public void func_70037_a(NBTTagCompound var1) {
   }

   public int func_146034_e(ItemStack var1) {
      if (!this.field_70170_p.field_72995_K && this.field_146042_b != null) {
         int var2 = 0;
         if (this.field_146043_c != null) {
            this.func_184527_k();
            CriteriaTriggers.field_204811_D.func_204820_a((EntityPlayerMP)this.field_146042_b, var1, this, Collections.emptyList());
            this.field_70170_p.func_72960_a(this, (byte)31);
            var2 = this.field_146043_c instanceof EntityItem ? 3 : 5;
         } else if (this.field_146045_ax > 0) {
            LootContext.Builder var3 = (new LootContext.Builder((WorldServer)this.field_70170_p)).func_204313_a(new BlockPos(this));
            var3.func_186469_a((float)this.field_191518_aw + this.field_146042_b.func_184817_da());
            List var4 = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(LootTableList.field_186387_al).func_186462_a(this.field_70146_Z, var3.func_186471_a());
            CriteriaTriggers.field_204811_D.func_204820_a((EntityPlayerMP)this.field_146042_b, var1, this, var4);
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               ItemStack var6 = (ItemStack)var5.next();
               EntityItem var7 = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var6);
               double var8 = this.field_146042_b.field_70165_t - this.field_70165_t;
               double var10 = this.field_146042_b.field_70163_u - this.field_70163_u;
               double var12 = this.field_146042_b.field_70161_v - this.field_70161_v;
               double var14 = (double)MathHelper.func_76133_a(var8 * var8 + var10 * var10 + var12 * var12);
               double var16 = 0.1D;
               var7.field_70159_w = var8 * 0.1D;
               var7.field_70181_x = var10 * 0.1D + (double)MathHelper.func_76133_a(var14) * 0.08D;
               var7.field_70179_y = var12 * 0.1D;
               this.field_70170_p.func_72838_d(var7);
               this.field_146042_b.field_70170_p.func_72838_d(new EntityXPOrb(this.field_146042_b.field_70170_p, this.field_146042_b.field_70165_t, this.field_146042_b.field_70163_u + 0.5D, this.field_146042_b.field_70161_v + 0.5D, this.field_70146_Z.nextInt(6) + 1));
               if (var6.func_77973_b().func_206844_a(ItemTags.field_206964_G)) {
                  this.field_146042_b.func_195067_a(StatList.field_188071_E, 1);
               }
            }

            var2 = 1;
         }

         if (this.field_146051_au) {
            var2 = 2;
         }

         this.func_70106_y();
         return var2;
      } else {
         return 0;
      }
   }

   public void func_70103_a(byte var1) {
      if (var1 == 31 && this.field_70170_p.field_72995_K && this.field_146043_c instanceof EntityPlayer && ((EntityPlayer)this.field_146043_c).func_175144_cb()) {
         this.func_184527_k();
      }

      super.func_70103_a(var1);
   }

   protected void func_184527_k() {
      if (this.field_146042_b != null) {
         double var1 = this.field_146042_b.field_70165_t - this.field_70165_t;
         double var3 = this.field_146042_b.field_70163_u - this.field_70163_u;
         double var5 = this.field_146042_b.field_70161_v - this.field_70161_v;
         double var7 = 0.1D;
         Entity var10000 = this.field_146043_c;
         var10000.field_70159_w += var1 * 0.1D;
         var10000 = this.field_146043_c;
         var10000.field_70181_x += var3 * 0.1D;
         var10000 = this.field_146043_c;
         var10000.field_70179_y += var5 * 0.1D;
      }
   }

   protected boolean func_70041_e_() {
      return false;
   }

   public void func_70106_y() {
      super.func_70106_y();
      if (this.field_146042_b != null) {
         this.field_146042_b.field_71104_cf = null;
      }

   }

   public EntityPlayer func_190619_l() {
      return this.field_146042_b;
   }

   public boolean func_184222_aU() {
      return false;
   }

   static {
      field_184528_c = EntityDataManager.func_187226_a(EntityFishHook.class, DataSerializers.field_187192_b);
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      private State() {
      }
   }
}
