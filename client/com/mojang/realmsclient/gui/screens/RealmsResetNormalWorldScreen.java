package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.util.LevelType;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.ExperimentsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final Component SEED_LABEL = Component.translatable("mco.reset.world.seed");
   public static final Component TITLE = Component.translatable("mco.reset.world.generate");
   private static final int BUTTON_SPACING = 10;
   private static final int CONTENT_WIDTH = 210;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Consumer<WorldGenerationInfo> callback;
   private EditBox seedEdit;
   private LevelType levelType;
   private boolean generateStructures;
   private final Set<String> experiments;
   private final Component buttonTitle;

   public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> var1, Component var2) {
      super(TITLE);
      this.levelType = LevelType.DEFAULT;
      this.generateStructures = true;
      this.experiments = new HashSet();
      this.callback = var1;
      this.buttonTitle = var2;
   }

   public void init() {
      this.seedEdit = new EditBox(this.font, 210, 20, Component.translatable("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.layout.addTitleHeader(this.title, this.font);
      LinearLayout var1 = ((LinearLayout)this.layout.addToContents(LinearLayout.vertical())).spacing(10);
      var1.addChild(CommonLayouts.labeledElement(this.font, this.seedEdit, SEED_LABEL));
      var1.addChild(CycleButton.builder(LevelType::getName).withValues((Object[])LevelType.values()).withInitialValue(this.levelType).create(0, 0, 210, 20, Component.translatable("selectWorld.mapType"), (var1x, var2x) -> {
         this.levelType = var2x;
      }));
      var1.addChild(CycleButton.onOffBuilder(this.generateStructures).create(0, 0, 210, 20, Component.translatable("selectWorld.mapFeatures"), (var1x, var2x) -> {
         this.generateStructures = var2x;
      }));
      this.createExperimentsButton(var1);
      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
      var2.addChild(Button.builder(this.buttonTitle, (var1x) -> {
         this.callback.accept(this.createWorldGenerationInfo());
      }).build());
      var2.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.seedEdit);
   }

   private void createExperimentsButton(LinearLayout var1) {
      PackRepository var2 = ServerPacksSource.createVanillaTrustedRepository();
      var2.reload();
      var1.addChild(Button.builder(Component.translatable("selectWorld.experiments"), (var2x) -> {
         this.minecraft.setScreen(new ExperimentsScreen(this, var2, (var1) -> {
            this.experiments.clear();
            Iterator var2 = var1.getSelectedPacks().iterator();

            while(var2.hasNext()) {
               Pack var3 = (Pack)var2.next();
               if (var3.getPackSource() == PackSource.FEATURE) {
                  this.experiments.add(var3.getId());
               }
            }

            this.minecraft.setScreen(this);
         }));
      }).width(210).build());
   }

   private WorldGenerationInfo createWorldGenerationInfo() {
      return new WorldGenerationInfo(this.seedEdit.getValue(), this.levelType, this.generateStructures, this.experiments);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.callback.accept((Object)null);
   }
}
