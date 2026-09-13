package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
   public static final RConConsoleSource field_70010_a = new RConConsoleSource();
   private StringBuffer field_70009_b = new StringBuffer();

   public RConConsoleSource() {
      super();
   }

   @Override
   public String func_70005_c_() {
      return "Rcon";
   }

   @Override
   public IChatComponent func_145748_c_() {
      return new ChatComponentText(this.func_70005_c_());
   }

   @Override
   public void func_145747_a(IChatComponent var1) {
      this.field_70009_b.append(var1.func_150260_c());
   }

   @Override
   public boolean func_70003_b(int var1, String var2) {
      return true;
   }

   @Override
   public ChunkCoordinates func_82114_b() {
      return new ChunkCoordinates(0, 0, 0);
   }

   @Override
   public World func_130014_f_() {
      return MinecraftServer.func_71276_C().func_130014_f_();
   }
}
