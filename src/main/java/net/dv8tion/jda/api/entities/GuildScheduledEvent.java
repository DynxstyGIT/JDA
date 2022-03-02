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
package net.dv8tion.jda.api.entities;

import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.GuildScheduledEventManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.GuildScheduledEventMembersPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.GuildScheduledEventUsersPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * A class representing a guild scheduled event (The events that show up under the events tab in the Official Discord Client).
 * These events should not be confused with {@link net.dv8tion.jda.api.events Gateway Events},
 * which are fired by Discord whenever something interesting happens
 * (ie., a {@link net.dv8tion.jda.api.events.message.MessageDeleteEvent MessageDeleteEvent} gets fired whenever a message gets deleted).
 */
public interface GuildScheduledEvent extends ISnowflake, Comparable<GuildScheduledEvent>
{
    /**
     * The maximum allowed length for an event's name.
     */
    int MAX_NAME_LENGTH = 100;

    /**
     * The maximum allowed length for an event's description.
     */
    int MAX_DESCRIPTION_LENGTH = 1000;

    /** Template for {@link #getImageUrl()} */
    String IMAGE_URL = "https://cdn.discordapp.com/guild-events/%s/%s.%s";

    /**
     * The name of the event.
     *
     * @return The name
     */
    @Nonnull
    String getName();

    /**
     * The description of the event.
     *
     * @return The description, or {@code null} if none is specified
     */
    @Nullable
    String getDescription();

    /**
     * The cover image url of the event.
     * <p>Links to a potentially heavily compressed image. You can append a size parameter to the URL if needed. Example: ?size=4096
     * @return The image url, or {@code null} if none is specified
     */
    @Nullable
    String getImageUrl();

    /**
     * The user who originally created the event.
     * <p> May return {@code null} if user has deleted their account, the {@link User} object is not cached
     * or the event was created before Discord started keeping track of event creators on October 21st, 2021.
     *
     * @return {@link User} object representing the event's creator or {@code null}.
     *
     * @see    #getCreatorId()
     * @see    #getCreatorIdLong()
     */
    @Nullable
    User getCreator();

    /**
     * The ID of the user who originally created this event.
     * <p> This method may return 0 if the event was created before Discord started keeping track of event creators on
     * October 21st, 2021.
     *
     * @return The ID of the user who created this event, or 0 if no user is associated with creating this event.
     *
     * @see    #getCreatorId()
     * @see    #getCreator()
     */
    long getCreatorIdLong();

    /**
     * The ID of the user who originally created this event.
     * <br> This method may return {@code null} if the event was created before Discord started keeping track of event
     * creators on October 21st, 2021.
     *
     * @return The Id of the user who created this event, or {@code null} if no user is associated with creating this event.
     *
     * @see    #getCreatorIdLong()
     * @see    #getCreator()
     */
    @Nullable
    default String getCreatorId()
    {
        return getCreatorIdLong() == 0 ? null : String.valueOf(getCreatorIdLong());
    }

    /**
     * Determines if this event has a creator associated with it.
     * <br> This will return {@code false} for events created prior to October 21st, 2021
     * when Discord first started keep track of who created an event.
     *
     * @return {@code true} if this event has a creator associated with it, {@code false} if not
     *
     * @see    #getCreatorIdLong()
     * @see    #getCreatorId()
     * @see    #getCreator()
     */
    @Nonnull
    Status getStatus();

    /**
     * Returns the type of the event. Possible types include
     * {@link Type#STAGE_INSTANCE Type.STAGE_INSTANCE}, {@link Type#VOICE Type.VOICE} and {@link Type#EXTERNAL Type.EXTERNAL}
     * (the latter indicates that the events location is manually set to a custom location).
     *
     * @return The type, or {@link Type#UNKNOWN Type.UNKOWN} if the event type is unknown to JDA.
     */
    @Nonnull
    Type getType();

    /**
     * The time the event is set to start at.
     *
     * @return The time the event is set to start at
     *
     * @see    #getEndTime()
     */
    @Nonnull
    OffsetDateTime getStartTime();

    /**
     * The time the event is set to end at.
     * <br>The end time is only required for external events,
     * which are events that are not associated with a stage or voice channel.
     *
     * @return The time the event is set to end at. This can't be {@code null} for events of
     *         {@link Type#EXTERNAL Type.EXTERNAL}, but can be null for other types.
     *
     * @see    #getType()
     * @see    #getStartTime()
     */
    @Nullable
    OffsetDateTime getEndTime();

    /**
     * The stage channel the event is set to take place at.
     * <br>Note that this method is only applicable to events which are of {@link Type#STAGE_INSTANCE Type.STAGE_INSTANCE}.
     *
     * @return The stage channel, or {@code null} if the stage channel was deleted
     *         or if the event is not of {@link Type#STAGE_INSTANCE Type.STAGE_INSTANCE}
     *
     * @see    #getType()
     * @see    #getVoiceChannel()
     * @see    #getExternalLocation()
     */
    @Nullable
    StageChannel getStageChannel();

    /**
     * The voice channel the event is set to take place at.
     * <br>Note that this method is only applicable to events which are of {@link Type#VOICE Type.VOICE}.
     *
     * @return The voice channel, or {@code null} if the voice channel was deleted
     *         or if the event is not of {@link Type#STAGE_INSTANCE Type.VOICE}
     *
     * @see    #getType()
     * @see    #getStageChannel()
     * @see    #getExternalLocation()
     */
    @Nullable
    VoiceChannel getVoiceChannel();

    /**
     * The external location the event is set to take place at.
     * <br>Note that this method is only applicable to events which are of {@link Type#EXTERNAL Type.EXTERNAL}.
     *
     * @return The location, or {@code null} if the event is not of {@link Type#EXTERNAL Type.EXTERNAL}
     *
     * @see    #getType()
     * @see    #getStageChannel()
     * @see    #getVoiceChannel()
     */
    @Nullable
    String getExternalLocation();

    /**
     * Deletes this Guild Scheduled Event.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_GUILD_SCHEDULED_EVENT}
     *     <br>If the the event was already deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>The send request was attempted after the account lost
     *         {@link net.dv8tion.jda.api.Permission#MANAGE_EVENTS Permission.MANAGE_EVENTS} in the guild.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild</li>
     * </ul>
     *
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException
     *         If we don't have the permission to {@link net.dv8tion.jda.api.Permission#MANAGE_EVENTS MANAGE_EVENTS}
     *
     * @return {@link net.dv8tion.jda.api.requests.RestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();

    /**
     * A {@link PaginationAction PaginationAction} implementation
     * that allows to {@link Iterable iterate} over all {@link net.dv8tion.jda.api.entities.User Users} interested in this Event.
     *
     * <br>This iterates in ascending order by user id.
     *
     * <h4>Examples</h4>
     * <pre>{@code
     *     GuildScheduledEvent event = guild.getScheduledEventById(id);
     *     ArrayList<User> users = new ArrayList<>();
     *     event.retrieveGuildScheduledEventUsers().queue(users::addAll);
     * }</pre>
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_GUILD_SCHEDULED_EVENT}
     *     <br>If the the event was already deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild or can't view the events channel (Location)</li>
     * </ul>
     *
     * @return {@link GuildScheduledEventUsersPaginationAction GuildScheduledEventUsersPaginationAction}
     */
    @Nonnull
    @CheckReturnValue
    GuildScheduledEventUsersPaginationAction retrieveInterestedUsers();

    /**
     * A {@link PaginationAction PaginationAction} implementation
     * that allows to {@link Iterable iterate} over all {@link net.dv8tion.jda.api.entities.Member Members} interested in this Event.
     *
     * <br>This iterates in ascending order by member id.
     *
     * <h4>Examples</h4>
     * <pre>{@code
     *     GuildScheduledEvent event = guild.getScheduledEventById(id);
     *     ArrayList<Member> members = new ArrayList<>();
     *     event.retrieveGuildScheduledEventMembers().queue(members::addAll);
     * }</pre>
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_GUILD_SCHEDULED_EVENT}
     *     <br>If the the event was already deleted.</li>
     *
     *     <li>{@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild or can't view the events channel (Location)</li>
     * </ul>
     *
     * @return {@link GuildScheduledEventUsersPaginationAction GuildScheduledEventUsersPaginationAction}
     */
    @Nonnull
    @CheckReturnValue
    GuildScheduledEventMembersPaginationAction retrieveInterestedMembers();

    /**
     * The amount of users who are interested in attending the event.
     * <p>This method only returns the cached count, and may not be consistent with the live count. Discord may additionally not
     * provide an interested user count for some {@link GuildScheduledEvent} objects returned from the Guild's or JDA's
     * cache, and this method may return -1 as a result. However, event's retrieved using {@link Guild#retrieveScheduledEventById(long)}
     * will always contain an interested user count.
     *
     * @return The amount of users who are interested in attending the event
     *
     * @see Guild#retrieveScheduledEventById(long)
     * @see Guild#retrieveScheduledEventById(String)
     */
    int getInterestedUserCount();

    /**
     * The guild that this event was created in
     *
     * @return The guild
     */
    @Nonnull
    Guild getGuild();

    /**
     * The JDA instance associated with this event object
     *
     * @return The JDA instance
     */
    @Nonnull
    default JDA getJDA()
    {
        return getGuild().getJDA();
    }

    /**
     * The {@link GuildScheduledEventManager} for this event.
     * <br>In the GuildScheduledEventManager, you can modify all its values, and can also start, end, or cancel events.
     * You modify multiple fields in one request by chaining setters before calling {@link net.dv8tion.jda.api.requests.RestAction#queue() RestAction.queue()}.
     *
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException
     *         If the currently logged in account does not have {@link net.dv8tion.jda.api.Permission#MANAGE_EVENTS Permission.MANAGE_EVENTS}
     *
     * @return The GuildScheduledEventManager of this event
     */
    @Nonnull
    GuildScheduledEventManager getManager();

    /**
     * Compares two {@link GuildScheduledEvent} objects based on their scheduled start times.
     * <br>If two events are set to start at the same time, the comparison will be made based on their snowflake ID.
     *
     * @param  guildScheduledEvent
     *         The provided scheduled event
     *
     * @throws IllegalArgumentException
     *         If the provided scheduled event is {@code null}, from a different {@link Guild}, or is not a valid
     *         scheduled event provided by JDA.
     *
     * @return A negative number if the original event (which is the event that the {@link #compareTo(GuildScheduledEvent) compareTo}
     *         method is called upon) starts sooner than the provided event, or positive if it will start later than
     *         the provided event. If both events are set to start at the same time, then the result will be negative if the original
     *         event's snowflake ID is less than the provided event's ID, positive if it is greater than, or 0 if they
     *         are the same.
     *
     * @see Comparable#compareTo(Object)
     * @see #getStartTime()
     * @see #getIdLong()
     */
    @Override
    int compareTo(@Nonnull GuildScheduledEvent guildScheduledEvent);

    /**
     * Represents the status of a scheduled guild event.
     *
     * @see GuildScheduledEvent#getStatus
     */
    enum Status
    {
        UNKNOWN(-1),
        SCHEDULED(1),
        ACTIVE(2),
        COMPLETED(3),
        CANCELED(4);

        private final int key;

        Status(int key)
        {
            this.key = key;
        }

        /**
         * The Discord id key for this Status.
         *
         * @return The id key for this Status
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Used to retrieve a Status based on a Discord id key.
         *
         * @param  key
         *         The Discord id key representing the requested Status.
         *
         * @return The Status related to the provided key, or {@link #UNKNOWN Status.UNKNOWN} if the key is not recognized.
         */
        @Nonnull
        public static Status fromKey(int key)
        {
            for (Status status : Status.values())
            {
                if (status.getKey() == key)
                    return status;
            }

            return UNKNOWN;
        }
    }

    /**
     * Represents what type of event an event is, or where the event will be taking place at.
     */
    enum Type
    {
        UNKNOWN(-1),
        STAGE_INSTANCE(1),
        VOICE(2),
        EXTERNAL(3);

        private final int key;

        Type(int key)
        {
            this.key = key;
        }

        /**
         * The Discord id key for this Type.
         *
         * @return The id key for this Type
         */
        public int getKey()
        {
            return key;
        }

        /**
         * Used to retrieve a Type based on a Discord id key.
         *
         * @param  key
         *         The Discord id key representing the requested Type.
         *
         * @return The Type related to the provided key, or {@link #UNKNOWN Type.UNKNOWN} if the key is not recognized.
         */
        @Nonnull
        public static Type fromKey(int key)
        {
            for (Type type : Type.values())
            {
                if (type.getKey() == key)
                    return type;
            }

            return UNKNOWN;
        }
    }
}