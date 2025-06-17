package net.minecraft.client.gui.contextualbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.Waypoint;

public class LocatorBarRenderer implements ContextualBarRenderer {
   private static final ResourceLocation LOCATOR_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("hud/locator_bar_background");
   private static final ResourceLocation LOCATOR_BAR_ARROW_UP = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up");
   private static final ResourceLocation LOCATOR_BAR_ARROW_DOWN = ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down");
   private static final int DOT_SIZE = 9;
   private static final int VISIBLE_DEGREE_RANGE = 60;
   private static final int ARROW_WIDTH = 7;
   private static final int ARROW_HEIGHT = 5;
   private static final int ARROW_LEFT = 1;
   private static final int ARROW_PADDING = 1;
   private final Minecraft minecraft;

   public LocatorBarRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void renderBackground(GuiGraphics var1, DeltaTracker var2) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)LOCATOR_BAR_BACKGROUND, this.left(this.minecraft.getWindow()), this.top(this.minecraft.getWindow()), 182, 5);
   }

   public void render(GuiGraphics var1, DeltaTracker var2) {
      int var3 = this.top(this.minecraft.getWindow());
      Level var4 = this.minecraft.cameraEntity.level();
      this.minecraft.player.connection.getWaypointManager().forEachWaypoint(this.minecraft.cameraEntity, (var4x) -> {
         if (!(Boolean)var4x.id().left().map((var1x) -> var1x.equals(this.minecraft.cameraEntity.getUUID())).orElse(false)) {
            double var5 = var4x.yawAngleToCamera(var4, this.minecraft.gameRenderer.getMainCamera());
            if (!(var5 <= -61.0) && !(var5 > 60.0)) {
               int var7 = Mth.ceil((float)(var1.guiWidth() - 9) / 2.0F);
               Waypoint.Icon var8 = var4x.icon();
               WaypointStyle var9 = this.minecraft.getWaypointStyles().get(var8.style);
               float var10 = Mth.sqrt((float)var4x.distanceSquared(this.minecraft.cameraEntity));
               ResourceLocation var11 = var9.sprite(var10);
               int var12 = (Integer)var8.color.orElseGet(() -> (Integer)var4x.id().map((var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F), (var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F)));
               int var13 = (int)(var5 * 173.0 / 2.0 / 60.0);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, var7 + var13, var3 - 2, 9, 9, var12);
               TrackedWaypoint.PitchDirection var14 = var4x.pitchDirectionToCamera(var4, this.minecraft.gameRenderer);
               if (var14 != TrackedWaypoint.PitchDirection.NONE) {
                  byte var15;
                  ResourceLocation var16;
                  if (var14 == TrackedWaypoint.PitchDirection.DOWN) {
                     var15 = 6;
                     var16 = LOCATOR_BAR_ARROW_DOWN;
                  } else {
                     var15 = -6;
                     var16 = LOCATOR_BAR_ARROW_UP;
                  }

                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var16, var7 + var13 + 1, var3 + var15, 7, 5);
               }

            }
         }
      });
   }
}
