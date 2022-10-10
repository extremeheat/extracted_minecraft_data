package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType field_198624_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.save.alreadyOn", new Object[0]));

   public static void func_198621_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("save-on").requires((var0x) -> {
         return var0x.func_197034_c(4);
      })).executes((var0x) -> {
         CommandSource var1 = (CommandSource)var0x.getSource();
         boolean var2 = false;
         Iterator var3 = var1.func_197028_i().func_212370_w().iterator();

         while(var3.hasNext()) {
            WorldServer var4 = (WorldServer)var3.next();
            if (var4 != null && var4.field_73058_d) {
               var4.field_73058_d = false;
               var2 = true;
            }
         }

         if (!var2) {
            throw field_198624_a.create();
         } else {
            var1.func_197030_a(new TextComponentTranslation("commands.save.enabled", new Object[0]), true);
            return 1;
         }
      }));
   }
}
