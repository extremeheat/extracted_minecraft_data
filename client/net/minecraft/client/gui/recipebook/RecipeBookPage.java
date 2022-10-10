package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;

public class RecipeBookPage {
   private final List<GuiButtonRecipe> field_193743_h = Lists.newArrayListWithCapacity(20);
   private GuiButtonRecipe field_194201_b;
   private final GuiRecipeOverlay field_194202_c = new GuiRecipeOverlay();
   private Minecraft field_193754_s;
   private final List<IRecipeUpdateListener> field_193757_v = Lists.newArrayList();
   private List<RecipeList> field_194203_f;
   private GuiButtonToggle field_193740_e;
   private GuiButtonToggle field_193741_f;
   private int field_193737_b;
   private int field_193738_c;
   private RecipeBook field_194204_k;
   private IRecipe field_194205_l;
   private RecipeList field_194206_m;

   public RecipeBookPage() {
      super();

      for(int var1 = 0; var1 < 20; ++var1) {
         this.field_193743_h.add(new GuiButtonRecipe());
      }

   }

   public void func_194194_a(Minecraft var1, int var2, int var3) {
      this.field_193754_s = var1;
      this.field_194204_k = var1.field_71439_g.func_199507_B();

      for(int var4 = 0; var4 < this.field_193743_h.size(); ++var4) {
         ((GuiButtonRecipe)this.field_193743_h.get(var4)).func_191770_c(var2 + 11 + 25 * (var4 % 5), var3 + 31 + 25 * (var4 / 5));
      }

      this.field_193740_e = new GuiButtonToggle(0, var2 + 93, var3 + 137, 12, 17, false);
      this.field_193740_e.func_191751_a(1, 208, 13, 18, GuiRecipeBook.field_191894_a);
      this.field_193741_f = new GuiButtonToggle(0, var2 + 38, var3 + 137, 12, 17, true);
      this.field_193741_f.func_191751_a(1, 208, 13, 18, GuiRecipeBook.field_191894_a);
   }

   public void func_193732_a(GuiRecipeBook var1) {
      this.field_193757_v.remove(var1);
      this.field_193757_v.add(var1);
   }

   public void func_194192_a(List<RecipeList> var1, boolean var2) {
      this.field_194203_f = var1;
      this.field_193737_b = (int)Math.ceil((double)var1.size() / 20.0D);
      if (this.field_193737_b <= this.field_193738_c || var2) {
         this.field_193738_c = 0;
      }

      this.func_194198_d();
   }

   private void func_194198_d() {
      int var1 = 20 * this.field_193738_c;

      for(int var2 = 0; var2 < this.field_193743_h.size(); ++var2) {
         GuiButtonRecipe var3 = (GuiButtonRecipe)this.field_193743_h.get(var2);
         if (var1 + var2 < this.field_194203_f.size()) {
            RecipeList var4 = (RecipeList)this.field_194203_f.get(var1 + var2);
            var3.func_203400_a(var4, this);
            var3.field_146125_m = true;
         } else {
            var3.field_146125_m = false;
         }
      }

      this.func_194197_e();
   }

   private void func_194197_e() {
      this.field_193740_e.field_146125_m = this.field_193737_b > 1 && this.field_193738_c < this.field_193737_b - 1;
      this.field_193741_f.field_146125_m = this.field_193737_b > 1 && this.field_193738_c > 0;
   }

   public void func_194191_a(int var1, int var2, int var3, int var4, float var5) {
      if (this.field_193737_b > 1) {
         String var6 = this.field_193738_c + 1 + "/" + this.field_193737_b;
         int var7 = this.field_193754_s.field_71466_p.func_78256_a(var6);
         this.field_193754_s.field_71466_p.func_211126_b(var6, (float)(var1 - var7 / 2 + 73), (float)(var2 + 141), -1);
      }

      RenderHelper.func_74518_a();
      this.field_194201_b = null;
      Iterator var8 = this.field_193743_h.iterator();

      while(var8.hasNext()) {
         GuiButtonRecipe var9 = (GuiButtonRecipe)var8.next();
         var9.func_194828_a(var3, var4, var5);
         if (var9.field_146125_m && var9.func_146115_a()) {
            this.field_194201_b = var9;
         }
      }

      this.field_193741_f.func_194828_a(var3, var4, var5);
      this.field_193740_e.func_194828_a(var3, var4, var5);
      this.field_194202_c.func_191842_a(var3, var4, var5);
   }

   public void func_193721_a(int var1, int var2) {
      if (this.field_193754_s.field_71462_r != null && this.field_194201_b != null && !this.field_194202_c.func_191839_a()) {
         this.field_193754_s.field_71462_r.func_146283_a(this.field_194201_b.func_191772_a(this.field_193754_s.field_71462_r), var1, var2);
      }

   }

   @Nullable
   public IRecipe func_194193_a() {
      return this.field_194205_l;
   }

   @Nullable
   public RecipeList func_194199_b() {
      return this.field_194206_m;
   }

   public void func_194200_c() {
      this.field_194202_c.func_192999_a(false);
   }

   public boolean func_198955_a(double var1, double var3, int var5, int var6, int var7, int var8, int var9) {
      this.field_194205_l = null;
      this.field_194206_m = null;
      if (this.field_194202_c.func_191839_a()) {
         if (this.field_194202_c.mouseClicked(var1, var3, var5)) {
            this.field_194205_l = this.field_194202_c.func_193967_b();
            this.field_194206_m = this.field_194202_c.func_193971_a();
         } else {
            this.field_194202_c.func_192999_a(false);
         }

         return true;
      } else if (this.field_193740_e.mouseClicked(var1, var3, var5)) {
         ++this.field_193738_c;
         this.func_194198_d();
         return true;
      } else if (this.field_193741_f.mouseClicked(var1, var3, var5)) {
         --this.field_193738_c;
         this.func_194198_d();
         return true;
      } else {
         Iterator var10 = this.field_193743_h.iterator();

         GuiButtonRecipe var11;
         do {
            if (!var10.hasNext()) {
               return false;
            }

            var11 = (GuiButtonRecipe)var10.next();
         } while(!var11.mouseClicked(var1, var3, var5));

         if (var5 == 0) {
            this.field_194205_l = var11.func_193760_e();
            this.field_194206_m = var11.func_191771_c();
         } else if (var5 == 1 && !this.field_194202_c.func_191839_a() && !var11.func_193929_d()) {
            this.field_194202_c.func_201703_a(this.field_193754_s, var11.func_191771_c(), var11.field_146128_h, var11.field_146129_i, var6 + var8 / 2, var7 + 13 + var9 / 2, (float)var11.func_146117_b());
         }

         return true;
      }
   }

   public void func_194195_a(List<IRecipe> var1) {
      Iterator var2 = this.field_193757_v.iterator();

      while(var2.hasNext()) {
         IRecipeUpdateListener var3 = (IRecipeUpdateListener)var2.next();
         var3.func_193001_a(var1);
      }

   }

   public Minecraft func_203411_d() {
      return this.field_193754_s;
   }

   public RecipeBook func_203412_e() {
      return this.field_194204_k;
   }
}
