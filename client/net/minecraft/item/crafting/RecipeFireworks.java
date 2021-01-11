package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe {
   private ItemStack field_92102_a;

   public RecipeFireworks() {
      super();
   }

   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      this.field_92102_a = null;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      for(int var9 = 0; var9 < var1.func_70302_i_(); ++var9) {
         ItemStack var10 = var1.func_70301_a(var9);
         if (var10 != null) {
            if (var10.func_77973_b() == Items.field_151016_H) {
               ++var4;
            } else if (var10.func_77973_b() == Items.field_151154_bQ) {
               ++var6;
            } else if (var10.func_77973_b() == Items.field_151100_aR) {
               ++var5;
            } else if (var10.func_77973_b() == Items.field_151121_aF) {
               ++var3;
            } else if (var10.func_77973_b() == Items.field_151114_aO) {
               ++var7;
            } else if (var10.func_77973_b() == Items.field_151045_i) {
               ++var7;
            } else if (var10.func_77973_b() == Items.field_151059_bz) {
               ++var8;
            } else if (var10.func_77973_b() == Items.field_151008_G) {
               ++var8;
            } else if (var10.func_77973_b() == Items.field_151074_bl) {
               ++var8;
            } else {
               if (var10.func_77973_b() != Items.field_151144_bL) {
                  return false;
               }

               ++var8;
            }
         }
      }

      var7 += var5 + var8;
      if (var4 <= 3 && var3 <= 1) {
         NBTTagCompound var16;
         NBTTagCompound var19;
         if (var4 >= 1 && var3 == 1 && var7 == 0) {
            this.field_92102_a = new ItemStack(Items.field_151152_bP);
            if (var6 > 0) {
               var16 = new NBTTagCompound();
               var19 = new NBTTagCompound();
               NBTTagList var25 = new NBTTagList();

               for(int var22 = 0; var22 < var1.func_70302_i_(); ++var22) {
                  ItemStack var26 = var1.func_70301_a(var22);
                  if (var26 != null && var26.func_77973_b() == Items.field_151154_bQ && var26.func_77942_o() && var26.func_77978_p().func_150297_b("Explosion", 10)) {
                     var25.func_74742_a(var26.func_77978_p().func_74775_l("Explosion"));
                  }
               }

               var19.func_74782_a("Explosions", var25);
               var19.func_74774_a("Flight", (byte)var4);
               var16.func_74782_a("Fireworks", var19);
               this.field_92102_a.func_77982_d(var16);
            }

            return true;
         } else if (var4 == 1 && var3 == 0 && var6 == 0 && var5 > 0 && var8 <= 1) {
            this.field_92102_a = new ItemStack(Items.field_151154_bQ);
            var16 = new NBTTagCompound();
            var19 = new NBTTagCompound();
            byte var23 = 0;
            ArrayList var12 = Lists.newArrayList();

            for(int var13 = 0; var13 < var1.func_70302_i_(); ++var13) {
               ItemStack var14 = var1.func_70301_a(var13);
               if (var14 != null) {
                  if (var14.func_77973_b() == Items.field_151100_aR) {
                     var12.add(ItemDye.field_150922_c[var14.func_77960_j() & 15]);
                  } else if (var14.func_77973_b() == Items.field_151114_aO) {
                     var19.func_74757_a("Flicker", true);
                  } else if (var14.func_77973_b() == Items.field_151045_i) {
                     var19.func_74757_a("Trail", true);
                  } else if (var14.func_77973_b() == Items.field_151059_bz) {
                     var23 = 1;
                  } else if (var14.func_77973_b() == Items.field_151008_G) {
                     var23 = 4;
                  } else if (var14.func_77973_b() == Items.field_151074_bl) {
                     var23 = 2;
                  } else if (var14.func_77973_b() == Items.field_151144_bL) {
                     var23 = 3;
                  }
               }
            }

            int[] var24 = new int[var12.size()];

            for(int var27 = 0; var27 < var24.length; ++var27) {
               var24[var27] = (Integer)var12.get(var27);
            }

            var19.func_74783_a("Colors", var24);
            var19.func_74774_a("Type", var23);
            var16.func_74782_a("Explosion", var19);
            this.field_92102_a.func_77982_d(var16);
            return true;
         } else if (var4 == 0 && var3 == 0 && var6 == 1 && var5 > 0 && var5 == var7) {
            ArrayList var15 = Lists.newArrayList();

            for(int var17 = 0; var17 < var1.func_70302_i_(); ++var17) {
               ItemStack var11 = var1.func_70301_a(var17);
               if (var11 != null) {
                  if (var11.func_77973_b() == Items.field_151100_aR) {
                     var15.add(ItemDye.field_150922_c[var11.func_77960_j() & 15]);
                  } else if (var11.func_77973_b() == Items.field_151154_bQ) {
                     this.field_92102_a = var11.func_77946_l();
                     this.field_92102_a.field_77994_a = 1;
                  }
               }
            }

            int[] var18 = new int[var15.size()];

            for(int var20 = 0; var20 < var18.length; ++var20) {
               var18[var20] = (Integer)var15.get(var20);
            }

            if (this.field_92102_a != null && this.field_92102_a.func_77942_o()) {
               NBTTagCompound var21 = this.field_92102_a.func_77978_p().func_74775_l("Explosion");
               if (var21 == null) {
                  return false;
               } else {
                  var21.func_74783_a("FadeColors", var18);
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public ItemStack func_77572_b(InventoryCrafting var1) {
      return this.field_92102_a.func_77946_l();
   }

   public int func_77570_a() {
      return 10;
   }

   public ItemStack func_77571_b() {
      return this.field_92102_a;
   }

   public ItemStack[] func_179532_b(InventoryCrafting var1) {
      ItemStack[] var2 = new ItemStack[var1.func_70302_i_()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (var4 != null && var4.func_77973_b().func_77634_r()) {
            var2[var3] = new ItemStack(var4.func_77973_b().func_77668_q());
         }
      }

      return var2;
   }
}
