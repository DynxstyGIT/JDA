/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.api.events.guild.scheduledevent.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildScheduledEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * Indicates the {@link GuildScheduledEvent#getEndTime() end time} of a {@link GuildScheduledEvent} has changed.
 *
 * <p>Can be used to detect when the {@link GuildScheduledEvent} end time has changed.
 *
 * <p>Identifier: {@code guild_scheduled_event_end_time}
 */
public class GuildScheduledEventUpdateEndTimeEvent extends GenericGuildScheduledEventUpdateEvent<OffsetDateTime>
{
    public static final String IDENTIFIER = "guild_scheduled_event_end_time";

    public GuildScheduledEventUpdateEndTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull GuildScheduledEvent guildScheduledEvent, @Nullable OffsetDateTime previous)
    {
        super(api, responseNumber, guildScheduledEvent, previous, guildScheduledEvent.getEndTime(), IDENTIFIER);
    }

    /**
     * The old {@link GuildScheduledEvent#getEndTime() end time}.
     *
     * @return The old end time, or {@code null} if no end time was previously set.
     */
    @Nullable
    public OffsetDateTime getOldEndTime()
    {
        return getOldValue();
    }

    /**
     * The new {@link GuildScheduledEvent#getEndTime() end time}.
     *
     * @return The new start time, or {@code null} if the end time has been removed.
     */
    @Nullable
    public OffsetDateTime getNewEndTime()
    {
        return getNewValue();
    }
}
