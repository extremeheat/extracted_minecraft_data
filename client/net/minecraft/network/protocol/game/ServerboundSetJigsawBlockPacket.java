package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetJigsawBlockPacket> STREAM_CODEC = Packet.codec(ServerboundSetJigsawBlockPacket::write, ServerboundSetJigsawBlockPacket::new);
   private final BlockPos pos;
   private final ResourceLocation name;
   private final ResourceLocation target;
   private final ResourceLocation pool;
   private final String finalState;
   private final JigsawBlockEntity.JointType joint;
   private final int selectionPriority;
   private final int placementPriority;

   public ServerboundSetJigsawBlockPacket(BlockPos var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, String var5, JigsawBlockEntity.JointType var6, int var7, int var8) {
      super();
      this.pos = var1;
      this.name = var2;
      this.target = var3;
      this.pool = var4;
      this.finalState = var5;
      this.joint = var6;
      this.selectionPriority = var7;
      this.placementPriority = var8;
   }

   private ServerboundSetJigsawBlockPacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.name = var1.readResourceLocation();
      this.target = var1.readResourceLocation();
      this.pool = var1.readResourceLocation();
      this.finalState = var1.readUtf();
      this.joint = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.byName(var1.readUtf()).orElse(JigsawBlockEntity.JointType.ALIGNED);
      this.selectionPriority = var1.readVarInt();
      this.placementPriority = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeResourceLocation(this.name);
      var1.writeResourceLocation(this.target);
      var1.writeResourceLocation(this.pool);
      var1.writeUtf(this.finalState);
      var1.writeUtf(this.joint.getSerializedName());
      var1.writeVarInt(this.selectionPriority);
      var1.writeVarInt(this.placementPriority);
   }

   public PacketType<ServerboundSetJigsawBlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_JIGSAW_BLOCK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceLocation getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawBlockEntity.JointType getJoint() {
      return this.joint;
   }

   public int getSelectionPriority() {
      return this.selectionPriority;
   }

   public int getPlacementPriority() {
      return this.placementPriority;
   }
}
