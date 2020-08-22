package net.minecraft.client.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public abstract class AbstractClientPlayer extends Player {
   private PlayerInfo playerInfo;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public final ClientLevel clientLevel;

   public AbstractClientPlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
      this.clientLevel = var1;
   }

   public boolean isSpectator() {
      PlayerInfo var1 = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return var1 != null && var1.getGameMode() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      PlayerInfo var1 = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return var1 != null && var1.getGameMode() == GameType.CREATIVE;
   }

   public boolean isCapeLoaded() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   protected PlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
      }

      return this.playerInfo;
   }

   public boolean isSkinLoaded() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 != null && var1.isSkinLoaded();
   }

   public ResourceLocation getSkinTextureLocation() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : var1.getSkinLocation();
   }

   @Nullable
   public ResourceLocation getCloakTextureLocation() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? null : var1.getCapeLocation();
   }

   public boolean isElytraLoaded() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   public ResourceLocation getElytraTextureLocation() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? null : var1.getElytraLocation();
   }

   public static HttpTexture registerSkinTexture(ResourceLocation var0, String var1) {
      TextureManager var2 = Minecraft.getInstance().getTextureManager();
      Object var3 = var2.getTexture(var0);
      if (var3 == null) {
         var3 = new HttpTexture((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtil.stripColor(var1)), DefaultPlayerSkin.getDefaultSkin(createPlayerUUID(var1)), true, (Runnable)null);
         var2.register((ResourceLocation)var0, (AbstractTexture)var3);
      }

      return (HttpTexture)var3;
   }

   public static ResourceLocation getSkinLocation(String var0) {
      return new ResourceLocation("skins/" + Hashing.sha1().hashUnencodedChars(StringUtil.stripColor(var0)));
   }

   public String getModelName() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : var1.getModelName();
   }

   public float getFieldOfViewModifier() {
      float var1 = 1.0F;
      if (this.abilities.flying) {
         var1 *= 1.1F;
      }

      AttributeInstance var2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      var1 = (float)((double)var1 * ((var2.getValue() / (double)this.abilities.getWalkingSpeed() + 1.0D) / 2.0D));
      if (this.abilities.getWalkingSpeed() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      if (this.isUsingItem() && this.getUseItem().getItem() == Items.BOW) {
         int var3 = this.getTicksUsingItem();
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
