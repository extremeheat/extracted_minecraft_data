package net.minecraft.client.resources;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelType;

public class DefaultPlayerSkin {
   private static final PlayerSkin[] DEFAULT_SKINS;

   public DefaultPlayerSkin() {
      super();
   }

   public static ResourceLocation getDefaultTexture() {
      return getDefaultSkin().texture();
   }

   public static PlayerSkin getDefaultSkin() {
      return DEFAULT_SKINS[6];
   }

   public static PlayerSkin get(UUID var0) {
      return DEFAULT_SKINS[Math.floorMod(var0.hashCode(), DEFAULT_SKINS.length)];
   }

   public static PlayerSkin get(GameProfile var0) {
      return get(var0.id());
   }

   private static PlayerSkin create(String var0, PlayerModelType var1) {
      return new PlayerSkin(ResourceLocation.withDefaultNamespace(var0), (String)null, (ResourceLocation)null, (ResourceLocation)null, var1, true);
   }

   static {
      DEFAULT_SKINS = new PlayerSkin[]{create("textures/entity/player/slim/alex.png", PlayerModelType.SLIM), create("textures/entity/player/slim/ari.png", PlayerModelType.SLIM), create("textures/entity/player/slim/efe.png", PlayerModelType.SLIM), create("textures/entity/player/slim/kai.png", PlayerModelType.SLIM), create("textures/entity/player/slim/makena.png", PlayerModelType.SLIM), create("textures/entity/player/slim/noor.png", PlayerModelType.SLIM), create("textures/entity/player/slim/steve.png", PlayerModelType.SLIM), create("textures/entity/player/slim/sunny.png", PlayerModelType.SLIM), create("textures/entity/player/slim/zuri.png", PlayerModelType.SLIM), create("textures/entity/player/wide/alex.png", PlayerModelType.WIDE), create("textures/entity/player/wide/ari.png", PlayerModelType.WIDE), create("textures/entity/player/wide/efe.png", PlayerModelType.WIDE), create("textures/entity/player/wide/kai.png", PlayerModelType.WIDE), create("textures/entity/player/wide/makena.png", PlayerModelType.WIDE), create("textures/entity/player/wide/noor.png", PlayerModelType.WIDE), create("textures/entity/player/wide/steve.png", PlayerModelType.WIDE), create("textures/entity/player/wide/sunny.png", PlayerModelType.WIDE), create("textures/entity/player/wide/zuri.png", PlayerModelType.WIDE)};
   }
}
