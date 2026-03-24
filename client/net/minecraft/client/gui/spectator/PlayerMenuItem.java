package net.minecraft.client.gui.spectator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.util.ARGB;

public class PlayerMenuItem implements SpectatorMenuItem {
   private final PlayerInfo playerInfo;
   private final Component name;

   public PlayerMenuItem(final PlayerInfo playerInfo) {
      super();
      this.playerInfo = playerInfo;
      this.name = Component.literal(playerInfo.getProfile().name());
   }

   public void selectItem(final SpectatorMenu menu) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.playerInfo.getProfile().id()));
   }

   public Component getName() {
      return this.name;
   }

   public void extractIcon(final GuiGraphicsExtractor graphics, final float brightness, final float alpha) {
      PlayerFaceExtractor.extractRenderState(graphics, this.playerInfo.getSkin(), 2, 2, 12, ARGB.white(alpha));
   }

   public boolean isEnabled() {
      return true;
   }
}
