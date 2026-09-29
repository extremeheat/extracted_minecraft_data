package net.minecraft.client.gui.components.debug;

import java.util.Collection;
import java.util.function.Consumer;

public interface DebugScreenDisplayer {
   void addPriorityLine(String line);

   void addToGroup(final DebugGroup group, Collection<String> lines);

   void addToGroup(final DebugGroup group, String lines);

   void addFactToGroup(final DebugGroup group, String name, Consumer<DebugFact> builder);

   void addToGroup(final DebugGroup group, DebugCustomRenderer customRenderer);
}
