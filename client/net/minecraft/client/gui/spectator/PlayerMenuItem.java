package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;

public class PlayerMenuItem implements SpectatorMenuItem {
   private final GameProfile profile;
   private final ResourceLocation location;
   private final Component name;

   public PlayerMenuItem(GameProfile var1) {
      super();
      this.profile = var1;
      Minecraft var2 = Minecraft.getInstance();
      this.location = var2.getSkinManager().getInsecureSkinLocation(var1);
      this.name = Component.literal(var1.getName());
   }

   @Override
   public void selectItem(SpectatorMenu var1) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
   }

   @Override
   public Component getName() {
      return this.name;
   }

   @Override
   public void renderIcon(GuiGraphics var1, float var2, int var3) {
      var1.setColor(1.0F, 1.0F, 1.0F, (float)var3 / 255.0F);
      PlayerFaceRenderer.draw(var1, this.location, 2, 2, 12);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}
