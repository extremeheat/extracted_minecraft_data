package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.Vec3;

public class IllusionerRenderer extends IllagerRenderer<Illusioner> {
   private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
      this.addLayer(new ItemInHandLayer<Illusioner, IllagerModel<Illusioner>>(this) {
         public void render(PoseStack var1, MultiBufferSource var2, int var3, Illusioner var4, float var5, float var6, float var7, float var8, float var9, float var10) {
            if (var4.isCastingSpell() || var4.isAggressive()) {
               super.render(var1, var2, var3, (LivingEntity)var4, var5, var6, var7, var8, var9, var10);
            }

         }
      });
      ((IllagerModel)this.model).getHat().visible = true;
   }

   public ResourceLocation getTextureLocation(Illusioner var1) {
      return ILLUSIONER;
   }

   public void render(Illusioner var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1.isInvisible()) {
         Vec3[] var7 = var1.getIllusionOffsets(var3);
         float var8 = this.getBob(var1, var3);

         for(int var9 = 0; var9 < var7.length; ++var9) {
            var4.pushPose();
            var4.translate(var7[var9].field_414 + (double)Mth.cos((float)var9 + var8 * 0.5F) * 0.025D, var7[var9].field_415 + (double)Mth.cos((float)var9 + var8 * 0.75F) * 0.0125D, var7[var9].field_416 + (double)Mth.cos((float)var9 + var8 * 0.7F) * 0.025D);
            super.render(var1, var2, var3, var4, var5, var6);
            var4.popPose();
         }
      } else {
         super.render(var1, var2, var3, var4, var5, var6);
      }

   }

   protected boolean isBodyVisible(Illusioner var1) {
      return true;
   }

   // $FF: synthetic method
   protected boolean isBodyVisible(LivingEntity var1) {
      return this.isBodyVisible((Illusioner)var1);
   }
}
