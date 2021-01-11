package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTestFor extends CommandBase {
   public CommandTestFor() {
      super();
   }

   public String func_71517_b() {
      return "testfor";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.testfor.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.testfor.usage", new Object[0]);
      } else {
         Entity var3 = func_175768_b(var1, var2[0]);
         NBTTagCompound var4 = null;
         if (var2.length >= 2) {
            try {
               var4 = JsonToNBT.func_180713_a(func_180529_a(var2, 1));
            } catch (NBTException var6) {
               throw new CommandException("commands.testfor.tagError", new Object[]{var6.getMessage()});
            }
         }

         if (var4 != null) {
            NBTTagCompound var5 = new NBTTagCompound();
            var3.func_70109_d(var5);
            if (!NBTUtil.func_181123_a(var4, var5, true)) {
               throw new CommandException("commands.testfor.failure", new Object[]{var3.func_70005_c_()});
            }
         }

         func_152373_a(var1, this, "commands.testfor.success", new Object[]{var3.func_70005_c_()});
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }
}
