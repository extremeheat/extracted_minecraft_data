package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandMessage extends CommandBase {
   public CommandMessage() {
      super();
   }

   public List<String> func_71514_a() {
      return Arrays.asList("w", "msg");
   }

   public String func_71517_b() {
      return "tell";
   }

   public int func_82362_a() {
      return 0;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.message.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.message.usage", new Object[0]);
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         if (var3 == var1) {
            throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
         } else {
            IChatComponent var4 = func_147176_a(var1, var2, 1, !(var1 instanceof EntityPlayer));
            ChatComponentTranslation var5 = new ChatComponentTranslation("commands.message.display.incoming", new Object[]{var1.func_145748_c_(), var4.func_150259_f()});
            ChatComponentTranslation var6 = new ChatComponentTranslation("commands.message.display.outgoing", new Object[]{var3.func_145748_c_(), var4.func_150259_f()});
            var5.func_150256_b().func_150238_a(EnumChatFormatting.GRAY).func_150217_b(true);
            var6.func_150256_b().func_150238_a(EnumChatFormatting.GRAY).func_150217_b(true);
            var3.func_145747_a(var5);
            var1.func_145747_a(var6);
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
