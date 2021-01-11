package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   public EntityMinecartEmpty(World var1) {
      super(var1);
   }

   public EntityMinecartEmpty(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   public boolean func_130002_c(EntityPlayer var1) {
      if (this.field_70153_n != null && this.field_70153_n instanceof EntityPlayer && this.field_70153_n != var1) {
         return true;
      } else if (this.field_70153_n != null && this.field_70153_n != var1) {
         return false;
      } else {
         if (!this.field_70170_p.field_72995_K) {
            var1.func_70078_a(this);
         }

         return true;
      }
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4) {
         if (this.field_70153_n != null) {
            this.field_70153_n.func_70078_a((Entity)null);
         }

         if (this.func_70496_j() == 0) {
            this.func_70494_i(-this.func_70493_k());
            this.func_70497_h(10);
            this.func_70492_c(50.0F);
            this.func_70018_K();
         }
      }

   }

   public EntityMinecart.EnumMinecartType func_180456_s() {
      return EntityMinecart.EnumMinecartType.RIDEABLE;
   }
}
