package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;

public class PlayerMenuItem implements SpectatorMenuItem {
   private final GameProfile profile;
   private final Supplier<PlayerSkin> skin;
   private final Component name;

   public PlayerMenuItem(GameProfile var1) {
      super();
      this.profile = var1;
      this.skin = Minecraft.getInstance().getSkinManager().lookupInsecure(var1);
      this.name = Component.literal(var1.getName());
   }

   public void selectItem(SpectatorMenu var1) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
   }

   public Component getName() {
      return this.name;
   }

   public void renderIcon(GuiGraphics var1, float var2, int var3) {
      var1.setColor(1.0F, 1.0F, 1.0F, (float)var3 / 255.0F);
      PlayerFaceRenderer.draw(var1, (PlayerSkin)((PlayerSkin)this.skin.get()), 2, 2, 12);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean isEnabled() {
      return true;
   }
}
