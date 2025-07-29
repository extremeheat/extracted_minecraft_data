package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public abstract class SkullModelBase extends Model<State> {
   public SkullModelBase(ModelPart var1) {
      super(var1, RenderType::entityTranslucent);
   }

   public static class State {
      public float animationPos;
      public float yRot;
      public float xRot;

      public State() {
         super();
      }
   }
}
