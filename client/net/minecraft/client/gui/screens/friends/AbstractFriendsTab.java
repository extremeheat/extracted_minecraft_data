package net.minecraft.client.gui.screens.friends;

import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;

public abstract class AbstractFriendsTab implements Tab {
   static final int PADDING_SIDES = 8;
   static final int SPACING = 8;
   protected final int width;
   protected int height;

   public AbstractFriendsTab(final int width, final int height) {
      super();
      this.width = width;
      this.height = height;
   }

   abstract void rearrangeElements();

   protected abstract Layout entriesContainer();

   final void disable() {
      this.entriesContainer().visitWidgets((widget) -> {
         if (widget instanceof AbstractFriendsEntryContainerWidget entry) {
            entry.disable();
         }

      });
   }

   void setHeight(final int height) {
      this.height = height;
      this.rearrangeElements();
   }

   FrameLayout createCenteredFrame(final LayoutElement child, final int frameWidth, final int frameHeight) {
      FrameLayout frameLayout = new FrameLayout(frameWidth, frameHeight);
      frameLayout.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      frameLayout.addChild(child);
      return frameLayout;
   }
}
