package net.minecraft.client.gui.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;

public class GuiFurnaceRecipeBook extends GuiRecipeBook {
   private Iterator<Item> field_201525_l;
   private Set<Item> field_201526_m;
   private Slot field_201527_n;
   private Item field_201528_o;
   private float field_201524_p;

   public GuiFurnaceRecipeBook() {
      super();
   }

   protected boolean func_201521_f() {
      boolean var1 = !this.field_193964_s.func_202884_d();
      this.field_193964_s.func_202882_d(var1);
      return var1;
   }

   public boolean func_191878_b() {
      return this.field_193964_s.func_202883_c();
   }

   protected void func_193006_a(boolean var1) {
      this.field_193964_s.func_202881_c(var1);
      if (!var1) {
         this.field_193022_s.func_194200_c();
      }

      this.func_193956_j();
   }

   protected void func_205702_a() {
      this.field_193960_m.func_191751_a(152, 182, 28, 18, field_191894_a);
   }

   protected String func_205703_f() {
      return I18n.func_135052_a(this.field_193960_m.func_191754_c() ? "gui.recipebook.toggleRecipes.smeltable" : "gui.recipebook.toggleRecipes.all");
   }

   public void func_191874_a(@Nullable Slot var1) {
      super.func_191874_a(var1);
      if (var1 != null && var1.field_75222_d < this.field_201522_g.func_203721_h()) {
         this.field_201527_n = null;
      }

   }

   public void func_193951_a(IRecipe var1, List<Slot> var2) {
      ItemStack var3 = var1.func_77571_b();
      this.field_191915_z.func_192685_a(var1);
      this.field_191915_z.func_194187_a(Ingredient.func_193369_a(var3), ((Slot)var2.get(2)).field_75223_e, ((Slot)var2.get(2)).field_75221_f);
      NonNullList var4 = var1.func_192400_c();
      this.field_201527_n = (Slot)var2.get(1);
      if (this.field_201526_m == null) {
         this.field_201526_m = TileEntityFurnace.func_201564_p().keySet();
      }

      this.field_201525_l = this.field_201526_m.iterator();
      this.field_201528_o = null;
      Iterator var5 = var4.iterator();

      for(int var6 = 0; var6 < 2; ++var6) {
         if (!var5.hasNext()) {
            return;
         }

         Ingredient var7 = (Ingredient)var5.next();
         if (!var7.func_203189_d()) {
            Slot var8 = (Slot)var2.get(var6);
            this.field_191915_z.func_194187_a(var7, var8.field_75223_e, var8.field_75221_f);
         }
      }

   }

   public void func_191864_a(int var1, int var2, boolean var3, float var4) {
      super.func_191864_a(var1, var2, var3, var4);
      if (this.field_201527_n != null) {
         if (!GuiScreen.func_146271_m()) {
            this.field_201524_p += var4;
         }

         RenderHelper.func_74520_c();
         GlStateManager.func_179140_f();
         int var5 = this.field_201527_n.field_75223_e + var1;
         int var6 = this.field_201527_n.field_75221_f + var2;
         Gui.func_73734_a(var5, var6, var5 + 16, var6 + 16, 822018048);
         this.field_191888_F.func_175599_af().func_184391_a(this.field_191888_F.field_71439_g, this.func_201523_i().func_190903_i(), var5, var6);
         GlStateManager.func_179143_c(516);
         Gui.func_73734_a(var5, var6, var5 + 16, var6 + 16, 822083583);
         GlStateManager.func_179143_c(515);
         GlStateManager.func_179145_e();
         RenderHelper.func_74518_a();
      }
   }

   private Item func_201523_i() {
      if (this.field_201528_o == null || this.field_201524_p > 30.0F) {
         this.field_201524_p = 0.0F;
         if (this.field_201525_l == null || !this.field_201525_l.hasNext()) {
            if (this.field_201526_m == null) {
               this.field_201526_m = TileEntityFurnace.func_201564_p().keySet();
            }

            this.field_201525_l = this.field_201526_m.iterator();
         }

         this.field_201528_o = (Item)this.field_201525_l.next();
      }

      return this.field_201528_o;
   }
}
