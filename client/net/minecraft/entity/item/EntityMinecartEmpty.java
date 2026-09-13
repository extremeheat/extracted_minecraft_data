package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   public EntityMinecartEmpty(World var1) {
      super(var1);
   }

   public EntityMinecartEmpty(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
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

   @Override
   public int func_94087_l() {
      return 0;
   }
}
