package net.minecraft.client.gui.screens.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.inventory.EnchantmentOptionButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.Nullable;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final Identifier ENCHANTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/enchanting_table.png");
   private static final Identifier ENCHANTING_BOOK_LOCATION = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");
   private final RandomSource random = RandomSource.create();
   private BookModel bookModel;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last;
   private final EnchantmentOptionButton[] enchantmentOptions;

   public EnchantmentScreen(final EnchantmentMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.last = ItemStack.EMPTY;
      this.enchantmentOptions = new EnchantmentOptionButton[3];
   }

   protected void init() {
      super.init();
      LinearLayout layout = LinearLayout.vertical();

      for(int i = 0; i < this.enchantmentOptions.length; ++i) {
         this.enchantmentOptions[i] = (EnchantmentOptionButton)layout.addChild(new EnchantmentOptionButton(0, 0, this.menu, i, (button) -> {
            if (((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, i)) {
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, i);
            }

         }));
      }

      ScrollableLayout scrollableLayout = new ScrollableLayout(this.minecraft, layout, 19 * this.enchantmentOptions.length, AbstractScrollArea.ScrollbarSettings.NO_SCROLL, 0, ScrollableLayout.ReserveStrategy.RIGHT, GroupNarration.list("narrator.position.list", "narration.group_navigation.usage.enchantment", ScreenAxis.VERTICAL));
      scrollableLayout.setPosition(this.leftPos + 60, this.topPos + 14);
      scrollableLayout.arrangeElements();
      scrollableLayout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      scrollableLayout.setTabGroup(7);
      scrollableLayout.allowTabNavigationInside(false);
      this.tickTooltipAndActiveState();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
   }

   public void containerTick() {
      super.containerTick();
      this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
      this.tickBook();
      this.tickTooltipAndActiveState();
   }

   public void extractBackground(final GuiGraphicsExtractor graphics) {
      super.extractBackground(graphics);
      graphics.blit(GuiPipeline.TEXTURED, ENCHANTING_TABLE_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.extractBook(graphics, this.leftPos, this.topPos);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
   }

   private void extractBook(final GuiGraphicsExtractor graphics, final int left, final int top) {
      float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
      float open = Mth.lerp(a, this.oOpen, this.open);
      float flip = Mth.lerp(a, this.oFlip, this.flip);
      int x0 = left + 14;
      int y0 = top + 14;
      int x1 = x0 + 38;
      int y1 = y0 + 31;
      graphics.book(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0F, open, flip, x0, y0, x1, y1);
   }

   public void tickBook() {
      ItemStack current = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
      if (!ItemStack.matches(current, this.last)) {
         this.last = current;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean shouldBeOpen = false;

      for(int i = 0; i < 3; ++i) {
         if ((this.menu).costs[i] != 0) {
            shouldBeOpen = true;
            break;
         }
      }

      if (shouldBeOpen) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Math.clamp(this.open, 0.0F, 1.0F);
      float diff = (this.flipT - this.flip) * 0.4F;
      float max = 0.2F;
      diff = Math.clamp(diff, -0.2F, 0.2F);
      this.flipA += (diff - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }

   private void tickTooltipAndActiveState() {
      for(int i = 0; i < this.enchantmentOptions.length; ++i) {
         Component clue = this.getClue(i);
         this.enchantmentOptions[i].setMessage(clue != null ? clue : CommonComponents.EMPTY);
         this.enchantmentOptions[i].setTooltip(clue != null ? this.getTooltip(i, clue) : Tooltip.create(CommonComponents.EMPTY));
         this.enchantmentOptions[i].active = (this.menu).costs[i] > 0 && (((EnchantmentMenu)this.menu).getGoldCount() >= i + 1 && this.minecraft.player.experienceLevel >= (this.menu).costs[i] || this.minecraft.player.hasInfiniteMaterials());
      }

   }

   private @Nullable Component getClue(final int index) {
      int cost = (this.menu).costs[index];
      int enchantLevel = (this.menu).levelClue[index];
      Optional<Holder.Reference<Enchantment>> enchant = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get((this.menu).enchantClue[index]);
      return !enchant.isEmpty() && cost > 0 && enchantLevel >= 0 ? Component.translatable("container.enchant.clue", Enchantment.getFullname((Holder)enchant.get(), enchantLevel)).withStyle(ChatFormatting.WHITE) : null;
   }

   private Tooltip getTooltip(final int index, final Component clue) {
      if (this.minecraft.player.hasInfiniteMaterials()) {
         return Tooltip.create(clue, (Component)null);
      } else {
         int gold = ((EnchantmentMenu)this.menu).getGoldCount();
         int lapisCost = index + 1;
         int cost = (this.menu).costs[index];
         List<Component> costs = new ArrayList();
         if (this.minecraft.player.experienceLevel < cost) {
            costs.add(Component.translatable("container.enchant.level.requirement", (this.menu).costs[index]).withStyle(ChatFormatting.RED));
         } else {
            MutableComponent lapisCostText;
            if (lapisCost == 1) {
               lapisCostText = Component.translatable("container.enchant.lapis.one");
            } else {
               lapisCostText = Component.translatable("container.enchant.lapis.many", lapisCost);
            }

            costs.add(lapisCostText.withStyle(gold >= lapisCost ? ChatFormatting.GRAY : ChatFormatting.RED));
            MutableComponent levelCost;
            if (lapisCost == 1) {
               levelCost = Component.translatable("container.enchant.level.one");
            } else {
               levelCost = Component.translatable("container.enchant.level.many", lapisCost);
            }

            costs.add(levelCost.withStyle(ChatFormatting.GRAY));
         }

         Component costText = CommonComponents.joinLines((Collection)costs);
         return Tooltip.create(CommonComponents.joinLines(clue, CommonComponents.EMPTY, costText), costText);
      }
   }
}
