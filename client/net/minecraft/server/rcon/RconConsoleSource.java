package net.minecraft.server.rcon;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TheGame;
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

   public CommandSourceStack createCommandSourceStack(TheGame var1) {
      ServerLevel var2 = var1.overworld();
      return new CommandSourceStack(this, Vec3.atLowerCornerOf(var2.getSharedSpawnPos()), Vec2.ZERO, var2, 4, "Rcon", RCON_COMPONENT, var1, (Entity)null);
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
