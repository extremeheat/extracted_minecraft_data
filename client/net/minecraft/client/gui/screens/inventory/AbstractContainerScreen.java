package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   public static final Identifier INVENTORY_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");
   private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
   private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");
   protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
   protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final float SNAPBACK_SPEED = 100.0F;
   private static final int QUICKDROP_DELAY = 500;
   protected static final int DEFAULT_IMAGE_WIDTH = 176;
   protected static final int DEFAULT_IMAGE_HEIGHT = 166;
   protected final int imageWidth;
   protected final int imageHeight;
   protected int titleLabelX;
   protected int titleLabelY;
   protected int inventoryLabelX;
   protected int inventoryLabelY;
   private final List<ItemSlotMouseAction> itemSlotMouseActions;
   protected final T menu;
   protected final Component playerInventoryTitle;
   protected @Nullable Slot hoveredSlot;
   private @Nullable Slot clickedSlot;
   private @Nullable Slot quickdropSlot;
   private @Nullable Slot lastClickSlot;
   private @Nullable SnapbackData snapbackData;
   protected int leftPos;
   protected int topPos;
   private boolean isSplittingStack;
   private ItemStack draggingItem;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots;
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private @MouseButtonInfo.MouseButton int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private boolean doubleclick;
   private ItemStack lastQuickMoved;

   public AbstractContainerScreen(final T menu, final Inventory inventory, final Component title) {
      this(menu, inventory, title, 176, 166);
   }

   public AbstractContainerScreen(final T menu, final Inventory inventory, final Component title, final int imageWidth, final int imageHeight) {
      super(title);
      this.draggingItem = ItemStack.EMPTY;
      this.quickCraftSlots = Sets.newHashSet();
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
      this.itemSlotMouseActions = new ArrayList();
   }

   protected void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.itemSlotMouseActions.clear();
      this.addItemSlotMouseAction(new BundleMouseActions(this.minecraft));
   }

   protected void addItemSlotMouseAction(final ItemSlotMouseAction itemSlotMouseAction) {
      this.itemSlotMouseActions.add(itemSlotMouseAction);
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      this.renderContents(graphics, mouseX, mouseY, a);
      this.renderCarriedItem(graphics, mouseX, mouseY);
      this.renderSnapbackItem(graphics);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   public void renderContents(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      int xo = this.leftPos;
      int yo = this.topPos;
      super.render(graphics, mouseX, mouseY, a);
      graphics.pose().pushMatrix();
      graphics.pose().translate((float)xo, (float)yo);
      this.renderLabels(graphics, mouseX, mouseY);
      Slot previouslyHoveredSlot = this.hoveredSlot;
      this.hoveredSlot = this.getHoveredSlot((double)mouseX, (double)mouseY);
      this.renderSlotHighlightBack(graphics);
      this.renderSlots(graphics, mouseX, mouseY);
      this.renderSlotHighlightFront(graphics);
      if (previouslyHoveredSlot != null && previouslyHoveredSlot != this.hoveredSlot) {
         this.onStopHovering(previouslyHoveredSlot);
      }

      graphics.pose().popMatrix();
   }

   public void renderCarriedItem(final GuiGraphics graphics, final int mouseX, final int mouseY) {
      ItemStack carried = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
      if (!carried.isEmpty()) {
         int xOffset = 8;
         int yOffset = this.draggingItem.isEmpty() ? 8 : 16;
         String itemCount = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            carried = carried.copyWithCount(Mth.ceil((float)carried.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            carried = carried.copyWithCount(this.quickCraftingRemainder);
            if (carried.isEmpty()) {
               itemCount = String.valueOf(ChatFormatting.YELLOW) + "0";
            }
         }

         graphics.nextStratum();
         this.renderFloatingItem(graphics, carried, mouseX - 8, mouseY - yOffset, itemCount);
      }

   }

   public void renderSnapbackItem(final GuiGraphics graphics) {
      if (this.snapbackData != null) {
         float snapbackProgress = Mth.clamp((float)(Util.getMillis() - this.snapbackData.time) / 100.0F, 0.0F, 1.0F);
         int xd = this.snapbackData.end.x - this.snapbackData.start.x;
         int yd = this.snapbackData.end.y - this.snapbackData.start.y;
         int x = this.snapbackData.start.x + (int)((float)xd * snapbackProgress);
         int y = this.snapbackData.start.y + (int)((float)yd * snapbackProgress);
         graphics.nextStratum();
         this.renderFloatingItem(graphics, this.snapbackData.item, x, y, (String)null);
         if (snapbackProgress >= 1.0F) {
            this.snapbackData = null;
         }
      }

   }

   protected void renderSlots(final GuiGraphics graphics, final int mouseX, final int mouseY) {
      for(Slot slot : this.menu.slots) {
         if (slot.isActive()) {
            this.renderSlot(graphics, slot, mouseX, mouseY);
         }
      }

   }

   public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.renderBackground(graphics, mouseX, mouseY, a);
      this.renderBg(graphics, a, mouseX, mouseY);
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         for(ItemSlotMouseAction itemMouseAction : this.itemSlotMouseActions) {
            if (itemMouseAction.matches(this.hoveredSlot) && itemMouseAction.onMouseScrolled(scrollX, scrollY, this.hoveredSlot.index, this.hoveredSlot.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   private void renderSlotHighlightBack(final GuiGraphics graphics) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   private void renderSlotHighlightFront(final GuiGraphics graphics) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   protected void renderTooltip(final GuiGraphics graphics, final int mouseX, final int mouseY) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         ItemStack item = this.hoveredSlot.getItem();
         if (this.menu.getCarried().isEmpty() || this.showTooltipWithItemInHand(item)) {
            graphics.setTooltipForNextFrame(this.font, this.getTooltipFromContainerItem(item), item.getTooltipImage(), mouseX, mouseY, (Identifier)item.get(DataComponents.TOOLTIP_STYLE));
         }

      }
   }

   private boolean showTooltipWithItemInHand(final ItemStack item) {
      return (Boolean)item.getTooltipImage().map(ClientTooltipComponent::create).map(ClientTooltipComponent::showTooltipWithItemInHand).orElse(false);
   }

   protected List<Component> getTooltipFromContainerItem(final ItemStack itemStack) {
      return getTooltipFromItem(this.minecraft, itemStack);
   }

   private void renderFloatingItem(final GuiGraphics graphics, final ItemStack carried, final int x, final int y, final @Nullable String itemCount) {
      graphics.renderItem(carried, x, y);
      graphics.renderItemDecorations(this.font, carried, x, y - (this.draggingItem.isEmpty() ? 0 : 8), itemCount);
   }

   protected void renderLabels(final GuiGraphics graphics, final int xm, final int ym) {
      graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
   }

   protected abstract void renderBg(final GuiGraphics graphics, float a, int xm, int ym);

   protected void renderSlot(final GuiGraphics graphics, final Slot slot, final int mouseX, final int mouseY) {
      int x = slot.x;
      int y = slot.y;
      ItemStack itemStack = slot.getItem();
      boolean quickCraftStack = false;
      boolean done = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack carried = this.menu.getCarried();
      String itemCount = null;
      if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
         itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !carried.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (AbstractContainerMenu.canItemQuickReplace(slot, carried, true) && this.menu.canDragTo(slot)) {
            quickCraftStack = true;
            int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
            int carry = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
            int newCount = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(), this.quickCraftingType, carried) + carry;
            if (newCount > maxSize) {
               newCount = maxSize;
               String var10000 = ChatFormatting.YELLOW.toString();
               itemCount = var10000 + maxSize;
            }

            itemStack = carried.copyWithCount(newCount);
         } else {
            this.quickCraftSlots.remove(slot);
            this.recalculateQuickCraftRemaining();
         }
      }

      if (itemStack.isEmpty() && slot.isActive()) {
         Identifier icon = slot.getNoItemIcon();
         if (icon != null) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)icon, x, y, 16, 16);
            done = true;
         }
      }

      if (!done) {
         if (quickCraftStack) {
            graphics.fill(x, y, x + 16, y + 16, -2130706433);
         }

         int seed = slot.x + slot.y * this.imageWidth;
         if (slot.isFake()) {
            graphics.renderFakeItem(itemStack, x, y, seed);
         } else {
            graphics.renderItem(itemStack, x, y, seed);
         }

         graphics.renderItemDecorations(this.font, itemStack, x, y, itemCount);
      }

   }

   private void recalculateQuickCraftRemaining() {
      ItemStack carried = this.menu.getCarried();
      if (!carried.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = carried.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = carried.getCount();

            for(Slot slot : this.quickCraftSlots) {
               ItemStack slotItemStack = slot.getItem();
               int carry = slotItemStack.isEmpty() ? 0 : slotItemStack.getCount();
               int maxSize = Math.min(carried.getMaxStackSize(), slot.getMaxStackSize(carried));
               int newCount = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(), this.quickCraftingType, carried) + carry, maxSize);
               this.quickCraftingRemainder -= newCount - carry;
            }

         }
      }
   }

   private @Nullable Slot getHoveredSlot(final double x, final double y) {
      for(Slot slot : this.menu.slots) {
         if (slot.isActive() && this.isHovering(slot, x, y)) {
            return slot;
         }
      }

      return null;
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         return true;
      } else {
         boolean cloning = this.minecraft.options.keyPickItem.matchesMouse(event) && this.minecraft.player.hasInfiniteMaterials();
         Slot slot = this.getHoveredSlot(event.x(), event.y());
         this.doubleclick = this.lastClickSlot == slot && doubleClick;
         this.skipNextRelease = false;
         if (event.button() != 0 && event.button() != 1 && !cloning) {
            this.checkHotbarMouseClicked(event);
         } else {
            int xo = this.leftPos;
            int yo = this.topPos;
            boolean clickedOutside = this.hasClickedOutside(event.x(), event.y(), xo, yo);
            int slotId = -1;
            if (slot != null) {
               slotId = slot.index;
            }

            if (clickedOutside) {
               slotId = -999;
            }

            if ((Boolean)this.minecraft.options.touchscreen().get() && clickedOutside && this.menu.getCarried().isEmpty()) {
               this.onClose();
               return true;
            }

            if (slotId != -1) {
               if ((Boolean)this.minecraft.options.touchscreen().get()) {
                  if (slot != null && slot.hasItem()) {
                     this.clickedSlot = slot;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = event.button() == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.menu.getCarried().isEmpty()) {
                     if (cloning) {
                        this.slotClicked(slot, slotId, event.button(), ContainerInput.CLONE);
                     } else {
                        boolean quickKey = slotId != -999 && event.hasShiftDown();
                        ContainerInput containerInput = ContainerInput.PICKUP;
                        if (quickKey) {
                           this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                           containerInput = ContainerInput.QUICK_MOVE;
                        } else if (slotId == -999) {
                           containerInput = ContainerInput.THROW;
                        }

                        this.slotClicked(slot, slotId, event.button(), containerInput);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = event.button();
                     this.quickCraftSlots.clear();
                     if (event.button() == 0) {
                        this.quickCraftingType = 0;
                     } else if (event.button() == 1) {
                        this.quickCraftingType = 1;
                     } else if (cloning) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slot;
         return true;
      }
   }

   private void checkHotbarMouseClicked(final MouseButtonEvent event) {
      if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
         if (this.minecraft.options.keySwapOffhand.matchesMouse(event)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ContainerInput.SWAP);
            return;
         }

         for(int i = 0; i < 9; ++i) {
            if (this.minecraft.options.keyHotbarSlots[i].matchesMouse(event)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ContainerInput.SWAP);
            }
         }
      }

   }

   protected boolean hasClickedOutside(final double mx, final double my, final int xo, final int yo) {
      return mx < (double)xo || my < (double)yo || mx >= (double)(xo + this.imageWidth) || my >= (double)(yo + this.imageHeight);
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      Slot slot = this.getHoveredSlot(event.x(), event.y());
      ItemStack carried = this.menu.getCarried();
      if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
         if (event.button() == 0 || event.button() == 1) {
            if (this.draggingItem.isEmpty()) {
               if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if (this.draggingItem.getCount() > 1 && slot != null && AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false)) {
               long time = Util.getMillis();
               if (this.quickdropSlot == slot) {
                  if (time - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ContainerInput.PICKUP);
                     this.slotClicked(slot, slot.index, 1, ContainerInput.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ContainerInput.PICKUP);
                     this.quickdropTime = time + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = slot;
                  this.quickdropTime = time;
               }
            }
         }

         return true;
      } else if (slot != null && this.shouldAddSlotToQuickCraft(slot, carried) && this.quickCraftSlots.add(slot)) {
         this.recalculateQuickCraftRemaining();
         return true;
      } else {
         return slot == null && this.menu.getCarried().isEmpty() ? super.mouseDragged(event, dx, dy) : true;
      }
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      Slot slot = this.getHoveredSlot(event.x(), event.y());
      int xo = this.leftPos;
      int yo = this.topPos;
      boolean clickedOutside = this.hasClickedOutside(event.x(), event.y(), xo, yo);
      int slotId = -1;
      if (slot != null) {
         slotId = slot.index;
      }

      if (clickedOutside) {
         slotId = -999;
      }

      if (this.doubleclick && slot != null && event.button() == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
         if (event.hasShiftDown()) {
            if (!this.lastQuickMoved.isEmpty()) {
               for(Slot target : this.menu.slots) {
                  if (target != null && target.mayPickup(this.minecraft.player) && target.hasItem() && target.container == slot.container && AbstractContainerMenu.canItemQuickReplace(target, this.lastQuickMoved, true)) {
                     this.slotClicked(target, target.index, event.button(), ContainerInput.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(slot, slotId, event.button(), ContainerInput.PICKUP_ALL);
         }

         this.doubleclick = false;
      } else {
         if (this.isQuickCrafting && this.quickCraftingButton != event.button()) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
            if (event.button() == 0 || event.button() == 1) {
               if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               boolean canReplace = AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false);
               if (slotId != -1 && !this.draggingItem.isEmpty() && canReplace) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, event.button(), ContainerInput.PICKUP);
                  this.slotClicked(slot, slotId, 0, ContainerInput.PICKUP);
                  if (this.menu.getCarried().isEmpty()) {
                     this.snapbackData = null;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, event.button(), ContainerInput.PICKUP);
                     this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)event.x(), (int)event.y()), new Vector2i(this.clickedSlot.x + xo, this.clickedSlot.y + yo), Util.getMillis());
                  }
               } else if (!this.draggingItem.isEmpty()) {
                  this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)event.x(), (int)event.y()), new Vector2i(this.clickedSlot.x + xo, this.clickedSlot.y + yo), Util.getMillis());
               }

               this.clearDraggingState();
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.quickCraftToSlots();
         } else if (!this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(event)) {
               this.slotClicked(slot, slotId, event.button(), ContainerInput.CLONE);
            } else {
               boolean quickKey = slotId != -999 && event.hasShiftDown();
               if (quickKey) {
                  this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(slot, slotId, event.button(), quickKey ? ContainerInput.QUICK_MOVE : ContainerInput.PICKUP);
            }
         }
      }

      this.isQuickCrafting = false;
      return super.mouseReleased(event);
   }

   public void clearDraggingState() {
      this.draggingItem = ItemStack.EMPTY;
      this.clickedSlot = null;
   }

   private boolean isHovering(final Slot slot, final double xm, final double ym) {
      return this.isHovering(slot.x, slot.y, 16, 16, xm, ym);
   }

   protected boolean isHovering(final int left, final int top, final int w, final int h, double xm, double ym) {
      int xo = this.leftPos;
      int yo = this.topPos;
      xm -= (double)xo;
      ym -= (double)yo;
      return xm >= (double)(left - 1) && xm < (double)(left + w + 1) && ym >= (double)(top - 1) && ym < (double)(top + h + 1);
   }

   private void onStopHovering(final Slot slot) {
      if (slot.hasItem()) {
         for(ItemSlotMouseAction itemMouseAction : this.itemSlotMouseActions) {
            if (itemMouseAction.matches(slot)) {
               itemMouseAction.onStopHovering(slot);
            }
         }
      }

   }

   protected void slotClicked(final Slot slot, int slotId, final int buttonNum, final ContainerInput containerInput) {
      if (slot != null) {
         slotId = slot.index;
      }

      this.onMouseClickAction(slot, containerInput);
      this.minecraft.gameMode.handleContainerInput(this.menu.containerId, slotId, buttonNum, containerInput, this.minecraft.player);
   }

   void onMouseClickAction(final @Nullable Slot slot, final ContainerInput containerInput) {
      if (slot != null && slot.hasItem()) {
         for(ItemSlotMouseAction itemMouseAction : this.itemSlotMouseActions) {
            if (itemMouseAction.matches(slot)) {
               itemMouseAction.onSlotClicked(slot, containerInput);
            }
         }
      }

   }

   protected void handleSlotStateChanged(final int slotId, final int containerId, final boolean newState) {
      this.minecraft.gameMode.handleSlotStateChanged(slotId, containerId, newState);
   }

   public boolean keyPressed(final KeyEvent event) {
      if (super.keyPressed(event)) {
         return true;
      } else if (this.minecraft.options.keyInventory.matches(event)) {
         this.onClose();
         return true;
      } else {
         this.checkHotbarKeyPressed(event);
         if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches(event)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ContainerInput.CLONE);
            } else if (this.minecraft.options.keyDrop.matches(event)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, event.hasControlDown() ? 1 : 0, ContainerInput.THROW);
            }
         }

         return false;
      }
   }

   protected boolean checkHotbarKeyPressed(final KeyEvent event) {
      if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
         if (this.minecraft.options.keySwapOffhand.matches(event)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ContainerInput.SWAP);
            return true;
         }

         for(int i = 0; i < 9; ++i) {
            if (this.minecraft.options.keyHotbarSlots[i].matches(event)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ContainerInput.SWAP);
               return true;
            }
         }
      }

      return false;
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

   private boolean shouldAddSlotToQuickCraft(final Slot slot, final ItemStack carried) {
      return this.isQuickCrafting && !carried.isEmpty() && (carried.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slot, carried, true) && slot.mayPlace(carried) && this.menu.canDragTo(slot);
   }

   private void quickCraftToSlots() {
      this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ContainerInput.QUICK_CRAFT);

      for(Slot quickSlot : this.quickCraftSlots) {
         this.slotClicked(quickSlot, quickSlot.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ContainerInput.QUICK_CRAFT);
      }

      this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ContainerInput.QUICK_CRAFT);
   }

   public T getMenu() {
      return this.menu;
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      if (this.hoveredSlot != null) {
         this.onStopHovering(this.hoveredSlot);
      }

      super.onClose();
   }

   private static record SnapbackData(ItemStack item, Vector2i start, Vector2i end, long time) {
      private SnapbackData {
         super();
      }
   }
}
