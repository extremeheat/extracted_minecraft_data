package net.minecraft.realms;

import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RealmListEntry extends ObjectSelectionList.Entry {
   public abstract void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9);

   public boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }
}
