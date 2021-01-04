package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SpinAttackEffectLayer<T extends LivingEntity> extends RenderLayer<T, PlayerModel<T>> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident_riptide.png");
   private final SpinAttackEffectLayer.SpinAttackModel model = new SpinAttackEffectLayer.SpinAttackModel();

   public SpinAttackEffectLayer(RenderLayerParent<T, PlayerModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.isAutoSpinAttack()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(TEXTURE);

         for(int var9 = 0; var9 < 3; ++var9) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(var5 * (float)(-(45 + var9 * 5)), 0.0F, 1.0F, 0.0F);
            float var10 = 0.75F * (float)var9;
            GlStateManager.scalef(var10, var10, var10);
            GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)var9, 0.0F);
            this.model.render(var2, var3, var5, var6, var7, var8);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean colorsOnDamage() {
      return false;
   }

   static class SpinAttackModel extends Model {
      private final ModelPart box;

      public SpinAttackModel() {
         super();
         this.texWidth = 64;
         this.texHeight = 64;
         this.box = new ModelPart(this, 0, 0);
         this.box.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
      }

      public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.box.render(var6);
      }
   }
}
