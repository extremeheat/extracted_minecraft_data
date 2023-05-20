package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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
   public void renderIcon(PoseStack var1, float var2, int var3) {
      RenderSystem.setShaderTexture(0, this.location);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)var3 / 255.0F);
      PlayerFaceRenderer.draw(var1, 2, 2, 12);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean isEnabled() {
      return true;
   }
}
