package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
   private final HeaderAndFooterLayout layout;
   private final Screen parent;
   private final Consumer<Holder<Biome>> applySettings;
   private final Registry<Biome> biomes;
   private BiomeList list;
   private Holder<Biome> biome;
   private Button doneButton;

   public CreateBuffetWorldScreen(final Screen parent, final WorldCreationContext settings, final Consumer<Holder<Biome>> applySettings) {
      super(Component.translatable("createWorld.customize.buffet.title"));
      this.parent = parent;
      this.applySettings = applySettings;
      Objects.requireNonNull(this.font);
      this.layout = new HeaderAndFooterLayout(this, 13 + 9 + 3 + 15, 33);
      this.biomes = settings.worldgenLoadContext().lookupOrThrow(Registries.BIOME);
      Holder<Biome> defaultBiome = (Holder)this.biomes.get(Biomes.PLAINS).or(() -> this.biomes.listElements().findAny()).orElseThrow();
      this.biome = (Holder)settings.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(defaultBiome);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   protected void init() {
      LinearLayout header = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(3));
      header.defaultCellSetting().alignHorizontallyCenter();
      header.addChild(new StringWidget(this.getTitle(), this.font));
      EditBox search = (EditBox)header.addChild(new EditBox(this.font, 200, 15, Component.empty()));
      BiomeList biomeList = new BiomeList();
      search.setHint(SEARCH_HINT);
      Objects.requireNonNull(biomeList);
      search.setResponder(biomeList::filterEntries);
      this.list = (BiomeList)this.layout.addToContents(biomeList);
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> {
         this.applySettings.accept(this.biome);
         this.onClose();
      }).build());
      footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.onClose()).build());
      this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter((e) -> Objects.equals(e.biome, this.biome)).findFirst().orElse((Object)null));
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.list.updateSize(this.width, this.layout);
   }

   private void updateButtonValidity() {
      this.doneButton.active = this.list.getSelected() != null;
   }

   static {
      SEARCH_HINT = Component.translatable("createWorld.customize.buffet.search").withStyle(EditBox.SEARCH_HINT_STYLE);
   }

   private class BiomeList extends ObjectSelectionList<Entry> {
      private BiomeList() {
         Objects.requireNonNull(CreateBuffetWorldScreen.this);
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.layout.getContentHeight(), CreateBuffetWorldScreen.this.layout.getHeaderHeight(), 15);
         this.filterEntries("");
      }

      private void filterEntries(final String filter) {
         Collator localeCollator = Collator.getInstance(Locale.getDefault());
         String lowercaseFilter = filter.toLowerCase(Locale.ROOT);
         List<Entry> list = CreateBuffetWorldScreen.this.biomes.listElements().map((x$0) -> new Entry(x$0)).sorted(Comparator.comparing((e) -> e.name.getString(), localeCollator)).filter((entry) -> filter.isEmpty() || entry.name.getString().toLowerCase(Locale.ROOT).contains(lowercaseFilter)).toList();
         this.replaceEntries(list);
         this.refreshScrollAmount();
      }

      public void setSelected(final Entry selected) {
         super.setSelected(selected);
         if (selected != null) {
            CreateBuffetWorldScreen.this.biome = selected.biome;
         }

         CreateBuffetWorldScreen.this.updateButtonValidity();
      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
         private final Holder.Reference<Biome> biome;
         private final Component name;

         public Entry(final Holder.Reference<Biome> biome) {
            Objects.requireNonNull(BiomeList.this);
            super();
            this.biome = biome;
            Identifier id = biome.key().identifier();
            String translationKey = id.toLanguageKey("biome");
            if (Language.getInstance().has(translationKey)) {
               this.name = Component.translatable(translationKey);
            } else {
               this.name = Component.literal(id.toString());
            }

         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            graphics.text(CreateBuffetWorldScreen.this.font, (Component)this.name, this.getContentX() + 5, this.getContentY() + 2, -1);
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            BiomeList.this.setSelected(this);
            return super.mouseClicked(event, doubleClick);
         }
      }
   }
}
