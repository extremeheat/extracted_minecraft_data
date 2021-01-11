package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData extends CommandBase {
   public CommandEntityData() {
      super();
   }

   public String func_71517_b() {
      return "entitydata";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.entitydata.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.entitydata.usage", new Object[0]);
      } else {
         Entity var3 = func_175768_b(var1, var2[0]);
         if (var3 instanceof EntityPlayer) {
            throw new CommandException("commands.entitydata.noPlayers", new Object[]{var3.func_145748_c_()});
         } else {
            NBTTagCompound var4 = new NBTTagCompound();
            var3.func_70109_d(var4);
            NBTTagCompound var5 = (NBTTagCompound)var4.func_74737_b();

            NBTTagCompound var6;
            try {
               var6 = JsonToNBT.func_180713_a(func_147178_a(var1, var2, 1).func_150260_c());
            } catch (NBTException var8) {
               throw new CommandException("commands.entitydata.tagError", new Object[]{var8.getMessage()});
            }

            var6.func_82580_o("UUIDMost");
            var6.func_82580_o("UUIDLeast");
            var4.func_179237_a(var6);
            if (var4.equals(var5)) {
               throw new CommandException("commands.entitydata.failed", new Object[]{var4.toString()});
            } else {
               var3.func_70020_e(var4);
               func_152373_a(var1, this, "commands.entitydata.success", new Object[]{var4.toString()});
            }
         }
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
