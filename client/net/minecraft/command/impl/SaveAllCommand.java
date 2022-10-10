package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.SessionLockException;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType field_198616_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.save.failed", new Object[0]));

   public static void func_198611_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("save-all").requires((var0x) -> {
         return var0x.func_197034_c(4);
      })).executes((var0x) -> {
         return func_198614_a((CommandSource)var0x.getSource(), false);
      })).then(Commands.func_197057_a("flush").executes((var0x) -> {
         return func_198614_a((CommandSource)var0x.getSource(), true);
      })));
   }

   private static int func_198614_a(CommandSource var0, boolean var1) throws CommandSyntaxException {
      var0.func_197030_a(new TextComponentTranslation("commands.save.saving", new Object[0]), false);
      MinecraftServer var2 = var0.func_197028_i();
      boolean var3 = false;
      var2.func_184103_al().func_72389_g();
      Iterator var4 = var2.func_212370_w().iterator();

      while(var4.hasNext()) {
         WorldServer var5 = (WorldServer)var4.next();
         if (var5 != null && func_198612_a(var5, var1)) {
            var3 = true;
         }
      }

      if (!var3) {
         throw field_198616_a.create();
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.save.success", new Object[0]), true);
         return 1;
      }
   }

   private static boolean func_198612_a(WorldServer var0, boolean var1) {
      boolean var2 = var0.field_73058_d;
      var0.field_73058_d = false;

      boolean var4;
      try {
         var0.func_73044_a(true, (IProgressUpdate)null);
         if (var1) {
            var0.func_104140_m();
         }

         return true;
      } catch (SessionLockException var8) {
         var4 = false;
      } finally {
         var0.field_73058_d = var2;
      }

      return var4;
   }
}
