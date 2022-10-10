package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;

public class RecipeToast implements IToast {
   private final List<IRecipe> field_202906_c = Lists.newArrayList();
   private long field_193667_d;
   private boolean field_193668_e;

   public RecipeToast(IRecipe var1) {
      super();
      this.field_202906_c.add(var1);
   }

   public IToast.Visibility func_193653_a(GuiToast var1, long var2) {
      if (this.field_193668_e) {
         this.field_193667_d = var2;
         this.field_193668_e = false;
      }

      if (this.field_202906_c.isEmpty()) {
         return IToast.Visibility.HIDE;
      } else {
         var1.func_192989_b().func_110434_K().func_110577_a(field_193654_a);
         GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
         var1.func_73729_b(0, 0, 0, 32, 160, 32);
         var1.func_192989_b().field_71466_p.func_211126_b(I18n.func_135052_a("recipe.toast.title"), 30.0F, 7.0F, -11534256);
         var1.func_192989_b().field_71466_p.func_211126_b(I18n.func_135052_a("recipe.toast.description"), 30.0F, 18.0F, -16777216);
         RenderHelper.func_74520_c();
         IRecipe var4 = (IRecipe)this.field_202906_c.get((int)(var2 / (5000L / (long)this.field_202906_c.size()) % (long)this.field_202906_c.size()));
         ItemStack var5;
         if (var4 instanceof FurnaceRecipe) {
            var5 = new ItemStack(Blocks.field_150460_al);
         } else {
            var5 = new ItemStack(Blocks.field_150462_ai);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(0.6F, 0.6F, 1.0F);
         var1.func_192989_b().func_175599_af().func_184391_a((EntityLivingBase)null, var5, 3, 3);
         GlStateManager.func_179121_F();
         var1.func_192989_b().func_175599_af().func_184391_a((EntityLivingBase)null, var4.func_77571_b(), 8, 8);
         return var2 - this.field_193667_d >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      }
   }

   public void func_202905_a(IRecipe var1) {
      if (this.field_202906_c.add(var1)) {
         this.field_193668_e = true;
      }

   }

   public static void func_193665_a(GuiToast var0, IRecipe var1) {
      RecipeToast var2 = (RecipeToast)var0.func_192990_a(RecipeToast.class, field_193655_b);
      if (var2 == null) {
         var0.func_192988_a(new RecipeToast(var1));
      } else {
         var2.func_202905_a(var1);
      }

   }
}
