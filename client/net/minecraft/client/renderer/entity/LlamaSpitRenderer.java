package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
   private final LlamaSpitModel<LlamaSpit> model = new LlamaSpitModel();

   public LlamaSpitRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(LlamaSpit var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.rotatef(Mth.lerp(var9, var1.yRotO, var1.yRot) - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(Mth.lerp(var9, var1.xRotO, var1.xRot), 0.0F, 0.0F, 1.0F);
      this.bindTexture(var1);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      this.model.render(var1, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(LlamaSpit var1) {
      return LLAMA_SPIT_LOCATION;
   }
}
