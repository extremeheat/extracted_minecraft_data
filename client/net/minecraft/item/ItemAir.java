package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemAir extends Item {
   private final Block field_190904_a;

   public ItemAir(Block var1, Item.Properties var2) {
      super(var2);
      this.field_190904_a = var1;
   }

   public String func_77658_a() {
      return this.field_190904_a.func_149739_a();
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      super.func_77624_a(var1, var2, var3, var4);
      this.field_190904_a.func_190948_a(var1, var2, var3, var4);
   }
}
