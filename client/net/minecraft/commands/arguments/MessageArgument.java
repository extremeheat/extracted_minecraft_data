package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;

public class MessageArgument implements SignedArgument<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
   static final Dynamic2CommandExceptionType TOO_LONG = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("argument.message.too_long", var0, var1)
   );

   public MessageArgument() {
      super();
   }

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      MessageArgument.Message var2 = (MessageArgument.Message)var0.getArgument(var1, MessageArgument.Message.class);
      return var2.resolveComponent((CommandSourceStack)var0.getSource());
   }

   public static void resolveChatMessage(CommandContext<CommandSourceStack> var0, String var1, Consumer<PlayerChatMessage> var2) throws CommandSyntaxException {
      MessageArgument.Message var3 = (MessageArgument.Message)var0.getArgument(var1, MessageArgument.Message.class);
      CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
      Component var5 = var3.resolveComponent(var4);
      CommandSigningContext var6 = var4.getSigningContext();
      PlayerChatMessage var7 = var6.getArgument(var1);
      if (var7 != null) {
         resolveSignedMessage(var2, var4, var7.withUnsignedContent(var5));
      } else {
         resolveDisguisedMessage(var2, var4, PlayerChatMessage.system(var3.text).withUnsignedContent(var5));
      }
   }

   private static void resolveSignedMessage(Consumer<PlayerChatMessage> var0, CommandSourceStack var1, PlayerChatMessage var2) {
      MinecraftServer var3 = var1.getServer();
      CompletableFuture var4 = filterPlainText(var1, var2);
      Component var5 = var3.getChatDecorator().decorate(var1.getPlayer(), var2.decoratedContent());
      var1.getChatMessageChainer().append(var4, var3x -> {
         PlayerChatMessage var4x = var2.withUnsignedContent(var5).filter(var3x.mask());
         var0.accept(var4x);
      });
   }

   private static void resolveDisguisedMessage(Consumer<PlayerChatMessage> var0, CommandSourceStack var1, PlayerChatMessage var2) {
      ChatDecorator var3 = var1.getServer().getChatDecorator();
      Component var4 = var3.decorate(var1.getPlayer(), var2.decoratedContent());
      var0.accept(var2.withUnsignedContent(var4));
   }

   private static CompletableFuture<FilteredText> filterPlainText(CommandSourceStack var0, PlayerChatMessage var1) {
      ServerPlayer var2 = var0.getPlayer();
      return var2 != null && var1.hasSignatureFrom(var2.getUUID())
         ? var2.getTextFilter().processStreamMessage(var1.signedContent())
         : CompletableFuture.completedFuture(FilteredText.passThrough(var1.signedContent()));
   }

   public MessageArgument.Message parse(StringReader var1) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(var1, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
