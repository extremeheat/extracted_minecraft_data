package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   private float field_98056_d = -1.0F;
   private float field_98057_e;

   public EntityAgeable(World var1) {
      super(var1);
   }

   public abstract EntityAgeable func_90011_a(EntityAgeable var1);

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && var2.func_77973_b() == Items.field_151063_bx) {
         if (!this.field_70170_p.field_72995_K) {
            Class var3 = EntityList.func_90035_a(var2.func_77960_j());
            if (var3 != null && var3.isAssignableFrom(this.getClass())) {
               EntityAgeable var4 = this.func_90011_a(this);
               if (var4 != null) {
                  var4.func_70873_a(-24000);
                  var4.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0F, 0.0F);
                  this.field_70170_p.func_72838_d(var4);
                  if (var2.func_82837_s()) {
                     var4.func_94058_c(var2.func_82833_r());
                  }

                  if (!var1.field_71075_bZ.field_75098_d) {
                     --var2.field_77994_a;
                     if (var2.field_77994_a <= 0) {
                        var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, null);
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(12, new Integer(0));
   }

   public int func_70874_b() {
      return this.field_70180_af.func_75679_c(12);
   }

   public void func_110195_a(int var1) {
      int var2 = this.func_70874_b();
      var2 += var1 * 20;
      if (var2 > 0) {
         var2 = 0;
      }

      this.func_70873_a(var2);
   }

   public void func_70873_a(int var1) {
      this.field_70180_af.func_75692_b(12, var1);
      this.func_98054_a(this.func_70631_g_());
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Age", this.func_70874_b());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70873_a(var1.func_74762_e("Age"));
   }

   @Override
   public void func_70636_d() {
      super.func_70636_d();
      if (this.field_70170_p.field_72995_K) {
         this.func_98054_a(this.func_70631_g_());
      } else {
         int var1 = this.func_70874_b();
         if (var1 < 0) {
            this.func_70873_a(++var1);
         } else if (var1 > 0) {
            this.func_70873_a(--var1);
         }
      }
   }

   @Override
   public boolean func_70631_g_() {
      return this.func_70874_b() < 0;
   }

   public void func_98054_a(boolean var1) {
      this.func_98055_j(var1 ? 0.5F : 1.0F);
   }

   @Override
   protected final void func_70105_a(float var1, float var2) {
      boolean var3 = this.field_98056_d > 0.0F;
      this.field_98056_d = var1;
      this.field_98057_e = var2;
      if (!var3) {
         this.func_98055_j(1.0F);
      }
   }

   protected final void func_98055_j(float var1) {
      super.func_70105_a(this.field_98056_d * var1, this.field_98057_e * var1);
   }
}
