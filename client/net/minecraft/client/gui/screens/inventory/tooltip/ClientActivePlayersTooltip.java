package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ClientActivePlayersTooltip implements ClientTooltipComponent {
   private static final int SKIN_SIZE = 10;
   private static final int PADDING = 2;
   private final List<PlayerSkinRenderCache.RenderInfo> activePlayers;

   public ClientActivePlayersTooltip(ActivePlayersTooltip var1) {
      super();
      this.activePlayers = var1.profiles();
   }

   public int getHeight(Font var1) {
      return this.activePlayers.size() * 12 + 2;
   }

   private static String getName(PlayerSkinRenderCache.RenderInfo var0) {
      return var0.gameProfile().getName();
   }

   public int getWidth(Font var1) {
      int var2 = 0;

      for(PlayerSkinRenderCache.RenderInfo var4 : this.activePlayers) {
         int var5 = var1.width(getName(var4));
         if (var5 > var2) {
            var2 = var5;
         }
      }

      return var2 + 10 + 6;
   }

   public void renderImage(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
      for(int var7 = 0; var7 < this.activePlayers.size(); ++var7) {
         PlayerSkinRenderCache.RenderInfo var8 = (PlayerSkinRenderCache.RenderInfo)this.activePlayers.get(var7);
         int var9 = var3 + 2 + var7 * 12;
         PlayerFaceRenderer.draw(var6, var8.playerSkin(), var2 + 2, var9, 10);
         var6.drawString(var1, (String)getName(var8), var2 + 10 + 4, var9 + 2, -1);
      }

   }

   public static record ActivePlayersTooltip(List<PlayerSkinRenderCache.RenderInfo> profiles) implements TooltipComponent {
      public ActivePlayersTooltip(List<PlayerSkinRenderCache.RenderInfo> var1) {
         super();
         this.profiles = var1;
      }
   }
}
