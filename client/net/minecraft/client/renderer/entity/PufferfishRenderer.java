package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer extends MobRenderer<Pufferfish, EntityModel<Pufferfish>> {
   private static final ResourceLocation PUFFER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fish/pufferfish.png");
   private int puffStateO = 3;
   private final EntityModel<Pufferfish> small;
   private final EntityModel<Pufferfish> mid;
   private final EntityModel<Pufferfish> big = this.getModel();

   public PufferfishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PufferfishBigModel(var1.bakeLayer(ModelLayers.PUFFERFISH_BIG)), 0.2F);
      this.mid = new PufferfishMidModel(var1.bakeLayer(ModelLayers.PUFFERFISH_MEDIUM));
      this.small = new PufferfishSmallModel(var1.bakeLayer(ModelLayers.PUFFERFISH_SMALL));
   }

   public ResourceLocation getTextureLocation(Pufferfish var1) {
      return PUFFER_LOCATION;
   }

   public void render(Pufferfish var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      int var7 = var1.getPuffState();
      if (var7 != this.puffStateO) {
         if (var7 == 0) {
            this.model = this.small;
         } else if (var7 == 1) {
            this.model = this.mid;
         } else {
            this.model = this.big;
         }
      }

      this.puffStateO = var7;
      this.shadowRadius = 0.1F + 0.1F * (float)var7;
      super.render(var1, var2, var3, var4, var5, var6);
   }

   protected void setupRotations(Pufferfish var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      var2.translate(0.0F, Mth.cos(var3 * 0.05F) * 0.08F, 0.0F);
      super.setupRotations(var1, var2, var3, var4, var5, var6);
   }
}
