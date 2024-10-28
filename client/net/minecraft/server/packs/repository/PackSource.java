package net.minecraft.server.packs.repository;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface PackSource {
   UnaryOperator<Component> NO_DECORATION = UnaryOperator.identity();
   PackSource DEFAULT = create(NO_DECORATION, true);
   PackSource BUILT_IN = create(decorateWithSource("pack.source.builtin"), true);
   PackSource FEATURE = create(decorateWithSource("pack.source.feature"), false);
   PackSource WORLD = create(decorateWithSource("pack.source.world"), true);
   PackSource SERVER = create(decorateWithSource("pack.source.server"), true);

   Component decorate(Component var1);

   boolean shouldAddAutomatically();

   static PackSource create(final UnaryOperator<Component> var0, final boolean var1) {
      return new PackSource() {
         public Component decorate(Component var1x) {
            return (Component)var0.apply(var1x);
         }

         public boolean shouldAddAutomatically() {
            return var1;
         }
      };
   }

   private static UnaryOperator<Component> decorateWithSource(String var0) {
      MutableComponent var1 = Component.translatable(var0);
      return (var1x) -> {
         return Component.translatable("pack.nameAndSource", var1x, var1).withStyle(ChatFormatting.GRAY);
      };
   }
}
