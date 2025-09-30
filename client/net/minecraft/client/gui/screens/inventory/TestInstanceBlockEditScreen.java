package net.minecraft.client.gui.screens.inventory;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundTestInstanceBlockActionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;

public class TestInstanceBlockEditScreen extends Screen {
   private static final Component ID_LABEL = Component.translatable("test_instance_block.test_id");
   private static final Component SIZE_LABEL = Component.translatable("test_instance_block.size");
   private static final Component INCLUDE_ENTITIES_LABEL = Component.translatable("test_instance_block.entities");
   private static final Component ROTATION_LABEL = Component.translatable("test_instance_block.rotation");
   private static final int BUTTON_PADDING = 8;
   private static final int WIDTH = 316;
   private final TestInstanceBlockEntity blockEntity;
   @Nullable
   private EditBox idEdit;
   @Nullable
   private EditBox sizeXEdit;
   @Nullable
   private EditBox sizeYEdit;
   @Nullable
   private EditBox sizeZEdit;
   @Nullable
   private FittingMultiLineTextWidget infoWidget;
   @Nullable
   private Button saveButton;
   @Nullable
   private Button exportButton;
   @Nullable
   private CycleButton<Boolean> includeEntitiesButton;
   @Nullable
   private CycleButton<Rotation> rotationButton;

   public TestInstanceBlockEditScreen(TestInstanceBlockEntity var1) {
      super(var1.getBlockState().getBlock().getName());
      this.blockEntity = var1;
   }

   protected void init() {
      int var1 = this.width / 2 - 158;
      boolean var2 = SharedConstants.IS_RUNNING_IN_IDE;
      int var3 = var2 ? 3 : 2;
      int var4 = widgetSize(var3);
      this.idEdit = new EditBox(this.font, var1, 40, 316, 20, Component.translatable("test_instance_block.test_id"));
      this.idEdit.setMaxLength(128);
      Optional var5 = this.blockEntity.test();
      if (var5.isPresent()) {
         this.idEdit.setValue(((ResourceKey)var5.get()).location().toString());
      }

      this.idEdit.setResponder((var1x) -> this.updateTestInfo(false));
      this.addRenderableWidget(this.idEdit);
      Objects.requireNonNull(this.font);
      this.infoWidget = new FittingMultiLineTextWidget(var1, 70, 316, 8 * 9, Component.literal(""), this.font);
      this.addRenderableWidget(this.infoWidget);
      Vec3i var6 = this.blockEntity.getSize();
      int var7 = 0;
      this.sizeXEdit = new EditBox(this.font, this.widgetX(var7++, 5), 160, widgetSize(5), 20, Component.translatable("structure_block.size.x"));
      this.sizeXEdit.setMaxLength(15);
      this.addRenderableWidget(this.sizeXEdit);
      this.sizeYEdit = new EditBox(this.font, this.widgetX(var7++, 5), 160, widgetSize(5), 20, Component.translatable("structure_block.size.y"));
      this.sizeYEdit.setMaxLength(15);
      this.addRenderableWidget(this.sizeYEdit);
      this.sizeZEdit = new EditBox(this.font, this.widgetX(var7++, 5), 160, widgetSize(5), 20, Component.translatable("structure_block.size.z"));
      this.sizeZEdit.setMaxLength(15);
      this.addRenderableWidget(this.sizeZEdit);
      this.setSize(var6);
      this.rotationButton = (CycleButton)this.addRenderableWidget(CycleButton.builder(TestInstanceBlockEditScreen::rotationDisplay).withValues(Rotation.values()).withInitialValue(this.blockEntity.getRotation()).displayOnlyValue().create(this.widgetX(var7++, 5), 160, widgetSize(5), 20, ROTATION_LABEL, (var1x, var2x) -> this.updateSaveState()));
      this.includeEntitiesButton = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(!this.blockEntity.ignoreEntities()).displayOnlyValue().create(this.widgetX(var7++, 5), 160, widgetSize(5), 20, INCLUDE_ENTITIES_LABEL));
      var7 = 0;
      this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.reset"), (var1x) -> {
         this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.RESET);
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.widgetX(var7++, var3), 185, var4, 20).build());
      this.saveButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.save"), (var1x) -> {
         this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.SAVE);
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.widgetX(var7++, var3), 185, var4, 20).build());
      if (var2) {
         this.exportButton = (Button)this.addRenderableWidget(Button.builder(Component.literal("Export Structure"), (var1x) -> {
            this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.EXPORT);
            this.minecraft.setScreen((Screen)null);
         }).bounds(this.widgetX(var7++, var3), 185, var4, 20).build());
      }

      this.addRenderableWidget(Button.builder(Component.translatable("test_instance.action.run"), (var1x) -> {
         this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.RUN);
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.widgetX(0, 3), 210, widgetSize(3), 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onDone()).bounds(this.widgetX(1, 3), 210, widgetSize(3), 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onCancel()).bounds(this.widgetX(2, 3), 210, widgetSize(3), 20).build());
      this.updateTestInfo(true);
   }

   private void updateSaveState() {
      boolean var1 = this.rotationButton.getValue() == Rotation.NONE && ResourceLocation.tryParse(this.idEdit.getValue()) != null;
      this.saveButton.active = var1;
      if (this.exportButton != null) {
         this.exportButton.active = var1;
      }

   }

   private static Component rotationDisplay(Rotation var0) {
      String var10000;
      switch (var0) {
         case NONE -> var10000 = "0";
         case CLOCKWISE_90 -> var10000 = "90";
         case CLOCKWISE_180 -> var10000 = "180";
         case COUNTERCLOCKWISE_90 -> var10000 = "270";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return Component.literal(var10000);
   }

   private void setSize(Vec3i var1) {
      this.sizeXEdit.setValue(Integer.toString(var1.getX()));
      this.sizeYEdit.setValue(Integer.toString(var1.getY()));
      this.sizeZEdit.setValue(Integer.toString(var1.getZ()));
   }

   private int widgetX(int var1, int var2) {
      int var3 = this.width / 2 - 158;
      float var4 = exactWidgetSize(var2);
      return (int)((float)var3 + (float)var1 * (8.0F + var4));
   }

   private static int widgetSize(int var0) {
      return (int)exactWidgetSize(var0);
   }

   private static float exactWidgetSize(int var0) {
      return (float)(316 - (var0 - 1) * 8) / (float)var0;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = this.width / 2 - 158;
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, -1);
      var1.drawString(this.font, (Component)ID_LABEL, var5, 30, -6250336);
      var1.drawString(this.font, (Component)SIZE_LABEL, var5, 150, -6250336);
      var1.drawString(this.font, (Component)ROTATION_LABEL, this.rotationButton.getX(), 150, -6250336);
      var1.drawString(this.font, (Component)INCLUDE_ENTITIES_LABEL, this.includeEntitiesButton.getX(), 150, -6250336);
   }

   private void updateTestInfo(boolean var1) {
      boolean var2 = this.sendToServer(var1 ? ServerboundTestInstanceBlockActionPacket.Action.INIT : ServerboundTestInstanceBlockActionPacket.Action.QUERY);
      if (!var2) {
         this.infoWidget.setMessage(Component.translatable("test_instance.description.invalid_id").withStyle(ChatFormatting.RED));
      }

      this.updateSaveState();
   }

   private void onDone() {
      this.sendToServer(ServerboundTestInstanceBlockActionPacket.Action.SET);
      this.onClose();
   }

   private boolean sendToServer(ServerboundTestInstanceBlockActionPacket.Action var1) {
      Optional var2 = Optional.ofNullable(ResourceLocation.tryParse(this.idEdit.getValue()));
      Optional var3 = var2.map((var0) -> ResourceKey.create(Registries.TEST_INSTANCE, var0));
      Vec3i var4 = new Vec3i(parseSize(this.sizeXEdit.getValue()), parseSize(this.sizeYEdit.getValue()), parseSize(this.sizeZEdit.getValue()));
      boolean var5 = !(Boolean)this.includeEntitiesButton.getValue();
      this.minecraft.getConnection().send(new ServerboundTestInstanceBlockActionPacket(this.blockEntity.getBlockPos(), var1, var3, var4, this.rotationButton.getValue(), var5));
      return var2.isPresent();
   }

   public void setStatus(Component var1, Optional<Vec3i> var2) {
      MutableComponent var3 = Component.empty();
      this.blockEntity.errorMessage().ifPresent((var1x) -> var3.append((Component)Component.translatable("test_instance.description.failed", Component.empty().withStyle(ChatFormatting.RED).append(var1x))).append("\n\n"));
      var3.append(var1);
      this.infoWidget.setMessage(var3);
      var2.ifPresent(this::setSize);
   }

   private void onCancel() {
      this.onClose();
   }

   private static int parseSize(String var0) {
      try {
         return Mth.clamp(Integer.parseInt(var0), 1, 48);
      } catch (NumberFormatException var2) {
         return 1;
      }
   }

   public boolean isInGameUi() {
      return true;
   }
}
