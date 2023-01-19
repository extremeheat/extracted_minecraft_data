package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class DefaultPlayerSkin {
   private static final DefaultPlayerSkin.SkinType[] DEFAULT_SKINS = new DefaultPlayerSkin.SkinType[]{
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/alex.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/ari.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/efe.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/kai.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/makena.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/noor.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/steve.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/sunny.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/slim/zuri.png", DefaultPlayerSkin.ModelType.SLIM),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/alex.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/ari.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/efe.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/kai.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/makena.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/noor.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/steve.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/sunny.png", DefaultPlayerSkin.ModelType.WIDE),
      new DefaultPlayerSkin.SkinType("textures/entity/player/wide/zuri.png", DefaultPlayerSkin.ModelType.WIDE)
   };

   public DefaultPlayerSkin() {
      super();
   }

   public static ResourceLocation getDefaultSkin() {
      return DEFAULT_SKINS[6].texture();
   }

   public static ResourceLocation getDefaultSkin(UUID var0) {
      return getSkinType(var0).texture;
   }

   public static String getSkinModelName(UUID var0) {
      return getSkinType(var0).model.id;
   }

   private static DefaultPlayerSkin.SkinType getSkinType(UUID var0) {
      return DEFAULT_SKINS[Math.floorMod(var0.hashCode(), DEFAULT_SKINS.length)];
   }

   static enum ModelType {
      SLIM("slim"),
      WIDE("default");

      final String id;

      private ModelType(String var3) {
         this.id = var3;
      }
   }

   static record SkinType(ResourceLocation a, DefaultPlayerSkin.ModelType b) {
      final ResourceLocation texture;
      final DefaultPlayerSkin.ModelType model;

      public SkinType(String var1, DefaultPlayerSkin.ModelType var2) {
         this(new ResourceLocation(var1), var2);
      }

      private SkinType(ResourceLocation var1, DefaultPlayerSkin.ModelType var2) {
         super();
         this.texture = var1;
         this.model = var2;
      }
   }
}
