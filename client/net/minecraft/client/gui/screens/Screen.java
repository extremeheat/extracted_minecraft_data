package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.slf4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Renderable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
   private static final Component USAGE_NARRATION = Component.translatable("narrator.screen.usage");
   protected final Component title;
   private final List<GuiEventListener> children = Lists.newArrayList();
   private final List<NarratableEntry> narratables = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   private final List<Renderable> renderables = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   @Nullable
   private URI clickedLink;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
   private static final long NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
   private long narrationSuppressTime = -9223372036854775808L;
   private long nextNarrationTime = 9223372036854775807L;
   @Nullable
   private NarratableEntry lastNarratable;
   @Nullable
   private Screen.DeferredTooltipRendering deferredTooltipRendering;

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

   public final void renderWithTooltip(PoseStack var1, int var2, int var3, float var4) {
      this.render(var1, var2, var3, var4);
      if (this.deferredTooltipRendering != null) {
         this.renderTooltip(var1, this.deferredTooltipRendering, var2, var3);
         this.deferredTooltipRendering = null;
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      for(Renderable var6 : this.renderables) {
         var6.render(var1, var2, var3, var4);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (var1 == 258) {
         boolean var4 = !hasShiftDown();
         if (!this.changeFocus(var4)) {
            this.changeFocus(var4);
         }

         return false;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
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

   protected void renderTooltip(PoseStack var1, ItemStack var2, int var3, int var4) {
      this.renderTooltip(var1, this.getTooltipFromItem(var2), var2.getTooltipImage(), var3, var4);
   }

   public void renderTooltip(PoseStack var1, List<Component> var2, Optional<TooltipComponent> var3, int var4, int var5) {
      List var6 = var2.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
      var3.ifPresent(var1x -> var6.add(1, ClientTooltipComponent.create(var1x)));
      this.renderTooltipInternal(var1, var6, var4, var5, DefaultTooltipPositioner.INSTANCE);
   }

   public List<Component> getTooltipFromItem(ItemStack var1) {
      return var1.getTooltipLines(
         this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
      );
   }

   public void renderTooltip(PoseStack var1, Component var2, int var3, int var4) {
      this.renderTooltip(var1, Arrays.asList(var2.getVisualOrderText()), var3, var4);
   }

   public void renderComponentTooltip(PoseStack var1, List<Component> var2, int var3, int var4) {
      this.renderTooltip(var1, Lists.transform(var2, Component::getVisualOrderText), var3, var4);
   }

   public void renderTooltip(PoseStack var1, List<? extends FormattedCharSequence> var2, int var3, int var4) {
      this.renderTooltipInternal(
         var1, var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var3, var4, DefaultTooltipPositioner.INSTANCE
      );
   }

   private void renderTooltip(PoseStack var1, Screen.DeferredTooltipRendering var2, int var3, int var4) {
      this.renderTooltipInternal(var1, var2.tooltip().stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var3, var4, var2.positioner());
   }

   private void renderTooltipInternal(PoseStack var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5) {
      if (!var2.isEmpty()) {
         int var6 = 0;
         int var7 = var2.size() == 1 ? -2 : 0;

         for(ClientTooltipComponent var9 : var2) {
            int var10 = var9.getWidth(this.font);
            if (var10 > var6) {
               var6 = var10;
            }

            var7 += var9.getHeight();
         }

         int var22 = var3 + 12;
         int var24 = var4 - 12;
         Vector2ic var12 = var5.positionTooltip(this, var22, var24, var6, var7);
         var22 = var12.x();
         var24 = var12.y();
         var1.pushPose();
         boolean var13 = true;
         float var14 = this.itemRenderer.blitOffset;
         this.itemRenderer.blitOffset = 400.0F;
         Tesselator var15 = Tesselator.getInstance();
         BufferBuilder var16 = var15.getBuilder();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         var16.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         Matrix4f var17 = var1.last().pose();
         TooltipRenderUtil.renderTooltipBackground(
            (var0, var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8) -> GuiComponent.fillGradient(
                  var0, var1x, var2x, var3x, var4x, var5x, var6x, var7x, var8
               ),
            var17,
            var16,
            var22,
            var24,
            var6,
            var7,
            400
         );
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         BufferUploader.drawWithShader(var16.end());
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         MultiBufferSource.BufferSource var18 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         var1.translate(0.0F, 0.0F, 400.0F);
         int var19 = var24;

         for(int var20 = 0; var20 < var2.size(); ++var20) {
            ClientTooltipComponent var21 = (ClientTooltipComponent)var2.get(var20);
            var21.renderText(this.font, var22, var19, var17, var18);
            var19 += var21.getHeight() + (var20 == 0 ? 2 : 0);
         }

         var18.endBatch();
         var1.popPose();
         var19 = var24;

         for(int var27 = 0; var27 < var2.size(); ++var27) {
            ClientTooltipComponent var28 = (ClientTooltipComponent)var2.get(var27);
            var28.renderImage(this.font, var22, var19, var1, this.itemRenderer, 400);
            var19 += var28.getHeight() + (var27 == 0 ? 2 : 0);
         }

         this.itemRenderer.blitOffset = var14;
      }
   }

   protected void renderComponentHoverEffect(PoseStack var1, @Nullable Style var2, int var3, int var4) {
      if (var2 != null && var2.getHoverEvent() != null) {
         HoverEvent var5 = var2.getHoverEvent();
         HoverEvent.ItemStackInfo var6 = var5.getValue(HoverEvent.Action.SHOW_ITEM);
         if (var6 != null) {
            this.renderTooltip(var1, var6.getItemStack(), var3, var4);
         } else {
            HoverEvent.EntityTooltipInfo var7 = var5.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (var7 != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(var1, var7.getTooltipLines(), var3, var4);
               }
            } else {
               Component var8 = var5.getValue(HoverEvent.Action.SHOW_TEXT);
               if (var8 != null) {
                  this.renderTooltip(var1, this.minecraft.font.split(var8, Math.max(this.width / 2, 200)), var3, var4);
               }
            }
         }
      }
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
                  URI var3 = new URI(var2.getValue());
                  String var4 = var3.getScheme();
                  if (var4 == null) {
                     throw new URISyntaxException(var2.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(var4.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var4.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.options.chatLinksPrompt().get()) {
                     this.clickedLink = var3;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, var2.getValue(), false));
                  } else {
                     this.openLink(var3);
                  }
               } catch (URISyntaxException var5) {
                  LOGGER.error("Can't open url for {}", var2, var5);
               }
            } else if (var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI var6 = new File(var2.getValue()).toURI();
               this.openLink(var6);
            } else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(SharedConstants.filterText(var2.getValue()), true);
            } else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               String var7 = SharedConstants.filterText(var2.getValue());
               if (var7.startsWith("/")) {
                  if (!this.minecraft.player.connection.sendUnsignedCommand(var7.substring(1))) {
                     LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", var7);
                  }
               } else {
                  LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", var7);
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
      this.itemRenderer = var1.getItemRenderer();
      this.font = var1.font;
      this.width = var2;
      this.height = var3;
      this.rebuildWidgets();
      this.triggerImmediateNarration(false);
      this.suppressNarration(NARRATE_SUPPRESS_AFTER_INIT_TIME);
   }

   protected void rebuildWidgets() {
      this.clearWidgets();
      this.setFocused(null);
      this.init();
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

   public void renderBackground(PoseStack var1) {
      this.renderBackground(var1, 0);
   }

   public void renderBackground(PoseStack var1, int var2) {
      if (this.minecraft.level != null) {
         this.fillGradient(var1, 0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.renderDirtBackground(var2);
      }
   }

   public void renderDirtBackground(int var1) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var3.vertex(0.0, (double)this.height, 0.0).uv(0.0F, (float)this.height / 32.0F + (float)var1).color(64, 64, 64, 255).endVertex();
      var3.vertex((double)this.width, (double)this.height, 0.0)
         .uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)var1)
         .color(64, 64, 64, 255)
         .endVertex();
      var3.vertex((double)this.width, 0.0, 0.0).uv((float)this.width / 32.0F, (float)var1).color(64, 64, 64, 255).endVertex();
      var3.vertex(0.0, 0.0, 0.0).uv(0.0F, (float)var1).color(64, 64, 64, 255).endVertex();
      var2.end();
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean var1) {
      if (var1) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI var1) {
      Util.getPlatform().openUri(var1);
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343)
            || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)
            || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
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

   public void resize(Minecraft var1, int var2, int var3) {
      this.init(var1, var2, var3);
   }

   public static void wrapScreenError(Runnable var0, String var1, String var2) {
      try {
         var0.run();
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, var1);
         CrashReportCategory var5 = var4.addCategory("Affected screen");
         var5.setDetail("Screen name", () -> var2);
         throw new ReportedException(var4);
      }
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

   protected void updateNarrationState(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getNarrationMessage());
      var1.add(NarratedElementType.USAGE, USAGE_NARRATION);
      this.updateNarratedWidget(var1);
   }

   protected void updateNarratedWidget(NarrationElementOutput var1) {
      ImmutableList var2 = this.narratables.stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
      Screen.NarratableSearchResult var3 = findNarratableWidget(var2, this.lastNarratable);
      if (var3 != null) {
         if (var3.priority.isTerminal()) {
            this.lastNarratable = var3.entry;
         }

         if (var2.size() > 1) {
            var1.add(NarratedElementType.POSITION, Component.translatable("narrator.position.screen", var3.index + 1, var2.size()));
            if (var3.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               var1.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
            }
         }

         var3.entry.updateNarration(var1.nest());
      }
   }

   @Nullable
   public static Screen.NarratableSearchResult findNarratableWidget(List<? extends NarratableEntry> var0, @Nullable NarratableEntry var1) {
      Screen.NarratableSearchResult var2 = null;
      Screen.NarratableSearchResult var3 = null;
      int var4 = 0;

      for(int var5 = var0.size(); var4 < var5; ++var4) {
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

   public void narrationEnabled() {
      this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> var1) {
      this.setTooltipForNextRenderPass(var1, DefaultTooltipPositioner.INSTANCE, true);
   }

   public void setTooltipForNextRenderPass(List<FormattedCharSequence> var1, ClientTooltipPositioner var2, boolean var3) {
      if (this.deferredTooltipRendering == null || var3) {
         this.deferredTooltipRendering = new Screen.DeferredTooltipRendering(var1, var2);
      }
   }

   protected void setTooltipForNextRenderPass(Component var1) {
      this.setTooltipForNextRenderPass(Tooltip.splitTooltip(this.minecraft, var1));
   }

   public void setTooltipForNextRenderPass(Tooltip var1, ClientTooltipPositioner var2, boolean var3) {
      this.setTooltipForNextRenderPass(var1.toCharSequence(this.minecraft), var2, var3);
   }

   protected static void hideWidgets(AbstractWidget... var0) {
      for(AbstractWidget var4 : var0) {
         var4.visible = false;
      }
   }

   static record DeferredTooltipRendering(List<FormattedCharSequence> a, ClientTooltipPositioner b) {
      private final List<FormattedCharSequence> tooltip;
      private final ClientTooltipPositioner positioner;

      DeferredTooltipRendering(List<FormattedCharSequence> var1, ClientTooltipPositioner var2) {
         super();
         this.tooltip = var1;
         this.positioner = var2;
      }
   }

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
