package net.minecraft.client.renderer.entity.state;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class EntityRenderState {
   public EntityType<?> entityType;
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
   public List<LeashState> leashStates;
   @Nullable
   public HitboxesRenderState hitboxesRenderState;
   @Nullable
   public ServerHitboxesRenderState serverHitboxesRenderState;

   public EntityRenderState() {
      super();
   }

   public void fillCrashReportCategory(CrashReportCategory var1) {
      var1.setDetail("EntityRenderState", this.getClass().getCanonicalName());
      var1.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
   }

   public static class LeashState {
      public Vec3 offset;
      public Vec3 start;
      public Vec3 end;
      public int startBlockLight;
      public int endBlockLight;
      public int startSkyLight;
      public int endSkyLight;
      public boolean slack;

      public LeashState() {
         super();
         this.offset = Vec3.ZERO;
         this.start = Vec3.ZERO;
         this.end = Vec3.ZERO;
         this.startBlockLight = 0;
         this.endBlockLight = 0;
         this.startSkyLight = 15;
         this.endSkyLight = 15;
         this.slack = true;
      }
   }
}
