package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemEmptyMap extends ItemMapBase {
   public ItemEmptyMap(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = ItemMap.func_195952_a(var1, MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70161_v), (byte)0, true, false);
      ItemStack var5 = var2.func_184586_b(var3);
      var5.func_190918_g(1);
      if (var5.func_190926_b()) {
         return new ActionResult(EnumActionResult.SUCCESS, var4);
      } else {
         if (!var2.field_71071_by.func_70441_a(var4.func_77946_l())) {
            var2.func_71019_a(var4, false);
         }

         var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
         return new ActionResult(EnumActionResult.SUCCESS, var5);
      }
   }
}
