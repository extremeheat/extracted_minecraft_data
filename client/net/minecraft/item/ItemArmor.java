package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   private static final int[] field_77882_bY = new int[]{11, 16, 15, 13};
   public static final String[] field_94603_a = new String[]{"minecraft:items/empty_armor_slot_helmet", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_boots"};
   private static final IBehaviorDispenseItem field_96605_cw = new BehaviorDefaultDispenseItem() {
      protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         BlockPos var3 = var1.func_180699_d().func_177972_a(BlockDispenser.func_149937_b(var1.func_82620_h()));
         int var4 = var3.func_177958_n();
         int var5 = var3.func_177956_o();
         int var6 = var3.func_177952_p();
         AxisAlignedBB var7 = new AxisAlignedBB((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1));
         List var8 = var1.func_82618_k().func_175647_a(EntityLivingBase.class, var7, Predicates.and(EntitySelectors.field_180132_d, new EntitySelectors.ArmoredMob(var2)));
         if (var8.size() > 0) {
            EntityLivingBase var9 = (EntityLivingBase)var8.get(0);
            int var10 = var9 instanceof EntityPlayer ? 1 : 0;
            int var11 = EntityLiving.func_82159_b(var2);
            ItemStack var12 = var2.func_77946_l();
            var12.field_77994_a = 1;
            var9.func_70062_b(var11 - var10, var12);
            if (var9 instanceof EntityLiving) {
               ((EntityLiving)var9).func_96120_a(var11, 2.0F);
            }

            --var2.field_77994_a;
            return var2;
         } else {
            return super.func_82487_b(var1, var2);
         }
      }
   };
   public final int field_77881_a;
   public final int field_77879_b;
   public final int field_77880_c;
   private final ItemArmor.ArmorMaterial field_77878_bZ;

   public ItemArmor(ItemArmor.ArmorMaterial var1, int var2, int var3) {
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

   public int func_77619_b() {
      return this.field_77878_bZ.func_78045_a();
   }

   public ItemArmor.ArmorMaterial func_82812_d() {
      return this.field_77878_bZ;
   }

   public boolean func_82816_b_(ItemStack var1) {
      if (this.field_77878_bZ != ItemArmor.ArmorMaterial.LEATHER) {
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
      if (this.field_77878_bZ != ItemArmor.ArmorMaterial.LEATHER) {
         return -1;
      } else {
         NBTTagCompound var2 = var1.func_77978_p();
         if (var2 != null) {
            NBTTagCompound var3 = var2.func_74775_l("display");
            if (var3 != null && var3.func_150297_b("color", 3)) {
               return var3.func_74762_e("color");
            }
         }

         return 10511680;
      }
   }

   public void func_82815_c(ItemStack var1) {
      if (this.field_77878_bZ == ItemArmor.ArmorMaterial.LEATHER) {
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
      if (this.field_77878_bZ != ItemArmor.ArmorMaterial.LEATHER) {
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

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return this.field_77878_bZ.func_151685_b() == var2.func_77973_b() ? true : super.func_82789_a(var1, var2);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      int var4 = EntityLiving.func_82159_b(var1) - 1;
      ItemStack var5 = var3.func_82169_q(var4);
      if (var5 == null) {
         var3.func_70062_b(var4, var1.func_77946_l());
         var1.field_77994_a = 0;
      }

      return var1;
   }

   public static enum ArmorMaterial {
      LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
      CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
      IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
      GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
      DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

      private final String field_179243_f;
      private final int field_78048_f;
      private final int[] field_78049_g;
      private final int field_78055_h;

      private ArmorMaterial(String var3, int var4, int[] var5, int var6) {
         this.field_179243_f = var3;
         this.field_78048_f = var4;
         this.field_78049_g = var5;
         this.field_78055_h = var6;
      }

      public int func_78046_a(int var1) {
         return ItemArmor.field_77882_bY[var1] * this.field_78048_f;
      }

      public int func_78044_b(int var1) {
         return this.field_78049_g[var1];
      }

      public int func_78045_a() {
         return this.field_78055_h;
      }

      public Item func_151685_b() {
         if (this == LEATHER) {
            return Items.field_151116_aA;
         } else if (this == CHAIN) {
            return Items.field_151042_j;
         } else if (this == GOLD) {
            return Items.field_151043_k;
         } else if (this == IRON) {
            return Items.field_151042_j;
         } else {
            return this == DIAMOND ? Items.field_151045_i : null;
         }
      }

      public String func_179242_c() {
         return this.field_179243_f;
      }
   }
}
