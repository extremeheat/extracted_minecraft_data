package net.minecraft.client.gui.contextualbar;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.Waypoint;

public class LocatorBarRenderer implements ContextualBarRenderer {
   private static final ResourceLocation LOCATOR_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("hud/locator_bar_background");
   private static final Int2ObjectMap<ResourceLocation> LOCATOR_BAR_ARROWS = new Int2ObjectArrayMap(Map.of(1, ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up"), -1, ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down")));
   private static final int DOT_SIZE = 9;
   private static final int VISIBLE_DEGREE_RANGE = 60;
   private static final int ARROW_WIDTH = 7;
   private static final int ARROW_HEIGHT = 5;
   private static final int ARROW_LEFT = 1;
   private static final int ARROW_ANIMATION_FRAMES = 2;
   private static final int ARROW_ANIMATION_TICKS_SPEED = 14;
   private static final int ARROW_ANIMATION_FRAME_TICK = 9;
   private final Minecraft minecraft;
   private float arrowAnimationTicks;

   public LocatorBarRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void renderBackground(GuiGraphics var1, DeltaTracker var2) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)LOCATOR_BAR_BACKGROUND, this.left(this.minecraft.getWindow()), this.top(this.minecraft.getWindow()), 182, 5);
   }

   public void render(GuiGraphics var1, DeltaTracker var2) {
      this.arrowAnimationTicks += var2.getGameTimeDeltaTicks();
      int var3 = this.top(this.minecraft.getWindow());
      int var4 = this.arrowAnimationTicks % 14.0F > 9.0F ? 7 : 0;
      Level var5 = this.minecraft.cameraEntity.level();
      this.minecraft.player.connection.getWaypointManager().forEachWaypoint(this.minecraft.cameraEntity, (var5x) -> {
         if (!(Boolean)var5x.id().left().map((var1x) -> var1x.equals(this.minecraft.cameraEntity.getUUID())).orElse(false)) {
            double var6 = var5x.yawAngleToCamera(var5, this.minecraft.gameRenderer.getMainCamera());
            if (!(var6 <= -61.0) && !(var6 > 60.0)) {
               int var8 = Mth.ceil((float)(var1.guiWidth() - 9) / 2.0F);
               Waypoint.Icon var9 = var5x.icon();
               WaypointStyle var10 = this.minecraft.getWaypointStyles().get(var9.style);
               float var11 = Mth.sqrt((float)var5x.distanceSquared(this.minecraft.cameraEntity));
               ResourceLocation var12 = var10.sprite(var11);
               int var13 = (Integer)var9.color.orElseGet(() -> (Integer)var5x.id().map((var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F), (var0) -> ARGB.setBrightness(ARGB.color(255, var0.hashCode()), 0.9F)));
               int var14 = (int)(var6 * 173.0 / 2.0 / 60.0);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var12, var8 + var14, var3 - 2, 9, 9, var13);
               int var15 = var5x.pitchDirectionToCamera(var5, this.minecraft.gameRenderer);
               if (var15 != 0) {
                  int var16 = var15 < 0 ? 9 : -5;
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)LOCATOR_BAR_ARROWS.get(var15), 14, 5, var4, 0, var8 + var14 + 1, var3 + var16 - 1, 7, 5);
               }

            }
         }
      });
   }
}
