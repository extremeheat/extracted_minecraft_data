package net.minecraft.server.rcon;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RconConsoleSource implements CommandSource {
   private static final String RCON = "Rcon";
   private static final Component RCON_COMPONENT = Component.literal("Rcon");
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RconConsoleSource(MinecraftServer var1) {
      super();
      this.server = var1;
   }

   public void prepareForCommand() {
      this.buffer.setLength(0);
   }

   public String getCommandResponse() {
      return this.buffer.toString();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel var1 = this.server.overworld();
      return new CommandSourceStack(this, Vec3.atLowerCornerOf(var1.getSharedSpawnPos()), Vec2.ZERO, var1, 4, "Rcon", RCON_COMPONENT, this.server, (Entity)null);
   }

   public void sendSystemMessage(Component var1) {
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
