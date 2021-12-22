package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
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
   private final List<GameEventListenerRenderer.TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
   private final List<GameEventListenerRenderer.TrackedListener> trackedListeners = Lists.newArrayList();

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
         BlockPos var10 = new BlockPos(var3, 0.0D, var7);
         this.trackedGameEvents.removeIf(GameEventListenerRenderer.TrackedGameEvent::isExpired);
         this.trackedListeners.removeIf((var2x) -> {
            return var2x.isExpired(var9, var10);
         });
         RenderSystem.disableTexture();
         RenderSystem.enableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         VertexConsumer var11 = var2.getBuffer(RenderType.lines());
         Iterator var12 = this.trackedListeners.iterator();

         while(var12.hasNext()) {
            GameEventListenerRenderer.TrackedListener var13 = (GameEventListenerRenderer.TrackedListener)var12.next();
            var13.getPosition(var9).ifPresent((var9x) -> {
               int var10 = var9x.getX() - var13.getListenerRadius();
               int var11x = var9x.getY() - var13.getListenerRadius();
               int var12 = var9x.getZ() - var13.getListenerRadius();
               int var13x = var9x.getX() + var13.getListenerRadius();
               int var14 = var9x.getY() + var13.getListenerRadius();
               int var15 = var9x.getZ() + var13.getListenerRadius();
               Vector3f var16 = new Vector3f(1.0F, 1.0F, 0.0F);
               LevelRenderer.renderVoxelShape(var1, var11, Shapes.create(new AABB((double)var10, (double)var11x, (double)var12, (double)var13x, (double)var14, (double)var15)), -var3, -var5, -var7, var16.method_82(), var16.method_83(), var16.method_84(), 0.35F);
            });
         }

         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         Tesselator var31 = Tesselator.getInstance();
         BufferBuilder var32 = var31.getBuilder();
         var32.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
         Iterator var14 = this.trackedListeners.iterator();

         GameEventListenerRenderer.TrackedListener var15;
         while(var14.hasNext()) {
            var15 = (GameEventListenerRenderer.TrackedListener)var14.next();
            var15.getPosition(var9).ifPresent((var7x) -> {
               Vector3f var8 = new Vector3f(1.0F, 1.0F, 0.0F);
               LevelRenderer.addChainedFilledBoxVertices(var32, (double)((float)var7x.getX() - 0.25F) - var3, (double)var7x.getY() - var5, (double)((float)var7x.getZ() - 0.25F) - var7, (double)((float)var7x.getX() + 0.25F) - var3, (double)var7x.getY() - var5 + 1.0D, (double)((float)var7x.getZ() + 0.25F) - var7, var8.method_82(), var8.method_83(), var8.method_84(), 0.35F);
            });
         }

         var31.end();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.depthMask(false);
         var14 = this.trackedListeners.iterator();

         while(var14.hasNext()) {
            var15 = (GameEventListenerRenderer.TrackedListener)var14.next();
            var15.getPosition(var9).ifPresent((var0) -> {
               DebugRenderer.renderFloatingText("Listener Origin", (double)var0.getX(), (double)((float)var0.getY() + 1.8F), (double)var0.getZ(), -1, 0.025F);
               DebugRenderer.renderFloatingText((new BlockPos(var0)).toString(), (double)var0.getX(), (double)((float)var0.getY() + 1.5F), (double)var0.getZ(), -6959665, 0.025F);
            });
         }

         var14 = this.trackedGameEvents.iterator();

         while(var14.hasNext()) {
            GameEventListenerRenderer.TrackedGameEvent var33 = (GameEventListenerRenderer.TrackedGameEvent)var14.next();
            Vec3 var16 = var33.position;
            double var17 = 0.20000000298023224D;
            double var19 = var16.field_414 - 0.20000000298023224D;
            double var21 = var16.field_415 - 0.20000000298023224D;
            double var23 = var16.field_416 - 0.20000000298023224D;
            double var25 = var16.field_414 + 0.20000000298023224D;
            double var27 = var16.field_415 + 0.20000000298023224D + 0.5D;
            double var29 = var16.field_416 + 0.20000000298023224D;
            renderTransparentFilledBox(new AABB(var19, var21, var23, var25, var27, var29), 1.0F, 1.0F, 1.0F, 0.2F);
            DebugRenderer.renderFloatingText(var33.gameEvent.getName(), var16.field_414, var16.field_415 + 0.8500000238418579D, var16.field_416, -7564911, 0.0075F);
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }
   }

   private static void renderTransparentFilledBox(AABB var0, float var1, float var2, float var3, float var4) {
      Camera var5 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var5.isInitialized()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3 var6 = var5.getPosition().reverse();
         DebugRenderer.renderFilledBox(var0.move(var6), var1, var2, var3, var4);
      }
   }

   public void trackGameEvent(GameEvent var1, BlockPos var2) {
      this.trackedGameEvents.add(new GameEventListenerRenderer.TrackedGameEvent(Util.getMillis(), var1, Vec3.atBottomCenterOf(var2)));
   }

   public void trackListener(PositionSource var1, int var2) {
      this.trackedListeners.add(new GameEventListenerRenderer.TrackedListener(var1, var2));
   }

   private static class TrackedListener implements GameEventListener {
      public final PositionSource listenerSource;
      public final int listenerRange;

      public TrackedListener(PositionSource var1, int var2) {
         super();
         this.listenerSource = var1;
         this.listenerRange = var2;
      }

      public boolean isExpired(Level var1, BlockPos var2) {
         Optional var3 = this.listenerSource.getPosition(var1);
         return !var3.isPresent() || ((BlockPos)var3.get()).distSqr(var2) <= 1024.0D;
      }

      public Optional<BlockPos> getPosition(Level var1) {
         return this.listenerSource.getPosition(var1);
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRange;
      }

      public boolean handleGameEvent(Level var1, GameEvent var2, @Nullable Entity var3, BlockPos var4) {
         return false;
      }
   }

   private static class TrackedGameEvent {
      public final long timeStamp;
      public final GameEvent gameEvent;
      public final Vec3 position;

      public TrackedGameEvent(long var1, GameEvent var3, Vec3 var4) {
         super();
         this.timeStamp = var1;
         this.gameEvent = var3;
         this.position = var4;
      }

      public boolean isExpired() {
         return Util.getMillis() - this.timeStamp > 3000L;
      }
   }
}
