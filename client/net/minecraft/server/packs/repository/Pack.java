package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pack implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String id;
   private final Supplier<PackResources> supplier;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final Pack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final PackSource packSource;

   @Nullable
   public static Pack create(String var0, boolean var1, Supplier<PackResources> var2, Pack.PackConstructor var3, Pack.Position var4, PackSource var5) {
      try {
         PackResources var6 = (PackResources)var2.get();
         Throwable var7 = null;

         Pack var9;
         try {
            PackMetadataSection var8 = (PackMetadataSection)var6.getMetadataSection(PackMetadataSection.SERIALIZER);
            if (var8 == null) {
               LOGGER.warn("Couldn't find pack meta for pack {}", var0);
               return null;
            }

            var9 = var3.create(var0, new TextComponent(var6.getName()), var1, var2, var8, var4, var5);
         } catch (Throwable var20) {
            var7 = var20;
            throw var20;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var19) {
                     var7.addSuppressed(var19);
                  }
               } else {
                  var6.close();
               }
            }

         }

         return var9;
      } catch (IOException var22) {
         LOGGER.warn("Couldn't get pack info for: {}", var22.toString());
         return null;
      }
   }

   public Pack(String var1, boolean var2, Supplier<PackResources> var3, Component var4, Component var5, PackCompatibility var6, Pack.Position var7, boolean var8, PackSource var9) {
      super();
      this.id = var1;
      this.supplier = var3;
      this.title = var4;
      this.description = var5;
      this.compatibility = var6;
      this.required = var2;
      this.defaultPosition = var7;
      this.fixedPosition = var8;
      this.packSource = var9;
   }

   public Pack(String var1, Component var2, boolean var3, Supplier<PackResources> var4, PackMetadataSection var5, PackType var6, Pack.Position var7, PackSource var8) {
      this(var1, var3, var4, var2, var5.getDescription(), PackCompatibility.forMetadata(var5, var6), var7, false, var8);
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean var1) {
      return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(new TextComponent(this.id))).withStyle((var2) -> {
         return var2.withColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new TextComponent("")).append(this.title).append("\n").append(this.description)));
      });
   }

   public PackCompatibility getCompatibility() {
      return this.compatibility;
   }

   public PackResources open() {
      return (PackResources)this.supplier.get();
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public Pack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public PackSource getPackSource() {
      return this.packSource;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Pack)) {
         return false;
      } else {
         Pack var2 = (Pack)var1;
         return this.id.equals(var2.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public void close() {
   }

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T> int insert(List<T> var1, T var2, Function<T, Pack> var3, boolean var4) {
         Pack.Position var5 = var4 ? this.opposite() : this;
         int var6;
         Pack var7;
         if (var5 == BOTTOM) {
            for(var6 = 0; var6 < var1.size(); ++var6) {
               var7 = (Pack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6, var2);
            return var6;
         } else {
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               var7 = (Pack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public Pack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   @FunctionalInterface
   public interface PackConstructor {
      @Nullable
      Pack create(String var1, Component var2, boolean var3, Supplier<PackResources> var4, PackMetadataSection var5, Pack.Position var6, PackSource var7);
   }
}
