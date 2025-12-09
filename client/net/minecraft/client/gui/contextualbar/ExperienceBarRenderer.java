package net.minecraft.client.gui.contextualbar;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ExperienceBarRenderer implements ContextualBarRenderer {
   private static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("hud/experience_bar_background");
   private static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Identifier.withDefaultNamespace("hud/experience_bar_progress");
   private final Minecraft minecraft;

   public ExperienceBarRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void renderBackground(GuiGraphics var1, DeltaTracker var2) {
      LocalPlayer var3 = this.minecraft.player;
      int var4 = this.left(this.minecraft.getWindow());
      int var5 = this.top(this.minecraft.getWindow());
      int var6 = var3.getXpNeededForNextLevel();
      if (var6 > 0) {
         int var7 = (int)(var3.experienceProgress * 183.0F);
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)EXPERIENCE_BAR_BACKGROUND_SPRITE, var4, var5, 182, 5);
         if (var7 > 0) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, var4, var5, var7, 5);
         }
      }

   }

   public void render(GuiGraphics var1, DeltaTracker var2) {
   }
}
