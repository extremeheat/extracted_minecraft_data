package net.minecraft.item;

import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   public ItemCarrotOnAStick(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (var1.field_72995_K) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else {
         if (var2.func_184218_aH() && var2.func_184187_bx() instanceof EntityPig) {
            EntityPig var5 = (EntityPig)var2.func_184187_bx();
            if (var4.func_77958_k() - var4.func_77952_i() >= 7 && var5.func_184762_da()) {
               var4.func_77972_a(7, var2);
               if (var4.func_190926_b()) {
                  ItemStack var6 = new ItemStack(Items.field_151112_aM);
                  var6.func_77982_d(var4.func_77978_p());
                  return new ActionResult(EnumActionResult.SUCCESS, var6);
               }

               return new ActionResult(EnumActionResult.SUCCESS, var4);
            }
         }

         var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
         return new ActionResult(EnumActionResult.PASS, var4);
      }
   }
}
