package net.minecraft.tileentity;

public class TileEntityDropper extends TileEntityDispenser {
   public TileEntityDropper() {
      super();
   }

   @Override
   public String func_145825_b() {
      return this.func_145818_k_() ? this.field_146020_a : "container.dropper";
   }
}
