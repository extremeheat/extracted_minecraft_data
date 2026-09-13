package net.minecraft.item.crafting;

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

   @Override
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
      if (var4 > 3 || var3 > 1) {
         return false;
      } else if (var4 >= 1 && var3 == 1 && var7 == 0) {
         this.field_92102_a = new ItemStack(Items.field_151152_bP);
         if (var6 > 0) {
            NBTTagCompound var18 = new NBTTagCompound();
            NBTTagCompound var22 = new NBTTagCompound();
            NBTTagList var26 = new NBTTagList();

            for(int var27 = 0; var27 < var1.func_70302_i_(); ++var27) {
               ItemStack var29 = var1.func_70301_a(var27);
               if (var29 != null
                  && var29.func_77973_b() == Items.field_151154_bQ
                  && var29.func_77942_o()
                  && var29.func_77978_p().func_150297_b("Explosion", 10)) {
                  var26.func_74742_a(var29.func_77978_p().func_74775_l("Explosion"));
               }
            }

            var22.func_74782_a("Explosions", var26);
            var22.func_74774_a("Flight", (byte)var4);
            var18.func_74782_a("Fireworks", var22);
            this.field_92102_a.func_77982_d(var18);
         }

         return true;
      } else if (var4 == 1 && var3 == 0 && var6 == 0 && var5 > 0 && var8 <= 1) {
         this.field_92102_a = new ItemStack(Items.field_151154_bQ);
         NBTTagCompound var17 = new NBTTagCompound();
         NBTTagCompound var21 = new NBTTagCompound();
         byte var25 = 0;
         ArrayList var12 = new ArrayList();

         for(int var13 = 0; var13 < var1.func_70302_i_(); ++var13) {
            ItemStack var14 = var1.func_70301_a(var13);
            if (var14 != null) {
               if (var14.func_77973_b() == Items.field_151100_aR) {
                  var12.add(ItemDye.field_150922_c[var14.func_77960_j()]);
               } else if (var14.func_77973_b() == Items.field_151114_aO) {
                  var21.func_74757_a("Flicker", true);
               } else if (var14.func_77973_b() == Items.field_151045_i) {
                  var21.func_74757_a("Trail", true);
               } else if (var14.func_77973_b() == Items.field_151059_bz) {
                  var25 = 1;
               } else if (var14.func_77973_b() == Items.field_151008_G) {
                  var25 = 4;
               } else if (var14.func_77973_b() == Items.field_151074_bl) {
                  var25 = 2;
               } else if (var14.func_77973_b() == Items.field_151144_bL) {
                  var25 = 3;
               }
            }
         }

         int[] var28 = new int[var12.size()];

         for(int var30 = 0; var30 < var28.length; ++var30) {
            var28[var30] = var12.get(var30);
         }

         var21.func_74783_a("Colors", var28);
         var21.func_74774_a("Type", var25);
         var17.func_74782_a("Explosion", var21);
         this.field_92102_a.func_77982_d(var17);
         return true;
      } else if (var4 == 0 && var3 == 0 && var6 == 1 && var5 > 0 && var5 == var7) {
         ArrayList var16 = new ArrayList();

         for(int var19 = 0; var19 < var1.func_70302_i_(); ++var19) {
            ItemStack var11 = var1.func_70301_a(var19);
            if (var11 != null) {
               if (var11.func_77973_b() == Items.field_151100_aR) {
                  var16.add(ItemDye.field_150922_c[var11.func_77960_j()]);
               } else if (var11.func_77973_b() == Items.field_151154_bQ) {
                  this.field_92102_a = var11.func_77946_l();
                  this.field_92102_a.field_77994_a = 1;
               }
            }
         }

         int[] var20 = new int[var16.size()];

         for(int var23 = 0; var23 < var20.length; ++var23) {
            var20[var23] = var16.get(var23);
         }

         if (this.field_92102_a != null && this.field_92102_a.func_77942_o()) {
            NBTTagCompound var24 = this.field_92102_a.func_77978_p().func_74775_l("Explosion");
            if (var24 == null) {
               return false;
            } else {
               var24.func_74783_a("FadeColors", var20);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack func_77572_b(InventoryCrafting var1) {
      return this.field_92102_a.func_77946_l();
   }

   @Override
   public int func_77570_a() {
      return 10;
   }

   @Override
   public ItemStack func_77571_b() {
      return this.field_92102_a;
   }
}
