package net.minecraft.item.crafting;

import java.util.Iterator;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class RecipesMapExtending extends ShapedRecipe {
   public RecipesMapExtending(ResourceLocation var1) {
      super(var1, "", 3, 3, NonNullList.func_193580_a(Ingredient.field_193370_a, Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151098_aY), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF), Ingredient.func_199804_a(Items.field_151121_aF)), new ItemStack(Items.field_151148_bJ));
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!super.func_77569_a(var1, var2)) {
         return false;
      } else {
         ItemStack var3 = ItemStack.field_190927_a;

         for(int var4 = 0; var4 < var1.func_70302_i_() && var3.func_190926_b(); ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (var5.func_77973_b() == Items.field_151098_aY) {
               var3 = var5;
            }
         }

         if (var3.func_190926_b()) {
            return false;
         } else {
            MapData var6 = ItemMap.func_195950_a(var3, var2);
            if (var6 == null) {
               return false;
            } else if (this.func_190934_a(var6)) {
               return false;
            } else {
               return var6.field_76197_d < 4;
            }
         }
      }
   }

   private boolean func_190934_a(MapData var1) {
      if (var1.field_76203_h != null) {
         Iterator var2 = var1.field_76203_h.values().iterator();

         while(var2.hasNext()) {
            MapDecoration var3 = (MapDecoration)var2.next();
            if (var3.func_191179_b() == MapDecoration.Type.MANSION || var3.func_191179_b() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = ItemStack.field_190927_a;

      for(int var3 = 0; var3 < var1.func_70302_i_() && var2.func_190926_b(); ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (var4.func_77973_b() == Items.field_151098_aY) {
            var2 = var4;
         }
      }

      var2 = var2.func_77946_l();
      var2.func_190920_e(1);
      var2.func_196082_o().func_74768_a("map_scale_direction", 1);
      return var2;
   }

   public boolean func_192399_d() {
      return true;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199580_f;
   }
}
