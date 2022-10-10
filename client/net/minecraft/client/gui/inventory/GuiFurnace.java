package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.recipebook.GuiFurnaceRecipeBook;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation field_147087_u = new ResourceLocation("textures/gui/container/furnace.png");
   private static final ResourceLocation field_201558_x = new ResourceLocation("textures/gui/recipe_button.png");
   private final InventoryPlayer field_175383_v;
   private final IInventory field_147086_v;
   public final GuiFurnaceRecipeBook field_201557_v = new GuiFurnaceRecipeBook();
   private boolean field_201556_A;

   public GuiFurnace(InventoryPlayer var1, IInventory var2) {
      super(new ContainerFurnace(var1, var2));
      this.field_175383_v = var1;
      this.field_147086_v = var2;
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_201556_A = this.field_146294_l < 379;
      this.field_201557_v.func_201520_a(this.field_146294_l, this.field_146295_m, this.field_146297_k, this.field_201556_A, (ContainerRecipeBook)this.field_147002_h);
      this.field_147003_i = this.field_201557_v.func_193011_a(this.field_201556_A, this.field_146294_l, this.field_146999_f);
      this.func_189646_b(new GuiButtonImage(10, this.field_147003_i + 20, this.field_146295_m / 2 - 49, 20, 18, 0, 0, 19, field_201558_x) {
         public void func_194829_a(double var1, double var3) {
            GuiFurnace.this.field_201557_v.func_201518_a(GuiFurnace.this.field_201556_A);
            GuiFurnace.this.field_201557_v.func_191866_a();
            GuiFurnace.this.field_147003_i = GuiFurnace.this.field_201557_v.func_193011_a(GuiFurnace.this.field_201556_A, GuiFurnace.this.field_146294_l, GuiFurnace.this.field_146999_f);
            this.func_191746_c(GuiFurnace.this.field_147003_i + 20, GuiFurnace.this.field_146295_m / 2 - 49);
         }
      });
   }

   public void func_73876_c() {
      super.func_73876_c();
      this.field_201557_v.func_193957_d();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      if (this.field_201557_v.func_191878_b() && this.field_201556_A) {
         this.func_146976_a(var3, var1, var2);
         this.field_201557_v.func_191861_a(var1, var2, var3);
      } else {
         this.field_201557_v.func_191861_a(var1, var2, var3);
         super.func_73863_a(var1, var2, var3);
         this.field_201557_v.func_191864_a(this.field_147003_i, this.field_147009_r, true, var3);
      }

      this.func_191948_b(var1, var2);
      this.field_201557_v.func_191876_c(this.field_147003_i, this.field_147009_r, var1, var2);
   }

   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147086_v.func_145748_c_().func_150254_d();
      this.field_146289_q.func_211126_b(var3, (float)(this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2), 6.0F, 4210752);
      this.field_146289_q.func_211126_b(this.field_175383_v.func_145748_c_().func_150254_d(), 8.0F, (float)(this.field_147000_g - 96 + 2), 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147087_u);
      int var4 = this.field_147003_i;
      int var5 = this.field_147009_r;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      int var6;
      if (TileEntityFurnace.func_174903_a(this.field_147086_v)) {
         var6 = this.func_175382_i(13);
         this.func_73729_b(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 1);
      }

      var6 = this.func_175381_h(24);
      this.func_73729_b(var4 + 79, var5 + 34, 176, 14, var6 + 1, 16);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_201557_v.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.field_201556_A && this.field_201557_v.func_191878_b() ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected void func_184098_a(Slot var1, int var2, int var3, ClickType var4) {
      super.func_184098_a(var1, var2, var3, var4);
      this.field_201557_v.func_191874_a(var1);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.field_201557_v.keyPressed(var1, var2, var3) ? false : super.keyPressed(var1, var2, var3);
   }

   protected boolean func_195361_a(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.field_146999_f) || var3 >= (double)(var6 + this.field_147000_g);
      return this.field_201557_v.func_195604_a(var1, var3, this.field_147003_i, this.field_147009_r, this.field_146999_f, this.field_147000_g, var7) && var8;
   }

   public boolean charTyped(char var1, int var2) {
      return this.field_201557_v.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public void func_192043_J_() {
      this.field_201557_v.func_193948_e();
   }

   public GuiRecipeBook func_194310_f() {
      return this.field_201557_v;
   }

   public void func_146281_b() {
      this.field_201557_v.func_191871_c();
      super.func_146281_b();
   }

   private int func_175381_h(int var1) {
      int var2 = this.field_147086_v.func_174887_a_(2);
      int var3 = this.field_147086_v.func_174887_a_(3);
      return var3 != 0 && var2 != 0 ? var2 * var1 / var3 : 0;
   }

   private int func_175382_i(int var1) {
      int var2 = this.field_147086_v.func_174887_a_(1);
      if (var2 == 0) {
         var2 = 200;
      }

      return this.field_147086_v.func_174887_a_(0) * var1 / var2;
   }
}
