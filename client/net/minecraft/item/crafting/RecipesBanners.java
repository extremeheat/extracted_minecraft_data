package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.world.World;

public class RecipesBanners {
   public RecipesBanners() {
      super();
   }

   void func_179534_a(CraftingManager var1) {
      EnumDyeColor[] var2 = EnumDyeColor.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumDyeColor var5 = var2[var4];
         var1.func_92103_a(new ItemStack(Items.field_179564_cE, 1, var5.func_176767_b()), "###", "###", " | ", '#', new ItemStack(Blocks.field_150325_L, 1, var5.func_176765_a()), '|', Items.field_151055_y);
      }

      var1.func_180302_a(new RecipesBanners.RecipeDuplicatePattern());
      var1.func_180302_a(new RecipesBanners.RecipeAddPattern());
   }

   static class RecipeAddPattern implements IRecipe {
      private RecipeAddPattern() {
         super();
      }

      public boolean func_77569_a(InventoryCrafting var1, World var2) {
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (var5 != null && var5.func_77973_b() == Items.field_179564_cE) {
               if (var3) {
                  return false;
               }

               if (TileEntityBanner.func_175113_c(var5) >= 6) {
                  return false;
               }

               var3 = true;
            }
         }

         if (!var3) {
            return false;
         } else {
            return this.func_179533_c(var1) != null;
         }
      }

      public ItemStack func_77572_b(InventoryCrafting var1) {
         ItemStack var2 = null;

         for(int var3 = 0; var3 < var1.func_70302_i_(); ++var3) {
            ItemStack var4 = var1.func_70301_a(var3);
            if (var4 != null && var4.func_77973_b() == Items.field_179564_cE) {
               var2 = var4.func_77946_l();
               var2.field_77994_a = 1;
               break;
            }
         }

         TileEntityBanner.EnumBannerPattern var8 = this.func_179533_c(var1);
         if (var8 != null) {
            int var9 = 0;

            ItemStack var6;
            for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
               var6 = var1.func_70301_a(var5);
               if (var6 != null && var6.func_77973_b() == Items.field_151100_aR) {
                  var9 = var6.func_77960_j();
                  break;
               }
            }

            NBTTagCompound var10 = var2.func_179543_a("BlockEntityTag", true);
            var6 = null;
            NBTTagList var11;
            if (var10.func_150297_b("Patterns", 9)) {
               var11 = var10.func_150295_c("Patterns", 10);
            } else {
               var11 = new NBTTagList();
               var10.func_74782_a("Patterns", var11);
            }

            NBTTagCompound var7 = new NBTTagCompound();
            var7.func_74778_a("Pattern", var8.func_177273_b());
            var7.func_74768_a("Color", var9);
            var11.func_74742_a(var7);
         }

         return var2;
      }

      public int func_77570_a() {
         return 10;
      }

      public ItemStack func_77571_b() {
         return null;
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

      private TileEntityBanner.EnumBannerPattern func_179533_c(InventoryCrafting var1) {
         TileEntityBanner.EnumBannerPattern[] var2 = TileEntityBanner.EnumBannerPattern.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            TileEntityBanner.EnumBannerPattern var5 = var2[var4];
            if (var5.func_177270_d()) {
               boolean var6 = true;
               int var9;
               if (var5.func_177269_e()) {
                  boolean var7 = false;
                  boolean var8 = false;

                  for(var9 = 0; var9 < var1.func_70302_i_() && var6; ++var9) {
                     ItemStack var10 = var1.func_70301_a(var9);
                     if (var10 != null && var10.func_77973_b() != Items.field_179564_cE) {
                        if (var10.func_77973_b() == Items.field_151100_aR) {
                           if (var8) {
                              var6 = false;
                              break;
                           }

                           var8 = true;
                        } else {
                           if (var7 || !var10.func_77969_a(var5.func_177272_f())) {
                              var6 = false;
                              break;
                           }

                           var7 = true;
                        }
                     }
                  }

                  if (!var7) {
                     var6 = false;
                  }
               } else if (var1.func_70302_i_() == var5.func_177267_c().length * var5.func_177267_c()[0].length()) {
                  int var12 = -1;

                  for(int var13 = 0; var13 < var1.func_70302_i_() && var6; ++var13) {
                     var9 = var13 / 3;
                     int var14 = var13 % 3;
                     ItemStack var11 = var1.func_70301_a(var13);
                     if (var11 != null && var11.func_77973_b() != Items.field_179564_cE) {
                        if (var11.func_77973_b() != Items.field_151100_aR) {
                           var6 = false;
                           break;
                        }

                        if (var12 != -1 && var12 != var11.func_77960_j()) {
                           var6 = false;
                           break;
                        }

                        if (var5.func_177267_c()[var9].charAt(var14) == ' ') {
                           var6 = false;
                           break;
                        }

                        var12 = var11.func_77960_j();
                     } else if (var5.func_177267_c()[var9].charAt(var14) != ' ') {
                        var6 = false;
                        break;
                     }
                  }
               } else {
                  var6 = false;
               }

               if (var6) {
                  return var5;
               }
            }
         }

         return null;
      }

      // $FF: synthetic method
      RecipeAddPattern(Object var1) {
         this();
      }
   }

   static class RecipeDuplicatePattern implements IRecipe {
      private RecipeDuplicatePattern() {
         super();
      }

      public boolean func_77569_a(InventoryCrafting var1, World var2) {
         ItemStack var3 = null;
         ItemStack var4 = null;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (var6 != null) {
               if (var6.func_77973_b() != Items.field_179564_cE) {
                  return false;
               }

               if (var3 != null && var4 != null) {
                  return false;
               }

               int var7 = TileEntityBanner.func_175111_b(var6);
               boolean var8 = TileEntityBanner.func_175113_c(var6) > 0;
               if (var3 != null) {
                  if (var8) {
                     return false;
                  }

                  if (var7 != TileEntityBanner.func_175111_b(var3)) {
                     return false;
                  }

                  var4 = var6;
               } else if (var4 != null) {
                  if (!var8) {
                     return false;
                  }

                  if (var7 != TileEntityBanner.func_175111_b(var4)) {
                     return false;
                  }

                  var3 = var6;
               } else if (var8) {
                  var3 = var6;
               } else {
                  var4 = var6;
               }
            }
         }

         return var3 != null && var4 != null;
      }

      public ItemStack func_77572_b(InventoryCrafting var1) {
         for(int var2 = 0; var2 < var1.func_70302_i_(); ++var2) {
            ItemStack var3 = var1.func_70301_a(var2);
            if (var3 != null && TileEntityBanner.func_175113_c(var3) > 0) {
               ItemStack var4 = var3.func_77946_l();
               var4.field_77994_a = 1;
               return var4;
            }
         }

         return null;
      }

      public int func_77570_a() {
         return 2;
      }

      public ItemStack func_77571_b() {
         return null;
      }

      public ItemStack[] func_179532_b(InventoryCrafting var1) {
         ItemStack[] var2 = new ItemStack[var1.func_70302_i_()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            ItemStack var4 = var1.func_70301_a(var3);
            if (var4 != null) {
               if (var4.func_77973_b().func_77634_r()) {
                  var2[var3] = new ItemStack(var4.func_77973_b().func_77668_q());
               } else if (var4.func_77942_o() && TileEntityBanner.func_175113_c(var4) > 0) {
                  var2[var3] = var4.func_77946_l();
                  var2[var3].field_77994_a = 1;
               }
            }
         }

         return var2;
      }

      // $FF: synthetic method
      RecipeDuplicatePattern(Object var1) {
         this();
      }
   }
}
