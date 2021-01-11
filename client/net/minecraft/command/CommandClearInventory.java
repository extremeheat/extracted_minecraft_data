package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase {
   public CommandClearInventory() {
      super();
   }

   public String func_71517_b() {
      return "clear";
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.clear.usage";
   }

   public int func_82362_a() {
      return 2;
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      EntityPlayerMP var3 = var2.length == 0 ? func_71521_c(var1) : func_82359_c(var1, var2[0]);
      Item var4 = var2.length >= 2 ? func_147179_f(var1, var2[1]) : null;
      int var5 = var2.length >= 3 ? func_180528_a(var2[2], -1) : -1;
      int var6 = var2.length >= 4 ? func_180528_a(var2[3], -1) : -1;
      NBTTagCompound var7 = null;
      if (var2.length >= 5) {
         try {
            var7 = JsonToNBT.func_180713_a(func_180529_a(var2, 4));
         } catch (NBTException var9) {
            throw new CommandException("commands.clear.tagError", new Object[]{var9.getMessage()});
         }
      }

      if (var2.length >= 2 && var4 == null) {
         throw new CommandException("commands.clear.failure", new Object[]{var3.func_70005_c_()});
      } else {
         int var8 = var3.field_71071_by.func_174925_a(var4, var5, var6, var7);
         var3.field_71069_bz.func_75142_b();
         if (!var3.field_71075_bZ.field_75098_d) {
            var3.func_71113_k();
         }

         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, var8);
         if (var8 == 0) {
            throw new CommandException("commands.clear.failure", new Object[]{var3.func_70005_c_()});
         } else {
            if (var6 == 0) {
               var1.func_145747_a(new ChatComponentTranslation("commands.clear.testing", new Object[]{var3.func_70005_c_(), var8}));
            } else {
               func_152373_a(var1, this, "commands.clear.success", new Object[]{var3.func_70005_c_(), var8});
            }

         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_147209_d());
      } else {
         return var2.length == 2 ? func_175762_a(var2, Item.field_150901_e.func_148742_b()) : null;
      }
   }

   protected String[] func_147209_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
