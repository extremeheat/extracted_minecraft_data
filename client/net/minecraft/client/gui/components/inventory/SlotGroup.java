package net.minecraft.client.gui.components.inventory;

import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SlotGroup extends AbstractContainerWidget {
   private static final String USAGE_KEY = "narration.group_navigation.usage.slot";
   private final List<SlotWidget> slots;

   public SlotGroup(final Component name, final List<SlotWidget> slots) {
      super(0, 0, 0, 0, name);
      this.slots = slots;
      this.allowTabNavigationInside(false);
      this.updateBounds();
   }

   public void updateX(final int leftPos) {
      this.slots.forEach((slot) -> slot.updateX(leftPos));
      this.updateBounds();
   }

   private void updateBounds() {
      int left = 2147483647;
      int top = 2147483647;
      int right = -2147483648;
      int bottom = -2147483648;

      for(SlotWidget slot : this.slots) {
         left = Math.min(left, slot.getX());
         top = Math.min(top, slot.getY());
         right = Math.max(right, slot.getRight());
         bottom = Math.max(bottom, slot.getBottom());
      }

      this.setPosition(left, top);
      this.setSize(right - left, bottom - top);
   }

   public List<SlotWidget> children() {
      return this.slots;
   }

   protected int contentHeight() {
      return this.height;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      for(SlotWidget slot : this.slots) {
         if (slot.slot().isActive()) {
            slot.extractRenderState(graphics);
         }
      }

   }

   public @Nullable SlotWidget getHoveredSlot() {
      for(SlotWidget slot : this.slots) {
         if (slot.isHovered() && slot.slot().isActive()) {
            return slot;
         }
      }

      return null;
   }

   private @Nullable SlotWidget getFocusedSlot() {
      GuiEventListener var2 = this.getFocused();
      SlotWidget var10000;
      if (var2 instanceof SlotWidget slot) {
         var10000 = slot;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return NarratableEntry.NarrationPriority.of(this.isFocused(), this.getHoveredSlot() != null);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      SlotWidget narrated = (SlotWidget)getNarratedChild(output.narrationTrigger(), this.getHoveredSlot(), this.getFocusedSlot());
      List<SlotWidget> activeSlots = this.getActiveSlots();
      GroupNarration groupNarration = this.getGroupNarration(activeSlots);
      if (narrated != null) {
         narrated.updateNarration(output.nest(), this.getMessage(), groupNarration.createPositionNarration(activeSlots, narrated));
      }

      if (this.isFocused()) {
         groupNarration.narrateUsage(output, activeSlots);
      }

   }

   private GroupNarration getGroupNarration(final List<SlotWidget> activeSlots) {
      IntSortedSet columns = new IntAVLTreeSet();
      IntSortedSet rows = new IntAVLTreeSet();

      for(SlotWidget slot : activeSlots) {
         columns.add(slot.getX());
         rows.add(slot.getY());
      }

      return columns.size() > 1 && rows.size() > 1 && columns.size() * rows.size() == activeSlots.size() ? GroupNarration.grid(columns.size(), "narration.group_navigation.usage.slot") : GroupNarration.list("narrator.position.slot", "narration.group_navigation.usage.slot", getListAxis(columns, rows));
   }

   private static @Nullable ScreenAxis getListAxis(final IntSortedSet columns, final IntSortedSet rows) {
      if (rows.size() == 1) {
         return ScreenAxis.HORIZONTAL;
      } else {
         return columns.size() == 1 ? ScreenAxis.VERTICAL : null;
      }
   }

   private List<SlotWidget> getActiveSlots() {
      List<SlotWidget> activeSlots = new ArrayList();

      for(SlotWidget slot : this.slots) {
         if (slot.slot().isActive()) {
            activeSlots.add(slot);
         }
      }

      return activeSlots;
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      return this.getChildAt(x, y).filter((child) -> child.mouseScrolled(x, y, scrollX, scrollY)).isPresent();
   }
}
