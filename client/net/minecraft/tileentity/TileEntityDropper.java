package net.minecraft.tileentity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDropper extends TileEntityDispenser {
   public TileEntityDropper() {
      super(TileEntityType.field_200977_h);
   }

   public ITextComponent func_200200_C_() {
      ITextComponent var1 = this.func_200201_e();
      return (ITextComponent)(var1 != null ? var1 : new TextComponentTranslation("container.dropper", new Object[0]));
   }

   public String func_174875_k() {
      return "minecraft:dropper";
   }
}
