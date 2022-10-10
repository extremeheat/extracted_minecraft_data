package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemElytra extends Item {
   public ItemElytra(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("broken"), (var0, var1x, var2) -> {
         return func_185069_d(var0) ? 0.0F : 1.0F;
      });
      BlockDispenser.func_199774_a(this, ItemArmor.field_96605_cw);
   }

   public static boolean func_185069_d(ItemStack var0) {
      return var0.func_77952_i() < var0.func_77958_k() - 1;
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return var2.func_77973_b() == Items.field_204840_eX;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      EntityEquipmentSlot var5 = EntityLiving.func_184640_d(var4);
      ItemStack var6 = var2.func_184582_a(var5);
      if (var6.func_190926_b()) {
         var2.func_184201_a(var5, var4.func_77946_l());
         var4.func_190920_e(0);
         return new ActionResult(EnumActionResult.SUCCESS, var4);
      } else {
         return new ActionResult(EnumActionResult.FAIL, var4);
      }
   }
}
