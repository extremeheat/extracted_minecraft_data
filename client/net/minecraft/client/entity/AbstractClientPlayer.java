package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager$SkinAvailableCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public abstract class AbstractClientPlayer extends EntityPlayer implements SkinManager$SkinAvailableCallback {
   public static final ResourceLocation field_110314_b = new ResourceLocation("textures/entity/steve.png");
   private ResourceLocation field_110312_d;
   private ResourceLocation field_110313_e;

   public AbstractClientPlayer(World var1, GameProfile var2) {
      super(var1, var2);
      String var3 = this.func_70005_c_();
      if (!var3.isEmpty()) {
         SkinManager var4 = Minecraft.func_71410_x().func_152342_ad();
         var4.func_152790_a(var2, this, true);
      }
   }

   public boolean func_152122_n() {
      return this.field_110313_e != null;
   }

   public boolean func_152123_o() {
      return this.field_110312_d != null;
   }

   public ResourceLocation func_110306_p() {
      return this.field_110312_d == null ? field_110314_b : this.field_110312_d;
   }

   public ResourceLocation func_110303_q() {
      return this.field_110313_e;
   }

   public static ThreadDownloadImageData func_110304_a(ResourceLocation var0, String var1) {
      TextureManager var2 = Minecraft.func_71410_x().func_110434_K();
      Object var3 = var2.func_110581_b(var0);
      if (var3 == null) {
         var3 = new ThreadDownloadImageData(
            null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.func_76338_a(var1)), field_110314_b, new ImageBufferDownload()
         );
         var2.func_110579_a(var0, (ITextureObject)var3);
      }

      return (ThreadDownloadImageData)var3;
   }

   public static ResourceLocation func_110311_f(String var0) {
      return new ResourceLocation("skins/" + StringUtils.func_76338_a(var0));
   }

   @Override
   public void func_152121_a(Type var1, ResourceLocation var2) {
      switch(var1) {
         case SKIN:
            this.field_110312_d = var2;
            break;
         case CAPE:
            this.field_110313_e = var2;
      }
   }
}
