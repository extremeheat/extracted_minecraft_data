package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;

public interface BakedGlyph {
   GlyphInfo info();

   @Nullable
   TextRenderable createGlyph(float var1, float var2, int var3, int var4, Style var5, float var6, float var7);
}
