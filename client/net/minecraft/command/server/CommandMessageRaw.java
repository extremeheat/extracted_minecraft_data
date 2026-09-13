package net.minecraft.command.server;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase {
   public CommandMessageRaw() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "tellraw";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.tellraw.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.tellraw.usage");
      } else {
         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         String var4 = func_82360_a(var1, var2, 1);

         try {
            IChatComponent var5 = IChatComponent$Serializer.func_150699_a(var4);
            var3.func_145747_a(var5);
         } catch (JsonParseException var7) {
            Throwable var6 = ExceptionUtils.getRootCause(var7);
            throw new SyntaxErrorException("commands.tellraw.jsonException", var6 == null ? "" : var6.getMessage());
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
