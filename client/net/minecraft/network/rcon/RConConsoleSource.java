package net.minecraft.network.rcon;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
   private static final RConConsoleSource field_70010_a = new RConConsoleSource();
   private StringBuffer field_70009_b = new StringBuffer();

   public RConConsoleSource() {
      super();
   }

   public String func_70005_c_() {
      return "Rcon";
   }

   public IChatComponent func_145748_c_() {
      return new ChatComponentText(this.func_70005_c_());
   }

   public void func_145747_a(IChatComponent var1) {
      this.field_70009_b.append(var1.func_150260_c());
   }

   public boolean func_70003_b(int var1, String var2) {
      return true;
   }

   public BlockPos func_180425_c() {
      return new BlockPos(0, 0, 0);
   }

   public Vec3 func_174791_d() {
      return new Vec3(0.0D, 0.0D, 0.0D);
   }

   public World func_130014_f_() {
      return MinecraftServer.func_71276_C().func_130014_f_();
   }

   public Entity func_174793_f() {
      return null;
   }

   public boolean func_174792_t_() {
      return true;
   }

   public void func_174794_a(CommandResultStats.Type var1, int var2) {
   }
}
