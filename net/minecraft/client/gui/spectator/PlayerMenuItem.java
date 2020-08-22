package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerMenuItem implements SpectatorMenuItem {
   private final GameProfile profile;
   private final ResourceLocation location;

   public PlayerMenuItem(GameProfile var1) {
      this.profile = var1;
      Minecraft var2 = Minecraft.getInstance();
      Map var3 = var2.getSkinManager().getInsecureSkinInformation(var1);
      if (var3.containsKey(Type.SKIN)) {
         this.location = var2.getSkinManager().registerTexture((MinecraftProfileTexture)var3.get(Type.SKIN), Type.SKIN);
      } else {
         this.location = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(var1));
      }

   }

   public void selectItem(SpectatorMenu var1) {
      Minecraft.getInstance().getConnection().send((Packet)(new ServerboundTeleportToEntityPacket(this.profile.getId())));
   }

   public Component getName() {
      return new TextComponent(this.profile.getName());
   }

   public void renderIcon(float var1, int var2) {
      Minecraft.getInstance().getTextureManager().bind(this.location);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float)var2 / 255.0F);
      GuiComponent.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
      GuiComponent.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
   }

   public boolean isEnabled() {
      return true;
   }
}
