package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSetJigsawBlockPacket implements Packet {
   private BlockPos pos;
   private ResourceLocation attachementType;
   private ResourceLocation targetPool;
   private String finalState;

   public ServerboundSetJigsawBlockPacket() {
   }

   public ServerboundSetJigsawBlockPacket(BlockPos var1, ResourceLocation var2, ResourceLocation var3, String var4) {
      this.pos = var1;
      this.attachementType = var2;
      this.targetPool = var3;
      this.finalState = var4;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.attachementType = var1.readResourceLocation();
      this.targetPool = var1.readResourceLocation();
      this.finalState = var1.readUtf(32767);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeResourceLocation(this.attachementType);
      var1.writeResourceLocation(this.targetPool);
      var1.writeUtf(this.finalState);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   public ResourceLocation getAttachementType() {
      return this.attachementType;
   }

   public String getFinalState() {
      return this.finalState;
   }
}
