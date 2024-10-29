package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class GameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int LISTENER_RENDER_DIST = 32;
   private static final float BOX_HEIGHT = 1.0F;
   private final List<TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
   private final List<TrackedListener> trackedListeners = Lists.newArrayList();

   public GameEventListenerRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      ClientLevel var9 = this.minecraft.level;
      if (var9 == null) {
         this.trackedGameEvents.clear();
         this.trackedListeners.clear();
      } else {
         Vec3 var10 = new Vec3(var3, 0.0, var7);
         this.trackedGameEvents.removeIf(TrackedGameEvent::isExpired);
         this.trackedListeners.removeIf((var2x) -> {
            return var2x.isExpired(var9, var10);
         });
         VertexConsumer var11 = var2.getBuffer(RenderType.lines());
         Iterator var12 = this.trackedListeners.iterator();

         while(var12.hasNext()) {
            TrackedListener var13 = (TrackedListener)var12.next();
            var13.getPosition(var9).ifPresent((var9x) -> {
               double var10 = var9x.x() - (double)var13.getListenerRadius();
               double var12 = var9x.y() - (double)var13.getListenerRadius();
               double var14 = var9x.z() - (double)var13.getListenerRadius();
               double var16 = var9x.x() + (double)var13.getListenerRadius();
               double var18 = var9x.y() + (double)var13.getListenerRadius();
               double var20 = var9x.z() + (double)var13.getListenerRadius();
               DebugRenderer.renderVoxelShape(var1, var11, Shapes.create(new AABB(var10, var12, var14, var16, var18, var20)), -var3, -var5, -var7, 1.0F, 1.0F, 0.0F, 0.35F, true);
            });
         }

         VertexConsumer var30 = var2.getBuffer(RenderType.debugFilledBox());
         Iterator var31 = this.trackedListeners.iterator();

         TrackedListener var14;
         while(var31.hasNext()) {
            var14 = (TrackedListener)var31.next();
            var14.getPosition(var9).ifPresent((var8) -> {
               ShapeRenderer.addChainedFilledBoxVertices(var1, var30, var8.x() - 0.25 - var3, var8.y() - var5, var8.z() - 0.25 - var7, var8.x() + 0.25 - var3, var8.y() - var5 + 1.0, var8.z() + 0.25 - var7, 1.0F, 1.0F, 0.0F, 0.35F);
            });
         }

         var31 = this.trackedListeners.iterator();

         while(var31.hasNext()) {
            var14 = (TrackedListener)var31.next();
            var14.getPosition(var9).ifPresent((var2x) -> {
               DebugRenderer.renderFloatingText(var1, var2, "Listener Origin", var2x.x(), var2x.y() + 1.7999999523162842, var2x.z(), -1, 0.025F);
               DebugRenderer.renderFloatingText(var1, var2, BlockPos.containing(var2x).toString(), var2x.x(), var2x.y() + 1.5, var2x.z(), -6959665, 0.025F);
            });
         }

         var31 = this.trackedGameEvents.iterator();

         while(var31.hasNext()) {
            TrackedGameEvent var32 = (TrackedGameEvent)var31.next();
            Vec3 var15 = var32.position;
            double var16 = 0.20000000298023224;
            double var18 = var15.x - 0.20000000298023224;
            double var20 = var15.y - 0.20000000298023224;
            double var22 = var15.z - 0.20000000298023224;
            double var24 = var15.x + 0.20000000298023224;
            double var26 = var15.y + 0.20000000298023224 + 0.5;
            double var28 = var15.z + 0.20000000298023224;
            renderFilledBox(var1, var2, new AABB(var18, var20, var22, var24, var26, var28), 1.0F, 1.0F, 1.0F, 0.2F);
            DebugRenderer.renderFloatingText(var1, var2, var32.gameEvent.location().toString(), var15.x, var15.y + 0.8500000238418579, var15.z, -7564911, 0.0075F);
         }

      }
   }

   private static void renderFilledBox(PoseStack var0, MultiBufferSource var1, AABB var2, float var3, float var4, float var5, float var6) {
      Camera var7 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var7.isInitialized()) {
         Vec3 var8 = var7.getPosition().reverse();
         DebugRenderer.renderFilledBox(var0, var1, var2.move(var8), var3, var4, var5, var6);
      }
   }

   public void trackGameEvent(ResourceKey<GameEvent> var1, Vec3 var2) {
      this.trackedGameEvents.add(new TrackedGameEvent(Util.getMillis(), var1, var2));
   }

   public void trackListener(PositionSource var1, int var2) {
      this.trackedListeners.add(new TrackedListener(var1, var2));
   }

   private static class TrackedListener implements GameEventListener {
      public final PositionSource listenerSource;
      public final int listenerRange;

      public TrackedListener(PositionSource var1, int var2) {
         super();
         this.listenerSource = var1;
         this.listenerRange = var2;
      }

      public boolean isExpired(Level var1, Vec3 var2) {
         return this.listenerSource.getPosition(var1).filter((var1x) -> {
            return var1x.distanceToSqr(var2) <= 1024.0;
         }).isPresent();
      }

      public Optional<Vec3> getPosition(Level var1) {
         return this.listenerSource.getPosition(var1);
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRange;
      }

      public boolean handleGameEvent(ServerLevel var1, Holder<GameEvent> var2, GameEvent.Context var3, Vec3 var4) {
         return false;
      }
   }

   private static record TrackedGameEvent(long timeStamp, ResourceKey<GameEvent> gameEvent, Vec3 position) {
      final ResourceKey<GameEvent> gameEvent;
      final Vec3 position;

      TrackedGameEvent(long var1, ResourceKey<GameEvent> var3, Vec3 var4) {
         super();
         this.timeStamp = var1;
         this.gameEvent = var3;
         this.position = var4;
      }

      public boolean isExpired() {
         return Util.getMillis() - this.timeStamp > 3000L;
      }

      public long timeStamp() {
         return this.timeStamp;
      }

      public ResourceKey<GameEvent> gameEvent() {
         return this.gameEvent;
      }

      public Vec3 position() {
         return this.position;
      }
   }
}
