package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

public class VersionCommand {
   private static final Component HEADER = Component.translatable("commands.version.header");
   private static final Component STABLE = Component.translatable("commands.version.stable.yes");
   private static final Component UNSTABLE = Component.translatable("commands.version.stable.no");

   public VersionCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, boolean var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("version").requires(Commands.hasPermission(var1 ? Commands.LEVEL_GAMEMASTERS : Commands.LEVEL_ALL))).executes((var0x) -> {
         CommandSourceStack var1 = (CommandSourceStack)var0x.getSource();
         var1.sendSystemMessage(HEADER);
         Objects.requireNonNull(var1);
         dumpVersion(var1::sendSystemMessage);
         return 1;
      }));
   }

   public static void dumpVersion(Consumer<Component> var0) {
      WorldVersion var1 = SharedConstants.getCurrentVersion();
      var0.accept(Component.translatable("commands.version.id", var1.id()));
      var0.accept(Component.translatable("commands.version.name", var1.name()));
      var0.accept(Component.translatable("commands.version.data", var1.dataVersion().version()));
      var0.accept(Component.translatable("commands.version.series", var1.dataVersion().series()));
      Object[] var10002 = new Object[]{var1.protocolVersion(), null};
      String var10005 = Integer.toHexString(var1.protocolVersion());
      var10002[1] = "0x" + var10005;
      var0.accept(Component.translatable("commands.version.protocol", var10002));
      var0.accept(Component.translatable("commands.version.build_time", Component.translationArg(var1.buildTime())));
      var0.accept(Component.translatable("commands.version.pack.resource", var1.packVersion(PackType.CLIENT_RESOURCES).toString()));
      var0.accept(Component.translatable("commands.version.pack.data", var1.packVersion(PackType.SERVER_DATA).toString()));
      var0.accept(var1.stable() ? STABLE : UNSTABLE);
   }
}
