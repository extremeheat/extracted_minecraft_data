package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class FurnaceRecipe implements IRecipe {
   private final ResourceLocation field_201832_a;
   private final String field_201833_b;
   private final Ingredient field_201834_c;
   private final ItemStack field_201835_d;
   private final float field_201836_e;
   private final int field_201837_f;

   public FurnaceRecipe(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super();
      this.field_201832_a = var1;
      this.field_201833_b = var2;
      this.field_201834_c = var3;
      this.field_201835_d = var4;
      this.field_201836_e = var5;
      this.field_201837_f = var6;
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      return var1 instanceof TileEntityFurnace && this.field_201834_c.test(var1.func_70301_a(0));
   }

   public ItemStack func_77572_b(IInventory var1) {
      return this.field_201835_d.func_77946_l();
   }

   public boolean func_194133_a(int var1, int var2) {
      return true;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_201839_p;
   }

   public NonNullList<Ingredient> func_192400_c() {
      NonNullList var1 = NonNullList.func_191196_a();
      var1.add(this.field_201834_c);
      return var1;
   }

   public float func_201831_g() {
      return this.field_201836_e;
   }

   public ItemStack func_77571_b() {
      return this.field_201835_d;
   }

   public String func_193358_e() {
      return this.field_201833_b;
   }

   public int func_201830_h() {
      return this.field_201837_f;
   }

   public ResourceLocation func_199560_c() {
      return this.field_201832_a;
   }

   public static class Serializer implements IRecipeSerializer<FurnaceRecipe> {
      public Serializer() {
         super();
      }

      public FurnaceRecipe func_199425_a_(ResourceLocation var1, JsonObject var2) {
         String var3 = JsonUtils.func_151219_a(var2, "group", "");
         Ingredient var4;
         if (JsonUtils.func_151202_d(var2, "ingredient")) {
            var4 = Ingredient.func_199802_a(JsonUtils.func_151214_t(var2, "ingredient"));
         } else {
            var4 = Ingredient.func_199802_a(JsonUtils.func_152754_s(var2, "ingredient"));
         }

         String var6 = JsonUtils.func_151200_h(var2, "result");
         Item var7 = (Item)IRegistry.field_212630_s.func_212608_b(new ResourceLocation(var6));
         if (var7 != null) {
            ItemStack var5 = new ItemStack(var7);
            float var8 = JsonUtils.func_151221_a(var2, "experience", 0.0F);
            int var9 = JsonUtils.func_151208_a(var2, "cookingtime", 200);
            return new FurnaceRecipe(var1, var3, var4, var5, var8, var9);
         } else {
            throw new IllegalStateException(var6 + " did not exist");
         }
      }

      public FurnaceRecipe func_199426_a_(ResourceLocation var1, PacketBuffer var2) {
         String var3 = var2.func_150789_c(32767);
         Ingredient var4 = Ingredient.func_199566_b(var2);
         ItemStack var5 = var2.func_150791_c();
         float var6 = var2.readFloat();
         int var7 = var2.func_150792_a();
         return new FurnaceRecipe(var1, var3, var4, var5, var6, var7);
      }

      public void func_199427_a_(PacketBuffer var1, FurnaceRecipe var2) {
         var1.func_180714_a(var2.field_201833_b);
         var2.field_201834_c.func_199564_a(var1);
         var1.func_150788_a(var2.field_201835_d);
         var1.writeFloat(var2.field_201836_e);
         var1.func_150787_b(var2.field_201837_f);
      }

      public String func_199567_a() {
         return "smelting";
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
