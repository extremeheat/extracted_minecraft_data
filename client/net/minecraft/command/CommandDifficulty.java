package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
   public CommandDifficulty() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "difficulty";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.difficulty.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0) {
         EnumDifficulty var3 = this.func_147201_h(var1, var2[0]);
         MinecraftServer.func_71276_C().func_147139_a(var3);
         func_152373_a(var1, this, "commands.difficulty.success", new Object[]{new ChatComponentTranslation(var3.func_151526_b())});
      } else {
         throw new WrongUsageException("commands.difficulty.usage");
      }
   }

   protected EnumDifficulty func_147201_h(ICommandSender var1, String var2) {
      if (var2.equalsIgnoreCase("peaceful") || var2.equalsIgnoreCase("p")) {
         return EnumDifficulty.PEACEFUL;
      } else if (var2.equalsIgnoreCase("easy") || var2.equalsIgnoreCase("e")) {
         return EnumDifficulty.EASY;
      } else if (var2.equalsIgnoreCase("normal") || var2.equalsIgnoreCase("n")) {
         return EnumDifficulty.NORMAL;
      } else {
         return !var2.equalsIgnoreCase("hard") && !var2.equalsIgnoreCase("h")
            ? EnumDifficulty.func_151523_a(func_71532_a(var1, var2, 0, 3))
            : EnumDifficulty.HARD;
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
