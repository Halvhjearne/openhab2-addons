/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.spotify.internal.handler;

import static org.openhab.binding.spotify.internal.SpotifyBindingConstants.*;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.type.DynamicStateDescriptionProvider;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.core.types.StateOption;
import org.openhab.binding.spotify.internal.api.model.Device;
import org.openhab.binding.spotify.internal.api.model.Playlist;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@Component(service = { DynamicStateDescriptionProvider.class, SpotifyDynamicStateDescriptionProvider.class })
@NonNullByDefault
public class SpotifyDynamicStateDescriptionProvider implements DynamicStateDescriptionProvider {

    private List<Device> devices = Collections.emptyList();
    private List<Playlist> playlists = Collections.emptyList();
    private @Nullable StateDescription devicesStateDescription;
    private @Nullable StateDescription playlistStateDescription;

    public void setDevices(List<Device> spotifyDevices) {
        if (spotifyDevices.size() != devices.size() || !spotifyDevices.stream().allMatch(sd -> devices.stream()
                .anyMatch(d -> sd.getId() == d.getId() && d.getName() != null && d.getName().equals(sd.getName())))) {
            List<StateOption> devicesStateOptions = spotifyDevices.stream()
                    .map(device -> new StateOption(device.getId(), device.getName())).collect(Collectors.toList());
            devices = spotifyDevices;
            devicesStateDescription = new StateDescription(null, null, null, null, devicesStateOptions.isEmpty(),
                    devicesStateOptions);
        }
    }

    public void setPlayList(List<Playlist> spotifyPlaylists) {
        if (spotifyPlaylists.size() != playlists.size() || !spotifyPlaylists.stream()
                .allMatch(sp -> playlists.stream().anyMatch(p -> p.getUri() != null && sp.getUri().equals(p.getUri())
                        && p.getName() != null && p.getName().equals(sp.getName())))) {
            List<StateOption> playlistStateOptions = spotifyPlaylists.stream()
                    .map(device -> new StateOption(device.getUri(), device.getName())).collect(Collectors.toList());

            playlists = spotifyPlaylists;
            playlistStateDescription = new StateDescription(null, null, null, null, playlistStateOptions.isEmpty(),
                    playlistStateOptions);
        }
    }

    @Override
    public @Nullable StateDescription getStateDescription(@NonNull Channel channel,
            @Nullable StateDescription originalStateDescription, @Nullable Locale locale) {
        if (channel.getUID().getId().equals(CHANNEL_DEVICENAME)) {
            return devicesStateDescription;
        } else if (channel.getUID().getId().equals(CHANNEL_PLAYLIST)) {
            return playlistStateDescription;
        } else {
            return originalStateDescription;
        }
    }
}
