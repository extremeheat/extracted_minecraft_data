package net.minecraft.entity.ai;

import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

class EntityMinecartMobSpawner$1 extends MobSpawnerBaseLogic {
   EntityMinecartMobSpawner$1(EntityMinecartMobSpawner var1) {
      super();
      this.field_98296_a = var1;
   }

   @Override
   public void func_98267_a(int var1) {
      this.field_98296_a.field_70170_p.func_72960_a(this.field_98296_a, (byte)var1);
   }

   @Override
   public World func_98271_a() {
      return this.field_98296_a.field_70170_p;
   }

   @Override
   public int func_98275_b() {
      return MathHelper.func_76128_c(this.field_98296_a.field_70165_t);
   }

   @Override
   public int func_98274_c() {
      return MathHelper.func_76128_c(this.field_98296_a.field_70163_u);
   }

   @Override
   public int func_98266_d() {
      return MathHelper.func_76128_c(this.field_98296_a.field_70161_v);
   }
}
