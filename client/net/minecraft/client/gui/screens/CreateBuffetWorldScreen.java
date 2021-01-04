package net.minecraft.client.gui.screens;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.chunk.ChunkGeneratorType;

public class CreateBuffetWorldScreen extends Screen {
   private static final List<ResourceLocation> GENERATORS;
   private final CreateWorldScreen parent;
   private final CompoundTag optionsTag;
   private CreateBuffetWorldScreen.BiomeList list;
   private int generatorIndex;
   private Button doneButton;

   public CreateBuffetWorldScreen(CreateWorldScreen var1, CompoundTag var2) {
      super(new TranslatableComponent("createWorld.customize.buffet.title", new Object[0]));
      this.parent = var1;
      this.optionsTag = var2;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addButton(new Button((this.width - 200) / 2, 40, 200, 20, I18n.get("createWorld.customize.buffet.generatortype") + " " + I18n.get(Util.makeDescriptionId("generator", (ResourceLocation)GENERATORS.get(this.generatorIndex))), (var1) -> {
         ++this.generatorIndex;
         if (this.generatorIndex >= GENERATORS.size()) {
            this.generatorIndex = 0;
         }

         var1.setMessage(I18n.get("createWorld.customize.buffet.generatortype") + " " + I18n.get(Util.makeDescriptionId("generator", (ResourceLocation)GENERATORS.get(this.generatorIndex))));
      }));
      this.list = new CreateBuffetWorldScreen.BiomeList();
      this.children.add(this.list);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("gui.done"), (var1) -> {
         this.parent.levelTypeOptions = this.saveOptions();
         this.minecraft.setScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.loadOptions();
      this.updateButtonValidity();
   }

   private void loadOptions() {
      int var2;
      if (this.optionsTag.contains("chunk_generator", 10) && this.optionsTag.getCompound("chunk_generator").contains("type", 8)) {
         ResourceLocation var1 = new ResourceLocation(this.optionsTag.getCompound("chunk_generator").getString("type"));

         for(var2 = 0; var2 < GENERATORS.size(); ++var2) {
            if (((ResourceLocation)GENERATORS.get(var2)).equals(var1)) {
               this.generatorIndex = var2;
               break;
            }
         }
      }

      if (this.optionsTag.contains("biome_source", 10) && this.optionsTag.getCompound("biome_source").contains("biomes", 9)) {
         ListTag var4 = this.optionsTag.getCompound("biome_source").getList("biomes", 8);

         for(var2 = 0; var2 < var4.size(); ++var2) {
            ResourceLocation var3 = new ResourceLocation(var4.getString(var2));
            this.list.setSelected((CreateBuffetWorldScreen.BiomeList.Entry)this.list.children().stream().filter((var1x) -> {
               return Objects.equals(var1x.key, var3);
            }).findFirst().orElse((Object)null));
         }
      }

      this.optionsTag.remove("chunk_generator");
      this.optionsTag.remove("biome_source");
   }

   private CompoundTag saveOptions() {
      CompoundTag var1 = new CompoundTag();
      CompoundTag var2 = new CompoundTag();
      var2.putString("type", Registry.BIOME_SOURCE_TYPE.getKey(BiomeSourceType.FIXED).toString());
      CompoundTag var3 = new CompoundTag();
      ListTag var4 = new ListTag();
      var4.add(new StringTag(((CreateBuffetWorldScreen.BiomeList.Entry)this.list.getSelected()).key.toString()));
      var3.put("biomes", var4);
      var2.put("options", var3);
      CompoundTag var5 = new CompoundTag();
      CompoundTag var6 = new CompoundTag();
      var5.putString("type", ((ResourceLocation)GENERATORS.get(this.generatorIndex)).toString());
      var6.putString("default_block", "minecraft:stone");
      var6.putString("default_fluid", "minecraft:water");
      var5.put("options", var6);
      var1.put("biome_source", var2);
      var1.put("chunk_generator", var5);
      return var1;
   }

   public void updateButtonValidity() {
      this.doneButton.active = this.list.getSelected() != null;
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      this.drawCenteredString(this.font, I18n.get("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
      this.drawCenteredString(this.font, I18n.get("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
      super.render(var1, var2, var3);
   }

   static {
      GENERATORS = (List)Registry.CHUNK_GENERATOR_TYPE.keySet().stream().filter((var0) -> {
         return ((ChunkGeneratorType)Registry.CHUNK_GENERATOR_TYPE.get(var0)).isPublic();
      }).collect(Collectors.toList());
   }

   class BiomeList extends ObjectSelectionList<CreateBuffetWorldScreen.BiomeList.Entry> {
      private BiomeList() {
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 80, CreateBuffetWorldScreen.this.height - 37, 16);
         Registry.BIOME.keySet().stream().sorted(Comparator.comparing((var0) -> {
            return ((Biome)Registry.BIOME.get(var0)).getName().getString();
         })).forEach((var1x) -> {
            this.addEntry(new CreateBuffetWorldScreen.BiomeList.Entry(var1x));
         });
      }

      protected boolean isFocused() {
         return CreateBuffetWorldScreen.this.getFocused() == this;
      }

      public void setSelected(@Nullable CreateBuffetWorldScreen.BiomeList.Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((Biome)Registry.BIOME.get(var1.key)).getName().getString()})).getString());
         }

      }

      protected void moveSelection(int var1) {
         super.moveSelection(var1);
         CreateBuffetWorldScreen.this.updateButtonValidity();
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
         this.setSelected((CreateBuffetWorldScreen.BiomeList.Entry)var1);
      }

      // $FF: synthetic method
      BiomeList(Object var2) {
         this();
      }

      class Entry extends ObjectSelectionList.Entry<CreateBuffetWorldScreen.BiomeList.Entry> {
         private final ResourceLocation key;

         public Entry(ResourceLocation var2) {
            super();
            this.key = var2;
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            BiomeList.this.drawString(CreateBuffetWorldScreen.this.font, ((Biome)Registry.BIOME.get(this.key)).getName().getString(), var3 + 5, var2 + 2, 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if (var5 == 0) {
               BiomeList.this.setSelected(this);
               CreateBuffetWorldScreen.this.updateButtonValidity();
               return true;
            } else {
               return false;
            }
         }
      }
   }
}
