package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component SCREEN_USAGE_NARRATION = Component.translatable("narrator.screen.usage");
   public static final Identifier MENU_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_background.png");
   public static final Identifier HEADER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/header_separator.png");
   public static final Identifier FOOTER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/footer_separator.png");
   private static final Identifier INWORLD_MENU_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_background.png");
   public static final Identifier INWORLD_HEADER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/inworld_header_separator.png");
   public static final Identifier INWORLD_FOOTER_SEPARATOR = Identifier.withDefaultNamespace("textures/gui/inworld_footer_separator.png");
   protected static final float FADE_IN_TIME = 2000.0F;
   protected final Component title;
   private final List<GuiEventListener> children;
   private final List<NarratableEntry> narratables;
   protected final Minecraft minecraft;
   private boolean initialized;
   public int width;
   public int height;
   private final List<Renderable> renderables;
   protected final Font font;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_NARRATOR_ENABLED;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState;
   private long narrationSuppressTime;
   private long nextNarrationTime;
   protected @Nullable CycleButton<NarratorStatus> narratorButton;
   private @Nullable NarratableEntry lastNarratable;
   protected final Executor screenExecutor;

   protected Screen(final Component title) {
      this(Minecraft.getInstance(), Minecraft.getInstance().font, title);
   }

   protected Screen(final Minecraft minecraft, final Font font, final Component title) {
      super();
      this.children = Lists.newArrayList();
      this.narratables = Lists.newArrayList();
      this.renderables = Lists.newArrayList();
      this.narrationState = new ScreenNarrationCollector();
      this.narrationSuppressTime = -9223372036854775808L;
      this.nextNarrationTime = 9223372036854775807L;
      this.minecraft = minecraft;
      this.font = font;
      this.title = title;
      this.screenExecutor = (runnable) -> minecraft.execute(() -> {
            if (minecraft.gui.screen() == this) {
               runnable.run();
            }

         });
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getNarrationMessage() {
      return this.getTitle();
   }

   public final void extractRenderStateWithTooltipAndSubtitles(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.nextStratum();
      this.extractBackground(graphics, mouseX, mouseY, a);
      graphics.nextStratum();
      this.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.extractDeferredElements(mouseX, mouseY, a);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      for(Renderable renderable : this.renderables) {
         renderable.extractRenderState(graphics, mouseX, mouseY, a);
      }

   }

   public boolean keyPressed(final KeyEvent event) {
      if (event.isEscape() && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (super.keyPressed(event)) {
         return true;
      } else {
         Object var10000;
         switch (event.key()) {
            case 258:
               var10000 = this.createTabEvent(!event.hasShiftDown());
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

         FocusNavigationEvent navigationEvent = (FocusNavigationEvent)var10000;
         if (navigationEvent != null) {
            ComponentPath focusPath = super.nextFocusPath(navigationEvent);
            if (focusPath == null && navigationEvent instanceof FocusNavigationEvent.TabNavigation) {
               this.clearFocus();
               focusPath = super.nextFocusPath(navigationEvent);
            }

            if (focusPath != null) {
               this.changeFocus(focusPath);
            }
         }

         return false;
      }
   }

   private FocusNavigationEvent.TabNavigation createTabEvent(final boolean forward) {
      return new FocusNavigationEvent.TabNavigation(forward);
   }

   private FocusNavigationEvent.ArrowNavigation createArrowEvent(final ScreenDirection direction) {
      return new FocusNavigationEvent.ArrowNavigation(direction);
   }

   protected void setInitialFocus() {
      if (this.minecraft.getLastInputType().isKeyboard()) {
         FocusNavigationEvent.TabNavigation forwardTabEvent = new FocusNavigationEvent.TabNavigation(true);
         ComponentPath focusPath = super.nextFocusPath(forwardTabEvent);
         if (focusPath != null) {
            this.changeFocus(focusPath);
         }
      }

   }

   protected void setInitialFocus(final GuiEventListener target) {
      ComponentPath path = ComponentPath.path((ContainerEventHandler)this, (ComponentPath)target.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
      if (path != null) {
         this.changeFocus(path);
      }

   }

   public void clearFocus() {
      ComponentPath componentPath = this.getCurrentFocusPath();
      if (componentPath != null) {
         componentPath.applyFocus(false);
      }

   }

   @VisibleForTesting
   protected void changeFocus(final ComponentPath componentPath) {
      this.clearFocus();
      componentPath.applyFocus(true);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.gui.setScreen((Screen)null);
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(final T widget) {
      this.renderables.add(widget);
      return (T)this.addWidget(widget);
   }

   protected <T extends Renderable> T addRenderableOnly(final T renderable) {
      this.renderables.add(renderable);
      return renderable;
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(final T widget) {
      this.children.add(widget);
      this.narratables.add(widget);
      return widget;
   }

   protected void removeWidget(final GuiEventListener widget) {
      if (widget instanceof Renderable renderable) {
         this.renderables.remove(renderable);
      }

      if (widget instanceof NarratableEntry narratableEntry) {
         this.narratables.remove(narratableEntry);
      }

      if (this.getFocused() == widget) {
         this.clearFocus();
      }

      this.children.remove(widget);
   }

   protected void clearWidgets() {
      this.renderables.clear();
      this.children.clear();
      this.narratables.clear();
   }

   public static List<Component> getTooltipFromItem(final Minecraft minecraft, final ItemStack itemStack) {
      return itemStack.getTooltipLines(Item.TooltipContext.of((Level)minecraft.level), minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   protected void insertText(final String text, final boolean replace) {
   }

   protected static void defaultHandleGameClickEvent(final ClickEvent event, final Minecraft minecraft, final @Nullable Screen activeScreen) {
      LocalPlayer player = (LocalPlayer)Objects.requireNonNull(minecraft.player, "Player not available");
      Objects.requireNonNull(event);
      byte var5 = 0;
      //$FF: var5->value
      //0->net/minecraft/network/chat/ClickEvent$RunCommand
      //1->net/minecraft/network/chat/ClickEvent$ShowDialog
      //2->net/minecraft/network/chat/ClickEvent$Custom
      switch (event.typeSwitch<invokedynamic>(event, var5)) {
         case 0:
            ClickEvent.RunCommand var6 = (ClickEvent.RunCommand)event;
            ClickEvent.RunCommand var10000 = var6;

            try {
               var12 = var10000.command();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            String command = var12;
            clickCommandAction(player, command, activeScreen);
            break;
         case 1:
            ClickEvent.ShowDialog dialog = (ClickEvent.ShowDialog)event;
            player.connection.showDialog(dialog.dialog(), activeScreen);
            break;
         case 2:
            ClickEvent.Custom custom = (ClickEvent.Custom)event;
            player.connection.send(new ServerboundCustomClickActionPacket(custom.id(), custom.payload()));
            if (minecraft.gui.screen() != activeScreen) {
               minecraft.gui.setScreen(activeScreen);
            }
            break;
         default:
            defaultHandleClickEvent(event, minecraft, activeScreen);
      }

   }

   protected static void defaultHandleClickEvent(final ClickEvent event, final Minecraft minecraft, final @Nullable Screen activeScreen) {
      Objects.requireNonNull(event);
      byte var5 = 0;
      boolean var20;
      //$FF: var5->value
      //0->net/minecraft/network/chat/ClickEvent$OpenUrl
      //1->net/minecraft/network/chat/ClickEvent$OpenFile
      //2->net/minecraft/network/chat/ClickEvent$SuggestCommand
      //3->net/minecraft/network/chat/ClickEvent$CopyToClipboard
      switch (event.typeSwitch<invokedynamic>(event, var5)) {
         case 0:
            ClickEvent.OpenUrl var6 = (ClickEvent.OpenUrl)event;
            ClickEvent.OpenUrl var23 = var6;

            try {
               var24 = var23.uri();
            } catch (Throwable var16) {
               throw new MatchException(var16.toString(), var16);
            }

            URI uri = var24;
            clickUrlAction(minecraft, activeScreen, uri);
            var20 = false;
            break;
         case 1:
            ClickEvent.OpenFile openFile = (ClickEvent.OpenFile)event;
            Util.getPlatform().openFile(openFile.file());
            var20 = true;
            break;
         case 2:
            ClickEvent.SuggestCommand var9 = (ClickEvent.SuggestCommand)event;
            ClickEvent.SuggestCommand var21 = var9;

            try {
               var22 = var21.command();
            } catch (Throwable var15) {
               throw new MatchException(var15.toString(), var15);
            }

            String command = var22;
            if (activeScreen != null) {
               activeScreen.insertText(command, true);
            }

            var20 = true;
            break;
         case 3:
            ClickEvent.CopyToClipboard command = (ClickEvent.CopyToClipboard)event;
            ClickEvent.CopyToClipboard var10000 = command;

            try {
               var19 = var10000.value();
            } catch (Throwable var14) {
               throw new MatchException(var14.toString(), var14);
            }

            String value = var19;
            minecraft.keyboardHandler.setClipboard(value);
            var20 = true;
            break;
         default:
            LOGGER.error("Don't know how to handle {}", event);
            var20 = true;
      }

      boolean shouldActivateScreen = var20;
      if (shouldActivateScreen && minecraft.gui.screen() != activeScreen) {
         minecraft.gui.setScreen(activeScreen);
      }

   }

   protected static boolean clickUrlAction(final Minecraft minecraft, final @Nullable Screen screen, final URI uri) {
      if (!(Boolean)minecraft.options.chatLinks().get()) {
         return false;
      } else {
         if ((Boolean)minecraft.options.chatLinksPrompt().get()) {
            minecraft.gui.setScreen(new ConfirmLinkScreen((result) -> {
               if (result) {
                  Util.getPlatform().openUri(uri);
               }

               minecraft.gui.setScreen(screen);
            }, uri.toString(), false));
         } else {
            Util.getPlatform().openUri(uri);
         }

         return true;
      }
   }

   protected static void clickCommandAction(final LocalPlayer player, final String command, final @Nullable Screen screenAfterCommand) {
      player.connection.sendUnattendedCommand(Commands.trimOptionalPrefix(command), screenAfterCommand);
   }

   public final void init(final int width, final int height) {
      this.width = width;
      this.height = height;
      if (!this.initialized) {
         this.init();
         this.setInitialFocus();
      } else {
         this.repositionElements();
      }

      this.initialized = true;
      this.triggerImmediateNarration(false);
      if (this.minecraft.getLastInputType().isKeyboard()) {
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

   protected void fadeWidgets(final float widgetFade) {
      for(GuiEventListener button : this.children()) {
         if (button instanceof AbstractWidget widget) {
            widget.setAlpha(widgetFade);
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

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.isInGameUi()) {
         this.extractTransparentBackground(graphics);
      } else {
         if (this.minecraft.level == null) {
            this.extractPanorama(graphics, a);
         }

         this.extractBlurredBackground(graphics);
         this.extractMenuBackground(graphics);
      }

      this.minecraft.gui.hud.extractDeferredSubtitles();
   }

   protected void extractBlurredBackground(final GuiGraphicsExtractor graphics) {
      float blurRadius = (float)this.minecraft.options.getMenuBackgroundBlurriness();
      if (blurRadius >= 1.0F) {
         graphics.blurBeforeThisStratum();
      }

   }

   protected void extractPanorama(final GuiGraphicsExtractor graphics, final float a) {
      this.minecraft.gameRenderer.panorama().extractRenderState(graphics, this.width, this.height);
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      this.extractMenuBackground(graphics, 0, 0, this.width, this.height);
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics, final int x, final int y, final int width, final int height) {
      extractMenuBackgroundTexture(graphics, this.minecraft.level == null ? MENU_BACKGROUND : INWORLD_MENU_BACKGROUND, x, y, 0.0F, 0.0F, width, height);
   }

   public static void extractMenuBackgroundTexture(final GuiGraphicsExtractor graphics, final Identifier menuBackground, final int x, final int y, final float u, final float v, final int width, final int height) {
      int size = 32;
      graphics.blit(RenderPipelines.GUI_TEXTURED, menuBackground, x, y, u, v, width, height, 32, 32);
   }

   public void extractTransparentBackground(final GuiGraphicsExtractor graphics) {
      graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
   }

   public boolean isPauseScreen() {
      return true;
   }

   public boolean isInGameUi() {
      return false;
   }

   public boolean isAllowedInPortal() {
      return this.isPauseScreen();
   }

   protected void repositionElements() {
      this.rebuildWidgets();
   }

   public void resize(final int width, final int height) {
      this.width = width;
      this.height = height;
      this.repositionElements();
   }

   public void fillCrashDetails(final CrashReport report) {
      CrashReportCategory category = report.addCategory("Affected screen", 1);
      category.setDetail("Screen name", (CrashReportDetail)(() -> this.getClass().getCanonicalName()));
   }

   protected boolean isValidCharacterForName(final String currentName, final int newChar, final int cursorPos) {
      int colonPos = currentName.indexOf(58);
      int slashPos = currentName.indexOf(47);
      if (newChar == 58) {
         return (slashPos == -1 || cursorPos <= slashPos) && colonPos == -1;
      } else if (newChar == 47) {
         return cursorPos > colonPos;
      } else {
         return newChar == 95 || newChar == 45 || newChar >= 97 && newChar <= 122 || newChar >= 48 && newChar <= 57 || newChar == 46;
      }
   }

   public boolean isMouseOver(final double mouseX, final double mouseY) {
      return true;
   }

   public void onFilesDrop(final List<Path> files) {
   }

   private void scheduleNarration(final long delay, final boolean ignoreSuppression) {
      this.nextNarrationTime = Util.getMillis() + delay;
      if (ignoreSuppression) {
         this.narrationSuppressTime = -9223372036854775808L;
      }

   }

   private void suppressNarration(final long duration) {
      this.setNarrationSuppressTime(Util.getMillis() + duration);
   }

   private void setNarrationSuppressTime(final long narrationSuppressTime) {
      this.narrationSuppressTime = narrationSuppressTime;
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
      return SharedConstants.DEBUG_UI_NARRATION || this.minecraft.getNarrator().isActive();
   }

   public void handleDelayedNarration() {
      if (this.shouldRunNarration()) {
         long currentTime = Util.getMillis();
         if (currentTime > this.nextNarrationTime && currentTime > this.narrationSuppressTime) {
            this.runNarration(true);
            this.nextNarrationTime = 9223372036854775807L;
         }
      }

   }

   public void triggerImmediateNarration(final boolean onlyChanged) {
      if (this.shouldRunNarration()) {
         this.runNarration(onlyChanged);
      }

   }

   private void runNarration(final boolean onlyChanged) {
      this.narrationState.update(this::updateNarrationState);
      String narration = this.narrationState.collectNarrationText(!onlyChanged);
      if (!narration.isEmpty()) {
         this.minecraft.getNarrator().saySystemNow(narration);
      }

   }

   protected boolean shouldNarrateNavigation() {
      return true;
   }

   protected void updateNarrationState(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, this.getNarrationMessage());
      this.updateNarratedWidget(output);
   }

   protected void updateNarratedWidget(final NarrationElementOutput output) {
      List<? extends NarratableEntry> activeNarratables = this.narratables.stream().flatMap((narratableEntry) -> narratableEntry.getNarratables().stream()).filter(NarratableEntry::isActive).sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup)).toList();
      NarratableSearchResult result = findNarratableWidget(activeNarratables, this.lastNarratable);
      if (result != null) {
         if (result.priority.isTerminal()) {
            this.lastNarratable = result.entry;
         }

         if (activeNarratables.size() > 1) {
            output.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.screen", result.index + 1, activeNarratables.size()));
            if (result.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               output.add(NarratedElementType.USAGE, this.getUsageNarration());
            }
         }

         result.entry.updateNarration(output.nest());
      } else if (this.shouldNarrateNavigation()) {
         output.add(NarratedElementType.USAGE, SCREEN_USAGE_NARRATION);
      }

   }

   protected Component getUsageNarration() {
      return Component.translatable("narration.component_list.usage");
   }

   public static @Nullable NarratableSearchResult findNarratableWidget(final List<? extends NarratableEntry> narratableEntries, final @Nullable NarratableEntry lastNarratable) {
      NarratableSearchResult result = null;
      NarratableSearchResult lowPrioNarratable = null;
      int i = 0;

      for(int narratablesSize = narratableEntries.size(); i < narratablesSize; ++i) {
         NarratableEntry narratable = (NarratableEntry)narratableEntries.get(i);
         NarratableEntry.NarrationPriority priority = narratable.narrationPriority();
         if (priority.isTerminal()) {
            if (narratable != lastNarratable) {
               return new NarratableSearchResult(narratable, i, priority);
            }

            lowPrioNarratable = new NarratableSearchResult(narratable, i, priority);
         } else if (priority.compareTo(result != null ? result.priority : NarratableEntry.NarrationPriority.NONE) > 0) {
            result = new NarratableSearchResult(narratable, i, priority);
         }
      }

      return result != null ? result : lowPrioNarratable;
   }

   public void updateNarratorStatus(final boolean wasDisabled) {
      if (wasDisabled) {
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

   public boolean canInterruptWithAnotherScreen() {
      return this.shouldCloseOnEsc();
   }

   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(0, 0, this.width, this.height);
   }

   public @Nullable Music getBackgroundMusic() {
      return null;
   }

   static {
      NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
      NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
   }

   public static record NarratableSearchResult(NarratableEntry entry, int index, NarratableEntry.NarrationPriority priority) {
      public NarratableSearchResult {
         super();
      }
   }
}
