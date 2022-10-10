package net.minecraft.item;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemEnderPearl extends Item {
   public ItemEnderPearl(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (!var2.field_71075_bZ.field_75098_d) {
         var4.func_190918_g(1);
      }

      var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187595_bc, SoundCategory.NEUTRAL, 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
      var2.func_184811_cZ().func_185145_a(this, 20);
      if (!var1.field_72995_K) {
         EntityEnderPearl var5 = new EntityEnderPearl(var1, var2);
         var5.func_184538_a(var2, var2.field_70125_A, var2.field_70177_z, 0.0F, 1.5F, 1.0F);
         var1.func_72838_d(var5);
      }

      var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }
}
