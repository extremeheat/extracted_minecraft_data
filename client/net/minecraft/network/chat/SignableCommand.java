package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.SignedArgument;

public record SignableCommand<S>(List<Argument<S>> arguments) {
   public SignableCommand(List<Argument<S>> var1) {
      super();
      this.arguments = var1;
   }

   public static <S> boolean hasSignableArguments(ParseResults<S> var0) {
      return !of(var0).arguments().isEmpty();
   }

   public static <S> SignableCommand<S> of(ParseResults<S> var0) {
      String var1 = var0.getReader().getString();
      CommandContextBuilder var2 = var0.getContext();
      CommandContextBuilder var3 = var2;

      List var4;
      CommandContextBuilder var5;
      for(var4 = collectArguments(var1, var2); (var5 = var3.getChild()) != null && var5.getRootNode() != var2.getRootNode(); var3 = var5) {
         var4.addAll(collectArguments(var1, var5));
      }

      return new SignableCommand(var4);
   }

   private static <S> List<Argument<S>> collectArguments(String var0, CommandContextBuilder<S> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.getNodes().iterator();

      while(var3.hasNext()) {
         ParsedCommandNode var4 = (ParsedCommandNode)var3.next();
         CommandNode var6 = var4.getNode();
         if (var6 instanceof ArgumentCommandNode var5) {
            if (var5.getType() instanceof SignedArgument) {
               ParsedArgument var8 = (ParsedArgument)var1.getArguments().get(var5.getName());
               if (var8 != null) {
                  String var7 = var8.getRange().get(var0);
                  var2.add(new Argument(var5, var7));
               }
            }
         }
      }

      return var2;
   }

   @Nullable
   public Argument<S> getArgument(String var1) {
      Iterator var2 = this.arguments.iterator();

      Argument var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Argument)var2.next();
      } while(!var1.equals(var3.name()));

      return var3;
   }

   public List<Argument<S>> arguments() {
      return this.arguments;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> node, String value) {
      public Argument(ArgumentCommandNode<S, ?> var1, String var2) {
         super();
         this.node = var1;
         this.value = var2;
      }

      public String name() {
         return this.node.getName();
      }

      public ArgumentCommandNode<S, ?> node() {
         return this.node;
      }

      public String value() {
         return this.value;
      }
   }
}
