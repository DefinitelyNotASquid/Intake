/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.intake.completion;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * A registry of command completer instances.
 */
@Beta
public class CompleterRegistry {

    /** A map of completer classes to completer instances. */
    // We can't use a Table here because null is not permitted.
    protected final Map<Class<? extends CommandCompleter>, Map<String, CommandCompleter>> sharedInstances = Maps.newHashMapWithExpectedSize(4);

    /**
     * Clear the completer class registry.
     */
    public void clear() {
        this.sharedInstances.clear();
    }

    /**
     * Registers a shared completer.
     *
     * <p>A shared completer will be used instead of creating a new instance
     * for each command definition with the specified class.</p>
     *
     * @param completer the shared completer
     */
    public void register(CommandCompleter completer) {
        this.register(completer, null);
    }

    /**
     * Registers a shared completer.
     *
     * <p>A shared completer will be used instead of creating a new instance
     * for each command definition with the specified class.</p>
     *
     * <p>A {@code null} id is represented as "default".</p>
     *
     * @param completer the shared completer
     * @param id the identifier for the completer
     */
    public void register(CommandCompleter completer, @Nullable String id) {
        this.register(completer.getClass(), completer, id);
    }

    /**
     * Registers a shared completer.
     *
     * <p>A shared completer will be used instead of creating a new instance
     * for each command definition with the specified class.</p>
     *
     * <p>A {@code null} id is represented as "default".</p>
     *
     * @param clazz the completer class
     * @param completer the shared completer
     * @param id the identifier for the completer
     */
    public void register(Class<? extends CommandCompleter> clazz, CommandCompleter completer, @Nullable String id) {
        this.getSharedInstanceMap(clazz).put(fixId(id), completer);
    }

    /**
     * Unregisters a shared completer.
     *
     * <p>A {@code null} id is represented as "default".</p>
     *
     * @param clazz the completer class
     * @param id the identifier for the completer
     */
    public void unregister(Class<? extends CommandCompleter> clazz, @Nullable String id) {
        this.getSharedInstanceMap(clazz).remove(fixId(id));
    }

    /**
     * Get an instance of a {@link CommandCompleter}, or create one if one is not already
     * available to us.
     *
     * <p>Depending on the value of <code>useSharedInstance</code>, the returned completer
     * can either be a new instance, or an existing cached instance.</p>
     *
     * @param completion the completion definition
     * @return the completer instance
     */
    public CommandCompleter getOrCreate(Completion completion) {
        Class<? extends CommandCompleter> clazz = completion.value();

        if (!completion.useSharedInstance()) {
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return null;
        }

        String id = fixId(completion.id());

        if (this.sharedInstances.containsKey(clazz) && this.sharedInstances.get(clazz).containsKey(id)) {
            return this.sharedInstances.get(clazz).get(id);
        } else {
            try {
                CommandCompleter completer = clazz.newInstance();
                this.getSharedInstanceMap(clazz).put(id, completer);

                return completer;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Get the id to shared completer instance map for the specified completer class.
     *
     * @param clazz the completer class
     * @return the id to shared completer instance map
     */
    protected final Map<String, CommandCompleter> getSharedInstanceMap(Class<? extends CommandCompleter> clazz) {
        @Nullable Map<String, CommandCompleter> map = this.sharedInstances.get(clazz);
        if (map == null) {
            map = Maps.newHashMapWithExpectedSize(4);
            this.sharedInstances.put(clazz, map);
        }

        return map;
    }

    /**
     * "Fix" the passed id
     *
     * @param id the id
     * @return the fixed id
     */
    protected static String fixId(String id) {
        // We don't want to allow null or empty ids
        if (id == null || id.trim().isEmpty()) {
            id = Completion.DEFAULT_ID;
        }

        return id.toLowerCase();
    }
}
