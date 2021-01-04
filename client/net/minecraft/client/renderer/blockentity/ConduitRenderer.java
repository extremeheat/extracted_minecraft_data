package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

public class ConduitRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
   private static final ResourceLocation SHELL_TEXTURE = new ResourceLocation("textures/entity/conduit/base.png");
   private static final ResourceLocation ACTIVE_SHELL_TEXTURE = new ResourceLocation("textures/entity/conduit/cage.png");
   private static final ResourceLocation WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind.png");
   private static final ResourceLocation VERTICAL_WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind_vertical.png");
   private static final ResourceLocation OPEN_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/open_eye.png");
   private static final ResourceLocation CLOSED_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/closed_eye.png");
   private final ConduitRenderer.ShellModel shellModel = new ConduitRenderer.ShellModel();
   private final ConduitRenderer.CageModel cageModel = new ConduitRenderer.CageModel();
   private final ConduitRenderer.WindModel windModel = new ConduitRenderer.WindModel();
   private final ConduitRenderer.EyeModel eyeModel = new ConduitRenderer.EyeModel();

   public ConduitRenderer() {
      super();
   }

   public void render(ConduitBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = (float)var1.tickCount + var8;
      float var11;
      if (!var1.isActive()) {
         var11 = var1.getActiveRotation(0.0F);
         this.bindTexture(SHELL_TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         GlStateManager.rotatef(var11, 0.0F, 1.0F, 0.0F);
         this.shellModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      } else if (var1.isActive()) {
         var11 = var1.getActiveRotation(var8) * 57.295776F;
         float var12 = Mth.sin(var10 * 0.1F) / 2.0F + 0.5F;
         var12 += var12 * var12;
         this.bindTexture(ACTIVE_SHELL_TEXTURE);
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.3F + var12 * 0.2F, (float)var6 + 0.5F);
         GlStateManager.rotatef(var11, 0.5F, 1.0F, 0.5F);
         this.cageModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
         boolean var13 = true;
         int var14 = var1.tickCount / 3 % 22;
         this.windModel.setActiveAnim(var14);
         int var15 = var1.tickCount / 66 % 3;
         switch(var15) {
         case 0:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 1:
            this.bindTexture(VERTICAL_WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 2:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
         }

         Camera var16 = this.blockEntityRenderDispatcher.camera;
         if (var1.isHunting()) {
            this.bindTexture(OPEN_EYE_TEXTURE);
         } else {
            this.bindTexture(CLOSED_EYE_TEXTURE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.3F + var12 * 0.2F, (float)var6 + 0.5F);
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.rotatef(-var16.getYRot(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(var16.getXRot(), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         this.eyeModel.render(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
         GlStateManager.popMatrix();
      }

      super.render(var1, var2, var4, var6, var8, var9);
   }

   static class EyeModel extends Model {
      private final ModelPart eye;

      public EyeModel() {
         super();
         this.texWidth = 8;
         this.texHeight = 8;
         this.eye = new ModelPart(this, 0, 0);
         this.eye.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
      }

      public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.eye.render(var6);
      }
   }

   static class WindModel extends Model {
      private final ModelPart[] box = new ModelPart[22];
      private int activeAnim;

      public WindModel() {
         super();
         this.texWidth = 64;
         this.texHeight = 1024;

         for(int var1 = 0; var1 < 22; ++var1) {
            this.box[var1] = new ModelPart(this, 0, 32 * var1);
            this.box[var1].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
         }

      }

      public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.box[this.activeAnim].render(var6);
      }

      public void setActiveAnim(int var1) {
         this.activeAnim = var1;
      }
   }

   static class CageModel extends Model {
      private final ModelPart box;

      public CageModel() {
         super();
         this.texWidth = 32;
         this.texHeight = 16;
         this.box = new ModelPart(this, 0, 0);
         this.box.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      }

      public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.box.render(var6);
      }
   }

   static class ShellModel extends Model {
      private final ModelPart box;

      public ShellModel() {
         super();
         this.texWidth = 32;
         this.texHeight = 16;
         this.box = new ModelPart(this, 0, 0);
         this.box.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      }

      public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.box.render(var6);
      }
   }
}
