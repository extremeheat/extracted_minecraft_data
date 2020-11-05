package net.minecraft.client.gui.components;

import java.util.List;
import java.util.Optional;
import net.minecraft.util.FormattedCharSequence;

public interface TooltipAccessor {
   Optional<List<FormattedCharSequence>> getTooltip();
}
