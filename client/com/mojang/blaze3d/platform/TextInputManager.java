package com.mojang.blaze3d.platform;

import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLKeyboard;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.system.MemoryStack;

public class TextInputManager {
   private final Window window;
   private boolean textInputEnabled;
   private @Nullable Object owner;
   private boolean hasTextInputArea;
   private int areaX;
   private int areaY;
   private int areaWidth;
   private int areaHeight;

   public TextInputManager(final Window window) {
      super();
      this.window = window;
   }

   public void setTextInputArea(final int x0, final int y0, final int x1, final int y1) {
      double windowScale = (double)this.window.getGuiScale() / (double)this.window.getPixelDensity();
      int x = (int)Math.round((double)x0 * windowScale);
      int y = (int)Math.round((double)y0 * windowScale);
      int width = Math.max(1, (int)Math.round((double)(x1 - x0) * windowScale));
      int height = Math.max(1, (int)Math.round((double)(y1 - y0) * windowScale));
      if (!this.hasTextInputArea || x != this.areaX || y != this.areaY || width != this.areaWidth || height != this.areaHeight) {
         this.areaX = x;
         this.areaY = y;
         this.areaWidth = width;
         this.areaHeight = height;
         this.hasTextInputArea = true;
         this.applyTextInputArea();
      }
   }

   private void applyTextInputArea() {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         SDL_Rect.Buffer rect = SDL_Rect.malloc(1, stack).x(this.areaX).y(this.areaY).w(this.areaWidth).h(this.areaHeight);
         SDLKeyboard.SDL_SetTextInputArea(this.window.handle(), rect, -1);
      } catch (Throwable var5) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void startTextInput(final Object owner) {
      this.owner = owner;
      if (!this.textInputEnabled) {
         if (this.hasTextInputArea) {
            this.applyTextInputArea();
         }

         this.textInputEnabled = true;
         SDLKeyboard.SDL_StartTextInput(this.window.handle());
      }

   }

   public void stopTextInput(final Object owner) {
      if (this.owner == owner) {
         this.stopTextInput();
      }

   }

   public void stopTextInput() {
      this.owner = null;
      if (this.textInputEnabled) {
         this.textInputEnabled = false;
         SDLKeyboard.SDL_StopTextInput(this.window.handle());
      }

   }

   public void onTextInputFocusChange(final Object owner, final boolean focused) {
      if (focused) {
         this.startTextInput(owner);
      } else {
         this.stopTextInput(owner);
      }

   }
}
