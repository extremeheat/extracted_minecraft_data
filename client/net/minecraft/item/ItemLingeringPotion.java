package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemLingeringPotion extends ItemPotion {
   public ItemLingeringPotion(Item.Properties var1) {
      super(var1);
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      PotionUtils.func_185182_a(var1, var3, 0.25F);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      ItemStack var5 = var2.field_71075_bZ.field_75098_d ? var4.func_77946_l() : var4.func_77979_a(1);
      var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187756_df, SoundCategory.NEUTRAL, 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
      if (!var1.field_72995_K) {
         EntityPotion var6 = new EntityPotion(var1, var2, var5);
         var6.func_184538_a(var2, var2.field_70125_A, var2.field_70177_z, -20.0F, 0.5F, 1.0F);
         var1.func_72838_d(var6);
      }

      var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }
}
