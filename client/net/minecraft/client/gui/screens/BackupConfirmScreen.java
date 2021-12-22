package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class BackupConfirmScreen extends Screen {
   @Nullable
   private final Screen lastScreen;
   protected final BackupConfirmScreen.Listener listener;
   private final Component description;
   private final boolean promptForCacheErase;
   private MultiLineLabel message;
   // $FF: renamed from: id int
   protected int field_30;
   private Checkbox eraseCache;

   public BackupConfirmScreen(@Nullable Screen var1, BackupConfirmScreen.Listener var2, Component var3, Component var4, boolean var5) {
      super(var3);
      this.message = MultiLineLabel.EMPTY;
      this.lastScreen = var1;
      this.listener = var2;
      this.description = var4;
      this.promptForCacheErase = var5;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
      int var10000 = this.message.getLineCount() + 1;
      Objects.requireNonNull(this.font);
      int var1 = var10000 * 9;
      this.addRenderableWidget(new Button(this.width / 2 - 155, 100 + var1, 150, 20, new TranslatableComponent("selectWorld.backupJoinConfirmButton"), (var1x) -> {
         this.listener.proceed(true, this.eraseCache.selected());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, 100 + var1, 150, 20, new TranslatableComponent("selectWorld.backupJoinSkipButton"), (var1x) -> {
         this.listener.proceed(false, this.eraseCache.selected());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 80, 124 + var1, 150, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + var1, 150, 20, new TranslatableComponent("selectWorld.backupEraseCache"), false);
      if (this.promptForCacheErase) {
         this.addRenderableWidget(this.eraseCache);
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 50, 16777215);
      this.message.renderCentered(var1, this.width / 2, 70);
      super.render(var1, var2, var3, var4);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public interface Listener {
      void proceed(boolean var1, boolean var2);
   }
}
