package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.inventory.SlotGroup;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   public static final Identifier INVENTORY_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");
   public static final int EQUIPMENT_TAB_GROUP = 1;
   public static final int CONTAINER_TAB_GROUP = 2;
   public static final int BEFORE_INPUT_TAB_GROUP = 3;
   public static final int INPUT_TAB_GROUP = 4;
   public static final int SECOND_INPUT_TAB_GROUP = 5;
   public static final int FUEL_TAB_GROUP = 6;
   public static final int BETWEEN_INPUT_OUTPUT_TAB_GROUP = 7;
   public static final int RESULT_TAB_GROUP = 8;
   public static final int INVENTORY_TAB_GROUP = 9;
   public static final int HOTBAR_TAB_GROUP = 10;
   private static final Component EQUIPMENT_GROUP_NAME = Component.translatable("narration.slot_group.equipment");
   private static final Component INPUT_GROUP_NAME = Component.translatable("narration.slot_group.input");
   private static final Component SECOND_INPUT_GROUP_NAME = Component.translatable("narration.slot_group.second_input");
   private static final Component FUEL_GROUP_NAME = Component.translatable("narration.slot_group.fuel");
   private static final Component RESULT_GROUP_NAME = Component.translatable("narration.slot_group.result");
   private static final Component INVENTORY_GROUP_NAME = Component.translatable("narration.slot_group.inventory");
   private static final Component HOTBAR_GROUP_NAME = Component.translatable("narration.slot_group.hotbar");
   protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
   protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   protected static final int DEFAULT_IMAGE_WIDTH = 176;
   protected static final int DEFAULT_IMAGE_HEIGHT = 166;
   private static final long DOUBLE_KEY_PRESS_THRESHOLD_MS = 250L;
   private static final int KEYBOARD_CARRIED_ITEM_OFFSET = 2;
   private static final float KEYBOARD_CARRIED_ITEM_SMOOTHING_MS = 50.0F;
   protected final int imageWidth;
   protected final int imageHeight;
   protected int titleLabelX;
   protected int titleLabelY;
   protected int inventoryLabelX;
   protected int inventoryLabelY;
   protected final T menu;
   protected final Component playerInventoryTitle;
   private final List<SlotGroup> slotGroups;
   private @Nullable SlotWidget lastClickSlotWidget;
   private @Nullable SlotWidget lastKeyPressedSlotWidget;
   protected int leftPos;
   protected int topPos;
   protected final Set<SlotWidget> quickCraftSlots;
   private boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private boolean doubleclick;
   private boolean doubleKeyPress;
   private long lastKeyPressTime;
   private @InputConstants.Value int lastKeyPressed;
   private ItemStack lastQuickMoved;
   private boolean carriedItemAnimStarted;
   private float carriedItemAnimX;
   private float carriedItemAnimY;
   private long carriedItemAnimTime;

   public AbstractContainerScreen(final T menu, final Inventory inventory, final Component title) {
      this(menu, inventory, title, 176, 166);
   }

   public AbstractContainerScreen(final T menu, final Inventory inventory, final Component title, final int imageWidth, final int imageHeight) {
      super(title);
      this.slotGroups = new ArrayList();
      this.quickCraftSlots = new HashSet();
      this.lastQuickMoved = ItemStack.EMPTY;
      this.menu = menu;
      this.playerInventoryTitle = inventory.getDisplayName();
      this.imageWidth = imageWidth;
      this.imageHeight = imageHeight;
      this.skipNextRelease = true;
      this.titleLabelX = 8;
      this.titleLabelY = 6;
      this.inventoryLabelX = 8;
      this.inventoryLabelY = imageHeight - 94;
   }

   private boolean isQuickCrafting() {
      return this.isQuickCrafting;
   }

   private void setQuickCrafting(final boolean isQuickCrafting) {
      this.isQuickCrafting = isQuickCrafting;
   }

   protected void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.updateSlotWidgets();
   }

   protected void updateSlotWidgets() {
      List<SlotWidget> widgets = new ArrayList();

      for(Slot slot : this.menu.slots) {
         widgets.add(new SlotWidget(slot, this.minecraft, this.imageWidth, this.leftPos, this.topPos));
      }

      this.setSlotWidgets(widgets);
   }

   protected final void setSlotWidgets(final List<SlotWidget> widgets) {
      this.slotGroups.forEach((x$0) -> this.removeWidget(x$0));
      this.slotGroups.clear();
      Map<Slot.Section, List<SlotWidget>> slotsBySection = new EnumMap(Slot.Section.class);

      for(SlotWidget widget : widgets) {
         ((List)slotsBySection.computeIfAbsent(widget.slot().getSection(), (var0) -> new ArrayList())).add(widget);
      }

      for(Slot.Section section : slotsBySection.keySet()) {
         List<SlotWidget> sectionSlots = (List)slotsBySection.get(section);
         SlotGroup group = (SlotGroup)this.addWidget(new SlotGroup(this.getSlotGroupName(section), sectionSlots));
         group.setTabOrderGroup(this.getTabOrderGroup(section));
         this.slotGroups.add(group);
      }

   }

   private Component getSlotGroupName(final Slot.Section section) {
      Component var10000;
      switch (section) {
         case EQUIPMENT -> var10000 = EQUIPMENT_GROUP_NAME;
         case CONTAINER -> var10000 = this.getTitle();
         case INPUT -> var10000 = INPUT_GROUP_NAME;
         case SECOND_INPUT -> var10000 = SECOND_INPUT_GROUP_NAME;
         case FUEL -> var10000 = FUEL_GROUP_NAME;
         case RESULT -> var10000 = RESULT_GROUP_NAME;
         case INVENTORY -> var10000 = INVENTORY_GROUP_NAME;
         case HOTBAR -> var10000 = HOTBAR_GROUP_NAME;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   protected int getTabOrderGroup(final Slot.Section section) {
      byte var10000;
      switch (section) {
         case EQUIPMENT -> var10000 = 1;
         case CONTAINER -> var10000 = 2;
         case INPUT -> var10000 = 4;
         case SECOND_INPUT -> var10000 = 5;
         case FUEL -> var10000 = 6;
         case RESULT -> var10000 = 8;
         case INVENTORY -> var10000 = 9;
         case HOTBAR -> var10000 = 10;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
      this.extractContents(graphics);
      this.extractCarriedItem(graphics);
   }

   public void extractContents(final GuiGraphicsExtractor graphics) {
      int xo = this.leftPos;
      int yo = this.topPos;
      super.extractRenderState(graphics);
      graphics.pose().pushMatrix();
      graphics.pose().translate((float)xo, (float)yo);
      this.extractLabels(graphics);
      graphics.pose().popMatrix();
      this.extractSlots(graphics);
   }

   public void updateScreenPositions() {
      this.slotGroups.forEach((group) -> group.updateX(this.leftPos));
   }

   public void extractCarriedItem(final GuiGraphicsExtractor graphics) {
      ItemStack carried = this.menu.getCarried();
      if (!carried.isEmpty()) {
         int xOffset = 8;
         int yOffset = 8;
         if (this.isQuickCrafting() && this.quickCraftSlots.size() > 1) {
            carried = carried.copyWithCount(this.quickCraftingRemainder);
         }

         int x;
         int y;
         label27: {
            graphics.nextStratum();
            ComponentPath currentFocusPath = this.getCurrentFocusPath();
            if (this.minecraft.getLastInputType().isKeyboard() && currentFocusPath != null) {
               GuiEventListener var9 = currentFocusPath.leafComponent();
               if (var9 instanceof AbstractWidget) {
                  AbstractWidget widget = (AbstractWidget)var9;
                  float targetX = (float)(widget.getX() + 2);
                  float targetY = (float)(widget.getY() - 2);
                  long now = Util.getMillis();
                  if (this.carriedItemAnimStarted) {
                     float lerpFactor = 1.0F - (float)Math.exp((double)((float)(-(now - this.carriedItemAnimTime)) / 50.0F));
                     this.carriedItemAnimX = Mth.lerp(lerpFactor, this.carriedItemAnimX, targetX);
                     this.carriedItemAnimY = Mth.lerp(lerpFactor, this.carriedItemAnimY, targetY);
                  } else {
                     this.carriedItemAnimX = targetX;
                     this.carriedItemAnimY = targetY;
                     this.carriedItemAnimStarted = true;
                  }

                  this.carriedItemAnimTime = now;
                  x = Math.round(this.carriedItemAnimX);
                  y = Math.round(this.carriedItemAnimY);
                  break label27;
               }
            }

            this.carriedItemAnimStarted = false;
            x = graphics.mouseX() - 8;
            y = graphics.mouseY() - 8;
         }

         this.extractFloatingItem(graphics, carried, x, y);
      } else {
         this.carriedItemAnimStarted = false;
      }

   }

   protected void extractSlots(final GuiGraphicsExtractor graphics) {
      for(SlotGroup group : this.slotGroups) {
         for(SlotWidget slotWidget : group.children()) {
            if (slotWidget.slot().isActive()) {
               this.updateQuickCrafting(slotWidget);
            }
         }

         group.extractRenderState(graphics);
      }

   }

   private void extractFloatingItem(final GuiGraphicsExtractor graphics, final ItemStack carried, final int x, final int y) {
      graphics.item(carried, x, y);
      graphics.itemDecorations(this.font, carried, x, y);
   }

   protected void extractLabels(final GuiGraphicsExtractor graphics) {
      graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
      graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
   }

   private void updateQuickCrafting(final SlotWidget slotWidget) {
      Slot slot = slotWidget.slot();
      ItemStack carried = this.menu.getCarried();
      if (this.isQuickCrafting() && this.quickCraftSlots.size() > 1 && this.quickCraftSlots.contains(slotWidget) && !carried.isEmpty()) {
         if (AbstractContainerMenu.canItemQuickReplace(slot, carried, true) && this.menu.canDragTo(slot)) {
            int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
            int carry = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
            int newCount = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(), this.quickCraftingType, carried) + carry;
            String overrideItemCount = null;
            if (newCount > maxSize) {
               newCount = maxSize;
               String var10000 = ChatFormatting.YELLOW.toString();
               overrideItemCount = var10000 + maxSize;
            }

            slotWidget.overrideItemToRender(carried.copyWithCount(newCount), overrideItemCount);
            return;
         }

         this.quickCraftSlots.remove(slotWidget);
         this.recalculateQuickCraftRemaining();
      }

      slotWidget.overrideItemToRender((ItemStack)null, (String)null);
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack carried = this.menu.getCarried();
      if (!carried.isEmpty() && this.isQuickCrafting()) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = carried.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = carried.getCount();

            for(SlotWidget slotWidget : this.quickCraftSlots) {
               Slot slot = slotWidget.slot();
               ItemStack slotItemStack = slot.getItem();
               int carry = slotItemStack.isEmpty() ? 0 : slotItemStack.getCount();
               int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
               int newCount = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(), this.quickCraftingType, carried) + carry, maxSize);
               this.quickCraftingRemainder -= newCount - carry;
            }

         }
      }
   }

   protected @Nullable SlotWidget getHoveredSlot() {
      double mouseX = this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
      double mouseY = this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());

      for(SlotGroup group : this.slotGroups) {
         for(SlotWidget slotWidget : group.children()) {
            if (slotWidget.isMouseOver(mouseX, mouseY)) {
               return slotWidget;
            }
         }
      }

      return null;
   }

   private static int getContainerClickButton(final MouseButtonEvent event) {
      int var10000;
      switch (event.button()) {
         case 1 -> var10000 = 0;
         case 3 -> var10000 = 1;
         default -> var10000 = event.button();
      }

      return var10000;
   }

   private static int getContainerClickButton(final KeyEvent event) {
      return event.hasControlDown() ? 1 : 0;
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      boolean mouseClickedResult = super.mouseClicked(event, doubleClick);
      boolean cloning = this.minecraft.options.keyPickItem.matchesMouse(event) && this.minecraft.player.hasInfiniteMaterials();
      SlotWidget slotWidget = this.getHoveredSlot();
      this.doubleclick = this.lastClickSlotWidget == slotWidget && doubleClick;
      this.lastClickSlotWidget = slotWidget;
      this.skipNextRelease = false;
      if (!event.isLeftMouse() && !event.isRightMouse() && !cloning) {
         return this.checkHotbarSwap((keyMapping) -> keyMapping.matchesMouse(event)) || mouseClickedResult;
      } else if (this.isQuickCrafting()) {
         return true;
      } else {
         int slotId;
         if (this.hasClickedOutside(event.x(), event.y())) {
            slotId = -999;
         } else {
            if (slotWidget == null) {
               return true;
            }

            slotId = slotWidget.slot().index;
         }

         if (this.menu.getCarried().isEmpty()) {
            this.tryPickUpItem(slotWidget, slotId, getContainerClickButton(event), cloning, event.hasShiftDown());
         } else {
            this.setupQuickCrafting(event.isLeftMouse(), event.isRightMouse(), cloning, getContainerClickButton(event));
         }

         return true;
      }
   }

   private void tryPickUpItem(final @Nullable SlotWidget slotWidget, final int slotId, final int button, final boolean cloning, final boolean hasShiftDown) {
      ContainerInput containerInput;
      if (cloning) {
         containerInput = ContainerInput.CLONE;
      } else if (slotId == -999) {
         containerInput = ContainerInput.THROW;
      } else if (hasShiftDown) {
         containerInput = ContainerInput.QUICK_MOVE;
      } else {
         containerInput = ContainerInput.PICKUP;
      }

      this.slotClicked(slotWidget, slotId, button, containerInput);
      this.skipNextRelease = true;
   }

   private void setupQuickCrafting(final boolean shouldBeCharitable, final boolean shouldBeGreedy, final boolean cloning, final int button) {
      this.setQuickCrafting(true);
      this.quickCraftingButton = button;
      this.quickCraftSlots.clear();
      if (shouldBeGreedy) {
         this.quickCraftingType = 1;
      } else if (cloning) {
         this.quickCraftingType = 2;
      } else if (shouldBeCharitable) {
         this.quickCraftingType = 0;
      }

   }

   private boolean checkHotbarSwap(final Predicate<KeyMapping> matches) {
      if (!this.menu.getCarried().isEmpty()) {
         return false;
      } else {
         SlotWidget hoveredSlotWidget = this.getSlotWidgetToMove();
         if (hoveredSlotWidget == null) {
            return false;
         } else {
            Slot hoveredSlot = hoveredSlotWidget.slot();
            if (matches.test(this.minecraft.options.keySwapOffhand)) {
               this.slotClicked(hoveredSlotWidget, hoveredSlot.index, 40, ContainerInput.SWAP);
               return true;
            } else {
               for(int i = 0; i < 9; ++i) {
                  if (matches.test(this.minecraft.options.keyHotbarSlots[i])) {
                     this.slotClicked(hoveredSlotWidget, hoveredSlot.index, i, ContainerInput.SWAP);
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   private @Nullable SlotWidget getSlotWidgetToMove() {
      SlotWidget hoveredSlot = this.getHoveredSlot();
      return hoveredSlot != null ? hoveredSlot : this.getFocusedSlotWidget();
   }

   protected boolean hasClickedOutside(final double mx, final double my) {
      return mx < (double)this.leftPos || my < (double)this.topPos || mx >= (double)(this.leftPos + this.imageWidth) || my >= (double)(this.topPos + this.imageHeight);
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      SlotWidget slotWidget = this.getHoveredSlot();
      ItemStack carried = this.menu.getCarried();
      if (slotWidget != null && this.shouldAddSlotToQuickCraft(slotWidget, carried, this.doubleclick) && this.quickCraftSlots.add(slotWidget)) {
         this.recalculateQuickCraftRemaining();
         return true;
      } else {
         return slotWidget == null && this.menu.getCarried().isEmpty() ? super.mouseDragged(event, dx, dy) : true;
      }
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      SlotWidget slotWidget = this.getHoveredSlot();
      int slotId;
      if (this.hasClickedOutside(event.x(), event.y())) {
         slotId = -999;
      } else if (slotWidget != null) {
         slotId = slotWidget.slot().index;
      } else {
         slotId = -1;
      }

      if (this.handleDoublePress(slotWidget, this.doubleclick, event.hasShiftDown(), getContainerClickButton(event))) {
         this.doubleclick = false;
         this.setQuickCrafting(false);
         return super.mouseReleased(event);
      } else if (this.isQuickCrafting() && this.quickCraftingButton != getContainerClickButton(event)) {
         this.setQuickCrafting(false);
         this.quickCraftSlots.clear();
         this.skipNextRelease = true;
         return true;
      } else if (this.skipNextRelease) {
         this.skipNextRelease = false;
         return true;
      } else {
         if (this.isQuickCrafting() && !this.quickCraftSlots.isEmpty()) {
            this.quickCraftToSlots();
         } else if (!this.menu.getCarried().isEmpty()) {
            this.releaseCarriedItem(slotWidget, slotId, getContainerClickButton(event), this.minecraft.options.keyPickItem.matchesMouse(event), event.hasShiftDown());
         }

         this.setQuickCrafting(false);
         return super.mouseReleased(event);
      }
   }

   private boolean handleDoublePress(final @Nullable SlotWidget slotWidget, final boolean isDoublePress, final boolean hasShiftDown, final int button) {
      if (slotWidget != null && isDoublePress && button == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slotWidget.slot())) {
         int slotId = slotWidget.slot().index;
         if (!hasShiftDown) {
            this.slotClicked(slotWidget, slotId, button, ContainerInput.PICKUP_ALL);
         } else if (!this.lastQuickMoved.isEmpty()) {
            for(SlotGroup group : this.slotGroups) {
               for(SlotWidget targetWidget : group.children()) {
                  Slot target = targetWidget.slot();
                  if (target.mayPickup(this.minecraft.player) && target.hasItem() && target.container == slotWidget.slot().container && AbstractContainerMenu.canItemQuickReplace(target, this.lastQuickMoved, true)) {
                     this.slotClicked(targetWidget, target.index, button, ContainerInput.QUICK_MOVE);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void releaseCarriedItem(final @Nullable SlotWidget slotWidget, final int slotId, final int button, final boolean cloning, final boolean hasShiftDown) {
      ContainerInput containerInput;
      if (cloning) {
         containerInput = ContainerInput.CLONE;
      } else if (slotId != -999 && hasShiftDown) {
         containerInput = ContainerInput.QUICK_MOVE;
      } else {
         containerInput = ContainerInput.PICKUP;
      }

      this.slotClicked(slotWidget, slotId, button, containerInput);
   }

   protected boolean isHovering(final int left, final int top, final int w, final int h, double xm, double ym) {
      int xo = this.leftPos;
      int yo = this.topPos;
      xm -= (double)xo;
      ym -= (double)yo;
      return xm >= (double)(left - 1) && xm < (double)(left + w + 1) && ym >= (double)(top - 1) && ym < (double)(top + h + 1);
   }

   protected void slotClicked(final @Nullable SlotWidget slotWidget, int slotId, final int buttonNum, final ContainerInput containerInput) {
      if (slotWidget != null) {
         Slot slot = slotWidget.slot();
         slotId = slot.index;
         slotWidget.clickSlot(containerInput);
         if (containerInput == ContainerInput.QUICK_MOVE) {
            this.updateLastQuickMoved(slot);
         }
      }

      this.minecraft.gameMode.handleContainerInput(this.menu.containerId, slotId, buttonNum, containerInput, this.minecraft.player);
   }

   protected void updateLastQuickMoved(final @Nullable Slot slot) {
      this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
   }

   protected void handleSlotStateChanged(final int slotId, final int containerId, final boolean newState) {
      this.minecraft.gameMode.handleSlotStateChanged(slotId, containerId, newState);
   }

   public boolean keyPressed(final KeyEvent event) {
      if (this.isInputCaptured()) {
         return super.keyPressed(event);
      } else {
         boolean keyPressedResult = super.keyPressed(event);
         if (this.minecraft.options.keyInventory.matches(event)) {
            this.onClose();
            return true;
         } else {
            this.checkHotbarKeyPressed(event);
            boolean selectionPressed = event.isSelection() && !event.repeat();
            if (selectionPressed) {
               this.skipNextRelease = false;
            }

            SlotWidget focusedSlotWidget = this.getFocusedSlotWidget();
            if (!event.repeat()) {
               this.doubleKeyPress = this.lastKeyPressedSlotWidget == focusedSlotWidget && Util.getMillis() - this.lastKeyPressTime < 250L && this.lastKeyPressed == event.key();
               this.lastKeyPressTime = Util.getMillis();
               this.lastKeyPressedSlotWidget = focusedSlotWidget;
               this.lastKeyPressed = event.key();
            }

            SlotWidget slotWidget = null;
            if (focusedSlotWidget != null) {
               if (selectionPressed && !this.isQuickCrafting()) {
                  boolean cloning = event.hasAltDown() && this.minecraft.player.hasInfiniteMaterials();
                  if (this.menu.getCarried().isEmpty()) {
                     this.tryPickUpItem(focusedSlotWidget, focusedSlotWidget.slot().index, getContainerClickButton(event), cloning, event.hasShiftDown());
                  } else {
                     this.setupQuickCrafting(!event.hasControlDown(), event.hasControlDown(), cloning, getContainerClickButton(event));
                  }
               }

               if (this.shouldAddSlotToQuickCraft(focusedSlotWidget, this.menu.getCarried(), this.doubleKeyPress) && this.quickCraftSlots.add(focusedSlotWidget)) {
                  this.recalculateQuickCraftRemaining();
               }

               slotWidget = focusedSlotWidget;
            }

            SlotWidget hoveredSlotWidget = this.getHoveredSlot();
            if (hoveredSlotWidget != null && hoveredSlotWidget.slot().hasItem()) {
               slotWidget = hoveredSlotWidget;
            }

            if (slotWidget != null) {
               if (this.minecraft.options.keyPickItem.matches(event) && this.minecraft.player.hasInfiniteMaterials()) {
                  this.slotClicked(slotWidget, slotWidget.slot().index, 0, ContainerInput.CLONE);
               } else if (this.minecraft.options.keyDrop.matches(event)) {
                  this.slotClicked(slotWidget, slotWidget.slot().index, getContainerClickButton(event), ContainerInput.THROW);
               }
            }

            return keyPressedResult;
         }
      }
   }

   public boolean keyReleased(final KeyEvent event) {
      if (!event.isSelection()) {
         return super.keyReleased(event);
      } else {
         SlotWidget slotWidget = this.getFocusedSlotWidget();
         if (this.handleDoublePress(slotWidget, this.doubleKeyPress, event.hasShiftDown(), getContainerClickButton(event))) {
            this.doubleKeyPress = false;
            this.setQuickCrafting(false);
            return super.keyReleased(event);
         } else if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         } else {
            if (this.isQuickCrafting() && !this.quickCraftSlots.isEmpty()) {
               this.quickCraftToSlots();
            } else if (slotWidget != null && !this.menu.getCarried().isEmpty()) {
               this.slotClicked(slotWidget, slotWidget.slot().index, getContainerClickButton(event), event.hasShiftDown() ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP);
            }

            this.setQuickCrafting(false);
            return super.keyReleased(event);
         }
      }
   }

   protected boolean checkHotbarKeyPressed(final KeyEvent event) {
      return this.checkHotbarSwap((keyMapping) -> keyMapping.matches(event));
   }

   private @Nullable SlotWidget getFocusedSlotWidget() {
      if (!this.minecraft.getLastInputType().isKeyboard()) {
         return null;
      } else {
         ComponentPath currentFocusPath = this.getCurrentFocusPath();
         if (currentFocusPath != null) {
            GuiEventListener var3 = currentFocusPath.leafComponent();
            if (var3 instanceof SlotWidget) {
               SlotWidget focusedSlotWidget = (SlotWidget)var3;
               return focusedSlotWidget;
            }
         }

         return null;
      }
   }

   public void removed() {
      if (this.minecraft.player != null) {
         this.menu.removed(this.minecraft.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isInGameUi() {
      return true;
   }

   public final void tick() {
      super.tick();
      if (this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
         this.containerTick();
      } else {
         this.minecraft.player.closeContainer();
      }

   }

   protected void containerTick() {
   }

   private boolean shouldAddSlotToQuickCraft(final SlotWidget slotWidget, final ItemStack carried, final boolean doublePress) {
      return this.isQuickCrafting() && !carried.isEmpty() && (carried.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slotWidget.slot(), carried, true) && slotWidget.slot().mayPlace(carried) && this.menu.canDragTo(slotWidget.slot()) && !doublePress;
   }

   private void quickCraftToSlots() {
      this.slotClicked((SlotWidget)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ContainerInput.QUICK_CRAFT);

      for(SlotWidget quickSlotWidget : this.quickCraftSlots) {
         this.slotClicked(quickSlotWidget, quickSlotWidget.slot().index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ContainerInput.QUICK_CRAFT);
      }

      this.slotClicked((SlotWidget)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ContainerInput.QUICK_CRAFT);
      this.quickCraftSlots.clear();
   }

   public T getMenu() {
      return this.menu;
   }

   public void onClose() {
      SlotWidget hoveredSlotWidget = this.getHoveredSlot();
      if (hoveredSlotWidget != null) {
         hoveredSlotWidget.onStopHovering();
      }

      this.minecraft.player.closeContainer();
      super.onClose();
   }
}
