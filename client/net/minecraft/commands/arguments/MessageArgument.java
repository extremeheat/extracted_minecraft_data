package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import org.slf4j.Logger;

public class MessageArgument implements SignedArgument<Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");
   static final Logger LOGGER = LogUtils.getLogger();

   public MessageArgument() {
      super();
   }

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Message var2 = (Message)var0.getArgument(var1, Message.class);
      return var2.resolveComponent((CommandSourceStack)var0.getSource());
   }

   public static ChatMessage getChatMessage(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Message var2 = (Message)var0.getArgument(var1, Message.class);
      Component var3 = var2.resolveComponent((CommandSourceStack)var0.getSource());
      CommandSigningContext var4 = ((CommandSourceStack)var0.getSource()).getSigningContext();
      MessageSignature var5 = var4.getArgumentSignature(var1);
      boolean var6 = var4.signedArgumentPreview(var1);
      ChatSender var7 = ((CommandSourceStack)var0.getSource()).asChatSender();
      return var5.isValid(var7.uuid()) ? new ChatMessage(var2.text, var3, var5, var6) : new ChatMessage(var2.text, var3, MessageSignature.unsigned(), false);
   }

   public Message parse(StringReader var1) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(var1, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public Component getPlainSignableComponent(Message var1) {
      return Component.literal(var1.getText());
   }

   public CompletableFuture<Component> resolvePreview(CommandSourceStack var1, Message var2) throws CommandSyntaxException {
      return var2.resolveDecoratedComponent(var1);
   }

   public Class<Message> getValueType() {
      return Message.class;
   }

   static void logResolutionFailure(CommandSourceStack var0, CompletableFuture<?> var1) {
      var1.exceptionally((var1x) -> {
         LOGGER.error("Encountered unexpected exception while resolving chat message argument from '{}'", var0.getDisplayName().getString(), var1x);
         return null;
      });
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Message {
      final String text;
      private final Part[] parts;

      public Message(String var1, Part[] var2) {
         super();
         this.text = var1;
         this.parts = var2;
      }

      public String getText() {
         return this.text;
      }

      public Part[] getParts() {
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
            Part[] var5 = this.parts;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Part var8 = var5[var7];
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

      public static Message parseText(StringReader var0, boolean var1) throws CommandSyntaxException {
         String var2 = var0.getString().substring(var0.getCursor(), var0.getTotalLength());
         if (!var1) {
            var0.setCursor(var0.getTotalLength());
            return new Message(var2, new Part[0]);
         } else {
            ArrayList var3 = Lists.newArrayList();
            int var4 = var0.getCursor();

            while(true) {
               int var5;
               EntitySelector var6;
               label38:
               while(true) {
                  while(var0.canRead()) {
                     if (var0.peek() == '@') {
                        var5 = var0.getCursor();

                        try {
                           EntitySelectorParser var7 = new EntitySelectorParser(var0);
                           var6 = var7.parse();
                           break label38;
                        } catch (CommandSyntaxException var8) {
                           if (var8.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE && var8.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                              throw var8;
                           }

                           var0.setCursor(var5 + 1);
                        }
                     } else {
                        var0.skip();
                     }
                  }

                  return new Message(var2, (Part[])var3.toArray(new Part[0]));
               }

               var3.add(new Part(var5 - var4, var0.getCursor() - var4, var6));
            }
         }
      }
   }

   public static record ChatMessage(String a, Component b, MessageSignature c, boolean d) {
      private final String plain;
      private final Component formatted;
      private final MessageSignature signature;
      private final boolean signedPreview;

      public ChatMessage(String var1, Component var2, MessageSignature var3, boolean var4) {
         super();
         this.plain = var1;
         this.formatted = var2;
         this.signature = var3;
         this.signedPreview = var4;
      }

      public CompletableFuture<FilteredText<PlayerChatMessage>> resolve(CommandSourceStack var1) {
         CompletableFuture var2 = this.filterComponent(var1, this.formatted).thenComposeAsync((var2x) -> {
            ChatDecorator var3 = var1.getServer().getChatDecorator();
            return var3.decorateChat(var1.getPlayer(), var2x, this.signature, this.signedPreview);
         }, var1.getServer()).thenApply((var2x) -> {
            PlayerChatMessage var3 = this.getSignedMessage(var2x);
            if (var3 != null) {
               this.verify(var1, var3);
            }

            return var2x;
         });
         MessageArgument.logResolutionFailure(var1, var2);
         return var2;
      }

      @Nullable
      private PlayerChatMessage getSignedMessage(FilteredText<PlayerChatMessage> var1) {
         if (this.signature.isValid()) {
            return this.signedPreview ? (PlayerChatMessage)var1.raw() : PlayerChatMessage.signed(this.plain, this.signature);
         } else {
            return null;
         }
      }

      private void verify(CommandSourceStack var1, PlayerChatMessage var2) {
         if (!var2.verify(var1)) {
            MessageArgument.LOGGER.warn("{} sent message with invalid signature: '{}'", var1.getDisplayName().getString(), var2.signedContent().getString());
         }

      }

      private CompletableFuture<FilteredText<Component>> filterComponent(CommandSourceStack var1, Component var2) {
         ServerPlayer var3 = var1.getPlayer();
         return var3 != null ? var3.getTextFilter().processStreamComponent(var2) : CompletableFuture.completedFuture(FilteredText.passThrough(var2));
      }

      public String plain() {
         return this.plain;
      }

      public Component formatted() {
         return this.formatted;
      }

      public MessageSignature signature() {
         return this.signature;
      }

      public boolean signedPreview() {
         return this.signedPreview;
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
