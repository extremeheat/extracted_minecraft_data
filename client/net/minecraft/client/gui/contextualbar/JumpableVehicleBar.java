package net.minecraft.client.gui.contextualbar;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideableJumping;

public class JumpableVehicleBar implements ContextualBar {
   private static final Identifier JUMP_BAR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_background");
   private static final Identifier JUMP_BAR_COOLDOWN_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_cooldown");
   private static final Identifier JUMP_BAR_PROGRESS_SPRITE = Identifier.withDefaultNamespace("hud/jump_bar_progress");
   private final Minecraft minecraft;
   private final PlayerRideableJumping playerJumpableVehicle;

   public JumpableVehicleBar(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
      this.playerJumpableVehicle = (PlayerRideableJumping)Objects.requireNonNull(((LocalPlayer)Objects.requireNonNull(minecraft.player)).jumpableVehicle());
   }

   public void extractBackground(final GuiGraphicsExtractor graphics) {
      int left = this.left(this.minecraft.getWindow());
      int top = this.top(this.minecraft.getWindow());
      graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)JUMP_BAR_BACKGROUND_SPRITE, left, top, 182, 5);
      if (this.playerJumpableVehicle.getJumpCooldown() > 0) {
         graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)JUMP_BAR_COOLDOWN_SPRITE, left, top, 182, 5);
      } else {
         int progress = Mth.lerpDiscrete(this.minecraft.player.getJumpRidingScale(), 0, 182);
         if (progress > 0) {
            graphics.blitSprite(GuiPipeline.TEXTURED, JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, left, top, progress, 5);
         }

      }
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
   }
}
