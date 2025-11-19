package net.minecraft.client.model.object.skull;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public abstract class SkullModelBase extends Model<State> {
   public SkullModelBase(ModelPart var1) {
      super(var1, RenderTypes::entityTranslucent);
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
