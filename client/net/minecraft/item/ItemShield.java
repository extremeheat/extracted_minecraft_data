package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemShield extends Item {
   public ItemShield(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("blocking"), (var0, var1x, var2) -> {
         return var2 != null && var2.func_184587_cr() && var2.func_184607_cu() == var0 ? 1.0F : 0.0F;
      });
      BlockDispenser.func_199774_a(this, ItemArmor.field_96605_cw);
   }

   public String func_77667_c(ItemStack var1) {
      return var1.func_179543_a("BlockEntityTag") != null ? this.func_77658_a() + '.' + func_195979_f(var1).func_176762_d() : super.func_77667_c(var1);
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      ItemBanner.func_185054_a(var1, var3);
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.BLOCK;
   }

   public int func_77626_a(ItemStack var1) {
      return 72000;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      var2.func_184598_c(var3);
      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return ItemTags.field_199905_b.func_199685_a_(var2.func_77973_b()) || super.func_82789_a(var1, var2);
   }

   public static EnumDyeColor func_195979_f(ItemStack var0) {
      return EnumDyeColor.func_196056_a(var0.func_190925_c("BlockEntityTag").func_74762_e("Base"));
   }
}
