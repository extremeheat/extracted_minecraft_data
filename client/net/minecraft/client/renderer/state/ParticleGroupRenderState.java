package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.SubmitNodeCollector;

public interface ParticleGroupRenderState {
   void submit(SubmitNodeCollector var1, CameraRenderState var2);

   default void clear() {
   }
}
