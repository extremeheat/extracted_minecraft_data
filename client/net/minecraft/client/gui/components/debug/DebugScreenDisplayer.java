package net.minecraft.client.gui.components.debug;

import java.util.Collection;
import net.minecraft.resources.ResourceLocation;

public interface DebugScreenDisplayer {
   void addPriorityLine(String var1);

   void addLine(String var1);

   void addToGroup(ResourceLocation var1, Collection<String> var2);

   void addToGroup(ResourceLocation var1, String var2);
}
