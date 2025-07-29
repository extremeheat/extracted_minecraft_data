package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
   public static final ResourceLocation MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_background.png");
   public static final ResourceLocation HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/header_separator.png");
   public static final ResourceLocation FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/footer_separator.png");
   private static final ResourceLocation INWORLD_MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png");
   public static final ResourceLocation INWORLD_HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_header_separator.png");
   public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_footer_separator.png");
   protected static final float FADE_IN_TIME = 2000.0F;
   protected final Component title;
   private final List<GuiEventListener> children = Lists.newArrayList();
   private final List<NarratableEntry> narratables = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   private boolean initialized;
   public int width;
   public int height;
   private final List<Renderable> renderables = Lists.newArrayList();
   protected Font font;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_NARRATOR_ENABLED;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
   private long narrationSuppressTime = -9223372036854775808L;
   private long nextNarrationTime = 9223372036854775807L;
   @Nullable
   protected CycleButton<NarratorStatus> narratorButton;
   @Nullable
   private NarratableEntry lastNarratable;
   protected final Executor screenExecutor = (var1x) -> this.minecraft.execute(() -> {
         if (this.minecraft.screen == this) {
            var1x.run();
         }

      });

   protected Screen(Component var1) {
      super();
      this.title = var1;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getNarrationMessage() {
      return this.getTitle();
   }

   public final void renderWithTooltipAndSubtitles(GuiGraphics var1, int var2, int var3, float var4) {
      var1.nextStratum();
      this.renderBackground(var1, var2, var3, var4);
      var1.nextStratum();
      this.render(var1, var2, var3, var4);
      this.minecraft.gui.renderDeferredSubtitles();
      var1.renderDeferredTooltip();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      for(Renderable var6 : this.renderables) {
         var6.render(var1, var2, var3, var4);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         Object var10000;
         switch (var1) {
            case 258:
               var10000 = this.createTabEvent();
               break;
            case 259:
            case 260:
            case 261:
            default:
               var10000 = null;
               break;
            case 262:
               var10000 = this.createArrowEvent(ScreenDirection.RIGHT);
               break;
            case 263:
               var10000 = this.createArrowEvent(ScreenDirection.LEFT);
               break;
            case 264:
               var10000 = this.createArrowEvent(ScreenDirection.DOWN);
               break;
            case 265:
               var10000 = this.createArrowEvent(ScreenDirection.UP);
         }

         Object var4 = var10000;
         if (var4 != null) {
            ComponentPath var5 = super.nextFocusPath((FocusNavigationEvent)var4);
            if (var5 == null && var4 instanceof FocusNavigationEvent.TabNavigation) {
               this.clearFocus();
               var5 = super.nextFocusPath((FocusNavigationEvent)var4);
            }

            if (var5 != null) {
               this.changeFocus(var5);
            }
         }

         return false;
      }
   }

   private FocusNavigationEvent.TabNavigation createTabEvent() {
      boolean var1 = !hasShiftDown();
      return new FocusNavigationEvent.TabNavigation(var1);
   }

   private FocusNavigationEvent.ArrowNavigation createArrowEvent(ScreenDirection var1) {
      return new FocusNavigationEvent.ArrowNavigation(var1);
   }

   protected void setInitialFocus() {
      if (this.minecraft.getLastInputType().isKeyboard()) {
         FocusNavigationEvent.TabNavigation var1 = new FocusNavigationEvent.TabNavigation(true);
         ComponentPath var2 = super.nextFocusPath(var1);
         if (var2 != null) {
            this.changeFocus(var2);
         }
      }

   }

   protected void setInitialFocus(GuiEventListener var1) {
      ComponentPath var2 = ComponentPath.path((ContainerEventHandler)this, (ComponentPath)var1.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
      if (var2 != null) {
         this.changeFocus(var2);
      }

   }

   public void clearFocus() {
      ComponentPath var1 = this.getCurrentFocusPath();
      if (var1 != null) {
         var1.applyFocus(false);
      }

   }

   @VisibleForTesting
   protected void changeFocus(ComponentPath var1) {
      this.clearFocus();
      var1.applyFocus(true);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.setScreen((Screen)null);
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T var1) {
      this.renderables.add((Renderable)var1);
      return (T)this.addWidget(var1);
   }

   protected <T extends Renderable> T addRenderableOnly(T var1) {
      this.renderables.add(var1);
      return (T)var1;
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T var1) {
      this.children.add(var1);
      this.narratables.add((NarratableEntry)var1);
      return (T)var1;
   }

   protected void removeWidget(GuiEventListener var1) {
      if (var1 instanceof Renderable) {
         this.renderables.remove((Renderable)var1);
      }

      if (var1 instanceof NarratableEntry) {
         this.narratables.remove((NarratableEntry)var1);
      }

      this.children.remove(var1);
   }

   protected void clearWidgets() {
      this.renderables.clear();
      this.children.clear();
      this.narratables.clear();
   }

   public static List<Component> getTooltipFromItem(Minecraft var0, ItemStack var1) {
      return var1.getTooltipLines(Item.TooltipContext.of((Level)var0.level), var0.player, var0.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   protected void insertText(String var1, boolean var2) {
   }

   public boolean handleComponentClicked(Style var1) {
      ClickEvent var2 = var1.getClickEvent();
      if (hasShiftDown()) {
         if (var1.getInsertion() != null) {
            this.insertText(var1.getInsertion(), false);
         }
      } else if (var2 != null) {
         this.handleClickEvent(this.minecraft, var2);
         return true;
      }

      return false;
   }

   protected void handleClickEvent(Minecraft var1, ClickEvent var2) {
      defaultHandleGameClickEvent(var2, var1, this);
   }

   protected static void defaultHandleGameClickEvent(ClickEvent var0, Minecraft var1, @Nullable Screen var2) {
      LocalPlayer var3 = (LocalPlayer)Objects.requireNonNull(var1.player, "Player not available");
      Objects.requireNonNull(var0);
      byte var5 = 0;
      //$FF: var5->value
      //0->net/minecraft/network/chat/ClickEvent$RunCommand
      //1->net/minecraft/network/chat/ClickEvent$ShowDialog
      //2->net/minecraft/network/chat/ClickEvent$Custom
      switch (var0.typeSwitch<invokedynamic>(var0, var5)) {
         case 0:
            ClickEvent.RunCommand var6 = (ClickEvent.RunCommand)var0;
            ClickEvent.RunCommand var10000 = var6;

            try {
               var12 = var10000.command();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            String var11 = var12;
            clickCommandAction(var3, var11, var2);
            break;
         case 1:
            ClickEvent.ShowDialog var8 = (ClickEvent.ShowDialog)var0;
            var3.connection.showDialog(var8.dialog(), var2);
            break;
         case 2:
            ClickEvent.Custom var9 = (ClickEvent.Custom)var0;
            var3.connection.send(new ServerboundCustomClickActionPacket(var9.id(), var9.payload()));
            if (var1.screen != var2) {
               var1.setScreen(var2);
            }
            break;
         default:
            defaultHandleClickEvent(var0, var1, var2);
      }

   }

   protected static void defaultHandleClickEvent(ClickEvent var0, Minecraft var1, @Nullable Screen var2) {
      Objects.requireNonNull(var0);
      byte var5 = 0;
      boolean var20;
      //$FF: var5->value
      //0->net/minecraft/network/chat/ClickEvent$OpenUrl
      //1->net/minecraft/network/chat/ClickEvent$OpenFile
      //2->net/minecraft/network/chat/ClickEvent$SuggestCommand
      //3->net/minecraft/network/chat/ClickEvent$CopyToClipboard
      switch (var0.typeSwitch<invokedynamic>(var0, var5)) {
         case 0:
            ClickEvent.OpenUrl var6 = (ClickEvent.OpenUrl)var0;
            ClickEvent.OpenUrl var23 = var6;

            try {
               var24 = var23.uri();
            } catch (Throwable var16) {
               throw new MatchException(var16.toString(), var16);
            }

            URI var17 = var24;
            clickUrlAction(var1, var2, var17);
            var20 = false;
            break;
         case 1:
            ClickEvent.OpenFile var8 = (ClickEvent.OpenFile)var0;
            Util.getPlatform().openFile(var8.file());
            var20 = true;
            break;
         case 2:
            ClickEvent.SuggestCommand var9 = (ClickEvent.SuggestCommand)var0;
            ClickEvent.SuggestCommand var21 = var9;

            try {
               var22 = var21.command();
            } catch (Throwable var15) {
               throw new MatchException(var15.toString(), var15);
            }

            String var18 = var22;
            if (var2 != null) {
               var2.insertText(var18, true);
            }

            var20 = true;
            break;
         case 3:
            ClickEvent.CopyToClipboard var11 = (ClickEvent.CopyToClipboard)var0;
            ClickEvent.CopyToClipboard var10000 = var11;

            try {
               var19 = var10000.value();
            } catch (Throwable var14) {
               throw new MatchException(var14.toString(), var14);
            }

            String var13 = var19;
            var1.keyboardHandler.setClipboard(var13);
            var20 = true;
            break;
         default:
            LOGGER.error("Don't know how to handle {}", var0);
            var20 = true;
      }

      boolean var3 = var20;
      if (var3 && var1.screen != var2) {
         var1.setScreen(var2);
      }

   }

   protected static boolean clickUrlAction(Minecraft var0, @Nullable Screen var1, URI var2) {
      if (!(Boolean)var0.options.chatLinks().get()) {
         return false;
      } else {
         if ((Boolean)var0.options.chatLinksPrompt().get()) {
            var0.setScreen(new ConfirmLinkScreen((var3) -> {
               if (var3) {
                  Util.getPlatform().openUri(var2);
               }

               var0.setScreen(var1);
            }, var2.toString(), false));
         } else {
            Util.getPlatform().openUri(var2);
         }

         return true;
      }
   }

   protected static void clickCommandAction(LocalPlayer var0, String var1, @Nullable Screen var2) {
      var0.connection.sendUnattendedCommand(Commands.trimOptionalPrefix(var1), var2);
   }

   public final void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
      this.font = var1.font;
      this.width = var2;
      this.height = var3;
      if (!this.initialized) {
         this.init();
         this.setInitialFocus();
      } else {
         this.repositionElements();
      }

      this.initialized = true;
      this.triggerImmediateNarration(false);
      if (var1.getLastInputType().isKeyboard()) {
         this.setNarrationSuppressTime(9223372036854775807L);
      } else {
         this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
      }

   }

   protected void rebuildWidgets() {
      this.clearWidgets();
      this.clearFocus();
      this.init();
      this.setInitialFocus();
   }

   protected void fadeWidgets(float var1) {
      for(GuiEventListener var3 : this.children()) {
         if (var3 instanceof AbstractWidget var4) {
            var4.setAlpha(var1);
         }
      }

   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void added() {
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.minecraft.level == null) {
         this.renderPanorama(var1, var4);
      }

      this.renderBlurredBackground(var1);
      this.renderMenuBackground(var1);
   }

   protected void renderBlurredBackground(GuiGraphics var1) {
      float var2 = (float)this.minecraft.options.getMenuBackgroundBlurriness();
      if (var2 >= 1.0F) {
         var1.blurBeforeThisStratum();
      }

   }

   protected void renderPanorama(GuiGraphics var1, float var2) {
      this.minecraft.gameRenderer.getPanorama().render(var1, this.width, this.height, this.panoramaShouldSpin());
   }

   protected void renderMenuBackground(GuiGraphics var1) {
      this.renderMenuBackground(var1, 0, 0, this.width, this.height);
   }

   protected void renderMenuBackground(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      renderMenuBackgroundTexture(var1, this.minecraft.level == null ? MENU_BACKGROUND : INWORLD_MENU_BACKGROUND, var2, var3, 0.0F, 0.0F, var4, var5);
   }

   public static void renderMenuBackgroundTexture(GuiGraphics var0, ResourceLocation var1, int var2, int var3, float var4, float var5, int var6, int var7) {
      boolean var8 = true;
      var0.blit(RenderPipelines.GUI_TEXTURED, var1, var2, var3, var4, var5, var6, var7, 32, 32);
   }

   public void renderTransparentBackground(GuiGraphics var1) {
      var1.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
   }

   public boolean isPauseScreen() {
      return true;
   }

   protected boolean panoramaShouldSpin() {
      return true;
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isCut(int var0) {
      return var0 == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int var0) {
      return var0 == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int var0) {
      return var0 == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int var0) {
      return var0 == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   protected void repositionElements() {
      this.rebuildWidgets();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      this.width = var2;
      this.height = var3;
      this.repositionElements();
   }

   public void fillCrashDetails(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Affected screen", 1);
      var2.setDetail("Screen name", (CrashReportDetail)(() -> this.getClass().getCanonicalName()));
   }

   protected boolean isValidCharacterForName(String var1, char var2, int var3) {
      int var4 = var1.indexOf(58);
      int var5 = var1.indexOf(47);
      if (var2 == ':') {
         return (var5 == -1 || var3 <= var5) && var4 == -1;
      } else if (var2 == '/') {
         return var3 > var4;
      } else {
         return var2 == '_' || var2 == '-' || var2 >= 'a' && var2 <= 'z' || var2 >= '0' && var2 <= '9' || var2 == '.';
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return true;
   }

   public void onFilesDrop(List<Path> var1) {
   }

   private void scheduleNarration(long var1, boolean var3) {
      this.nextNarrationTime = Util.getMillis() + var1;
      if (var3) {
         this.narrationSuppressTime = -9223372036854775808L;
      }

   }

   private void suppressNarration(long var1) {
      this.setNarrationSuppressTime(Util.getMillis() + var1);
   }

   private void setNarrationSuppressTime(long var1) {
      this.narrationSuppressTime = var1;
   }

   public void afterMouseMove() {
      this.scheduleNarration(750L, false);
   }

   public void afterMouseAction() {
      this.scheduleNarration(200L, true);
   }

   public void afterKeyboardAction() {
      this.scheduleNarration(200L, true);
   }

   private boolean shouldRunNarration() {
      return this.minecraft.getNarrator().isActive();
   }

   public void handleDelayedNarration() {
      if (this.shouldRunNarration()) {
         long var1 = Util.getMillis();
         if (var1 > this.nextNarrationTime && var1 > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = 9223372036854775807L;
         }
      }

   }

   public void triggerImmediateNarration(boolean var1) {
      if (this.shouldRunNarration()) {
         this.runNarration(var1);
      }

   }

   private void runNarration(boolean var1) {
      this.narrationState.update(this::updateNarrationState);
      String var2 = this.narrationState.collectNarrationText(!var1);
      if (!var2.isEmpty()) {
         this.minecraft.getNarrator().saySystemNow(var2);
      }

   }

   protected boolean shouldNarrateNavigation() {
      return true;
   }

   protected void updateNarrationState(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getNarrationMessage());
      if (this.shouldNarrateNavigation()) {
         var1.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

      this.updateNarratedWidget(var1);
   }

   protected void updateNarratedWidget(NarrationElementOutput var1) {
      List var2 = this.narratables.stream().flatMap((var0) -> var0.getNarratables().stream()).filter(NarratableEntry::isActive).sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup)).toList();
      NarratableSearchResult var3 = findNarratableWidget(var2, this.lastNarratable);
      if (var3 != null) {
         if (var3.priority.isTerminal()) {
            this.lastNarratable = var3.entry;
         }

         if (var2.size() > 1) {
            var1.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.screen", var3.index + 1, var2.size()));
            if (var3.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               var1.add(NarratedElementType.USAGE, this.getUsageNarration());
            }
         }

         var3.entry.updateNarration(var1.nest());
      }

   }

   protected Component getUsageNarration() {
      return Component.translatable("narration.component_list.usage");
   }

   @Nullable
   public static NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> var0, @Nullable NarratableEntry var1) {
      NarratableSearchResult var2 = null;
      NarratableSearchResult var3 = null;
      int var4 = 0;

      for(int var5 = var0.size(); var4 < var5; ++var4) {
         NarratableEntry var6 = (NarratableEntry)var0.get(var4);
         NarratableEntry.NarrationPriority var7 = var6.narrationPriority();
         if (var7.isTerminal()) {
            if (var6 != var1) {
               return new NarratableSearchResult(var6, var4, var7);
            }

            var3 = new NarratableSearchResult(var6, var4, var7);
         } else if (var7.compareTo(var2 != null ? var2.priority : NarratableEntry.NarrationPriority.NONE) > 0) {
            var2 = new NarratableSearchResult(var6, var4, var7);
         }
      }

      return var2 != null ? var2 : var3;
   }

   public void updateNarratorStatus(boolean var1) {
      if (var1) {
         this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
      }

      if (this.narratorButton != null) {
         this.narratorButton.setValue((NarratorStatus)this.minecraft.options.narrator().get());
      }

   }

   public Font getFont() {
      return this.font;
   }

   public boolean showsActiveEffects() {
      return false;
   }

   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(0, 0, this.width, this.height);
   }

   @Nullable
   public Music getBackgroundMusic() {
      return null;
   }

   static {
      NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
      NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
   }

   public static record NarratableSearchResult(NarratableEntry entry, int index, NarratableEntry.NarrationPriority priority) {
      final NarratableEntry entry;
      final int index;
      final NarratableEntry.NarrationPriority priority;

      public NarratableSearchResult(NarratableEntry var1, int var2, NarratableEntry.NarrationPriority var3) {
         super();
         this.entry = var1;
         this.index = var2;
         this.priority = var3;
      }
   }
}
