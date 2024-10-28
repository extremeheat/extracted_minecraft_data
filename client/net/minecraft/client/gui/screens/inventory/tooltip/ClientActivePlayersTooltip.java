package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ClientActivePlayersTooltip implements ClientTooltipComponent {
   private static final int SKIN_SIZE = 10;
   private static final int PADDING = 2;
   private final List<ProfileResult> activePlayers;

   public ClientActivePlayersTooltip(ActivePlayersTooltip var1) {
      super();
      this.activePlayers = var1.profiles();
   }

   public int getHeight() {
      return this.activePlayers.size() * 12 + 2;
   }

   public int getWidth(Font var1) {
      int var2 = 0;
      Iterator var3 = this.activePlayers.iterator();

      while(var3.hasNext()) {
         ProfileResult var4 = (ProfileResult)var3.next();
         int var5 = var1.width(var4.profile().getName());
         if (var5 > var2) {
            var2 = var5;
         }
      }

      return var2 + 10 + 6;
   }

   public void renderImage(Font var1, int var2, int var3, GuiGraphics var4) {
      for(int var5 = 0; var5 < this.activePlayers.size(); ++var5) {
         ProfileResult var6 = (ProfileResult)this.activePlayers.get(var5);
         int var7 = var3 + 2 + var5 * 12;
         PlayerFaceRenderer.draw(var4, (PlayerSkin)Minecraft.getInstance().getSkinManager().getInsecureSkin(var6.profile()), var2 + 2, var7, 10);
         var4.drawString(var1, (String)var6.profile().getName(), var2 + 10 + 4, var7 + 2, -1);
      }

   }

   public static record ActivePlayersTooltip(List<ProfileResult> profiles) implements TooltipComponent {
      public ActivePlayersTooltip(List<ProfileResult> var1) {
         super();
         this.profiles = var1;
      }

      public List<ProfileResult> profiles() {
         return this.profiles;
      }
   }
}
