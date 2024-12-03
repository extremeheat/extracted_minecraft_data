package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.Panda;

public class PandaRenderState extends HoldingEntityRenderState {
   public Panda.Gene variant;
   public boolean isUnhappy;
   public boolean isSneezing;
   public int sneezeTime;
   public boolean isEating;
   public boolean isScared;
   public boolean isSitting;
   public float sitAmount;
   public float lieOnBackAmount;
   public float rollAmount;
   public float rollTime;

   public PandaRenderState() {
      super();
      this.variant = Panda.Gene.NORMAL;
   }
}
