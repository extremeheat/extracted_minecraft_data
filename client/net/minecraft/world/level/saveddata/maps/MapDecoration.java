package net.minecraft.world.level.saveddata.maps;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecoration(Holder<MapDecorationType> type, byte x, byte y, byte rot, Optional<Component> name) {
   public static final StreamCodec<RegistryFriendlyByteBuf, MapDecoration> STREAM_CODEC;

   public MapDecoration(Holder<MapDecorationType> type, byte x, byte y, byte rot, Optional<Component> name) {
      super();
      rot = (byte)(rot & 15);
      this.type = type;
      this.x = x;
      this.y = y;
      this.rot = rot;
      this.name = name;
   }

   public ResourceLocation getSpriteLocation() {
      return ((MapDecorationType)this.type.value()).assetId();
   }

   public boolean renderOnFrame() {
      return ((MapDecorationType)this.type.value()).showOnItemFrame();
   }

   public Holder<MapDecorationType> type() {
      return this.type;
   }

   public byte x() {
      return this.x;
   }

   public byte y() {
      return this.y;
   }

   public byte rot() {
      return this.rot;
   }

   public Optional<Component> name() {
      return this.name;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(MapDecorationType.STREAM_CODEC, MapDecoration::type, ByteBufCodecs.BYTE, MapDecoration::x, ByteBufCodecs.BYTE, MapDecoration::y, ByteBufCodecs.BYTE, MapDecoration::rot, ComponentSerialization.OPTIONAL_STREAM_CODEC, MapDecoration::name, MapDecoration::new);
   }
}
