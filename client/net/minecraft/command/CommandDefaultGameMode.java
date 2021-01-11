package net.minecraft.command;

import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandDefaultGameMode extends CommandGameMode {
   public CommandDefaultGameMode() {
      super();
   }

   public String func_71517_b() {
      return "defaultgamemode";
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.defaultgamemode.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
      } else {
         WorldSettings.GameType var3 = this.func_71539_b(var1, var2[0]);
         this.func_71541_a(var3);
         func_152373_a(var1, this, "commands.defaultgamemode.success", new Object[]{new ChatComponentTranslation("gameMode." + var3.func_77149_b(), new Object[0])});
      }
   }

   protected void func_71541_a(WorldSettings.GameType var1) {
      MinecraftServer var2 = MinecraftServer.func_71276_C();
      var2.func_71235_a(var1);
      EntityPlayerMP var4;
      if (var2.func_104056_am()) {
         for(Iterator var3 = MinecraftServer.func_71276_C().func_71203_ab().func_181057_v().iterator(); var3.hasNext(); var4.field_70143_R = 0.0F) {
            var4 = (EntityPlayerMP)var3.next();
            var4.func_71033_a(var1);
         }
      }

   }
}
