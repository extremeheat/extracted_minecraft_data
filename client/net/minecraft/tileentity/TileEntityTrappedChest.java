package net.minecraft.tileentity;

public class TileEntityTrappedChest extends TileEntityChest {
   public TileEntityTrappedChest() {
      super(TileEntityType.field_200973_d);
   }

   protected void func_195482_p() {
      super.func_195482_p();
      this.field_145850_b.func_195593_d(this.field_174879_c.func_177977_b(), this.func_195044_w().func_177230_c());
   }
}
