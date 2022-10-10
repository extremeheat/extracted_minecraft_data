package net.minecraft.util.math.shapes;

public interface IBooleanFunction {
   IBooleanFunction FALSE = (var0, var1) -> {
      return false;
   };
   IBooleanFunction NOT_OR = (var0, var1) -> {
      return !var0 && !var1;
   };
   IBooleanFunction ONLY_SECOND = (var0, var1) -> {
      return var1 && !var0;
   };
   IBooleanFunction NOT_FIRST = (var0, var1) -> {
      return !var0;
   };
   IBooleanFunction ONLY_FIRST = (var0, var1) -> {
      return var0 && !var1;
   };
   IBooleanFunction NOT_SECOND = (var0, var1) -> {
      return !var1;
   };
   IBooleanFunction NOT_SAME = (var0, var1) -> {
      return var0 != var1;
   };
   IBooleanFunction NOT_AND = (var0, var1) -> {
      return !var0 || !var1;
   };
   IBooleanFunction AND = (var0, var1) -> {
      return var0 && var1;
   };
   IBooleanFunction SAME = (var0, var1) -> {
      return var0 == var1;
   };
   IBooleanFunction SECOND = (var0, var1) -> {
      return var1;
   };
   IBooleanFunction CAUSES = (var0, var1) -> {
      return !var0 || var1;
   };
   IBooleanFunction FIRST = (var0, var1) -> {
      return var0;
   };
   IBooleanFunction CAUSED_BY = (var0, var1) -> {
      return var0 || !var1;
   };
   IBooleanFunction OR = (var0, var1) -> {
      return var0 || var1;
   };
   IBooleanFunction TRUE = (var0, var1) -> {
      return true;
   };

   boolean apply(boolean var1, boolean var2);
}
