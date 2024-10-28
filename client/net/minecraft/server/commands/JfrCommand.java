package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;

public class JfrCommand {
   private static final SimpleCommandExceptionType START_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.jfr.start.failed"));
   private static final DynamicCommandExceptionType DUMP_FAILED = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.jfr.dump.failed", var0);
   });

   private JfrCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("jfr").requires((var0x) -> {
         return var0x.hasPermission(4);
      })).then(Commands.literal("start").executes((var0x) -> {
         return startJfr((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("stop").executes((var0x) -> {
         return stopJfr((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int startJfr(CommandSourceStack var0) throws CommandSyntaxException {
      Environment var1 = Environment.from(var0.getServer());
      if (!JvmProfiler.INSTANCE.start(var1)) {
         throw START_FAILED.create();
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.jfr.started");
         }, false);
         return 1;
      }
   }

   private static int stopJfr(CommandSourceStack var0) throws CommandSyntaxException {
      try {
         Path var1 = Paths.get(".").relativize(JvmProfiler.INSTANCE.stop().normalize());
         Path var2 = var0.getServer().isPublished() && !SharedConstants.IS_RUNNING_IN_IDE ? var1 : var1.toAbsolutePath();
         MutableComponent var3 = Component.literal(var1.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((var1x) -> {
            return var1x.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, var2.toString())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")));
         });
         var0.sendSuccess(() -> {
            return Component.translatable("commands.jfr.stopped", var3);
         }, false);
         return 1;
      } catch (Throwable var4) {
         throw DUMP_FAILED.create(var4.getMessage());
      }
   }
}
