package net.minecraft.client.renderer;

public interface ParticleGroupRenderState {
   void submit(SubmitNodeCollector var1);

   default void clear() {
   }
}
