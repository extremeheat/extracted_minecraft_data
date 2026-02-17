package net.minecraft.client.renderer.entity.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EntityRenderState {
   public static final int NO_OUTLINE = 0;
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
   public int lightCoords = 15728880;
   public int outlineColor = 0;
   public @Nullable Vec3 passengerOffset;
   public @Nullable Component nameTag;
   public @Nullable Component scoreText;
   public @Nullable Vec3 nameTagAttachment;
   public @Nullable List<LeashState> leashStates;
   public float shadowRadius;
   public final List<ShadowPiece> shadowPieces = new ArrayList();

   public EntityRenderState() {
      super();
   }

   public boolean appearsGlowing() {
      return this.outlineColor != 0;
   }

   public void fillCrashReportCategory(final CrashReportCategory category) {
      category.setDetail("EntityRenderState", this.getClass().getCanonicalName());
      category.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
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

   public static record ShadowPiece(float relativeX, float relativeY, float relativeZ, VoxelShape shapeBelow, float alpha) {
      public ShadowPiece {
         super();
      }
   }
}
