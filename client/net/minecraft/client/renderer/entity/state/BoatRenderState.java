package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderState extends EntityRenderState {
   public float yRot;
   public int hurtDir;
   public float hurtTime;
   public float damageTime;
   public float bubbleAngle;
   public boolean isUnderWater;
   public Boat.Type variant = Boat.Type.OAK;
   public float rowingTimeLeft;
   public float rowingTimeRight;

   public BoatRenderState() {
      super();
   }
}
