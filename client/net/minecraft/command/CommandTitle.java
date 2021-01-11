package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase {
   private static final Logger field_175774_a = LogManager.getLogger();

   public CommandTitle() {
      super();
   }

   public String func_71517_b() {
      return "title";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.title.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.title.usage", new Object[0]);
      } else {
         if (var2.length < 3) {
            if ("title".equals(var2[1]) || "subtitle".equals(var2[1])) {
               throw new WrongUsageException("commands.title.usage.title", new Object[0]);
            }

            if ("times".equals(var2[1])) {
               throw new WrongUsageException("commands.title.usage.times", new Object[0]);
            }
         }

         EntityPlayerMP var3 = func_82359_c(var1, var2[0]);
         S45PacketTitle.Type var4 = S45PacketTitle.Type.func_179969_a(var2[1]);
         if (var4 != S45PacketTitle.Type.CLEAR && var4 != S45PacketTitle.Type.RESET) {
            if (var4 == S45PacketTitle.Type.TIMES) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.title.usage", new Object[0]);
               } else {
                  int var11 = func_175755_a(var2[2]);
                  int var12 = func_175755_a(var2[3]);
                  int var13 = func_175755_a(var2[4]);
                  S45PacketTitle var14 = new S45PacketTitle(var11, var12, var13);
                  var3.field_71135_a.func_147359_a(var14);
                  func_152373_a(var1, this, "commands.title.success", new Object[0]);
               }
            } else if (var2.length < 3) {
               throw new WrongUsageException("commands.title.usage", new Object[0]);
            } else {
               String var10 = func_180529_a(var2, 2);

               IChatComponent var6;
               try {
                  var6 = IChatComponent.Serializer.func_150699_a(var10);
               } catch (JsonParseException var9) {
                  Throwable var8 = ExceptionUtils.getRootCause(var9);
                  throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[]{var8 == null ? "" : var8.getMessage()});
               }

               S45PacketTitle var7 = new S45PacketTitle(var4, ChatComponentProcessor.func_179985_a(var1, var6, var3));
               var3.field_71135_a.func_147359_a(var7);
               func_152373_a(var1, this, "commands.title.success", new Object[0]);
            }
         } else if (var2.length != 2) {
            throw new WrongUsageException("commands.title.usage", new Object[0]);
         } else {
            S45PacketTitle var5 = new S45PacketTitle(var4, (IChatComponent)null);
            var3.field_71135_a.func_147359_a(var5);
            func_152373_a(var1, this, "commands.title.success", new Object[0]);
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
      } else {
         return var2.length == 2 ? func_71530_a(var2, S45PacketTitle.Type.func_179971_a()) : null;
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
