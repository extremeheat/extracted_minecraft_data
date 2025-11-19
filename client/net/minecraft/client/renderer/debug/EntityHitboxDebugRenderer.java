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
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityHitboxDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   final Minecraft minecraft;

   public EntityHitboxDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      if (this.minecraft.level != null) {
         for(Entity var11 : this.minecraft.level.entitiesForRendering()) {
            if (!var11.isInvisible() && var8.isVisible(var11.getBoundingBox()) && (var11 != this.minecraft.getCameraEntity() || this.minecraft.options.getCameraType() != CameraType.FIRST_PERSON)) {
               this.showHitboxes(var11, var9, false);
               if (SharedConstants.DEBUG_SHOW_LOCAL_SERVER_ENTITY_HIT_BOXES) {
                  Entity var12 = this.getServerEntity(var11);
                  if (var12 != null) {
                     this.showHitboxes(var11, var9, true);
                  } else {
                     Gizmos.billboardText("Missing Server Entity", var11.getPosition(var9).add(0.0, var11.getBoundingBox().getYsize() + 1.5, 0.0), TextGizmo.Style.forColorAndCentered(-65536));
                  }
               }
            }
         }

      }
   }

   private @Nullable Entity getServerEntity(Entity var1) {
      IntegratedServer var2 = this.minecraft.getSingleplayerServer();
      if (var2 != null) {
         ServerLevel var3 = var2.getLevel(var1.level().dimension());
         if (var3 != null) {
            return var3.getEntity(var1.getId());
         }
      }

      return null;
   }

   private void showHitboxes(Entity var1, float var2, boolean var3) {
      Vec3 var4 = var1.position();
      Vec3 var5 = var1.getPosition(var2);
      Vec3 var6 = var5.subtract(var4);
      int var7 = var3 ? -16711936 : -1;
      Gizmos.cuboid(var1.getBoundingBox().move(var6), GizmoStyle.stroke(var7));
      Gizmos.point(var5, var7, 2.0F);
      Entity var8 = var1.getVehicle();
      if (var8 != null) {
         float var9 = Math.min(var8.getBbWidth(), var1.getBbWidth()) / 2.0F;
         float var10 = 0.0625F;
         Vec3 var11 = var8.getPassengerRidingPosition(var1).add(var6);
         Gizmos.cuboid(new AABB(var11.x - (double)var9, var11.y, var11.z - (double)var9, var11.x + (double)var9, var11.y + 0.0625, var11.z + (double)var9), GizmoStyle.stroke(-256));
      }

      if (var1 instanceof LivingEntity) {
         AABB var17 = var1.getBoundingBox().move(var6);
         float var20 = 0.01F;
         Gizmos.cuboid(new AABB(var17.minX, var17.minY + (double)var1.getEyeHeight() - 0.009999999776482582, var17.minZ, var17.maxX, var17.minY + (double)var1.getEyeHeight() + 0.009999999776482582, var17.maxZ), GizmoStyle.stroke(-65536));
      }

      if (var1 instanceof EnderDragon var18) {
         for(EnderDragonPart var13 : var18.getSubEntities()) {
            Vec3 var14 = var13.position();
            Vec3 var15 = var13.getPosition(var2);
            Vec3 var16 = var15.subtract(var14);
            Gizmos.cuboid(var13.getBoundingBox().move(var16), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F)));
         }
      }

      Vec3 var19 = var5.add(0.0, (double)var1.getEyeHeight(), 0.0);
      Vec3 var22 = var1.getViewVector(var2);
      Gizmos.arrow(var19, var19.add(var22.scale(2.0)), -16776961);
      if (var3) {
         Vec3 var24 = var1.getDeltaMovement();
         Gizmos.arrow(var5, var5.add(var24), -256);
      }

   }
}
