package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemTippedArrow extends ItemArrow {
   public ItemTippedArrow(Item.Properties var1) {
      super(var1);
   }

   public ItemStack func_190903_i() {
      return PotionUtils.func_185188_a(super.func_190903_i(), PotionTypes.field_185254_z);
   }

   public EntityArrow func_200887_a(World var1, ItemStack var2, EntityLivingBase var3) {
      EntityTippedArrow var4 = new EntityTippedArrow(var1, var3);
      var4.func_184555_a(var2);
      return var4;
   }

   public void func_150895_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      if (this.func_194125_a(var1)) {
         Iterator var3 = IRegistry.field_212621_j.iterator();

         while(var3.hasNext()) {
            PotionType var4 = (PotionType)var3.next();
            if (!var4.func_185170_a().isEmpty()) {
               var2.add(PotionUtils.func_185188_a(new ItemStack(this), var4));
            }
         }
      }

   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      PotionUtils.func_185182_a(var1, var3, 0.125F);
   }

   public String func_77667_c(ItemStack var1) {
      return PotionUtils.func_185191_c(var1).func_185174_b(this.func_77658_a() + ".effect.");
   }
}
