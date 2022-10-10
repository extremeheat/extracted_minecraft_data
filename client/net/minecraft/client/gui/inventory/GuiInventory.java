package net.minecraft.client.gui.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiInventory extends InventoryEffectRenderer implements IRecipeShownListener {
   private static final ResourceLocation field_201555_w = new ResourceLocation("textures/gui/recipe_button.png");
   private float field_147048_u;
   private float field_147047_v;
   private final GuiRecipeBook field_192045_A = new GuiRecipeBook();
   private boolean field_212353_B;
   private boolean field_192046_B;
   private boolean field_194031_B;

   public GuiInventory(EntityPlayer var1) {
      super(var1.field_71069_bz);
      this.field_146291_p = true;
   }

   public void func_73876_c() {
      if (this.field_146297_k.field_71442_b.func_78758_h()) {
         this.field_146297_k.func_147108_a(new GuiContainerCreative(this.field_146297_k.field_71439_g));
      } else {
         this.field_192045_A.func_193957_d();
      }
   }

   protected void func_73866_w_() {
      if (this.field_146297_k.field_71442_b.func_78758_h()) {
         this.field_146297_k.func_147108_a(new GuiContainerCreative(this.field_146297_k.field_71439_g));
      } else {
         super.func_73866_w_();
         this.field_192046_B = this.field_146294_l < 379;
         this.field_192045_A.func_201520_a(this.field_146294_l, this.field_146295_m, this.field_146297_k, this.field_192046_B, (ContainerRecipeBook)this.field_147002_h);
         this.field_212353_B = true;
         this.field_147003_i = this.field_192045_A.func_193011_a(this.field_192046_B, this.field_146294_l, this.field_146999_f);
         this.field_195124_j.add(this.field_192045_A);
         this.func_189646_b(new GuiButtonImage(10, this.field_147003_i + 104, this.field_146295_m / 2 - 22, 20, 18, 0, 0, 19, field_201555_w) {
            public void func_194829_a(double var1, double var3) {
               GuiInventory.this.field_192045_A.func_201518_a(GuiInventory.this.field_192046_B);
               GuiInventory.this.field_192045_A.func_191866_a();
               GuiInventory.this.field_147003_i = GuiInventory.this.field_192045_A.func_193011_a(GuiInventory.this.field_192046_B, GuiInventory.this.field_146294_l, GuiInventory.this.field_146999_f);
               this.func_191746_c(GuiInventory.this.field_147003_i + 104, GuiInventory.this.field_146295_m / 2 - 22);
               GuiInventory.this.field_194031_B = true;
            }
         });
      }
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.field_192045_A;
   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_211126_b(I18n.func_135052_a("container.crafting"), 97.0F, 8.0F, 4210752);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      this.field_147045_u = !this.field_192045_A.func_191878_b();
      if (this.field_192045_A.func_191878_b() && this.field_192046_B) {
         this.func_146976_a(var3, var1, var2);
         this.field_192045_A.func_191861_a(var1, var2, var3);
      } else {
         this.field_192045_A.func_191861_a(var1, var2, var3);
         super.func_73863_a(var1, var2, var3);
         this.field_192045_A.func_191864_a(this.field_147003_i, this.field_147009_r, false, var3);
      }

      this.func_191948_b(var1, var2);
      this.field_192045_A.func_191876_c(this.field_147003_i, this.field_147009_r, var1, var2);
      this.field_147048_u = (float)var1;
      this.field_147047_v = (float)var2;
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147001_a);
      int var4 = this.field_147003_i;
      int var5 = this.field_147009_r;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      func_147046_a(var4 + 51, var5 + 75, 30, (float)(var4 + 51) - this.field_147048_u, (float)(var5 + 75 - 50) - this.field_147047_v, this.field_146297_k.field_71439_g);
   }

   public static void func_147046_a(int var0, int var1, int var2, float var3, float var4, EntityLivingBase var5) {
      GlStateManager.func_179142_g();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var0, (float)var1, 50.0F);
      GlStateManager.func_179152_a((float)(-var2), (float)var2, (float)var2);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      float var6 = var5.field_70761_aq;
      float var7 = var5.field_70177_z;
      float var8 = var5.field_70125_A;
      float var9 = var5.field_70758_at;
      float var10 = var5.field_70759_as;
      GlStateManager.func_179114_b(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.func_74519_b();
      GlStateManager.func_179114_b(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-((float)Math.atan((double)(var4 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      var5.field_70761_aq = (float)Math.atan((double)(var3 / 40.0F)) * 20.0F;
      var5.field_70177_z = (float)Math.atan((double)(var3 / 40.0F)) * 40.0F;
      var5.field_70125_A = -((float)Math.atan((double)(var4 / 40.0F))) * 20.0F;
      var5.field_70759_as = var5.field_70177_z;
      var5.field_70758_at = var5.field_70177_z;
      GlStateManager.func_179109_b(0.0F, 0.0F, 0.0F);
      RenderManager var11 = Minecraft.func_71410_x().func_175598_ae();
      var11.func_178631_a(180.0F);
      var11.func_178633_a(false);
      var11.func_188391_a(var5, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
      var11.func_178633_a(true);
      var5.field_70761_aq = var6;
      var5.field_70177_z = var7;
      var5.field_70125_A = var8;
      var5.field_70758_at = var9;
      var5.field_70759_as = var10;
      GlStateManager.func_179121_F();
      RenderHelper.func_74518_a();
      GlStateManager.func_179101_C();
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   protected boolean func_195359_a(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.field_192046_B || !this.field_192045_A.func_191878_b()) && super.func_195359_a(var1, var2, var3, var4, var5, var7);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.field_192045_A.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.field_192046_B && this.field_192045_A.func_191878_b() ? false : super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.field_194031_B) {
         this.field_194031_B = false;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   protected boolean func_195361_a(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.field_146999_f) || var3 >= (double)(var6 + this.field_147000_g);
      return this.field_192045_A.func_195604_a(var1, var3, this.field_147003_i, this.field_147009_r, this.field_146999_f, this.field_147000_g, var7) && var8;
   }

   protected void func_184098_a(Slot var1, int var2, int var3, ClickType var4) {
      super.func_184098_a(var1, var2, var3, var4);
      this.field_192045_A.func_191874_a(var1);
   }

   public void func_192043_J_() {
      this.field_192045_A.func_193948_e();
   }

   public void func_146281_b() {
      if (this.field_212353_B) {
         this.field_192045_A.func_191871_c();
      }

      super.func_146281_b();
   }

   public GuiRecipeBook func_194310_f() {
      return this.field_192045_A;
   }
}
