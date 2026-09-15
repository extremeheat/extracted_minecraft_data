package net.minecraft.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface InputWithModifiers {
   @InputConstants.Value int input();

   default int shortcutKey() {
      return this.input();
   }

   @InputWithModifiers.Modifiers int modifiers();

   default boolean isSelection() {
      return this.input() == 40 || this.input() == 44 || this.input() == 88;
   }

   default boolean isConfirmation() {
      return this.input() == 40 || this.input() == 88;
   }

   default boolean isEscape() {
      return this.input() == 41;
   }

   default boolean isLeft() {
      return this.input() == 80;
   }

   default boolean isRight() {
      return this.input() == 79;
   }

   default boolean isUp() {
      return this.input() == 82;
   }

   default boolean isDown() {
      return this.input() == 81;
   }

   default boolean isCycleFocus() {
      return this.input() == 43;
   }

   default boolean hasAltDown() {
      return (this.modifiers() & 768) != 0;
   }

   default boolean hasShiftDown() {
      return (this.modifiers() & 3) != 0;
   }

   default boolean hasControlDown() {
      return (this.modifiers() & 192) != 0;
   }

   default boolean hasControlDownWithQuirk() {
      return (this.modifiers() & InputQuirks.EDIT_SHORTCUT_KEY_MODIFIER) != 0;
   }

   default boolean isSelectAll() {
      return this.shortcutKey() == 97 && this.hasControlDownWithQuirk() && !this.hasShiftDown() && !this.hasAltDown();
   }

   default boolean isCopy() {
      return this.shortcutKey() == 99 && this.hasControlDownWithQuirk() && !this.hasShiftDown() && !this.hasAltDown();
   }

   default boolean isPaste() {
      return this.shortcutKey() == 118 && this.hasControlDownWithQuirk() && !this.hasShiftDown() && !this.hasAltDown();
   }

   default boolean isCut() {
      return this.shortcutKey() == 120 && this.hasControlDownWithQuirk() && !this.hasShiftDown() && !this.hasAltDown();
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Modifiers {
   }
}
