package net.minecraft.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BannerAddPatternRecipe extends IRecipeHidden {
   public BannerAddPatternRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (var5.func_77973_b() instanceof ItemBanner) {
               if (var3) {
                  return false;
               }

               if (TileEntityBanner.func_175113_c(var5) >= 6) {
                  return false;
               }

               var3 = true;
            }
         }

         return var3 && this.func_201838_c(var1) != null;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = ItemStack.field_190927_a;

      for(int var3 = 0; var3 < var1.func_70302_i_(); ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (!var4.func_190926_b() && var4.func_77973_b() instanceof ItemBanner) {
            var2 = var4.func_77946_l();
            var2.func_190920_e(1);
            break;
         }
      }

      BannerPattern var8 = this.func_201838_c(var1);
      if (var8 != null) {
         EnumDyeColor var9 = EnumDyeColor.WHITE;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            Item var6 = var1.func_70301_a(var5).func_77973_b();
            if (var6 instanceof ItemDye) {
               var9 = ((ItemDye)var6).func_195962_g();
               break;
            }
         }

         NBTTagCompound var10 = var2.func_190925_c("BlockEntityTag");
         NBTTagList var11;
         if (var10.func_150297_b("Patterns", 9)) {
            var11 = var10.func_150295_c("Patterns", 10);
         } else {
            var11 = new NBTTagList();
            var10.func_74782_a("Patterns", var11);
         }

         NBTTagCompound var7 = new NBTTagCompound();
         var7.func_74778_a("Pattern", var8.func_190993_b());
         var7.func_74768_a("Color", var9.func_196059_a());
         var11.add((INBTBase)var7);
      }

      return var2;
   }

   @Nullable
   private BannerPattern func_201838_c(IInventory var1) {
      BannerPattern[] var2 = BannerPattern.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BannerPattern var5 = var2[var4];
         if (var5.func_191000_d()) {
            boolean var6 = true;
            int var9;
            if (var5.func_190999_e()) {
               boolean var7 = false;
               boolean var8 = false;

               for(var9 = 0; var9 < var1.func_70302_i_() && var6; ++var9) {
                  ItemStack var10 = var1.func_70301_a(var9);
                  if (!var10.func_190926_b() && !(var10.func_77973_b() instanceof ItemBanner)) {
                     if (var10.func_77973_b() instanceof ItemDye) {
                        if (var8) {
                           var6 = false;
                           break;
                        }

                        var8 = true;
                     } else {
                        if (var7 || !var10.func_77969_a(var5.func_190998_f())) {
                           var6 = false;
                           break;
                        }

                        var7 = true;
                     }
                  }
               }

               if (!var7 || !var8) {
                  var6 = false;
               }
            } else if (var1.func_70302_i_() == var5.func_190996_c().length * var5.func_190996_c()[0].length()) {
               EnumDyeColor var14 = null;

               for(int var15 = 0; var15 < var1.func_70302_i_() && var6; ++var15) {
                  var9 = var15 / 3;
                  int var16 = var15 % 3;
                  ItemStack var11 = var1.func_70301_a(var15);
                  Item var12 = var11.func_77973_b();
                  if (!var11.func_190926_b() && !(var12 instanceof ItemBanner)) {
                     if (!(var12 instanceof ItemDye)) {
                        var6 = false;
                        break;
                     }

                     EnumDyeColor var13 = ((ItemDye)var12).func_195962_g();
                     if (var14 != null && var14 != var13) {
                        var6 = false;
                        break;
                     }

                     if (var5.func_190996_c()[var9].charAt(var16) == ' ') {
                        var6 = false;
                        break;
                     }

                     var14 = var13;
                  } else if (var5.func_190996_c()[var9].charAt(var16) != ' ') {
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

   public boolean func_194133_a(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199587_m;
   }
}
