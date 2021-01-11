package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
   public CommandDifficulty() {
      super();
   }

   public String func_71517_b() {
      return "difficulty";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.difficulty.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
      } else {
         EnumDifficulty var3 = this.func_180531_e(var2[0]);
         MinecraftServer.func_71276_C().func_147139_a(var3);
         func_152373_a(var1, this, "commands.difficulty.success", new Object[]{new ChatComponentTranslation(var3.func_151526_b(), new Object[0])});
      }
   }

   protected EnumDifficulty func_180531_e(String var1) throws NumberInvalidException {
      if (!var1.equalsIgnoreCase("peaceful") && !var1.equalsIgnoreCase("p")) {
         if (!var1.equalsIgnoreCase("easy") && !var1.equalsIgnoreCase("e")) {
            if (!var1.equalsIgnoreCase("normal") && !var1.equalsIgnoreCase("n")) {
               return !var1.equalsIgnoreCase("hard") && !var1.equalsIgnoreCase("h") ? EnumDifficulty.func_151523_a(func_175764_a(var1, 0, 3)) : EnumDifficulty.HARD;
            } else {
               return EnumDifficulty.NORMAL;
            }
         } else {
            return EnumDifficulty.EASY;
         }
      } else {
         return EnumDifficulty.PEACEFUL;
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
