package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
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
      Map var3 = var2.getSkinManager().getInsecureSkinInformation(var1);
      if (var3.containsKey(Type.SKIN)) {
         this.location = var2.getSkinManager().registerTexture((MinecraftProfileTexture)var3.get(Type.SKIN), Type.SKIN);
      } else {
         this.location = DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(var1));
      }

      this.name = Component.literal(var1.getName());
   }

   public void selectItem(SpectatorMenu var1) {
      Minecraft.getInstance().getConnection().send((Packet)(new ServerboundTeleportToEntityPacket(this.profile.getId())));
   }

   public Component getName() {
      return this.name;
   }

   public void renderIcon(PoseStack var1, float var2, int var3) {
      RenderSystem.setShaderTexture(0, this.location);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)var3 / 255.0F);
      GuiComponent.blit(var1, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
      GuiComponent.blit(var1, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
   }

   public boolean isEnabled() {
      return true;
   }
}
