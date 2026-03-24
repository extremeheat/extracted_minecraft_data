package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public abstract class AbstractGameRulesScreen extends Screen {
   protected static final Component TITLE = Component.translatable("editGamerule.title");
   private static final Component SEARCH_HINT;
   private static final int SEARCH_BOX_HEIGHT = 15;
   private final Set<RuleEntry> invalidEntries = Sets.newHashSet();
   private final Consumer<Optional<GameRules>> exitCallback;
   protected final HeaderAndFooterLayout layout;
   protected final GameRules gameRules;
   protected @Nullable EditBox searchBox;
   protected RuleList ruleList;
   protected @Nullable Button doneButton;

   public AbstractGameRulesScreen(final GameRules gameRules, final Consumer<Optional<GameRules>> exitCallback) {
      super(TITLE);
      this.gameRules = gameRules;
      this.exitCallback = exitCallback;
      Objects.requireNonNull(this.font);
      this.layout = new HeaderAndFooterLayout(this, (int)(12.0 + 9.0 + 15.0), 33);
   }

   protected void createAndConfigureSearchBox(final LinearLayout headerLayout) {
      this.searchBox = (EditBox)headerLayout.addChild(new EditBox(this.font, 200, 15, Component.empty()));
      this.searchBox.setHint(SEARCH_HINT);
      this.searchBox.setResponder(this::filterGameRules);
   }

   protected void init() {
      LinearLayout header = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      header.defaultCellSetting().alignHorizontallyCenter();
      header.addChild(new StringWidget(TITLE, this.font));
      this.createAndConfigureSearchBox(header);
      this.initContent();
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onDone()).build());
      footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected abstract void initContent();

   protected abstract void onDone();

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.ruleList != null) {
         this.ruleList.updateSize(this.width, this.layout);
      }

   }

   protected void setInitialFocus() {
      if (this.searchBox != null) {
         this.setInitialFocus(this.searchBox);
      }

   }

   private void markInvalid(final RuleEntry invalidEntry) {
      this.invalidEntries.add(invalidEntry);
      this.updateDoneButton();
   }

   private void clearInvalid(final RuleEntry invalidEntry) {
      this.invalidEntries.remove(invalidEntry);
      this.updateDoneButton();
   }

   private void updateDoneButton() {
      if (this.doneButton != null) {
         this.doneButton.active = this.invalidEntries.isEmpty();
      }

   }

   protected void closeAndDiscardChanges() {
      this.exitCallback.accept(Optional.empty());
   }

   protected void closeAndApplyChanges() {
      this.exitCallback.accept(Optional.of(this.gameRules));
   }

   protected void filterGameRules(final String filter) {
      if (this.ruleList != null) {
         this.ruleList.populateChildren(filter);
         this.ruleList.setScrollAmount(0.0);
         this.repositionElements();
      }

   }

   static {
      SEARCH_HINT = Component.translatable("gui.game_rule.search").withStyle(EditBox.SEARCH_HINT_STYLE);
   }

   public abstract static class RuleEntry extends ContainerObjectSelectionList.Entry<RuleEntry> {
      private final @Nullable List<FormattedCharSequence> tooltip;

      public RuleEntry(final @Nullable List<FormattedCharSequence> tooltip) {
         super();
         this.tooltip = tooltip;
      }
   }

   public class CategoryRuleEntry extends RuleEntry {
      private final Component label;

      public CategoryRuleEntry(final Component label) {
         Objects.requireNonNull(AbstractGameRulesScreen.this);
         super((List)null);
         this.label = label;
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         graphics.centeredText(AbstractGameRulesScreen.this.minecraft.font, (Component)this.label, this.getContentXMiddle(), this.getContentY() + 5, -1);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            {
               Objects.requireNonNull(CategoryRuleEntry.this);
            }

            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            public void updateNarration(final NarrationElementOutput output) {
               output.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
            }
         });
      }
   }

   public abstract class GameRuleEntry extends RuleEntry {
      private final List<FormattedCharSequence> label;
      protected final List<AbstractWidget> children;

      public GameRuleEntry(final List<FormattedCharSequence> tooltip, final Component label) {
         Objects.requireNonNull(AbstractGameRulesScreen.this);
         super(tooltip);
         this.children = Lists.newArrayList();
         this.label = AbstractGameRulesScreen.this.minecraft.font.split(label, 170);
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      protected void extractLabel(final GuiGraphicsExtractor graphics, final int rowTop, final int rowLeft) {
         if (this.label.size() == 1) {
            graphics.text(AbstractGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(0)), rowLeft, rowTop + 5, -1);
         } else if (this.label.size() >= 2) {
            graphics.text(AbstractGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(0)), rowLeft, rowTop, -1);
            graphics.text(AbstractGameRulesScreen.this.minecraft.font, (FormattedCharSequence)((FormattedCharSequence)this.label.get(1)), rowLeft, rowTop + 10, -1);
         }

      }
   }

   public class BooleanRuleEntry extends GameRuleEntry {
      private final CycleButton<Boolean> checkbox;

      public BooleanRuleEntry(final Component name, final List<FormattedCharSequence> tooltip, final String narration, final GameRule<Boolean> gameRule) {
         Objects.requireNonNull(AbstractGameRulesScreen.this);
         super(tooltip, name);
         this.checkbox = CycleButton.onOffBuilder((Boolean)AbstractGameRulesScreen.this.gameRules.get(gameRule)).displayOnlyValue().withCustomNarration((button) -> button.createDefaultNarrationMessage().append("\n").append(narration)).create(10, 5, 44, 20, name, (button, newValue) -> AbstractGameRulesScreen.this.gameRules.set(gameRule, newValue, (MinecraftServer)null));
         this.children.add(this.checkbox);
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.extractLabel(graphics, this.getContentY(), this.getContentX());
         this.checkbox.setX(this.getContentRight() - 45);
         this.checkbox.setY(this.getContentY());
         this.checkbox.extractRenderState(graphics, mouseX, mouseY, a);
      }
   }

   public class IntegerRuleEntry extends GameRuleEntry {
      private final EditBox input;

      public IntegerRuleEntry(final Component label, final List<FormattedCharSequence> tooltip, final String narration, final GameRule<Integer> gameRule) {
         Objects.requireNonNull(AbstractGameRulesScreen.this);
         super(tooltip, label);
         this.input = new EditBox(AbstractGameRulesScreen.this.minecraft.font, 10, 5, 44, 20, label.copy().append("\n").append(narration).append("\n"));
         this.input.setValue(AbstractGameRulesScreen.this.gameRules.getAsString(gameRule));
         this.input.setResponder((v) -> {
            DataResult<Integer> value = gameRule.deserialize(v);
            if (value.isSuccess()) {
               this.input.setTextColor(-2039584);
               AbstractGameRulesScreen.this.clearInvalid(this);
               AbstractGameRulesScreen.this.gameRules.set(gameRule, (Integer)value.getOrThrow(), (MinecraftServer)null);
            } else {
               this.input.setTextColor(-65536);
               AbstractGameRulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.input);
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.extractLabel(graphics, this.getContentY(), this.getContentX());
         this.input.setX(this.getContentRight() - 45);
         this.input.setY(this.getContentY());
         this.input.extractRenderState(graphics, mouseX, mouseY, a);
      }
   }

   public class RuleList extends ContainerObjectSelectionList<RuleEntry> {
      private static final int ITEM_HEIGHT = 24;
      private final GameRules gameRules;

      public RuleList(final GameRules gameRules) {
         Objects.requireNonNull(AbstractGameRulesScreen.this);
         super(Minecraft.getInstance(), AbstractGameRulesScreen.this.width, AbstractGameRulesScreen.this.layout.getContentHeight(), AbstractGameRulesScreen.this.layout.getHeaderHeight(), 24);
         this.gameRules = gameRules;
         this.populateChildren("");
      }

      private void populateChildren(final String filter) {
         this.clearEntries();
         final Map<GameRuleCategory, Map<GameRule<?>, RuleEntry>> entries = Maps.newHashMap();
         final String lowerCaseFilter = filter.toLowerCase(Locale.ROOT);
         this.gameRules.visitGameRuleTypes(new GameRuleTypeVisitor() {
            {
               Objects.requireNonNull(RuleList.this);
            }

            public void visitBoolean(final GameRule<Boolean> gameRule) {
               this.addEntry(gameRule, (x$0, x$1, x$2, x$3) -> AbstractGameRulesScreen.this.new BooleanRuleEntry(x$0, x$1, x$2, x$3));
            }

            public void visitInteger(final GameRule<Integer> gameRule) {
               this.addEntry(gameRule, (x$0, x$1, x$2, x$3) -> AbstractGameRulesScreen.this.new IntegerRuleEntry(x$0, x$1, x$2, x$3));
            }

            private <T> void addEntry(final GameRule<T> gameRule, final EntryFactory<T> factory) {
               Component readableName = Component.translatable(gameRule.getDescriptionId());
               String descriptionKey = gameRule.getDescriptionId() + ".description";
               Optional<MutableComponent> optionalDescription = Optional.of(Component.translatable(descriptionKey)).filter(ComponentUtils::isTranslationResolvable);
               if (AbstractGameRulesScreen.RuleList.matchesFilter(gameRule.id(), readableName.getString(), gameRule.category().label().getString(), optionalDescription, lowerCaseFilter)) {
                  Component actualName = Component.literal(gameRule.id()).withStyle(ChatFormatting.YELLOW);
                  Component defaultValue = Component.translatable("editGamerule.default", Component.literal(gameRule.serialize(gameRule.defaultValue()))).withStyle(ChatFormatting.GRAY);
                  List<FormattedCharSequence> tooltip;
                  String narration;
                  if (optionalDescription.isPresent()) {
                     ImmutableList.Builder<FormattedCharSequence> result = ImmutableList.builder().add(actualName.getVisualOrderText());
                     List var10000 = AbstractGameRulesScreen.this.font.split((FormattedText)optionalDescription.get(), 150);
                     Objects.requireNonNull(result);
                     var10000.forEach(result::add);
                     tooltip = result.add(defaultValue.getVisualOrderText()).build();
                     String var11 = ((MutableComponent)optionalDescription.get()).getString();
                     narration = var11 + "\n" + defaultValue.getString();
                  } else {
                     tooltip = ImmutableList.of(actualName.getVisualOrderText(), defaultValue.getVisualOrderText());
                     narration = defaultValue.getString();
                  }

                  ((Map)entries.computeIfAbsent(gameRule.category(), (k) -> Maps.newHashMap())).put(gameRule, factory.create(readableName, tooltip, narration, gameRule));
               }
            }
         });
         entries.entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(GameRuleCategory::getDescriptionId))).forEach((e) -> {
            this.addEntry(AbstractGameRulesScreen.this.new CategoryRuleEntry(((GameRuleCategory)e.getKey()).label().withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
            ((Map)e.getValue()).entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(GameRule::getDescriptionId))).forEach((v) -> this.addEntry((RuleEntry)v.getValue()));
         });
      }

      private static boolean matchesFilter(final String gameRuleId, final String readableName, final String categoryName, final Optional<MutableComponent> optionalDescription, final String lowerCaseFilter) {
         return toLowerCaseMatchesFilter(gameRuleId, lowerCaseFilter) || toLowerCaseMatchesFilter(readableName, lowerCaseFilter) || toLowerCaseMatchesFilter(categoryName, lowerCaseFilter) || (Boolean)optionalDescription.map((description) -> toLowerCaseMatchesFilter(description.getString(), lowerCaseFilter)).orElse(false);
      }

      private static boolean toLowerCaseMatchesFilter(final String gameRuleId, final String lowerCaseFilter) {
         return gameRuleId.toLowerCase(Locale.ROOT).contains(lowerCaseFilter);
      }

      public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
         RuleEntry hovered = (RuleEntry)this.getHovered();
         if (hovered != null && hovered.tooltip != null) {
            graphics.setTooltipForNextFrame(hovered.tooltip, mouseX, mouseY);
         }

      }
   }

   @FunctionalInterface
   private interface EntryFactory<T> {
      RuleEntry create(Component name, List<FormattedCharSequence> tooltip, String narration, GameRule<T> gameRule);
   }
}
