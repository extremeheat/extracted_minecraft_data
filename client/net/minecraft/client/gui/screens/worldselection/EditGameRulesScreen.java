package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;

public class EditGameRulesScreen extends Screen {
   private final Consumer<Optional<GameRules>> exitCallback;
   private EditGameRulesScreen.RuleList rules;
   private final Set<EditGameRulesScreen.RuleEntry> invalidEntries = Sets.newHashSet();
   private Button doneButton;
   @Nullable
   private List<FormattedCharSequence> tooltip;
   private final GameRules gameRules;

   public EditGameRulesScreen(GameRules var1, Consumer<Optional<GameRules>> var2) {
      super(Component.translatable("editGamerule.title"));
      this.gameRules = var1;
      this.exitCallback = var2;
   }

   @Override
   protected void init() {
      this.rules = new EditGameRulesScreen.RuleList(this.gameRules);
      this.addWidget(this.rules);
      GridLayout.RowHelper var1 = new GridLayout().columnSpacing(10).createRowHelper(2);
      this.doneButton = var1.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.exitCallback.accept(Optional.of(this.gameRules))).build());
      var1.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.exitCallback.accept(Optional.empty())).build());
      var1.getGrid().visitWidgets(var1x -> {
      });
      var1.getGrid().setPosition(this.width / 2 - 155, this.height - 28);
      var1.getGrid().arrangeElements();
   }

   @Override
   public void onClose() {
      this.exitCallback.accept(Optional.empty());
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.tooltip = null;
      this.rules.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      super.render(var1, var2, var3, var4);
   }

   private void updateDoneButton() {
      this.doneButton.active = this.invalidEntries.isEmpty();
   }

   void markInvalid(EditGameRulesScreen.RuleEntry var1) {
      this.invalidEntries.add(var1);
      this.updateDoneButton();
   }

   void clearInvalid(EditGameRulesScreen.RuleEntry var1) {
      this.invalidEntries.remove(var1);
      this.updateDoneButton();
   }

   public class BooleanRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final CycleButton<Boolean> checkbox;

      public BooleanRuleEntry(Component var2, List<FormattedCharSequence> var3, String var4, GameRules.BooleanValue var5) {
         super(var3, var2);
         this.checkbox = CycleButton.onOffBuilder(var5.get())
            .displayOnlyValue()
            .withCustomNarration(var1x -> var1x.createDefaultNarrationMessage().append("\n").append(var4))
            .create(10, 5, 44, 20, var2, (var1x, var2x) -> var5.set(var2x, null));
         this.children.add(this.checkbox);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderLabel(var1, var3, var4);
         this.checkbox.setX(var4 + var5 - 45);
         this.checkbox.setY(var3);
         this.checkbox.render(var1, var7, var8, var10);
      }
   }

   public class CategoryRuleEntry extends EditGameRulesScreen.RuleEntry {
      final Component label;

      public CategoryRuleEntry(Component var2) {
         super(null);
         this.label = var2;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawCenteredString(EditGameRulesScreen.this.minecraft.font, this.label, var4 + var5 / 2, var3 + 5, 16777215);
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return ImmutableList.of();
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            @Override
            public NarratableEntry.NarrationPriority narrationPriority() {
               return NarratableEntry.NarrationPriority.HOVERED;
            }

            @Override
            public void updateNarration(NarrationElementOutput var1) {
               var1.add(NarratedElementType.TITLE, CategoryRuleEntry.this.label);
            }
         });
      }
   }

   @FunctionalInterface
   interface EntryFactory<T extends GameRules.Value<T>> {
      EditGameRulesScreen.RuleEntry create(Component var1, List<FormattedCharSequence> var2, String var3, T var4);
   }

   public abstract class GameRuleEntry extends EditGameRulesScreen.RuleEntry {
      private final List<FormattedCharSequence> label;
      protected final List<AbstractWidget> children = Lists.newArrayList();

      public GameRuleEntry(@Nullable List<FormattedCharSequence> var2, Component var3) {
         super(var2);
         this.label = EditGameRulesScreen.this.minecraft.font.split(var3, 175);
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      protected void renderLabel(GuiGraphics var1, int var2, int var3) {
         if (this.label.size() == 1) {
            var1.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(0), var3, var2 + 5, 16777215, false);
         } else if (this.label.size() >= 2) {
            var1.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(0), var3, var2, 16777215, false);
            var1.drawString(EditGameRulesScreen.this.minecraft.font, this.label.get(1), var3, var2 + 10, 16777215, false);
         }
      }
   }

   public class IntegerRuleEntry extends EditGameRulesScreen.GameRuleEntry {
      private final EditBox input;

      public IntegerRuleEntry(Component var2, List<FormattedCharSequence> var3, String var4, GameRules.IntegerValue var5) {
         super(var3, var2);
         this.input = new EditBox(EditGameRulesScreen.this.minecraft.font, 10, 5, 42, 20, var2.copy().append("\n").append(var4).append("\n"));
         this.input.setValue(Integer.toString(var5.get()));
         this.input.setResponder(var2x -> {
            if (var5.tryDeserialize(var2x)) {
               this.input.setTextColor(14737632);
               EditGameRulesScreen.this.clearInvalid(this);
            } else {
               this.input.setTextColor(16711680);
               EditGameRulesScreen.this.markInvalid(this);
            }
         });
         this.children.add(this.input);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderLabel(var1, var3, var4);
         this.input.setX(var4 + var5 - 44);
         this.input.setY(var3);
         this.input.render(var1, var7, var8, var10);
      }
   }

   public abstract static class RuleEntry extends ContainerObjectSelectionList.Entry<EditGameRulesScreen.RuleEntry> {
      @Nullable
      final List<FormattedCharSequence> tooltip;

      public RuleEntry(@Nullable List<FormattedCharSequence> var1) {
         super();
         this.tooltip = var1;
      }
   }

   public class RuleList extends ContainerObjectSelectionList<EditGameRulesScreen.RuleEntry> {
      public RuleList(final GameRules var2) {
         super(
            EditGameRulesScreen.this.minecraft, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24
         );
         final HashMap var3 = Maps.newHashMap();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override
            public void visitBoolean(GameRules.Key<GameRules.BooleanValue> var1, GameRules.Type<GameRules.BooleanValue> var2x) {
               this.addEntry(var1, (var1x, var2xxx, var3xx, var4) -> EditGameRulesScreen.this.new BooleanRuleEntry(var1x, var2xxx, var3xx, var4));
            }

            @Override
            public void visitInteger(GameRules.Key<GameRules.IntegerValue> var1, GameRules.Type<GameRules.IntegerValue> var2x) {
               this.addEntry(var1, (var1x, var2xxx, var3xx, var4) -> EditGameRulesScreen.this.new IntegerRuleEntry(var1x, var2xxx, var3xx, var4));
            }

            private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> var1, EditGameRulesScreen.EntryFactory<T> var2x) {
               MutableComponent var3x = Component.translatable(var1.getDescriptionId());
               MutableComponent var4 = Component.literal(var1.getId()).withStyle(ChatFormatting.YELLOW);
               GameRules.Value var5 = var2.getRule(var1);
               String var6 = var5.serialize();
               MutableComponent var7 = Component.translatable("editGamerule.default", Component.literal(var6)).withStyle(ChatFormatting.GRAY);
               String var8 = var1.getDescriptionId() + ".description";
               ImmutableList var9;
               String var10;
               if (I18n.exists(var8)) {
                  Builder var11 = ImmutableList.builder().add(var4.getVisualOrderText());
                  MutableComponent var12 = Component.translatable(var8);
                  EditGameRulesScreen.this.font.split(var12, 150).forEach(var11::add);
                  var9 = var11.add(var7.getVisualOrderText()).build();
                  var10 = var12.getString() + "\n" + var7.getString();
               } else {
                  var9 = ImmutableList.of(var4.getVisualOrderText(), var7.getVisualOrderText());
                  var10 = var7.getString();
               }

               var3.computeIfAbsent(var1.getCategory(), var0 -> Maps.newHashMap()).put(var1, var2x.create(var3x, var9, var10, (T)var5));
            }
         });
         var3.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(
               var1x -> {
                  this.addEntry(
                     EditGameRulesScreen.this.new CategoryRuleEntry(
                        Component.translatable(((GameRules.Category)var1x.getKey()).getDescriptionId()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)
                     )
                  );
                  ((Map)var1x.getValue())
                     .entrySet()
                     .stream()
                     .sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getId)))
                     .forEach(var1xx -> this.addEntry((EditGameRulesScreen.RuleEntry)var1xx.getValue()));
               }
            );
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, float var4) {
         super.render(var1, var2, var3, var4);
         EditGameRulesScreen.RuleEntry var5 = this.getHovered();
         if (var5 != null && var5.tooltip != null) {
            EditGameRulesScreen.this.setTooltipForNextRenderPass(var5.tooltip);
         }
      }
   }
}
