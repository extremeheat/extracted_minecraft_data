package net.minecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean field_150907_b;

   public ItemFishFood(boolean var1) {
      super(0, 0.0F, false);
      this.field_150907_b = var1;
   }

   @Override
   public int func_150905_g(ItemStack var1) {
      ItemFishFood$FishType var2 = ItemFishFood$FishType.func_150978_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150970_e() : var2.func_150975_c();
   }

   @Override
   public float func_150906_h(ItemStack var1) {
      ItemFishFood$FishType var2 = ItemFishFood$FishType.func_150978_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150977_f() : var2.func_150967_d();
   }

   @Override
   public String func_150896_i(ItemStack var1) {
      return ItemFishFood$FishType.func_150978_a(var1) == ItemFishFood$FishType.PUFFERFISH ? PotionHelper.field_151423_m : null;
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      for(ItemFishFood$FishType var5 : ItemFishFood$FishType.values()) {
         var5.func_150968_a(var1);
      }
   }

   @Override
   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      ItemFishFood$FishType var4 = ItemFishFood$FishType.func_150978_a(var1);
      if (var4 == ItemFishFood$FishType.PUFFERFISH) {
         var3.func_70690_d(new PotionEffect(Potion.field_76436_u.field_76415_H, 1200, 3));
         var3.func_70690_d(new PotionEffect(Potion.field_76438_s.field_76415_H, 300, 2));
         var3.func_70690_d(new PotionEffect(Potion.field_76431_k.field_76415_H, 300, 1));
      }

      super.func_77849_c(var1, var2, var3);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      ItemFishFood$FishType var2 = ItemFishFood$FishType.func_150974_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150979_h() : var2.func_150971_g();
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      for(ItemFishFood$FishType var7 : ItemFishFood$FishType.values()) {
         if (!this.field_150907_b || var7.func_150973_i()) {
            var3.add(new ItemStack(this, 1, var7.func_150976_a()));
         }
      }
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      ItemFishFood$FishType var2 = ItemFishFood$FishType.func_150978_a(var1);
      return this.func_77658_a() + "." + var2.func_150972_b() + "." + (this.field_150907_b && var2.func_150973_i() ? "cooked" : "raw");
   }
}
