package net.minecraft.client.gui.recipebook;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiButtonRecipe extends GuiButton {
   private static final ResourceLocation field_191780_o = new ResourceLocation("textures/gui/recipe_book.png");
   private ContainerRecipeBook field_203401_p;
   private RecipeBook field_193930_p;
   private RecipeList field_191774_p;
   private float field_193931_r;
   private float field_191778_t;
   private int field_193932_t;

   public GuiButtonRecipe() {
      super(0, 0, 0, 25, 25, "");
   }

   public void func_203400_a(RecipeList var1, RecipeBookPage var2) {
      this.field_191774_p = var1;
      this.field_203401_p = (ContainerRecipeBook)var2.func_203411_d().field_71439_g.field_71070_bA;
      this.field_193930_p = var2.func_203412_e();
      List var3 = var1.func_194208_a(this.field_193930_p.func_203432_a(this.field_203401_p));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         IRecipe var5 = (IRecipe)var4.next();
         if (this.field_193930_p.func_194076_e(var5)) {
            var2.func_194195_a(var3);
            this.field_191778_t = 15.0F;
            break;
         }
      }

   }

   public RecipeList func_191771_c() {
      return this.field_191774_p;
   }

   public void func_191770_c(int var1, int var2) {
      this.field_146128_h = var1;
      this.field_146129_i = var2;
   }

   public void func_194828_a(int var1, int var2, float var3) {
      if (this.field_146125_m) {
         if (!GuiScreen.func_146271_m()) {
            this.field_193931_r += var3;
         }

         this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
         RenderHelper.func_74520_c();
         Minecraft var4 = Minecraft.func_71410_x();
         var4.func_110434_K().func_110577_a(field_191780_o);
         GlStateManager.func_179140_f();
         int var5 = 29;
         if (!this.field_191774_p.func_192708_c()) {
            var5 += 25;
         }

         int var6 = 206;
         if (this.field_191774_p.func_194208_a(this.field_193930_p.func_203432_a(this.field_203401_p)).size() > 1) {
            var6 += 25;
         }

         boolean var7 = this.field_191778_t > 0.0F;
         if (var7) {
            float var8 = 1.0F + 0.1F * (float)Math.sin((double)(this.field_191778_t / 15.0F * 3.1415927F));
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(this.field_146128_h + 8), (float)(this.field_146129_i + 12), 0.0F);
            GlStateManager.func_179152_a(var8, var8, 1.0F);
            GlStateManager.func_179109_b((float)(-(this.field_146128_h + 8)), (float)(-(this.field_146129_i + 12)), 0.0F);
            this.field_191778_t -= var3;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var6, this.field_146120_f, this.field_146121_g);
         List var11 = this.func_193927_f();
         this.field_193932_t = MathHelper.func_76141_d(this.field_193931_r / 30.0F) % var11.size();
         ItemStack var9 = ((IRecipe)var11.get(this.field_193932_t)).func_77571_b();
         int var10 = 4;
         if (this.field_191774_p.func_194211_e() && this.func_193927_f().size() > 1) {
            var4.func_175599_af().func_180450_b(var9, this.field_146128_h + var10 + 1, this.field_146129_i + var10 + 1);
            --var10;
         }

         var4.func_175599_af().func_180450_b(var9, this.field_146128_h + var10, this.field_146129_i + var10);
         if (var7) {
            GlStateManager.func_179121_F();
         }

         GlStateManager.func_179145_e();
         RenderHelper.func_74518_a();
      }
   }

   private List<IRecipe> func_193927_f() {
      List var1 = this.field_191774_p.func_194207_b(true);
      if (!this.field_193930_p.func_203432_a(this.field_203401_p)) {
         var1.addAll(this.field_191774_p.func_194207_b(false));
      }

      return var1;
   }

   public boolean func_193929_d() {
      return this.func_193927_f().size() == 1;
   }

   public IRecipe func_193760_e() {
      List var1 = this.func_193927_f();
      return (IRecipe)var1.get(this.field_193932_t);
   }

   public List<String> func_191772_a(GuiScreen var1) {
      ItemStack var2 = ((IRecipe)this.func_193927_f().get(this.field_193932_t)).func_77571_b();
      List var3 = var1.func_191927_a(var2);
      if (this.field_191774_p.func_194208_a(this.field_193930_p.func_203432_a(this.field_203401_p)).size() > 1) {
         var3.add(I18n.func_135052_a("gui.recipebook.moreRecipes"));
      }

      return var3;
   }

   public int func_146117_b() {
      return 25;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0 || var5 == 1) {
         boolean var6 = this.func_199400_c(var1, var3);
         if (var6) {
            this.func_146113_a(Minecraft.func_71410_x().func_147118_V());
            if (var5 == 0) {
               this.func_194829_a(var1, var3);
            }

            return true;
         }
      }

      return false;
   }
}
