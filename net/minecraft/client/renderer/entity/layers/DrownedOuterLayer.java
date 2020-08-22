package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedOuterLayer extends RenderLayer {
   private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
   private final DrownedModel model = new DrownedModel(0.25F, 0.0F, 64, 64);

   public DrownedOuterLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Zombie var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, 1.0F, 1.0F, 1.0F);
   }
}
