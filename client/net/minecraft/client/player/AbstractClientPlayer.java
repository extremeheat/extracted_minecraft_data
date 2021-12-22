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
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public abstract class AbstractClientPlayer extends Player {
   private static final String SKIN_URL_TEMPLATE = "http://skins.minecraft.net/MinecraftSkins/%s.png";
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   @Nullable
   private PlayerInfo playerInfo;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public final ClientLevel clientLevel;

   public AbstractClientPlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var1.getSharedSpawnPos(), var1.getSharedSpawnAngle(), var2);
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

   public static void registerSkinTexture(ResourceLocation var0, String var1) {
      TextureManager var2 = Minecraft.getInstance().getTextureManager();
      AbstractTexture var3 = var2.getTexture(var0, MissingTextureAtlasSprite.getTexture());
      if (var3 == MissingTextureAtlasSprite.getTexture()) {
         HttpTexture var4 = new HttpTexture((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtil.stripColor(var1)), DefaultPlayerSkin.getDefaultSkin(createPlayerUUID(var1)), true, (Runnable)null);
         var2.register((ResourceLocation)var0, (AbstractTexture)var4);
      }

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
      if (this.getAbilities().flying) {
         var1 *= 1.1F;
      }

      var1 = (float)((double)var1 * ((this.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double)this.getAbilities().getWalkingSpeed() + 1.0D) / 2.0D));
      if (this.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      ItemStack var2 = this.getUseItem();
      if (this.isUsingItem()) {
         if (var2.method_87(Items.BOW)) {
            int var3 = this.getTicksUsingItem();
            float var4 = (float)var3 / 20.0F;
            if (var4 > 1.0F) {
               var4 = 1.0F;
            } else {
               var4 *= var4;
            }

            var1 *= 1.0F - var4 * 0.15F;
         } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.isScoping()) {
            return 0.1F;
         }
      }

      return Mth.lerp(Minecraft.getInstance().options.fovEffectScale, 1.0F, var1);
   }
}
