package net.minecraft.client.renderer.debug;

import net.minecraft.SharedConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityHitboxDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public EntityHitboxDebugRenderer(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void emitGizmos(final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks) {
      if (this.minecraft.level != null) {
         long chunkFadeDuration = Util.toMillis((Double)this.minecraft.options.chunkSectionFadeInTime().get());

         for(Entity entity : this.minecraft.level.entitiesForRendering()) {
            if (!entity.isInvisible()) {
               float entityPartialTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(!this.minecraft.level.tickRateManager().isEntityFrozen(entity));
               if (this.minecraft.levelExtractor.isEntityVisible(entity, frustum, camX, camY, camZ, entityPartialTicks, chunkFadeDuration) && (entity != this.minecraft.getCameraEntity() || this.minecraft.options.getCameraType() != CameraType.FIRST_PERSON)) {
                  this.showHitboxes(entity, entityPartialTicks, false);
                  if (SharedConstants.DEBUG_SHOW_LOCAL_SERVER_ENTITY_HIT_BOXES) {
                     Entity serverEntity = this.getServerEntity(entity);
                     if (serverEntity != null) {
                        this.showHitboxes(entity, entityPartialTicks, true);
                     } else {
                        Gizmos.billboardText("Missing Server Entity", entity.getPosition(entityPartialTicks).add(0.0, entity.getBoundingBox().getYsize() + 1.5, 0.0), TextGizmo.Style.forColorAndCentered(-65536));
                     }
                  }
               }
            }
         }

      }
   }

   private @Nullable Entity getServerEntity(final Entity entity) {
      IntegratedServer server = this.minecraft.getSingleplayerServer();
      if (server != null) {
         ServerLevel level = server.getLevel(entity.level().dimension());
         if (level != null) {
            return level.getEntity(entity.getId());
         }
      }

      return null;
   }

   private void showHitboxes(final Entity entity, final float partialTicks, final boolean isServerEntity) {
      Vec3 latestPosition = entity.position();
      Vec3 currentPosition = entity.getPosition(partialTicks);
      Vec3 offset = currentPosition.subtract(latestPosition);
      int mainColor = isServerEntity ? -16711936 : -1;
      Gizmos.cuboid(entity.getBoundingBox().move(offset), GizmoStyle.stroke(mainColor));
      Gizmos.point(currentPosition, mainColor, 2.0F);
      Entity vehicle = entity.getVehicle();
      if (vehicle != null) {
         float width = Math.min(vehicle.getBbWidth(), entity.getBbWidth()) / 2.0F;
         float height = 0.0625F;
         Vec3 position = vehicle.getPassengerRidingPosition(entity).add(offset);
         Gizmos.cuboid(new AABB(position.x - (double)width, position.y, position.z - (double)width, position.x + (double)width, position.y + 0.0625, position.z + (double)width), GizmoStyle.stroke(-256));
      }

      if (entity instanceof LivingEntity) {
         AABB bb = entity.getBoundingBox().move(offset);
         float padding = 0.01F;
         Gizmos.cuboid(new AABB(bb.minX, bb.minY + (double)entity.getEyeHeight() - 0.009999999776482582, bb.minZ, bb.maxX, bb.minY + (double)entity.getEyeHeight() + 0.009999999776482582, bb.maxZ), GizmoStyle.stroke(-65536));
      }

      if (entity instanceof EnderDragon dragon) {
         for(EnderDragonPart subEntity : dragon.getSubEntities()) {
            Vec3 latestSubPosition = subEntity.position();
            Vec3 currentSubPosition = subEntity.getPosition(partialTicks);
            Vec3 subOffset = currentSubPosition.subtract(latestSubPosition);
            Gizmos.cuboid(subEntity.getBoundingBox().move(subOffset), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F)));
         }
      }

      Vec3 eyePosition = currentPosition.add(0.0, (double)entity.getEyeHeight(), 0.0);
      Vec3 viewVector = entity.getViewVector(partialTicks);
      Gizmos.arrow(eyePosition, eyePosition.add(viewVector.scale(2.0)), -16776961);
      if (isServerEntity) {
         Vec3 deltaMovement = entity.getDeltaMovement();
         Gizmos.arrow(currentPosition, currentPosition.add(deltaMovement), -256);
      }

   }
}
