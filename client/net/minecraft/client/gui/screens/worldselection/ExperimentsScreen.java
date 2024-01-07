package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class ExperimentsScreen extends Screen {
   private static final int MAIN_CONTENT_WIDTH = 310;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final PackRepository packRepository;
   private final Consumer<PackRepository> output;
   private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap();

   public ExperimentsScreen(Screen var1, PackRepository var2, Consumer<PackRepository> var3) {
      super(Component.translatable("experiments_screen.title"));
      this.parent = var1;
      this.packRepository = var2;
      this.output = var3;

      for(Pack var5 : var2.getAvailablePacks()) {
         if (var5.getPackSource() == PackSource.FEATURE) {
            this.packs.put(var5, var2.getSelectedPacks().contains(var5));
         }
      }
   }

   @Override
   protected void init() {
      this.layout.addToHeader(new StringWidget(Component.translatable("selectWorld.experiments"), this.font));
      LinearLayout var1 = this.layout.addToContents(LinearLayout.vertical());
      var1.addChild(
         new MultiLineTextWidget(Component.translatable("selectWorld.experiments.info").withStyle(ChatFormatting.RED), this.font).setMaxWidth(310),
         var0 -> var0.paddingBottom(15)
      );
      SwitchGrid.Builder var2 = SwitchGrid.builder(310).withInfoUnderneath(2, true).withRowSpacing(4);
      this.packs
         .forEach(
            (var2x, var3x) -> var2.addSwitch(getHumanReadableTitle(var2x), () -> this.packs.getBoolean(var2x), var2xx -> this.packs.put(var2x, var2xx))
                  .withInfo(var2x.getDescription())
         );
      var2.build(var1::addChild);
      GridLayout.RowHelper var3 = this.layout.addToFooter(new GridLayout().columnSpacing(10)).createRowHelper(2);
      var3.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onDone()).build());
      var3.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.repositionElements();
   }

   private static Component getHumanReadableTitle(Pack var0) {
      String var1 = "dataPack." + var0.getId() + ".name";
      return (Component)(I18n.exists(var1) ? Component.translatable(var1) : var0.getTitle());
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   private void onDone() {
      ArrayList var1 = new ArrayList<>(this.packRepository.getSelectedPacks());
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

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      var1.setColor(0.125F, 0.125F, 0.125F, 1.0F);
      boolean var5 = true;
      var1.blit(
         BACKGROUND_LOCATION,
         0,
         this.layout.getHeaderHeight(),
         0.0F,
         0.0F,
         this.width,
         this.height - this.layout.getHeaderHeight() - this.layout.getFooterHeight(),
         32,
         32
      );
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
