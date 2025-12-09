package net.minecraft.client.gui.contextualbar;

import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideableJumping;

public class JumpableVehicleBarRenderer implements ContextualBarRenderer {
   private static final Identifier JUMP_BAR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_background");
   private static final Identifier JUMP_BAR_COOLDOWN_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_cooldown");
   private static final Identifier JUMP_BAR_PROGRESS_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_progress");
   private final Minecraft minecraft;
   private final PlayerRideableJumping playerJumpableVehicle;

   public JumpableVehicleBarRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.playerJumpableVehicle = (PlayerRideableJumping)Objects.requireNonNull(((LocalPlayer)Objects.requireNonNull(var1.player)).jumpableVehicle());
   }

   public void renderBackground(GuiGraphics var1, DeltaTracker var2) {
      int var3 = this.left(this.minecraft.getWindow());
      int var4 = this.top(this.minecraft.getWindow());
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)JUMP_BAR_BACKGROUND_SPRITE, var3, var4, 182, 5);
      if (this.playerJumpableVehicle.getJumpCooldown() > 0) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)JUMP_BAR_COOLDOWN_SPRITE, var3, var4, 182, 5);
      } else {
         int var5 = Mth.lerpDiscrete(this.minecraft.player.getJumpRidingScale(), 0, 182);
         if (var5 > 0) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, var3, var4, var5, 5);
         }

      }
   }

   public void render(GuiGraphics var1, DeltaTracker var2) {
   }
}
