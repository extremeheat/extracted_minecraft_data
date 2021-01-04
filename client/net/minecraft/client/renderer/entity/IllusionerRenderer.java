package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.Vec3;

public class IllusionerRenderer extends IllagerRenderer<Illusioner> {
   private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRenderDispatcher var1) {
      super(var1, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new ItemInHandLayer<Illusioner, IllagerModel<Illusioner>>(this) {
         public void render(Illusioner var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
            if (var1.isCastingSpell() || var1.isAggressive()) {
               super.render((LivingEntity)var1, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      });
      ((IllagerModel)this.model).getHat().visible = true;
   }

   protected ResourceLocation getTextureLocation(Illusioner var1) {
      return ILLUSIONER;
   }

   public void render(Illusioner var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.isInvisible()) {
         Vec3[] var10 = var1.getIllusionOffsets(var9);
         float var11 = this.getBob(var1, var9);

         for(int var12 = 0; var12 < var10.length; ++var12) {
            super.render(var1, var2 + var10[var12].x + (double)Mth.cos((float)var12 + var11 * 0.5F) * 0.025D, var4 + var10[var12].y + (double)Mth.cos((float)var12 + var11 * 0.75F) * 0.0125D, var6 + var10[var12].z + (double)Mth.cos((float)var12 + var11 * 0.7F) * 0.025D, var8, var9);
         }
      } else {
         super.render(var1, var2, var4, var6, var8, var9);
      }

   }

   protected boolean isVisible(Illusioner var1) {
      return true;
   }

   // $FF: synthetic method
   protected boolean isVisible(LivingEntity var1) {
      return this.isVisible((Illusioner)var1);
   }
}
