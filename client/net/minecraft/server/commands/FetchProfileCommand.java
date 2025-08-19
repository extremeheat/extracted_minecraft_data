package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.component.ResolvableProfile;

public class FetchProfileCommand {
   public FetchProfileCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fetchprofile").requires(Commands.hasPermission(2))).then(Commands.literal("name").then(Commands.argument("name", StringArgumentType.greedyString()).executes((var0x) -> resolveName((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "name")))))).then(Commands.literal("id").then(Commands.argument("id", UuidArgument.uuid()).executes((var0x) -> resolveId((CommandSourceStack)var0x.getSource(), UuidArgument.getUuid(var0x, "id"))))));
   }

   private static void reportResolvedProfile(CommandSourceStack var0, GameProfile var1, String var2, Component var3) {
      ResolvableProfile var4 = ResolvableProfile.createResolved(var1);
      ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, var4).ifSuccess((var3x) -> {
         String var4 = var3x.toString();
         var0.sendSuccess(() -> {
            MutableComponent var3x = ComponentUtils.formatList(List.of(Component.translatable("commands.profile_fetch.copy_component").withStyle((UnaryOperator)((var1) -> var1.withClickEvent(new ClickEvent.CopyToClipboard(var4)))), Component.translatable("commands.profile_fetch.give_item").withStyle((UnaryOperator)((var1) -> var1.withClickEvent(new ClickEvent.RunCommand("give @s minecraft:player_head[profile=" + var4 + "]"))))), CommonComponents.SPACE, (var0) -> ComponentUtils.wrapInSquareBrackets(var0.withStyle(ChatFormatting.GREEN)));
            return Component.translatable(var2, var3, var3x);
         }, false);
      }).ifError((var1x) -> var0.sendFailure(Component.translatable("commands.profile_fetch.failed_to_serialize", var1x.message())));
   }

   private static int resolveName(CommandSourceStack var0, String var1) {
      MinecraftServer var2 = var0.getServer();
      ProfileResolver var3 = var2.services().profileResolver();
      Util.nonCriticalIoPool().execute(() -> {
         MutableComponent var4 = Component.literal(var1);
         Optional var5 = var3.fetchByName(var1);
         var2.execute(() -> var5.ifPresentOrElse((var2) -> reportResolvedProfile(var0, var2, "commands.profile_fetch.name.success", var4), () -> var0.sendFailure(Component.translatable("commands.profile_fetch.name.failure", var4))));
      });
      return 1;
   }

   private static int resolveId(CommandSourceStack var0, UUID var1) {
      MinecraftServer var2 = var0.getServer();
      ProfileResolver var3 = var2.services().profileResolver();
      Util.nonCriticalIoPool().execute(() -> {
         Component var4 = Component.translationArg(var1);
         Optional var5 = var3.fetchById(var1);
         var2.execute(() -> var5.ifPresentOrElse((var2) -> reportResolvedProfile(var0, var2, "commands.profile_fetch.id.success", var4), () -> var0.sendFailure(Component.translatable("commands.profile_fetch.id.failure", var4))));
      });
      return 1;
   }
}
