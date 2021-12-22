package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record ClientboundBlockBreakAckPacket(BlockPos a, BlockState b, ServerboundPlayerActionPacket.Action c, boolean d) implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final BlockState state;
   private final ServerboundPlayerActionPacket.Action action;
   private final boolean allGood;
   private static final Logger LOGGER = LogManager.getLogger();

   public ClientboundBlockBreakAckPacket(BlockPos var1, BlockState var2, ServerboundPlayerActionPacket.Action var3, boolean var4, String var5) {
      this(var1, var2, var3, var4);
   }

   public ClientboundBlockBreakAckPacket(BlockPos var1, BlockState var2, ServerboundPlayerActionPacket.Action var3, boolean var4) {
      super();
      var1 = var1.immutable();
      this.pos = var1;
      this.state = var2;
      this.action = var3;
      this.allGood = var4;
   }

   public ClientboundBlockBreakAckPacket(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), (BlockState)Block.BLOCK_STATE_REGISTRY.byId(var1.readVarInt()), (ServerboundPlayerActionPacket.Action)var1.readEnum(ServerboundPlayerActionPacket.Action.class), var1.readBoolean());
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeVarInt(Block.getId(this.state));
      var1.writeEnum(this.action);
      var1.writeBoolean(this.allGood);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockBreakAck(this);
   }

   public BlockPos pos() {
      return this.pos;
   }

   public BlockState state() {
      return this.state;
   }

   public ServerboundPlayerActionPacket.Action action() {
      return this.action;
   }

   public boolean allGood() {
      return this.allGood;
   }
}
