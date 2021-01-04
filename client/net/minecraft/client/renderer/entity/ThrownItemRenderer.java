package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
   private final ItemRenderer itemRenderer;
   private final float scale;

   public ThrownItemRenderer(EntityRenderDispatcher var1, ItemRenderer var2, float var3) {
      super(var1);
      this.itemRenderer = var2;
      this.scale = var3;
   }

   public ThrownItemRenderer(EntityRenderDispatcher var1, ItemRenderer var2) {
      this(var1, var2, 1.0F);
   }

   public void render(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2 ? -1 : 1) * this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      this.itemRenderer.renderStatic(((ItemSupplier)var1).getItem(), ItemTransforms.TransformType.GROUND);
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Entity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
