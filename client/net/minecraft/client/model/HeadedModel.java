package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;

public interface HeadedModel {
   ModelPart getHead();

   default void translateToHead(PoseStack var1) {
      this.getHead().translateAndRotate(var1);
   }
}
