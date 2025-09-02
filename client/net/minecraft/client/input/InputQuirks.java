package net.minecraft.client.input;

import net.minecraft.Util;

public class InputQuirks {
   private static final boolean ON_OSX;
   public static final boolean REPLACE_CTRL_KEY_WITH_CMD_KEY;
   public static final int EDIT_SHORTCUT_KEY_LEFT;
   public static final int EDIT_SHORTCUT_KEY_RIGHT;
   public static final int EDIT_SHORTCUT_KEY_MODIFIER;
   public static final boolean SIMULATE_RIGHT_CLICK_WITH_LONG_LEFT_CLICK;
   public static final boolean RESTORE_KEY_STATE_AFTER_MOUSE_GRAB;

   public InputQuirks() {
      super();
   }

   static {
      ON_OSX = Util.getPlatform() == Util.OS.OSX;
      REPLACE_CTRL_KEY_WITH_CMD_KEY = ON_OSX;
      EDIT_SHORTCUT_KEY_LEFT = REPLACE_CTRL_KEY_WITH_CMD_KEY ? 343 : 341;
      EDIT_SHORTCUT_KEY_RIGHT = REPLACE_CTRL_KEY_WITH_CMD_KEY ? 347 : 345;
      EDIT_SHORTCUT_KEY_MODIFIER = REPLACE_CTRL_KEY_WITH_CMD_KEY ? 8 : 2;
      SIMULATE_RIGHT_CLICK_WITH_LONG_LEFT_CLICK = ON_OSX;
      RESTORE_KEY_STATE_AFTER_MOUSE_GRAB = !ON_OSX;
   }
}
