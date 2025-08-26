package com.mojang.blaze3d.platform.cursor;

public class CursorTypes {
   public static final CursorType ARROW;
   public static final CursorType IBEAM;
   public static final CursorType CROSSHAIR;
   public static final CursorType POINTING_HAND;
   public static final CursorType RESIZE_NS;
   public static final CursorType RESIZE_EW;
   public static final CursorType RESIZE_ALL;
   public static final CursorType NOT_ALLOWED;

   public CursorTypes() {
      super();
   }

   static {
      ARROW = CursorType.createStandardCursor(221185, "arrow", CursorType.DEFAULT);
      IBEAM = CursorType.createStandardCursor(221186, "ibeam", CursorType.DEFAULT);
      CROSSHAIR = CursorType.createStandardCursor(221187, "crosshair", CursorType.DEFAULT);
      POINTING_HAND = CursorType.createStandardCursor(221188, "pointing_hand", CursorType.DEFAULT);
      RESIZE_NS = CursorType.createStandardCursor(221190, "resize_ns", CursorType.DEFAULT);
      RESIZE_EW = CursorType.createStandardCursor(221189, "resize_ew", CursorType.DEFAULT);
      RESIZE_ALL = CursorType.createStandardCursor(221193, "resize_all", CursorType.DEFAULT);
      NOT_ALLOWED = CursorType.createStandardCursor(221194, "not_allowed", CursorType.DEFAULT);
   }
}
