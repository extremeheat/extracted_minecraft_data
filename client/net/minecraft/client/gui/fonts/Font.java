package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Font implements AutoCloseable {
   private static final Logger field_211189_a = LogManager.getLogger();
   private static final EmptyGlyph field_212460_b = new EmptyGlyph();
   private static final IGlyph field_212461_c = () -> {
      return 4.0F;
   };
   private static final Random field_212462_d = new Random();
   private final TextureManager field_211191_c;
   private final ResourceLocation field_211192_d;
   private TexturedGlyph field_211572_d;
   private final List<IGlyphProvider> field_211194_f = Lists.newArrayList();
   private final Char2ObjectMap<TexturedGlyph> field_212463_j = new Char2ObjectOpenHashMap();
   private final Char2ObjectMap<IGlyph> field_211195_g = new Char2ObjectOpenHashMap();
   private final Int2ObjectMap<CharList> field_211196_h = new Int2ObjectOpenHashMap();
   private final List<FontTexture> field_211197_i = Lists.newArrayList();

   public Font(TextureManager var1, ResourceLocation var2) {
      super();
      this.field_211191_c = var1;
      this.field_211192_d = var2;
   }

   public void func_211570_a(List<IGlyphProvider> var1) {
      Iterator var2 = this.field_211194_f.iterator();

      while(var2.hasNext()) {
         IGlyphProvider var3 = (IGlyphProvider)var2.next();
         var3.close();
      }

      this.field_211194_f.clear();
      this.func_211571_a();
      this.field_211197_i.clear();
      this.field_212463_j.clear();
      this.field_211195_g.clear();
      this.field_211196_h.clear();
      this.field_211572_d = this.func_211185_a(DefaultGlyph.INSTANCE);
      HashSet var7 = Sets.newHashSet();

      for(char var8 = 0; var8 < '\uffff'; ++var8) {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            IGlyphProvider var5 = (IGlyphProvider)var4.next();
            Object var6 = var8 == ' ' ? field_212461_c : var5.func_212248_a(var8);
            if (var6 != null) {
               var7.add(var5);
               if (var6 != DefaultGlyph.INSTANCE) {
                  ((CharList)this.field_211196_h.computeIfAbsent(MathHelper.func_76123_f(((IGlyph)var6).getAdvance(false)), (var0) -> {
                     return new CharArrayList();
                  })).add(var8);
               }
               break;
            }
         }
      }

      Stream var10000 = var1.stream();
      var7.getClass();
      var10000 = var10000.filter(var7::contains);
      List var10001 = this.field_211194_f;
      var10000.forEach(var10001::add);
   }

   public void close() {
      this.func_211571_a();
   }

   public void func_211571_a() {
      Iterator var1 = this.field_211197_i.iterator();

      while(var1.hasNext()) {
         FontTexture var2 = (FontTexture)var1.next();
         var2.close();
      }

   }

   public IGlyph func_211184_b(char var1) {
      return (IGlyph)this.field_211195_g.computeIfAbsent(var1, (var1x) -> {
         return (IGlyph)(var1x == 32 ? field_212461_c : this.func_212455_c((char)var1x));
      });
   }

   private IGlyphInfo func_212455_c(char var1) {
      Iterator var2 = this.field_211194_f.iterator();

      IGlyphInfo var4;
      do {
         if (!var2.hasNext()) {
            return DefaultGlyph.INSTANCE;
         }

         IGlyphProvider var3 = (IGlyphProvider)var2.next();
         var4 = var3.func_212248_a(var1);
      } while(var4 == null);

      return var4;
   }

   public TexturedGlyph func_211187_a(char var1) {
      return (TexturedGlyph)this.field_212463_j.computeIfAbsent(var1, (var1x) -> {
         return (TexturedGlyph)(var1x == 32 ? field_212460_b : this.func_211185_a(this.func_212455_c((char)var1x)));
      });
   }

   private TexturedGlyph func_211185_a(IGlyphInfo var1) {
      Iterator var2 = this.field_211197_i.iterator();

      TexturedGlyph var4;
      do {
         if (!var2.hasNext()) {
            FontTexture var5 = new FontTexture(new ResourceLocation(this.field_211192_d.func_110624_b(), this.field_211192_d.func_110623_a() + "/" + this.field_211197_i.size()), var1.func_211579_f());
            this.field_211197_i.add(var5);
            this.field_211191_c.func_110579_a(var5.func_211132_a(), var5);
            TexturedGlyph var6 = var5.func_211131_a(var1);
            return var6 == null ? this.field_211572_d : var6;
         }

         FontTexture var3 = (FontTexture)var2.next();
         var4 = var3.func_211131_a(var1);
      } while(var4 == null);

      return var4;
   }

   public TexturedGlyph func_211188_a(IGlyph var1) {
      CharList var2 = (CharList)this.field_211196_h.get(MathHelper.func_76123_f(var1.getAdvance(false)));
      return var2 != null && !var2.isEmpty() ? this.func_211187_a(var2.get(field_212462_d.nextInt(var2.size()))) : this.field_211572_d;
   }
}
