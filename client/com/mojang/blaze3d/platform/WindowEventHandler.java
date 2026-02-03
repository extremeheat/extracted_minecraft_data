package com.mojang.blaze3d.platform;

public interface WindowEventHandler {
   void setWindowActive(final boolean windowActive);

   void resizeDisplay();

   void cursorEntered();
}
