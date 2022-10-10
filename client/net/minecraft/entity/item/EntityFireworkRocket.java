package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity {
   private static final DataParameter<ItemStack> field_184566_a;
   private static final DataParameter<Integer> field_191512_b;
   private int field_92056_a;
   private int field_92055_b;
   private EntityLivingBase field_191513_e;

   public EntityFireworkRocket(World var1) {
      super(EntityType.field_200810_x, var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184566_a, ItemStack.field_190927_a);
      this.field_70180_af.func_187214_a(field_191512_b, 0);
   }

   public boolean func_70112_a(double var1) {
      return var1 < 4096.0D && !this.func_191511_j();
   }

   public boolean func_145770_h(double var1, double var3, double var5) {
      return super.func_145770_h(var1, var3, var5) && !this.func_191511_j();
   }

   public EntityFireworkRocket(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.field_200810_x, var1);
      this.field_92056_a = 0;
      this.func_70105_a(0.25F, 0.25F);
      this.func_70107_b(var2, var4, var6);
      int var9 = 1;
      if (!var8.func_190926_b() && var8.func_77942_o()) {
         this.field_70180_af.func_187227_b(field_184566_a, var8.func_77946_l());
         var9 += var8.func_190925_c("Fireworks").func_74771_c("Flight");
      }

      this.field_70159_w = this.field_70146_Z.nextGaussian() * 0.001D;
      this.field_70179_y = this.field_70146_Z.nextGaussian() * 0.001D;
      this.field_70181_x = 0.05D;
      this.field_92055_b = 10 * var9 + this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(7);
   }

   public EntityFireworkRocket(World var1, ItemStack var2, EntityLivingBase var3) {
      this(var1, var3.field_70165_t, var3.field_70163_u, var3.field_70161_v, var2);
      this.field_70180_af.func_187227_b(field_191512_b, var3.func_145782_y());
      this.field_191513_e = var3;
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
         this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var7) * 57.2957763671875D);
         this.field_70126_B = this.field_70177_z;
         this.field_70127_C = this.field_70125_A;
      }

   }

   public void func_70071_h_() {
      this.field_70142_S = this.field_70165_t;
      this.field_70137_T = this.field_70163_u;
      this.field_70136_U = this.field_70161_v;
      super.func_70071_h_();
      if (this.func_191511_j()) {
         if (this.field_191513_e == null) {
            Entity var1 = this.field_70170_p.func_73045_a((Integer)this.field_70180_af.func_187225_a(field_191512_b));
            if (var1 instanceof EntityLivingBase) {
               this.field_191513_e = (EntityLivingBase)var1;
            }
         }

         if (this.field_191513_e != null) {
            if (this.field_191513_e.func_184613_cA()) {
               Vec3d var6 = this.field_191513_e.func_70040_Z();
               double var2 = 1.5D;
               double var4 = 0.1D;
               EntityLivingBase var10000 = this.field_191513_e;
               var10000.field_70159_w += var6.field_72450_a * 0.1D + (var6.field_72450_a * 1.5D - this.field_191513_e.field_70159_w) * 0.5D;
               var10000 = this.field_191513_e;
               var10000.field_70181_x += var6.field_72448_b * 0.1D + (var6.field_72448_b * 1.5D - this.field_191513_e.field_70181_x) * 0.5D;
               var10000 = this.field_191513_e;
               var10000.field_70179_y += var6.field_72449_c * 0.1D + (var6.field_72449_c * 1.5D - this.field_191513_e.field_70179_y) * 0.5D;
            }

            this.func_70107_b(this.field_191513_e.field_70165_t, this.field_191513_e.field_70163_u, this.field_191513_e.field_70161_v);
            this.field_70159_w = this.field_191513_e.field_70159_w;
            this.field_70181_x = this.field_191513_e.field_70181_x;
            this.field_70179_y = this.field_191513_e.field_70179_y;
         }
      } else {
         this.field_70159_w *= 1.15D;
         this.field_70179_y *= 1.15D;
         this.field_70181_x += 0.04D;
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      }

      float var7 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var7) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
      if (this.field_92056_a == 0 && !this.func_174814_R()) {
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187631_bo, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.field_92056_a;
      if (this.field_70170_p.field_72995_K && this.field_92056_a % 2 < 2) {
         this.field_70170_p.func_195594_a(Particles.field_197629_v, this.field_70165_t, this.field_70163_u - 0.3D, this.field_70161_v, this.field_70146_Z.nextGaussian() * 0.05D, -this.field_70181_x * 0.5D, this.field_70146_Z.nextGaussian() * 0.05D);
      }

      if (!this.field_70170_p.field_72995_K && this.field_92056_a > this.field_92055_b) {
         this.field_70170_p.func_72960_a(this, (byte)17);
         this.func_191510_k();
         this.func_70106_y();
      }

   }

   private void func_191510_k() {
      float var1 = 0.0F;
      ItemStack var2 = (ItemStack)this.field_70180_af.func_187225_a(field_184566_a);
      NBTTagCompound var3 = var2.func_190926_b() ? null : var2.func_179543_a("Fireworks");
      NBTTagList var4 = var3 != null ? var3.func_150295_c("Explosions", 10) : null;
      if (var4 != null && !var4.isEmpty()) {
         var1 = (float)(5 + var4.size() * 2);
      }

      if (var1 > 0.0F) {
         if (this.field_191513_e != null) {
            this.field_191513_e.func_70097_a(DamageSource.field_191552_t, (float)(5 + var4.size() * 2));
         }

         double var5 = 5.0D;
         Vec3d var7 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         List var8 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_186662_g(5.0D));
         Iterator var9 = var8.iterator();

         while(true) {
            EntityLivingBase var10;
            do {
               do {
                  if (!var9.hasNext()) {
                     return;
                  }

                  var10 = (EntityLivingBase)var9.next();
               } while(var10 == this.field_191513_e);
            } while(this.func_70068_e(var10) > 25.0D);

            boolean var11 = false;

            for(int var12 = 0; var12 < 2; ++var12) {
               RayTraceResult var13 = this.field_70170_p.func_200259_a(var7, new Vec3d(var10.field_70165_t, var10.field_70163_u + (double)var10.field_70131_O * 0.5D * (double)var12, var10.field_70161_v), RayTraceFluidMode.NEVER, true, false);
               if (var13 == null || var13.field_72313_a == RayTraceResult.Type.MISS) {
                  var11 = true;
                  break;
               }
            }

            if (var11) {
               float var14 = var1 * (float)Math.sqrt((5.0D - (double)this.func_70032_d(var10)) / 5.0D);
               var10.func_70097_a(DamageSource.field_191552_t, var14);
            }
         }
      }
   }

   public boolean func_191511_j() {
      return (Integer)this.field_70180_af.func_187225_a(field_191512_b) > 0;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 17 && this.field_70170_p.field_72995_K) {
         ItemStack var2 = (ItemStack)this.field_70180_af.func_187225_a(field_184566_a);
         NBTTagCompound var3 = var2.func_190926_b() ? null : var2.func_179543_a("Fireworks");
         this.field_70170_p.func_92088_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70159_w, this.field_70181_x, this.field_70179_y, var3);
      }

      super.func_70103_a(var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("Life", this.field_92056_a);
      var1.func_74768_a("LifeTime", this.field_92055_b);
      ItemStack var2 = (ItemStack)this.field_70180_af.func_187225_a(field_184566_a);
      if (!var2.func_190926_b()) {
         var1.func_74782_a("FireworksItem", var2.func_77955_b(new NBTTagCompound()));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_92056_a = var1.func_74762_e("Life");
      this.field_92055_b = var1.func_74762_e("LifeTime");
      ItemStack var2 = ItemStack.func_199557_a(var1.func_74775_l("FireworksItem"));
      if (!var2.func_190926_b()) {
         this.field_70180_af.func_187227_b(field_184566_a, var2);
      }

   }

   public boolean func_70075_an() {
      return false;
   }

   static {
      field_184566_a = EntityDataManager.func_187226_a(EntityFireworkRocket.class, DataSerializers.field_187196_f);
      field_191512_b = EntityDataManager.func_187226_a(EntityFireworkRocket.class, DataSerializers.field_187192_b);
   }
}
