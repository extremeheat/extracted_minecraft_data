package net.minecraft.client.gui.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiCrafting extends GuiContainer implements IRecipeShownListener {
   private static final ResourceLocation field_147019_u = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation field_201559_w = new ResourceLocation("textures/gui/recipe_button.png");
   private final GuiRecipeBook field_192050_x;
   private boolean field_193112_y;
   private final InventoryPlayer field_212354_A;

   public GuiCrafting(InventoryPlayer var1, World var2) {
      this(var1, var2, BlockPos.field_177992_a);
   }

   public GuiCrafting(InventoryPlayer var1, World var2, BlockPos var3) {
      super(new ContainerWorkbench(var1, var2, var3));
      this.field_192050_x = new GuiRecipeBook();
      this.field_212354_A = var1;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_193112_y = this.field_146294_l < 379;
      this.field_192050_x.func_201520_a(this.field_146294_l, this.field_146295_m, this.field_146297_k, this.field_193112_y, (ContainerRecipeBook)this.field_147002_h);
      this.field_147003_i = this.field_192050_x.func_193011_a(this.field_193112_y, this.field_146294_l, this.field_146999_f);
      this.field_195124_j.add(this.field_192050_x);
      this.func_189646_b(new GuiButtonImage(10, this.field_147003_i + 5, this.field_146295_m / 2 - 49, 20, 18, 0, 0, 19, field_201559_w) {
         public void func_194829_a(double var1, double var3) {
            GuiCrafting.this.field_192050_x.func_201518_a(GuiCrafting.this.field_193112_y);
            GuiCrafting.this.field_192050_x.func_191866_a();
            GuiCrafting.this.field_147003_i = GuiCrafting.this.field_192050_x.func_193011_a(GuiCrafting.this.field_193112_y, GuiCrafting.this.field_146294_l, GuiCrafting.this.field_146999_f);
            this.func_191746_c(GuiCrafting.this.field_147003_i + 5, GuiCrafting.this.field_146295_m / 2 - 49);
         }
      });
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_192050_x;
   }

   public void func_73876_c() {
      super.func_73876_c();
      this.field_192050_x.func_193957_d();
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      if (this.field_192050_x.func_191878_b() && this.field_193112_y) {
         this.func_146976_a(var3, var1, var2);
         this.field_192050_x.func_191861_a(var1, var2, var3);
      } else {
         this.field_192050_x.func_191861_a(var1, var2, var3);
         super.func_73863_a(var1, var2, var3);
         this.field_192050_x.func_191864_a(this.field_147003_i, this.field_147009_r, true, var3);
      }

      this.func_191948_b(var1, var2);
      this.field_192050_x.func_191876_c(this.field_147003_i, this.field_147009_r, var1, var2);
   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_211126_b(I18n.func_135052_a("container.crafting"), 28.0F, 6.0F, 4210752);
      this.field_146289_q.func_211126_b(this.field_212354_A.func_145748_c_().func_150254_d(), 8.0F, (float)(this.field_147000_g - 96 + 2), 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147019_u);
      int var4 = this.field_147003_i;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
   }

   protected boolean func_195359_a(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.field_193112_y || !this.field_192050_x.func_191878_b()) && super.func_195359_a(var1, var2, var3, var4, var5, var7);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_192050_x.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.field_193112_y && this.field_192050_x.func_191878_b() ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected boolean func_195361_a(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.field_146999_f) || var3 >= (double)(var6 + this.field_147000_g);
      return this.field_192050_x.func_195604_a(var1, var3, this.field_147003_i, this.field_147009_r, this.field_146999_f, this.field_147000_g, var7) && var8;
   }

   protected void func_184098_a(Slot var1, int var2, int var3, ClickType var4) {
      super.func_184098_a(var1, var2, var3, var4);
      this.field_192050_x.func_191874_a(var1);
   }

   public void func_192043_J_() {
      this.field_192050_x.func_193948_e();
   }

   public void func_146281_b() {
      this.field_192050_x.func_191871_c();
      super.func_146281_b();
   }

   public GuiRecipeBook func_194310_f() {
      return this.field_192050_x;
   }
}
