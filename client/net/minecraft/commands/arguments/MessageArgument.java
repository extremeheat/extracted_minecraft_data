package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public MessageArgument() {
      super();
   }

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((MessageArgument.Message)var0.getArgument(var1, MessageArgument.Message.class)).toComponent((CommandSourceStack)var0.getSource(), ((CommandSourceStack)var0.getSource()).hasPermission(2));
   }

   public MessageArgument.Message parse(StringReader var1) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(var1, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Message {
      private final String text;
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

      public Component toComponent(CommandSourceStack var1, boolean var2) throws CommandSyntaxException {
         if (this.parts.length != 0 && var2) {
            TextComponent var3 = new TextComponent(this.text.substring(0, this.parts[0].getStart()));
            int var4 = this.parts[0].getStart();
            MessageArgument.Part[] var5 = this.parts;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               MessageArgument.Part var8 = var5[var7];
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
            return new TextComponent(this.text);
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

                  return new MessageArgument.Message(var2, (MessageArgument.Part[])var3.toArray(new MessageArgument.Part[0]));
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
