package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.floats.FloatComparators;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class DebugOptionsScreen extends Screen {
   private static final Component TITLE = Component.translatable("debug.options.title");
   private static final Component SUBTITLE = Component.translatable("debug.options.warning");
   static final Component ENABLED_TEXT = Component.translatable("debug.entry.always");
   static final Component IN_F3_TEXT = Component.translatable("debug.entry.f3");
   static final Component DISABLED_TEXT;
   static final Component NOT_ALLOWED_TOOLTIP;
   private static final Component SEARCH;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
   @Nullable
   private OptionList optionList;
   private EditBox searchBox;
   final List<Button> profileButtons = new ArrayList();

   public DebugOptionsScreen() {
      super(TITLE);
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      this.optionList = new OptionList();
      int var2 = this.optionList.getRowWidth();
      LinearLayout var3 = LinearLayout.horizontal().spacing(8);
      var3.addChild(new SpacerElement(var2 / 3, 1));
      var3.addChild(new StringWidget(TITLE, this.font), var3.newCellSettings().alignVerticallyMiddle());
      this.searchBox = new EditBox(this.font, 0, 0, var2 / 3, 20, this.searchBox, SEARCH);
      this.searchBox.setResponder((var1x) -> this.optionList.updateSearch(var1x));
      this.searchBox.setHint(SEARCH);
      var3.addChild(this.searchBox);
      var1.addChild(var3, (Consumer)(LayoutSettings::alignHorizontallyCenter));
      var1.addChild((new MultiLineTextWidget(SUBTITLE, this.font)).setMaxWidth(var2).setCentered(true).setColor(-2142128), (Consumer)(LayoutSettings::alignHorizontallyCenter));
      this.layout.addToContents(this.optionList);
      LinearLayout var4 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.addProfileButton(DebugScreenProfile.DEFAULT, var4);
      this.addProfileButton(DebugScreenProfile.PERFORMANCE, var4);
      var4.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).width(60).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.searchBox);
   }

   private void addProfileButton(DebugScreenProfile var1, LinearLayout var2) {
      Button var3 = Button.builder(Component.translatable(var1.translationKey()), (var2x) -> {
         this.minecraft.debugEntries.loadProfile(var1);
         this.minecraft.debugEntries.save();
         this.optionList.refreshEntries();

         for(Button var4 : this.profileButtons) {
            var4.active = true;
         }

         var2x.active = false;
      }).width(120).build();
      var3.active = !this.minecraft.debugEntries.isUsingProfile(var1);
      this.profileButtons.add(var3);
      var2.addChild(var3);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.optionList != null) {
         this.optionList.updateSize(this.width, this.layout);
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
   }

   static {
      DISABLED_TEXT = CommonComponents.OPTION_OFF;
      NOT_ALLOWED_TOOLTIP = Component.translatable("debug.options.notAllowed.tooltip");
      SEARCH = Component.translatable("debug.options.search");
   }

   class OptionList extends ContainerObjectSelectionList<AbstractOptionEntry> {
      private static final Comparator<Map.Entry<ResourceLocation, DebugScreenEntry>> COMPARATOR = (var0, var1) -> {
         int var2 = FloatComparators.NATURAL_COMPARATOR.compare(((DebugScreenEntry)var0.getValue()).category().sortKey(), ((DebugScreenEntry)var1.getValue()).category().sortKey());
         return var2 != 0 ? var2 : ((ResourceLocation)var0.getKey()).compareTo((ResourceLocation)var1.getKey());
      };
      private static final int ITEM_HEIGHT = 20;

      public OptionList() {
         super(Minecraft.getInstance(), DebugOptionsScreen.this.width, DebugOptionsScreen.this.layout.getContentHeight(), DebugOptionsScreen.this.layout.getHeaderHeight(), 20);
         this.updateSearch("");
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
      }

      public int getRowWidth() {
         return 310;
      }

      public void refreshEntries() {
         this.children().forEach(AbstractOptionEntry::refreshEntry);
      }

      public void updateSearch(String var1) {
         this.clearEntries();
         ArrayList var2 = new ArrayList(DebugScreenEntries.allEntries().entrySet());
         var2.sort(COMPARATOR);
         DebugEntryCategory var3 = null;

         for(Map.Entry var5 : var2) {
            if (((ResourceLocation)var5.getKey()).getPath().contains(var1)) {
               DebugEntryCategory var6 = ((DebugScreenEntry)var5.getValue()).category();
               if (!var6.equals(var3)) {
                  this.addEntry(DebugOptionsScreen.this.new CategoryEntry(var6.label()));
                  var3 = var6;
               }

               this.addEntry(DebugOptionsScreen.this.new OptionEntry((ResourceLocation)var5.getKey()));
            }
         }

         this.notifyListUpdated();
      }

      private void notifyListUpdated() {
         this.refreshScrollAmount();
         DebugOptionsScreen.this.triggerImmediateNarration(true);
      }
   }

   public abstract static class AbstractOptionEntry extends ContainerObjectSelectionList.Entry<AbstractOptionEntry> {
      public AbstractOptionEntry() {
         super();
      }

      public abstract void refreshEntry();
   }

   class CategoryEntry extends AbstractOptionEntry {
      final Component category;

      public CategoryEntry(final Component var2) {
         super();
         this.category = var2;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawCenteredString(DebugOptionsScreen.this.minecraft.font, (Component)this.category, var4 + var5 / 2, var3 + 5, -1);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput var1) {
               var1.add(NarratedElementType.TITLE, CategoryEntry.this.category);
            }
         });
      }

      public void refreshEntry() {
      }
   }

   class OptionEntry extends AbstractOptionEntry {
      private final ResourceLocation location;
      protected final List<AbstractWidget> children = Lists.newArrayList();
      private final CycleButton<Boolean> always;
      private final CycleButton<Boolean> f3;
      private final CycleButton<Boolean> never;
      private final String name;
      private final boolean isAllowed;

      public OptionEntry(final ResourceLocation var2) {
         super();
         this.location = var2;
         DebugScreenEntry var3 = DebugScreenEntries.getEntry(var2);
         this.isAllowed = var3 != null && var3.isAllowed(DebugOptionsScreen.this.minecraft.showOnlyReducedInfo());
         String var4 = var2.getPath();
         if (this.isAllowed) {
            this.name = var4;
         } else {
            String var10001 = String.valueOf(ChatFormatting.ITALIC);
            this.name = var10001 + var4;
         }

         this.always = CycleButton.booleanBuilder(DebugOptionsScreen.ENABLED_TEXT.copy().withColor(-2142128), DebugOptionsScreen.ENABLED_TEXT.copy().withColor(-4539718)).displayOnlyValue().withCustomNarration(this::narrateButton).create(10, 5, 44, 16, Component.literal(var4), (var2x, var3x) -> this.setValue(var2, DebugScreenEntryStatus.ALWAYS_ON));
         this.f3 = CycleButton.booleanBuilder(DebugOptionsScreen.IN_F3_TEXT.copy().withColor(-171), DebugOptionsScreen.IN_F3_TEXT.copy().withColor(-4539718)).displayOnlyValue().withCustomNarration(this::narrateButton).create(10, 5, 44, 16, Component.literal(var4), (var2x, var3x) -> this.setValue(var2, DebugScreenEntryStatus.IN_F3));
         this.never = CycleButton.booleanBuilder(DebugOptionsScreen.DISABLED_TEXT.copy().withColor(-1), DebugOptionsScreen.DISABLED_TEXT.copy().withColor(-4539718)).displayOnlyValue().withCustomNarration(this::narrateButton).create(10, 5, 44, 16, Component.literal(var4), (var2x, var3x) -> this.setValue(var2, DebugScreenEntryStatus.NEVER));
         this.children.add(this.never);
         this.children.add(this.f3);
         this.children.add(this.always);
         this.refreshEntry();
      }

      private MutableComponent narrateButton(CycleButton<Boolean> var1) {
         DebugScreenEntryStatus var2 = DebugOptionsScreen.this.minecraft.debugEntries.getStatus(this.location);
         MutableComponent var3 = Component.translatable("debug.entry.currently." + var2.getSerializedName(), this.name);
         return CommonComponents.optionNameValue(var3, var1.getMessage());
      }

      private void setValue(ResourceLocation var1, DebugScreenEntryStatus var2) {
         DebugOptionsScreen.this.minecraft.debugEntries.setStatus(var1, var2);

         for(Button var4 : DebugOptionsScreen.this.profileButtons) {
            var4.active = true;
         }

         this.refreshEntry();
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawString(DebugOptionsScreen.this.minecraft.font, this.name, var4, var3 + 5, this.isAllowed ? -1 : -8355712);
         int var11 = var4 + var5 - this.never.getWidth() - this.f3.getWidth() - this.always.getWidth();
         if (!this.isAllowed && var9 && var7 < var11) {
            var1.setTooltipForNextFrame(DebugOptionsScreen.NOT_ALLOWED_TOOLTIP, var7, var8);
         }

         this.never.setX(var11);
         this.f3.setX(this.never.getX() + this.never.getWidth());
         this.always.setX(this.f3.getX() + this.f3.getWidth());
         this.always.setY(var3);
         this.f3.setY(var3);
         this.never.setY(var3);
         this.always.render(var1, var7, var8, var10);
         this.f3.render(var1, var7, var8, var10);
         this.never.render(var1, var7, var8, var10);
      }

      public void refreshEntry() {
         DebugScreenEntryStatus var1 = DebugOptionsScreen.this.minecraft.debugEntries.getStatus(this.location);
         this.always.setValue(var1 == DebugScreenEntryStatus.ALWAYS_ON);
         this.f3.setValue(var1 == DebugScreenEntryStatus.IN_F3);
         this.never.setValue(var1 == DebugScreenEntryStatus.NEVER);
         this.always.active = !(Boolean)this.always.getValue();
         this.f3.active = !(Boolean)this.f3.getValue();
         this.never.active = !(Boolean)this.never.getValue();
      }
   }
}
