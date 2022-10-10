package net.minecraft.client.gui.recipebook;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class GuiButtonRecipeTab extends GuiButtonToggle {
   private final RecipeBookCategories field_193921_u;
   private float field_193922_v;

   public GuiButtonRecipeTab(int var1, RecipeBookCategories var2) {
      super(var1, 0, 0, 35, 27, false);
      this.field_193921_u = var2;
      this.func_191751_a(153, 2, 35, 0, GuiRecipeBook.field_191894_a);
   }

   public void func_193918_a(Minecraft var1) {
      RecipeBookClient var2 = var1.field_71439_g.func_199507_B();
      List var3 = var2.func_202891_a(this.field_193921_u);
      if (var1.field_71439_g.field_71070_bA instanceof ContainerRecipeBook) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            RecipeList var5 = (RecipeList)var4.next();
            Iterator var6 = var5.func_194208_a(var2.func_203432_a((ContainerRecipeBook)var1.field_71439_g.field_71070_bA)).iterator();

            while(var6.hasNext()) {
               IRecipe var7 = (IRecipe)var6.next();
               if (var2.func_194076_e(var7)) {
                  this.field_193922_v = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void func_194828_a(int var1, int var2, float var3) {
      if (this.field_146125_m) {
         if (this.field_193922_v > 0.0F) {
            float var4 = 1.0F + 0.1F * (float)Math.sin((double)(this.field_193922_v / 15.0F * 3.1415927F));
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(this.field_146128_h + 8), (float)(this.field_146129_i + 12), 0.0F);
            GlStateManager.func_179152_a(1.0F, var4, 1.0F);
            GlStateManager.func_179109_b((float)(-(this.field_146128_h + 8)), (float)(-(this.field_146129_i + 12)), 0.0F);
         }

         this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
         Minecraft var8 = Minecraft.func_71410_x();
         var8.func_110434_K().func_110577_a(this.field_191760_o);
         GlStateManager.func_179097_i();
         int var5 = this.field_191756_q;
         int var6 = this.field_191757_r;
         if (this.field_191755_p) {
            var5 += this.field_191758_s;
         }

         if (this.field_146123_n) {
            var6 += this.field_191759_t;
         }

         int var7 = this.field_146128_h;
         if (this.field_191755_p) {
            var7 -= 2;
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_73729_b(var7, this.field_146129_i, var5, var6, this.field_146120_f, this.field_146121_g);
         GlStateManager.func_179126_j();
         RenderHelper.func_74520_c();
         GlStateManager.func_179140_f();
         this.func_193920_a(var8.func_175599_af());
         GlStateManager.func_179145_e();
         RenderHelper.func_74518_a();
         if (this.field_193922_v > 0.0F) {
            GlStateManager.func_179121_F();
            this.field_193922_v -= var3;
         }

      }
   }

   private void func_193920_a(ItemRenderer var1) {
      List var2 = this.field_193921_u.func_202903_a();
      int var3 = this.field_191755_p ? -2 : 0;
      if (var2.size() == 1) {
         var1.func_180450_b((ItemStack)var2.get(0), this.field_146128_h + 9 + var3, this.field_146129_i + 5);
      } else if (var2.size() == 2) {
         var1.func_180450_b((ItemStack)var2.get(0), this.field_146128_h + 3 + var3, this.field_146129_i + 5);
         var1.func_180450_b((ItemStack)var2.get(1), this.field_146128_h + 14 + var3, this.field_146129_i + 5);
      }

   }

   public RecipeBookCategories func_201503_d() {
      return this.field_193921_u;
   }

   public boolean func_199500_a(RecipeBookClient var1) {
      List var2 = var1.func_202891_a(this.field_193921_u);
      this.field_146125_m = false;
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            RecipeList var4 = (RecipeList)var3.next();
            if (var4.func_194209_a() && var4.func_194212_c()) {
               this.field_146125_m = true;
               break;
            }
         }
      }

      return this.field_146125_m;
   }
}
