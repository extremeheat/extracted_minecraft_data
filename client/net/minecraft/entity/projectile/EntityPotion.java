package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable {
   private ItemStack field_70197_d;

   public EntityPotion(World var1) {
      super(var1);
   }

   public EntityPotion(World var1, EntityLivingBase var2, int var3) {
      this(var1, var2, new ItemStack(Items.field_151068_bn, 1, var3));
   }

   public EntityPotion(World var1, EntityLivingBase var2, ItemStack var3) {
      super(var1, var2);
      this.field_70197_d = var3;
   }

   public EntityPotion(World var1, double var2, double var4, double var6, int var8) {
      this(var1, var2, var4, var6, new ItemStack(Items.field_151068_bn, 1, var8));
   }

   public EntityPotion(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1, var2, var4, var6);
      this.field_70197_d = var8;
   }

   protected float func_70185_h() {
      return 0.05F;
   }

   protected float func_70182_d() {
      return 0.5F;
   }

   protected float func_70183_g() {
      return -20.0F;
   }

   public void func_82340_a(int var1) {
      if (this.field_70197_d == null) {
         this.field_70197_d = new ItemStack(Items.field_151068_bn, 1, 0);
      }

      this.field_70197_d.func_77964_b(var1);
   }

   public int func_70196_i() {
      if (this.field_70197_d == null) {
         this.field_70197_d = new ItemStack(Items.field_151068_bn, 1, 0);
      }

      return this.field_70197_d.func_77960_j();
   }

   protected void func_70184_a(MovingObjectPosition var1) {
      if (!this.field_70170_p.field_72995_K) {
         List var2 = Items.field_151068_bn.func_77832_l(this.field_70197_d);
         if (var2 != null && !var2.isEmpty()) {
            AxisAlignedBB var3 = this.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D);
            List var4 = this.field_70170_p.func_72872_a(EntityLivingBase.class, var3);
            if (!var4.isEmpty()) {
               Iterator var5 = var4.iterator();

               label45:
               while(true) {
                  EntityLivingBase var6;
                  double var7;
                  do {
                     if (!var5.hasNext()) {
                        break label45;
                     }

                     var6 = (EntityLivingBase)var5.next();
                     var7 = this.func_70068_e(var6);
                  } while(var7 >= 16.0D);

                  double var9 = 1.0D - Math.sqrt(var7) / 4.0D;
                  if (var6 == var1.field_72308_g) {
                     var9 = 1.0D;
                  }

                  Iterator var11 = var2.iterator();

                  while(var11.hasNext()) {
                     PotionEffect var12 = (PotionEffect)var11.next();
                     int var13 = var12.func_76456_a();
                     if (Potion.field_76425_a[var13].func_76403_b()) {
                        Potion.field_76425_a[var13].func_180793_a(this, this.func_85052_h(), var6, var12.func_76458_c(), var9);
                     } else {
                        int var14 = (int)(var9 * (double)var12.func_76459_b() + 0.5D);
                        if (var14 > 20) {
                           var6.func_70690_d(new PotionEffect(var13, var14, var12.func_76458_c()));
                        }
                     }
                  }
               }
            }
         }

         this.field_70170_p.func_175718_b(2002, new BlockPos(this), this.func_70196_i());
         this.func_70106_y();
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("Potion", 10)) {
         this.field_70197_d = ItemStack.func_77949_a(var1.func_74775_l("Potion"));
      } else {
         this.func_82340_a(var1.func_74762_e("potionValue"));
      }

      if (this.field_70197_d == null) {
         this.func_70106_y();
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_70197_d != null) {
         var1.func_74782_a("Potion", this.field_70197_d.func_77955_b(new NBTTagCompound()));
      }

   }
}
