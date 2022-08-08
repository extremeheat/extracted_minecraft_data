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
import com.mojang.math.Matrix4f;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
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
import org.slf4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Widget {
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
   private final List<Widget> renderables = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   @Nullable
   private URI clickedLink;
   private static final long NARRATE_SUPPRESS_AFTER_INIT_TIME;
   private static final long NARRATE_DELAY_NARRATOR_ENABLED;
   private static final long NARRATE_DELAY_MOUSE_MOVE = 750L;
   private static final long NARRATE_DELAY_MOUSE_ACTION = 200L;
   private static final long NARRATE_DELAY_KEYBOARD_ACTION = 200L;
   private final ScreenNarrationCollector narrationState = new ScreenNarrationCollector();
   private long narrationSuppressTime = -9223372036854775808L;
   private long nextNarrationTime = 9223372036854775807L;
   @Nullable
   private NarratableEntry lastNarratable;

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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      Iterator var5 = this.renderables.iterator();

      while(var5.hasNext()) {
         Widget var6 = (Widget)var5.next();
         var6.render(var1, var2, var3, var4);
      }

   }

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
      this.minecraft.setScreen((Screen)null);
   }

   protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T var1) {
      this.renderables.add((Widget)var1);
      return this.addWidget(var1);
   }

   protected <T extends Widget> T addRenderableOnly(T var1) {
      this.renderables.add(var1);
      return var1;
   }

   protected <T extends GuiEventListener & NarratableEntry> T addWidget(T var1) {
      this.children.add(var1);
      this.narratables.add((NarratableEntry)var1);
      return var1;
   }

   protected void removeWidget(GuiEventListener var1) {
      if (var1 instanceof Widget) {
         this.renderables.remove((Widget)var1);
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
      List var6 = (List)var2.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
      var3.ifPresent((var1x) -> {
         var6.add(1, ClientTooltipComponent.create(var1x));
      });
      this.renderTooltipInternal(var1, var6, var4, var5);
   }

   public List<Component> getTooltipFromItem(ItemStack var1) {
      return var1.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   public void renderTooltip(PoseStack var1, Component var2, int var3, int var4) {
      this.renderTooltip(var1, Arrays.asList(var2.getVisualOrderText()), var3, var4);
   }

   public void renderComponentTooltip(PoseStack var1, List<Component> var2, int var3, int var4) {
      this.renderTooltip(var1, Lists.transform(var2, Component::getVisualOrderText), var3, var4);
   }

   public void renderTooltip(PoseStack var1, List<? extends FormattedCharSequence> var2, int var3, int var4) {
      this.renderTooltipInternal(var1, (List)var2.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), var3, var4);
   }

   private void renderTooltipInternal(PoseStack var1, List<ClientTooltipComponent> var2, int var3, int var4) {
      if (!var2.isEmpty()) {
         int var5 = 0;
         int var6 = var2.size() == 1 ? -2 : 0;

         ClientTooltipComponent var8;
         for(Iterator var7 = var2.iterator(); var7.hasNext(); var6 += var8.getHeight()) {
            var8 = (ClientTooltipComponent)var7.next();
            int var9 = var8.getWidth(this.font);
            if (var9 > var5) {
               var5 = var9;
            }
         }

         int var23 = var3 + 12;
         int var24 = var4 - 12;
         if (var23 + var5 > this.width) {
            var23 -= 28 + var5;
         }

         if (var24 + var6 + 6 > this.height) {
            var24 = this.height - var6 - 6;
         }

         var1.pushPose();
         int var11 = -267386864;
         int var12 = 1347420415;
         int var13 = 1344798847;
         boolean var14 = true;
         float var15 = this.itemRenderer.blitOffset;
         this.itemRenderer.blitOffset = 400.0F;
         Tesselator var16 = Tesselator.getInstance();
         BufferBuilder var17 = var16.getBuilder();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         var17.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         Matrix4f var18 = var1.last().pose();
         fillGradient(var18, var17, var23 - 3, var24 - 4, var23 + var5 + 3, var24 - 3, 400, -267386864, -267386864);
         fillGradient(var18, var17, var23 - 3, var24 + var6 + 3, var23 + var5 + 3, var24 + var6 + 4, 400, -267386864, -267386864);
         fillGradient(var18, var17, var23 - 3, var24 - 3, var23 + var5 + 3, var24 + var6 + 3, 400, -267386864, -267386864);
         fillGradient(var18, var17, var23 - 4, var24 - 3, var23 - 3, var24 + var6 + 3, 400, -267386864, -267386864);
         fillGradient(var18, var17, var23 + var5 + 3, var24 - 3, var23 + var5 + 4, var24 + var6 + 3, 400, -267386864, -267386864);
         fillGradient(var18, var17, var23 - 3, var24 - 3 + 1, var23 - 3 + 1, var24 + var6 + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(var18, var17, var23 + var5 + 2, var24 - 3 + 1, var23 + var5 + 3, var24 + var6 + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(var18, var17, var23 - 3, var24 - 3, var23 + var5 + 3, var24 - 3 + 1, 400, 1347420415, 1347420415);
         fillGradient(var18, var17, var23 - 3, var24 + var6 + 2, var23 + var5 + 3, var24 + var6 + 3, 400, 1344798847, 1344798847);
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         BufferUploader.drawWithShader(var17.end());
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         MultiBufferSource.BufferSource var19 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         var1.translate(0.0, 0.0, 400.0);
         int var20 = var24;

         int var21;
         ClientTooltipComponent var22;
         for(var21 = 0; var21 < var2.size(); ++var21) {
            var22 = (ClientTooltipComponent)var2.get(var21);
            var22.renderText(this.font, var23, var20, var18, var19);
            var20 += var22.getHeight() + (var21 == 0 ? 2 : 0);
         }

         var19.endBatch();
         var1.popPose();
         var20 = var24;

         for(var21 = 0; var21 < var2.size(); ++var21) {
            var22 = (ClientTooltipComponent)var2.get(var21);
            var22.renderImage(this.font, var23, var20, var1, this.itemRenderer, 400);
            var20 += var22.getHeight() + (var21 == 0 ? 2 : 0);
         }

         this.itemRenderer.blitOffset = var15;
      }
   }

   protected void renderComponentHoverEffect(PoseStack var1, @Nullable Style var2, int var3, int var4) {
      if (var2 != null && var2.getHoverEvent() != null) {
         HoverEvent var5 = var2.getHoverEvent();
         HoverEvent.ItemStackInfo var6 = (HoverEvent.ItemStackInfo)var5.getValue(HoverEvent.Action.SHOW_ITEM);
         if (var6 != null) {
            this.renderTooltip(var1, var6.getItemStack(), var3, var4);
         } else {
            HoverEvent.EntityTooltipInfo var7 = (HoverEvent.EntityTooltipInfo)var5.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (var7 != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(var1, var7.getTooltipLines(), var3, var4);
               }
            } else {
               Component var8 = (Component)var5.getValue(HoverEvent.Action.SHOW_TEXT);
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
            URI var3;
            if (var2.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!(Boolean)this.minecraft.options.chatLinks().get()) {
                  return false;
               }

               try {
                  var3 = new URI(var2.getValue());
                  String var4 = var3.getScheme();
                  if (var4 == null) {
                     throw new URISyntaxException(var2.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(var4.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var4.toLowerCase(Locale.ROOT));
                  }

                  if ((Boolean)this.minecraft.options.chatLinksPrompt().get()) {
                     this.clickedLink = var3;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, var2.getValue(), false));
                  } else {
                     this.openLink(var3);
                  }
               } catch (URISyntaxException var5) {
                  LOGGER.error("Can't open url for {}", var2, var5);
               }
            } else if (var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               var3 = (new File(var2.getValue())).toURI();
               this.openLink(var3);
            } else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(SharedConstants.filterText(var2.getValue()), true);
            } else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               String var6 = SharedConstants.filterText(var2.getValue());
               if (var6.startsWith("/")) {
                  if (!this.minecraft.player.commandUnsigned(var6.substring(1))) {
                     LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", var6);
                  }
               } else {
                  LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", var6);
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
      this.setFocused((GuiEventListener)null);
      this.init();
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
      var3.vertex((double)this.width, (double)this.height, 0.0).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)var1).color(64, 64, 64, 255).endVertex();
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

   public void resize(Minecraft var1, int var2, int var3) {
      this.init(var1, var2, var3);
   }

   public static void wrapScreenError(Runnable var0, String var1, String var2) {
      try {
         var0.run();
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, var1);
         CrashReportCategory var5 = var4.addCategory("Affected screen");
         var5.setDetail("Screen name", () -> {
            return var2;
         });
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
      ImmutableList var2 = (ImmutableList)this.narratables.stream().filter(NarratableEntry::isActive).collect(ImmutableList.toImmutableList());
      NarratableSearchResult var3 = findNarratableWidget(var2, this.lastNarratable);
      if (var3 != null) {
         if (var3.priority.isTerminal()) {
            this.lastNarratable = var3.entry;
         }

         if (var2.size() > 1) {
            var1.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.screen", var3.index + 1, var2.size()));
            if (var3.priority == NarratableEntry.NarrationPriority.FOCUSED) {
               var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.component_list.usage"));
            }
         }

         var3.entry.updateNarration(var1.nest());
      }

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

   public void narrationEnabled() {
      this.scheduleNarration(NARRATE_DELAY_NARRATOR_ENABLED, false);
   }

   protected static void hideWidgets(AbstractWidget... var0) {
      AbstractWidget[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AbstractWidget var4 = var1[var3];
         var4.visible = false;
      }

   }

   static {
      NARRATE_SUPPRESS_AFTER_INIT_TIME = TimeUnit.SECONDS.toMillis(2L);
      NARRATE_DELAY_NARRATOR_ENABLED = NARRATE_SUPPRESS_AFTER_INIT_TIME;
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
