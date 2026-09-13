package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetSpawnpoint extends CommandBase {
   public CommandSetSpawnpoint() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "spawnpoint";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.spawnpoint.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      EntityPlayerMP var3 = var2.length == 0 ? func_71521_c(var1) : func_82359_c(var1, var2[0]);
      if (var2.length == 4) {
         if (var3.field_70170_p != null) {
            int var4 = 1;
            int var5 = 30000000;
            int var6 = func_71532_a(var1, var2[var4++], -var5, var5);
            int var7 = func_71532_a(var1, var2[var4++], 0, 256);
            int var8 = func_71532_a(var1, var2[var4++], -var5, var5);
            var3.func_71063_a(new ChunkCoordinates(var6, var7, var8), true);
            func_152373_a(var1, this, "commands.spawnpoint.success", new Object[]{var3.func_70005_c_(), var6, var7, var8});
         }
      } else {
         if (var2.length > 1) {
            throw new WrongUsageException("commands.spawnpoint.usage");
         }

         ChunkCoordinates var12 = var3.func_82114_b();
         var3.func_71063_a(var12, true);
         func_152373_a(
            var1, this, "commands.spawnpoint.success", new Object[]{var3.func_70005_c_(), var12.field_71574_a, var12.field_71572_b, var12.field_71573_c}
         );
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length != 1 && var2.length != 2 ? null : func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
