package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.jspecify.annotations.Nullable;

public record Advancement(Optional<Identifier> parent, Optional<DisplayInfo> display, AdvancementRewards rewards, Map<String, Criterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional<Component> name) {
   private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC;
   public static final Codec<Advancement> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Advancement> STREAM_CODEC;

   public Advancement(final Optional<Identifier> parent, final Optional<DisplayInfo> display, final AdvancementRewards rewards, final Map<String, Criterion<?>> criteria, final AdvancementRequirements requirements, final boolean sendsTelemetryEvent) {
      this(parent, display, rewards, Map.copyOf(criteria), requirements, sendsTelemetryEvent, display.map(Advancement::decorateName));
   }

   public Advancement {
      super();
   }

   private static DataResult<Advancement> validate(final Advancement advancement) {
      return advancement.requirements().validate(advancement.criteria().keySet()).map((r) -> advancement);
   }

   private static Component decorateName(final DisplayInfo display) {
      Component displayTitle = display.getTitle();
      ChatFormatting color = display.getType().getChatColor();
      Component tooltip = ComponentUtils.mergeStyles(displayTitle.copy(), Style.EMPTY.withColor(color)).append("\n").append(display.getDescription());
      Component title = displayTitle.copy().withStyle((UnaryOperator)((s) -> s.withHoverEvent(new HoverEvent.ShowText(tooltip))));
      return ComponentUtils.wrapInSquareBrackets(title).withStyle(color);
   }

   public static Component name(final AdvancementHolder holder) {
      return (Component)holder.value().name().orElseGet(() -> Component.literal(holder.id().toString()));
   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeOptional(this.parent, FriendlyByteBuf::writeIdentifier);
      DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(output, this.display);
      this.requirements.write(output);
      output.writeBoolean(this.sendsTelemetryEvent);
   }

   private static Advancement read(final RegistryFriendlyByteBuf input) {
      return new Advancement(input.readOptional(FriendlyByteBuf::readIdentifier), (Optional)DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(input), AdvancementRewards.EMPTY, Map.of(), new AdvancementRequirements(input), input.readBoolean());
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(final ProblemReporter reporter, final HolderGetter.Provider lootData) {
      this.criteria.forEach((name, criterion) -> {
         ValidationContextSource validator = new ValidationContextSource(reporter.forChild(new ProblemReporter.RootFieldPathElement(name)), lootData);
         criterion.triggerInstance().validate(validator);
      });
   }

   static {
      CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, Criterion.CODEC).validate((criteria) -> criteria.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(criteria));
      CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent), DisplayInfo.CODEC.optionalFieldOf("display").forGetter(Advancement::display), AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards), CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria), AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter((a) -> Optional.of(a.requirements())), Codec.BOOL.optionalFieldOf("sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)).apply(i, (parent, display, rewards, criteria, requirementsOpt, sendsTelemetryEvent) -> {
            AdvancementRequirements requirements = (AdvancementRequirements)requirementsOpt.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
            return new Advancement(parent, display, rewards, criteria, requirements, sendsTelemetryEvent);
         })).validate(Advancement::validate);
      STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Advancement>ofMember(Advancement::write, Advancement::read);
   }

   public static class Builder {
      private Optional<Identifier> parent = Optional.empty();
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

      public Builder parent(final AdvancementHolder parent) {
         this.parent = Optional.of(parent.id());
         return this;
      }

      /** @deprecated */
      @Deprecated(
         forRemoval = true
      )
      public Builder parent(final Identifier parent) {
         this.parent = Optional.of(parent);
         return this;
      }

      public Builder display(final ItemStackTemplate icon, final Component title, final Component description, final @Nullable Identifier background, final AdvancementType frame, final boolean showToast, final boolean announceChat, final boolean hidden) {
         return this.display(new DisplayInfo(icon, title, description, Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new), frame, showToast, announceChat, hidden));
      }

      public Builder display(final ItemLike icon, final Component title, final Component description, final @Nullable Identifier background, final AdvancementType frame, final boolean showToast, final boolean announceChat, final boolean hidden) {
         return this.display(new DisplayInfo(new ItemStackTemplate(icon.asItem()), title, description, Optional.ofNullable(background).map(ClientAsset.ResourceTexture::new), frame, showToast, announceChat, hidden));
      }

      public Builder display(final DisplayInfo display) {
         this.display = Optional.of(display);
         return this;
      }

      public Builder rewards(final AdvancementRewards.Builder rewards) {
         return this.rewards(rewards.build());
      }

      public Builder rewards(final AdvancementRewards rewards) {
         this.rewards = rewards;
         return this;
      }

      public Builder addCriterion(final String name, final Criterion<?> criterion) {
         this.criteria.put(name, criterion);
         return this;
      }

      public Builder requirements(final AdvancementRequirements.Strategy strategy) {
         this.requirementsStrategy = strategy;
         return this;
      }

      public Builder requirements(final AdvancementRequirements requirements) {
         this.requirements = Optional.of(requirements);
         return this;
      }

      public Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(final Identifier id) {
         Map<String, Criterion<?>> criteria = this.criteria.buildOrThrow();
         AdvancementRequirements requirements = (AdvancementRequirements)this.requirements.orElseGet(() -> this.requirementsStrategy.create(criteria.keySet()));
         return new AdvancementHolder(id, new Advancement(this.parent, this.display, this.rewards, criteria, requirements, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(final Consumer<AdvancementHolder> output, final String name) {
         AdvancementHolder advancement = this.build(Identifier.parse(name));
         output.accept(advancement);
         return advancement;
      }
   }
}
