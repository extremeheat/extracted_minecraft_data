package net.minecraft.client.gui.screens.inventory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class StructureBlockEditScreen extends Screen {
   private final StructureBlockEntity structure;
   private Mirror initialMirror;
   private Rotation initialRotation;
   private StructureMode initialMode;
   private boolean initialEntityIgnoring;
   private boolean initialShowAir;
   private boolean initialShowBoundingBox;
   private EditBox nameEdit;
   private EditBox posXEdit;
   private EditBox posYEdit;
   private EditBox posZEdit;
   private EditBox sizeXEdit;
   private EditBox sizeYEdit;
   private EditBox sizeZEdit;
   private EditBox integrityEdit;
   private EditBox seedEdit;
   private EditBox dataEdit;
   private Button doneButton;
   private Button cancelButton;
   private Button saveButton;
   private Button loadButton;
   private Button rot0Button;
   private Button rot90Button;
   private Button rot180Button;
   private Button rot270Button;
   private Button modeButton;
   private Button detectButton;
   private Button entitiesButton;
   private Button mirrorButton;
   private Button toggleAirButton;
   private Button toggleBoundingBox;
   private final DecimalFormat decimalFormat;

   public StructureBlockEditScreen(StructureBlockEntity var1) {
      super(new TranslatableComponent(Blocks.STRUCTURE_BLOCK.getDescriptionId(), new Object[0]));
      this.initialMirror = Mirror.NONE;
      this.initialRotation = Rotation.NONE;
      this.initialMode = StructureMode.DATA;
      this.decimalFormat = new DecimalFormat("0.0###");
      this.structure = var1;
      this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public void tick() {
      this.nameEdit.tick();
      this.posXEdit.tick();
      this.posYEdit.tick();
      this.posZEdit.tick();
      this.sizeXEdit.tick();
      this.sizeYEdit.tick();
      this.sizeZEdit.tick();
      this.integrityEdit.tick();
      this.seedEdit.tick();
      this.dataEdit.tick();
   }

   private void onDone() {
      if (this.sendToServer(StructureBlockEntity.UpdateType.UPDATE_DATA)) {
         this.minecraft.setScreen((Screen)null);
      }

   }

   private void onCancel() {
      this.structure.setMirror(this.initialMirror);
      this.structure.setRotation(this.initialRotation);
      this.structure.setMode(this.initialMode);
      this.structure.setIgnoreEntities(this.initialEntityIgnoring);
      this.structure.setShowAir(this.initialShowAir);
      this.structure.setShowBoundingBox(this.initialShowBoundingBox);
      this.minecraft.setScreen((Screen)null);
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done"), (var1x) -> {
         this.onDone();
      }));
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel"), (var1x) -> {
         this.onCancel();
      }));
      this.saveButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.save"), (var1x) -> {
         if (this.structure.getMode() == StructureMode.SAVE) {
            this.sendToServer(StructureBlockEntity.UpdateType.SAVE_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.loadButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.get("structure_block.button.load"), (var1x) -> {
         if (this.structure.getMode() == StructureMode.LOAD) {
            this.sendToServer(StructureBlockEntity.UpdateType.LOAD_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", (var1x) -> {
         this.structure.nextMode();
         this.updateMode();
      }));
      this.detectButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 120, 50, 20, I18n.get("structure_block.button.detect_size"), (var1x) -> {
         if (this.structure.getMode() == StructureMode.SAVE) {
            this.sendToServer(StructureBlockEntity.UpdateType.SCAN_AREA);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.entitiesButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", (var1x) -> {
         this.structure.setIgnoreEntities(!this.structure.isIgnoreEntities());
         this.updateEntitiesButton();
      }));
      this.mirrorButton = (Button)this.addButton(new Button(this.width / 2 - 20, 185, 40, 20, "MIRROR", (var1x) -> {
         switch(this.structure.getMirror()) {
         case NONE:
            this.structure.setMirror(Mirror.LEFT_RIGHT);
            break;
         case LEFT_RIGHT:
            this.structure.setMirror(Mirror.FRONT_BACK);
            break;
         case FRONT_BACK:
            this.structure.setMirror(Mirror.NONE);
         }

         this.updateMirrorButton();
      }));
      this.toggleAirButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", (var1x) -> {
         this.structure.setShowAir(!this.structure.getShowAir());
         this.updateToggleAirButton();
      }));
      this.toggleBoundingBox = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", (var1x) -> {
         this.structure.setShowBoundingBox(!this.structure.getShowBoundingBox());
         this.updateToggleBoundingBox();
      }));
      this.rot0Button = (Button)this.addButton(new Button(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", (var1x) -> {
         this.structure.setRotation(Rotation.NONE);
         this.updateDirectionButtons();
      }));
      this.rot90Button = (Button)this.addButton(new Button(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", (var1x) -> {
         this.structure.setRotation(Rotation.CLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.rot180Button = (Button)this.addButton(new Button(this.width / 2 + 1 + 20, 185, 40, 20, "180", (var1x) -> {
         this.structure.setRotation(Rotation.CLOCKWISE_180);
         this.updateDirectionButtons();
      }));
      this.rot270Button = (Button)this.addButton(new Button(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", (var1x) -> {
         this.structure.setRotation(Rotation.COUNTERCLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("structure_block.structure_name")) {
         public boolean charTyped(char var1, int var2) {
            return !StructureBlockEditScreen.this.isValidCharacterForName(this.getValue(), var1, this.getCursorPosition()) ? false : super.charTyped(var1, var2);
         }
      };
      this.nameEdit.setMaxLength(64);
      this.nameEdit.setValue(this.structure.getStructureName());
      this.children.add(this.nameEdit);
      BlockPos var1 = this.structure.getStructurePos();
      this.posXEdit = new EditBox(this.font, this.width / 2 - 152, 80, 80, 20, I18n.get("structure_block.position.x"));
      this.posXEdit.setMaxLength(15);
      this.posXEdit.setValue(Integer.toString(var1.getX()));
      this.children.add(this.posXEdit);
      this.posYEdit = new EditBox(this.font, this.width / 2 - 72, 80, 80, 20, I18n.get("structure_block.position.y"));
      this.posYEdit.setMaxLength(15);
      this.posYEdit.setValue(Integer.toString(var1.getY()));
      this.children.add(this.posYEdit);
      this.posZEdit = new EditBox(this.font, this.width / 2 + 8, 80, 80, 20, I18n.get("structure_block.position.z"));
      this.posZEdit.setMaxLength(15);
      this.posZEdit.setValue(Integer.toString(var1.getZ()));
      this.children.add(this.posZEdit);
      BlockPos var2 = this.structure.getStructureSize();
      this.sizeXEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.size.x"));
      this.sizeXEdit.setMaxLength(15);
      this.sizeXEdit.setValue(Integer.toString(var2.getX()));
      this.children.add(this.sizeXEdit);
      this.sizeYEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.size.y"));
      this.sizeYEdit.setMaxLength(15);
      this.sizeYEdit.setValue(Integer.toString(var2.getY()));
      this.children.add(this.sizeYEdit);
      this.sizeZEdit = new EditBox(this.font, this.width / 2 + 8, 120, 80, 20, I18n.get("structure_block.size.z"));
      this.sizeZEdit.setMaxLength(15);
      this.sizeZEdit.setValue(Integer.toString(var2.getZ()));
      this.children.add(this.sizeZEdit);
      this.integrityEdit = new EditBox(this.font, this.width / 2 - 152, 120, 80, 20, I18n.get("structure_block.integrity.integrity"));
      this.integrityEdit.setMaxLength(15);
      this.integrityEdit.setValue(this.decimalFormat.format((double)this.structure.getIntegrity()));
      this.children.add(this.integrityEdit);
      this.seedEdit = new EditBox(this.font, this.width / 2 - 72, 120, 80, 20, I18n.get("structure_block.integrity.seed"));
      this.seedEdit.setMaxLength(31);
      this.seedEdit.setValue(Long.toString(this.structure.getSeed()));
      this.children.add(this.seedEdit);
      this.dataEdit = new EditBox(this.font, this.width / 2 - 152, 120, 240, 20, I18n.get("structure_block.custom_data"));
      this.dataEdit.setMaxLength(128);
      this.dataEdit.setValue(this.structure.getMetaData());
      this.children.add(this.dataEdit);
      this.initialMirror = this.structure.getMirror();
      this.updateMirrorButton();
      this.initialRotation = this.structure.getRotation();
      this.updateDirectionButtons();
      this.initialMode = this.structure.getMode();
      this.updateMode();
      this.initialEntityIgnoring = this.structure.isIgnoreEntities();
      this.updateEntitiesButton();
      this.initialShowAir = this.structure.getShowAir();
      this.updateToggleAirButton();
      this.initialShowBoundingBox = this.structure.getShowBoundingBox();
      this.updateToggleBoundingBox();
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.nameEdit.getValue();
      String var5 = this.posXEdit.getValue();
      String var6 = this.posYEdit.getValue();
      String var7 = this.posZEdit.getValue();
      String var8 = this.sizeXEdit.getValue();
      String var9 = this.sizeYEdit.getValue();
      String var10 = this.sizeZEdit.getValue();
      String var11 = this.integrityEdit.getValue();
      String var12 = this.seedEdit.getValue();
      String var13 = this.dataEdit.getValue();
      this.init(var1, var2, var3);
      this.nameEdit.setValue(var4);
      this.posXEdit.setValue(var5);
      this.posYEdit.setValue(var6);
      this.posZEdit.setValue(var7);
      this.sizeXEdit.setValue(var8);
      this.sizeYEdit.setValue(var9);
      this.sizeZEdit.setValue(var10);
      this.integrityEdit.setValue(var11);
      this.seedEdit.setValue(var12);
      this.dataEdit.setValue(var13);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void updateEntitiesButton() {
      boolean var1 = !this.structure.isIgnoreEntities();
      if (var1) {
         this.entitiesButton.setMessage(I18n.get("options.on"));
      } else {
         this.entitiesButton.setMessage(I18n.get("options.off"));
      }

   }

   private void updateToggleAirButton() {
      boolean var1 = this.structure.getShowAir();
      if (var1) {
         this.toggleAirButton.setMessage(I18n.get("options.on"));
      } else {
         this.toggleAirButton.setMessage(I18n.get("options.off"));
      }

   }

   private void updateToggleBoundingBox() {
      boolean var1 = this.structure.getShowBoundingBox();
      if (var1) {
         this.toggleBoundingBox.setMessage(I18n.get("options.on"));
      } else {
         this.toggleBoundingBox.setMessage(I18n.get("options.off"));
      }

   }

   private void updateMirrorButton() {
      Mirror var1 = this.structure.getMirror();
      switch(var1) {
      case NONE:
         this.mirrorButton.setMessage("|");
         break;
      case LEFT_RIGHT:
         this.mirrorButton.setMessage("< >");
         break;
      case FRONT_BACK:
         this.mirrorButton.setMessage("^ v");
      }

   }

   private void updateDirectionButtons() {
      this.rot0Button.active = true;
      this.rot90Button.active = true;
      this.rot180Button.active = true;
      this.rot270Button.active = true;
      switch(this.structure.getRotation()) {
      case NONE:
         this.rot0Button.active = false;
         break;
      case CLOCKWISE_180:
         this.rot180Button.active = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.rot270Button.active = false;
         break;
      case CLOCKWISE_90:
         this.rot90Button.active = false;
      }

   }

   private void updateMode() {
      this.nameEdit.setVisible(false);
      this.posXEdit.setVisible(false);
      this.posYEdit.setVisible(false);
      this.posZEdit.setVisible(false);
      this.sizeXEdit.setVisible(false);
      this.sizeYEdit.setVisible(false);
      this.sizeZEdit.setVisible(false);
      this.integrityEdit.setVisible(false);
      this.seedEdit.setVisible(false);
      this.dataEdit.setVisible(false);
      this.saveButton.visible = false;
      this.loadButton.visible = false;
      this.detectButton.visible = false;
      this.entitiesButton.visible = false;
      this.mirrorButton.visible = false;
      this.rot0Button.visible = false;
      this.rot90Button.visible = false;
      this.rot180Button.visible = false;
      this.rot270Button.visible = false;
      this.toggleAirButton.visible = false;
      this.toggleBoundingBox.visible = false;
      switch(this.structure.getMode()) {
      case SAVE:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.sizeXEdit.setVisible(true);
         this.sizeYEdit.setVisible(true);
         this.sizeZEdit.setVisible(true);
         this.saveButton.visible = true;
         this.detectButton.visible = true;
         this.entitiesButton.visible = true;
         this.toggleAirButton.visible = true;
         break;
      case LOAD:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.integrityEdit.setVisible(true);
         this.seedEdit.setVisible(true);
         this.loadButton.visible = true;
         this.entitiesButton.visible = true;
         this.mirrorButton.visible = true;
         this.rot0Button.visible = true;
         this.rot90Button.visible = true;
         this.rot180Button.visible = true;
         this.rot270Button.visible = true;
         this.toggleBoundingBox.visible = true;
         this.updateDirectionButtons();
         break;
      case CORNER:
         this.nameEdit.setVisible(true);
         break;
      case DATA:
         this.dataEdit.setVisible(true);
      }

      this.modeButton.setMessage(I18n.get("structure_block.mode." + this.structure.getMode().getSerializedName()));
   }

   private boolean sendToServer(StructureBlockEntity.UpdateType var1) {
      BlockPos var2 = new BlockPos(this.parseCoordinate(this.posXEdit.getValue()), this.parseCoordinate(this.posYEdit.getValue()), this.parseCoordinate(this.posZEdit.getValue()));
      BlockPos var3 = new BlockPos(this.parseCoordinate(this.sizeXEdit.getValue()), this.parseCoordinate(this.sizeYEdit.getValue()), this.parseCoordinate(this.sizeZEdit.getValue()));
      float var4 = this.parseIntegrity(this.integrityEdit.getValue());
      long var5 = this.parseSeed(this.seedEdit.getValue());
      this.minecraft.getConnection().send((Packet)(new ServerboundSetStructureBlockPacket(this.structure.getBlockPos(), var1, this.structure.getMode(), this.nameEdit.getValue(), var2, var3, this.structure.getMirror(), this.structure.getRotation(), this.dataEdit.getValue(), this.structure.isIgnoreEntities(), this.structure.getShowAir(), this.structure.getShowBoundingBox(), var4, var5)));
      return true;
   }

   private long parseSeed(String var1) {
      try {
         return Long.valueOf(var1);
      } catch (NumberFormatException var3) {
         return 0L;
      }
   }

   private float parseIntegrity(String var1) {
      try {
         return Float.valueOf(var1);
      } catch (NumberFormatException var3) {
         return 1.0F;
      }
   }

   private int parseCoordinate(String var1) {
      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   public void onClose() {
      this.onCancel();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      StructureMode var4 = this.structure.getMode();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 10, 16777215);
      if (var4 != StructureMode.DATA) {
         this.drawString(this.font, I18n.get("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
         this.nameEdit.render(var1, var2, var3);
      }

      String var5;
      int var6;
      if (var4 == StructureMode.LOAD || var4 == StructureMode.SAVE) {
         this.drawString(this.font, I18n.get("structure_block.position"), this.width / 2 - 153, 70, 10526880);
         this.posXEdit.render(var1, var2, var3);
         this.posYEdit.render(var1, var2, var3);
         this.posZEdit.render(var1, var2, var3);
         var5 = I18n.get("structure_block.include_entities");
         var6 = this.font.width(var5);
         this.drawString(this.font, var5, this.width / 2 + 154 - var6, 150, 10526880);
      }

      if (var4 == StructureMode.SAVE) {
         this.drawString(this.font, I18n.get("structure_block.size"), this.width / 2 - 153, 110, 10526880);
         this.sizeXEdit.render(var1, var2, var3);
         this.sizeYEdit.render(var1, var2, var3);
         this.sizeZEdit.render(var1, var2, var3);
         var5 = I18n.get("structure_block.detect_size");
         var6 = this.font.width(var5);
         this.drawString(this.font, var5, this.width / 2 + 154 - var6, 110, 10526880);
         String var7 = I18n.get("structure_block.show_air");
         int var8 = this.font.width(var7);
         this.drawString(this.font, var7, this.width / 2 + 154 - var8, 70, 10526880);
      }

      if (var4 == StructureMode.LOAD) {
         this.drawString(this.font, I18n.get("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
         this.integrityEdit.render(var1, var2, var3);
         this.seedEdit.render(var1, var2, var3);
         var5 = I18n.get("structure_block.show_boundingbox");
         var6 = this.font.width(var5);
         this.drawString(this.font, var5, this.width / 2 + 154 - var6, 70, 10526880);
      }

      if (var4 == StructureMode.DATA) {
         this.drawString(this.font, I18n.get("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
         this.dataEdit.render(var1, var2, var3);
      }

      var5 = "structure_block.mode_info." + var4.getSerializedName();
      this.drawString(this.font, I18n.get(var5), this.width / 2 - 153, 174, 10526880);
      super.render(var1, var2, var3);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
