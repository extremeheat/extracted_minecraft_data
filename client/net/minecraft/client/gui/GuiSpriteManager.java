package net.minecraft.client.gui;

import java.util.Set;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class GuiSpriteManager extends TextureAtlasHolder {
   private static final Set<MetadataSectionSerializer<?>> METADATA_SECTIONS = Set.of(AnimationMetadataSection.SERIALIZER, GuiMetadataSection.TYPE);

   public GuiSpriteManager(TextureManager var1) {
      super(var1, new ResourceLocation("textures/atlas/gui.png"), new ResourceLocation("gui"), METADATA_SECTIONS);
   }

   @Override
   public TextureAtlasSprite getSprite(ResourceLocation var1) {
      return super.getSprite(var1);
   }

   public GuiSpriteScaling getSpriteScaling(TextureAtlasSprite var1) {
      return this.getMetadata(var1).scaling();
   }

   private GuiMetadataSection getMetadata(TextureAtlasSprite var1) {
      return var1.contents().metadata().getSection(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT);
   }
}
