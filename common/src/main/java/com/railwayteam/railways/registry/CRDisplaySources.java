package com.railwayteam.railways.registry;
import java.util.function.Supplier;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.coupling.TrackCouplerDisplaySource;
import com.railwayteam.railways.content.switches.SwitchDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CRDisplaySources {
    private static final CreateRegistrate REGISTRATE = Railways.registrate();

    public static final RegistryEntry<TrackCouplerDisplaySource> TRACK_COUPLER = simpleSource("track_coupler_info", TrackCouplerDisplaySource::new);
    public static final RegistryEntry<SwitchDisplaySource> SWITCH = simpleSource("switch", SwitchDisplaySource::new);

    private static <T extends DisplaySource> RegistryEntry<T> simpleSource(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }

    @SuppressWarnings("EmptyMethod")
    public static void register() {}
}
