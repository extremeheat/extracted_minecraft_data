package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {
   protected final Block field_179227_b;
   protected final Function<ItemStack, String> field_179228_c;

   public ItemMultiTexture(Block var1, Block var2, Function<ItemStack, String> var3) {
      super(var1);
      this.field_179227_b = var2;
      this.field_179228_c = var3;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public ItemMultiTexture(Block var1, Block var2, final String[] var3) {
      this(var1, var2, new Function<ItemStack, String>() {
         public String apply(ItemStack var1) {
            int var2 = var1.func_77960_j();
            if (var2 < 0 || var2 >= var3.length) {
               var2 = 0;
            }

            return var3[var2];
         }

         // $FF: synthetic method
         public Object apply(Object var1) {
            return this.apply((ItemStack)var1);
         }
      });
   }

   public int func_77647_b(int var1) {
      return var1;
   }

   public String func_77667_c(ItemStack var1) {
      return super.func_77658_a() + "." + (String)this.field_179228_c.apply(var1);
   }
}
