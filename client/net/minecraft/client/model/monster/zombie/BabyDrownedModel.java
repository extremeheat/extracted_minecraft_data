package net.minecraft.client.model.monster.zombie;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class BabyDrownedModel extends DrownedModel {
   private static final float CUSTOM_HEAD_LAYER_SCALE = 0.75F;

   public BabyDrownedModel(final ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer(final CubeDeformation g) {
      return BabyZombieModel.createBodyLayer(g);
   }

   public float getCustomHeadLayerScale() {
      return 0.75F;
   }
}
