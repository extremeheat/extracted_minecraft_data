package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumHandSide {
   LEFT(new TextComponentTranslation("options.mainHand.left", new Object[0])),
   RIGHT(new TextComponentTranslation("options.mainHand.right", new Object[0]));

   private final ITextComponent field_188471_c;

   private EnumHandSide(ITextComponent var3) {
      this.field_188471_c = var3;
   }

   public EnumHandSide func_188468_a() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public String toString() {
      return this.field_188471_c.getString();
   }
}
