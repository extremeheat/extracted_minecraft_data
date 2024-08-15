package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class EntityRenderState {
   public double x;
   public double y;
   public double z;
   public float ageInTicks;
   public float boundingBoxWidth;
   public float boundingBoxHeight;
   public float eyeHeight;
   public double distanceToCameraSq;
   public boolean isInvisible;
   public boolean isDiscrete;
   public boolean displayFireAnimation;
   @Nullable
   public Vec3 passengerOffset;
   @Nullable
   public Component nameTag;
   @Nullable
   public Vec3 nameTagAttachment;
   @Nullable
   public EntityRenderState.LeashState leashState;

   public EntityRenderState() {
      super();
   }

   public static class LeashState {
      public Vec3 offset = Vec3.ZERO;
      public Vec3 start = Vec3.ZERO;
      public Vec3 end = Vec3.ZERO;
      public int startBlockLight = 0;
      public int endBlockLight = 0;
      public int startSkyLight = 15;
      public int endSkyLight = 15;

      public LeashState() {
         super();
      }
   }
}
