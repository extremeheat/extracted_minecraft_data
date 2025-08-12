package net.minecraft.client.gui.contextualbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.PartialTickSupplier;
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
      Entity var4 = this.minecraft.getCameraEntity();
      if (var4 != null) {
         Level var5 = var4.level();
         TickRateManager var6 = var5.tickRateManager();
         PartialTickSupplier var7 = (var2x) -> var2.getGameTimeDeltaPartialTick(!var6.isEntityFrozen(var2x));
         this.minecraft.player.connection.getWaypointManager().forEachWaypoint(var4, (var6x) -> {
            if (!(Boolean)var6x.id().left().map((var1x) -> var1x.equals(var4.getUUID())).orElse(false)) {
               double var7x = var6x.yawAngleToCamera(var5, this.minecraft.gameRenderer.getMainCamera(), var7);
               if (!(var7x <= -60.0) && !(var7x > 60.0)) {
                  int var9 = Mth.ceil((float)(var1.guiWidth() - 9) / 2.0F);
                  Waypoint.Icon var10 = var6x.icon();
                  WaypointStyle var11 = this.minecraft.getWaypointStyles().get(var10.style);
                  float var12 = Mth.sqrt((float)var6x.distanceSquared(var4));
                  ResourceLocation var13 = var11.sprite(var12);
                  int var14 = (Integer)var10.color.orElseGet(() -> (Integer)var6x.id().map((var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F), (var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F)));
                  int var15 = Mth.floor(var7x * 173.0 / 2.0 / 60.0);
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var13, var9 + var15, var3 - 2, 9, 9, var14);
                  TrackedWaypoint.PitchDirection var16 = var6x.pitchDirectionToCamera(var5, this.minecraft.gameRenderer, var7);
                  if (var16 != TrackedWaypoint.PitchDirection.NONE) {
                     byte var17;
                     ResourceLocation var18;
                     if (var16 == TrackedWaypoint.PitchDirection.DOWN) {
                        var17 = 6;
                        var18 = LOCATOR_BAR_ARROW_DOWN;
                     } else {
                        var17 = -6;
                        var18 = LOCATOR_BAR_ARROW_UP;
                     }

                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var18, var9 + var15 + 1, var3 + var17, 7, 5);
                  }

               }
            }
         });
      }
   }
}
