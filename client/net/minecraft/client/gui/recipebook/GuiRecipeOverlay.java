package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiRecipeOverlay extends Gui implements IGuiEventListener {
   private static final ResourceLocation field_191847_a = new ResourceLocation("textures/gui/recipe_book.png");
   private final List<GuiRecipeOverlay.Button> field_193972_f = Lists.newArrayList();
   private boolean field_191850_h;
   private int field_191851_i;
   private int field_191852_j;
   private Minecraft field_191853_k;
   private RecipeList field_191848_f;
   private IRecipe field_193973_l;
   private float field_193974_m;
   private boolean field_201704_n;

   public GuiRecipeOverlay() {
      super();
   }

   public void func_201703_a(Minecraft var1, RecipeList var2, int var3, int var4, int var5, int var6, float var7) {
      this.field_191853_k = var1;
      this.field_191848_f = var2;
      if (var1.field_71439_g.field_71070_bA instanceof ContainerFurnace) {
         this.field_201704_n = true;
      }

      boolean var8 = var1.field_71439_g.func_199507_B().func_203432_a((ContainerRecipeBook)var1.field_71439_g.field_71070_bA);
      List var9 = var2.func_194207_b(true);
      List var10 = var8 ? Collections.emptyList() : var2.func_194207_b(false);
      int var11 = var9.size();
      int var12 = var11 + var10.size();
      int var13 = var12 <= 16 ? 4 : 5;
      int var14 = (int)Math.ceil((double)((float)var12 / (float)var13));
      this.field_191851_i = var3;
      this.field_191852_j = var4;
      boolean var15 = true;
      float var16 = (float)(this.field_191851_i + Math.min(var12, var13) * 25);
      float var17 = (float)(var5 + 50);
      if (var16 > var17) {
         this.field_191851_i = (int)((float)this.field_191851_i - var7 * (float)((int)((var16 - var17) / var7)));
      }

      float var18 = (float)(this.field_191852_j + var14 * 25);
      float var19 = (float)(var6 + 50);
      if (var18 > var19) {
         this.field_191852_j = (int)((float)this.field_191852_j - var7 * (float)MathHelper.func_76123_f((var18 - var19) / var7));
      }

      float var20 = (float)this.field_191852_j;
      float var21 = (float)(var6 - 100);
      if (var20 < var21) {
         this.field_191852_j = (int)((float)this.field_191852_j - var7 * (float)MathHelper.func_76123_f((var20 - var21) / var7));
      }

      this.field_191850_h = true;
      this.field_193972_f.clear();

      for(int var22 = 0; var22 < var12; ++var22) {
         boolean var23 = var22 < var11;
         IRecipe var24 = var23 ? (IRecipe)var9.get(var22) : (IRecipe)var10.get(var22 - var11);
         int var25 = this.field_191851_i + 4 + 25 * (var22 % var13);
         int var26 = this.field_191852_j + 5 + 25 * (var22 / var13);
         if (this.field_201704_n) {
            this.field_193972_f.add(new GuiRecipeOverlay.FurnaceButton(var25, var26, var24, var23));
         } else {
            this.field_193972_f.add(new GuiRecipeOverlay.Button(var25, var26, var24, var23));
         }
      }

      this.field_193973_l = null;
   }

   public RecipeList func_193971_a() {
      return this.field_191848_f;
   }

   public IRecipe func_193967_b() {
      return this.field_193973_l;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 != 0) {
         return false;
      } else {
         Iterator var6 = this.field_193972_f.iterator();

         GuiRecipeOverlay.Button var7;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            var7 = (GuiRecipeOverlay.Button)var6.next();
         } while(!var7.mouseClicked(var1, var3, var5));

         this.field_193973_l = var7.field_193924_p;
         return true;
      }
   }

   public void func_191842_a(int var1, int var2, float var3) {
      if (this.field_191850_h) {
         this.field_193974_m += var3;
         RenderHelper.func_74520_c();
         GlStateManager.func_179147_l();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_191853_k.func_110434_K().func_110577_a(field_191847_a);
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 0.0F, 170.0F);
         int var4 = this.field_193972_f.size() <= 16 ? 4 : 5;
         int var5 = Math.min(this.field_193972_f.size(), var4);
         int var6 = MathHelper.func_76123_f((float)this.field_193972_f.size() / (float)var4);
         boolean var7 = true;
         boolean var8 = true;
         boolean var9 = true;
         boolean var10 = true;
         this.func_191846_c(var5, var6, 24, 4, 82, 208);
         GlStateManager.func_179084_k();
         RenderHelper.func_74518_a();
         Iterator var11 = this.field_193972_f.iterator();

         while(var11.hasNext()) {
            GuiRecipeOverlay.Button var12 = (GuiRecipeOverlay.Button)var11.next();
            var12.func_194828_a(var1, var2, var3);
         }

         GlStateManager.func_179121_F();
      }
   }

   private void func_191846_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_73729_b(this.field_191851_i, this.field_191852_j, var5, var6, var4, var4);
      this.func_73729_b(this.field_191851_i + var4 * 2 + var1 * var3, this.field_191852_j, var5 + var3 + var4, var6, var4, var4);
      this.func_73729_b(this.field_191851_i, this.field_191852_j + var4 * 2 + var2 * var3, var5, var6 + var3 + var4, var4, var4);
      this.func_73729_b(this.field_191851_i + var4 * 2 + var1 * var3, this.field_191852_j + var4 * 2 + var2 * var3, var5 + var3 + var4, var6 + var3 + var4, var4, var4);

      for(int var7 = 0; var7 < var1; ++var7) {
         this.func_73729_b(this.field_191851_i + var4 + var7 * var3, this.field_191852_j, var5 + var4, var6, var3, var4);
         this.func_73729_b(this.field_191851_i + var4 + (var7 + 1) * var3, this.field_191852_j, var5 + var4, var6, var4, var4);

         for(int var8 = 0; var8 < var2; ++var8) {
            if (var7 == 0) {
               this.func_73729_b(this.field_191851_i, this.field_191852_j + var4 + var8 * var3, var5, var6 + var4, var4, var3);
               this.func_73729_b(this.field_191851_i, this.field_191852_j + var4 + (var8 + 1) * var3, var5, var6 + var4, var4, var4);
            }

            this.func_73729_b(this.field_191851_i + var4 + var7 * var3, this.field_191852_j + var4 + var8 * var3, var5 + var4, var6 + var4, var3, var3);
            this.func_73729_b(this.field_191851_i + var4 + (var7 + 1) * var3, this.field_191852_j + var4 + var8 * var3, var5 + var4, var6 + var4, var4, var3);
            this.func_73729_b(this.field_191851_i + var4 + var7 * var3, this.field_191852_j + var4 + (var8 + 1) * var3, var5 + var4, var6 + var4, var3, var4);
            this.func_73729_b(this.field_191851_i + var4 + (var7 + 1) * var3 - 1, this.field_191852_j + var4 + (var8 + 1) * var3 - 1, var5 + var4, var6 + var4, var4 + 1, var4 + 1);
            if (var7 == var1 - 1) {
               this.func_73729_b(this.field_191851_i + var4 * 2 + var1 * var3, this.field_191852_j + var4 + var8 * var3, var5 + var3 + var4, var6 + var4, var4, var3);
               this.func_73729_b(this.field_191851_i + var4 * 2 + var1 * var3, this.field_191852_j + var4 + (var8 + 1) * var3, var5 + var3 + var4, var6 + var4, var4, var4);
            }
         }

         this.func_73729_b(this.field_191851_i + var4 + var7 * var3, this.field_191852_j + var4 * 2 + var2 * var3, var5 + var4, var6 + var3 + var4, var3, var4);
         this.func_73729_b(this.field_191851_i + var4 + (var7 + 1) * var3, this.field_191852_j + var4 * 2 + var2 * var3, var5 + var4, var6 + var3 + var4, var4, var4);
      }

   }

   public void func_192999_a(boolean var1) {
      this.field_191850_h = var1;
   }

   public boolean func_191839_a() {
      return this.field_191850_h;
   }

   class Button extends GuiButton implements IRecipePlacer<Ingredient> {
      private final IRecipe field_193924_p;
      private final boolean field_193925_q;
      protected final List<GuiRecipeOverlay.Button.Child> field_201506_o = Lists.newArrayList();

      public Button(int var2, int var3, IRecipe var4, boolean var5) {
         super(0, var2, var3, "");
         this.field_146120_f = 24;
         this.field_146121_g = 24;
         this.field_193924_p = var4;
         this.field_193925_q = var5;
         this.func_201505_a(var4);
      }

      protected void func_201505_a(IRecipe var1) {
         this.func_201501_a(3, 3, -1, var1, var1.func_192400_c().iterator(), 0);
      }

      public void func_201500_a(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
         ItemStack[] var6 = ((Ingredient)var1.next()).func_193365_a();
         if (var6.length != 0) {
            this.field_201506_o.add(new GuiRecipeOverlay.Button.Child(3 + var5 * 7, 3 + var4 * 7, var6));
         }

      }

      public void func_194828_a(int var1, int var2, float var3) {
         RenderHelper.func_74520_c();
         GlStateManager.func_179141_d();
         GuiRecipeOverlay.this.field_191853_k.func_110434_K().func_110577_a(GuiRecipeOverlay.field_191847_a);
         this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
         int var4 = 152;
         if (!this.field_193925_q) {
            var4 += 26;
         }

         int var5 = GuiRecipeOverlay.this.field_201704_n ? 130 : 78;
         if (this.field_146123_n) {
            var5 += 26;
         }

         this.func_73729_b(this.field_146128_h, this.field_146129_i, var4, var5, this.field_146120_f, this.field_146121_g);
         Iterator var6 = this.field_201506_o.iterator();

         while(var6.hasNext()) {
            GuiRecipeOverlay.Button.Child var7 = (GuiRecipeOverlay.Button.Child)var6.next();
            GlStateManager.func_179094_E();
            float var8 = 0.42F;
            int var9 = (int)((float)(this.field_146128_h + var7.field_201706_b) / 0.42F - 3.0F);
            int var10 = (int)((float)(this.field_146129_i + var7.field_201707_c) / 0.42F - 3.0F);
            GlStateManager.func_179152_a(0.42F, 0.42F, 1.0F);
            GlStateManager.func_179145_e();
            GuiRecipeOverlay.this.field_191853_k.func_175599_af().func_180450_b(var7.field_201705_a[MathHelper.func_76141_d(GuiRecipeOverlay.this.field_193974_m / 30.0F) % var7.field_201705_a.length], var9, var10);
            GlStateManager.func_179140_f();
            GlStateManager.func_179121_F();
         }

         GlStateManager.func_179118_c();
         RenderHelper.func_74518_a();
      }

      public class Child {
         public ItemStack[] field_201705_a;
         public int field_201706_b;
         public int field_201707_c;

         public Child(int var2, int var3, ItemStack[] var4) {
            super();
            this.field_201706_b = var2;
            this.field_201707_c = var3;
            this.field_201705_a = var4;
         }
      }
   }

   class FurnaceButton extends GuiRecipeOverlay.Button {
      public FurnaceButton(int var2, int var3, IRecipe var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      protected void func_201505_a(IRecipe var1) {
         ItemStack[] var2 = ((Ingredient)var1.func_192400_c().get(0)).func_193365_a();
         this.field_201506_o.add(new GuiRecipeOverlay.Button.Child(10, 10, var2));
      }
   }
}
