package net.minecraft.world.level.gamerules;

public interface GameRuleTypeVisitor {
   default <T> void visit(GameRule<T> var1) {
   }

   default void visitBoolean(GameRule<Boolean> var1) {
   }

   default void visitInteger(GameRule<Integer> var1) {
   }
}
