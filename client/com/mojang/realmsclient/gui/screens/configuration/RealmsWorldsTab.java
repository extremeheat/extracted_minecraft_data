package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.gui.RealmsWorldSlotButton;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.SwitchMinigameTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

class RealmsWorldsTab extends GridLayoutTab implements RealmsConfigurationTab {
   static final Component TITLE = Component.translatable("mco.configure.worlds.title");
   private final RealmsConfigureWorldScreen configurationScreen;
   private final Minecraft minecraft;
   private RealmsServer serverData;
   private final Button optionsButton;
   private final Button backupButton;
   private final Button resetWorldButton;
   private final List<RealmsWorldSlotButton> slotButtonList = Lists.newArrayList();

   RealmsWorldsTab(RealmsConfigureWorldScreen var1, Minecraft var2, RealmsServer var3) {
      super(TITLE);
      this.configurationScreen = var1;
      this.minecraft = var2;
      this.serverData = var3;
      GridLayout.RowHelper var4 = this.layout.spacing(20).createRowHelper(1);
      GridLayout.RowHelper var5 = (new GridLayout()).spacing(16).createRowHelper(4);
      this.slotButtonList.clear();

      for(int var6 = 1; var6 < 5; ++var6) {
         this.slotButtonList.add((RealmsWorldSlotButton)var5.addChild(this.createSlotButton(var6), LayoutSettings.defaults().alignVerticallyBottom()));
      }

      var4.addChild(var5.getGrid());
      GridLayout.RowHelper var7 = (new GridLayout()).spacing(8).createRowHelper(1);
      this.optionsButton = (Button)var7.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.options"), (var3x) -> var2.setScreen(new RealmsSlotOptionsScreen(var1, ((RealmsSlot)var3.slots.get(var3.activeSlot)).copy(), var3.worldType, var3.activeSlot))).bounds(0, 0, 150, 20).build());
      this.backupButton = (Button)var7.addChild(Button.builder(Component.translatable("mco.configure.world.backup"), (var3x) -> var2.setScreen(new RealmsBackupScreen(var1, var3.copy(), var3.activeSlot))).bounds(0, 0, 150, 20).build());
      this.resetWorldButton = (Button)var7.addChild(Button.builder(Component.empty(), (var1x) -> this.resetButtonPressed()).bounds(0, 0, 150, 20).build());
      var4.addChild(var7.getGrid(), LayoutSettings.defaults().alignHorizontallyCenter());
      this.backupButton.active = true;
      this.updateData(var3);
   }

   private void resetButtonPressed() {
      if (this.isMinigame()) {
         this.minecraft.setScreen(new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME, (WorldTemplatePaginatedList)null));
      } else {
         this.minecraft.setScreen(RealmsResetWorldScreen.forResetSlot(this.configurationScreen, this.serverData.copy(), () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.configurationScreen.getNewScreen()))));
      }

   }

   private void templateSelectionCallback(@Nullable WorldTemplate var1) {
      if (var1 != null && WorldTemplate.WorldTemplateType.MINIGAME == var1.type()) {
         this.configurationScreen.stateChanged();
         RealmsConfigureWorldScreen var2 = this.configurationScreen.getNewScreen();
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var2, new LongRunningTask[]{new SwitchMinigameTask(this.serverData.id, var1, var2)}));
      } else {
         this.minecraft.setScreen(this.configurationScreen);
      }

   }

   private boolean isMinigame() {
      return this.serverData.isMinigameActive();
   }

   public void onSelected(RealmsServer var1) {
      this.updateData(var1);
   }

   public void updateData(RealmsServer var1) {
      this.serverData = var1;
      this.optionsButton.active = !var1.expired && !this.isMinigame();
      this.resetWorldButton.active = !var1.expired;
      if (this.isMinigame()) {
         this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.switchminigame"));
      } else {
         boolean var2 = var1.slots.containsKey(var1.activeSlot) && ((RealmsSlot)var1.slots.get(var1.activeSlot)).options.empty;
         if (var2) {
            this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.newworld"));
         } else {
            this.resetWorldButton.setMessage(Component.translatable("mco.configure.world.buttons.resetworld"));
         }
      }

      this.backupButton.active = !this.isMinigame();

      for(RealmsWorldSlotButton var3 : this.slotButtonList) {
         RealmsWorldSlotButton.State var4 = var3.setServerData(var1);
         if (var4.activeSlot) {
            var3.setSize(80, 80);
         } else {
            var3.setSize(50, 50);
         }
      }

   }

   private RealmsWorldSlotButton createSlotButton(int var1) {
      return new RealmsWorldSlotButton(0, 0, 80, 80, var1, this.serverData, (var2) -> {
         RealmsWorldSlotButton.State var3 = ((RealmsWorldSlotButton)var2).getState();
         switch (var3.action) {
            case SWITCH_SLOT:
               if (var3.minigame) {
                  this.switchToMinigame();
               } else if (var3.empty) {
                  this.switchToEmptySlot(var1, this.serverData);
               } else {
                  this.switchToFullSlot(var1, this.serverData);
               }
            case NOTHING:
               return;
            default:
               throw new IllegalStateException("Unknown action " + String.valueOf(var3.action));
         }
      });
   }

   private void switchToMinigame() {
      RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(Component.translatable("mco.template.title.minigame"), this::templateSelectionCallback, RealmsServer.WorldType.MINIGAME, (WorldTemplatePaginatedList)null, List.of(Component.translatable("mco.minigame.world.info.line1").withColor(-4539718), Component.translatable("mco.minigame.world.info.line2").withColor(-4539718)));
      this.minecraft.setScreen(var1);
   }

   private void switchToFullSlot(int var1, RealmsServer var2) {
      this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this.configurationScreen, Component.translatable("mco.configure.world.slot.switch.question.line1"), (var3) -> {
         RealmsConfigureWorldScreen var4 = this.configurationScreen.getNewScreen();
         this.configurationScreen.stateChanged();
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(var4, new LongRunningTask[]{new SwitchSlotTask(var2.id, var1, () -> this.minecraft.execute(() -> this.minecraft.setScreen(var4)))}));
      }));
   }

   private void switchToEmptySlot(int var1, RealmsServer var2) {
      this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this.configurationScreen, Component.translatable("mco.configure.world.slot.switch.question.line1"), (var3) -> {
         this.configurationScreen.stateChanged();
         RealmsResetWorldScreen var4 = RealmsResetWorldScreen.forEmptySlot(this.configurationScreen, var1, var2, () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.configurationScreen.getNewScreen())));
         this.minecraft.setScreen(var4);
      }));
   }
}
