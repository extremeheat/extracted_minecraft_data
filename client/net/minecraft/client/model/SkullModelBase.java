package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public abstract class SkullModelBase extends Model {
   public SkullModelBase(ModelPart var1) {
      super(var1, RenderType::entityTranslucent);
   }

   public abstract void setupAnim(float var1, float var2, float var3);
}
