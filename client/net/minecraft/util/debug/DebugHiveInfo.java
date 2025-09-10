package net.minecraft.util.debug;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

public record DebugHiveInfo(Block type, int occupantCount, int honeyLevel, boolean sedated) {
   public static final StreamCodec<RegistryFriendlyByteBuf, DebugHiveInfo> STREAM_CODEC;

   public DebugHiveInfo(Block var1, int var2, int var3, boolean var4) {
      super();
      this.type = var1;
      this.occupantCount = var2;
      this.honeyLevel = var3;
      this.sedated = var4;
   }

   public static DebugHiveInfo pack(BeehiveBlockEntity var0) {
      return new DebugHiveInfo(var0.getBlockState().getBlock(), var0.getOccupantCount(), BeehiveBlockEntity.getHoneyLevel(var0.getBlockState()), var0.isSedated());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.registry(Registries.BLOCK), DebugHiveInfo::type, ByteBufCodecs.VAR_INT, DebugHiveInfo::occupantCount, ByteBufCodecs.VAR_INT, DebugHiveInfo::honeyLevel, ByteBufCodecs.BOOL, DebugHiveInfo::sedated, DebugHiveInfo::new);
   }
}
