package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatMessageContent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.players.PlayerList;
import org.slf4j.Logger;

public class MessageArgument implements SignedArgument<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
   private static final Logger LOGGER = LogUtils.getLogger();

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

   public static MessageArgument.ChatMessage getChatMessage(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      MessageArgument.Message var2 = (MessageArgument.Message)var0.getArgument(var1, MessageArgument.Message.class);
      Component var3 = var2.resolveComponent((CommandSourceStack)var0.getSource());
      CommandSigningContext var4 = ((CommandSourceStack)var0.getSource()).getSigningContext();
      PlayerChatMessage var5 = var4.getArgument(var1);
      if (var5 == null) {
         ChatMessageContent var6 = new ChatMessageContent(var2.text, var3);
         return new MessageArgument.ChatMessage(PlayerChatMessage.system(var6));
      } else {
         return new MessageArgument.ChatMessage(ChatDecorator.attachIfNotDecorated(var5, var3));
      }
   }

   public MessageArgument.Message parse(StringReader var1) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(var1, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public String getSignableText(MessageArgument.Message var1) {
      return var1.getText();
   }

   public CompletableFuture<Component> resolvePreview(CommandSourceStack var1, MessageArgument.Message var2) throws CommandSyntaxException {
      return var2.resolveDecoratedComponent(var1);
   }

   @Override
   public Class<MessageArgument.Message> getValueType() {
      return MessageArgument.Message.class;
   }

   static void logResolutionFailure(CommandSourceStack var0, CompletableFuture<?> var1) {
      var1.exceptionally(var1x -> {
         LOGGER.error("Encountered unexpected exception while resolving chat message argument from '{}'", var0.getDisplayName().getString(), var1x);
         return null;
      });
   }

   public static record ChatMessage(PlayerChatMessage a) {
      private final PlayerChatMessage signedArgument;

      public ChatMessage(PlayerChatMessage var1) {
         super();
         this.signedArgument = var1;
      }

      public void resolve(CommandSourceStack var1, Consumer<PlayerChatMessage> var2) {
         MinecraftServer var3 = var1.getServer();
         var1.getChatMessageChainer().append(() -> {
            CompletableFuture var4 = this.filterPlainText(var1, this.signedArgument.signedContent().plain());
            CompletableFuture var5 = var3.getChatDecorator().decorate(var1.getPlayer(), this.signedArgument);
            return CompletableFuture.allOf(var4, var5).thenAcceptAsync(var3xx -> {
               PlayerChatMessage var4x = ((PlayerChatMessage)var5.join()).filter(((FilteredText)var4.join()).mask());
               var2.accept(var4x);
            }, var3);
         });
      }

      private CompletableFuture<FilteredText> filterPlainText(CommandSourceStack var1, String var2) {
         ServerPlayer var3 = var1.getPlayer();
         return var3 != null && this.signedArgument.hasSignatureFrom(var3.getUUID())
            ? var3.getTextFilter().processStreamMessage(var2)
            : CompletableFuture.completedFuture(FilteredText.passThrough(var2));
      }

      public void consume(CommandSourceStack var1) {
         if (!this.signedArgument.signer().isSystem()) {
            this.resolve(var1, var1x -> {
               PlayerList var2 = var1.getServer().getPlayerList();
               var2.broadcastMessageHeader(var1x, Set.of());
            });
         }
      }
   }

   public static class Message {
      final String text;
      private final MessageArgument.Part[] parts;

      public Message(String var1, MessageArgument.Part[] var2) {
         super();
         this.text = var1;
         this.parts = var2;
      }

      public String getText() {
         return this.text;
      }

      public MessageArgument.Part[] getParts() {
         return this.parts;
      }

      CompletableFuture<Component> resolveDecoratedComponent(CommandSourceStack var1) throws CommandSyntaxException {
         Component var2 = this.resolveComponent(var1);
         CompletableFuture var3 = var1.getServer().getChatDecorator().decorate(var1.getPlayer(), var2);
         MessageArgument.logResolutionFailure(var1, var3);
         return var3;
      }

      Component resolveComponent(CommandSourceStack var1) throws CommandSyntaxException {
         return this.toComponent(var1, var1.hasPermission(2));
      }

      public Component toComponent(CommandSourceStack var1, boolean var2) throws CommandSyntaxException {
         if (this.parts.length != 0 && var2) {
            MutableComponent var3 = Component.literal(this.text.substring(0, this.parts[0].getStart()));
            int var4 = this.parts[0].getStart();

            for(MessageArgument.Part var8 : this.parts) {
               Component var9 = var8.toComponent(var1);
               if (var4 < var8.getStart()) {
                  var3.append(this.text.substring(var4, var8.getStart()));
               }

               if (var9 != null) {
                  var3.append(var9);
               }

               var4 = var8.getEnd();
            }

            if (var4 < this.text.length()) {
               var3.append(this.text.substring(var4));
            }

            return var3;
         } else {
            return Component.literal(this.text);
         }
      }

      public static MessageArgument.Message parseText(StringReader var0, boolean var1) throws CommandSyntaxException {
         String var2 = var0.getString().substring(var0.getCursor(), var0.getTotalLength());
         if (!var1) {
            var0.setCursor(var0.getTotalLength());
            return new MessageArgument.Message(var2, new MessageArgument.Part[0]);
         } else {
            ArrayList var3 = Lists.newArrayList();
            int var4 = var0.getCursor();

            while(true) {
               int var5;
               EntitySelector var6;
               while(true) {
                  if (!var0.canRead()) {
                     return new MessageArgument.Message(var2, var3.toArray(new MessageArgument.Part[0]));
                  }

                  if (var0.peek() == '@') {
                     var5 = var0.getCursor();

                     try {
                        EntitySelectorParser var7 = new EntitySelectorParser(var0);
                        var6 = var7.parse();
                        break;
                     } catch (CommandSyntaxException var8) {
                        if (var8.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE
                           && var8.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                           throw var8;
                        }

                        var0.setCursor(var5 + 1);
                     }
                  } else {
                     var0.skip();
                  }
               }

               var3.add(new MessageArgument.Part(var5 - var4, var0.getCursor() - var4, var6));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int var1, int var2, EntitySelector var3) {
         super();
         this.start = var1;
         this.end = var2;
         this.selector = var3;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      public EntitySelector getSelector() {
         return this.selector;
      }

      @Nullable
      public Component toComponent(CommandSourceStack var1) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.findEntities(var1));
      }
   }
}
