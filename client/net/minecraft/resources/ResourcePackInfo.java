package net.minecraft.resources;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackInfo {
   private static final Logger field_195799_a = LogManager.getLogger();
   private static final PackMetadataSection field_212500_b;
   private final String field_195800_b;
   private final Supplier<IResourcePack> field_195801_c;
   private final ITextComponent field_195802_d;
   private final ITextComponent field_195803_e;
   private final PackCompatibility field_195804_f;
   private final ResourcePackInfo.Priority field_195805_g;
   private final boolean field_195806_h;
   private final boolean field_195807_i;

   @Nullable
   public static <T extends ResourcePackInfo> T func_195793_a(String var0, boolean var1, Supplier<IResourcePack> var2, ResourcePackInfo.IFactory<T> var3, ResourcePackInfo.Priority var4) {
      try {
         IResourcePack var5 = (IResourcePack)var2.get();
         Throwable var6 = null;

         ResourcePackInfo var8;
         try {
            PackMetadataSection var7 = (PackMetadataSection)var5.func_195760_a(PackMetadataSection.field_198964_a);
            if (var1 && var7 == null) {
               field_195799_a.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
               var7 = field_212500_b;
            }

            if (var7 == null) {
               field_195799_a.warn("Couldn't find pack meta for pack {}", var0);
               return null;
            }

            var8 = var3.create(var0, var1, var2, var5, var7, var4);
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return var8;
      } catch (IOException var21) {
         field_195799_a.warn("Couldn't get pack info for: {}", var21.toString());
         return null;
      }
   }

   public ResourcePackInfo(String var1, boolean var2, Supplier<IResourcePack> var3, ITextComponent var4, ITextComponent var5, PackCompatibility var6, ResourcePackInfo.Priority var7, boolean var8) {
      super();
      this.field_195800_b = var1;
      this.field_195801_c = var3;
      this.field_195802_d = var4;
      this.field_195803_e = var5;
      this.field_195804_f = var6;
      this.field_195806_h = var2;
      this.field_195805_g = var7;
      this.field_195807_i = var8;
   }

   public ResourcePackInfo(String var1, boolean var2, Supplier<IResourcePack> var3, IResourcePack var4, PackMetadataSection var5, ResourcePackInfo.Priority var6) {
      this(var1, var2, var3, new TextComponentString(var4.func_195762_a()), var5.func_198963_a(), PackCompatibility.func_198969_a(var5.func_198962_b()), var6, false);
   }

   public ITextComponent func_195789_b() {
      return this.field_195802_d;
   }

   public ITextComponent func_195795_c() {
      return this.field_195803_e;
   }

   public ITextComponent func_195794_a(boolean var1) {
      return TextComponentUtils.func_197676_a(new TextComponentString(this.field_195800_b)).func_211710_a((var2) -> {
         var2.func_150238_a(var1 ? TextFormatting.GREEN : TextFormatting.RED).func_179989_a(StringArgumentType.escapeIfRequired(this.field_195800_b)).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new TextComponentString("")).func_150257_a(this.field_195802_d).func_150258_a("\n").func_150257_a(this.field_195803_e)));
      });
   }

   public PackCompatibility func_195791_d() {
      return this.field_195804_f;
   }

   public IResourcePack func_195796_e() {
      return (IResourcePack)this.field_195801_c.get();
   }

   public String func_195790_f() {
      return this.field_195800_b;
   }

   public boolean func_195797_g() {
      return this.field_195806_h;
   }

   public boolean func_195798_h() {
      return this.field_195807_i;
   }

   public ResourcePackInfo.Priority func_195792_i() {
      return this.field_195805_g;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ResourcePackInfo)) {
         return false;
      } else {
         ResourcePackInfo var2 = (ResourcePackInfo)var1;
         return this.field_195800_b.equals(var2.field_195800_b);
      }
   }

   public int hashCode() {
      return this.field_195800_b.hashCode();
   }

   static {
      field_212500_b = new PackMetadataSection((new TextComponentTranslation("resourcePack.broken_assets", new Object[0])).func_211709_a(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}), 4);
   }

   public static enum Priority {
      TOP,
      BOTTOM;

      private Priority() {
      }

      public <T, P extends ResourcePackInfo> int func_198993_a(List<T> var1, T var2, Function<T, P> var3, boolean var4) {
         ResourcePackInfo.Priority var5 = var4 ? this.func_198992_a() : this;
         int var6;
         ResourcePackInfo var7;
         if (var5 == BOTTOM) {
            for(var6 = 0; var6 < var1.size(); ++var6) {
               var7 = (ResourcePackInfo)var3.apply(var1.get(var6));
               if (!var7.func_195798_h() || var7.func_195792_i() != this) {
                  break;
               }
            }

            var1.add(var6, var2);
            return var6;
         } else {
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               var7 = (ResourcePackInfo)var3.apply(var1.get(var6));
               if (!var7.func_195798_h() || var7.func_195792_i() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public ResourcePackInfo.Priority func_198992_a() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   @FunctionalInterface
   public interface IFactory<T extends ResourcePackInfo> {
      @Nullable
      T create(String var1, boolean var2, Supplier<IResourcePack> var3, IResourcePack var4, PackMetadataSection var5, ResourcePackInfo.Priority var6);
   }
}
