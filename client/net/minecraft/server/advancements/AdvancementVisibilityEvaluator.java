package net.minecraft.server.advancements;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;

public class AdvancementVisibilityEvaluator {
   private static final int VISIBILITY_DEPTH = 2;

   public AdvancementVisibilityEvaluator() {
      super();
   }

   private static AdvancementVisibilityEvaluator.VisibilityRule evaluateVisibilityRule(Advancement var0, boolean var1) {
      Optional var2 = var0.display();
      if (var2.isEmpty()) {
         return AdvancementVisibilityEvaluator.VisibilityRule.HIDE;
      } else if (var1) {
         return AdvancementVisibilityEvaluator.VisibilityRule.SHOW;
      } else {
         return ((DisplayInfo)var2.get()).isHidden()
            ? AdvancementVisibilityEvaluator.VisibilityRule.HIDE
            : AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE;
      }
   }

   private static boolean evaluateVisiblityForUnfinishedNode(Stack<AdvancementVisibilityEvaluator.VisibilityRule> var0) {
      for(int var1 = 0; var1 <= 2; ++var1) {
         AdvancementVisibilityEvaluator.VisibilityRule var2 = (AdvancementVisibilityEvaluator.VisibilityRule)var0.peek(var1);
         if (var2 == AdvancementVisibilityEvaluator.VisibilityRule.SHOW) {
            return true;
         }

         if (var2 == AdvancementVisibilityEvaluator.VisibilityRule.HIDE) {
            return false;
         }
      }

      return false;
   }

   private static boolean evaluateVisibility(
      AdvancementNode var0,
      Stack<AdvancementVisibilityEvaluator.VisibilityRule> var1,
      Predicate<AdvancementNode> var2,
      AdvancementVisibilityEvaluator.Output var3
   ) {
      boolean var4 = var2.test(var0);
      AdvancementVisibilityEvaluator.VisibilityRule var5 = evaluateVisibilityRule(var0.advancement(), var4);
      boolean var6 = var4;
      var1.push(var5);

      for(AdvancementNode var8 : var0.children()) {
         var6 |= evaluateVisibility(var8, var1, var2, var3);
      }

      boolean var9 = var6 || evaluateVisiblityForUnfinishedNode(var1);
      var1.pop();
      var3.accept(var0, var9);
      return var6;
   }

   public static void evaluateVisibility(AdvancementNode var0, Predicate<AdvancementNode> var1, AdvancementVisibilityEvaluator.Output var2) {
      AdvancementNode var3 = var0.root();
      ObjectArrayList var4 = new ObjectArrayList();

      for(int var5 = 0; var5 <= 2; ++var5) {
         var4.push(AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
      }

      evaluateVisibility(var3, var4, var1, var2);
   }

   @FunctionalInterface
   public interface Output {
      void accept(AdvancementNode var1, boolean var2);
   }

   static enum VisibilityRule {
      SHOW,
      HIDE,
      NO_CHANGE;

      private VisibilityRule() {
      }
   }
}