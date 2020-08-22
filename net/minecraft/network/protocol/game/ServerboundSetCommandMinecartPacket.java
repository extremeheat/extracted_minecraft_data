package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;

public class ServerboundSetCommandMinecartPacket implements Packet {
   private int entity;
   private String command;
   private boolean trackOutput;

   public ServerboundSetCommandMinecartPacket() {
   }

   public ServerboundSetCommandMinecartPacket(int var1, String var2, boolean var3) {
      this.entity = var1;
      this.command = var2;
      this.trackOutput = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entity = var1.readVarInt();
      this.command = var1.readUtf(32767);
      this.trackOutput = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entity);
      var1.writeUtf(this.command);
      var1.writeBoolean(this.trackOutput);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCommandMinecart(this);
   }

   @Nullable
   public BaseCommandBlock getCommandBlock(Level var1) {
      Entity var2 = var1.getEntity(this.entity);
      return var2 instanceof MinecartCommandBlock ? ((MinecartCommandBlock)var2).getCommandBlock() : null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }
}
