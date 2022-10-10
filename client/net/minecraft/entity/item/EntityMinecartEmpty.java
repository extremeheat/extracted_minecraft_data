package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
   public EntityMinecartEmpty(World var1) {
      super(EntityType.field_200772_L, var1);
   }

   public EntityMinecartEmpty(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200772_L, var1, var2, var4, var6);
   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (var1.func_70093_af()) {
         return false;
      } else if (this.func_184207_aI()) {
         return true;
      } else {
         if (!this.field_70170_p.field_72995_K) {
            var1.func_184220_m(this);
         }

         return true;
      }
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4) {
         if (this.func_184207_aI()) {
            this.func_184226_ay();
         }

         if (this.func_70496_j() == 0) {
            this.func_70494_i(-this.func_70493_k());
            this.func_70497_h(10);
            this.func_70492_c(50.0F);
            this.func_70018_K();
         }
      }

   }

   public EntityMinecart.Type func_184264_v() {
      return EntityMinecart.Type.RIDEABLE;
   }
}
