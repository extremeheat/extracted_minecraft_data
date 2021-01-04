package net.minecraft.client.gui.screens.worldselection;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageSource;

public class OptimizeWorldScreen extends Screen {
   private static final Object2IntMap<DimensionType> DIMENSION_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), (var0) -> {
      var0.put(DimensionType.OVERWORLD, -13408734);
      var0.put(DimensionType.NETHER, -10075085);
      var0.put(DimensionType.THE_END, -8943531);
      var0.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldUpgrader upgrader;

   public OptimizeWorldScreen(BooleanConsumer var1, String var2, LevelStorageSource var3, boolean var4) {
      super(new TranslatableComponent("optimizeWorld.title", new Object[]{var3.getDataTagFor(var2).getLevelName()}));
      this.callback = var1;
      this.upgrader = new WorldUpgrader(var2, var3, var3.getDataTagFor(var2), var4);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.get("gui.cancel"), (var1) -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }));
   }

   public void tick() {
      if (this.upgrader.isFinished()) {
         this.callback.accept(true);
      }

   }

   public void removed() {
      this.upgrader.cancel();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      int var4 = this.width / 2 - 150;
      int var5 = this.width / 2 + 150;
      int var6 = this.height / 4 + 100;
      int var7 = var6 + 10;
      Font var10001 = this.font;
      String var10002 = this.upgrader.getStatus().getColoredString();
      int var10003 = this.width / 2;
      this.font.getClass();
      this.drawCenteredString(var10001, var10002, var10003, var6 - 9 - 2, 10526880);
      if (this.upgrader.getTotalChunks() > 0) {
         fill(var4 - 1, var6 - 1, var5 + 1, var7 + 1, -16777216);
         this.drawString(this.font, I18n.get("optimizeWorld.info.converted", this.upgrader.getConverted()), var4, 40, 10526880);
         var10001 = this.font;
         var10002 = I18n.get("optimizeWorld.info.skipped", this.upgrader.getSkipped());
         this.font.getClass();
         this.drawString(var10001, var10002, var4, 40 + 9 + 3, 10526880);
         var10001 = this.font;
         var10002 = I18n.get("optimizeWorld.info.total", this.upgrader.getTotalChunks());
         this.font.getClass();
         this.drawString(var10001, var10002, var4, 40 + (9 + 3) * 2, 10526880);
         int var8 = 0;

         int var11;
         for(Iterator var9 = DimensionType.getAllTypes().iterator(); var9.hasNext(); var8 += var11) {
            DimensionType var10 = (DimensionType)var9.next();
            var11 = Mth.floor(this.upgrader.dimensionProgress(var10) * (float)(var5 - var4));
            fill(var4 + var8, var6, var4 + var8 + var11, var7, DIMENSION_COLORS.getInt(var10));
         }

         int var12 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         var10001 = this.font;
         var10002 = var12 + " / " + this.upgrader.getTotalChunks();
         var10003 = this.width / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var6 + 2 * 9 + 2, 10526880);
         var10001 = this.font;
         var10002 = Mth.floor(this.upgrader.getProgress() * 100.0F) + "%";
         var10003 = this.width / 2;
         int var10004 = var6 + (var7 - var6) / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var10004 - 9 / 2, 10526880);
      }

      super.render(var1, var2, var3);
   }
}
