package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public interface INameable {
   ITextComponent func_200200_C_();

   boolean func_145818_k_();

   default ITextComponent func_145748_c_() {
      return this.func_200200_C_();
   }

   @Nullable
   ITextComponent func_200201_e();
}
