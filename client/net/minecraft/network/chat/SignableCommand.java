package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.arguments.SignedArgument;

public record SignableCommand<S>(List<SignableCommand.Argument<S>> a) {
   private final List<SignableCommand.Argument<S>> arguments;

   public SignableCommand(List<SignableCommand.Argument<S>> var1) {
      super();
      this.arguments = var1;
   }

   public static <S> SignableCommand<S> of(ParseResults<S> var0) {
      String var1 = var0.getReader().getString();
      CommandContextBuilder var2 = var0.getContext();
      CommandContextBuilder var3 = var2;

      List var4;
      CommandContextBuilder var5;
      for(var4 = collectArguments(var1, var2); (var5 = var3.getChild()) != null; var3 = var5) {
         boolean var6 = var5.getRootNode() != var2.getRootNode();
         if (!var6) {
            break;
         }

         var4.addAll(collectArguments(var1, var5));
      }

      return new SignableCommand<>(var4);
   }

   private static <S> List<SignableCommand.Argument<S>> collectArguments(String var0, CommandContextBuilder<S> var1) {
      ArrayList var2 = new ArrayList();

      for(ParsedCommandNode var4 : var1.getNodes()) {
         CommandNode var6 = var4.getNode();
         if (var6 instanceof ArgumentCommandNode var5 && var5.getType() instanceof SignedArgument) {
            ParsedArgument var8 = (ParsedArgument)var1.getArguments().get(var5.getName());
            if (var8 != null) {
               String var7 = var8.getRange().get(var0);
               var2.add(new SignableCommand.Argument((ArgumentCommandNode<S, ?>)var5, var7));
            }
         }
      }

      return var2;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> a, String b) {
      private final ArgumentCommandNode<S, ?> node;
      private final String value;

      public Argument(ArgumentCommandNode<S, ?> var1, String var2) {
         super();
         this.node = var1;
         this.value = var2;
      }

      public String name() {
         return this.node.getName();
      }
   }
}
