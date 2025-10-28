package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public class EditGameRulesScreen extends Screen {
   private static final Component TITLE = Component.translatable("editGamerule.title");
   private static final int SPACING = 8;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Consumer<Optional<GameRules>> exitCallback;
   private final Set<RuleEntry> invalidEntries = Sets.newHashSet();
   final GameRules gameRules;
   private RuleList ruleList;
   private @Nullable Button doneButton;

   public EditGameRulesScreen(GameRules var1, Consumer<Optional<GameRules>> var2) {
      super(TITLE);
      this.gameRules = var1;
      this.exitCallback = var2;
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.ruleList = (RuleList)this.layout.addToContents(new RuleList(this.gameRules));
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)var1.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.exitCallback.accept(Optional.of(this.gameRules))).build());
      var1.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onClose()).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.ruleList != null) {
         this.ruleList.updateSize(this.width, this.layout);
      }

   }

   public void onClose() {
      this.exitCallback.accept(Optional.empty());
   }

   private void updateDoneButton() {
      if (this.doneButton != null) {
         this.doneButton.active = this.invalidEntries.isEmpty();
      }

   }

   void markInvalid(RuleEntry var1) {
      this.invalidEntries.add(var1);
      this.updateDoneButton();
   }

   void clearInvalid(RuleEntry var1) {
      this.invalidEntries.remove(var1);
      this.updateDoneButton();
   }

   public abstract static class RuleEntry extends ContainerObjectSelectionList.Entry<RuleEntry> {
      final @Nullable List<FormattedCharSequence> tooltip;

      public RuleEntry(@Nullable List<FormattedCharSequence> var1) {
         super();
         this.tooltip = var1;
      }
   }

   public class CategoryRuleEntry extends RuleEntry {
      final Component label;

      public CategoryRuleEntry(final Component var2) {
         super((List)null);
         this.label = var2;
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         var1.drawCenteredString(EditGameRulesScreen.this.minecraft.font, (Component)this.label, this.getContentXMiddle(), this.getContentY() + 5, -1);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput var1) {
               var1.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
            }
         });
      }
   }

   public abstract class GameRuleEntry extends RuleEntry {
      private final List<FormattedCharSequence> label;
      protected final List<AbstractWidget> children = Lists.newArrayList();

      public GameRuleEntry(final List<FormattedCharSequence> var2, final Component var3) {
         super(var2);
         this.label = EditGameRulesScreen.this.minecraft.font.split(var3, 175);
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      protected void renderLabel(GuiGraphics var1, int var2, int var3) {
         if (this.label.size() == 1) {
            var1.drawString(EditGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(0)), var3, var2 + 5, -1);
         } else if (this.label.size() >= 2) {
            var1.drawString(EditGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(0)), var3, var2, -1);
            var1.drawString(EditGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(1)), var3, var2 + 10, -1);
         }

      }
   }

   public class BooleanRuleEntry extends GameRuleEntry {
      private final CycleButton<Boolean> checkbox;

      public BooleanRuleEntry(final Component var2, final List<FormattedCharSequence> var3, final String var4, final GameRule<Boolean> var5) {
         super(var3, var2);
         this.checkbox = CycleButton.onOffBuilder((Boolean)EditGameRulesScreen.this.gameRules.get(var5)).displayOnlyValue().withCustomNarration((var1x) -> var1x.createDefaultNarrationMessage().append("\n").append(var4)).create(10, 5, 44, 20, var2, (var2x, var3x) -> EditGameRulesScreen.this.gameRules.set(var5, var3x, (MinecraftServer)null));
         this.children.add(this.checkbox);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.renderLabel(var1, this.getContentY(), this.getContentX());
         this.checkbox.setX(this.getContentRight() - 45);
         this.checkbox.setY(this.getContentY());
         this.checkbox.render(var1, var2, var3, var5);
      }
   }

   public class IntegerRuleEntry extends GameRuleEntry {
      private final EditBox input;

      public IntegerRuleEntry(final Component var2, final List<FormattedCharSequence> var3, final String var4, final GameRule<Integer> var5) {
         super(var3, var2);
         this.input = new EditBox(EditGameRulesScreen.this.minecraft.font, 10, 5, 44, 20, var2.copy().append("\n").append(var4).append("\n"));
         this.input.setValue(EditGameRulesScreen.this.gameRules.getAsString(var5));
         this.input.setResponder((var2x) -> {
            DataResult var3 = var5.deserialize(var2x);
            if (var3.isSuccess()) {
               this.input.setTextColor(-2039584);
               EditGameRulesScreen.this.clearInvalid(this);
               EditGameRulesScreen.this.gameRules.set(var5, (Integer)var3.getOrThrow(), (MinecraftServer)null);
            } else {
               this.input.setTextColor(-65536);
               EditGameRulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.input);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.renderLabel(var1, this.getContentY(), this.getContentX());
         this.input.setX(this.getContentRight() - 45);
         this.input.setY(this.getContentY());
         this.input.render(var1, var2, var3, var5);
      }
   }

   public class RuleList extends ContainerObjectSelectionList<RuleEntry> {
      private static final int ITEM_HEIGHT = 24;

      public RuleList(final GameRules var2) {
         super(Minecraft.getInstance(), EditGameRulesScreen.this.width, EditGameRulesScreen.this.layout.getContentHeight(), EditGameRulesScreen.this.layout.getHeaderHeight(), 24);
         final HashMap var3 = Maps.newHashMap();
         var2.visitGameRuleTypes(new GameRuleTypeVisitor() {
            public void visitBoolean(GameRule<Boolean> var1) {
               this.addEntry(var1, (var1x, var2, var3x, var4) -> EditGameRulesScreen.thisx.new BooleanRuleEntry(var1x, var2, var3x, var4));
            }

            public void visitInteger(GameRule<Integer> var1) {
               this.addEntry(var1, (var1x, var2, var3x, var4) -> EditGameRulesScreen.thisx.new IntegerRuleEntry(var1x, var2, var3x, var4));
            }

            private <T> void addEntry(GameRule<T> var1, EntryFactory<T> var2) {
               MutableComponent var3x = Component.translatable(var1.getDescriptionId());
               MutableComponent var4 = Component.literal(var1.id()).withStyle(ChatFormatting.YELLOW);
               MutableComponent var5 = Component.translatable("editGamerule.default", Component.literal(var1.serialize(var1.defaultValue()))).withStyle(ChatFormatting.GRAY);
               String var6 = var1.getDescriptionId() + ".description";
               ImmutableList var7;
               String var8;
               if (I18n.exists(var6)) {
                  ImmutableList.Builder var9 = ImmutableList.builder().add(var4.getVisualOrderText());
                  MutableComponent var10 = Component.translatable(var6);
                  List var10000 = EditGameRulesScreen.this.font.split(var10, 150);
                  Objects.requireNonNull(var9);
                  var10000.forEach(var9::add);
                  var7 = var9.add(var5.getVisualOrderText()).build();
                  String var11 = var10.getString();
                  var8 = var11 + "\n" + var5.getString();
               } else {
                  var7 = ImmutableList.of(var4.getVisualOrderText(), var5.getVisualOrderText());
                  var8 = var5.getString();
               }

               ((Map)var3.computeIfAbsent(var1.category(), (var0) -> Maps.newHashMap())).put(var1, var2.create(var3x, var7, var8, var1));
            }
         });
         var3.entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(GameRuleCategory::getDescriptionId))).forEach((var1x) -> {
            this.addEntry(EditGameRulesScreen.this.new CategoryRuleEntry(((GameRuleCategory)var1x.getKey()).label().withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
            ((Map)var1x.getValue()).entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(GameRule::getDescriptionId))).forEach((var1) -> this.addEntry((RuleEntry)var1.getValue()));
         });
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         super.renderWidget(var1, var2, var3, var4);
         RuleEntry var5 = (RuleEntry)this.getHovered();
         if (var5 != null && var5.tooltip != null) {
            var1.setTooltipForNextFrame(var5.tooltip, var2, var3);
         }

      }
   }

   @FunctionalInterface
   interface EntryFactory<T> {
      RuleEntry create(Component var1, List<FormattedCharSequence> var2, String var3, GameRule<T> var4);
   }
}
