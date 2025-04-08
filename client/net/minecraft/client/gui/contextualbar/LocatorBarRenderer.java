package net.minecraft.client.gui.contextualbar;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.waypoints.Waypoint;

public class LocatorBarRenderer implements ContextualBarRenderer {
   private static final ResourceLocation LOCATOR_BAR_BACKGROUND = ResourceLocation.withDefaultNamespace("hud/locator_bar_background");
   private static final ResourceLocation LOCATOR_BAR_DOT = ResourceLocation.withDefaultNamespace("hud/locator_bar_player");
   private static final Int2ObjectMap<ResourceLocation> LOCATOR_BAR_ARROWS = new Int2ObjectArrayMap(Map.of(1, ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_up"), -1, ResourceLocation.withDefaultNamespace("hud/locator_bar_arrow_down")));
   private static final int DOT_SIZE = 7;
   private static final int BAR_PADDING = 1;
   private static final int VISIBLE_DEGREE_RANGE = 60;
   private static final int ARROW_WIDTH = 7;
   private static final int ARROW_HEIGHT = 5;
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
      this.minecraft.player.connection.getWaypointManager().forEachWaypoint(this.minecraft.cameraEntity, (var4x) -> {
         if (!(Boolean)var4x.id().left().map((var1x) -> var1x.equals(this.minecraft.cameraEntity.getUUID())).orElse(false)) {
            double var5 = var4x.yawAngleToEntity(this.minecraft.cameraEntity);
            if (!(var5 < -60.0) && !(var5 > 60.0)) {
               int var7 = (var1.guiWidth() - 7) / 2;
               Waypoint.Icon var8 = var4x.icon();
               float var10 = Mth.sqrt((float)var4x.distanceSquared(this.minecraft.cameraEntity));
               float var9;
               if (var10 < (float)var8.alphaFade.nearDist()) {
                  var9 = var8.alphaFade.nearAlpha();
               } else if (var10 < (float)var8.alphaFade.farDist()) {
                  var9 = var8.alphaFade.lerpAlpha(var10);
               } else {
                  var9 = var8.alphaFade.farAlpha();
               }

               int var11 = (Integer)var8.color.orElseGet(() -> (Integer)var4x.id().map((var0) -> ARGB.setBrightness(var0.hashCode(), 0.9F), (var0) -> ARGB.setBrightness(var0.hashCode(), 0.9F)));
               int var12 = Mth.floor(var5 * 174.0 / 2.0 / 60.0);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)LOCATOR_BAR_DOT, var7 + var12, var3 - 1, 7, 7, ARGB.color(ARGB.as8BitChannel(var9), var11));
               int var13 = var4x.pitchDirectionToEntity(this.minecraft.cameraEntity, this.minecraft.gameRenderer);
               if (var13 != 0) {
                  int var14 = var13 < 0 ? 7 : -5;
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)((ResourceLocation)LOCATOR_BAR_ARROWS.get(var13)), 14, 5, var4, 0, var7 + var12, var3 + var14 - 1, 7, 5, ARGB.color(ARGB.as8BitChannel(var9), -1));
               }

            }
         }
      });
   }
}
