package com.railwayteam.railways.registry;

import java.util.function.Supplier;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.distant_signals.SemaphoreDisplayTarget;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CRDisplayTargets {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final RegistryEntry<SemaphoreDisplayTarget> SEMAPHORE = simpleTarget("semaphore", SemaphoreDisplayTarget::new);

    private static <T extends DisplayTarget> RegistryEntry<T> simpleTarget(String name, Supplier<T> supplier) {
        return REGISTRATE.displayTarget(name, supplier).register();
    }
    @SuppressWarnings("EmptyMethod")
	public static void register() {}
}
