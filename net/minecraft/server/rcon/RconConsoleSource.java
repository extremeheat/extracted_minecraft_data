package net.minecraft.server.rcon;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RconConsoleSource implements CommandSource {
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RconConsoleSource(MinecraftServer var1) {
      this.server = var1;
   }

   public void prepareForCommand() {
      this.buffer.setLength(0);
   }

   public String getCommandResponse() {
      return this.buffer.toString();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel var1 = this.server.getLevel(DimensionType.OVERWORLD);
      return new CommandSourceStack(this, new Vec3(var1.getSharedSpawnPos()), Vec2.ZERO, var1, 4, "Recon", new TextComponent("Rcon"), this.server, (Entity)null);
   }

   public void sendMessage(Component var1) {
      this.buffer.append(var1.getString());
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return this.server.shouldRconBroadcast();
   }
}
