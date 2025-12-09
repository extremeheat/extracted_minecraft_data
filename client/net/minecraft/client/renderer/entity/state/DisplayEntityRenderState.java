package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Display;
import org.jspecify.annotations.Nullable;

public abstract class DisplayEntityRenderState extends EntityRenderState {
   public Display.@Nullable RenderState renderState;
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
