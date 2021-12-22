package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
   private final BlockPos pos;
   private final ResourceLocation name;
   private final ResourceLocation target;
   private final ResourceLocation pool;
   private final String finalState;
   private final JigsawBlockEntity.JointType joint;

   public ServerboundSetJigsawBlockPacket(BlockPos var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, String var5, JigsawBlockEntity.JointType var6) {
      super();
      this.pos = var1;
      this.name = var2;
      this.target = var3;
      this.pool = var4;
      this.finalState = var5;
      this.joint = var6;
   }

   public ServerboundSetJigsawBlockPacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.name = var1.readResourceLocation();
      this.target = var1.readResourceLocation();
      this.pool = var1.readResourceLocation();
      this.finalState = var1.readUtf();
      this.joint = (JigsawBlockEntity.JointType)JigsawBlockEntity.JointType.byName(var1.readUtf()).orElse(JigsawBlockEntity.JointType.ALIGNED);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeResourceLocation(this.name);
      var1.writeResourceLocation(this.target);
      var1.writeResourceLocation(this.pool);
      var1.writeUtf(this.finalState);
      var1.writeUtf(this.joint.getSerializedName());
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
}
