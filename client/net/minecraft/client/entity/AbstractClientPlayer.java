package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class AbstractClientPlayer extends EntityPlayer {
   private NetworkPlayerInfo field_175157_a;

   public AbstractClientPlayer(World var1, GameProfile var2) {
      super(var1, var2);
   }

   public boolean func_175149_v() {
      NetworkPlayerInfo var1 = Minecraft.func_71410_x().func_147114_u().func_175102_a(this.func_146103_bH().getId());
      return var1 != null && var1.func_178848_b() == WorldSettings.GameType.SPECTATOR;
   }

   public boolean func_152122_n() {
      return this.func_175155_b() != null;
   }

   protected NetworkPlayerInfo func_175155_b() {
      if (this.field_175157_a == null) {
         this.field_175157_a = Minecraft.func_71410_x().func_147114_u().func_175102_a(this.func_110124_au());
      }

      return this.field_175157_a;
   }

   public boolean func_152123_o() {
      NetworkPlayerInfo var1 = this.func_175155_b();
      return var1 != null && var1.func_178856_e();
   }

   public ResourceLocation func_110306_p() {
      NetworkPlayerInfo var1 = this.func_175155_b();
      return var1 == null ? DefaultPlayerSkin.func_177334_a(this.func_110124_au()) : var1.func_178837_g();
   }

   public ResourceLocation func_110303_q() {
      NetworkPlayerInfo var1 = this.func_175155_b();
      return var1 == null ? null : var1.func_178861_h();
   }

   public static ThreadDownloadImageData func_110304_a(ResourceLocation var0, String var1) {
      TextureManager var2 = Minecraft.func_71410_x().func_110434_K();
      Object var3 = var2.func_110581_b(var0);
      if (var3 == null) {
         var3 = new ThreadDownloadImageData((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.func_76338_a(var1)), DefaultPlayerSkin.func_177334_a(func_175147_b(var1)), new ImageBufferDownload());
         var2.func_110579_a(var0, (ITextureObject)var3);
      }

      return (ThreadDownloadImageData)var3;
   }

   public static ResourceLocation func_110311_f(String var0) {
      return new ResourceLocation("skins/" + StringUtils.func_76338_a(var0));
   }

   public String func_175154_l() {
      NetworkPlayerInfo var1 = this.func_175155_b();
      return var1 == null ? DefaultPlayerSkin.func_177332_b(this.func_110124_au()) : var1.func_178851_f();
   }

   public float func_175156_o() {
      float var1 = 1.0F;
      if (this.field_71075_bZ.field_75100_b) {
         var1 *= 1.1F;
      }

      IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      var1 = (float)((double)var1 * ((var2.func_111126_e() / (double)this.field_71075_bZ.func_75094_b() + 1.0D) / 2.0D));
      if (this.field_71075_bZ.func_75094_b() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      if (this.func_71039_bw() && this.func_71011_bu().func_77973_b() == Items.field_151031_f) {
         int var3 = this.func_71057_bx();
         float var4 = (float)var3 / 20.0F;
         if (var4 > 1.0F) {
            var4 = 1.0F;
         } else {
            var4 *= var4;
         }

         var1 *= 1.0F - var4 * 0.15F;
      }

      return var1;
   }
}
