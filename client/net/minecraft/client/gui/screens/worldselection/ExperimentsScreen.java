package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
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
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final PackRepository packRepository;
   private final Consumer<PackRepository> output;
   private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap();

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
      SwitchGrid.Builder var2 = SwitchGrid.builder(310).withInfoUnderneath(2, true).withRowSpacing(4);
      this.packs.forEach((var2x, var3x) -> {
         var2.addSwitch(getHumanReadableTitle(var2x), () -> {
            return this.packs.getBoolean(var2x);
         }, (var2xx) -> {
            this.packs.put(var2x, var2xx);
         }).withInfo(var2x.getDescription());
      });
      Objects.requireNonNull(var1);
      var2.build(var1::addChild);
      LinearLayout var3 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var3.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onDone();
      }).build());
      var3.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
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
      this.layout.arrangeElements();
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
}
