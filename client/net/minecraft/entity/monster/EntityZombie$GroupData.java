package net.minecraft.entity.monster;

import net.minecraft.entity.IEntityLivingData;

class EntityZombie$GroupData implements IEntityLivingData {
   public boolean field_142048_a;
   public boolean field_142046_b;

   private EntityZombie$GroupData(EntityZombie var1, boolean var2, boolean var3) {
      super();
      this.field_142047_c = var1;
      this.field_142048_a = false;
      this.field_142046_b = false;
      this.field_142048_a = var2;
      this.field_142046_b = var3;
   }
}
