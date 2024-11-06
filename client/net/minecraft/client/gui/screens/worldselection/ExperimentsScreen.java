package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class ExperimentsScreen extends Screen {
   private static final Component TITLE = Component.translatable("selectWorld.experiments");
   private static final Component INFO;
   private static final int MAIN_CONTENT_WIDTH = 310;
   private static final int SCROLL_AREA_MIN_HEIGHT = 130;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final PackRepository packRepository;
   private final Consumer<PackRepository> output;
   private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap();
   @Nullable
   private ScrollArea scrollArea;

   public ExperimentsScreen(Screen var1, PackRepository var2, Consumer<PackRepository> var3) {
      super(TITLE);
      this.parent = var1;
      this.packRepository = var2;
      this.output = var3;
      Iterator var4 = var2.getAvailablePacks().iterator();

      while(var4.hasNext()) {
         Pack var5 = (Pack)var4.next();
         if (var5.getPackSource() == PackSource.FEATURE) {
            this.packs.put(var5, var2.getSelectedPacks().contains(var5));
         }
      }

   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      LinearLayout var1 = (LinearLayout)this.layout.addToContents(LinearLayout.vertical());
      var1.addChild((new MultiLineTextWidget(INFO, this.font)).setMaxWidth(310), (Consumer)((var0) -> {
         var0.paddingBottom(15);
      }));
      SwitchGrid.Builder var2 = SwitchGrid.builder(299).withInfoUnderneath(2, true).withRowSpacing(4);
      this.packs.forEach((var2x, var3x) -> {
         var2.addSwitch(getHumanReadableTitle(var2x), () -> {
            return this.packs.getBoolean(var2x);
         }, (var2xx) -> {
            this.packs.put(var2x, var2xx);
         }).withInfo(var2x.getDescription());
      });
      Layout var3 = var2.build().layout();
      this.scrollArea = new ScrollArea(this, var3, 310, 130);
      var1.addChild(this.scrollArea);
      LinearLayout var4 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var4.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onDone();
      }).build());
      var4.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   private static Component getHumanReadableTitle(Pack var0) {
      String var1 = "dataPack." + var0.getId() + ".name";
      return (Component)(I18n.exists(var1) ? Component.translatable(var1) : var0.getTitle());
   }

   protected void repositionElements() {
      this.scrollArea.setHeight(130);
      this.layout.arrangeElements();
      int var1 = this.height - this.layout.getFooterHeight() - this.scrollArea.getRectangle().bottom();
      this.scrollArea.setHeight(this.scrollArea.getHeight() + var1);
      this.scrollArea.refreshScrollAmount();
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), INFO);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private void onDone() {
      ArrayList var1 = new ArrayList(this.packRepository.getSelectedPacks());
      ArrayList var2 = new ArrayList();
      this.packs.forEach((var2x, var3) -> {
         var1.remove(var2x);
         if (var3) {
            var2.add(var2x);
         }

      });
      var1.addAll(Lists.reverse(var2));
      this.packRepository.setSelected(var1.stream().map(Pack::getId).toList());
      this.output.accept(this.packRepository);
   }

   static {
      INFO = Component.translatable("selectWorld.experiments.info").withStyle(ChatFormatting.RED);
   }

   public class ScrollArea extends AbstractContainerWidget {
      private final List<AbstractWidget> children = new ArrayList();
      private final Layout layout;

      public ScrollArea(final ExperimentsScreen var1, final Layout var2, final int var3, final int var4) {
         super(0, 0, var3, var4, CommonComponents.EMPTY);
         this.layout = var2;
         var2.visitWidgets(this::addWidget);
      }

      public void addWidget(AbstractWidget var1) {
         this.children.add(var1);
      }

      protected int contentHeight() {
         return this.layout.getHeight();
      }

      protected double scrollRate() {
         return 10.0;
      }

      protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
         var1.pose().pushPose();
         var1.pose().translate(0.0, -this.scrollAmount(), 0.0);
         Iterator var5 = this.children.iterator();

         while(var5.hasNext()) {
            AbstractWidget var6 = (AbstractWidget)var5.next();
            var6.render(var1, var2, var3, var4);
         }

         var1.pose().popPose();
         var1.disableScissor();
         this.renderScrollbar(var1);
      }

      protected void updateWidgetNarration(NarrationElementOutput var1) {
      }

      public ScreenRectangle getBorderForArrowNavigation(ScreenDirection var1) {
         return new ScreenRectangle(this.getX(), this.getY(), this.width, this.contentHeight());
      }

      public void setFocused(@Nullable GuiEventListener var1) {
         super.setFocused(var1);
         if (var1 != null) {
            ScreenRectangle var2 = this.getRectangle();
            ScreenRectangle var3 = var1.getRectangle();
            int var4 = (int)((double)var3.top() - this.scrollAmount() - (double)var2.top());
            int var5 = (int)((double)var3.bottom() - this.scrollAmount() - (double)var2.bottom());
            if (var4 < 0) {
               this.setScrollAmount(this.scrollAmount() + (double)var4 - 14.0);
            } else if (var5 > 0) {
               this.setScrollAmount(this.scrollAmount() + (double)var5 + 14.0);
            }

         }
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public void setX(int var1) {
         super.setX(var1);
         this.layout.setX(var1);
         this.layout.arrangeElements();
      }

      public void setY(int var1) {
         super.setY(var1);
         this.layout.setY(var1);
         this.layout.arrangeElements();
      }

      public Collection<? extends NarratableEntry> getNarratables() {
         return this.children;
      }
   }
}
