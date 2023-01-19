package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.util.LevelType;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final Component SEED_LABEL = Component.translatable("mco.reset.world.seed");
   private final Consumer<WorldGenerationInfo> callback;
   private EditBox seedEdit;
   private LevelType levelType = LevelType.DEFAULT;
   private boolean generateStructures = true;
   private final Component buttonTitle;

   public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> var1, Component var2) {
      super(Component.translatable("mco.reset.world.generate"));
      this.callback = var1;
      this.buttonTitle = var2;
   }

   @Override
   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   @Override
   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, null, Component.translatable("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.addWidget(this.seedEdit);
      this.setInitialFocus(this.seedEdit);
      this.addRenderableWidget(
         CycleButton.builder(LevelType::getName)
            .withValues(LevelType.values())
            .withInitialValue(this.levelType)
            .create(this.width / 2 - 102, row(4), 205, 20, Component.translatable("selectWorld.mapType"), (var1, var2) -> this.levelType = var2)
      );
      this.addRenderableWidget(
         CycleButton.onOffBuilder(this.generateStructures)
            .create(
               this.width / 2 - 102, row(6) - 2, 205, 20, Component.translatable("selectWorld.mapFeatures"), (var1, var2) -> this.generateStructures = var2
            )
      );
      this.addRenderableWidget(
         new Button(
            this.width / 2 - 102,
            row(12),
            97,
            20,
            this.buttonTitle,
            var1 -> this.callback.accept(new WorldGenerationInfo(this.seedEdit.getValue(), this.levelType, this.generateStructures))
         )
      );
      this.addRenderableWidget(new Button(this.width / 2 + 8, row(12), 97, 20, CommonComponents.GUI_BACK, var1 -> this.onClose()));
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   @Override
   public void onClose() {
      this.callback.accept(null);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(var1, SEED_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      this.seedEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
