package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface SubmitNodeCollector extends OrderedSubmitNodeCollector {
   OrderedSubmitNodeCollector order(int var1);

   public interface CustomGeometryRenderer {
      void render(PoseStack.Pose var1, VertexConsumer var2);
   }
}
