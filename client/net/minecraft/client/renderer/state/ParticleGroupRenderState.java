package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.SubmitNodeCollector;

public interface ParticleGroupRenderState {
   void submit(SubmitNodeCollector submitNodeCollector, final CameraRenderState camera);

   default void clear() {
   }
}
