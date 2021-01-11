package net.minecraft.tileentity;

public class TileEntityDropper extends TileEntityDispenser {
   public TileEntityDropper() {
      super();
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.field_146020_a : "container.dropper";
   }

   public String func_174875_k() {
      return "minecraft:dropper";
   }
}
