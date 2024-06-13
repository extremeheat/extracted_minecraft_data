package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class OptimizeWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ToIntFunction<ResourceKey<Level>> DIMENSION_COLORS = Util.make(new Reference2IntOpenHashMap(), var0 -> {
      var0.put(Level.OVERWORLD, -13408734);
      var0.put(Level.NETHER, -10075085);
      var0.put(Level.END, -8943531);
      var0.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldUpgrader upgrader;

   @Nullable
   public static OptimizeWorldScreen create(Minecraft var0, BooleanConsumer var1, DataFixer var2, LevelStorageSource.LevelStorageAccess var3, boolean var4) {
      try {
         WorldOpenFlows var5 = var0.createWorldOpenFlows();
         PackRepository var6 = ServerPacksSource.createPackRepository(var3);

         OptimizeWorldScreen var10;
         try (WorldStem var7 = var5.loadWorldStem(var3.getDataTag(), false, var6)) {
            WorldData var8 = var7.worldData();
            RegistryAccess.Frozen var9 = var7.registries().compositeAccess();
            var3.saveDataTag(var9, var8);
            var10 = new OptimizeWorldScreen(var1, var2, var3, var8.getLevelSettings(), var4, var9);
         }

         return var10;
      } catch (Exception var13) {
         LOGGER.warn("Failed to load datapacks, can't optimize world", var13);
         return null;
      }
   }

   private OptimizeWorldScreen(
      BooleanConsumer var1, DataFixer var2, LevelStorageSource.LevelStorageAccess var3, LevelSettings var4, boolean var5, RegistryAccess var6
   ) {
      super(Component.translatable("optimizeWorld.title", var4.levelName()));
      this.callback = var1;
      this.upgrader = new WorldUpgrader(var3, var2, var6, var5, false);
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1 -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
   }

   @Override
   public void tick() {
      if (this.upgrader.isFinished()) {
         this.callback.accept(true);
      }
   }

   @Override
   public void onClose() {
      this.callback.accept(false);
   }

   @Override
   public void removed() {
      this.upgrader.cancel();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      int var5 = this.width / 2 - 150;
      int var6 = this.width / 2 + 150;
      int var7 = this.height / 4 + 100;
      int var8 = var7 + 10;
      var1.drawCenteredString(this.font, this.upgrader.getStatus(), this.width / 2, var7 - 9 - 2, 10526880);
      if (this.upgrader.getTotalChunks() > 0) {
         var1.fill(var5 - 1, var7 - 1, var6 + 1, var8 + 1, -16777216);
         var1.drawString(this.font, Component.translatable("optimizeWorld.info.converted", this.upgrader.getConverted()), var5, 40, 10526880);
         var1.drawString(this.font, Component.translatable("optimizeWorld.info.skipped", this.upgrader.getSkipped()), var5, 40 + 9 + 3, 10526880);
         var1.drawString(this.font, Component.translatable("optimizeWorld.info.total", this.upgrader.getTotalChunks()), var5, 40 + (9 + 3) * 2, 10526880);
         int var9 = 0;

         for (ResourceKey var11 : this.upgrader.levels()) {
            int var12 = Mth.floor(this.upgrader.dimensionProgress(var11) * (float)(var6 - var5));
            var1.fill(var5 + var9, var7, var5 + var9 + var12, var8, DIMENSION_COLORS.applyAsInt(var11));
            var9 += var12;
         }

         int var13 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         MutableComponent var14 = Component.translatable("optimizeWorld.progress.counter", var13, this.upgrader.getTotalChunks());
         MutableComponent var15 = Component.translatable("optimizeWorld.progress.percentage", Mth.floor(this.upgrader.getProgress() * 100.0F));
         var1.drawCenteredString(this.font, var14, this.width / 2, var7 + 2 * 9 + 2, 10526880);
         var1.drawCenteredString(this.font, var15, this.width / 2, var7 + (var8 - var7) / 2 - 9 / 2, 10526880);
      }
   }
}
