package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile field_178668_a;
   private final ResourceLocation field_178667_b;

   public PlayerMenuObject(GameProfile var1) {
      super();
      this.field_178668_a = var1;
      this.field_178667_b = AbstractClientPlayer.func_110311_f(var1.getName());
      AbstractClientPlayer.func_110304_a(this.field_178667_b, var1.getName());
   }

   public void func_178661_a(SpectatorMenu var1) {
      Minecraft.func_71410_x().func_147114_u().func_147297_a(new C18PacketSpectate(this.field_178668_a.getId()));
   }

   public IChatComponent func_178664_z_() {
      return new ChatComponentText(this.field_178668_a.getName());
   }

   public void func_178663_a(float var1, int var2) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(this.field_178667_b);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, (float)var2 / 255.0F);
      Gui.func_152125_a(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      Gui.func_152125_a(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
   }

   public boolean func_178662_A_() {
      return true;
   }
}
