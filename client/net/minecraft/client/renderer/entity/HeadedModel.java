package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelPart;

public interface HeadedModel {
   ModelPart getHead();

   default void translateToHead(float var1) {
      this.getHead().translateTo(var1);
   }
}
