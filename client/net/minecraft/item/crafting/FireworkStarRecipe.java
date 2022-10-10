package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class FireworkStarRecipe extends IRecipeHidden {
   private static final Ingredient field_196212_a;
   private static final Ingredient field_196213_b;
   private static final Ingredient field_196214_c;
   private static final Map<Item, ItemFireworkRocket.Shape> field_196215_d;
   private static final Ingredient field_196216_e;

   public FireworkStarRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;

         for(int var8 = 0; var8 < var1.func_70302_i_(); ++var8) {
            ItemStack var9 = var1.func_70301_a(var8);
            if (!var9.func_190926_b()) {
               if (field_196212_a.test(var9)) {
                  if (var5) {
                     return false;
                  }

                  var5 = true;
               } else if (field_196214_c.test(var9)) {
                  if (var7) {
                     return false;
                  }

                  var7 = true;
               } else if (field_196213_b.test(var9)) {
                  if (var6) {
                     return false;
                  }

                  var6 = true;
               } else if (field_196216_e.test(var9)) {
                  if (var3) {
                     return false;
                  }

                  var3 = true;
               } else {
                  if (!(var9.func_77973_b() instanceof ItemDye)) {
                     return false;
                  }

                  var4 = true;
               }
            }
         }

         return var3 && var4;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = new ItemStack(Items.field_196153_dF);
      NBTTagCompound var3 = var2.func_190925_c("Explosion");
      ItemFireworkRocket.Shape var4 = ItemFireworkRocket.Shape.SMALL_BALL;
      ArrayList var5 = Lists.newArrayList();

      for(int var6 = 0; var6 < var1.func_70302_i_(); ++var6) {
         ItemStack var7 = var1.func_70301_a(var6);
         if (!var7.func_190926_b()) {
            if (field_196212_a.test(var7)) {
               var4 = (ItemFireworkRocket.Shape)field_196215_d.get(var7.func_77973_b());
            } else if (field_196214_c.test(var7)) {
               var3.func_74757_a("Flicker", true);
            } else if (field_196213_b.test(var7)) {
               var3.func_74757_a("Trail", true);
            } else if (var7.func_77973_b() instanceof ItemDye) {
               var5.add(((ItemDye)var7.func_77973_b()).func_195962_g().func_196060_f());
            }
         }
      }

      var3.func_197646_b("Colors", var5);
      var3.func_74774_a("Type", (byte)var4.func_196071_a());
      return var2;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack func_77571_b() {
      return new ItemStack(Items.field_196153_dF);
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199582_h;
   }

   static {
      field_196212_a = Ingredient.func_199804_a(Items.field_151059_bz, Items.field_151008_G, Items.field_151074_bl, Items.field_196182_dv, Items.field_196183_dw, Items.field_196185_dy, Items.field_196184_dx, Items.field_196151_dA, Items.field_196186_dz);
      field_196213_b = Ingredient.func_199804_a(Items.field_151045_i);
      field_196214_c = Ingredient.func_199804_a(Items.field_151114_aO);
      field_196215_d = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
         var0.put(Items.field_151059_bz, ItemFireworkRocket.Shape.LARGE_BALL);
         var0.put(Items.field_151008_G, ItemFireworkRocket.Shape.BURST);
         var0.put(Items.field_151074_bl, ItemFireworkRocket.Shape.STAR);
         var0.put(Items.field_196182_dv, ItemFireworkRocket.Shape.CREEPER);
         var0.put(Items.field_196183_dw, ItemFireworkRocket.Shape.CREEPER);
         var0.put(Items.field_196185_dy, ItemFireworkRocket.Shape.CREEPER);
         var0.put(Items.field_196184_dx, ItemFireworkRocket.Shape.CREEPER);
         var0.put(Items.field_196151_dA, ItemFireworkRocket.Shape.CREEPER);
         var0.put(Items.field_196186_dz, ItemFireworkRocket.Shape.CREEPER);
      });
      field_196216_e = Ingredient.func_199804_a(Items.field_151016_H);
   }
}
