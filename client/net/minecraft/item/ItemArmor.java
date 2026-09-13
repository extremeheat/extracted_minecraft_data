package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final int[] field_77882_bY = new int[]{11, 16, 15, 13};
   private static final String[] field_94606_cu = new String[]{
      "leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"
   };
   public static final String[] field_94603_a = new String[]{
      "empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"
   };
   private static final IBehaviorDispenseItem field_96605_cw = new ItemArmor$1();
   public final int field_77881_a;
   public final int field_77879_b;
   public final int field_77880_c;
   private final ItemArmor$ArmorMaterial field_77878_bZ;
   private IIcon field_94605_cw;
   private IIcon field_94604_cx;

   public ItemArmor(ItemArmor$ArmorMaterial var1, int var2, int var3) {
      super();
      this.field_77878_bZ = var1;
      this.field_77881_a = var3;
      this.field_77880_c = var2;
      this.field_77879_b = var1.func_78044_b(var3);
      this.func_77656_e(var1.func_78046_a(var3));
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78037_j);
      BlockDispenser.field_149943_a.func_82595_a(this, field_96605_cw);
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      if (var2 > 0) {
         return 16777215;
      } else {
         int var3 = this.func_82814_b(var1);
         if (var3 < 0) {
            var3 = 16777215;
         }

         return var3;
      }
   }

   @Override
   public boolean func_77623_v() {
      return this.field_77878_bZ == ItemArmor$ArmorMaterial.CLOTH;
   }

   @Override
   public int func_77619_b() {
      return this.field_77878_bZ.func_78045_a();
   }

   public ItemArmor$ArmorMaterial func_82812_d() {
      return this.field_77878_bZ;
   }

   public boolean func_82816_b_(ItemStack var1) {
      if (this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH) {
         return false;
      } else if (!var1.func_77942_o()) {
         return false;
      } else if (!var1.func_77978_p().func_150297_b("display", 10)) {
         return false;
      } else {
         return var1.func_77978_p().func_74775_l("display").func_150297_b("color", 3);
      }
   }

   public int func_82814_b(ItemStack var1) {
      if (this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH) {
         return -1;
      } else {
         NBTTagCompound var2 = var1.func_77978_p();
         if (var2 == null) {
            return 10511680;
         } else {
            NBTTagCompound var3 = var2.func_74775_l("display");
            if (var3 == null) {
               return 10511680;
            } else {
               return var3.func_150297_b("color", 3) ? var3.func_74762_e("color") : 10511680;
            }
         }
      }
   }

   @Override
   public IIcon func_77618_c(int var1, int var2) {
      return var2 == 1 ? this.field_94605_cw : super.func_77618_c(var1, var2);
   }

   public void func_82815_c(ItemStack var1) {
      if (this.field_77878_bZ == ItemArmor$ArmorMaterial.CLOTH) {
         NBTTagCompound var2 = var1.func_77978_p();
         if (var2 != null) {
            NBTTagCompound var3 = var2.func_74775_l("display");
            if (var3.func_74764_b("color")) {
               var3.func_82580_o("color");
            }
         }
      }
   }

   public void func_82813_b(ItemStack var1, int var2) {
      if (this.field_77878_bZ != ItemArmor$ArmorMaterial.CLOTH) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      } else {
         NBTTagCompound var3 = var1.func_77978_p();
         if (var3 == null) {
            var3 = new NBTTagCompound();
            var1.func_77982_d(var3);
         }

         NBTTagCompound var4 = var3.func_74775_l("display");
         if (!var3.func_150297_b("display", 10)) {
            var3.func_74782_a("display", var4);
         }

         var4.func_74768_a("color", var2);
      }
   }

   @Override
   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_77878_bZ.func_151685_b() == var2.func_77973_b() ? true : super.func_82789_a(var1, var2);
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      super.func_94581_a(var1);
      if (this.field_77878_bZ == ItemArmor$ArmorMaterial.CLOTH) {
         this.field_94605_cw = var1.func_94245_a(field_94606_cu[this.field_77881_a]);
      }

      this.field_94604_cx = var1.func_94245_a(field_94603_a[this.field_77881_a]);
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      int var4 = EntityLiving.func_82159_b(var1) - 1;
      ItemStack var5 = var3.func_82169_q(var4);
      if (var5 == null) {
         var3.func_70062_b(var4, var1.func_77946_l());
         var1.field_77994_a = 0;
      }

      return var1;
   }

   public static IIcon func_94602_b(int var0) {
      switch(var0) {
         case 0:
            return Items.field_151161_ac.field_94604_cx;
         case 1:
            return Items.field_151163_ad.field_94604_cx;
         case 2:
            return Items.field_151173_ae.field_94604_cx;
         case 3:
            return Items.field_151175_af.field_94604_cx;
         default:
            return null;
      }
   }
}
