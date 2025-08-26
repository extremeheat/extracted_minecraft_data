package net.minecraft.client.gui.spectator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.util.ARGB;

public class PlayerMenuItem implements SpectatorMenuItem {
   private final PlayerInfo playerInfo;
   private final Component name;

   public PlayerMenuItem(PlayerInfo var1) {
      super();
      this.playerInfo = var1;
      this.name = Component.literal(var1.getProfile().name());
   }

   public void selectItem(SpectatorMenu var1) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.playerInfo.getProfile().id()));
   }

   public Component getName() {
      return this.name;
   }

   public void renderIcon(GuiGraphics var1, float var2, float var3) {
      PlayerFaceRenderer.draw(var1, this.playerInfo.getSkin(), 2, 2, 12, ARGB.white(var3));
   }

   public boolean isEnabled() {
      return true;
   }
}
