package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface NarrationElementOutput {
   default void add(final NarratedElementType type, final Component contents) {
      this.add(type, NarrationThunk.from(contents.getString()));
   }

   default void add(final NarratedElementType type, final String contents) {
      this.add(type, NarrationThunk.from(contents));
   }

   default void add(final NarratedElementType type, final Component... contents) {
      this.add(type, NarrationThunk.from((List)ImmutableList.copyOf(contents)));
   }

   void add(final NarratedElementType type, final NarrationThunk<?> contents);

   NarrationElementOutput nest();
}
