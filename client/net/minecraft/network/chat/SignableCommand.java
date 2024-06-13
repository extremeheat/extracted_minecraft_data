package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.SignedArgument;

public record SignableCommand<S>(List<SignableCommand.Argument<S>> arguments) {
   public SignableCommand(List<SignableCommand.Argument<S>> arguments) {
      super();
      this.arguments = arguments;
   }

   public static <S> boolean hasSignableArguments(ParseResults<S> var0) {
      return !of(var0).arguments().isEmpty();
   }

   public static <S> SignableCommand<S> of(ParseResults<S> var0) {
      String var1 = var0.getReader().getString();
      CommandContextBuilder var2 = var0.getContext();
      CommandContextBuilder var3 = var2;
      List var4 = collectArguments(var1, var2);

      CommandContextBuilder var5;
      while ((var5 = var3.getChild()) != null && var5.getRootNode() != var2.getRootNode()) {
         var4.addAll(collectArguments(var1, var5));
         var3 = var5;
      }

      return new SignableCommand<>(var4);
   }

   private static <S> List<SignableCommand.Argument<S>> collectArguments(String var0, CommandContextBuilder<S> var1) {
      ArrayList var2 = new ArrayList();

      for (ParsedCommandNode var4 : var1.getNodes()) {
         CommandNode var6 = var4.getNode();
         if (var6 instanceof ArgumentCommandNode) {
            ArgumentCommandNode var5 = (ArgumentCommandNode)var6;
            if (var5.getType() instanceof SignedArgument) {
               ParsedArgument var8 = (ParsedArgument)var1.getArguments().get(var5.getName());
               if (var8 != null) {
                  String var7 = var8.getRange().get(var0);
                  var2.add(new SignableCommand.Argument(var5, var7));
               }
            }
         }
      }

      return var2;
   }

   @Nullable
   public SignableCommand.Argument<S> getArgument(String var1) {
      for (SignableCommand.Argument var3 : this.arguments) {
         if (var1.equals(var3.name())) {
            return var3;
         }
      }

      return null;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> node, String value) {
      public Argument(ArgumentCommandNode<S, ?> node, String value) {
         super();
         this.node = node;
         this.value = value;
      }

      public String name() {
         return this.node.getName();
      }
   }
}
