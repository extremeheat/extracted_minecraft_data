package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecorationType(ResourceLocation d, boolean e, int f, boolean g, boolean h) {
   private final ResourceLocation assetId;
   private final boolean showOnItemFrame;
   private final int mapColor;
   private final boolean explorationMapElement;
   private final boolean trackCount;
   public static final int NO_MAP_COLOR = -1;
   public static final Codec<Holder<MapDecorationType>> CODEC = BuiltInRegistries.MAP_DECORATION_TYPE.holderByNameCodec();
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(
      Registries.MAP_DECORATION_TYPE
   );

   public MapDecorationType(ResourceLocation var1, boolean var2, int var3, boolean var4, boolean var5) {
      super();
      this.assetId = var1;
      this.showOnItemFrame = var2;
      this.mapColor = var3;
      this.explorationMapElement = var4;
      this.trackCount = var5;
   }

   public boolean hasMapColor() {
      return this.mapColor != -1;
   }
}
