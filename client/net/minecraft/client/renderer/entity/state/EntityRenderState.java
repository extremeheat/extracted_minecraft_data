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
   public LeashState leashState;

   public EntityRenderState() {
      super();
   }

   public static class LeashState {
      public Vec3 offset;
      public Vec3 start;
      public Vec3 end;
      public int startBlockLight;
      public int endBlockLight;
      public int startSkyLight;
      public int endSkyLight;

      public LeashState() {
         super();
         this.offset = Vec3.ZERO;
         this.start = Vec3.ZERO;
         this.end = Vec3.ZERO;
         this.startBlockLight = 0;
         this.endBlockLight = 0;
         this.startSkyLight = 15;
         this.endSkyLight = 15;
      }
   }
}
