package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.FrostbiteModel;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;

public class FrostbiteOuterLayer extends RenderLayer<ZombieRenderState, ZombieModel<ZombieRenderState>> {
   private static final Identifier FROSTBITE_OUTER_LAYER_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/frostbite_outer_layer.png");
   private final FrostbiteModel model;

   public FrostbiteOuterLayer(final RenderLayerParent<ZombieRenderState, ZombieModel<ZombieRenderState>> renderer, final EntityModelSet modelSet) {
      super(renderer);
      this.model = new FrostbiteModel(modelSet.bakeLayer(ModelLayers.FROSTBITE_OUTER_LAYER));
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final ZombieRenderState state, final float yRot, final float xRot) {
      if (!state.isBaby) {
         coloredCutoutModelCopyLayerRender(this.model, FROSTBITE_OUTER_LAYER_LOCATION, poseStack, submitNodeCollector, lightCoords, state, -1, 1);
      }

   }
}
