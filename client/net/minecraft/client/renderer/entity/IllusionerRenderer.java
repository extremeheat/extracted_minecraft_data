package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.client.renderer.entity.state.IllusionerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class IllusionerRenderer extends IllagerRenderer<Illusioner, IllusionerRenderState> {
   private static final ResourceLocation ILLUSIONER = ResourceLocation.withDefaultNamespace("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
      this.addLayer(new ItemInHandLayer<IllusionerRenderState, IllagerModel<IllusionerRenderState>>(this, this, var1.getItemRenderer()) {
         public void render(PoseStack var1, MultiBufferSource var2, int var3, IllusionerRenderState var4, float var5, float var6) {
            if (var4.isCastingSpell || var4.isAggressive) {
               super.render(var1, var2, var3, (LivingEntityRenderState)var4, var5, var6);
            }

         }
      });
      ((IllagerModel)this.model).getHat().visible = true;
   }

   public ResourceLocation getTextureLocation(IllusionerRenderState var1) {
      return ILLUSIONER;
   }

   public IllusionerRenderState createRenderState() {
      return new IllusionerRenderState();
   }

   public void extractRenderState(Illusioner var1, IllusionerRenderState var2, float var3) {
      super.extractRenderState((AbstractIllager)var1, (IllagerRenderState)var2, var3);
      Vec3[] var4 = var1.getIllusionOffsets(var3);
      var2.illusionOffsets = (Vec3[])Arrays.copyOf(var4, var4.length);
      var2.isCastingSpell = var1.isCastingSpell();
   }

   public void render(IllusionerRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (var1.isInvisible) {
         Vec3[] var5 = var1.illusionOffsets;

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2.pushPose();
            var2.translate(var5[var6].x + (double)Mth.cos((float)var6 + var1.ageInTicks * 0.5F) * 0.025, var5[var6].y + (double)Mth.cos((float)var6 + var1.ageInTicks * 0.75F) * 0.0125, var5[var6].z + (double)Mth.cos((float)var6 + var1.ageInTicks * 0.7F) * 0.025);
            super.render(var1, var2, var3, var4);
            var2.popPose();
         }
      } else {
         super.render(var1, var2, var3, var4);
      }

   }

   protected boolean isBodyVisible(IllusionerRenderState var1) {
      return true;
   }

   protected AABB getBoundingBoxForCulling(Illusioner var1) {
      return super.getBoundingBoxForCulling(var1).inflate(3.0, 0.0, 3.0);
   }

   // $FF: synthetic method
   protected boolean isBodyVisible(final LivingEntityRenderState var1) {
      return this.isBodyVisible((IllusionerRenderState)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((IllusionerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
