package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {
   public final IEntityMultiPart field_70259_a;
   public final String field_146032_b;

   public EntityDragonPart(IEntityMultiPart var1, String var2, float var3, float var4) {
      super(var1.func_82194_d());
      this.func_70105_a(var3, var4);
      this.field_70259_a = var1;
      this.field_146032_b = var2;
   }

   protected void func_70088_a() {
   }

   protected void func_70037_a(NBTTagCompound var1) {
   }

   protected void func_70014_b(NBTTagCompound var1) {
   }

   public boolean func_70067_L() {
      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return this.func_180431_b(var1) ? false : this.field_70259_a.func_70965_a(this, var1, var2);
   }

   public boolean func_70028_i(Entity var1) {
      return this == var1 || this.field_70259_a == var1;
   }
}
