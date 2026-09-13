package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
   boolean field_75361_i;
   int field_75360_j;

   public EntityAIOpenDoor(EntityLiving var1, boolean var2) {
      super(var1);
      this.field_75356_a = var1;
      this.field_75361_i = var2;
   }

   @Override
   public boolean func_75253_b() {
      return this.field_75361_i && this.field_75360_j > 0 && super.func_75253_b();
   }

   @Override
   public void func_75249_e() {
      this.field_75360_j = 20;
      this.field_151504_e.func_150014_a(this.field_75356_a.field_70170_p, this.field_75354_b, this.field_75355_c, this.field_75352_d, true);
   }

   @Override
   public void func_75251_c() {
      if (this.field_75361_i) {
         this.field_151504_e.func_150014_a(this.field_75356_a.field_70170_p, this.field_75354_b, this.field_75355_c, this.field_75352_d, false);
      }
   }

   @Override
   public void func_75246_d() {
      --this.field_75360_j;
      super.func_75246_d();
   }
}
