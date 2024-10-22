package net.minecraft.client.renderer.entity.state;

public class WitherRenderState extends LivingEntityRenderState {
   public float[] xHeadRots = new float[2];
   public float[] yHeadRots = new float[2];
   public float invulnerableTicks;
   public boolean isPowered;

   public WitherRenderState() {
      super();
   }
}
