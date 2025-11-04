package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CreateBuffetWorldScreen extends Screen {
   private static final Component SEARCH_HINT;
   private static final int SPACING = 3;
   private static final int SEARCH_BOX_HEIGHT = 15;
   final HeaderAndFooterLayout layout;
   private final Screen parent;
   private final Consumer<Holder<Biome>> applySettings;
   final Registry<Biome> biomes;
   private BiomeList list;
   Holder<Biome> biome;
   private Button doneButton;

   public CreateBuffetWorldScreen(Screen var1, WorldCreationContext var2, Consumer<Holder<Biome>> var3) {
      super(Component.translatable("createWorld.customize.buffet.title"));
      this.parent = var1;
      this.applySettings = var3;
      Objects.requireNonNull(this.font);
      this.layout = new HeaderAndFooterLayout(this, 13 + 9 + 3 + 15, 33);
      this.biomes = var2.worldgenLoadContext().lookupOrThrow(Registries.BIOME);
      Holder var4 = (Holder)this.biomes.get(Biomes.PLAINS).or(() -> this.biomes.listElements().findAny()).orElseThrow();
      this.biome = (Holder)var2.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(var4);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(3));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      EditBox var2 = (EditBox)var1.addChild(new EditBox(this.font, 200, 15, Component.empty()));
      BiomeList var3 = new BiomeList();
      var2.setHint(SEARCH_HINT);
      Objects.requireNonNull(var3);
      var2.setResponder(var3::filterEntries);
      this.list = (BiomeList)this.layout.addToContents(var3);
      LinearLayout var4 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)var4.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.applySettings.accept(this.biome);
         this.onClose();
      }).build());
      var4.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onClose()).build());
      this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter((var1x) -> Objects.equals(var1x.biome, this.biome)).findFirst().orElse((Object)null));
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.list.updateSize(this.width, this.layout);
   }

   void updateButtonValidity() {
      this.doneButton.active = this.list.getSelected() != null;
   }

   static {
      SEARCH_HINT = Component.translatable("createWorld.customize.buffet.search").withStyle(EditBox.SEARCH_HINT_STYLE);
   }

   class BiomeList extends ObjectSelectionList<Entry> {
      BiomeList() {
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.layout.getContentHeight(), CreateBuffetWorldScreen.this.layout.getHeaderHeight(), 15);
         this.filterEntries("");
      }

      private void filterEntries(String var1) {
         Collator var2 = Collator.getInstance(Locale.getDefault());
         String var3 = var1.toLowerCase(Locale.ROOT);
         List var4 = CreateBuffetWorldScreen.this.biomes.listElements().map((var1x) -> new Entry(var1x)).sorted(Comparator.comparing((var0) -> var0.name.getString(), var2)).filter((var2x) -> var1.isEmpty() || var2x.name.getString().toLowerCase(Locale.ROOT).contains(var3)).toList();
         this.replaceEntries(var4);
         this.refreshScrollAmount();
      }

      public void setSelected(Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            CreateBuffetWorldScreen.this.biome = var1.biome;
         }

         CreateBuffetWorldScreen.this.updateButtonValidity();
      }

      class Entry extends ObjectSelectionList.Entry<Entry> {
         final Holder.Reference<Biome> biome;
         final Component name;

         public Entry(final Holder.Reference<Biome> var2) {
            super();
            this.biome = var2;
            Identifier var3 = var2.key().identifier();
            String var4 = var3.toLanguageKey("biome");
            if (Language.getInstance().has(var4)) {
               this.name = Component.translatable(var4);
            } else {
               this.name = Component.literal(var3.toString());
            }

         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            var1.drawString(CreateBuffetWorldScreen.this.font, (Component)this.name, this.getContentX() + 5, this.getContentY() + 2, -1);
         }

         public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
            BiomeList.this.setSelected(this);
            return super.mouseClicked(var1, var2);
         }
      }
   }
}
