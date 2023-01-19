package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.arguments.PreviewedArgument;

public record PreviewableCommand<S>(List<PreviewableCommand.Argument<S>> a) {
   private final List<PreviewableCommand.Argument<S>> arguments;

   public PreviewableCommand(List<PreviewableCommand.Argument<S>> var1) {
      super();
      this.arguments = var1;
   }

   public static <S> PreviewableCommand<S> of(ParseResults<S> var0) {
      CommandContextBuilder var1 = var0.getContext();
      CommandContextBuilder var2 = var1;

      List var3;
      CommandContextBuilder var4;
      for(var3 = collectArguments(var1); (var4 = var2.getChild()) != null; var2 = var4) {
         boolean var5 = var4.getRootNode() != var1.getRootNode();
         if (!var5) {
            break;
         }

         var3.addAll(collectArguments(var4));
      }

      return new PreviewableCommand<>(var3);
   }

   private static <S> List<PreviewableCommand.Argument<S>> collectArguments(CommandContextBuilder<S> var0) {
      ArrayList var1 = new ArrayList();

      for(ParsedCommandNode var3 : var0.getNodes()) {
         CommandNode var6 = var3.getNode();
         if (var6 instanceof ArgumentCommandNode var4) {
            ArgumentType var7 = var4.getType();
            if (var7 instanceof PreviewedArgument var5) {
               ParsedArgument var8 = (ParsedArgument)var0.getArguments().get(var4.getName());
               if (var8 != null) {
                  var1.add(new PreviewableCommand.Argument((ArgumentCommandNode<S, ?>)var4, var8, (PreviewedArgument<?>)var5));
               }
            }
         }
      }

      return var1;
   }

   public boolean isPreviewed(CommandNode<?> var1) {
      for(PreviewableCommand.Argument var3 : this.arguments) {
         if (var3.node() == var1) {
            return true;
         }
      }

      return false;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> a, ParsedArgument<S, ?> b, PreviewedArgument<?> c) {
      private final ArgumentCommandNode<S, ?> node;
      private final ParsedArgument<S, ?> parsedValue;
      private final PreviewedArgument<?> previewType;

      public Argument(ArgumentCommandNode<S, ?> var1, ParsedArgument<S, ?> var2, PreviewedArgument<?> var3) {
         super();
         this.node = var1;
         this.parsedValue = var2;
         this.previewType = var3;
      }

      public String name() {
         return this.node.getName();
      }
   }
}
