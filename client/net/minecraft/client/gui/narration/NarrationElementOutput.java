package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface NarrationElementOutput {
   default void add(NarratedElementType var1, Component var2) {
      this.add(var1, NarrationThunk.from(var2.getString()));
   }

   default void add(NarratedElementType var1, String var2) {
      this.add(var1, NarrationThunk.from(var2));
   }

   default void add(NarratedElementType var1, Component... var2) {
      this.add(var1, NarrationThunk.from((List)ImmutableList.copyOf(var2)));
   }

   void add(NarratedElementType var1, NarrationThunk<?> var2);

   NarrationElementOutput nest();
}
