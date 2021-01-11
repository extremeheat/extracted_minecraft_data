package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandGameMode extends CommandBase {
   public CommandGameMode() {
      super();
   }

   public String func_71517_b() {
      return "gamemode";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.gamemode.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
      } else {
         WorldSettings.GameType var3 = this.func_71539_b(var1, var2[0]);
         EntityPlayerMP var4 = var2.length >= 2 ? func_82359_c(var1, var2[1]) : func_71521_c(var1);
         var4.func_71033_a(var3);
         var4.field_70143_R = 0.0F;
         if (var1.func_130014_f_().func_82736_K().func_82766_b("sendCommandFeedback")) {
            var4.func_145747_a(new ChatComponentTranslation("gameMode.changed", new Object[0]));
         }

         ChatComponentTranslation var5 = new ChatComponentTranslation("gameMode." + var3.func_77149_b(), new Object[0]);
         if (var4 != var1) {
            func_152374_a(var1, this, 1, "commands.gamemode.success.other", new Object[]{var4.func_70005_c_(), var5});
         } else {
            func_152374_a(var1, this, 1, "commands.gamemode.success.self", new Object[]{var5});
         }

      }
   }

   protected WorldSettings.GameType func_71539_b(ICommandSender var1, String var2) throws NumberInvalidException {
      if (!var2.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.func_77149_b()) && !var2.equalsIgnoreCase("s")) {
         if (!var2.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.func_77149_b()) && !var2.equalsIgnoreCase("c")) {
            if (!var2.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.func_77149_b()) && !var2.equalsIgnoreCase("a")) {
               return !var2.equalsIgnoreCase(WorldSettings.GameType.SPECTATOR.func_77149_b()) && !var2.equalsIgnoreCase("sp") ? WorldSettings.func_77161_a(func_175764_a(var2, 0, WorldSettings.GameType.values().length - 2)) : WorldSettings.GameType.SPECTATOR;
            } else {
               return WorldSettings.GameType.ADVENTURE;
            }
         } else {
            return WorldSettings.GameType.CREATIVE;
         }
      } else {
         return WorldSettings.GameType.SURVIVAL;
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"survival", "creative", "adventure", "spectator"});
      } else {
         return var2.length == 2 ? func_71530_a(var2, this.func_71538_c()) : null;
      }
   }

   protected String[] func_71538_c() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 1;
   }
}
