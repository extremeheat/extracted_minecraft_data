package net.minecraft.util.profiling;

import java.util.function.Supplier;

public interface ProfilerFiller {
   void startTick();

   void endTick();

   void push(String var1);

   void push(Supplier<String> var1);

   void pop();

   void popPush(String var1);

   void popPush(Supplier<String> var1);
}
