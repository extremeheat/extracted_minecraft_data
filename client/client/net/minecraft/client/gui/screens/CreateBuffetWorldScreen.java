package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class CreateBuffetWorldScreen extends Screen {
   private static final Component BIOME_SELECT_INFO = Component.translatable("createWorld.customize.buffet.biome").withColor(-8355712);
   private static final int SPACING = 8;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final Consumer<Holder<Biome>> applySettings;
   final Registry<Biome> biomes;
   private CreateBuffetWorldScreen.BiomeList list;
   Holder<Biome> biome;
   private Button doneButton;

   public CreateBuffetWorldScreen(Screen var1, WorldCreationContext var2, Consumer<Holder<Biome>> var3) {
      super(Component.translatable("createWorld.customize.buffet.title"));
      this.parent = var1;
      this.applySettings = var3;
      this.biomes = var2.worldgenLoadContext().registryOrThrow(Registries.BIOME);
      Holder var4 = this.biomes.getHolder(Biomes.PLAINS).or(() -> this.biomes.holders().findAny()).orElseThrow();
      this.biome = var2.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(var4);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   @Override
   protected void init() {
      LinearLayout var1 = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      var1.addChild(new StringWidget(BIOME_SELECT_INFO, this.font));
      this.list = this.layout.addToContents(new CreateBuffetWorldScreen.BiomeList());
      LinearLayout var2 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = var2.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> {
         this.applySettings.accept(this.biome);
         this.onClose();
      }).build());
      var2.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).build());
      this.list.setSelected(this.list.children().stream().filter(var1x -> Objects.equals(var1x.biome, this.biome)).findFirst().orElse(null));
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      this.list.updateSize(this.width, this.layout);
   }

   void updateButtonValidity() {
      this.doneButton.active = this.list.getSelected() != null;
   }

   class BiomeList extends ObjectSelectionList<CreateBuffetWorldScreen.BiomeList.Entry> {
      BiomeList() {
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height - 77, 40, 16);
         Collator var2 = Collator.getInstance(Locale.getDefault());
         CreateBuffetWorldScreen.this.biomes
            .holders()
            .map(var1 -> new CreateBuffetWorldScreen.BiomeList.Entry((Holder.Reference<Biome>)var1))
            .sorted(Comparator.comparing(var0 -> var0.name.getString(), var2))
            .forEach(var1 -> this.addEntry(var1));
      }

      public void setSelected(@Nullable CreateBuffetWorldScreen.BiomeList.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            CreateBuffetWorldScreen.this.biome = var1.biome;
         }

         CreateBuffetWorldScreen.this.updateButtonValidity();
      }

      class Entry extends ObjectSelectionList.Entry<CreateBuffetWorldScreen.BiomeList.Entry> {
         final Holder.Reference<Biome> biome;
         final Component name;

         public Entry(final Holder.Reference<Biome> param2) {
            super();
            this.biome = nullx;
            ResourceLocation var3 = nullx.key().location();
            String var4 = var3.toLanguageKey("biome");
            if (Language.getInstance().has(var4)) {
               this.name = Component.translatable(var4);
            } else {
               this.name = Component.literal(var3.toString());
            }
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawString(CreateBuffetWorldScreen.this.font, this.name, var4 + 5, var3 + 2, 16777215);
         }

         @Override
         public boolean mouseClicked(double var1, double var3, int var5) {
            BiomeList.this.setSelected(this);
            return super.mouseClicked(var1, var3, var5);
         }
      }
   }
}
