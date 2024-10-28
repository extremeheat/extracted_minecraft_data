package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record Advancement(Optional<ResourceLocation> parent, Optional<DisplayInfo> display, AdvancementRewards rewards, Map<String, Criterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional<Component> name) {
   private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC;
   public static final Codec<Advancement> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Advancement> STREAM_CODEC;

   public Advancement(Optional<ResourceLocation> var1, Optional<DisplayInfo> var2, AdvancementRewards var3, Map<String, Criterion<?>> var4, AdvancementRequirements var5, boolean var6) {
      this(var1, var2, var3, Map.copyOf(var4), var5, var6, var2.map(Advancement::decorateName));
   }

   public Advancement(Optional<ResourceLocation> parent, Optional<DisplayInfo> display, AdvancementRewards rewards, Map<String, Criterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional<Component> name) {
      super();
      this.parent = parent;
      this.display = display;
      this.rewards = rewards;
      this.criteria = criteria;
      this.requirements = requirements;
      this.sendsTelemetryEvent = sendsTelemetryEvent;
      this.name = name;
   }

   private static DataResult<Advancement> validate(Advancement var0) {
      return var0.requirements().validate(var0.criteria().keySet()).map((var1) -> {
         return var0;
      });
   }

   private static Component decorateName(DisplayInfo var0) {
      Component var1 = var0.getTitle();
      ChatFormatting var2 = var0.getType().getChatColor();
      MutableComponent var3 = ComponentUtils.mergeStyles(var1.copy(), Style.EMPTY.withColor(var2)).append("\n").append(var0.getDescription());
      MutableComponent var4 = var1.copy().withStyle((var1x) -> {
         return var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var3));
      });
      return ComponentUtils.wrapInSquareBrackets(var4).withStyle(var2);
   }

   public static Component name(AdvancementHolder var0) {
      return (Component)var0.value().name().orElseGet(() -> {
         return Component.literal(var0.id().toString());
      });
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeOptional(this.parent, FriendlyByteBuf::writeResourceLocation);
      DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(var1, this.display);
      this.requirements.write(var1);
      var1.writeBoolean(this.sendsTelemetryEvent);
   }

   private static Advancement read(RegistryFriendlyByteBuf var0) {
      return new Advancement(var0.readOptional(FriendlyByteBuf::readResourceLocation), (Optional)DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(var0), AdvancementRewards.EMPTY, Map.of(), new AdvancementRequirements(var0), var0.readBoolean());
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(ProblemReporter var1, HolderGetter.Provider var2) {
      this.criteria.forEach((var2x, var3) -> {
         CriterionValidator var4 = new CriterionValidator(var1.forChild(var2x), var2);
         var3.triggerInstance().validate(var4);
      });
   }

   public Optional<ResourceLocation> parent() {
      return this.parent;
   }

   public Optional<DisplayInfo> display() {
      return this.display;
   }

   public AdvancementRewards rewards() {
      return this.rewards;
   }

   public Map<String, Criterion<?>> criteria() {
      return this.criteria;
   }

   public AdvancementRequirements requirements() {
      return this.requirements;
   }

   public boolean sendsTelemetryEvent() {
      return this.sendsTelemetryEvent;
   }

   public Optional<Component> name() {
      return this.name;
   }

   static {
      CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC).validate((var0) -> {
         return var0.isEmpty() ? DataResult.error(() -> {
            return "Advancement criteria cannot be empty";
         }) : DataResult.success(var0);
      });
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent), DisplayInfo.CODEC.optionalFieldOf("display").forGetter(Advancement::display), AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards), CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria), AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter((var0x) -> {
            return Optional.of(var0x.requirements());
         }), Codec.BOOL.optionalFieldOf("sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)).apply(var0, (var0x, var1, var2, var3, var4, var5) -> {
            AdvancementRequirements var6 = (AdvancementRequirements)var4.orElseGet(() -> {
               return AdvancementRequirements.allOf(var3.keySet());
            });
            return new Advancement(var0x, var1, var2, var3, var6, var5);
         });
      }).validate(Advancement::validate);
      STREAM_CODEC = StreamCodec.ofMember(Advancement::write, Advancement::read);
   }

   public static class Builder {
      private Optional<ResourceLocation> parent = Optional.empty();
      private Optional<DisplayInfo> display = Optional.empty();
      private AdvancementRewards rewards;
      private final ImmutableMap.Builder<String, Criterion<?>> criteria;
      private Optional<AdvancementRequirements> requirements;
      private AdvancementRequirements.Strategy requirementsStrategy;
      private boolean sendsTelemetryEvent;

      public Builder() {
         super();
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = ImmutableMap.builder();
         this.requirements = Optional.empty();
         this.requirementsStrategy = AdvancementRequirements.Strategy.AND;
      }

      public static Builder advancement() {
         return (new Builder()).sendsTelemetryEvent();
      }

      public static Builder recipeAdvancement() {
         return new Builder();
      }

      public Builder parent(AdvancementHolder var1) {
         this.parent = Optional.of(var1.id());
         return this;
      }

      /** @deprecated */
      @Deprecated(
         forRemoval = true
      )
      public Builder parent(ResourceLocation var1) {
         this.parent = Optional.of(var1);
         return this;
      }

      public Builder display(ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, AdvancementType var5, boolean var6, boolean var7, boolean var8) {
         return this.display(new DisplayInfo(var1, var2, var3, Optional.ofNullable(var4), var5, var6, var7, var8));
      }

      public Builder display(ItemLike var1, Component var2, Component var3, @Nullable ResourceLocation var4, AdvancementType var5, boolean var6, boolean var7, boolean var8) {
         return this.display(new DisplayInfo(new ItemStack(var1.asItem()), var2, var3, Optional.ofNullable(var4), var5, var6, var7, var8));
      }

      public Builder display(DisplayInfo var1) {
         this.display = Optional.of(var1);
         return this;
      }

      public Builder rewards(AdvancementRewards.Builder var1) {
         return this.rewards(var1.build());
      }

      public Builder rewards(AdvancementRewards var1) {
         this.rewards = var1;
         return this;
      }

      public Builder addCriterion(String var1, Criterion<?> var2) {
         this.criteria.put(var1, var2);
         return this;
      }

      public Builder requirements(AdvancementRequirements.Strategy var1) {
         this.requirementsStrategy = var1;
         return this;
      }

      public Builder requirements(AdvancementRequirements var1) {
         this.requirements = Optional.of(var1);
         return this;
      }

      public Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(ResourceLocation var1) {
         ImmutableMap var2 = this.criteria.buildOrThrow();
         AdvancementRequirements var3 = (AdvancementRequirements)this.requirements.orElseGet(() -> {
            return this.requirementsStrategy.create(var2.keySet());
         });
         return new AdvancementHolder(var1, new Advancement(this.parent, this.display, this.rewards, var2, var3, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(Consumer<AdvancementHolder> var1, String var2) {
         AdvancementHolder var3 = this.build(new ResourceLocation(var2));
         var1.accept(var3);
         return var3;
      }
   }
}
