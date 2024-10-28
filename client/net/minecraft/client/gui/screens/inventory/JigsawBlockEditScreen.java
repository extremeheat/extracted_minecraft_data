package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class JigsawBlockEditScreen extends Screen {
   private static final Component JOINT_LABEL = Component.translatable("jigsaw_block.joint_label");
   private static final Component POOL_LABEL = Component.translatable("jigsaw_block.pool");
   private static final Component NAME_LABEL = Component.translatable("jigsaw_block.name");
   private static final Component TARGET_LABEL = Component.translatable("jigsaw_block.target");
   private static final Component FINAL_STATE_LABEL = Component.translatable("jigsaw_block.final_state");
   private static final Component PLACEMENT_PRIORITY_LABEL = Component.translatable("jigsaw_block.placement_priority");
   private static final Component PLACEMENT_PRIORITY_TOOLTIP = Component.translatable("jigsaw_block.placement_priority.tooltip");
   private static final Component SELECTION_PRIORITY_LABEL = Component.translatable("jigsaw_block.selection_priority");
   private static final Component SELECTION_PRIORITY_TOOLTIP = Component.translatable("jigsaw_block.selection_priority.tooltip");
   private final JigsawBlockEntity jigsawEntity;
   private EditBox nameEdit;
   private EditBox targetEdit;
   private EditBox poolEdit;
   private EditBox finalStateEdit;
   private EditBox selectionPriorityEdit;
   private EditBox placementPriorityEdit;
   int levels;
   private boolean keepJigsaws = true;
   private CycleButton<JigsawBlockEntity.JointType> jointButton;
   private Button doneButton;
   private Button generateButton;
   private JigsawBlockEntity.JointType joint;

   public JigsawBlockEditScreen(JigsawBlockEntity var1) {
      super(GameNarrator.NO_TITLE);
      this.jigsawEntity = var1;
   }

   private void onDone() {
      this.sendToServer();
      this.minecraft.setScreen((Screen)null);
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void sendToServer() {
      this.minecraft.getConnection().send(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint, this.parseAsInt(this.selectionPriorityEdit.getValue()), this.parseAsInt(this.placementPriorityEdit.getValue())));
   }

   private int parseAsInt(String var1) {
      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   private void sendGenerate() {
      this.minecraft.getConnection().send(new ServerboundJigsawGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws));
   }

   public void onClose() {
      this.onCancel();
   }

   protected void init() {
      this.poolEdit = new EditBox(this.font, this.width / 2 - 153, 20, 300, 20, POOL_LABEL);
      this.poolEdit.setMaxLength(128);
      this.poolEdit.setValue(this.jigsawEntity.getPool().location().toString());
      this.poolEdit.setResponder((var1x) -> {
         this.updateValidity();
      });
      this.addWidget(this.poolEdit);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 153, 55, 300, 20, NAME_LABEL);
      this.nameEdit.setMaxLength(128);
      this.nameEdit.setValue(this.jigsawEntity.getName().toString());
      this.nameEdit.setResponder((var1x) -> {
         this.updateValidity();
      });
      this.addWidget(this.nameEdit);
      this.targetEdit = new EditBox(this.font, this.width / 2 - 153, 90, 300, 20, TARGET_LABEL);
      this.targetEdit.setMaxLength(128);
      this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
      this.targetEdit.setResponder((var1x) -> {
         this.updateValidity();
      });
      this.addWidget(this.targetEdit);
      this.finalStateEdit = new EditBox(this.font, this.width / 2 - 153, 125, 300, 20, FINAL_STATE_LABEL);
      this.finalStateEdit.setMaxLength(256);
      this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
      this.addWidget(this.finalStateEdit);
      this.selectionPriorityEdit = new EditBox(this.font, this.width / 2 - 153, 160, 98, 20, SELECTION_PRIORITY_LABEL);
      this.selectionPriorityEdit.setMaxLength(3);
      this.selectionPriorityEdit.setValue(Integer.toString(this.jigsawEntity.getSelectionPriority()));
      this.selectionPriorityEdit.setTooltip(Tooltip.create(SELECTION_PRIORITY_TOOLTIP));
      this.addWidget(this.selectionPriorityEdit);
      this.placementPriorityEdit = new EditBox(this.font, this.width / 2 - 50, 160, 98, 20, PLACEMENT_PRIORITY_LABEL);
      this.placementPriorityEdit.setMaxLength(3);
      this.placementPriorityEdit.setValue(Integer.toString(this.jigsawEntity.getPlacementPriority()));
      this.placementPriorityEdit.setTooltip(Tooltip.create(PLACEMENT_PRIORITY_TOOLTIP));
      this.addWidget(this.placementPriorityEdit);
      this.joint = this.jigsawEntity.getJoint();
      this.jointButton = (CycleButton)this.addRenderableWidget(CycleButton.builder(JigsawBlockEntity.JointType::getTranslatedName).withValues((Object[])JigsawBlockEntity.JointType.values()).withInitialValue(this.joint).displayOnlyValue().create(this.width / 2 + 54, 160, 100, 20, JOINT_LABEL, (var1x, var2) -> {
         this.joint = var2;
      }));
      boolean var1 = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
      this.jointButton.active = var1;
      this.jointButton.visible = var1;
      this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 185, 100, 20, CommonComponents.EMPTY, 0.0) {
         {
            this.updateMessage();
         }

         protected void updateMessage() {
            this.setMessage(Component.translatable("jigsaw_block.levels", JigsawBlockEditScreen.this.levels));
         }

         protected void applyValue() {
            JigsawBlockEditScreen.this.levels = Mth.floor(Mth.clampedLerp(0.0, 20.0, this.value));
         }
      });
      this.addRenderableWidget(CycleButton.onOffBuilder(this.keepJigsaws).create(this.width / 2 - 50, 185, 100, 20, Component.translatable("jigsaw_block.keep_jigsaws"), (var1x, var2) -> {
         this.keepJigsaws = var2;
      }));
      this.generateButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("jigsaw_block.generate"), (var1x) -> {
         this.onDone();
         this.sendGenerate();
      }).bounds(this.width / 2 + 54, 185, 100, 20).build());
      this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onDone();
      }).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.onCancel();
      }).bounds(this.width / 2 + 4, 210, 150, 20).build());
      this.updateValidity();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.poolEdit);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
   }

   private void updateValidity() {
      boolean var1 = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
      this.doneButton.active = var1;
      this.generateButton.active = var1;
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.nameEdit.getValue();
      String var5 = this.targetEdit.getValue();
      String var6 = this.poolEdit.getValue();
      String var7 = this.finalStateEdit.getValue();
      String var8 = this.selectionPriorityEdit.getValue();
      String var9 = this.placementPriorityEdit.getValue();
      int var10 = this.levels;
      JigsawBlockEntity.JointType var11 = this.joint;
      this.init(var1, var2, var3);
      this.nameEdit.setValue(var4);
      this.targetEdit.setValue(var5);
      this.poolEdit.setValue(var6);
      this.finalStateEdit.setValue(var7);
      this.levels = var10;
      this.joint = var11;
      this.jointButton.setValue(var11);
      this.selectionPriorityEdit.setValue(var8);
      this.placementPriorityEdit.setValue(var9);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (!this.doneButton.active || var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)POOL_LABEL, this.width / 2 - 153, 10, 10526880);
      this.poolEdit.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)NAME_LABEL, this.width / 2 - 153, 45, 10526880);
      this.nameEdit.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)TARGET_LABEL, this.width / 2 - 153, 80, 10526880);
      this.targetEdit.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
      this.finalStateEdit.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)SELECTION_PRIORITY_LABEL, this.width / 2 - 153, 150, 10526880);
      this.placementPriorityEdit.render(var1, var2, var3, var4);
      var1.drawString(this.font, (Component)PLACEMENT_PRIORITY_LABEL, this.width / 2 - 50, 150, 10526880);
      this.selectionPriorityEdit.render(var1, var2, var3, var4);
      if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
         var1.drawString(this.font, (Component)JOINT_LABEL, this.width / 2 + 53, 150, 10526880);
      }

   }
}
