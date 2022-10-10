package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShapelessRecipe implements IRecipe {
   private final ResourceLocation field_199562_a;
   private final String field_194138_c;
   private final ItemStack field_77580_a;
   private final NonNullList<Ingredient> field_77579_b;

   public ShapelessRecipe(ResourceLocation var1, String var2, ItemStack var3, NonNullList<Ingredient> var4) {
      super();
      this.field_199562_a = var1;
      this.field_194138_c = var2;
      this.field_77580_a = var3;
      this.field_77579_b = var4;
   }

   public ResourceLocation func_199560_c() {
      return this.field_199562_a;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199576_b;
   }

   public String func_193358_e() {
      return this.field_194138_c;
   }

   public ItemStack func_77571_b() {
      return this.field_77580_a;
   }

   public NonNullList<Ingredient> func_192400_c() {
      return this.field_77579_b;
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         RecipeItemHelper var3 = new RecipeItemHelper();
         int var4 = 0;

         for(int var5 = 0; var5 < var1.func_174923_h(); ++var5) {
            for(int var6 = 0; var6 < var1.func_174922_i(); ++var6) {
               ItemStack var7 = var1.func_70301_a(var6 + var5 * var1.func_174922_i());
               if (!var7.func_190926_b()) {
                  ++var4;
                  var3.func_194112_a(new ItemStack(var7.func_77973_b()));
               }
            }
         }

         return var4 == this.field_77579_b.size() && var3.func_194116_a(this, (IntList)null);
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      return this.field_77580_a.func_77946_l();
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 * var2 >= this.field_77579_b.size();
   }

   public static class Serializer implements IRecipeSerializer<ShapelessRecipe> {
      public Serializer() {
         super();
      }

      public ShapelessRecipe func_199425_a_(ResourceLocation var1, JsonObject var2) {
         String var3 = JsonUtils.func_151219_a(var2, "group", "");
         NonNullList var4 = func_199568_a(JsonUtils.func_151214_t(var2, "ingredients"));
         if (var4.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (var4.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack var5 = ShapedRecipe.func_199798_a(JsonUtils.func_152754_s(var2, "result"));
            return new ShapelessRecipe(var1, var3, var5, var4);
         }
      }

      private static NonNullList<Ingredient> func_199568_a(JsonArray var0) {
         NonNullList var1 = NonNullList.func_191196_a();

         for(int var2 = 0; var2 < var0.size(); ++var2) {
            Ingredient var3 = Ingredient.func_199802_a(var0.get(var2));
            if (!var3.func_203189_d()) {
               var1.add(var3);
            }
         }

         return var1;
      }

      public String func_199567_a() {
         return "crafting_shapeless";
      }

      public ShapelessRecipe func_199426_a_(ResourceLocation var1, PacketBuffer var2) {
         String var3 = var2.func_150789_c(32767);
         int var4 = var2.func_150792_a();
         NonNullList var5 = NonNullList.func_191197_a(var4, Ingredient.field_193370_a);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, Ingredient.func_199566_b(var2));
         }

         ItemStack var7 = var2.func_150791_c();
         return new ShapelessRecipe(var1, var3, var7, var5);
      }

      public void func_199427_a_(PacketBuffer var1, ShapelessRecipe var2) {
         var1.func_180714_a(var2.field_194138_c);
         var1.func_150787_b(var2.field_77579_b.size());
         Iterator var3 = var2.field_77579_b.iterator();

         while(var3.hasNext()) {
            Ingredient var4 = (Ingredient)var3.next();
            var4.func_199564_a(var1);
         }

         var1.func_150788_a(var2.field_77580_a);
      }

      // $FF: synthetic method
      public IRecipe func_199426_a_(ResourceLocation var1, PacketBuffer var2) {
         return this.func_199426_a_(var1, var2);
      }

      // $FF: synthetic method
      public IRecipe func_199425_a_(ResourceLocation var1, JsonObject var2) {
         return this.func_199425_a_(var1, var2);
      }
   }
}
