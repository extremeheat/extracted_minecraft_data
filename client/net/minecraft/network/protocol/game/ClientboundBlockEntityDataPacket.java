package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final BlockEntityType<?> type;
   @Nullable
   private final CompoundTag tag;

   public static ClientboundBlockEntityDataPacket create(BlockEntity var0, Function<BlockEntity, CompoundTag> var1) {
      return new ClientboundBlockEntityDataPacket(var0.getBlockPos(), var0.getType(), (CompoundTag)var1.apply(var0));
   }

   public static ClientboundBlockEntityDataPacket create(BlockEntity var0) {
      return create(var0, BlockEntity::getUpdateTag);
   }

   private ClientboundBlockEntityDataPacket(BlockPos var1, BlockEntityType<?> var2, CompoundTag var3) {
      super();
      this.pos = var1;
      this.type = var2;
      this.tag = var3.isEmpty() ? null : var3;
   }

   public ClientboundBlockEntityDataPacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.type = var1.readById(Registry.BLOCK_ENTITY_TYPE);
      this.tag = var1.readNbt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeId(Registry.BLOCK_ENTITY_TYPE, this.type);
      var1.writeNbt(this.tag);
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

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }
}
