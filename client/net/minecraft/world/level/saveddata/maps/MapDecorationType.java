package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecorationType(ResourceLocation assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
   public static final int NO_MAP_COLOR = -1;
   public static final Codec<Holder<MapDecorationType>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> STREAM_CODEC;

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

   public ResourceLocation assetId() {
      return this.assetId;
   }

   public boolean showOnItemFrame() {
      return this.showOnItemFrame;
   }

   public int mapColor() {
      return this.mapColor;
   }

   public boolean explorationMapElement() {
      return this.explorationMapElement;
   }

   public boolean trackCount() {
      return this.trackCount;
   }

   static {
      CODEC = BuiltInRegistries.MAP_DECORATION_TYPE.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.MAP_DECORATION_TYPE);
   }
}
