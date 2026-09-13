package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings$GameType;

public class CommandDefaultGameMode extends CommandGameMode {
   public CommandDefaultGameMode() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "defaultgamemode";
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.defaultgamemode.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0) {
         WorldSettings$GameType var3 = this.func_71539_b(var1, var2[0]);
         this.func_71541_a(var3);
         func_152373_a(var1, this, "commands.defaultgamemode.success", new Object[]{new ChatComponentTranslation("gameMode." + var3.func_77149_b())});
      } else {
         throw new WrongUsageException("commands.defaultgamemode.usage");
      }
   }

   protected void func_71541_a(WorldSettings$GameType var1) {
      MinecraftServer var2 = MinecraftServer.func_71276_C();
      var2.func_71235_a(var1);
      if (var2.func_104056_am()) {
         for(EntityPlayerMP var4 : MinecraftServer.func_71276_C().func_71203_ab().field_72404_b) {
            var4.func_71033_a(var1);
            var4.field_70143_R = 0.0F;
         }
      }
   }
}
