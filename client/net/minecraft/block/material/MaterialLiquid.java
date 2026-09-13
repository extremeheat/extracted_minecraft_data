package net.minecraft.block.material;

public class MaterialLiquid extends Material {
   public MaterialLiquid(MapColor var1) {
      super(var1);
      this.func_76231_i();
      this.func_76219_n();
   }

   @Override
   public boolean func_76224_d() {
      return true;
   }

   @Override
   public boolean func_76230_c() {
      return false;
   }

   @Override
   public boolean func_76220_a() {
      return false;
   }
}
