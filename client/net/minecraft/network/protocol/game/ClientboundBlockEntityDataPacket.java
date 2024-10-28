package net.minecraft.network.protocol.game;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket> STREAM_CODEC;
   private final BlockPos pos;
   private final BlockEntityType<?> type;
   private final CompoundTag tag;

   public static ClientboundBlockEntityDataPacket create(BlockEntity var0, BiFunction<BlockEntity, RegistryAccess, CompoundTag> var1) {
      RegistryAccess var2 = var0.getLevel().registryAccess();
      return new ClientboundBlockEntityDataPacket(var0.getBlockPos(), var0.getType(), (CompoundTag)var1.apply(var0, var2));
   }

   public static ClientboundBlockEntityDataPacket create(BlockEntity var0) {
      return create(var0, BlockEntity::getUpdateTag);
   }

   private ClientboundBlockEntityDataPacket(BlockPos var1, BlockEntityType<?> var2, CompoundTag var3) {
      super();
      this.pos = var1;
      this.type = var2;
      this.tag = var3;
   }

   public PacketType<ClientboundBlockEntityDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_ENTITY_DATA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockEntityData(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   public CompoundTag getTag() {
      return this.tag;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundBlockEntityDataPacket::getPos, ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE), ClientboundBlockEntityDataPacket::getType, ByteBufCodecs.TRUSTED_COMPOUND_TAG, ClientboundBlockEntityDataPacket::getTag, ClientboundBlockEntityDataPacket::new);
   }
}
