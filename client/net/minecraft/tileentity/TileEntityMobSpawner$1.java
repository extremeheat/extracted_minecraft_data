package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

class TileEntityMobSpawner$1 extends MobSpawnerBaseLogic {
   TileEntityMobSpawner$1(TileEntityMobSpawner var1) {
      super();
      this.field_150825_a = var1;
   }

   @Override
   public void func_98267_a(int var1) {
      this.field_150825_a
         .field_145850_b
         .func_147452_c(
            this.field_150825_a.field_145851_c, this.field_150825_a.field_145848_d, this.field_150825_a.field_145849_e, Blocks.field_150474_ac, var1, 0
         );
   }

   @Override
   public World func_98271_a() {
      return this.field_150825_a.field_145850_b;
   }

   @Override
   public int func_98275_b() {
      return this.field_150825_a.field_145851_c;
   }

   @Override
   public int func_98274_c() {
      return this.field_150825_a.field_145848_d;
   }

   @Override
   public int func_98266_d() {
      return this.field_150825_a.field_145849_e;
   }

   @Override
   public void func_98277_a(MobSpawnerBaseLogic$WeightedRandomMinecart var1) {
      super.func_98277_a(var1);
      if (this.func_98271_a() != null) {
         this.func_98271_a().func_147471_g(this.field_150825_a.field_145851_c, this.field_150825_a.field_145848_d, this.field_150825_a.field_145849_e);
      }
   }
}
