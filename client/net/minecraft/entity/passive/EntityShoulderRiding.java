package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityShoulderRiding extends EntityTameable {
   private int field_191996_bB;

   protected EntityShoulderRiding(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   public boolean func_191994_f(EntityPlayer var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var2.func_74778_a("id", this.func_70022_Q());
      this.func_189511_e(var2);
      if (var1.func_192027_g(var2)) {
         this.field_70170_p.func_72900_e(this);
         return true;
      } else {
         return false;
      }
   }

   public void func_70071_h_() {
      ++this.field_191996_bB;
      super.func_70071_h_();
   }

   public boolean func_191995_du() {
      return this.field_191996_bB > 100;
   }
}
