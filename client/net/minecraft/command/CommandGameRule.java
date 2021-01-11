package net.minecraft.command;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   public CommandGameRule() {
      super();
   }

   public String func_71517_b() {
      return "gamerule";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.gamerule.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      GameRules var3 = this.func_82366_d();
      String var4 = var2.length > 0 ? var2[0] : "";
      String var5 = var2.length > 1 ? func_180529_a(var2, 1) : "";
      switch(var2.length) {
      case 0:
         var1.func_145747_a(new ChatComponentText(func_71527_a(var3.func_82763_b())));
         break;
      case 1:
         if (!var3.func_82765_e(var4)) {
            throw new CommandException("commands.gamerule.norule", new Object[]{var4});
         }

         String var6 = var3.func_82767_a(var4);
         var1.func_145747_a((new ChatComponentText(var4)).func_150258_a(" = ").func_150258_a(var6));
         var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var3.func_180263_c(var4));
         break;
      default:
         if (var3.func_180264_a(var4, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(var5) && !"false".equals(var5)) {
            throw new CommandException("commands.generic.boolean.invalid", new Object[]{var5});
         }

         var3.func_82764_b(var4, var5);
         func_175773_a(var3, var4);
         func_152373_a(var1, this, "commands.gamerule.success", new Object[0]);
      }

   }

   public static void func_175773_a(GameRules var0, String var1) {
      if ("reducedDebugInfo".equals(var1)) {
         int var2 = var0.func_82766_b(var1) ? 22 : 23;
         Iterator var3 = MinecraftServer.func_71276_C().func_71203_ab().func_181057_v().iterator();

         while(var3.hasNext()) {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
            var4.field_71135_a.func_147359_a(new S19PacketEntityStatus(var4, (byte)var2));
         }
      }

   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_82366_d().func_82763_b());
      } else {
         if (var2.length == 2) {
            GameRules var4 = this.func_82366_d();
            if (var4.func_180264_a(var2[0], GameRules.ValueType.BOOLEAN_VALUE)) {
               return func_71530_a(var2, new String[]{"true", "false"});
            }
         }

         return null;
      }
   }

   private GameRules func_82366_d() {
      return MinecraftServer.func_71276_C().func_71218_a(0).func_82736_K();
   }
}
