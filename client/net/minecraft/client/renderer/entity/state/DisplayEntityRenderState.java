package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Display;

public abstract class DisplayEntityRenderState extends EntityRenderState {
   @Nullable
   public Display.RenderState renderState;
   public float interpolationProgress;
   public float entityYRot;
   public float entityXRot;
   public float cameraYRot;
   public float cameraXRot;

   public DisplayEntityRenderState() {
      super();
   }

   public abstract boolean hasSubState();
}
