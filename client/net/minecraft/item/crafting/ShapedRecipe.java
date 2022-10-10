package net.minecraft.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class ShapedRecipe implements IRecipe {
   private final int field_77576_b;
   private final int field_77577_c;
   private final NonNullList<Ingredient> field_77574_d;
   private final ItemStack field_77575_e;
   private final ResourceLocation field_199561_e;
   private final String field_194137_e;

   public ShapedRecipe(ResourceLocation var1, String var2, int var3, int var4, NonNullList<Ingredient> var5, ItemStack var6) {
      super();
      this.field_199561_e = var1;
      this.field_194137_e = var2;
      this.field_77576_b = var3;
      this.field_77577_c = var4;
      this.field_77574_d = var5;
      this.field_77575_e = var6;
   }

   public ResourceLocation func_199560_c() {
      return this.field_199561_e;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199575_a;
   }

   public String func_193358_e() {
      return this.field_194137_e;
   }

   public ItemStack func_77571_b() {
      return this.field_77575_e;
   }

   public NonNullList<Ingredient> func_192400_c() {
      return this.field_77574_d;
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 >= this.field_77576_b && var2 >= this.field_77577_c;
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         for(int var3 = 0; var3 <= var1.func_174922_i() - this.field_77576_b; ++var3) {
            for(int var4 = 0; var4 <= var1.func_174923_h() - this.field_77577_c; ++var4) {
               if (this.func_77573_a(var1, var3, var4, true)) {
                  return true;
               }

               if (this.func_77573_a(var1, var3, var4, false)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean func_77573_a(IInventory var1, int var2, int var3, boolean var4) {
      for(int var5 = 0; var5 < var1.func_174922_i(); ++var5) {
         for(int var6 = 0; var6 < var1.func_174923_h(); ++var6) {
            int var7 = var5 - var2;
            int var8 = var6 - var3;
            Ingredient var9 = Ingredient.field_193370_a;
            if (var7 >= 0 && var8 >= 0 && var7 < this.field_77576_b && var8 < this.field_77577_c) {
               if (var4) {
                  var9 = (Ingredient)this.field_77574_d.get(this.field_77576_b - var7 - 1 + var8 * this.field_77576_b);
               } else {
                  var9 = (Ingredient)this.field_77574_d.get(var7 + var8 * this.field_77576_b);
               }
            }

            if (!var9.test(var1.func_70301_a(var5 + var6 * var1.func_174922_i()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack func_77572_b(IInventory var1) {
      return this.func_77571_b().func_77946_l();
   }

   public int func_192403_f() {
      return this.field_77576_b;
   }

   public int func_192404_g() {
      return this.field_77577_c;
   }

   private static NonNullList<Ingredient> func_192402_a(String[] var0, Map<String, Ingredient> var1, int var2, int var3) {
      NonNullList var4 = NonNullList.func_191197_a(var2 * var3, Ingredient.field_193370_a);
      HashSet var5 = Sets.newHashSet(var1.keySet());
      var5.remove(" ");

      for(int var6 = 0; var6 < var0.length; ++var6) {
         for(int var7 = 0; var7 < var0[var6].length(); ++var7) {
            String var8 = var0[var6].substring(var7, var7 + 1);
            Ingredient var9 = (Ingredient)var1.get(var8);
            if (var9 == null) {
               throw new JsonSyntaxException("Pattern references symbol '" + var8 + "' but it's not defined in the key");
            }

            var5.remove(var8);
            var4.set(var7 + var2 * var6, var9);
         }
      }

      if (!var5.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + var5);
      } else {
         return var4;
      }
   }

   @VisibleForTesting
   static String[] func_194134_a(String... var0) {
      int var1 = 2147483647;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         String var6 = var0[var5];
         var1 = Math.min(var1, func_194135_a(var6));
         int var7 = func_194136_b(var6);
         var2 = Math.max(var2, var7);
         if (var7 < 0) {
            if (var3 == var5) {
               ++var3;
            }

            ++var4;
         } else {
            var4 = 0;
         }
      }

      if (var0.length == var4) {
         return new String[0];
      } else {
         String[] var8 = new String[var0.length - var4 - var3];

         for(int var9 = 0; var9 < var8.length; ++var9) {
            var8[var9] = var0[var9 + var3].substring(var1, var2 + 1);
         }

         return var8;
      }
   }

   private static int func_194135_a(String var0) {
      int var1;
      for(var1 = 0; var1 < var0.length() && var0.charAt(var1) == ' '; ++var1) {
      }

      return var1;
   }

   private static int func_194136_b(String var0) {
      int var1;
      for(var1 = var0.length() - 1; var1 >= 0 && var0.charAt(var1) == ' '; --var1) {
      }

      return var1;
   }

   private static String[] func_192407_a(JsonArray var0) {
      String[] var1 = new String[var0.size()];
      if (var1.length > 3) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
      } else if (var1.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = JsonUtils.func_151206_a(var0.get(var2), "pattern[" + var2 + "]");
            if (var3.length() > 3) {
               throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }

            if (var2 > 0 && var1[0].length() != var3.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            var1[var2] = var3;
         }

         return var1;
      }
   }

   private static Map<String, Ingredient> func_192408_a(JsonObject var0) {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((String)var3.getKey()).length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + (String)var3.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(var3.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         var1.put(var3.getKey(), Ingredient.func_199802_a((JsonElement)var3.getValue()));
      }

      var1.put(" ", Ingredient.field_193370_a);
      return var1;
   }

   public static ItemStack func_199798_a(JsonObject var0) {
      String var1 = JsonUtils.func_151200_h(var0, "item");
      Item var2 = (Item)IRegistry.field_212630_s.func_212608_b(new ResourceLocation(var1));
      if (var2 == null) {
         throw new JsonSyntaxException("Unknown item '" + var1 + "'");
      } else if (var0.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int var3 = JsonUtils.func_151208_a(var0, "count", 1);
         return new ItemStack(var2, var3);
      }
   }

   public static class Serializer implements IRecipeSerializer<ShapedRecipe> {
      public Serializer() {
         super();
      }

      public ShapedRecipe func_199425_a_(ResourceLocation var1, JsonObject var2) {
         String var3 = JsonUtils.func_151219_a(var2, "group", "");
         Map var4 = ShapedRecipe.func_192408_a(JsonUtils.func_152754_s(var2, "key"));
         String[] var5 = ShapedRecipe.func_194134_a(ShapedRecipe.func_192407_a(JsonUtils.func_151214_t(var2, "pattern")));
         int var6 = var5[0].length();
         int var7 = var5.length;
         NonNullList var8 = ShapedRecipe.func_192402_a(var5, var4, var6, var7);
         ItemStack var9 = ShapedRecipe.func_199798_a(JsonUtils.func_152754_s(var2, "result"));
         return new ShapedRecipe(var1, var3, var6, var7, var8, var9);
      }

      public String func_199567_a() {
         return "crafting_shaped";
      }

      public ShapedRecipe func_199426_a_(ResourceLocation var1, PacketBuffer var2) {
         int var3 = var2.func_150792_a();
         int var4 = var2.func_150792_a();
         String var5 = var2.func_150789_c(32767);
         NonNullList var6 = NonNullList.func_191197_a(var3 * var4, Ingredient.field_193370_a);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            var6.set(var7, Ingredient.func_199566_b(var2));
         }

         ItemStack var8 = var2.func_150791_c();
         return new ShapedRecipe(var1, var5, var3, var4, var6, var8);
      }

      public void func_199427_a_(PacketBuffer var1, ShapedRecipe var2) {
         var1.func_150787_b(var2.field_77576_b);
         var1.func_150787_b(var2.field_77577_c);
         var1.func_180714_a(var2.field_194137_e);
         Iterator var3 = var2.field_77574_d.iterator();

         while(var3.hasNext()) {
            Ingredient var4 = (Ingredient)var3.next();
            var4.func_199564_a(var1);
         }

         var1.func_150788_a(var2.field_77575_e);
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
