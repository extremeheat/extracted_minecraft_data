package net.minecraft.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum TextureFilteringMethod {
   NONE(0, "options.textureFiltering.none"),
   RGSS(1, "options.textureFiltering.rgss"),
   ANISOTROPIC(2, "options.textureFiltering.anisotropic");

   private static final IntFunction<TextureFilteringMethod> BY_ID = ByIdMap.<TextureFilteringMethod>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<TextureFilteringMethod> LEGACY_CODEC;
   private final int id;
   private final Component caption;

   private TextureFilteringMethod(final int var3, final String var4) {
      this.id = var3;
      this.caption = Component.translatable(var4);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static TextureFilteringMethod[] $values() {
      return new TextureFilteringMethod[]{NONE, RGSS, ANISOTROPIC};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      IntFunction var10001 = BY_ID;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::apply, (var0) -> var0.id);
   }
}
