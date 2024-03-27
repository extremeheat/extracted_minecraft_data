package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

public record WrittenBookContent(Filterable<String> l, String m, int n, List<Filterable<Component>> o, boolean p)
   implements BookContent<Component, WrittenBookContent> {
   private final Filterable<String> title;
   private final String author;
   private final int generation;
   private final List<Filterable<Component>> pages;
   private final boolean resolved;
   public static final WrittenBookContent EMPTY = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
   public static final int PAGE_LENGTH = 32767;
   public static final int MAX_PAGES = 100;
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int MAX_GENERATION = 3;
   public static final int MAX_CRAFTABLE_GENERATION = 2;
   public static final Codec<Component> CONTENT_CODEC = ComponentSerialization.flatCodec(32767);
   public static final Codec<List<Filterable<Component>>> PAGES_CODEC = pagesCodec(CONTENT_CODEC);
   public static final Codec<WrittenBookContent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Filterable.codec(Codec.string(0, 32)).fieldOf("title").forGetter(WrittenBookContent::title),
               Codec.STRING.fieldOf("author").forGetter(WrittenBookContent::author),
               ExtraCodecs.intRange(0, 3).optionalFieldOf("generation", 0).forGetter(WrittenBookContent::generation),
               PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WrittenBookContent::pages),
               Codec.BOOL.optionalFieldOf("resolved", false).forGetter(WrittenBookContent::resolved)
            )
            .apply(var0, WrittenBookContent::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, WrittenBookContent> STREAM_CODEC = StreamCodec.composite(
      Filterable.streamCodec(ByteBufCodecs.stringUtf8(32)),
      WrittenBookContent::title,
      ByteBufCodecs.STRING_UTF8,
      WrittenBookContent::author,
      ByteBufCodecs.VAR_INT,
      WrittenBookContent::generation,
      Filterable.streamCodec(ComponentSerialization.STREAM_CODEC).apply(ByteBufCodecs.list(100)),
      WrittenBookContent::pages,
      ByteBufCodecs.BOOL,
      WrittenBookContent::resolved,
      WrittenBookContent::new
   );

   public WrittenBookContent(Filterable<String> var1, String var2, int var3, List<Filterable<Component>> var4, boolean var5) {
      super();
      this.title = var1;
      this.author = var2;
      this.generation = var3;
      this.pages = var4;
      this.resolved = var5;
   }

   private static Codec<Filterable<Component>> pageCodec(Codec<Component> var0) {
      return Filterable.codec(var0);
   }

   public static Codec<List<Filterable<Component>>> pagesCodec(Codec<Component> var0) {
      return pageCodec(var0).sizeLimitedListOf(100);
   }

   @Nullable
   public WrittenBookContent tryCraftCopy() {
      return this.generation >= 2 ? null : new WrittenBookContent(this.title, this.author, this.generation + 1, this.pages, this.resolved);
   }

   @Nullable
   public WrittenBookContent resolve(CommandSourceStack var1, @Nullable Player var2) {
      if (this.resolved) {
         return null;
      } else {
         Builder var3 = ImmutableList.builderWithExpectedSize(this.pages.size());

         for(Filterable var5 : this.pages) {
            Optional var6 = resolvePage(var1, var2, var5);
            if (var6.isEmpty()) {
               return null;
            }

            var3.add((Filterable)var6.get());
         }

         return new WrittenBookContent(this.title, this.author, this.generation, var3.build(), true);
      }
   }

   public WrittenBookContent markResolved() {
      return new WrittenBookContent(this.title, this.author, this.generation, this.pages, true);
   }

   private static Optional<Filterable<Component>> resolvePage(CommandSourceStack var0, @Nullable Player var1, Filterable<Component> var2) {
      return var2.resolve(var2x -> {
         try {
            MutableComponent var3 = ComponentUtils.updateForEntity(var0, var2x, var1, 0);
            return isPageTooLarge(var3, var0.registryAccess()) ? Optional.empty() : Optional.of(var3);
         } catch (Exception var4) {
            return Optional.of(var2x);
         }
      });
   }

   private static boolean isPageTooLarge(Component var0, HolderLookup.Provider var1) {
      return Component.Serializer.toJson(var0, var1).length() > 32767;
   }

   public List<Component> getPages(boolean var1) {
      return Lists.transform(this.pages, var1x -> (Component)var1x.get(var1));
   }

   public WrittenBookContent withReplacedPages(List<Filterable<Component>> var1) {
      return new WrittenBookContent(this.title, this.author, this.generation, var1, false);
   }
}
