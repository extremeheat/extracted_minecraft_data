package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;

public class CommandGameMode extends CommandBase {
   public CommandGameMode() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "gamemode";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.gamemode.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0) {
         WorldSettings$GameType var3 = this.func_71539_b(var1, var2[0]);
         EntityPlayerMP var4 = var2.length >= 2 ? func_82359_c(var1, var2[1]) : func_71521_c(var1);
         var4.func_71033_a(var3);
         var4.field_70143_R = 0.0F;
         ChatComponentTranslation var5 = new ChatComponentTranslation("gameMode." + var3.func_77149_b());
         if (var4 != var1) {
            func_152374_a(var1, this, 1, "commands.gamemode.success.other", new Object[]{var4.func_70005_c_(), var5});
         } else {
            func_152374_a(var1, this, 1, "commands.gamemode.success.self", new Object[]{var5});
         }
      } else {
         throw new WrongUsageException("commands.gamemode.usage");
      }
   }

   protected WorldSettings$GameType func_71539_b(ICommandSender var1, String var2) {
      if (var2.equalsIgnoreCase(WorldSettings$GameType.SURVIVAL.func_77149_b()) || var2.equalsIgnoreCase("s")) {
         return WorldSettings$GameType.SURVIVAL;
      } else if (var2.equalsIgnoreCase(WorldSettings$GameType.CREATIVE.func_77149_b()) || var2.equalsIgnoreCase("c")) {
         return WorldSettings$GameType.CREATIVE;
      } else {
         return !var2.equalsIgnoreCase(WorldSettings$GameType.ADVENTURE.func_77149_b()) && !var2.equalsIgnoreCase("a")
            ? WorldSettings.func_77161_a(func_71532_a(var1, var2, 0, WorldSettings$GameType.values().length - 2))
            : WorldSettings$GameType.ADVENTURE;
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"survival", "creative", "adventure"});
      } else {
         return var2.length == 2 ? func_71530_a(var2, this.func_71538_c()) : null;
      }
   }

   protected String[] func_71538_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 1;
   }
}
