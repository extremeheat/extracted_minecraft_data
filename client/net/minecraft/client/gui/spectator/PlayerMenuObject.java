package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile field_178668_a;
   private final ResourceLocation field_178667_b;

   public PlayerMenuObject(GameProfile var1) {
      super();
      this.field_178668_a = var1;
      Minecraft var2 = Minecraft.func_71410_x();
      Map var3 = var2.func_152342_ad().func_152788_a(var1);
      if (var3.containsKey(Type.SKIN)) {
         this.field_178667_b = var2.func_152342_ad().func_152792_a((MinecraftProfileTexture)var3.get(Type.SKIN), Type.SKIN);
      } else {
         this.field_178667_b = DefaultPlayerSkin.func_177334_a(EntityPlayer.func_146094_a(var1));
      }

   }

   public void func_178661_a(SpectatorMenu var1) {
      Minecraft.func_71410_x().func_147114_u().func_147297_a(new CPacketSpectate(this.field_178668_a.getId()));
   }

   public ITextComponent func_178664_z_() {
      return new TextComponentString(this.field_178668_a.getName());
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
