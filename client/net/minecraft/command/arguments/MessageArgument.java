package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> field_201313_a = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public MessageArgument() {
      super();
   }

   public static MessageArgument func_197123_a() {
      return new MessageArgument();
   }

   public static ITextComponent func_197124_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((MessageArgument.Message)var0.getArgument(var1, MessageArgument.Message.class)).func_201312_a((CommandSource)var0.getSource(), ((CommandSource)var0.getSource()).func_197034_c(2));
   }

   public MessageArgument.Message parse(StringReader var1) throws CommandSyntaxException {
      return MessageArgument.Message.func_197113_a(var1, true);
   }

   public Collection<String> getExamples() {
      return field_201313_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Part {
      private final int field_197119_a;
      private final int field_197120_b;
      private final EntitySelector field_197121_c;

      public Part(int var1, int var2, EntitySelector var3) {
         super();
         this.field_197119_a = var1;
         this.field_197120_b = var2;
         this.field_197121_c = var3;
      }

      public int func_197117_a() {
         return this.field_197119_a;
      }

      public int func_197118_b() {
         return this.field_197120_b;
      }

      @Nullable
      public ITextComponent func_197116_a(CommandSource var1) throws CommandSyntaxException {
         return EntitySelector.func_197350_a(this.field_197121_c.func_197341_b(var1));
      }
   }

   public static class Message {
      private final String field_197114_a;
      private final MessageArgument.Part[] field_197115_b;

      public Message(String var1, MessageArgument.Part[] var2) {
         super();
         this.field_197114_a = var1;
         this.field_197115_b = var2;
      }

      public ITextComponent func_201312_a(CommandSource var1, boolean var2) throws CommandSyntaxException {
         if (this.field_197115_b.length != 0 && var2) {
            TextComponentString var3 = new TextComponentString(this.field_197114_a.substring(0, this.field_197115_b[0].func_197117_a()));
            int var4 = this.field_197115_b[0].func_197117_a();
            MessageArgument.Part[] var5 = this.field_197115_b;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               MessageArgument.Part var8 = var5[var7];
               ITextComponent var9 = var8.func_197116_a(var1);
               if (var4 < var8.func_197117_a()) {
                  var3.func_150258_a(this.field_197114_a.substring(var4, var8.func_197117_a()));
               }

               if (var9 != null) {
                  var3.func_150257_a(var9);
               }

               var4 = var8.func_197118_b();
            }

            if (var4 < this.field_197114_a.length()) {
               var3.func_150258_a(this.field_197114_a.substring(var4, this.field_197114_a.length()));
            }

            return var3;
         } else {
            return new TextComponentString(this.field_197114_a);
         }
      }

      public static MessageArgument.Message func_197113_a(StringReader var0, boolean var1) throws CommandSyntaxException {
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
                           var6 = var7.func_201345_m();
                           break label38;
                        } catch (CommandSyntaxException var8) {
                           if (var8.getType() != EntitySelectorParser.field_197410_c && var8.getType() != EntitySelectorParser.field_197409_b) {
                              throw var8;
                           }

                           var0.setCursor(var5 + 1);
                        }
                     } else {
                        var0.skip();
                     }
                  }

                  return new MessageArgument.Message(var2, (MessageArgument.Part[])var3.toArray(new MessageArgument.Part[var3.size()]));
               }

               var3.add(new MessageArgument.Part(var5 - var4, var0.getCursor() - var4, var6));
            }
         }
      }
   }
}
