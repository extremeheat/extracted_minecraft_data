package net.minecraft.entity.ai;

import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;

public class EntityAILookAtVillager extends EntityAIBase {
   private EntityIronGolem field_75397_a;
   private EntityVillager field_75395_b;
   private int field_75396_c;

   public EntityAILookAtVillager(EntityIronGolem var1) {
      super();
      this.field_75397_a = var1;
      this.func_75248_a(3);
   }

   @Override
   public boolean func_75250_a() {
      if (!this.field_75397_a.field_70170_p.func_72935_r()) {
         return false;
      } else if (this.field_75397_a.func_70681_au().nextInt(8000) != 0) {
         return false;
      } else {
         this.field_75395_b = (EntityVillager)this.field_75397_a
            .field_70170_p
            .func_72857_a(EntityVillager.class, this.field_75397_a.field_70121_D.func_72314_b(6.0, 2.0, 6.0), this.field_75397_a);
         return this.field_75395_b != null;
      }
   }

   @Override
   public boolean func_75253_b() {
      return this.field_75396_c > 0;
   }

   @Override
   public void func_75249_e() {
      this.field_75396_c = 400;
      this.field_75397_a.func_70851_e(true);
   }

   @Override
   public void func_75251_c() {
      this.field_75397_a.func_70851_e(false);
      this.field_75395_b = null;
   }

   @Override
   public void func_75246_d() {
      this.field_75397_a.func_70671_ap().func_75651_a(this.field_75395_b, 30.0F, 30.0F);
      --this.field_75396_c;
   }
}
