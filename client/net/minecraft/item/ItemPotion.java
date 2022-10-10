package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemPotion extends Item {
   public ItemPotion(Item.Properties var1) {
      super(var1);
   }

   public ItemStack func_190903_i() {
      return PotionUtils.func_185188_a(super.func_190903_i(), PotionTypes.field_185230_b);
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityLivingBase var3) {
      EntityPlayer var4 = var3 instanceof EntityPlayer ? (EntityPlayer)var3 : null;
      if (var4 == null || !var4.field_71075_bZ.field_75098_d) {
         var1.func_190918_g(1);
      }

      if (var4 instanceof EntityPlayerMP) {
         CriteriaTriggers.field_193138_y.func_193148_a((EntityPlayerMP)var4, var1);
      }

      if (!var2.field_72995_K) {
         List var5 = PotionUtils.func_185189_a(var1);
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            PotionEffect var7 = (PotionEffect)var6.next();
            if (var7.func_188419_a().func_76403_b()) {
               var7.func_188419_a().func_180793_a(var4, var4, var3, var7.func_76458_c(), 1.0D);
            } else {
               var3.func_195064_c(new PotionEffect(var7));
            }
         }
      }

      if (var4 != null) {
         var4.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      }

      if (var4 == null || !var4.field_71075_bZ.field_75098_d) {
         if (var1.func_190926_b()) {
            return new ItemStack(Items.field_151069_bo);
         }

         if (var4 != null) {
            var4.field_71071_by.func_70441_a(new ItemStack(Items.field_151069_bo));
         }
      }

      return var1;
   }

   public int func_77626_a(ItemStack var1) {
      return 32;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.DRINK;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      var2.func_184598_c(var3);
      return new ActionResult(EnumActionResult.SUCCESS, var2.func_184586_b(var3));
   }

   public String func_77667_c(ItemStack var1) {
      return PotionUtils.func_185191_c(var1).func_185174_b(this.func_77658_a() + ".effect.");
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      PotionUtils.func_185182_a(var1, var3, 1.0F);
   }

   public boolean func_77636_d(ItemStack var1) {
      return super.func_77636_d(var1) || !PotionUtils.func_185189_a(var1).isEmpty();
   }

   public void func_150895_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      if (this.func_194125_a(var1)) {
         Iterator var3 = IRegistry.field_212621_j.iterator();

         while(var3.hasNext()) {
            PotionType var4 = (PotionType)var3.next();
            if (var4 != PotionTypes.field_185229_a) {
               var2.add(PotionUtils.func_185188_a(new ItemStack(this), var4));
            }
         }
      }

   }
}
