package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiInventory extends InventoryEffectRenderer {
   private float field_147048_u;
   private float field_147047_v;

   public GuiInventory(EntityPlayer var1) {
      super(var1.field_71069_bz);
      this.field_146291_p = true;
   }

   public void func_73876_c() {
      if (this.field_146297_k.field_71442_b.func_78758_h()) {
         this.field_146297_k.func_147108_a(new GuiContainerCreative(this.field_146297_k.field_71439_g));
      }

      this.func_175378_g();
   }

   public void func_73866_w_() {
      this.field_146292_n.clear();
      if (this.field_146297_k.field_71442_b.func_78758_h()) {
         this.field_146297_k.func_147108_a(new GuiContainerCreative(this.field_146297_k.field_71439_g));
      } else {
         super.func_73866_w_();
      }

   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.crafting"), 86, 16, 4210752);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
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
      var11.func_147940_a(var5, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
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

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == 0) {
         this.field_146297_k.func_147108_a(new GuiAchievements(this, this.field_146297_k.field_71439_g.func_146107_m()));
      }

      if (var1.field_146127_k == 1) {
         this.field_146297_k.func_147108_a(new GuiStats(this, this.field_146297_k.field_71439_g.func_146107_m()));
      }

   }
}
