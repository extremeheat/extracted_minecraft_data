package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   public ItemFishingRod(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("cast"), (var0, var1x, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            boolean var3 = var2.func_184614_ca() == var0;
            boolean var4 = var2.func_184592_cb() == var0;
            if (var2.func_184614_ca().func_77973_b() instanceof ItemFishingRod) {
               var4 = false;
            }

            return (var3 || var4) && var2 instanceof EntityPlayer && ((EntityPlayer)var2).field_71104_cf != null ? 1.0F : 0.0F;
         }
      });
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (var2.field_71104_cf != null) {
         int var5 = var2.field_71104_cf.func_146034_e(var4);
         var4.func_77972_a(var5, var2);
         var2.func_184609_a(var3);
         var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_193780_J, SoundCategory.NEUTRAL, 1.0F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
      } else {
         var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187612_G, SoundCategory.NEUTRAL, 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
         if (!var1.field_72995_K) {
            EntityFishHook var8 = new EntityFishHook(var1, var2);
            int var6 = EnchantmentHelper.func_191528_c(var4);
            if (var6 > 0) {
               var8.func_191516_a(var6);
            }

            int var7 = EnchantmentHelper.func_191529_b(var4);
            if (var7 > 0) {
               var8.func_191517_b(var7);
            }

            var1.func_72838_d(var8);
         }

         var2.func_184609_a(var3);
         var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      }

      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }

   public int func_77619_b() {
      return 1;
   }
}
