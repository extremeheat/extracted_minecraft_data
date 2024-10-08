package net.minecraft.client.gui.screens;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
   protected static final CubeMap CUBE_MAP = new CubeMap(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));
   protected static final PanoramaRenderer PANORAMA = new PanoramaRenderer(CUBE_MAP);
   public static final ResourceLocation MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_background.png");
   public static final ResourceLocation HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/header_separator.png");
   public static final ResourceLocation FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/footer_separator.png");
   private static final ResourceLocation INWORLD_MENU_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_background.png");
   public static final ResourceLocation INWORLD_HEADER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_header_separator.png");
   public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = ResourceLocation.withDefaultNamespace("textures/gui/inworld_footer_separator.png");
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
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
   private static final long NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
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
   @Nullable
   private Screen.DeferredTooltipRendering deferredTooltipRendering;
   protected final Executor screenExecutor = var1x -> this.minecraft.execute(() -> {
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

   public final void renderWithTooltip(GuiGraphics var1, int var2, int var3, float var4) {
      this.render(var1, var2, var3, var4);
      if (this.deferredTooltipRendering != null) {
         var1.renderTooltip(this.font, this.deferredTooltipRendering.tooltip(), this.deferredTooltipRendering.positioner(), var2, var3);
         this.deferredTooltipRendering = null;
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1, var2, var3, var4);

      for (Renderable var6 : this.renderables) {
         var6.render(var1, var2, var3, var4);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         Object var4 = switch (var1) {
            case 258 -> this.createTabEvent();
            default -> null;
            case 262 -> this.createArrowEvent(ScreenDirection.RIGHT);
            case 263 -> this.createArrowEvent(ScreenDirection.LEFT);
            case 264 -> this.createArrowEvent(ScreenDirection.DOWN);
            case 265 -> this.createArrowEvent(ScreenDirection.UP);
         };
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
      ComponentPath var2 = ComponentPath.path(this, var1.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
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
      this.minecraft.setScreen(null);
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T var1) {
      this.renderables.add((Renderable)var1);
      return this.addWidget((T)var1);
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
      return var1.getTooltipLines(
         Item.TooltipContext.of(var0.level), var0.player, var0.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
      );
   }

   protected void insertText(String var1, boolean var2) {
   }

   public boolean handleComponentClicked(@Nullable Style var1) {
      if (var1 == null) {
         return false;
      } else {
         ClickEvent var2 = var1.getClickEvent();
         if (hasShiftDown()) {
            if (var1.getInsertion() != null) {
               this.insertText(var1.getInsertion(), false);
            }
         } else if (var2 != null) {
            if (var2.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.options.chatLinks().get()) {
                  return false;
               }

               try {
                  URI var3 = Util.parseAndValidateUntrustedUri(var2.getValue());
                  if (this.minecraft.options.chatLinksPrompt().get()) {
                     this.minecraft.setScreen(new ConfirmLinkScreen(var2x -> {
                        if (var2x) {
                           Util.getPlatform().openUri(var3);
                        }

                        this.minecraft.setScreen(this);
                     }, var2.getValue(), false));
                  } else {
                     Util.getPlatform().openUri(var3);
                  }
               } catch (URISyntaxException var4) {
                  LOGGER.error("Can't open url for {}", var2, var4);
               }
            } else if (var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               Util.getPlatform().openFile(new File(var2.getValue()));
            } else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(StringUtil.filterText(var2.getValue()), true);
            } else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               String var5 = StringUtil.filterText(var2.getValue());
               if (var5.startsWith("/")) {
                  if (!this.minecraft.player.connection.sendUnsignedCommand(var5.substring(1))) {
                     LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", var5);
                  }
               } else {
                  LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", var5);
               }
            } else if (var2.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardHandler.setClipboard(var2.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", var2);
            }

            return true;
         }

         return false;
      }
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
      this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
   }

   protected void rebuildWidgets() {
      this.clearWidgets();
      this.clearFocus();
      this.init();
      this.setInitialFocus();
   }

   @Override
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

      this.renderBlurredBackground();
      this.renderMenuBackground(var1);
   }

   protected void renderBlurredBackground() {
      this.minecraft.gameRenderer.processBlurEffect();
      this.minecraft.getMainRenderTarget().bindWrite(false);
   }

   protected void renderPanorama(GuiGraphics var1, float var2) {
      PANORAMA.render(var1, this.width, this.height, 1.0F, var2);
   }

   protected void renderMenuBackground(GuiGraphics var1) {
      this.renderMenuBackground(var1, 0, 0, this.width, this.height);
   }

   protected void renderMenuBackground(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      renderMenuBackgroundTexture(var1, this.minecraft.level == null ? MENU_BACKGROUND : INWORLD_MENU_BACKGROUND, var2, var3, 0.0F, 0.0F, var4, var5);
   }

   public static void renderMenuBackgroundTexture(GuiGraphics var0, ResourceLocation var1, int var2, int var3, float var4, float var5, int var6, int var7) {
      byte var8 = 32;
      var0.blit(RenderType::guiTextured, var1, var2, var3, var4, var5, var6, var7, 32, 32);
   }

   public void renderTransparentBackground(GuiGraphics var1) {
      var1.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
   }

   public boolean isPauseScreen() {
      return true;
   }

   public static boolean hasControlDown() {
      return Minecraft.ON_OSX
         ? InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343)
            || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347)
         : InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)
            || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
         || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342)
         || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
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
      var2.setDetail("Screen name", () -> this.getClass().getCanonicalName());
   }

   protected boolean isValidCharacterForName(String var1, char var2, int var3) {
      int var4 = var1.indexOf(58);
      int var5 = var1.indexOf(47);
      if (var2 == ':') {
         return (var5 == -1 || var3 <= var5) && var4 == -1;
      } else {
         return var2 == '/' ? var3 > var4 : var2 == '_' || var2 == '-' || var2 >= 'a' && var2 <= 'z' || var2 >= '0' && var2 <= '9' || var2 == '.';
      }
   }

   @Override
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
      this.narrationSuppressTime = Util.getMillis() + var1;
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
         this.minecraft.getNarrator().sayNow(var2);
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
      List var2 = this.narratables.stream().filter(NarratableEntry::isActive).sorted(Comparator.comparingInt(TabOrderedElement::getTabOrderGroup)).toList();
      Screen.NarratableSearchResult var3 = findNarratableWidget(var2, this.lastNarratable);
      if (var3 != null) {
         if (var3.priority.isTerminal()) {
            this.lastNarratable = var3.entry;
         }

         if (var2.size() > 1) {
            var1.add(NarratedElementType.POSITION, Component.translatable("narrator.position.screen", var3.index + 1, var2.size()));
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
   public static Screen.NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> var0, @Nullable NarratableEntry var1) {
      Screen.NarratableSearchResult var2 = null;
      Screen.NarratableSearchResult var3 = null;
      int var4 = 0;

      for (int var5 = var0.size(); var4 < var5; var4++) {
         NarratableEntry var6 = (NarratableEntry)var0.get(var4);
         NarratableEntry.NarrationPriority var7 = var6.narrationPriority();
         if (var7.isTerminal()) {
            if (var6 != var1) {
               return new Screen.NarratableSearchResult(var6, var4, var7);
            }

            var3 = new Screen.NarratableSearchResult(var6, var4, var7);
         } else if (var7.compareTo(var2 != null ? var2.priority : NarratableEntry.NarrationPriority.NONE) > 0) {
            var2 = new Screen.NarratableSearchResult(var6, var4, var7);
         }
      }

      return var2 != null ? var2 : var3;
   }

   public void updateNarratorStatus(boolean var1) {
      if (var1) {
         this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
      }

      if (this.narratorButton != null) {
         this.narratorButton.setValue(this.minecraft.options.narrator().get());
      }
   }

   protected void clearTooltipForNextRenderPass() {
      this.deferredTooltipRendering = null;
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> var1) {
      this.setTooltipForNextRenderPass(var1, DefaultTooltipPositioner.INSTANCE, true);
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> var1, ClientTooltipPositioner var2, boolean var3) {
      if (this.deferredTooltipRendering == null || var3) {
         this.deferredTooltipRendering = new Screen.DeferredTooltipRendering(var1, var2);
      }
   }

   public void setTooltipForNextRenderPass(Component var1) {
      this.setTooltipForNextRenderPass(Tooltip.splitTooltip(this.minecraft, var1));
   }

   public void setTooltipForNextRenderPass(Tooltip var1, ClientTooltipPositioner var2, boolean var3) {
      this.setTooltipForNextRenderPass(var1.toCharSequence(this.minecraft), var2, var3);
   }

   public Font getFont() {
      return this.font;
   }

   public boolean showsActiveEffects() {
      return false;
   }

   @Override
   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(0, 0, this.width, this.height);
   }

   @Nullable
   public Music getBackgroundMusic() {
      return null;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static class NarratableSearchResult {
      public final NarratableEntry entry;
      public final int index;
      public final NarratableEntry.NarrationPriority priority;

      public NarratableSearchResult(NarratableEntry var1, int var2, NarratableEntry.NarrationPriority var3) {
         super();
         this.entry = var1;
         this.index = var2;
         this.priority = var3;
      }
   }
}
