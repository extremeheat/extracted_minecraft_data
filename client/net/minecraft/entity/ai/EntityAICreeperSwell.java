package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;

public class EntityAICreeperSwell extends EntityAIBase {
   EntityCreeper field_75269_a;
   EntityLivingBase field_75268_b;

   public EntityAICreeperSwell(EntityCreeper var1) {
      super();
      this.field_75269_a = var1;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      EntityLivingBase var1 = this.field_75269_a.func_70638_az();
      return this.field_75269_a.func_70832_p() > 0 || var1 != null && this.field_75269_a.func_70068_e(var1) < 9.0;
   }

   @Override
   public void func_75249_e() {
      this.field_75269_a.func_70661_as().func_75499_g();
      this.field_75268_b = this.field_75269_a.func_70638_az();
   }

   @Override
   public void func_75251_c() {
      this.field_75268_b = null;
   }

   @Override
   public void func_75246_d() {
      if (this.field_75268_b == null) {
         this.field_75269_a.func_70829_a(-1);
      } else if (this.field_75269_a.func_70068_e(this.field_75268_b) > 49.0) {
         this.field_75269_a.func_70829_a(-1);
      } else if (!this.field_75269_a.func_70635_at().func_75522_a(this.field_75268_b)) {
         this.field_75269_a.func_70829_a(-1);
      } else {
         this.field_75269_a.func_70829_a(1);
      }
   }
}
