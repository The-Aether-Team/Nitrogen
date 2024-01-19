package com.aetherteam.nitrogen.integration.rei;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public class REIClientUtils {
    public static <T extends Display> Collection<EntryStack<?>> setupRendering(Collection<EntryStack<?>> entryStacks, BlockPropertyPair pair, @Nullable Consumer<Tooltip> processor){
        return setupRendering(entryStacks, new BlockPropertyPair[]{pair}, processor);
    }

    public static <T extends Display> Collection<EntryStack<?>> setupRendering(Collection<EntryStack<?>> entryStacks, BlockPropertyPair[] pairs, @Nullable Consumer<Tooltip> processor){
        for (EntryStack<?> entry : entryStacks) {
            if (processor != null) {
                ClientEntryStacks.setTooltipProcessor(entry, (entryStack, tooltip) -> {
                    processor.accept(tooltip);
                    return tooltip;
                });
            }
            if (entry.getType() == VanillaEntryTypes.FLUID) {
                ClientEntryStacks.setRenderer(entry, new FluidStateRenderer());
            } else if (entry.getType() == VanillaEntryTypes.ITEM) {
                ClientEntryStacks.setRenderer(entry, new BlockStateRenderer(pairs));
            }
        }
        return entryStacks;
    }

    public static <T extends Display> Collection<EntryStack<?>> setupRendering(Collection<EntryStack<?>> entryStacks, Consumer<Tooltip> processor){
        for (EntryStack<?> entry : entryStacks) {
            ClientEntryStacks.setTooltipProcessor(entry, (entryStack, tooltip) -> {
                processor.accept(tooltip);
                return tooltip;
            });
        }
        return entryStacks;
    }
}
