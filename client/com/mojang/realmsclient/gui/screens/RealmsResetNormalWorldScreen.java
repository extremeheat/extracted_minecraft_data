package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final Component SEED_LABEL = new TranslatableComponent("mco.reset.world.seed");
   private static final Component[] LEVEL_TYPES = new Component[]{new TranslatableComponent("generator.default"), new TranslatableComponent("generator.flat"), new TranslatableComponent("generator.large_biomes"), new TranslatableComponent("generator.amplified")};
   private final RealmsResetWorldScreen lastScreen;
   private RealmsLabel titleLabel;
   private EditBox seedEdit;
   private Boolean generateStructures = true;
   private Integer levelTypeIndex = 0;
   private final Component buttonTitle;

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen var1, Component var2) {
      super();
      this.lastScreen = var1;
      this.buttonTitle = var2;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.titleLabel = new RealmsLabel(new TranslatableComponent("mco.reset.world.generate"), this.width / 2, 17, 16777215);
      this.addWidget(this.titleLabel);
      this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (EditBox)null, new TranslatableComponent("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.addWidget(this.seedEdit);
      this.setInitialFocus(this.seedEdit);
      this.addButton(new Button(this.width / 2 - 102, row(4), 205, 20, this.levelTypeTitle(), (var1) -> {
         this.levelTypeIndex = (this.levelTypeIndex + 1) % LEVEL_TYPES.length;
         var1.setMessage(this.levelTypeTitle());
      }));
      this.addButton(new Button(this.width / 2 - 102, row(6) - 2, 205, 20, this.generateStructuresTitle(), (var1) -> {
         this.generateStructures = !this.generateStructures;
         var1.setMessage(this.generateStructuresTitle());
      }));
      this.addButton(new Button(this.width / 2 - 102, row(12), 97, 20, this.buttonTitle, (var1) -> {
         this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures));
      }));
      this.addButton(new Button(this.width / 2 + 8, row(12), 97, 20, CommonComponents.GUI_BACK, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.narrateLabels();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.titleLabel.render(this, var1);
      this.font.draw(var1, SEED_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      this.seedEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   private Component levelTypeTitle() {
      return (new TranslatableComponent("selectWorld.mapType")).append(" ").append(LEVEL_TYPES[this.levelTypeIndex]);
   }

   private Component generateStructuresTitle() {
      return CommonComponents.optionStatus(new TranslatableComponent("selectWorld.mapFeatures"), this.generateStructures);
   }
}
