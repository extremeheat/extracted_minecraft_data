package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BackupConfirmScreen extends Screen {
   private static final Component SKIP_AND_JOIN = Component.translatable("selectWorld.backupJoinSkipButton");
   public static final Component BACKUP_AND_JOIN = Component.translatable("selectWorld.backupJoinConfirmButton");
   private final Runnable onCancel;
   protected final Listener onProceed;
   private final Component description;
   private final boolean promptForCacheErase;
   private MultiLineLabel message;
   final Component confirmation;
   protected int id;
   private Checkbox eraseCache;

   public BackupConfirmScreen(Runnable var1, Listener var2, Component var3, Component var4, boolean var5) {
      this(var1, var2, var3, var4, BACKUP_AND_JOIN, var5);
   }

   public BackupConfirmScreen(Runnable var1, Listener var2, Component var3, Component var4, Component var5, boolean var6) {
      super(var3);
      this.message = MultiLineLabel.EMPTY;
      this.onCancel = var1;
      this.onProceed = var2;
      this.description = var4;
      this.promptForCacheErase = var6;
      this.confirmation = var5;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
      int var10000 = this.message.getLineCount() + 1;
      Objects.requireNonNull(this.font);
      int var1 = var10000 * 9;
      this.eraseCache = Checkbox.builder(Component.translatable("selectWorld.backupEraseCache"), this.font).pos(this.width / 2 - 155 + 80, 76 + var1).build();
      if (this.promptForCacheErase) {
         this.addRenderableWidget(this.eraseCache);
      }

      this.addRenderableWidget(Button.builder(this.confirmation, (var1x) -> this.onProceed.proceed(true, this.eraseCache.selected())).bounds(this.width / 2 - 155, 100 + var1, 150, 20).build());
      this.addRenderableWidget(Button.builder(SKIP_AND_JOIN, (var1x) -> this.onProceed.proceed(false, this.eraseCache.selected())).bounds(this.width / 2 - 155 + 160, 100 + var1, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onCancel.run()).bounds(this.width / 2 - 155 + 80, 124 + var1, 150, 20).build());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 50, 16777215);
      this.message.renderCentered(var1, this.width / 2, 70);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.onCancel.run();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public interface Listener {
      void proceed(boolean var1, boolean var2);
   }
}
