package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String id;
   private final Supplier<PackResources> supplier;
   private final Component title;
   private final Component description;
   private final PackCompatibility compatibility;
   private final Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final PackSource packSource;

   @Nullable
   public static Pack create(String var0, boolean var1, Supplier<PackResources> var2, PackConstructor var3, Position var4, PackSource var5) {
      try {
         PackResources var6 = (PackResources)var2.get();

         Pack var8;
         label54: {
            try {
               PackMetadataSection var7 = (PackMetadataSection)var6.getMetadataSection(PackMetadataSection.SERIALIZER);
               if (var7 != null) {
                  var8 = var3.create(var0, Component.literal(var6.getName()), var1, var2, var7, var4, var5);
                  break label54;
               }

               LOGGER.warn("Couldn't find pack meta for pack {}", var0);
            } catch (Throwable var10) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var6 != null) {
               var6.close();
            }

            return null;
         }

         if (var6 != null) {
            var6.close();
         }

         return var8;
      } catch (IOException var11) {
         LOGGER.warn("Couldn't get pack info for: {}", var11.toString());
         return null;
      }
   }

   public Pack(String var1, boolean var2, Supplier<PackResources> var3, Component var4, Component var5, PackCompatibility var6, Position var7, boolean var8, PackSource var9) {
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

   public Pack(String var1, Component var2, boolean var3, Supplier<PackResources> var4, PackMetadataSection var5, PackType var6, Position var7, PackSource var8) {
      this(var1, var3, var4, var2, var5.getDescription(), PackCompatibility.forMetadata(var5, var6), var7, false, var8);
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getChatLink(boolean var1) {
      return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(Component.literal(this.id))).withStyle((var2) -> {
         return var2.withColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(this.description)));
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

   public Position getDefaultPosition() {
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

   @FunctionalInterface
   public interface PackConstructor {
      @Nullable
      Pack create(String var1, Component var2, boolean var3, Supplier<PackResources> var4, PackMetadataSection var5, Position var6, PackSource var7);
   }

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T> int insert(List<T> var1, T var2, Function<T, Pack> var3, boolean var4) {
         Position var5 = var4 ? this.opposite() : this;
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

      public Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }

      // $FF: synthetic method
      private static Position[] $values() {
         return new Position[]{TOP, BOTTOM};
      }
   }
}
